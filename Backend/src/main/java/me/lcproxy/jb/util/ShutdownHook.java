package me.lcproxy.jb.util;

import lombok.SneakyThrows;
import me.lcproxy.jb.WebServer;
import me.lcproxy.jb.object.CC;
import me.lcproxy.jb.player.Player;
import me.lcproxy.jb.player.PlayerManager;
import me.lcproxy.jb.server.packets.WSSendChatMessage;

public class ShutdownHook extends Thread {
    @SneakyThrows
    @Override
    public void run() {
        for (Player user : PlayerManager.getPlayerMap().values()) {
            WebServer.getInstance().getServerHandler().sendPacket(user.getConn(), new WSSendChatMessage(CC.RED.getCode() + "LCProxy servers are restarting.\n" + CC.RED.getCode() + "You may see \"Connecting\" for up to a minute."));
        }
    }
}
