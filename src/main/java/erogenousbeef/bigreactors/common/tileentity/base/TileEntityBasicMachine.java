package erogenousbeef.bigreactors.common.tileentity.base;

import erogenousbeef.bigreactors.common.interfaces.IElectricMachine;
import erogenousbeef.bigreactors.common.recipe.input.MachineInput;
import erogenousbeef.bigreactors.common.recipe.machines.MachineRecipe;
import erogenousbeef.bigreactors.common.recipe.output.MachineOutput;

public abstract class TileEntityBasicMachine<INPUT extends MachineInput<INPUT>, OUTPUT extends MachineOutput<OUTPUT>, RECIPE extends MachineRecipe<INPUT, OUTPUT, RECIPE>> extends TileEntityPoweredInventoryFluid implements IElectricMachine<INPUT, OUTPUT, RECIPE> {

    public RECIPE cachedRecipe = null;

    @Override
    public void onUpdate() {

    }

}
