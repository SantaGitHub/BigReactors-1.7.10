package erogenousbeef.bigreactors.common.tileentity.base;

import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.recipe.RecipeHandler;
import erogenousbeef.bigreactors.common.recipe.input.ItemFluidInput;
import erogenousbeef.bigreactors.common.recipe.machines.AdvancedMachineRecipe;
import erogenousbeef.bigreactors.common.recipe.output.FluidOutput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public abstract class TileEntityAdvancedBasicMachine<RECIPE extends AdvancedMachineRecipe<RECIPE>> extends TileEntityBasicMachine<ItemFluidInput, FluidOutput, RECIPE> {

    public FluidTank fluidTank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 5);
    public FluidTank fluidTankOutput = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 5);
    public FluidStack fluidInput;

    @Override
    public void onUpdate()
    {
        super.onUpdate();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        if(slot == 0)
        {
            for(ItemFluidInput input : getRecipes().keySet())
            {
                if(input.itemStack.isItemEqual(itemstack))
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ItemFluidInput getInput()
    {
        return new ItemFluidInput(_inventories[0], fluidInput);
    }

    @Override
    public RECIPE getRecipe()
    {
        ItemFluidInput input = getInput();

        if(cachedRecipe == null || !input.testEquality(cachedRecipe.getInput()))
        {
            cachedRecipe = RecipeHandler.getRecipe(input, getRecipes());
        }

        return cachedRecipe;
    }

    @Override
    public void operate(RECIPE recipe)
    {
        recipe.operate(_inventories, 0, fluidTank, fluidTankOutput);

        markChunkDirty();
    }

    @Override
    public boolean canOperate(RECIPE recipe) {

        return recipe != null && recipe.canOperate(_inventories, 0, fluidTank, fluidTankOutput);
    }
}
