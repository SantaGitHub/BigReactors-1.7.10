package erogenousbeef.bigreactors.common.recipe.output;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class FluidOutput extends MachineOutput<FluidOutput> {

    public FluidStack output;

    public FluidOutput(FluidStack fluidStack)
    {
        output = fluidStack;
    }

    public FluidOutput() {}

    @Override
    public FluidOutput copy() {
        return new FluidOutput(output.copy());
    }

    @Override
    public void load(NBTTagCompound nbtTags) {

        output = FluidStack.loadFluidStackFromNBT(nbtTags.getCompoundTag("output"));
    }

    public boolean applyOutputs(FluidTank fluidTank, boolean doEmit)
    {
        if(fluidTank.fill(output, false) > 0)
        {
            fluidTank.fill(output, doEmit);

            return true;
        }

        return false;
    }
}
