package net.silentchaos512.gems.api;

import net.minecraft.item.ItemStack;
import net.silentchaos512.gems.api.recipe.altar.RecipeChaosAltar;

public class SilentGemsAPI {
    public static void addAltarRecipe(ItemStack output, ItemStack input, int chaosCost, ItemStack catalyst) {
        addAltarRecipe(new RecipeChaosAltar(output, input, chaosCost, catalyst));
    }

    public static void addAltarRecipe(ItemStack output, ItemStack input, int chaosCost) {
        addAltarRecipe(new RecipeChaosAltar(output, input, chaosCost, null));
    }

    public static void addAltarRecipe(RecipeChaosAltar recipe) {
        RecipeChaosAltar.ALL_RECIPES.add(recipe);
    }
}
