package me.lcproxy.jb.server.packets;

import me.lcproxy.jb.server.ByteBufWrapper;
import me.lcproxy.jb.server.ServerHandler;
import me.lcproxy.jb.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class WSPacketEmoteGive extends WSPacket {
    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeVarInt(87);
        for(int i = 0; i < 86; i++)
            out.writeVarInt(i);

        out.writeVarInt(87);
        for(int i = 0; i < 86; i++)
            out.writeVarInt(i);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) {

    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {

    }
}