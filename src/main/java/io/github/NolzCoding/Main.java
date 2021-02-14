package io.github.NolzCoding;
import io.github.NolzCoding.Commands.CommandManger;
import io.github.NolzCoding.Data.PlayerData;
import io.github.NolzCoding.Data.PlayerManager;
import io.github.NolzCoding.Events.EventsHandler;
import io.github.NolzCoding.GUI.GUIManager;
import io.github.NolzCoding.Utils.GetStockInfo;
import io.github.NolzCoding.Utils.Stock;
import io.github.NolzCoding.Utils.StockInfo;
import io.github.NolzCoding.Utils.WebSocketClient;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private Economy econ = null;

    public static String token = "";
    private StockInfo stockInfo;
    private GUIManager guiManager;
    private GetStockInfo getStockInfo;
    private List<String> symbols;
    private static Main main;
    private CommandManger commandManger;
    private PlayerManager playerManager;


    public static Main getmain() {
        return main;
    }

    @Override
    public void onEnable() {
        main = this;
        //Eco
        initEconomy();
        //Commands
        commandManger = new CommandManger();
        this.getCommand("openstock").setExecutor(commandManger);
        //Config
        saveDefaultConfig();
        saveConfig();
        token = getConfig().getString("token-for-api");
        //Stock setup
        getStockInfo = new GetStockInfo();
        symbols = getSymbols();
        setupStockInfo();
        super.onEnable();
        log.info(String.format("[%s] - Connecting to Websocket server",
                getDescription().getName()));
        try {
            webSocket();
        } catch (InterruptedException e) {
            log.warning(String.format("[%s] - Websocket connection failed, disabling plugin to avoid futher errors",
                    getDescription().getName()));
        }
        log.info(String.format("[%s] - Connected to Websocket server",
                getDescription().getName()));
        //Data
        playerManager = new PlayerManager();
        //GUI
        guiManager = new GUIManager();
        commandManger.setGuIsGUIHashMap(guiManager.getGuis());


        //Events
        getServer().getPluginManager().registerEvents(new EventsHandler(), this);
        log.info(String.format("[%s] - Successfully enabled",
                getDescription().getName()));
    }

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
        savePlayers();
        super.onDisable();
    }

    private void savePlayers() {
        FileConfiguration config = playerManager.getPlayercfg();
        for (PlayerData playerData : playerManager.getPlayerDataHashMap().values()) {
            UUID uuid = playerData.getUuid();
            HashMap<String, Integer> hash = playerData.getStockHash();
            for (String key : hash.keySet()) {
                config.set(uuid + ".stocks." + key, hash.get(key));
            }
        }
    }

    private void setupStockInfo() {

    }

    public Economy getEcon() {
        return econ;
    }

    public CommandManger getCommandManger() {
        return commandManger;
    }

    public StockInfo getStockInfo() {
        return stockInfo;
    }

    private void initEconomy() {
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        else {
            log.severe(String.format("[%s] - Loaded, Vault dependency found!", getDescription().getName()));
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


    private List<String> getSymbols() {
        HashMap<String, Stock> hashMap = new HashMap<>();

        List<String> list = getConfig().getStringList("stock-symbols");
        for (String key : list) {

            hashMap.put(key, new Stock(getStockInfo.getCurrentPrice(key), key, getStockInfo.getInfo(key), "$", 0));
        }
        stockInfo = new StockInfo(hashMap);
        return list;
    }
//wss://ws.finnhub.io?token=c0fceev48v6snrib7nn0
    private void webSocket() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        WebSocket ws = HttpClient
                .newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create("wss://ws.finnhub.io?token=" + token), new WebSocketClient(latch, symbols))
                .join();


        latch.await();

    }
    public GUIManager getGuiManager() {
        return guiManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
