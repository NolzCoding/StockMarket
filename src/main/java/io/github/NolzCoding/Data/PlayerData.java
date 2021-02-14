package io.github.NolzCoding.Data;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private Player player;
    private HashMap<String, Integer> stockHash;


    public PlayerData(UUID uuid, Player player, HashMap<String, Integer> stockHash) {
        this.uuid = uuid;
        this.player = player;
        this.stockHash = stockHash;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public HashMap<String, Integer> getStockHash() {
        return stockHash;
    }

    public void setStockHash(HashMap<String, Integer> stockHash) {
        this.stockHash = stockHash;
    }
}
