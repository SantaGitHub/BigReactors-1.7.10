package erogenousbeef.bigreactors.common.tileentity.liquidizer;

import erogenousbeef.bigreactors.ModObject;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.recipe.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.List;

public class LiquidizerRecipeManager {

    private static final String CORE_FILE_NAME = "LiquidizerRecipes_Core.xml";
    private static final String CUSTOM_FILE_NAME = "LiquidizerRecipes_User.xml";

    static final LiquidizerRecipeManager instance = new LiquidizerRecipeManager();

    public static LiquidizerRecipeManager getInstance() {
        return instance;
    }

    private final List<IRecipe> recipes = new ArrayList<IRecipe>();

    public LiquidizerRecipeManager() {
    }

    public void loadRecipesFromConfig() {
        RecipeConfig config = RecipeConfig.loadRecipeConfig(CORE_FILE_NAME, CUSTOM_FILE_NAME, null);
        if(config != null) {
            processConfig(config);
        } else {
            BRLog.error("Could not load recipes for Liquidizer.");
        }

        MachineRecipeRegistry.instance.registerRecipe(ModObject.blockLiquidizer.unlocalisedName, new LiquidizerMachineRecipe());

    }

    public void addCustomRecipes(String xmlDef) {
        RecipeConfig config;
        try {
            config = RecipeConfigParser.parse(xmlDef, null);
        } catch (Exception e) {
            BRLog.error("Error parsing custom xml");
            return;
        }

        if(config == null) {
            BRLog.error("Could process custom XML");
            return;
        }
        processConfig(config);
    }

    public IRecipe getRecipeForInput(MachineRecipeInput[] inputs) {
        if(inputs == null || inputs.length == 0) {
            return null;
        }
        for (IRecipe recipe : recipes) {
            if(recipe.isInputForRecipe(inputs)) {
                return recipe;
            }
        }
        return null;
    }

    private void processConfig(RecipeConfig config) {

        List<Recipe> newRecipes = config.getRecipes(false);
        BRLog.info("Found " + newRecipes.size() + " valid Liquidizer recipes in config.");
        for (Recipe rec : newRecipes) {
            addRecipe(rec);
        }
        BRLog.info("Finished processing Liquidizer recipes. " + recipes.size() + " recipes avaliable.");
    }

    public void addRecipe(IRecipe recipe) {
        if(recipe == null || !recipe.isValid()) {
            BRLog.debug("Could not add invalid Liquidizer recipe: " + recipe);
            return;
        }
        recipes.add(new LiquidizerRecipe(recipe));
    }

    public List<IRecipe> getRecipes() {
        return recipes;
    }

    public boolean isValidInput(MachineRecipeInput input) {
        for (IRecipe recipe : recipes) {
            if(input.item != null && recipe.isValidInput(input.slotNumber, input.item)) {
                return true;
            } else if(input.fluid != null && recipe.isValidInput(input.fluid)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidInput(MachineRecipeInput[] inputs) {
        for (IRecipe recipe : recipes) {
            boolean allValid = true;
            for(MachineRecipeInput input : inputs) {
                if(input.item != null) {
                    allValid = recipe.isValidInput(input.slotNumber, input.item);
                } else if(input.fluid != null) {
                    allValid = recipe.isValidInput(input.fluid);
                }
                if(!allValid) {
                    break;
                }
            }
            if(allValid) {
                return true;
            }
        }
        return false;
    }

    public float getMultiplierForInput(ItemStack input, Fluid output) {
        if (output != null) {
            for (IRecipe recipe : recipes) {
                RecipeOutput out = recipe.getOutputs()[0];
                if (out.getFluidOutput().getFluid().getID() == output.getID()) {
                    for (RecipeInput ri : recipe.getInputs()) {
                        if (ri.isInput(input)) {
                            return ri.getMulitplier();
                        }
                    }
                }
            }
        }
        // no fluid or not an input for this fluid: best guess
        // (after all, the item IS in the input slot)
        float found = -1f;
        for (IRecipe recipe : recipes) {
            for (RecipeInput ri : recipe.getInputs()) {
                if (ri.isInput(input)) {
                    if (found < 0f || found > ri.getMulitplier()) {
                        found = ri.getMulitplier();
                    }
                }
            }
        }
        return found > 0 ? found : 0;
    }
}
