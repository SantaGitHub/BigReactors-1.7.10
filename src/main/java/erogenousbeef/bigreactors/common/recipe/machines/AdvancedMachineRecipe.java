package erogenousbeef.bigreactors.common.recipe.machines;

import erogenousbeef.bigreactors.common.recipe.inputs.AdvancedMachineFluidInput;
import erogenousbeef.bigreactors.common.recipe.output.FluidOutput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public abstract class AdvancedMachineRecipe <RECIPE extends AdvancedMachineRecipe<RECIPE>> extends MachineRecipe<AdvancedMachineFluidInput, FluidOutput, RECIPE>
{
    public AdvancedMachineRecipe(AdvancedMachineFluidInput input, FluidOutput output)
    {
        super(input, output);
    }

    public AdvancedMachineRecipe(ItemStack input1, ItemStack input2, FluidStack fluidInput, FluidStack output)
    {
        this(new AdvancedMachineFluidInput(input1, input2, fluidInput), new FluidOutput(output));
    }

    public AdvancedMachineRecipe(ItemStack input1, ItemStack input2, String fluidName, int inputFluidAmount, FluidStack output)
    {
        this(new AdvancedMachineFluidInput(input1, input2, FluidRegistry.getFluidStack(fluidName, inputFluidAmount)), new FluidOutput(output));
    }

    public boolean canOperate(ItemStack[] inventory, int inputIndex, FluidTank fluidTank, int amount, FluidTank outputTank)
    {
        return getInput().useItem(inventory, inputIndex, false) && getInput().useSecondary(fluidTank, amount, false) && getOutput().applyOutputs(outputTank, false);
    }

    public void operate(ItemStack[] inventory, int inputIndex, FluidTank fluidTank, int needed, FluidTank outputTank)
    {
        if(getInput().useItem(inventory, inputIndex, true) && getInput().useSecondary(fluidTank, needed, true))
        {
            getOutput().applyOutputs(outputTank, true);
        }

    }
}
