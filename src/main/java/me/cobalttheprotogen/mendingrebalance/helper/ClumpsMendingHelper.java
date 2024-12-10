package me.cobalttheprotogen.mendingrebalance.helper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ClumpsMendingHelper {

    public static void registerRepairEvent() {
        if (isClumpsLoaded()) {
            MinecraftForge.EVENT_BUS.register(new ClumpsMendingHelper());
        }
    }

    public static boolean isClumpsLoaded() {
        return ModList.get().isLoaded("clumps");
    }

    @SubscribeEvent
    public static void repairDamagedMendingGears(PlayerXpEvent.PickupXp event) {
        Player player = event.getPlayer();
        Inventory inventory = player.getInventory();
        int xpValue = event.getOrb().getValue();
        int remainingXP = xpValue;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);

            if (MendingHelper.isRepairable(itemStack)) {
                remainingXP = MendingHelper.repairPlayerItems(player, remainingXP);
                if (remainingXP <= 0) {
                    break;
                }
            }
        }

        player.giveExperiencePoints(- (xpValue - remainingXP)); // Remove the used XP from the player
        event.getOrb().remove(Entity.RemovalReason.DISCARDED); // Remove the XP orb after it's picked up
    }
}

