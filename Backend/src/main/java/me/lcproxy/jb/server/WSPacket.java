package me.lcproxy.jb.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.buffer.ByteBuf;
import me.lcproxy.jb.server.packets.*;
import org.java_websocket.WebSocket;

import java.io.IOException;

public abstract class WSPacket {
    public static BiMap<Class<? extends WSPacket>, Integer> REGISTRY;

    public abstract void write(WebSocket conn, ByteBufWrapper out) throws IOException;

    public abstract void read(WebSocket conn, ByteBufWrapper in) throws IOException;

    public abstract void process(WebSocket conn, ServerHandler handler) throws IOException;

    protected void writeBlob(ByteBuf buf, byte[] bytes) {
        buf.writeShort(bytes.length);
        buf.writeBytes(bytes);
    }

    protected byte[] readBlob(ByteBuf buf) {
        short key = buf.readShort();
        if (key < 0) {
            System.out.println("Key was smaller than 0?");
            return new byte[0];
        }
        byte[] blob = new byte[key];
        buf.readBytes(blob);
        return blob;
    }

    static {
        REGISTRY = HashBiMap.create();

        REGISTRY.put(WSPacketServerUpdate.class, 6);
        REGISTRY.put(WSSendChatMessage.class, 65);
        REGISTRY.put(WSPacketCosmeticGive.class, 8);
        REGISTRY.put(WSPacketCosmeticSet.class, 20);
        REGISTRY.put(WSPacketBanPlayer.class, 1056);
        REGISTRY.put(WSPacketForceCrash.class, 33);
        REGISTRY.put(WSPacketEmoteGive.class, 57);
        REGISTRY.put(WSPacketPlayEmote.class, 39);
        REGISTRY.put(WSPacketSendEmote.class, 51);
    }
}
