package erogenousbeef.bigreactors.api.recipes;

import java.util.Collection;

public interface ICraftingProvider<T extends IBigReactorsRecipe> {
    /**
     * Add a new recipe to the crafting provider.
     *
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @since Forestry 4.1.0
     */
    boolean addRecipe(T recipe);

    /**
     * Remove a specific recipe from the crafting provider.
     *
     * @return <tt>true</tt> if an element was removed as a result of this call
     */
    boolean removeRecipe(T recipe);

    /**
     * @return an unmodifiable collection of all recipes registered to the crafting provider.
     */
    Collection<T> recipes();
}
