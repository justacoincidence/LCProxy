package me.lcproxy.jb.server.packets;

import me.lcproxy.jb.server.ByteBufWrapper;
import me.lcproxy.jb.server.ServerHandler;
import me.lcproxy.jb.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.UUID;

public class WSPacketSendEmote extends WSPacket {
    UUID uuid;
    int id;

    public WSPacketSendEmote() {}

    public WSPacketSendEmote(UUID uuid, int id) {
        this.uuid = uuid;
        this.id = id;
    }
    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
        out.writeInt(id);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {

    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {

    }
}
