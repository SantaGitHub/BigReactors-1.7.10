package erogenousbeef.bigreactors.common.machine;

import erogenousbeef.bigreactors.common.recipe.IMachineRecipe;
import erogenousbeef.bigreactors.common.recipe.IMachineRecipe.ResultStack;
import erogenousbeef.bigreactors.common.recipe.MachineRecipeInput;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Client side stub for reporting progress.
 */
public class PoweredTaskProgress implements IPoweredTask {

    private float progress;

    public PoweredTaskProgress(IPoweredTask task) {
        progress = task.getProgress();
    }

    public PoweredTaskProgress(float progress) {
        this.progress = progress;
    }

    @Override
    public void update(float availableEnergy) {
    }

    @Override
    public boolean isComplete() {
        return getProgress() >= 1;
    }

    @Override
    public float getProgress() {
        return progress;
    }

    @Override
    public ResultStack[] getCompletedResult() {
        return new ResultStack[0];
    }

    @Override
    public float getRequiredEnergy() {
        return 0;
    }

    @Override
    public float getChance() {
        return 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtRoot) {
    }

    @Override
    public IMachineRecipe getRecipe() {
        return null;
    }

    @Override
    public MachineRecipeInput[] getInputs() {
        return new MachineRecipeInput[0];
    }
}
