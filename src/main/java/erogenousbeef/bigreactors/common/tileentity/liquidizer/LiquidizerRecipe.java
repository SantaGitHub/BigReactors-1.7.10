package erogenousbeef.bigreactors.common.tileentity.liquidizer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.recipe.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

public class LiquidizerRecipe implements IRecipe {

    protected final List<ItemStack> inputStacks;
    protected final boolean valid;

    protected final Table<RecipeInput, RecipeInput, FluidStack> inputFluidStacks = HashBasedTable.create();
    protected final Table<RecipeInput, RecipeInput, FluidStack> outputFluidStacks = HashBasedTable.create();

    protected final RecipeInput[] inputs;
    protected final RecipeOutput[] output;
    protected final int energyRequired;
    private int requiredItems;

    public LiquidizerRecipe(IRecipe recipe) {

        FluidStack inputFluidStack = null, outputFluidStack = null;

        inputs = recipe.getInputs();

        for (RecipeOutput output : recipe.getOutputs()) {
            if(output.isFluid()) {
                outputFluidStack = output.getFluidOutput().copy();
                break;
            }
        }

        if (outputFluidStack == null) {
            BRLog.warning("Ignoring invalid Liquidizer recipe without output fluid");
        } else {

            requiredItems = 1;
            for (RecipeInput ri : inputs) {
                if (!ri.isFluid() && ri.getSlotNumber() == 1) {
                    requiredItems = 2;
                }
            }

            if (requiredItems == 2) {
                for (RecipeInput r0 : inputs) {
                    if (!r0.isFluid() && r0.getSlotNumber() == 0) {
                        for (RecipeInput r1 : inputs) {
                            if (!r1.isFluid() && r1.getSlotNumber() == 1) {
                                for (RecipeInput r2 : inputs) {
                                    if (r2.isFluid()) {
                                        float im = r0.getMulitplier() * r1.getMulitplier();
                                        inputFluidStack = r2.getFluidInput().copy();
                                        inputFluidStack.amount = Math.round(FluidContainerRegistry.BUCKET_VOLUME * im);
                                        outputFluidStack.amount = Math.round(im * r2.getMulitplier() * FluidContainerRegistry.BUCKET_VOLUME);
                                        inputFluidStacks.put(r0, r1, inputFluidStack.copy());
                                        outputFluidStacks.put(r0, r1, outputFluidStack.copy());
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                for (RecipeInput r0 : inputs) {
                    if (!r0.isFluid()) {
                        for (RecipeInput r2 : inputs) {
                            if (r2.isFluid()) {
                                float im = r0.getMulitplier();
                                inputFluidStack = r2.getFluidInput().copy();
                                inputFluidStack.amount = Math.round(FluidContainerRegistry.BUCKET_VOLUME * im);
                                outputFluidStack.amount = Math.round(im * r2.getMulitplier() * FluidContainerRegistry.BUCKET_VOLUME);
                                inputFluidStacks.put(r0, r0, inputFluidStack.copy());
                                outputFluidStacks.put(r0, r0, outputFluidStack.copy());
                            }
                        }
                    }
                }
            }

            if (inputFluidStack == null) {
                BRLog.warning("Ignoring invalid Liquidizer recipe without input fluid");
            }
        }

        output = new RecipeOutput[] { new RecipeOutput(outputFluidStack) };
        energyRequired = recipe.getEnergyRequired();

        this.inputStacks = recipe.getInputStacks();
        valid = inputFluidStack != null && inputStacks != null && !inputStacks.isEmpty() && inputStacks.size() > 0
                && outputFluidStack != null;

    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public int getEnergyRequired() {
        return energyRequired;
    }

    @Override
    public RecipeOutput[] getOutputs() {
        return output;
    }

    @Override
    public RecipeInput[] getInputs() {
        return inputs;
    }

    @Override
    public List<ItemStack> getInputStacks() {
        return inputStacks;
    }

    @Override
    public RecipeBonusType getBonusType() {
        return RecipeBonusType.NONE;
    }

    private RecipeInput getRecipeInput(int slot, ItemStack item) {
        if(item == null) {
            return null;
        }
        for (RecipeInput ri : inputs) {
            if (ri.getSlotNumber() == slot && ri.isInput(item)) {
                return ri;
            }
        }
        return null;
    }

    @Override
    public boolean isValidInput(int slot, ItemStack item) {
        return getRecipeInput(slot, item) != null;
    }

    @Override
    public boolean isValidInput(FluidStack item) {
        if(item == null) {
            return false;
        }
        for (RecipeInput ri : inputs) {
            if(item.getFluid() != null && item.isFluidEqual(ri.getFluidInput())) {
                return true;
            }
        }
        return false;
    }

    private static class RecipeMatch {
        RecipeInput r0, r1;
        FluidStack in, out;

        void setRecipeInput(RecipeInput r) {
            if (r != null) {
                if (r.getSlotNumber() == 0) {
                    r0 = r;
                } else {
                    r1 = r;
                }
            }
        }
    }

    private RecipeMatch matchRecipe(MachineRecipeInput... inputs) {
        if (!isValid() || inputs == null || inputs.length < requiredItems + 1) {
            return null;
        }
        FluidStack inputFluid = null;
        RecipeMatch m = new RecipeMatch();
        for (MachineRecipeInput in : inputs) {
            if(in.isFluid()) {
                inputFluid = in.fluid;
            } else {
                m.setRecipeInput(getRecipeInput(in.slotNumber, in.item));
            }
        }
        if (requiredItems == 1) {
            m.r1 = m.r0;
        }
        m.in = inputFluidStacks.get(m.r0, m.r1);
        m.out = outputFluidStacks.get(m.r0, m.r1);
        if (inputFluid != null && inputFluid.containsFluid(m.in)) {
            return m;
        } else {
            return null;
        }
    }

    @Override
    public boolean isInputForRecipe(MachineRecipeInput... inputs) {
        RecipeMatch m = matchRecipe(inputs);
        return m != null;
    }

    @Override
    public List<FluidStack> getInputFluidStacks() {
        throw new NotImplementedException("");
    }

    public float getMultiplierForInput(FluidStack item) {
        for (RecipeInput input : inputs) {
            if(input.isInput(item)) {
                return input.getMulitplier();
            }
        }
        return 1;
    }

    public FluidStack getRequiredFluidInput(MachineRecipeInput[] inputs) {
        RecipeMatch m = matchRecipe(inputs);
        if (m != null) {
            return m.in;
        } else {
            // inputs are no valid recipe.
            return new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME * 999);
        }
    }

    public FluidStack getFluidOutput(MachineRecipeInput... inputs) {
        RecipeMatch m = matchRecipe(inputs);
        if (m != null) {
            return m.out;
        } else {
            // inputs are no valid recipe.
            return new FluidStack(FluidRegistry.WATER, 0);
        }
    }

    public int getNumConsumed(ItemStack item) {
        for (RecipeInput input : inputs) {
            if(input.isInput(item)) {
                return input.getInput().stackSize;
            }
        }
        return 1;
    }
}
