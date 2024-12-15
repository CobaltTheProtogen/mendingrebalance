package me.cobalttheprotogen.mendingrebalance.registry;

import me.cobalttheprotogen.mendingrebalance.MendingRebalance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, MendingRebalance.MOD_ID);

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);

        for (int level = 1; level <= 10; level++) {
            final int potionLevel = level;
            String potionName = "mending_" + potionLevel;

            POTIONS.register(potionName, () ->
                    new Potion(new MobEffectInstance(ModEffects.MENDING.get(), 12000 / potionLevel, potionLevel - 1))
            );
        }
    }
}


