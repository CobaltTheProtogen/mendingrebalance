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

public class MRConfig {
    private static final Gson GSON = new Gson();
    private static final String CONFIG_FILE = "mending_config.json"; // Correct path depends on mod loader
    private static final MRConfig INSTANCE = new MRConfig();
    private static final HashMap<Enchantment, ConfigData> configs = new HashMap<>();

    public record ConfigData(
            List<Integer> armorRepairPercentages,
            List<Integer> toolRepairPercentages,
            List<Integer> potionArmorRepairPercentages,
            List<Integer> potionToolRepairPercentages,
            List<Integer> enchantmentRepairAmount,
            List<Integer> potionRepairAmount,
            int maximumMendingLevel,
            boolean scaleRepairRateWithPotionLevel,
            List<String> mutuallyExclusiveEnchantments) {
    }

    public static void loadConfig(ResourceManager resourceManager) {
        try {
            Resource resource = resourceManager.getResource(new ResourceLocation(MendingRebalance.MOD_ID, CONFIG_FILE));
            if (resource != null && resource.getInputStream() != null) {
                try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    ConfigData configData = GSON.fromJson(jsonElement, ConfigData.class);
                    INSTANCE.addToConfigs(Enchantments.MENDING, configData);

                    // Debug logs
                    MendingRebalance.LOGGER.info("Loaded config data: {}", GSON.toJson(configData));
                    MendingRebalance.LOGGER.info("Set maximum mending level to: {}", configData.maximumMendingLevel());
                }
            } else {
                MendingRebalance.LOGGER.error("Config resource is null: {}", CONFIG_FILE);
            }
        } catch (Exception e) {
            MendingRebalance.LOGGER.error("Couldn't load Mending Rebalance config", e);
        }
    }

    private void addToConfigs(Enchantment enchantment, ConfigData data) {
        configs.put(enchantment, data);
    }

    public static HashMap<Enchantment, ConfigData> getConfigs() {
        return configs;
    }

    public static MRConfig getInstance() {
        return INSTANCE;
    }
}
