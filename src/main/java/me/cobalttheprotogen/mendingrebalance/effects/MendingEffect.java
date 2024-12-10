package me.cobalttheprotogen.mendingrebalance.effects;

import me.cobalttheprotogen.mendingrebalance.MendingRebalance;
import me.cobalttheprotogen.mendingrebalance.json.MRConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MendingEffect extends MobEffect {
    public MendingEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level.isClientSide()) {
            ItemStack randomDamagedItem = getRandomDamagedItem(pLivingEntity);
            int potionLevel = getPotionLevel(pLivingEntity);
            int potionRepairAmount = getMendingRepairAmount(pAmplifier + 1);
            boolean scaleRepairRateWithPotionLevel = MRConfig.getConfigData().getPotion().isScaleRepairRateWithAmplifier();

            if (randomDamagedItem != null) {
                int repairPercentage = getRepairPercentage(potionLevel, randomDamagedItem);
                int maxRepairAmount = (int) (randomDamagedItem.getMaxDamage() * (repairPercentage / 100.0));
                int currentRepairAmount = randomDamagedItem.getMaxDamage() - randomDamagedItem.getDamageValue();
                int remainingRepairCapacity = maxRepairAmount - currentRepairAmount;

                int repairAmountPerTick;
                if (scaleRepairRateWithPotionLevel) {
                    repairAmountPerTick = remainingRepairCapacity > 0 ?
                            (int) Math.max(remainingRepairCapacity / (getDuration(pAmplifier + 1) * 1.025), potionRepairAmount) : 0;
                } else {
                    repairAmountPerTick = remainingRepairCapacity > 0 ?
                            Math.min(remainingRepairCapacity, potionRepairAmount) : 0;
                }

                int currentDamage = randomDamagedItem.getDamageValue();
                if (currentDamage > 0) {
                    randomDamagedItem.setDamageValue(Math.max(currentDamage - repairAmountPerTick, 0));
                }
            }
        }
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration % 20 == 0;
    }

    public static int getPotionLevel(LivingEntity pLivingEntity) {
        MobEffectInstance effectInstance = pLivingEntity.getEffect(MendingRebalance.MENDING);
        return effectInstance != null ? effectInstance.getAmplifier() + 1 : 0;
    }

    private static boolean isArmor(ItemStack item) {
        return item.getItem() instanceof ArmorItem;
    }

    private static int getRepairPercentage(int level, ItemStack item) {
        return isArmor(item) ?
                MRConfig.getConfigData().getEnchantment().getLevel().get("1").getArmorRepairCap() :
                MRConfig.getConfigData().getEnchantment().getLevel().get("1").getItemRepairCap();
    }

    private static int getDuration(int amplifier) {
        return Math.max(200 / (int) Math.pow(2, amplifier + 1), 1);
    }

    public static ItemStack getRandomDamagedItem(LivingEntity pLivingEntity) {
        ItemStack[] items = {
                pLivingEntity.getItemBySlot(EquipmentSlot.HEAD),
                pLivingEntity.getItemBySlot(EquipmentSlot.CHEST),
                pLivingEntity.getItemBySlot(EquipmentSlot.LEGS),
                pLivingEntity.getItemBySlot(EquipmentSlot.FEET),
                pLivingEntity.getMainHandItem(),
                pLivingEntity.getOffhandItem()
        };

        items = Arrays.stream(items).filter(ItemStack::isDamaged).toArray(ItemStack[]::new);
        return items.length > 0 ? items[new Random().nextInt(items.length)] : null;
    }

    private static int getMendingRepairAmount(int level) {
        return MRConfig.getConfigData().getPotion().getAmplifier().get("1").getRepairAmount();
    }
}
