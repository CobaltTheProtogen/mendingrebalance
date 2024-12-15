package me.cobalttheprotogen.mendingrebalance.helper;

import me.cobalttheprotogen.mendingrebalance.json.MRConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class MendingHelper {

    public static int repairPlayerItems(Player player, int amount) {
        ItemStack randomEnchantedItem = getRandomEnchantedItem(player);

        if (!randomEnchantedItem.isEmpty()) {
            int enchantmentLevel = getEnchantmentLevel(randomEnchantedItem);
            int repairPercentage = getRepairCap(enchantmentLevel, randomEnchantedItem);
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

    private static int getRepairCap(int level, ItemStack item) {
        if (MRConfig.getConfigData().isEmpty()) {
            return 100; // default value
        }

        return MRConfig.getConfigData().values().stream()
                .map(configData -> configData.enchantment().level().orElse(Collections.emptyMap()))
                .map(levels -> levels.get(String.valueOf(level)))
                .filter(Objects::nonNull)
                .findFirst()
                .map(config -> isArmor(item) ? config.armorRepairCap() : config.itemRepairCap())
                .orElse(0); // default value
    }

    private static int getMendingCost(int repairAmount) {
        return repairAmount / 2;
    }

    private static int getMendingAmount(int level, int experienceAmount) {
        if (MRConfig.getConfigData().isEmpty()) {
            return 2; // default value
        }

        return MRConfig.getConfigData().values().stream()
                .map(configData -> configData.enchantment().level().orElse(Collections.emptyMap()))
                .map(levels -> levels.get(String.valueOf(level)))
                .filter(Objects::nonNull)
                .findFirst()
                .map(config -> config.repairAmount() * experienceAmount)
                .orElse(2); // default value
    }
}



