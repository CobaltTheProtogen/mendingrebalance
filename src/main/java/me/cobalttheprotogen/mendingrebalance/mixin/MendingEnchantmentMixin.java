package me.cobalttheprotogen.mendingrebalance.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.cobalttheprotogen.mendingrebalance.json.MRConfig;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.MendingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MendingEnchantment.class)
public class MendingEnchantmentMixin {
    @ModifyReturnValue(method = "getMaxLevel", at = @At("RETURN"))
    public int _mr$setMaxLevel(int original) {
        MRConfig.ConfigData configData = MRConfig.getConfigs().get(Enchantments.MENDING);
        if (configData != null) {
            return configData.maximumMendingLevel();
        }
        return original;
    }
}

