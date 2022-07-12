// create a https and websocket server


package main

import (
	"log"
	"net/http"
	"github.com/gorilla/websocket"
	"encoding/json"
	"io"
	"io/ioutil"
	"context"
	"sync"
)

var upgrader = websocket.Upgrader{}

type Connection struct {
  Socket *websocket.Conn
  mu     sync.Mutex
}

func (c *Connection) Send(v interface{}) error {
  c.mu.Lock()
  defer c.mu.Unlock()
	
	// send the messsage as binary
	return c.Socket.WriteMessage(websocket.BinaryMessage, v.([]byte))
}

func main() {
	// listen for upgrades on "/connect", then forward the request to the websocket server
	http.HandleFunc("/connect", func(w http.ResponseWriter, r *http.Request) {
		upgrader.CheckOrigin = func(r *http.Request) bool { return true }

		conn, err := upgrader.Upgrade(w, r, nil)
		if err != nil {
			log.Println(err)
			return
		}

		connection := new(Connection)
    connection.Socket = conn

		// get the headers from the request
		headers := r.Header

		d := websocket.DefaultDialer
		header := http.Header{}

		header.Set("accounttype", headers.Get("accounttype"))
		header.Set("arch", headers.Get("arch"))
		header.Set("authorization", headers.Get("authorization"))
		header.Set("branch", headers.Get("branch"))
		header.Set("gitcommit", headers.Get("gitcommit"))
		header.Set("hwid", headers.Get("hwid"))
		header.Set("launcherversion", headers.Get("launcherversion"))
		header.Set("os", headers.Get("os"))
		header.Set("playerid", headers.Get("playerid"))
		header.Set("username", headers.Get("username"))
		header.Set("version", headers.Get("version"))

		/*if headers.Get("username") != "Amplitudes" {
			// close the connection
			conn.Close()
			return
		} else {
			log.Println("Websocket connection established by", headers.Get("username"))
		}*/

		// connect to wss://assetserver.lunarclientprod.com/connect and every time a message is sent, send it to the client
		ws, _, err := d.Dial("wss://assetserver.lunarclientprod.com/connect", header)
		if err != nil {
			log.Println(err)
			return
		}

		sc := new(Connection)
    sc.Socket = ws

		ws2, _, err := d.Dial("ws://127.0.0.1:19486", header)
		if err != nil {
			log.Println(err)
			return
		}

		sc2 := new(Connection)
    sc2.Socket = ws2

		go func() {
			// connect to the websocket server and forward the messages from the client to the websocket server and vice versa
			for {
				_, message, err := ws.ReadMessage()
				if err != nil {
					log.Println("websocket err (lunar):", err)
					break
				}
				// write the message to conn as a binary message
				err = connection.Send(message)
				if err != nil {
					log.Println("websocket err (lunar):", err)
					break
				}
			}
		}()

		go func() {
			for {
				_, message, err := ws2.ReadMessage()
				if err != nil {
					log.Println("websocket err (backend):", err)
					break
				}
				// write the message to conn as a binary message
				err = connection.Send(message)
				if err != nil {
					log.Println("websocket err (backend):", err)
					break
				}
			}
		}()

		for {
			// read from the client
			_, message, err := conn.ReadMessage()
			if err != nil {
				log.Println("websocket err (client):", err)
				break
			}

			// write the message to ws as a binary message
			err = sc.Send(message)
			if err != nil {
				log.Println("websocket err (client):", err)
				break
			}

			err = sc2.Send(message)
			if err != nil {
				log.Println("websocket err (client):", err)
				return
			}
		}
	})

	// listen for all http requests
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		// get the host
		host := r.Host
		// check if the host is api.lunarclientprod.com
		if host == "api.lunarclientprod.com" {
			r.URL.Scheme = "https"
			r.URL.Host = "api.lunarclientprod.com"
			r.RequestURI = ""
			client := &http.Client{}

			resp, err := client.Do(r.WithContext(context.Background()))
			if err != nil {
				log.Println(err)
			  return
			}

			defer resp.Body.Close()

			// check if the url is "/game/metadata"
			if r.URL.Path == "/game/metadata" {
				// parse the body of the request into json
				body, err := ioutil.ReadAll(resp.Body)
				if err != nil {
					log.Println(err)
					return
				}

				// create a new json object from the body
				var data map[string]interface{}
				json.Unmarshal(body, &data)

				// set serverIntegration to []
				data["serverIntegration"] = []interface{}{}
				// set pinnedServers to []
				data["pinnedServers"] = []interface{}{}

				// stringify the json and send it to the client
				jsonString, err := json.Marshal(data)
				if err != nil {
					log.Fatal(err)
				}
				
				io.WriteString(w, string(jsonString))
			} else {
				// send the response to the client
				io.Copy(w, resp.Body)
			}
		}
	})

	// listen on port 443
	http.ListenAndServeTLS(":443", "server.cert", "server.key", nil)
}