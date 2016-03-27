package erogenousbeef.bigreactors.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ILiquidizerManager extends ICraftingProvider<ILiquidizerRecipe> {

    /**
     * Add a recipe to the Liquidizer
     *
     * @param resource1 ItemStack representing the resource in first slot.
     * @param resource2 ItemStack representing the resource in second slot.
     * @param liquid LiquidStack representing resource liquid and amount.
     * @param output LiquidStack representing output liquid.
     * @throws NullPointerException if resource, output or liquid is null
     */
    void addRecipe(ItemStack resource1, ItemStack resource2, FluidStack liquid, FluidStack output);

    /**
     * Add a recipe to the Liquidizer. Defaults to water as input liquid.
     *
     * @param resource1 ItemStack representing the resource in first slot.
     * @param resource2 ItemStack representing the resource in second slot.
     * @param output LiquidStack representing output liquid.
     * @throws NullPointerException if resource, output or liquid is null
     */
    void addRecipe(ItemStack resource1, ItemStack resource2, FluidStack output);
}