package erogenousbeef.bigreactors.common.recipe;

import erogenousbeef.bigreactors.common.recipe.input.ItemFluidInput;
import erogenousbeef.bigreactors.common.recipe.input.MachineInput;
import erogenousbeef.bigreactors.common.recipe.machines.AdvancedMachineRecipe;
import erogenousbeef.bigreactors.common.recipe.machines.LiquidizerRecipe;
import erogenousbeef.bigreactors.common.recipe.machines.MachineRecipe;
import erogenousbeef.bigreactors.common.recipe.output.FluidOutput;
import erogenousbeef.bigreactors.common.recipe.output.MachineOutput;
import erogenousbeef.bigreactors.utils.ItemStackUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public final class RecipeHandler {

    public static final String LIQUIDIZER = "Liquidizer";

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
     * @param input - input ItemsStack
     * @param inputFluid - input FluidStack
     * @param output - output FluidStack
     */
    public static void addLiquidizerRecipe(ItemStack input, FluidStack inputFluid, FluidStack output)
    {
        addRecipe(Recipe.LIQUIDIZER_RECIPE, new LiquidizerRecipe(input, inputFluid, output));
    }

    /**
     * Gets the AdvancedMachineRecipe of the ItemFluidInput in the parameters, using the map in the paramaters.
     * @param input - AdvancedInput
     * @param recipes - Map of recipes
     * @return AdvancedMachineRecipe
     */
    public static <RECIPE extends AdvancedMachineRecipe<RECIPE>> RECIPE getRecipe(ItemFluidInput input, Map<ItemFluidInput, RECIPE> recipes)
    {
        if(input.isValid())
        {
            RECIPE recipe = recipes.get(input);
            return recipe == null ? null : recipe.copy();
        }

        return null;
    }

    public enum Recipe
    {
        LIQUIDIZER_RECIPE(LIQUIDIZER, ItemFluidInput.class, FluidOutput.class, LiquidizerRecipe.class);

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

                    if(entry.getKey() instanceof ItemFluidInput)
                    {
                        ItemStack stack = ((ItemFluidInput)entry.getKey()).itemStack;

                        if(ItemStackUtil.isCraftingEquivalent(stack, input))
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
