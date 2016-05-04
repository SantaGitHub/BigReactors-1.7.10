package erogenousbeef.bigreactors.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public interface IRecipe {

    boolean isValid();

    int getEnergyRequired();

    RecipeOutput[] getOutputs();

    RecipeInput[] getInputs();

    List<ItemStack> getInputStacks();

    List<FluidStack> getInputFluidStacks();

    RecipeBonusType getBonusType();

    boolean isInputForRecipe(MachineRecipeInput... inputs);

    boolean isValidInput(int slotNumber, ItemStack item);

    boolean isValidInput(FluidStack fluid);

}
