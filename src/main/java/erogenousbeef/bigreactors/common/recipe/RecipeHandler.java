package erogenousbeef.bigreactors.common.recipe;

import erogenousbeef.bigreactors.api.util.StackUtils;
import erogenousbeef.bigreactors.common.recipe.inputs.AdvancedMachineFluidInput;
import erogenousbeef.bigreactors.common.recipe.inputs.FluidInput;
import erogenousbeef.bigreactors.common.recipe.inputs.ItemStackInput;
import erogenousbeef.bigreactors.common.recipe.inputs.MachineInput;
import erogenousbeef.bigreactors.common.recipe.machines.LiquidizerRecipe;
import erogenousbeef.bigreactors.common.recipe.machines.MachineRecipe;
import erogenousbeef.bigreactors.common.recipe.output.FluidOutput;
import erogenousbeef.bigreactors.common.recipe.output.MachineOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Class used to handle machine recipes. This is used for both adding and fetching recipes.
 * @author AidanBrady, unpairedbracket
 *
 */
public class RecipeHandler {

    public static final String LIQUIDIZER_NAME = "Liquidizer";

    public static void addRecipe(Recipe recipeMap, MachineRecipe recipe)
    {
        recipeMap.put(recipe);
    }

    public static void removeRecipe(Recipe recipeMap, MachineRecipe recipe)
    {
        recipeMap.remove(recipe);
    }

    /**
     * Add a Liquidizer recipe.
     * @param input1 - input ItemStack
     * @param input2 - input ItemStack
     * @param fluidInput - input FLuidStack
     * @param fluidOutput - output FluidStack
     */
    public static void addLiquidizerRecipe(ItemStack input1, ItemStack input2, FluidStack fluidInput, FluidStack fluidOutput)
    {
        addRecipe(Recipe.LIQUIDIZER, new LiquidizerRecipe(input1, input2, fluidInput, fluidOutput));
    }

    public static enum Recipe
    {
        LIQUIDIZER(LIQUIDIZER_NAME, AdvancedMachineFluidInput.class, FluidOutput.class, LiquidizerRecipe.class);

        private HashMap recipes;
        private String recipeName;

        private Class<? extends MachineInput> inputClass;
        private Class<? extends MachineOutput> outputClass;
        private Class<? extends MachineRecipe> recipeClass;

        private <INPUT extends MachineInput<INPUT>, OUTPUT extends MachineOutput<OUTPUT>, RECIPE extends MachineRecipe<INPUT, ?, RECIPE>> Recipe(String name, Class<INPUT> input, Class<OUTPUT> output, Class<RECIPE> recipe)
        {
            recipeName = name;

            inputClass = input;
            outputClass = output;
            recipeClass = recipe;

            recipes = new HashMap<INPUT, RECIPE>();
        }

        public <RECIPE extends MachineRecipe<?, ?, RECIPE>> void put(RECIPE recipe)
        {
            recipes.put(recipe.getInput(), recipe);
        }

        public <RECIPE extends MachineRecipe<?, ?, RECIPE>> void remove(RECIPE recipe)
        {
            recipes.remove(recipe.getInput());
        }

        public String getRecipeName()
        {
            return recipeName;
        }

        public <INPUT> INPUT createInput(NBTTagCompound nbtTags)
        {
            try {
                MachineInput input = inputClass.newInstance();
                input.load(nbtTags);

                return (INPUT)input;
            } catch(Exception e) {
                return null;
            }
        }

        public <RECIPE, INPUT> RECIPE createRecipe(INPUT input, NBTTagCompound nbtTags)
        {
            try {
                MachineOutput output = outputClass.newInstance();
                output.load(nbtTags);

                Constructor<? extends MachineRecipe> construct = recipeClass.getDeclaredConstructor(inputClass, outputClass);
                return (RECIPE)construct.newInstance(input, output);
            } catch(Exception e) {
                return null;
            }
        }

        public boolean containsRecipe(ItemStack input)
        {
            for(Object obj : get().entrySet())
            {
                if(obj instanceof Map.Entry)
                {
                    Map.Entry entry = (Map.Entry)obj;

                    if(entry.getKey() instanceof ItemStackInput)
                    {
                        ItemStack stack = ((ItemStackInput)entry.getKey()).ingredient;

                        if(StackUtils.equalsWildcard(stack, input))
                        {
                            return true;
                        }
                    }
                    else if(entry.getKey() instanceof FluidInput)
                    {
                        if(((FluidInput)entry.getKey()).ingredient.isFluidEqual(input))
                        {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        public boolean containsRecipe(ItemStack input1, ItemStack input2)
        {
            for(Object obj : get().entrySet())
            {
                if(obj instanceof Map.Entry)
                {
                    Map.Entry entry = (Map.Entry)obj;

                    if(entry.getKey() instanceof AdvancedMachineFluidInput)
                    {
                        ItemStack stack1 = ((AdvancedMachineFluidInput)entry.getKey()).itemInput1;
                        ItemStack stack2 = ((AdvancedMachineFluidInput)entry.getKey()).itemInput2;

                        if(StackUtils.equalsWildcard(stack1, input1) && StackUtils.equalsWildcard(stack2, input2))
                        {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        public boolean containsRecipe(Fluid input)
        {
            for(Object obj : get().entrySet())
            {
                if(obj instanceof Map.Entry)
                {
                    Map.Entry entry = (Map.Entry)obj;

                    if(entry.getKey() instanceof FluidInput)
                    {
                        if(((FluidInput)entry.getKey()).ingredient.getFluid() == input)
                        {
                            return true;
                        }
                    }

                    else if(entry.getKey() instanceof AdvancedMachineFluidInput)
                    {
                        if(((AdvancedMachineFluidInput)entry.getKey()).fluid.getFluid() == input)
                        {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        public HashMap get()
        {
            return recipes;
        }
    }
}
