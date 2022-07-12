package me.lcproxy.jb.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class MongoManager {
    private final MongoClient client;
    private DB database;
    private DBCollection profileCollection;

    @SneakyThrows
    public MongoManager() {
        MongoClientURI uri = new MongoClientURI(
                "mongodb+srv://amonger:oP4RN5eKsBqy6dP9MTA54XS89sM7Tgrj@cluster0.xetr3.mongodb.net/LCProxy?retryWrites=true&w=majority");
        this.client = new MongoClient(uri);

        try {
            this.database = this.client.getDB("LCProxy");
            this.profileCollection = this.database.getCollection("profiles");
            System.out.println("Loaded mongo successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
