package io.github.NolzCoding.Events;
import io.github.NolzCoding.Data.PlayerData;
import io.github.NolzCoding.Data.PlayerManager;
import io.github.NolzCoding.GUI.GUIManager;
import io.github.NolzCoding.Main;
import io.github.NolzCoding.Utils.GUI;
import io.github.NolzCoding.Utils.GUIs;
import io.github.NolzCoding.Utils.Stock;
import io.github.NolzCoding.Utils.StockInfo;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EventsHandler implements Listener {

    private final Main main = Main.getmain();
    private final PlayerManager playerManager = main.getPlayerManager();
    private final HashMap<UUID, PlayerData> dataHashMap = playerManager.getPlayerDataHashMap();
    private final GUIManager guiManager = main.getGuiManager();
    private final HashMap<String, Stock> stockInfo = main.getStockInfo().getStocks();
    private final HashMap<UUID, String> activebuy = guiManager.getActivebuy();
    private final HashMap<UUID, String> activesell = guiManager.getActivesell();
    private final HashMap<GUIs, GUI> guis = guiManager.getGuis();
    private final Economy economy = main.getEcon();
    private final ArrayList<Inventory> invs = guiManager.getInventories();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playerManager.loadPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerManager.savePlayer(playerManager.getPlayerDataHashMap().get(event.getPlayer().getUniqueId()));
        playerManager.savePlayerData();
    }

    @EventHandler
    public void clickInv(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        for (GUI gui : guis.values()) {
            if (inventory == gui.getInventory()) {
                event.setCancelled(gui.leftClick(event.getSlot(), (Player) event.getWhoClicked(), inventory));
                return;
            }

        }
        for (Inventory inv : invs) {
            if (inventory == inv) {
                guis.get(GUIs.INDISTOCKMENU).leftClick(event.getSlot(), (Player) event.getWhoClicked(), inventory, ChatColor.stripColor( event.getView().getTitle()));
                event.setCancelled(true);
                return;
            }
        }

    }

    @EventHandler
    public void closeInv(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (invs.contains(inventory)) {
            invs.remove(inventory);
        }
    }

    private void sellStocks(Player player, String sym, float depo,int amount) {
        EconomyResponse r = economy.depositPlayer(player, depo);
        if (r.transactionSuccess()) {
            player.sendMessage(ChatColor.GREEN + "You sold " + amount + " shares of " + sym + " stock");
            player.sendMessage(ChatColor.GREEN + "+$" + depo);
            HashMap<String, Integer> playerData = dataHashMap.get(player.getUniqueId()).getStockHash();
            playerData.put(sym, playerData.get(sym) - amount);

        }
        else {
            player.sendMessage(String.format(ChatColor.RED + "An error occured: %s", r.errorMessage));
        }
    }
    private void buyStocks(Player player, String sym, float cost, int parsed) {
        if (economy.getBalance(player) >= cost) {
            EconomyResponse r = economy.withdrawPlayer(player, cost);
            if (r.transactionSuccess()) {
                player.sendMessage(ChatColor.GREEN + "You bought " + parsed + " shares of " + sym + " stock");
                player.sendMessage(ChatColor.RED + "-$" + cost);
                activebuy.remove(player.getUniqueId());
                HashMap<String, Integer> playerData = dataHashMap.get(player.getUniqueId()).getStockHash();
                if (playerData.containsKey(sym)) {
                    playerData.put(sym, playerData.get(sym) + parsed);
                }
                else {
                    playerData.put(sym, parsed);
                }
            }
            else {
                player.sendMessage(String.format(ChatColor.RED + "An error occured: %s", r.errorMessage));

            }
        } else {
            player.sendMessage(ChatColor.RED + "Error: Insufficient balance");
        }
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        if (activebuy.containsKey(event.getPlayer().getUniqueId())) {
            if (event.getMessage().equalsIgnoreCase("cancel")) {
                Player player = event.getPlayer();
                player.sendMessage(ChatColor.RED + "You canceled buying " + activebuy.get(player) + " shares" );
                activebuy.remove(player.getUniqueId());
                event.setCancelled(true);
                return;
            }
            else if (event.getMessage().equalsIgnoreCase("max")) {
                Player player = event.getPlayer();
                String sym = activebuy.get(player.getUniqueId());
                Stock stock = stockInfo.get(sym);
                double amount = economy.getBalance(player) / stock.getPrice();
                int flored = (int) Math.floor(amount);
                if (flored <= 0) {
                    player.sendMessage(ChatColor.RED + "Error: Insufficient balance");
                    activebuy.remove(player.getUniqueId());
                    event.setCancelled(true);
                    return;
                }
                buyStocks(player, sym, flored * stock.getPrice(), flored);
                activebuy.remove(player.getUniqueId());
                event.setCancelled(true);
                return;
            }
            Player player = event.getPlayer();
            int parsed = 0;
            try {
                parsed = Integer.parseInt(event.getMessage());
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Error: Please enter a valid number -(No decimals)-");
                activebuy.remove(player.getUniqueId());
                event.setCancelled(true);
            }

            if (parsed <= 0) {
                player.sendMessage(ChatColor.RED + "Error: Cannot buy 0 or a negative number amount of shares");
                activebuy.remove(player.getUniqueId());
                event.setCancelled(true);
                return;
            }
            String sym = activebuy.get(player.getUniqueId());
            Stock stock = stockInfo.get(sym);
            float cost = stock.getPrice() * parsed;

            buyStocks(player, sym, cost, parsed);
            activebuy.remove(player.getUniqueId());
            event.setCancelled(true);

        }
        else if (activesell.containsKey(event.getPlayer().getUniqueId())) {
            if (event.getMessage().equalsIgnoreCase("cancel")) {
                Player player = event.getPlayer();
                player.sendMessage(ChatColor.RED + "You canceled selling " + activesell.get(player.getUniqueId()) + " shares" );
                activesell.remove(player.getUniqueId());
                event.setCancelled(true);
                return;
            }
            else if (event.getMessage().equalsIgnoreCase("max")) {
                Player player = event.getPlayer();

                HashMap<String, Integer> playerData = dataHashMap.get(player.getUniqueId()).getStockHash();
                String sym = activesell.get(player.getUniqueId());
                if (playerData.containsKey(sym)) {

                    int owned = playerData.get(sym);
                    if (owned != 0) {
                        Stock stock = stockInfo.get(sym);
                        float depo = stock.getPrice() * owned;
                        sellStocks(player, sym, depo, owned);
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "Error: You don't own any of this stock");
                    }
                }
                else {
                    player.sendMessage(ChatColor.RED + "Error: You don't own any of this stock");
                }

                activesell.remove(player.getUniqueId());
                event.setCancelled(true);
                return;
            }
            Player player = event.getPlayer();
            int parsed = 0;
            try {
                parsed = Integer.parseInt(event.getMessage());
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Error: Please enter a valid number -(No decimals)-");
                event.setCancelled(true);
                activesell.remove(player.getUniqueId());
            }

            if (parsed <= 0) {
                player.sendMessage(ChatColor.RED + "Error: Cannot sell 0 or a negative number amount of shares");
                activesell.remove(player.getUniqueId());
                event.setCancelled(true);
                return;
            }
            HashMap<String, Integer> playerData = dataHashMap.get(player.getUniqueId()).getStockHash();
            String sym = activesell.get(player.getUniqueId());
            if (!(playerData.containsKey(sym) && playerData.get(sym) != 0)) {
                player.sendMessage(ChatColor.RED + "Error: You don't own any" + sym + " shares");
                activesell.remove(player.getUniqueId());
                event.setCancelled(true);
                return;
            }

            int owned = playerData.get(sym);
            Stock stock = stockInfo.get(sym);
            float depo = stock.getPrice() * parsed;
            sellStocks(player, sym, depo, parsed);

            activesell.remove(player.getUniqueId());
            event.setCancelled(true);

        }
    }

}
