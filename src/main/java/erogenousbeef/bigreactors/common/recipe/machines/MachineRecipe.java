package erogenousbeef.bigreactors.common.recipe.machines;

import erogenousbeef.bigreactors.common.recipe.inputs.MachineInput;
import erogenousbeef.bigreactors.common.recipe.output.MachineOutput;

public abstract class MachineRecipe <INPUT extends MachineInput, OUTPUT extends MachineOutput, RECIPE extends MachineRecipe<INPUT, OUTPUT, RECIPE>> {

    public INPUT recipeInput;
    public OUTPUT recipeOutput;

    public MachineRecipe(INPUT input, OUTPUT output)
    {
        recipeInput = input;
        recipeOutput = output;
    }

    public INPUT getInput()
    {
        return recipeInput;
    }

    public OUTPUT getOutput()
    {
        return recipeOutput;
    }

    public abstract RECIPE copy();
}
