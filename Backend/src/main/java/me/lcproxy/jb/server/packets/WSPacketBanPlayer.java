package me.lcproxy.jb.server.packets;

import lombok.AllArgsConstructor;
import me.lcproxy.jb.server.ByteBufWrapper;
import me.lcproxy.jb.server.ServerHandler;
import me.lcproxy.jb.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;

@AllArgsConstructor
public class WSPacketBanPlayer extends WSPacket {
    private String user;
    private String reason;

    public void WSPacketBanPlayer(String user, String reason) {
        this.user = user;
        this.reason = reason;
    }

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeInt(2);
        out.writeString(user);
        out.writeInt(3);
        out.writeString(reason);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {

    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {

    }
}
