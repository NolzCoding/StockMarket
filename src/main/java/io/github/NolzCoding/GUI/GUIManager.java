package io.github.NolzCoding.GUI;

import io.github.NolzCoding.Main;
import io.github.NolzCoding.Utils.GUI;
import io.github.NolzCoding.Utils.GUIs;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GUIManager {

    private HashMap<GUIs, GUI> guis;
    private Main main = Main.getmain();
    private final NamespacedKey namespacedKey = new NamespacedKey(Main.getmain(), "stockitem");
    private final ArrayList<Inventory> inventories = new ArrayList<>();
    private final HashMap<UUID, String> activebuy = new HashMap<>();
    private final HashMap<UUID, String> activesell = new HashMap<>();

    public GUIManager () {
        guis = new HashMap<>();
        guis.put(GUIs.STOCKMENU, new StockMenu(namespacedKey, guis, this));
        guis.put(GUIs.INDISTOCKMENU, new InvidualStock(this));
    }

    public HashMap<GUIs, GUI> getGuis() {
        return guis;
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public ArrayList<Inventory> getInventories() {
        return inventories;
    }

    public HashMap<UUID, String> getActivebuy() {
        return activebuy;
    }

    public HashMap<UUID, String> getActivesell() {
        return activesell;
    }
}
