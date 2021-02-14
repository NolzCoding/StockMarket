package io.github.NolzCoding.Data;

import io.github.NolzCoding.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    private Main main;
    private FileConfiguration playercfg;
    private File playerfile;
    private HashMap<UUID, PlayerData> playerDataHashMap = new HashMap<>();

    public PlayerManager () {
        main = Main.getmain();
        File datafolder = main.getDataFolder();
        if (!main.getDataFolder().exists()) {
            datafolder.mkdir();
        }
        playerfile = new File(datafolder, "playerdata.yml");
        createfile(playerfile);

        playercfg = YamlConfiguration.loadConfiguration(playerfile);
    }

    public void loadPlayer(Player player) {
        UUID playerUUID = player.getUniqueId();
        HashMap<String, Integer> stocks = new HashMap<>();
        if (playercfg.contains(playerUUID.toString() + ".stocks")) {
            for (String key : playercfg.getConfigurationSection(playerUUID + ".stocks").getKeys(false)) {
                stocks.put(key, playercfg.getInt(playerUUID + ".stocks." + key));
            }
        }
        playerDataHashMap.put(playerUUID, new PlayerData(playerUUID, player, stocks));
    }

    public void savePlayer(PlayerData playerData) {
        UUID playerUUID = playerData.getUuid();
        HashMap<String, Integer> hashMap = playerData.getStockHash();
        for (String sym : hashMap.keySet()) {
            playercfg.set(playerUUID + ".stocks." + sym, hashMap.get(sym));
        }
    }

    public HashMap<UUID, PlayerData> getPlayerDataHashMap() {
        return playerDataHashMap;
    }

    private void createfile(File file) {
        if (!playerfile.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                disable();
                e.printStackTrace();
            }
        }
    }

    public void savePlayerData() {
        try {
            playercfg.save(playerfile);
        }
        catch (IOException e) {
            disable();
            e.printStackTrace();
        }
    }

    public FileConfiguration getPlayercfg() {
        return playercfg;
    }

    private void disable() {
        main.getServer().getPluginManager().disablePlugin(Main.getPlugin(Main.class));
    }


}
