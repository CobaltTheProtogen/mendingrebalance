package me.cobalttheprotogen.mendingrebalance.helper;

import me.cobalttheprotogen.mendingrebalance.json.MRConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;
import java.util.Map;

public class MendingHelper {
    public static int repairPlayerItems(Player player, int amount) {
        ItemStack randomEnchantedItem = getRandomEnchantedItem(player);
        int enchantmentLevel = getEnchantmentLevel(randomEnchantedItem);

        if (randomEnchantedItem != ItemStack.EMPTY) {
            int repairPercentage = getRepairPercentage(enchantmentLevel, randomEnchantedItem);
            int maxRepairAmount = (int) (randomEnchantedItem.getMaxDamage() * (repairPercentage / 100.0));
            int currentRepairAmount = randomEnchantedItem.getMaxDamage() - randomEnchantedItem.getDamageValue();
            int remainingRepairCapacity = maxRepairAmount - currentRepairAmount;
            int repairAmount = Math.min(remainingRepairCapacity, Math.min(getMendingAmount(enchantmentLevel, amount), randomEnchantedItem.getDamageValue()));

            if (repairAmount > 0) {
                randomEnchantedItem.setDamageValue(randomEnchantedItem.getDamageValue() - repairAmount);
                int newAmount = amount - getMendingCost(repairAmount);
                return newAmount > 0 ? repairPlayerItems(player, newAmount) : 0;
            }
        }
        return amount;
    }

    public static boolean isRepairable(ItemStack stack) {
        return stack.isDamaged() && getEnchantmentLevel(stack) > 0;
    }

    public static int getEnchantmentLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, stack);
    }

    private static boolean isArmor(ItemStack item) {
        return item.getItem() instanceof ArmorItem;
    }

    public static ItemStack getRandomEnchantedItem(Player player) {
        Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomItemWith(Enchantments.MENDING, player, ItemStack::isDamaged);
        return entry != null ? entry.getValue() : ItemStack.EMPTY;
    }

    private static int getRepairPercentage(int level, ItemStack item) {
        MRConfig.ConfigData configData = MRConfig.getConfigs().get(Enchantments.MENDING);
        List<? extends Integer> levels = isArmor(item) ? configData.armorRepairPercentages() : configData.toolRepairPercentages();
        return (level >= 1 && level <= levels.size()) ? levels.get(level - 1) : 0;
    }


    private static int getMendingCost(int repairAmount) {
        return repairAmount / 2;
    }

    private static int getMendingAmount(int enchantmentLevel, int experienceAmount) {
        MRConfig.ConfigData configData = MRConfig.getConfigs().get(Enchantments.MENDING);
        List<? extends Integer> levels = configData.enchantmentRepairAmount();
        return (enchantmentLevel >= 1 && enchantmentLevel <= levels.size()) ? experienceAmount * levels.get(enchantmentLevel - 1) : 0;
    }
}

