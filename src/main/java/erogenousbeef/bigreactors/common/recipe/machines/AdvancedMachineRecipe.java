package erogenousbeef.bigreactors.common.recipe.machines;

import erogenousbeef.bigreactors.common.recipe.input.ItemFluidInput;
import erogenousbeef.bigreactors.common.recipe.output.FluidOutput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public abstract class AdvancedMachineRecipe <RECIPE extends AdvancedMachineRecipe<RECIPE>> extends MachineRecipe<ItemFluidInput, FluidOutput, RECIPE> {

    public AdvancedMachineRecipe(ItemFluidInput input, FluidOutput output)
    {
        super(input, output);
    }

    public AdvancedMachineRecipe(ItemStack input, FluidStack fluid, FluidStack output)
    {
        this(new ItemFluidInput(input, fluid), new FluidOutput(output));
    }

    public AdvancedMachineRecipe(ItemStack input, String fluidName, int amount, FluidStack output)
    {
        this(new ItemFluidInput(input, FluidRegistry.getFluidStack(fluidName, amount)), new FluidOutput(output));
    }

    public boolean canOperate(ItemStack[] inventory, int inputIndex, FluidTank fluidTank, FluidTank output)
    {
        return getInput().useItem(inventory, inputIndex, false) && getInput().useFluid(fluidTank, false, 1) && getOutput().applyOutputs(output, false);
    }

    public void operate(ItemStack[] inventory, int inputIndex, FluidTank fluidTank, FluidTank output)
    {
        if(getInput().useItem(inventory, inputIndex, true) && getInput().useFluid(fluidTank, true, 1))
        {
            getOutput().applyOutputs(output, true);
        }
    }
}
