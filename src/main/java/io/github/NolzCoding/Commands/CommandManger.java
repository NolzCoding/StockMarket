package io.github.NolzCoding.Commands;

import io.github.NolzCoding.GUI.GUIManager;
import io.github.NolzCoding.Main;
import io.github.NolzCoding.Utils.GUI;
import io.github.NolzCoding.Utils.GUIs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandManger implements CommandExecutor {
    private final Main main = Main.getmain();
    private HashMap<GUIs, GUI> guIsGUIHashMap;
    public void setGuIsGUIHashMap(HashMap<GUIs, GUI> guIsGUIHashMap) {
        this.guIsGUIHashMap = guIsGUIHashMap;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("openstock")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                guIsGUIHashMap.get(GUIs.STOCKMENU).openInventory(player);
                return true;
            }
        }

        return false;
    }
}
