package ru.anton_flame.afgooditemslore;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import ru.anton_flame.afgooditemslore.commands.AFGoodItemsLoreCommand;
import ru.anton_flame.afgooditemslore.tasks.LoreUpdateTask;

import java.util.List;

public final class AFGoodItemsLore extends JavaPlugin {

    public String effectFormat, zeroEffectDuration, enchantFormat, noPermission, reloaded;
    public List<String> effectsFormat, enchantsFormat, help;
    public ConfigurationSection effectsSection, enchantsSection;

    @Override
    public void onEnable() {
        getLogger().info("Плагин был включен!");
        saveDefaultConfig();
        setupConfigValues();

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

    public void setupConfigValues() {
        effectFormat = getConfig().getString("settings.effect-format");
        zeroEffectDuration = getConfig().getString("settings.zero-effect-duration");
        enchantFormat = getConfig().getString("settings.enchant-format");
        effectsSection = getConfig().getConfigurationSection("settings.effects");
        enchantsSection = getConfig().getConfigurationSection("settings.enchants");
        noPermission = getConfig().getString("messages.no-permission");
        reloaded = getConfig().getString("messages.reloaded");
        effectsFormat = getConfig().getStringList("settings.effects-format");
        enchantsFormat = getConfig().getStringList("settings.enchants-format");
        help = getConfig().getStringList("messages.help");
    }
}
