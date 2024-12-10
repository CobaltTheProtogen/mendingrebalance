package me.cobalttheprotogen.mendingrebalance.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.cobalttheprotogen.mendingrebalance.helper.ClumpsMendingHelper;
import me.cobalttheprotogen.mendingrebalance.helper.MendingHelper;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbEntityMixin {
    @ModifyVariable(method = "playerTouch", at = @At("STORE"), ordinal = 0)
    private int _mr$init(int original, @Local(argsOnly = true) Player player) {
        int amount = original;

        if (!ClumpsMendingHelper.isClumpsLoaded() && amount > 0) {
            final Inventory inventory = player.getInventory();

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                final ItemStack itemStack = inventory.getItem(i);

                if (MendingHelper.isRepairable(itemStack)) {
                    amount = MendingHelper.repairPlayerItems(player, amount);
                }
            }
        }
        return amount;
    }

    @Inject(method = "repairPlayerItems", at = @At("HEAD"), cancellable = true)
    private void _mr$addOriginalExperience(Player player, int amount, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(amount);
    }
}








