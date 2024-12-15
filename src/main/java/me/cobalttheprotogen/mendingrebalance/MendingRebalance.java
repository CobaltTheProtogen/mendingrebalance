package me.cobalttheprotogen.mendingrebalance;

import me.cobalttheprotogen.mendingrebalance.effects.MendingEffect;
import me.cobalttheprotogen.mendingrebalance.helper.ClumpsMendingHelper;
import me.cobalttheprotogen.mendingrebalance.json.MRConfig;
import me.cobalttheprotogen.mendingrebalance.recipe.MendingBrewingRecipe;
import me.cobalttheprotogen.mendingrebalance.registry.ModEffects;
import me.cobalttheprotogen.mendingrebalance.registry.ModPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Objects;

@Mod(MendingRebalance.MOD_ID)
public class MendingRebalance {
    public static final Logger LOGGER = LogManager.getLogger(MendingRebalance.class);
    public static final String MOD_ID = "mendingrebalance";
    public static final String NAME = "Cobalt's Mending Rebalance";
    public static final String VERSION = "1.1.0";

    public MendingRebalance() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        ModPotions.register(bus); // Register the potions
        ModEffects.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ClumpsMendingHelper.registerRepairEvent();
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        registerCustomBrewingRecipes();
    }

    private void registerCustomBrewingRecipes() {
        if (MRConfig.getConfigData() == null) {
            return;
        }

        int maxMendingLevel = MRConfig.getMaxEnchantmentLevel();
        ItemStack awkwardPotion = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD);

        for (int level = 1; level <= maxMendingLevel; level++) {
            ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantmentHelper.setEnchantments(Map.of(Enchantments.MENDING, level), enchantedBook);

            ItemStack mendingPotionOutput = PotionUtils.setPotion(
                    new ItemStack(Items.POTION),
                    Objects.requireNonNull(ForgeRegistries.POTIONS.getValue(new ResourceLocation(MOD_ID, "mending_" + level)))
            );

            BrewingRecipeRegistry.addRecipe(new MendingBrewingRecipe(
                    awkwardPotion,
                    Ingredient.of(enchantedBook),
                    mendingPotionOutput
            ).setRegistryName(new ResourceLocation(MOD_ID, "mending_potion_recipe_" + level)));
        }
    }

    @SubscribeEvent
    public void onReload(AddReloadListenerEvent event) {
        event.addListener(MRConfig.getInstance());
    }
}














