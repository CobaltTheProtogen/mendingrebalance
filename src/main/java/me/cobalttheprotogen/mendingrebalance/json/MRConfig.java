package me.cobalttheprotogen.mendingrebalance.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.cobalttheprotogen.mendingrebalance.MendingRebalance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MRConfig {
    private static final Gson GSON = new Gson();
    private static final String CONFIG_FILE = "mending_config.json"; // Correct path depends on mod loader
    private static final MRConfig INSTANCE = new MRConfig();
    private static final HashMap<Enchantment, ConfigData> configs = new HashMap<>();
    private static ConfigData configDataInstance;

    public static class ConfigData {
        private EnchantmentConfig enchantment;
        private PotionConfig potion;

        public static class EnchantmentConfig {
            private Map<String, EnchantmentLevelConfig> level;
            private List<String> mutuallyExclusiveWith;
            private List<String> itemBlacklist;

            // Getters
            public Map<String, EnchantmentLevelConfig> getLevel() {
                return level;
            }

            public List<String> getMutuallyExclusiveWith() {
                return mutuallyExclusiveWith;
            }

            public List<String> getItemBlacklist() {
                return itemBlacklist;
            }

            public int getMaxEnchantmentLevel() {
                return level.keySet().stream()
                        .mapToInt(Integer::parseInt)
                        .max()
                        .orElse(0);
            }

        }

        public static class EnchantmentLevelConfig {
            private int armorRepairCap;
            private int itemRepairCap;
            private int repairAmount;

            // Getters
            public int getArmorRepairCap() {
                return armorRepairCap;
            }

            public int getItemRepairCap() {
                return itemRepairCap;
            }

            public int getRepairAmount() {
                return repairAmount;
            }
        }

        public static class PotionConfig {
            private Map<String, PotionLevelConfig> amplifier;
            private boolean potionConsumesExperience;
            private boolean scaleRepairRateWithAmplifier;

            // Getters
            public Map<String, PotionLevelConfig> getAmplifier() {
                return amplifier;
            }

            public boolean isPotionConsumesExperience() {
                return potionConsumesExperience;
            }

            public boolean isScaleRepairRateWithAmplifier() {
                return scaleRepairRateWithAmplifier;
            }
        }

        public static class PotionLevelConfig {
            private int armorRepairCap;
            private int itemRepairCap;
            private int repairAmount;

            // Getters
            public int getArmorRepairCap() {
                return armorRepairCap;
            }

            public int getItemRepairCap() {
                return itemRepairCap;
            }

            public int getRepairAmount() {
                return repairAmount;
            }
        }

        // Getters for the root fields
        public EnchantmentConfig getEnchantment() {
            return enchantment;
        }

        public PotionConfig getPotion() {
            return potion;
        }
    }

    public static void loadConfig(ResourceManager resourceManager) {
        try {
            Resource resource = resourceManager.getResource(new ResourceLocation(MendingRebalance.MOD_ID, CONFIG_FILE));
            try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                configDataInstance = GSON.fromJson(jsonElement, ConfigData.class);

                // Debug logs
                MendingRebalance.LOGGER.info("Loaded config data: {}", GSON.toJson(configDataInstance));
                MendingRebalance.LOGGER.info("Set maximum mending level to: {}", configDataInstance.getEnchantment().getMaxEnchantmentLevel());
            }
        } catch (Exception e) {
            MendingRebalance.LOGGER.error("Couldn't load Mending Rebalance config", e);
        }
    }

    public static ConfigData getConfigData() {
        return configDataInstance;
    }

    public static MRConfig getInstance() {
        return INSTANCE;
    }
}
