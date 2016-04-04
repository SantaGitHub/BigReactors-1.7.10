package erogenousbeef.bigreactors.common.recipe.machines;

import erogenousbeef.bigreactors.common.recipe.inputs.AdvancedMachineFluidInput;
import erogenousbeef.bigreactors.common.recipe.output.FluidOutput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class LiquidizerRecipe extends AdvancedMachineRecipe<LiquidizerRecipe> {
    public LiquidizerRecipe(AdvancedMachineFluidInput input, FluidOutput output)
    {
        super(input, output);
    }

    public LiquidizerRecipe(ItemStack input1, ItemStack input2, FluidStack fluidInput, FluidStack fluidOutput) {

        super(input1, input2, fluidInput, fluidOutput);

    }

    @Override
    public LiquidizerRecipe copy()
    {
        return new LiquidizerRecipe(getInput().copy(), getOutput().copy());
    }
}
