package me.cobalttheprotogen.mendingrebalance.mixin;

import me.cobalttheprotogen.mendingrebalance.json.MRConfig;
import net.minecraft.core.Registry;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.MendingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(method = "checkCompatibility", at = @At("RETURN"), cancellable = true)
    public void _mr$checkCompatibility(Enchantment enchantment, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof MendingEnchantment) {
            cir.setReturnValue(false);
        }
    }
}

