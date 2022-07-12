package me.lcproxy.jb.server.packets;

import me.lcproxy.jb.player.Player;
import me.lcproxy.jb.player.PlayerManager;
import me.lcproxy.jb.player.Rank;
import me.lcproxy.jb.server.ByteBufWrapper;
import me.lcproxy.jb.server.ServerHandler;
import me.lcproxy.jb.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class WSPacketServerUpdate extends WSPacket {
    private String playerId;
    private String serverAddress;

    public WSPacketServerUpdate() {
    }

    public WSPacketServerUpdate(String playerId, String serverAddress) {
        this.playerId = playerId;
        this.serverAddress = serverAddress;
    }

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeString(this.playerId);
        out.writeString(this.serverAddress);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        this.playerId = in.readString(52);
        this.serverAddress = in.readString(100);
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
        Player player = PlayerManager.getPlayerMap().get(conn.getAttachment());
        handler.sendPacket(conn, new WSPacketCosmeticGive());
        //handler.sendPacket(conn, new WSPacketEmoteGive());

        if (this.serverAddress.equalsIgnoreCase(player.getServer())) return;

        if (!this.serverAddress.equalsIgnoreCase("")) {
            player.setServer(this.serverAddress);

            for (Player online : PlayerManager.getPlayerMap().values()) {
                if (online != player) {
                    try {
                        if (online != null && online.isOnline() && online.getServer() != null && player.getServer() != null) {
                            if (online.getServer().toLowerCase().contains(player.getServer().toLowerCase())) {
                                handler.sendPacket(online.getConn(), new WSPacketCosmeticGive(player.getPlayerId(), player.getRankOrDefault() == Rank.CUSTOM ? player.getCustomColor() : player.getRankOrDefault().getColor()));
                                handler.sendPacket(online.getConn(), new WSPacketCosmeticGive(player.getPlayerId(), true));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //System.out.println("Sending cosmetics to player " + online.getUsername());
                }
            }

            handler.sendPacket(conn, new WSSendChatMessage("§bThanks for using LCProxy!\n§bYour cosmetics have been §aactivated§b."));
        } else {
            player.setServer("");
        }
    }
}
