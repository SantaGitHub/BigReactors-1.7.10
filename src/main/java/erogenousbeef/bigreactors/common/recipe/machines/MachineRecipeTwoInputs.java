package erogenousbeef.bigreactors.common.recipe.machines;

import erogenousbeef.bigreactors.common.recipe.inputs.MachineInput;
import erogenousbeef.bigreactors.common.recipe.output.MachineOutput;

public abstract class MachineRecipeTwoInputs<INPUT1 extends MachineInput, INPUT2 extends MachineInput, OUTPUT extends MachineOutput, RECIPE extends MachineRecipeTwoInputs<INPUT1, INPUT2, OUTPUT, RECIPE>> {

    public INPUT1 recipeInput1;
    public INPUT2 recipeInput2;
    public OUTPUT recipeOutput;

    public MachineRecipeTwoInputs(INPUT1 input1, INPUT2 input2, OUTPUT output)
    {
        recipeInput1 = input1;
        recipeInput2 = input2;
        recipeOutput = output;
    }

    public INPUT1 getInput()
    {
        return recipeInput1;
    }

    public INPUT2 getInput2()
    {
        return recipeInput2;
    }

    public OUTPUT getOutput()
    {
        return recipeOutput;
    }

    public abstract RECIPE copy();
}
