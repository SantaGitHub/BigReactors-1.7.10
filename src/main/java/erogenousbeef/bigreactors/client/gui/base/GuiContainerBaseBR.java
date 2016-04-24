package erogenousbeef.bigreactors.client.gui.base;

import erogenousbeef.bigreactors.core.client.gui.GuiContainerBase;
import erogenousbeef.bigreactors.core.client.render.RenderUtil;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiContainerBaseBR extends GuiContainerBase {

    private static final String TEXTURE_PATH = ":textures/gui/23/";
    private static final String TEXTURE_EXT = ".png";

    private final List<ResourceLocation> guiTextures = new ArrayList<ResourceLocation>();

    public GuiContainerBaseBR(Container par1Container, String... guiTexture) {
        super(par1Container);
        for (String string : guiTexture) {
            guiTextures.add(getGuiTexture(string));
        }
    }

    public void bindGuiTexture() {
        bindGuiTexture(0);
    }

    public void bindGuiTexture(int id) {
        RenderUtil.bindTexture(getGuiTexture(id));
    }

    protected ResourceLocation getGuiTexture(int id) {
        return guiTextures.size() > id ? guiTextures.get(id) : null;
    }

    public static ResourceLocation getGuiTexture(String name) {
        return new ResourceLocation("bigreactors" + TEXTURE_PATH + name + TEXTURE_EXT);
    }

}
