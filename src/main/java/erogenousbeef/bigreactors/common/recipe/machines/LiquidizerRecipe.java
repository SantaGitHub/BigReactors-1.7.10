package erogenousbeef.bigreactors.common.recipe.machines;

import erogenousbeef.bigreactors.common.recipe.input.ItemFluidInput;
import erogenousbeef.bigreactors.common.recipe.output.FluidOutput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class LiquidizerRecipe extends AdvancedMachineRecipe<LiquidizerRecipe> {

    public LiquidizerRecipe(ItemFluidInput input, FluidOutput output) {
        super(input, output);
    }

    public LiquidizerRecipe(ItemStack input, FluidStack fluidStack, FluidStack output)
    {
        super(input, fluidStack, output);
    }

    @Override
    public LiquidizerRecipe copy() {
        return new LiquidizerRecipe(getInput().copy(), getOutput().copy());
    }
}
