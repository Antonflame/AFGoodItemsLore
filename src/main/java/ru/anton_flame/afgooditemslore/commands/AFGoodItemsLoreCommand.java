package ru.anton_flame.afgooditemslore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.anton_flame.afgooditemslore.AFGoodItemsLore;
import ru.anton_flame.afgooditemslore.utils.Hex;

import java.util.Collections;
import java.util.List;

public class AFGoodItemsLoreCommand implements CommandExecutor, TabCompleter {
    private final AFGoodItemsLore plugin;
    public AFGoodItemsLoreCommand(AFGoodItemsLore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length < 1 || !strings[0].equalsIgnoreCase("reload")) {
            for (String message : plugin.help) {
                commandSender.sendMessage(Hex.color(message));
            }
            return false;
        }

        if (strings.length == 1 && strings[0].equalsIgnoreCase("reload")) {
            if (!commandSender.hasPermission("afgooditemslore.reload")) {
                commandSender.sendMessage(Hex.color(plugin.noPermission));
                return false;
            }

            plugin.reloadConfig();
            commandSender.sendMessage(Hex.color(plugin.reloaded));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }
}
