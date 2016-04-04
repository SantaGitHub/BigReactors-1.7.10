package erogenousbeef.bigreactors.common.interfaces;

import erogenousbeef.bigreactors.common.recipe.inputs.MachineInput;
import erogenousbeef.bigreactors.common.recipe.machines.MachineRecipe;
import erogenousbeef.bigreactors.common.recipe.output.MachineOutput;

import java.util.Map;

public interface IElectricMachine<INPUT extends MachineInput<INPUT>, OUTPUT extends MachineOutput<OUTPUT>, RECIPE extends MachineRecipe<INPUT, OUTPUT, RECIPE>>
{
    /**
     * Update call for machines. Use instead of updateEntity() - it's called every tick.
     */
    public void onUpdate();

    /**
     * Whether or not this machine can operate.
     * @return can operate
     */
    public boolean canOperate(RECIPE recipe);

    /**
     * Runs this machine's operation -- or smelts the item.
     */
    public void operate(RECIPE recipe);

    /**
     * Gets this machine's recipes.
     */
    public Map<INPUT, RECIPE> getRecipes();

    public RECIPE getRecipe();

    public INPUT getInput();
}
