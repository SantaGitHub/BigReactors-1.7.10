package erogenousbeef.bigreactors.client.gui.base;

import erogenousbeef.bigreactors.common.machine.IIoConfigurable;
import erogenousbeef.bigreactors.common.machine.IoMode;
import erogenousbeef.bigreactors.core.client.gui.IGuiOverlay;
import erogenousbeef.bigreactors.core.client.gui.IGuiScreen;
import erogenousbeef.bigreactors.core.client.gui.button.ToggleButton;
import erogenousbeef.bigreactors.core.client.render.ColorUtil;
import erogenousbeef.bigreactors.core.client.render.RenderUtil;
import erogenousbeef.bigreactors.client.gui.base.IoConfigRenderer.SelectedFace;
import erogenousbeef.bigreactors.core.util.BlockCoord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.*;

public class GuiOverlayIoConfig implements IGuiOverlay {

    private boolean visible = false;
    private ToggleButton configB;

    private IGuiScreen screen;

    private Rectangle bounds;
    int height = 80;

    private IoConfigRenderer renderer;

    private java.util.List<BlockCoord> coords = new ArrayList<BlockCoord>();

    public GuiOverlayIoConfig(IIoConfigurable ioConf) {
        coords.add(ioConf.getLocation());
    }

    public GuiOverlayIoConfig(Collection<BlockCoord> bc) {
        coords.addAll(bc);
    }

    public void setConfigB(ToggleButton configB) {
        this.configB = configB;
    }

    @Override
    public void init(IGuiScreen screen) {
        this.screen = screen;
        renderer = new IoConfigRenderer(coords) {

            @Override
            protected String getLabelForMode(IoMode mode) {
                return GuiOverlayIoConfig.this.getLabelForMode(mode);
            }

        };
        renderer.init();
        bounds = new Rectangle(screen.getOverlayOffsetX() + 5, screen.getYSize() - height -5, screen.getXSize() - 10, height);
    }

    protected String getLabelForMode(IoMode mode) {
        return mode.getLocalisedName();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTick) {

        RenderUtil.renderQuad2D(bounds.x, bounds.y, 0, bounds.width, bounds.height, ColorUtil.getRGB(Color.black));
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        int vpx = ( (screen.getGuiLeft() + bounds.x - screen.getOverlayOffsetX())* scaledresolution.getScaleFactor());
        int vpy = (screen.getGuiTop() + 4) * scaledresolution.getScaleFactor();
        int w = bounds.width * scaledresolution.getScaleFactor();
        int h = bounds.height * scaledresolution.getScaleFactor();

        renderer.drawScreen(mouseX, mouseY, partialTick, new Rectangle(vpx,vpy,w,h), bounds);

    }

    @Override
    public boolean handleMouseInput(int x, int y, int b) {
        if(!isMouseInBounds(x, y)) {
            renderer.handleMouseInput();
            return false;
        }

        renderer.handleMouseInput();
        return true;
    }

    @Override
    public boolean isMouseInBounds(int mouseX, int mouseY) {
        int x = mouseX - screen.getGuiLeft();
        int y = mouseY - screen.getGuiTop();
        if(bounds.contains(x,y)) {
            return true;
        }
        return false;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        if(configB != null) {
            configB.setSelected(visible);
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    public SelectedFace getSelection() {
        return visible ? renderer.getSelection() : null;
    }
}