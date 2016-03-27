package erogenousbeef.bigreactors.common.interfaces;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public interface IRecipeFluid {

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    boolean matches(InventoryCrafting p_77569_1_, World p_77569_2_);

    /**
     * Returns an Item that is the result of this recipe
     */
    ItemStack getCraftingResult(InventoryCrafting p_77572_1_);

    /**
     * Returns the size of the recipe area
     */
    int getRecipeSize();

    FluidStack getRecipeOutput();
}
