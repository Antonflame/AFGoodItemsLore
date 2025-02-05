package ru.anton_flame.afgooditemslore.tasks;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.anton_flame.afgooditemslore.AFGoodItemsLore;
import ru.anton_flame.afgooditemslore.utils.Hex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoreUpdateTask extends BukkitRunnable {
    private final AFGoodItemsLore plugin;
    public LoreUpdateTask(AFGoodItemsLore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType() == Material.AIR) continue;
                if (!item.hasItemMeta()) continue;

                String enchantFormat = plugin.enchantFormat;
                String effectFormat = plugin.effectFormat;

                ItemMeta meta = item.getItemMeta();
                List<Component> lore = new ArrayList<>();
                if (meta.lore() != null) {
                    String strippedEnchantStart = ChatColor.stripColor(Hex.color(enchantFormat.split("%")[0]));
                    String strippedEffectStart = ChatColor.stripColor(Hex.color(effectFormat.split("%")[0]));

                    for (Component line : meta.lore()) {
                        String plainText = line.toString();
                        String strippedLine = ChatColor.stripColor(plainText);

                        if (!strippedLine.startsWith(strippedEnchantStart) && !strippedLine.startsWith(strippedEffectStart)) {
                            lore.add(line);
                        }
                    }
                }

                if (meta.hasEnchants()) {
                    ConfigurationSection enchantsSection = plugin.enchantsSection;

                    for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                        Enchantment enchantment = entry.getKey();
                        if (enchantment == null) continue;
                        String enchant = enchantsSection.getString(entry.getKey().getName());
                        String level = String.valueOf(entry.getValue());
                        String format = Hex.color(enchantFormat.replace("%enchant%", enchant).replace("%level%", level));
                        lore.add(Component.text(format));
                    }

                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }

                if (meta instanceof PotionMeta) {
                    PotionMeta potionMeta = (PotionMeta) meta;
                    ConfigurationSection effectsSection = plugin.effectsSection;

                    if (potionMeta.hasCustomEffects()) {
                        for (PotionEffect potionEffect : potionMeta.getCustomEffects()) {
                            String type = effectsSection.getString(potionEffect.getType().getName());
                            String level = String.valueOf(potionEffect.getAmplifier() + 1);
                            String duration = formatDuration(potionEffect.getDuration() / 20);
                            String format = Hex.color(effectFormat.replace("%type%", type)
                                    .replace("%level%", level)
                                    .replace("%duration%", duration));
                            lore.add(Component.text(format));
                        }
                    } else {
                        PotionData potionData = potionMeta.getBasePotionData();
                        PotionEffectType potionType = potionData.getType().getEffectType();
                        boolean isUpgraded = potionData.isUpgraded();
                        boolean isExtended = potionData.isExtended();
                        if (potionType == null) continue;

                        if (potionData.getType() == PotionType.TURTLE_MASTER) {
                            String slowType = effectsSection.getString("SLOW");
                            String slowLevel = isUpgraded ? "6" : "4";
                            String duration = formatDuration(getBaseEffectDuration(potionData, PotionEffectType.SLOW, isUpgraded, isExtended));
                            String slowFormat = Hex.color(effectFormat.replace("%type%", slowType)
                                    .replace("%level%", slowLevel)
                                    .replace("%duration%", duration));
                            lore.add(Component.text(slowFormat));

                            String resistanceType = effectsSection.getString("DAMAGE_RESISTANCE");
                            String resistanceLevel = isUpgraded ? "4" : "3";
                            String resistanceFormat = Hex.color(effectFormat.replace("%type%", resistanceType)
                                    .replace("%level%", resistanceLevel)
                                    .replace("%duration%", duration));
                            lore.add(Component.text(resistanceFormat));
                        } else {
                            String type = effectsSection.getString(potionType.getName());
                            String level = isUpgraded ? "2" : "1";
                            String duration = formatDuration(getBaseEffectDuration(potionData, potionType, isUpgraded, isExtended));
                            String format = Hex.color(effectFormat.replace("%type%", type)
                                    .replace("%level%", level)
                                    .replace("%duration%", duration));
                            lore.add(Component.text(format));
                        }
                    }

                    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                }

                meta.lore(lore);
                item.setItemMeta(meta);
            }
        }
    }

    private int getBaseEffectDuration(PotionData potionData, PotionEffectType type, boolean isUpgraded, boolean isExtended) {
        int duration = 0;
        switch (type.getName()) {
            case "INVISIBILITY":
            case "FIRE_RESISTANCE":
            case "WATER_BREATHING":
                duration = 180;
                if (isExtended) duration = 480;
                break;
            case "JUMP":
            case "INCREASE_DAMAGE":
            case "SPEED":
                duration = 180;
                if (isExtended) duration = 480;
                if (isUpgraded) duration = 90;
                break;
            case "SLOW":
                if (potionData.getType() == PotionType.TURTLE_MASTER) {
                    duration = 20;
                    if (isExtended) duration = 40;
                } else {
                    duration = 90;
                    if (isExtended) duration = 240;
                }
                if (isUpgraded) duration = 20;
                break;
            case "POISON":
                duration = 45;
                if (isExtended) duration = 90;
                if (isUpgraded) duration = 21;
                break;
            case "REGENERATION":
                duration = 45;
                if (isExtended) duration = 90;
                if (isUpgraded) duration = 22;
                break;
            case "WEAKNESS":
            case "SLOW_FALLING":
                duration = 90;
                if (isExtended) duration = 240;
                break;
            case "LUCK":
                duration = 300;
                break;
            case "DAMAGE_RESISTANCE":
                if (potionData.getType() == PotionType.TURTLE_MASTER) {
                    duration = 20;
                    if (isExtended) duration = 40;
                    if (isUpgraded) duration = 20;
                }
                break;
        }
        return duration;
    }

    private String formatDuration(int seconds) {
        if (seconds == 0) {
            return plugin.zeroEffectDuration;
        }

        StringBuilder result = new StringBuilder();
        if (seconds >= 3600) {
            result.append(seconds / 3600).append(" ч");
            seconds %= 3600;
        }

        if (seconds >= 60) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(seconds / 60).append(" мин");
            seconds %= 60;
        }

        if (seconds > 0) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(seconds).append(" сек");
        }

        return result.toString();
    }
}
