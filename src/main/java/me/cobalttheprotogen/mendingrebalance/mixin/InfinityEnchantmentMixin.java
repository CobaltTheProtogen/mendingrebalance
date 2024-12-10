package me.cobalttheprotogen.mendingrebalance.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.enchantment.ArrowInfiniteEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArrowInfiniteEnchantment.class)
public class InfinityEnchantmentMixin {
    @ModifyReturnValue(method = "checkCompatibility", at = @At("RETURN"))
    public boolean _mr$checkCompatibility(boolean original) {
        return true; // We need this to force Infinity to be compatible with Mending.
    }
}

