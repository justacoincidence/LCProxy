package me.lcproxy.jb;

import io.netty.buffer.Unpooled;
import io.netty.util.internal.ThreadLocalRandom;
import lombok.Getter;
import lombok.SneakyThrows;
import me.lcproxy.jb.mongo.MongoManager;
import me.lcproxy.jb.player.Player;
import me.lcproxy.jb.player.PlayerManager;
import me.lcproxy.jb.player.Rank;
import me.lcproxy.jb.server.ByteBufWrapper;
import me.lcproxy.jb.server.ServerHandler;
import me.lcproxy.jb.server.packets.WSPacketCosmeticGive;
import me.lcproxy.jb.server.packets.WSPacketEmoteGive;
import me.lcproxy.jb.server.packets.WSSendChatMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WebServer extends WebSocketServer {

    @Getter
    private static WebServer instance;

    @Getter
    private final ServerHandler serverHandler;

    @Getter
    private final PlayerManager playerManager;

    @Getter
    private final MongoManager mongoManager;

    @Getter
    private final int serverId;

    private final ExecutorService threadPool;

    @SneakyThrows
    public WebServer() {
        super(new InetSocketAddress("0.0.0.0", 19486));
        instance = this;
        this.serverHandler = new ServerHandler();
        this.playerManager = new PlayerManager();
        this.mongoManager = new MongoManager();
        this.serverId = ThreadLocalRandom.current().nextInt(1, 999999);
        this.threadPool = new ThreadPoolExecutor(128, 1024 * 4,
                30L, TimeUnit.SECONDS,
                new SynchronousQueue<>());
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        threadPool.execute(() -> {
            try {
                System.out.println("Connection from " + clientHandshake.getFieldValue("username"));

                serverHandler.sendPacket(webSocket, new WSSendChatMessage("§b[LCProxy] §aHang on, we're loading your data."));

                UUID funnyUUID = UUID.fromString(clientHandshake.getFieldValue("playerid"));

                if (PlayerManager.getPlayerMap().containsKey(funnyUUID)) {
                    playerManager.removePlayer(funnyUUID, false);
                }

                webSocket.setAttachment(funnyUUID);

                Player player = null;

                try {
                    player = playerManager.getOrCreatePlayer(webSocket, clientHandshake.getFieldValue("username"));
                    player.sendAllPackets();
                } catch (Exception e) {
                    // ignore
                }

                serverHandler.sendPacket(webSocket, new WSSendChatMessage("§bThanks for using LCProxy!\n§bYour cosmetics have been §aactivated§b."));

//                System.out.println("Sent packets to " + player.getUsername());

                for (Player online : PlayerManager.getPlayerMap().values()) {
                    if(online != player)
                        serverHandler.sendPacket(online.getConn(), new WSPacketCosmeticGive(player.getPlayerId(), player.getRankOrDefault() == Rank.CUSTOM ? player.getCustomColor() : player.getRankOrDefault().getColor()));
                }

                WebServer.getInstance().getServerHandler().sendPacket(webSocket, new WSPacketCosmeticGive());
                WebServer.getInstance().getServerHandler().sendPacket(webSocket, new WSPacketEmoteGive());
            } catch (Exception e) {
                System.out.println("Error on open socket. Username: " + clientHandshake.getFieldValue("username"));
                e.printStackTrace();
            }
        });
    }

    public void updateTags() {
        /*for (Player user : PlayerManager.getPlayerMap().values()) {
            for (Player online : PlayerManager.getPlayerMap().values()) {
                WebServer.getInstance().getServerHandler().sendPacket(online.getConn(), new WSPacketCosmeticGive(user.getPlayerId(), user.getRankorDefault().getColor()));
            }
        }*/
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        threadPool.execute(() -> {
            if (webSocket.getAttachment() != null) {
                Player player = PlayerManager.getPlayerMap().get(webSocket.getAttachment());
                if (player != null) {
                    player.save(false);
                    this.playerManager.removePlayer(webSocket.getAttachment(), true);
                } else {
                    System.out.println("Player is null.");
                }
            }
        });
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

    }

    @Override
    public void onMessage(WebSocket webSocket, ByteBuffer message) {
        this.serverHandler.handlePacket(webSocket, new ByteBufWrapper(Unpooled.wrappedBuffer(message.array())));
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}