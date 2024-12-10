package me.cobalttheprotogen.mendingrebalance.recipe;

import me.cobalttheprotogen.mendingrebalance.MendingRebalance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class MendingBrewingRecipe extends BrewingRecipe implements IForgeRegistryEntry<MendingBrewingRecipe> {

    private final ItemStack inputPotion;
    private ResourceLocation registryName;

    public MendingBrewingRecipe(ItemStack input, Ingredient ingredient, ItemStack output) {
        super(Ingredient.of(input), ingredient, output);
        this.inputPotion = input;
    }

    @Override
    public boolean isInput(ItemStack input) {
        return inputPotion.getItem() == input.getItem() && PotionUtils.getPotion(input) == PotionUtils.getPotion(inputPotion);
    }

    @Override
    public boolean isIngredient(ItemStack ingredientStack) {
        return EnchantmentHelper.getEnchantments(ingredientStack).containsKey(Enchantments.MENDING);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (isInput(input) && isIngredient(ingredient)) {
            int mendingLevel = EnchantmentHelper.getEnchantments(ingredient).getOrDefault(Enchantments.MENDING, 0);
            Potion mendingPotion = getMendingPotion(mendingLevel);
            return PotionUtils.setPotion(new ItemStack(Items.POTION), mendingPotion);
        }
        return ItemStack.EMPTY;
    }

    private Potion getMendingPotion(int mendingLevel) {
        ResourceLocation potionKey = new ResourceLocation(MendingRebalance.MOD_ID, "mending_" + mendingLevel);
        return ForgeRegistries.POTIONS.getValue(potionKey);
    }

    @Override
    public MendingBrewingRecipe setRegistryName(ResourceLocation name) {
        this.registryName = name;
        return this;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    @Override
    public Class<MendingBrewingRecipe> getRegistryType() {
        return MendingBrewingRecipe.class;
    }
}
