package erogenousbeef.bigreactors.common.recipes;

import erogenousbeef.bigreactors.api.recipes.ILiquidizerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class LiquidizerRecipe implements ILiquidizerRecipe {

    private final ItemStack resource1;
    private final ItemStack resource2;
    private final FluidStack fluidResource;
    private final Fluid output;

    public LiquidizerRecipe(ItemStack resource1, ItemStack resource2, FluidStack fluidResource, Fluid output) {

        if (resource1 == null || resource2 == null) {
            throw new NullPointerException("Liquidizer Resource (Input) cannot be null!");
        }

        if (output == null) {
            throw new NullPointerException("Liquidizer Output cannot be null!");
        }

        if (fluidResource == null) {
            throw new NullPointerException("Liquidizer Liquid (Input) cannot be null!");
        }

        this.resource1 = resource1;
        this.resource2 = resource2;
        this.fluidResource = fluidResource;
        this.output = output;

    }

    @Override
    public ItemStack getResourceFirst() {
        return resource1;
    }

    @Override
    public ItemStack getResourceSecond() {
        return resource2;
    }

    @Override
    public FluidStack getFluidResource() {
        return fluidResource;
    }

    @Override
    public Fluid getOutput() {
        return output;
    }
}
