package erogenousbeef.bigreactors.common.tileentity.liquidizer;

import erogenousbeef.bigreactors.ModObject;
import erogenousbeef.bigreactors.common.recipe.AbstractMachineRecipe;
import erogenousbeef.bigreactors.common.recipe.IRecipe;
import erogenousbeef.bigreactors.common.recipe.MachineRecipeInput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class LiquidizerMachineRecipe extends AbstractMachineRecipe {

    @Override
    public String getUid() {
        return "LiquidizerRecipe";
    }

    @Override
    public IRecipe getRecipeForInputs(MachineRecipeInput[] inputs) {
        return LiquidizerRecipeManager.instance.getRecipeForInput(inputs);
    }

    @Override
    public boolean isValidInput(MachineRecipeInput input) {
        if(input == null) {
            return false;
        }
        return LiquidizerRecipeManager.instance.isValidInput(input);
    }

    @Override
    public String getMachineName() {
        return ModObject.blockLiquidizer.unlocalisedName;
    }

    @Override
    public List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput[] inputs) {

        List<MachineRecipeInput> result = new ArrayList<MachineRecipeInput>();

        LiquidizerRecipe rec = (LiquidizerRecipe) getRecipeForInputs(inputs);
        FluidStack inputFluidStack = rec.getRequiredFluidInput(inputs);
        result.add(new MachineRecipeInput(0, inputFluidStack));

        for (MachineRecipeInput ri : inputs) {
            if(!ri.isFluid() && ri.item != null) {
                ItemStack st = ri.item.copy();
                st.stackSize = rec.getNumConsumed(ri.item);
                result.add(new MachineRecipeInput(ri.slotNumber, st));
            }
        }
        return result;

    }

    @Override
    public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
        if(inputs == null || inputs.length <= 0) {
            return new ResultStack[0];
        }
        LiquidizerRecipe recipe = (LiquidizerRecipe) getRecipeForInputs(inputs);
        if(recipe == null || !recipe.isValid()) {
            return new ResultStack[0];
        }
        return new ResultStack[] { new ResultStack(recipe.getFluidOutput(inputs)) };
    }
}
