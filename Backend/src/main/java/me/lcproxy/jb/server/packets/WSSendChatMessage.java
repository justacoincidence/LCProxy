package me.lcproxy.jb.server.packets;

import me.lcproxy.jb.server.ByteBufWrapper;
import me.lcproxy.jb.server.ServerHandler;
import me.lcproxy.jb.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class WSSendChatMessage extends WSPacket {
    String message;

    public WSSendChatMessage(String message) {
        this.message = message;
    }

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeString(message);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {

    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {

    }
}