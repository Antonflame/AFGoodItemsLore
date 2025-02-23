package ru.anton_flame.afgooditemslore;

import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import ru.anton_flame.afgooditemslore.commands.AFGoodItemsLoreCommand;
import ru.anton_flame.afgooditemslore.tasks.LoreUpdateTask;
import ru.anton_flame.afgooditemslore.utils.ConfigManager;

import java.util.List;

public final class AFGoodItemsLore extends JavaPlugin {

    public NamespacedKey hasEffects = new NamespacedKey(this, "has_effects");
    public NamespacedKey hasEnchants = new NamespacedKey(this, "has_enchants");
    public NamespacedKey stringsCount = new NamespacedKey(this, "string_counts");

    @Override
    public void onEnable() {
        getLogger().info("Плагин был включен!");
        saveDefaultConfig();
        ConfigManager.setupConfigValues(this);

        PluginCommand afGoodItemsLoreCommand = getCommand("afgooditemslore");
        AFGoodItemsLoreCommand commandClass = new AFGoodItemsLoreCommand(this);
        afGoodItemsLoreCommand.setExecutor(commandClass);
        afGoodItemsLoreCommand.setTabCompleter(commandClass);

        LoreUpdateTask loreUpdateTask = new LoreUpdateTask(this);
        loreUpdateTask.runTaskTimerAsynchronously(this, 0, 20);
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин был выключен!");
    }
}
