package io.github.NolzCoding.GUI;

import io.github.NolzCoding.Data.PlayerData;
import io.github.NolzCoding.Main;
import io.github.NolzCoding.Utils.GUI;
import io.github.NolzCoding.Utils.Stock;
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
import java.util.UUID;

public class MyStocksMenu extends GUI {
    private final HashMap<UUID, PlayerData> playerDataHashMap = Main.getmain().getPlayerManager().getPlayerDataHashMap();
    private final HashMap<String, Stock> stockInfo = Main.getmain().getStockInfo().getStocks();
    private final NamespacedKey namespacedKey;
    public MyStocksMenu(NamespacedKey namespacedKey) {
        super(InventoryType.CHEST, ChatColor.GREEN + "My stocks");
        this.namespacedKey = namespacedKey;
    }

    @Override
    public void openInventory(Player player) {
        Inventory inventory = getInventory();
        HashMap<String, Integer> playerData = playerDataHashMap.get(player.getUniqueId()).getStockHash();
        int index = 0;
        inventory.clear();
        for (String key : playerData.keySet()) {
            if (playerData.get(key) != 0) {
                Stock stock = stockInfo.get(key);
                List<String> lores = new ArrayList<>();
                ItemStack i = new ItemStack(Material.PAPER);
                ItemMeta m = i.getItemMeta();
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
        }

        super.openInventory(player);
    }
}
