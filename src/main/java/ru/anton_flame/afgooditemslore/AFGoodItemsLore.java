package ru.anton_flame.afgooditemslore;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ru.anton_flame.afgooditemslore.commands.AFGoodItemsLoreCommand;
import ru.anton_flame.afgooditemslore.tasks.LoreUpdateTask;

public final class AFGoodItemsLore extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Плагин был включен!");
        saveDefaultConfig();

        PluginCommand afGoodItemsLoreCommand = getCommand("afgooditemslore");
        afGoodItemsLoreCommand.setExecutor(new AFGoodItemsLoreCommand(this));
        afGoodItemsLoreCommand.setTabCompleter(new AFGoodItemsLoreCommand(this));

        LoreUpdateTask loreUpdateTask = new LoreUpdateTask(this);
        loreUpdateTask.runTaskTimerAsynchronously(this, 0, 20);
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин был выключен!");
    }
}
