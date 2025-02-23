package ru.anton_flame.afgooditemslore.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.anton_flame.afgooditemslore.AFGoodItemsLore;
import ru.anton_flame.afgooditemslore.utils.ConfigManager;

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
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : onlinePlayer.getInventory().getContents()) {
                if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) continue;
                ItemMeta itemMeta = item.getItemMeta();
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();

                itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS);

                String effectFormat = ConfigManager.effectFormat;
                List<String> effectsFormat = ConfigManager.effectsFormat;
                ConfigurationSection effectLevels = ConfigManager.effectLevels;
                ConfigurationSection effectsSection = ConfigManager.effectsSection;

                String enchantFormat = ConfigManager.enchantFormat;
                List<String> enchantsFormat = ConfigManager.enchantsFormat;
                ConfigurationSection enchantLevels = ConfigManager.enchantLevels;
                ConfigurationSection enchantsSection = ConfigManager.enchantsSection;

                int sectionStartIndex = -1;
                List<String> lore = itemMeta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                } else {
                    if (container.getOrDefault(plugin.hasEffects, PersistentDataType.INTEGER, 0) == 1) {
                        int maxIndex = lore.size() - effectsFormat.size();
                        for (int i = 0; i <= maxIndex; i++) {
                            boolean isSectionMatch = true;
                            for (int j = 0; j < effectsFormat.size(); j++) {
                                if (!lore.get(i + j).equalsIgnoreCase(effectsFormat.get(j))) {
                                    isSectionMatch = false;
                                    break;
                                }
                            }
                            if (isSectionMatch) {
                                sectionStartIndex = i;
                                break;
                            }
                        }
                    } else if (container.getOrDefault(plugin.hasEnchants, PersistentDataType.INTEGER, 0) == 1) {
                        int maxIndex = lore.size() - enchantsFormat.size();
                        for (int i = 0; i <= maxIndex; i++) {
                            boolean isSectionMatch = true;
                            for (int j = 0; j < enchantsFormat.size(); j++) {
                                if (!lore.get(i + j).equalsIgnoreCase(enchantsFormat.get(j))) {
                                    isSectionMatch = false;
                                    break;
                                }
                            }
                            if (isSectionMatch) {
                                sectionStartIndex = i;
                                break;
                            }
                        }
                    }
                }

                if (sectionStartIndex != -1) {
                    int sectionSize = container.getOrDefault(plugin.stringsCount, PersistentDataType.INTEGER, 0);
                    for (int i = 0; i < sectionSize; i++) {
                        lore.remove(sectionStartIndex);
                    }
                }

                int stringsCount = 0;
                if (itemMeta instanceof PotionMeta) {
                    PotionMeta potionMeta = (PotionMeta) itemMeta;
                    stringsCount += effectsFormat.size();
                    lore.addAll(effectsFormat);
                    if (potionMeta.hasCustomEffects()) {
                        for (PotionEffect potionEffect : potionMeta.getCustomEffects()) {
                            String name = effectsSection.getString(potionEffect.getType().getName());
                            String level = effectLevels.getString(String.valueOf(potionEffect.getAmplifier() + 1));
                            String duration = formatDuration(potionEffect.getDuration() / 20);
                            String format = effectFormat.replace("%type%", name).replace("%level%", level).replace("%duration%", duration);

                            lore.add(format);
                            stringsCount++;
                        }
                    } else {
                        PotionData potionData = potionMeta.getBasePotionData();

                        if (potionData.getType() == PotionType.TURTLE_MASTER) {
                            String slowName = effectsSection.getString(PotionEffectType.SLOW.getName());
                            String slowLevel = String.valueOf(potionData.isUpgraded() ? 6 : 4);
                            String slowDuration = formatDuration(getBaseEffectDuration(potionData));
                            String slowFormat = effectFormat.replace("%type%", slowName)
                                    .replace("%level%", slowLevel)
                                    .replace("%duration%", slowDuration);
                            lore.add(slowFormat);

                            String resistanceName = effectsSection.getString(PotionEffectType.DAMAGE_RESISTANCE.getName());
                            String resistanceLevel = String.valueOf(potionData.isUpgraded() ? 4 : 3);
                            String resistanceDuration = formatDuration(getBaseEffectDuration(potionData));
                            String resistanceFormat = effectFormat.replace("%type%", resistanceName)
                                    .replace("%level%", resistanceLevel)
                                    .replace("%duration%", resistanceDuration);
                            lore.add(resistanceFormat);

                            stringsCount += 2;
                        } else {
                            String name = effectsSection.getString(potionData.getType().getEffectType().getName());
                            String level = effectLevels.getString(String.valueOf(potionData.isUpgraded() ? 2 : 1));
                            String duration = formatDuration(getBaseEffectDuration(potionData));
                            String format = effectFormat.replace("%type%", name)
                                    .replace("%level%", String.valueOf(level))
                                    .replace("%duration%", String.valueOf(duration));

                            lore.add(format);
                            stringsCount++;
                        }
                    }
                }

                if (itemMeta.hasEnchants()) {
                    stringsCount += enchantsFormat.size();
                    lore.addAll(enchantsFormat);
                    for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : itemMeta.getEnchants().entrySet()) {
                        String name = enchantsSection.getString(enchantmentIntegerEntry.getKey().getName());
                        String level = enchantLevels.getString(String.valueOf(enchantmentIntegerEntry.getValue()));
                        String format = enchantFormat.replace("%enchant%", name).replace("%level%", level);

                        lore.add(format);
                        stringsCount++;
                    }
                }

                container.set(plugin.hasEffects, PersistentDataType.INTEGER, itemMeta instanceof PotionMeta ? 1 : 0);
                container.set(plugin.hasEnchants, PersistentDataType.INTEGER, itemMeta.hasEnchants() ? 1 : 0);
                container.set(plugin.stringsCount, PersistentDataType.INTEGER, stringsCount);
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
            }
        }
    }

    private int getBaseEffectDuration(PotionData potionData) {
        PotionEffectType type = potionData.getType().getEffectType();
        boolean isExtended = potionData.isExtended();
        boolean isUpgraded = potionData.isUpgraded();
        int duration = 0;

        switch (type.getName()) {
            case "NIGHT_VISION":
            case "WEAKNESS":
            case "SLOW_FALLING":
                duration = 90;
                if (isExtended) duration = 240;
                break;
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
            return ConfigManager.zeroEffectDuration;
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
