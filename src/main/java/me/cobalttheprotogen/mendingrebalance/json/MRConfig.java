package me.cobalttheprotogen.mendingrebalance.json;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import me.cobalttheprotogen.mendingrebalance.MendingRebalance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Optional;

public class MRConfig extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();
    private static final String DIRECTORY = "config";
    private static final MRConfig INSTANCE = new MRConfig();
    private static final HashMap<String, ConfigData> config = new HashMap<>();

    public MRConfig() {
        super(GSON, DIRECTORY);
    }

    public static int getMaxEnchantmentLevel() {
        return config.values().stream()
                .flatMap(configData -> configData.enchantment().level().orElse(Map.of()).keySet().stream())
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(1);
    }

    public static int getMaxPotionAmplifier() {
        return config.values().stream()
                .flatMap(configData -> configData.potion().amplifier().orElse(Map.of()).keySet().stream())
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
    }

    public static MRConfig getInstance() {
        return INSTANCE;
    }

    private void addToConfig(ConfigData data) {
        config.put(String.valueOf(data.enchantment()), data);
    }

    public static HashMap<String, ConfigData> getConfigData() {
        return config;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager, ProfilerFiller profiler) {
        config.clear();
        resources.forEach((resourceLocation, jsonElement) -> {
            try {
                DataResult<ConfigData> dataResult = ConfigData.CODEC.parse(JsonOps.INSTANCE, jsonElement);
                dataResult.resultOrPartial(result -> System.out.println("Parsing config data for " + resourceLocation + " with result: " + result)).ifPresent(this::addToConfig);
            } catch (Exception e) {
                MendingRebalance.LOGGER.error("Couldn't parse config data file {}", resourceLocation, e);
            }
        });
    }

    public record ConfigData(
            EnchantmentConfig enchantment,
            PotionConfig potion) {

        public static final Codec<ConfigData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EnchantmentConfig.CODEC.fieldOf("enchantment").forGetter(ConfigData::enchantment),
                PotionConfig.CODEC.fieldOf("potion").forGetter(ConfigData::potion)
        ).apply(instance, ConfigData::new));
    }

    public record EnchantmentConfig(Optional<Map<String, EnchantmentLevelConfig>> level,
                                    Optional<List<String>> mutuallyExclusiveWith,
                                    Optional<List<String>> itemBlacklist) {
        public static final Codec<EnchantmentConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Codec.STRING, EnchantmentLevelConfig.CODEC).optionalFieldOf("level").forGetter(EnchantmentConfig::level),
                Codec.list(Codec.STRING).optionalFieldOf("mutuallyExclusiveWith").forGetter(EnchantmentConfig::mutuallyExclusiveWith),
                Codec.list(Codec.STRING).optionalFieldOf("itemBlacklist").forGetter(EnchantmentConfig::itemBlacklist)
        ).apply(instance, EnchantmentConfig::new));
    }

    public record EnchantmentLevelConfig(Integer armorRepairCap,
                                         Integer itemRepairCap,
                                         Integer repairAmount) {
        public static final Codec<EnchantmentLevelConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("armorRepairCap").forGetter(EnchantmentLevelConfig::armorRepairCap),
                Codec.INT.fieldOf("itemRepairCap").forGetter(EnchantmentLevelConfig::itemRepairCap),
                Codec.INT.fieldOf("repairAmount").forGetter(EnchantmentLevelConfig::repairAmount)
        ).apply(instance, EnchantmentLevelConfig::new));
    }

    public record PotionConfig(Optional<Map<String, PotionLevelConfig>> amplifier,
                               Optional<Boolean> createRecipes) {

        public static final Codec<PotionConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Codec.STRING, PotionLevelConfig.CODEC).optionalFieldOf("amplifier").forGetter(PotionConfig::amplifier),
                Codec.BOOL.optionalFieldOf("createRecipes").forGetter(PotionConfig::createRecipes)
        ).apply(instance, PotionConfig::new));
    }

    public record PotionLevelConfig(int armorRepairCap,
                                    int itemRepairCap,
                                    int repairAmount,
                                    Optional<Double> repairMultiplier,
                                    Optional<Boolean> createRecipe) {

        public static final Codec<PotionLevelConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("armorRepairCap").forGetter(PotionLevelConfig::armorRepairCap),
                Codec.INT.fieldOf("itemRepairCap").forGetter(PotionLevelConfig::itemRepairCap),
                Codec.INT.fieldOf("repairAmount").forGetter(PotionLevelConfig::repairAmount),
                Codec.DOUBLE.optionalFieldOf("repairMultiplier").forGetter(PotionLevelConfig::repairMultiplier),
                Codec.BOOL.optionalFieldOf("createRecipe").forGetter(PotionLevelConfig::createRecipe)
        ).apply(instance, PotionLevelConfig::new));
    }
}



