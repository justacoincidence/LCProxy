package me.lcproxy.jb;

import me.lcproxy.jb.util.GenFromIndexFile;
import me.lcproxy.jb.util.ShutdownHook;
import org.java_websocket.server.WebSocketServer;

public class Main {
    public static void main(String[] args) {
        ShutdownHook shutDownTask = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(shutDownTask);

        Discord discord = new Discord();
        new Thread(() -> discord.initialize()).start();

        GenFromIndexFile.load();

        WebSocketServer server = new WebServer();
        server.setReuseAddr(true);
        server.setTcpNoDelay(true);
        server.setConnectionLostTimeout(0);
        server.run();
    }
}
