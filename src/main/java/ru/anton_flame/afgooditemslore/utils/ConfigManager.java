package ru.anton_flame.afgooditemslore.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ConfigManager {

    public static String effectFormat, zeroEffectDuration, enchantFormat, noPermission, reloaded;
    public static List<String> effectsFormat, enchantsFormat, help;
    public static ConfigurationSection effectsSection, enchantsSection, effectLevels, enchantLevels;

    public static void setupConfigValues(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection settings = config.getConfigurationSection("settings");
        ConfigurationSection messages = config.getConfigurationSection("messages");

        effectFormat = Hex.color(settings.getString("effect-format"));
        zeroEffectDuration = Hex.color(settings.getString("zero-effect-duration"));
        enchantFormat = Hex.color(settings.getString("enchant-format"));
        effectsSection = settings.getConfigurationSection("effects");
        enchantsSection = settings.getConfigurationSection("enchants");
        noPermission = Hex.color(messages.getString("no-permission"));
        reloaded = Hex.color(messages.getString("reloaded"));
        effectsFormat = Hex.color(settings.getStringList("effects-format"));
        enchantsFormat = Hex.color(settings.getStringList("enchants-format"));
        effectLevels = settings.getConfigurationSection("effect-levels");
        enchantLevels = settings.getConfigurationSection("enchant-levels");
        help = Hex.color(messages.getStringList("help"));
    }
}
