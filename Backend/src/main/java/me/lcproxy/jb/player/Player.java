package me.lcproxy.jb.player;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.client.model.DBCollectionUpdateOptions;
import lombok.Getter;
import lombok.Setter;
import me.lcproxy.jb.WebServer;
import me.lcproxy.jb.server.ServerHandler;
import me.lcproxy.jb.server.packets.WSPacketCosmeticGive;
import org.java_websocket.WebSocket;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
@SuppressWarnings("unchecked")
public class Player {
    private UUID playerId;
    private String username;

    private String server;
    private WebSocket conn;

    private ArrayList<Integer> enabledCosmetics = new ArrayList<>();

    private int customColor = 16777215;

    private Rank rank = Rank.USER;

    @ConstructorProperties({"playerId", "username"})
    public Player(UUID playerId, String username) {
        this.playerId = playerId;
        this.username = username;
    }

    public void load() {
        try {
            DBObject profile = WebServer.getInstance().getMongoManager().getProfileCollection().find(new BasicDBObject("_id", this.playerId.toString())).one();

            if (profile == null) return;

            if (profile.get("rank") != null)
                this.rank = Rank.getRankById((int) profile.get("rank"));
            if (profile.get("cosmetics") != null)
                ((BasicDBList) profile.get("cosmetics")).forEach(string -> this.getEnabledCosmetics().add((Integer) string));
            if (profile.get("color") != null)
                this.customColor = (int) profile.get("color");

            //if (this.isOnline()) this.sendAllPackets();
        } catch (Exception e) {
            System.out.println("Error loading user profile.");
            e.printStackTrace();
        }
    }

    public Rank getRankOrDefault() {
        if (rank == null) {
            return Rank.USER;
        } else {
            return rank;
        }
    }

    public void sendAllPackets() {
        ServerHandler handler = WebServer.getInstance().getServerHandler();
        handler.sendPacket(conn, new WSPacketCosmeticGive());
        /*for (Player online : PlayerManager.getPlayerMap().values()) {
            if (online != this)
                handler.sendPacket(conn, new WSPacketCosmeticGive(online.getPlayerId()));
        }*/
        getRankOrDefault();
        /*handler.sendPacket(conn, new WSPacketCosmeticGive());
        handler.sendPacket(conn, new WSPacketEmoteGive());*/
    }

    public void save(boolean thread) {
        if (thread) {
            new Thread(() -> this.save(false));
            return;
        }
        System.out.println("Saving " + playerId + "'s profile.");
        WebServer.getInstance().getMongoManager().getProfileCollection().update(new BasicDBObject("_id", this.playerId.toString()), this.toJson(), new DBCollectionUpdateOptions().upsert(true));
    }

    private DBObject toJson() {
        return new BasicDBObjectBuilder().add("_id", this.playerId.toString())
                .add("username", this.username)
                .add("rank", this.rank.id)
                .add("cosmetics", this.enabledCosmetics)
                .add("color", this.customColor)
                .get();
    }

    public boolean isOnline() {
        return this.conn != null;
    }
}
