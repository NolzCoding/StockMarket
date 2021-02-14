package io.github.NolzCoding.GUI;

import io.github.NolzCoding.Data.PlayerData;
import io.github.NolzCoding.Main;
import io.github.NolzCoding.Utils.GUI;
import io.github.NolzCoding.Utils.Stock;
import io.github.NolzCoding.Utils.StockInfo;
import net.milkbowl.vault.chat.Chat;
import net.minecraft.server.v1_16_R2.InventoryLargeChest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InvidualStock extends GUI {

    private final Main main = Main.getmain();
    private final HashMap<UUID, PlayerData> playerDataHashMap = main.getPlayerManager().getPlayerDataHashMap();
    private final HashMap<String, Stock> stocks = main.getStockInfo().getStocks();
    private final ArrayList<Inventory> invs;
    private final HashMap<UUID, String> activebuy;
    private final HashMap<UUID, String> activesell;

    public InvidualStock(GUIManager guiManager) {
        super(InventoryType.CHEST, ChatColor.translateAlternateColorCodes('&', "&a%STOCK%"));
        activebuy = guiManager.getActivebuy();
        activesell = guiManager.getActivesell();
        invs = guiManager.getInventories();

        Inventory inventory = getInventory();
        ItemStack itemStack = createItem(Material.GRAY_STAINED_GLASS_PANE, "", new ArrayList<>());

        for (int i = 0; i < inventory.getSize(); i++) {
            itemStack.setType(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("");
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack.clone());
        }
        inventory.setItem(12, createItem(Material.GREEN_STAINED_GLASS_PANE, "&aBuy shares", new ArrayList<>()));
        inventory.setItem(13, createItem(Material.EMERALD_BLOCK, "&a%Stock%", new ArrayList<>()));
        inventory.setItem(14, createItem(Material.RED_STAINED_GLASS_PANE, "&cSell shares", new ArrayList<>()));
    }

    @Override
    public void openInventory(Player player, String sym) {
        HashMap<String, Integer> playerData = playerDataHashMap.get(player.getUniqueId()).getStockHash();
        Stock stock = stocks.get(sym);
        int stocksowned = 0;
        float stocksvalue = 0;
        float pricestock = stock.getPrice();
        if (playerData.containsKey(sym)) {
            stocksowned = playerData.get(sym);
            stocksvalue = stocksowned * pricestock;
        }
        ItemStack itemStack = getInventory().getItem(13);
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(color("&7Shares: &a" + stocksowned));
        lore.add(color("&7Value: &a$" + stocksvalue));
        lore.add(color("&7Price: &a$" + pricestock));
        itemMeta.setLore(lore);
        itemMeta.setDisplayName(color("&a" + stock.getName()));
        itemStack.setItemMeta(itemMeta);
        getInventory().setItem(13,itemStack);
        Inventory inventory = Bukkit.createInventory(null, InventoryType.CHEST, color("&a" + stock.getSymbol()));
        inventory.setContents(getInventory().getContents());
        invs.add(inventory);
        player.openInventory(inventory);
    }

    private String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    @Override
    public boolean leftClick(Integer slot, Player player, Inventory inventory, String sym) {
        switch (slot) {
            case 12: { //buy
                    player.closeInventory();
                    player.sendMessage(ChatColor.GOLD + "Type in a number or \"max\" to buy the max amount. " +
                            "If you want to cancel just type \"cancel\" ");
                    activebuy.put(player.getUniqueId(), sym);
                break;
            }
            case 14: { //sell
                player.closeInventory();
                player.sendMessage(ChatColor.GOLD + "Type in a number or \"max\" to sell all your shares. " +
                        "If you want to cancel just type \"cancel\" ");
                activesell.put(player.getUniqueId(), sym);
                break;
            }
            default: {
                break;
            }
        }
        return true;
    }
}
