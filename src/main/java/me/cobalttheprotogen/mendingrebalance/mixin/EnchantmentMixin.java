package me.cobalttheprotogen.mendingrebalance.mixin;

import me.cobalttheprotogen.mendingrebalance.json.MRConfig;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.MendingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(method = "checkCompatibility", at = @At("RETURN"), cancellable = true)
    public void _mr$checkCompatibility(Enchantment enchantment, CallbackInfoReturnable<Boolean> cir) {
        if (MRConfig.getConfigData() != null
                && (Object) this instanceof MendingEnchantment
                && MRConfig.getConfigData().values().stream()
                .anyMatch(configData -> configData.enchantment().mutuallyExclusiveWith()
                        .orElse(Collections.emptyList()).contains(enchantment.getRegistryName().toString()))) {
            cir.setReturnValue(false);
        }
    }
}