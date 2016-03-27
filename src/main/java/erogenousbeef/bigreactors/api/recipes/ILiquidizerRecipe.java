package erogenousbeef.bigreactors.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface ILiquidizerRecipe extends IBigReactorsRecipe {

    /**
     * @return ItemStack representing the input resource first slot.
     */
    ItemStack getResourceFirst();

    /**
     * @return ItemStack representing the input resource in second slot.
     */
    ItemStack getResourceSecond();

    /**
     * @return FluidStack representing the input fluid resource.
     */
    FluidStack getFluidResource();

    /**
     * @return Fluid representing output.
     */
    Fluid getOutput();

}