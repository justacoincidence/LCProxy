package me.lcproxy.jb.player;

import lombok.Getter;
import org.java_websocket.WebSocket;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    @Getter
    private static Map<UUID, Player> playerMap;

    public PlayerManager() {
        playerMap = new ConcurrentHashMap<>();
    }

    public Player getOrCreatePlayer(WebSocket conn, String username) {
        return playerMap.getOrDefault(conn.getAttachment(), this.createProfile(conn, username));
    }

    public Player createProfile(WebSocket conn, String username) {
        if (playerMap.containsKey(conn.getAttachment()))
            return playerMap.get(conn.getAttachment());

        Player player = new Player(conn.getAttachment(), username);
        player.setConn(conn);
        player.load();
        //new Thread(player::load).start();
        return playerMap.put(conn.getAttachment(), player);
    }

    public void removePlayer(UUID playerId, boolean thread) {
        if (!playerMap.containsKey(playerId))
            return;
        playerMap.get(playerId).save(thread);
        playerMap.remove(playerId);
    }

    public Player getPlayerById(UUID id) {
        if (playerMap.containsKey(id))
            return playerMap.get(id);
        return null;
    }

    public int getTotalOnline() {
        return playerMap.size();
    }
}
