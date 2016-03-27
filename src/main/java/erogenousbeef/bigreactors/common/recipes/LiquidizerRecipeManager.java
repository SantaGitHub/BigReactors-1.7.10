package erogenousbeef.bigreactors.common.recipes;

import erogenousbeef.bigreactors.api.recipes.ILiquidizerManager;
import erogenousbeef.bigreactors.api.recipes.ILiquidizerRecipe;
import erogenousbeef.bigreactors.utils.ItemStackUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LiquidizerRecipeManager implements ILiquidizerManager {

    private static final Set<ILiquidizerRecipe> recipes = new HashSet<>();
    public static final Set<Fluid> recipeFluidInputs = new HashSet<>();
    public static final Set<Fluid> recipeFluidOutputs = new HashSet<>();

    @Override
    public void addRecipe(ItemStack resource1, ItemStack resource2, FluidStack liquid, FluidStack output) {
        ILiquidizerRecipe recipe = new LiquidizerRecipe(resource1, resource2, liquid, output.getFluid());
        addRecipe(recipe);
    }

    @Override
    public void addRecipe(ItemStack resource1, ItemStack resource2, FluidStack output) {
        addRecipe(resource1, resource2, FluidRegistry.getFluidStack("water", 1000), output);
    }

    public static ILiquidizerRecipe findMatchingRecipe(ItemStack res1, ItemStack res2, FluidStack liqu) {
        for (ILiquidizerRecipe recipe : recipes) {
            if (matches(recipe, res1, res2, liqu)) {
                return recipe;
            }
        }
        return null;
    }

    public static boolean matches(ILiquidizerRecipe recipe, ItemStack res1, ItemStack res2, FluidStack liqu) {
        ItemStack resource1 = recipe.getResourceFirst();
        ItemStack resource2 = recipe.getResourceSecond();
        if (ItemStackUtil.isCraftingEquivalent(resource1, res1)) {
            if (!ItemStackUtil.isCraftingEquivalent(resource2, res2)) {
                return false;
            }
        }

        FluidStack fluid = recipe.getFluidResource();
        return liqu != null && liqu.isFluidEqual(fluid);
    }

    public static boolean isResource(ItemStack resource1, ItemStack resource2) {
        if (resource1 == null || resource2 == null) {
            return false;
        }

        for (ILiquidizerRecipe recipe : recipes) {
            if (ItemStackUtil.isCraftingEquivalent(recipe.getResourceFirst(), resource1) && ItemStackUtil.isCraftingEquivalent(recipe.getResourceSecond(), resource2)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addRecipe(ILiquidizerRecipe recipe) {
        FluidStack liquid = recipe.getFluidResource();
        recipeFluidInputs.add(liquid.getFluid());

        Fluid output = recipe.getOutput();
        recipeFluidOutputs.add(output);

        return recipes.add(recipe);
    }

    @Override
    public boolean removeRecipe(ILiquidizerRecipe recipe) {
        FluidStack liquid = recipe.getFluidResource();
        recipeFluidInputs.remove(liquid.getFluid());

        Fluid output = recipe.getOutput();
        recipeFluidOutputs.remove(output);

        return recipes.remove(recipe);
    }

    @Override
    public Collection<ILiquidizerRecipe> recipes() {
        return Collections.unmodifiableSet(recipes);
    }
}
