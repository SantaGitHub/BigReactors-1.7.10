package erogenousbeef.bigreactors.client.gui;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.block.BlockBRDevice;
import erogenousbeef.bigreactors.common.tileentity.TileEntityLiquidizer;
import erogenousbeef.bigreactors.gui.controls.*;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiLiquidizer extends BeefGuiDeviceBase {
    private TileEntityLiquidizer _entity;

    private BeefGuiLabel titleString;

    private BeefGuiPowerBarSmall powerBarSmall;
    private BeefGuiFluidBar fluidBar_in;
    private BeefGuiFluidBar fluidBar_out;
    private BeefGuiProgressArrow progressArrow;

    public GuiLiquidizer(Container container, TileEntityLiquidizer entity) {
        super(container, entity);

        _entity = entity;
        xSize = 175;
        ySize = 165;
    }

    @Override
    public void initGui() {
        super.initGui();

        titleString = new BeefGuiLabel(this, _entity.getInventoryName(), guiLeft + 76, guiTop + 8);
        powerBarSmall = new BeefGuiPowerBarSmall(this, guiLeft + 9, guiTop + 8, _entity); //x, y -1 more than u think :)

        fluidBar_in = new BeefGuiFluidBar(this, guiLeft + 33, guiTop + 8, _entity, 0);
        fluidBar_out = new BeefGuiFluidBar(this, guiLeft + 152, guiTop + 8, _entity, 1);

        /*progressArrow = new BeefGuiProgressArrow(this, guiLeft + 76, guiTop + 41, 0, 178, _entity); */

        registerControl(titleString);
        registerControl(powerBarSmall);

        registerControl(fluidBar_in);
        registerControl(fluidBar_out);

        /*registerControl(progressArrow);

        createInventoryExposureButtons(guiLeft + 180, guiTop + 4);*/
    }

    @Override
    public ResourceLocation getGuiBackground() {
        return new ResourceLocation(BigReactors.GUI_DIRECTORY + "Liquidizer.png");
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float gameTicks) {
        super.drawScreen(mouseX, mouseY, gameTicks);
    }

    @Override
    protected int getBlockMetadata() {
        return BlockBRDevice.META_LIQUIDIZER;
    }
}
