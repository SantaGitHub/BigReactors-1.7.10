package erogenousbeef.bigreactors.common.machine;

import erogenousbeef.bigreactors.common.recipe.IMachineRecipe;
import erogenousbeef.bigreactors.common.recipe.IMachineRecipe.ResultStack;
import erogenousbeef.bigreactors.common.recipe.MachineRecipeInput;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public interface IPoweredTask {

    void update(float availableEnergy);

    boolean isComplete();

    float getProgress();

    ResultStack[] getCompletedResult();

    float getRequiredEnergy();

    float getChance();

    void writeToNBT(NBTTagCompound nbtRoot);

    @Nullable
    IMachineRecipe getRecipe();

    public abstract MachineRecipeInput[] getInputs();

}
