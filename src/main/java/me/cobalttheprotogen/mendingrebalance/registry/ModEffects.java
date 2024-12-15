package me.cobalttheprotogen.mendingrebalance.registry;

import me.cobalttheprotogen.mendingrebalance.MendingRebalance;
import me.cobalttheprotogen.mendingrebalance.effects.MendingEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS
            = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MendingRebalance.MOD_ID);

    public static final RegistryObject<MobEffect> MENDING = MOB_EFFECTS.register("mending",
            () -> new MendingEffect(MobEffectCategory.BENEFICIAL, 11141290));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
