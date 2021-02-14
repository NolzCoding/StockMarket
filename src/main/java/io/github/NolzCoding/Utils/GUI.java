package io.github.NolzCoding.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class GUI {

    private final Inventory inventory;
    private final InventoryType inventoryType;
    private final String name;

    public GUI(InventoryType size, String name) {
        inventory = Bukkit.createInventory(null,size,name);
        inventoryType = size;
        this.name = name;
    }

    public void openInventory(Player player) {
        player.openInventory(inventory);
    }

    public void openInventory(Player player, String sym) {
        player.openInventory(inventory);
    }

    public boolean leftClick(Integer slot, Player player, Inventory inventory) {

        return false;
    }

    public boolean leftClick(Integer slot, Player player, Inventory inventory, String sym) {

        return false;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getName() {
        return name;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public ItemStack createItem(Material mat, String name, List<String> lore) {
        ItemStack itemStack = new ItemStack(mat);
        ItemMeta itemMeta = itemStack.getItemMeta();
        int i = 0;
        for (String string : lore) {
            lore.set(i,ChatColor.translateAlternateColorCodes('&', string));
        }
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',name));
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
