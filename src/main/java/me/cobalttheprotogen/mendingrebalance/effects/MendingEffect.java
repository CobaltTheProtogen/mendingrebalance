package me.cobalttheprotogen.mendingrebalance.effects;

import me.cobalttheprotogen.mendingrebalance.json.MRConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MendingEffect extends MobEffect {
    public MendingEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level.isClientSide()) {
            int potionRepairAmount = getMendingRepairAmount(pAmplifier);
            ItemStack randomDamagedItem = getRandomDamagedItem(pLivingEntity);
            if (randomDamagedItem != null) {
                int repairPercentage = getRepairPercentage(pAmplifier, randomDamagedItem);
                int maxRepairAmount = (int) (randomDamagedItem.getMaxDamage() * (repairPercentage / 100.0));
                int currentRepairAmount = randomDamagedItem.getMaxDamage() - randomDamagedItem.getDamageValue();
                int remainingRepairCapacity = maxRepairAmount - currentRepairAmount;

                int newAmount = remainingRepairCapacity > 0 ?
                                Math.min(remainingRepairCapacity, potionRepairAmount) : 0;

                int currentDamage = randomDamagedItem.getDamageValue();
                if (currentDamage > 0) {
                        randomDamagedItem.setDamageValue(Math.max(currentDamage - newAmount, 0));
                    }
                }
            }
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration % getDuration(pAmplifier) == 0;
    }

    private static boolean isArmor(ItemStack item) {
        return item.getItem() instanceof ArmorItem;
    }

    private static int getRepairPercentage(int level, ItemStack item) {
        if (MRConfig.getConfigData().isEmpty()) {
            return 100; // default value
        }
        return getConfigValue(level, isArmor(item));
    }

    private static int getConfigValue(int level, boolean isArmor) {
        return MRConfig.getConfigData().values().stream()
                .map(configData -> configData.potion().amplifier().orElse(Collections.emptyMap()))
                .map(amplifier -> amplifier.get(String.valueOf(level)))
                .filter(Objects::nonNull)
                .findFirst()
                .map(config -> isArmor ? config.armorRepairCap() : config.itemRepairCap())
                .orElse(100); // default value
    }

    private static int getDuration(int pAmplifier) {
        return MRConfig.getConfigData().values().stream()
                .map(configData -> configData.potion().amplifier().orElse(Collections.emptyMap()))
                .map(amplifier -> amplifier.get(String.valueOf(pAmplifier)))
                .filter(Objects::nonNull)
                .findFirst()
                .map(config -> Math.max((int) (20 / config.repairMultiplier().orElse(1.0)), 1))
                .orElse(1); // Ensure the duration is at least 1
    }

    public static ItemStack getRandomDamagedItem(LivingEntity pLivingEntity) {
        List<ItemStack> items = Arrays.asList(
                pLivingEntity.getItemBySlot(EquipmentSlot.HEAD),
                pLivingEntity.getItemBySlot(EquipmentSlot.CHEST),
                pLivingEntity.getItemBySlot(EquipmentSlot.LEGS),
                pLivingEntity.getItemBySlot(EquipmentSlot.FEET),
                pLivingEntity.getMainHandItem(),
                pLivingEntity.getOffhandItem()
        );

        items = items.stream().filter(ItemStack::isDamaged).toList();
        return items.isEmpty() ? null : items.get(new Random().nextInt(items.size()));
    }

    private static int getMendingRepairAmount(int level) {
        if (MRConfig.getConfigData().isEmpty()) {
            return 2; // default value
        }

        return MRConfig.getConfigData().values().stream()
                .map(configData -> configData.potion().amplifier().orElse(Collections.emptyMap()))
                .map(amplifier -> amplifier.get(String.valueOf(level)))
                .filter(Objects::nonNull)
                .findFirst()
                .map(MRConfig.PotionLevelConfig::repairAmount)
                .orElse(2); // Default repair amount if not found
    }
}


