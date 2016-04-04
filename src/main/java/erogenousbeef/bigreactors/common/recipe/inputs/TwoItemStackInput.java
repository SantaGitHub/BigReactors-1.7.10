package erogenousbeef.bigreactors.common.recipe.inputs;

import erogenousbeef.bigreactors.api.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * An input, containing two different input ItemStacks.
 * @author AidanBrady
 *
 */

public class TwoItemStackInput extends MachineInput<TwoItemStackInput> {

    /** The two input ItemStack */
    public ItemStack inputStack1;
    public ItemStack inputStack2;

    public TwoItemStackInput(ItemStack itemStack1, ItemStack itemStack2)
    {
        inputStack1 = itemStack1;
        inputStack2 = itemStack2;
    }

    @Override
    public void load(NBTTagCompound nbtTags)
    {
        inputStack1 = ItemStack.loadItemStackFromNBT(nbtTags.getCompoundTag("input1"));
        inputStack2 = ItemStack.loadItemStackFromNBT(nbtTags.getCompoundTag("input2"));
    }

    public TwoItemStackInput() {}

    @Override
    public TwoItemStackInput copy()
    {
        return new TwoItemStackInput(inputStack1.copy(), inputStack2.copy());
    }

    @Override
    public boolean isValid()
    {
        return inputStack1 != null && inputStack2 !=null;
    }

    public boolean use(ItemStack[] inventory, int index, boolean deplete)
    {
        if(StackUtils.contains(inventory[index], inputStack1, inputStack2))
        {
            if(deplete)
            {
                inventory[index] = StackUtils.subtract(inventory[index], inputStack1, inputStack2);
            }
            return true;
        }
        return false;
    }

    public boolean matches(TwoItemStackInput input)
    {
        return (StackUtils.equalsWildcard(inputStack1, input.inputStack1) && input.inputStack1.stackSize >= inputStack1.stackSize) && (StackUtils.equalsWildcard(inputStack2, input.inputStack2) && input.inputStack2.stackSize >= inputStack2.stackSize);
    }

    @Override
    public int hashIngredients()
    {
        return StackUtils.hashItemStack(inputStack1) << 8| StackUtils.hashItemStack(inputStack2) << 8;
    }

    @Override
    public boolean testEquality(TwoItemStackInput other)
    {
        if(!isValid())
        {
            return !other.isValid();
        }
        return StackUtils.equalsWildcardWithNBT(inputStack1, other.inputStack1) && StackUtils.equalsWildcardWithNBT(inputStack2, other.inputStack2);
    }

    @Override
    public boolean isInstance(Object other)
    {
        return other instanceof TwoItemStackInput;
    }
}
