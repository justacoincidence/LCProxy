package me.lcproxy.jb.server.packets;

import me.lcproxy.jb.WebServer;
import me.lcproxy.jb.player.Player;
import me.lcproxy.jb.player.PlayerManager;
import me.lcproxy.jb.server.ByteBufWrapper;
import me.lcproxy.jb.server.ServerHandler;
import me.lcproxy.jb.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class WSPacketPlayEmote extends WSPacket {
    int emoteId;
    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {

    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        emoteId = in.readInt();
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
        Player player = WebServer.getInstance().getPlayerManager().getPlayerById(conn.getAttachment());
        for(Player online : PlayerManager.getPlayerMap().values()) {
            if (online != null && online.isOnline() && online.getServer() != null && player.getServer() != null) {
                if (online.getServer().toLowerCase().contains(player.getServer().toLowerCase())) {
                    handler.sendPacket(online.getConn(), new WSPacketSendEmote(player.getPlayerId(), emoteId));
                }
            }
        }
    }
}
