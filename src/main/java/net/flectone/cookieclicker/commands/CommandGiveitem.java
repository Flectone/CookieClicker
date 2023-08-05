package net.flectone.cookieclicker.commands;

import net.flectone.cookieclicker.ItemManager;
import net.flectone.cookieclicker.ItemUtils;
import org.bukkit.block.data.type.Switch;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandGiveitem implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender) return true;
        Player player = (Player) commandSender;
        if (strings.length == 0) {
            player.sendMessage("Ты неправильно ввёл команду");
            return true;
        }
        ItemStack itemStack = ItemManager.get(strings[0].toUpperCase());
        if (itemStack == null) return true;
        int amount = strings.length == 2 && ItemUtils.isNumeric(strings[1]) ? Integer.parseInt(strings[1]) : 1;
        itemStack.setAmount(amount);
        Inventory inventory = player.getInventory();
        inventory.addItem(itemStack);


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length == 1) {
            list.addAll(ItemManager.getKeys());
        }
        Collections.sort(list);
        return list;
    }
}

