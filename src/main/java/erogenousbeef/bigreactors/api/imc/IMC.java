package erogenousbeef.bigreactors.api.imc;

import erogenousbeef.bigreactors.common.tileentity.liquidizer.LiquidizerRecipeManager;

/**
 * This class provides the keys for the IMC messages supported by EIO and links
 * the the details of how the messages are processed. It is preferable not to
 * refer to these constants directly to avoid a dependence on this class.
 */
public final class IMC {

    /**
     * Key for a string message to add Vat recipes. Calls
     * {@link LiquidizerRecipeManager#addCustomRecipes(String)} with the string value of
     * the message.
     */
    public static final String LIQUIDIZER_RECIPE = "recipe:liquidizer";

    private IMC() {
    }

}
