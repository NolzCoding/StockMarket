package io.github.NolzCoding.GUI;

import io.github.NolzCoding.Main;
import io.github.NolzCoding.Utils.GUI;
import io.github.NolzCoding.Utils.GUIs;
import io.github.NolzCoding.Utils.Stock;
import io.github.NolzCoding.Utils.StockInfo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StockMenu extends GUI {

    private final HashMap<String, Stock> stockHashMap;
    private final HashMap<GUIs, GUI> guIs;
    private GUIManager guiManager;
    private final NamespacedKey namespacedKey;


    public StockMenu(NamespacedKey namespacedKey, HashMap<GUIs, GUI> guis, GUIManager guiManager) {
        super(InventoryType.CHEST, ChatColor.GREEN + "Stock Menu");
        Main main = Main.getmain();
        StockInfo stockInfo = main.getStockInfo();
        stockHashMap = stockInfo.getStocks();
        this.guIs = guis;
        this.namespacedKey = namespacedKey;
    }

    @Override
    public void openInventory(Player player) {
        Inventory inventory = getInventory();
        int index = 0;
        for (Stock stock : stockHashMap.values()) {
            List<String> lores = new ArrayList<>();
            ItemStack i = new ItemStack(Material.PAPER);
            ItemMeta m = i.getItemMeta();
            assert m != null;
            m.setDisplayName(ChatColor.GREEN + stock.getName());
            lores.add(ChatColor.translateAlternateColorCodes('&', "&7 Symbol: " + stock.getSymbol()));
            lores.add(ChatColor.translateAlternateColorCodes('&', "&7 Price: $" + stock.getPrice()));
            lores.add(ChatColor.translateAlternateColorCodes('&', "&7 Volume: " + stock.getVolume()));
            m.setLore(lores);
            m.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, stock.getSymbol());
            i.setItemMeta(m);
            inventory.setItem(index, i);
            index++;
        }
        inventory.setItem(26, createItem(Material.PLAYER_HEAD, ChatColor.AQUA  + "My stocks", new ArrayList<>()));
        super.openInventory(player);

    }

    @Override
    public boolean leftClick(Integer slot, Player player, Inventory inventory) {
        ItemStack itemStack = inventory.getItem(slot);
        if (itemStack != null && itemStack.getType().equals(Material.PAPER)) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null && itemMeta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING)) {
                guIs.get(GUIs.INDISTOCKMENU).openInventory(player,
                        itemMeta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING)
                );
                return true;
            }
        }

        if (slot == 26) {
            guIs.get(GUIs.MYSTOCKSMENU).openInventory(player);
        }

        return true;
    }
}