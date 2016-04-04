package erogenousbeef.bigreactors.common.recipe.inputs;

import erogenousbeef.bigreactors.api.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class AdvancedMachineFluidInput extends MachineInput<AdvancedMachineFluidInput> {
    public ItemStack itemInput1;
    public ItemStack itemInput2;
    public FluidStack fluid;

    public AdvancedMachineFluidInput(ItemStack input1, ItemStack input2, FluidStack fluidInput)
    {
        itemInput1 = input1;
        itemInput2 = input2;
        fluid = fluidInput;
    }

    public AdvancedMachineFluidInput() {}

    @Override
    public void load(NBTTagCompound nbtTags)
    {
        itemInput1 = ItemStack.loadItemStackFromNBT(nbtTags.getCompoundTag("input1"));
        itemInput2 = ItemStack.loadItemStackFromNBT(nbtTags.getCompoundTag("input2"));
        fluid = FluidStack.loadFluidStackFromNBT(nbtTags.getCompoundTag("fluidInput"));
    }

    @Override
    public AdvancedMachineFluidInput copy()
    {
        return new AdvancedMachineFluidInput(itemInput1.copy(), itemInput2.copy(), fluid.copy());
    }

    @Override
    public boolean isValid()
    {
        return itemInput1 != null && itemInput2 != null && fluid != null;
    }

    public boolean useItem(ItemStack[] inventory, int index, boolean deplete)
    {
        if(StackUtils.contains(inventory[index], itemInput1, itemInput2))
        {
            if(deplete)
            {
                inventory[index] = StackUtils.subtract(inventory[index], itemInput1, itemInput2);
            }

            return true;
        }

        return false;
    }

    public boolean useSecondary(FluidTank fluidTank, int amountToUse, boolean deplete)
    {
        if(fluidTank.getFluid() == fluid && fluidTank.getFluidAmount() >= amountToUse)
        {
            fluidTank.drain(amountToUse, deplete);
            return true;
        }

        return false;
    }

    public boolean matches(AdvancedMachineFluidInput input)
    {
        return (StackUtils.equalsWildcard(itemInput1, input.itemInput1) && input.itemInput1.stackSize >= itemInput1.stackSize) && (StackUtils.equalsWildcard(itemInput2, input.itemInput2) && input.itemInput2.stackSize >= itemInput2.stackSize);
    }

    @Override
    public int hashIngredients()
    {
        return StackUtils.hashItemStack(itemInput1) << 8 | StackUtils.hashItemStack(itemInput2) << 8 | fluid.getFluidID();
    }

    @Override
    public boolean testEquality(AdvancedMachineFluidInput other)
    {
        if(!isValid())
        {
            return !other.isValid();
        }
        return (StackUtils.equalsWildcardWithNBT(itemInput1, other.itemInput1) && StackUtils.equalsWildcardWithNBT(itemInput2, other.itemInput2)) && fluid.getFluidID() == other.fluid.getFluidID();
    }

    @Override
    public boolean isInstance(Object other)
    {
        return other instanceof AdvancedMachineFluidInput;
    }
}
