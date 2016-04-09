package erogenousbeef.bigreactors.common.recipe.input;

import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.utils.ItemStackUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class ItemFluidInput extends MachineInput<ItemFluidInput> {

    public ItemStack itemStack;

    public FluidStack fluidStack;

    public ItemFluidInput(ItemStack item, FluidStack fluid) {

        itemStack = item;
        fluidStack = fluid;

        BRLog.info("input: "+isValid());
    }

    public ItemFluidInput() {}

    @Override
    public boolean isValid() {
        return itemStack != null && fluidStack != null;
    }

    @Override
    public ItemFluidInput copy() {
        return new ItemFluidInput(itemStack.copy(), fluidStack.copy());
    }

    public boolean useItem(ItemStack[] inventory, int index, boolean deplete)
    {
        if(ItemStackUtil.contains(inventory[index], itemStack))
        {
            if(deplete)
            {
                inventory[index] = ItemStackUtil.subtract(inventory[index], itemStack);
            }

            return true;
        }

        return false;
    }

    public boolean useFluid(FluidTank fluidTank, boolean deplete, int scale)
    {
        if(fluidTank.getFluid() != null && fluidTank.getFluid().containsFluid(fluidStack))
        {
            fluidTank.drain(fluidStack.amount*scale, deplete);
            return true;
        }

        return false;
    }

    public boolean matches(ItemFluidInput input)
    {
        return ItemStackUtil.isCraftingEquivalent(itemStack, input.itemStack) && input.itemStack.stackSize >= itemStack.stackSize;
    }

    @Override
    public int hashIngredients() {
        return ItemStackUtil.hashItemStack(itemStack) << 8 | fluidStack.getFluidID();
    }

    @Override
    public void load(NBTTagCompound nbtTags) {

        itemStack = ItemStack.loadItemStackFromNBT(nbtTags.getCompoundTag("inputItem"));
        fluidStack = FluidStack.loadFluidStackFromNBT(nbtTags.getCompoundTag("inputFluid"));
    }

    @Override
    public boolean testEquality(ItemFluidInput other) {

        if(!isValid()) {

            return !other.isValid();
        }

        return ItemStackUtil.isCraftingEquivalent(itemStack, other.itemStack) && fluidStack.equals(other.fluidStack);
    }

    @Override
    public boolean isInstance(Object other) {

        return other instanceof ItemFluidInput;
    }
}
