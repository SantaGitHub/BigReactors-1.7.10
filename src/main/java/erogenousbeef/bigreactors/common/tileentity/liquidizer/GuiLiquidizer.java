package erogenousbeef.bigreactors.common.tileentity.liquidizer;

import erogenousbeef.bigreactors.client.gui.base.GuiPoweredMachineBase;
import erogenousbeef.bigreactors.client.gui.base.IconBR;
import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.fluid.Fluids;
import erogenousbeef.bigreactors.common.machine.IoMode;
import erogenousbeef.bigreactors.core.client.gui.IconButton;
import erogenousbeef.bigreactors.core.client.gui.widget.GuiToolTip;
import erogenousbeef.bigreactors.core.client.render.ColorUtil;
import erogenousbeef.bigreactors.core.client.render.RenderUtil;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiLiquidizer extends GuiPoweredMachineBase<TileEntityLiquidizer> {

    private static final String GUI_TEXTURE = "liquidizerTest";

    private final IconButton dump1, dump2;

    public GuiLiquidizer(InventoryPlayer inventory, TileEntityLiquidizer te) {
        super(te, new ContainerLiquidizer(inventory, te), GUI_TEXTURE);

        addToolTip(new GuiToolTip(new Rectangle(30, 12, 15, 47), "") {

            @Override
            protected void updateText() {
                text.clear();
                String heading = BRLoader.lang.localize("liquidizer.inputTank");
                if(getTileEntity().inputTank.getFluid() != null) {
                    heading += ": " + getTileEntity().inputTank.getFluid().getLocalizedName();
                }
                text.add(heading);
                text.add(Fluids.toCapactityString(getTileEntity().inputTank));
            }

        });

        addToolTip(new GuiToolTip(new Rectangle(132, 12, 15, 47), "") {

            @Override
            protected void updateText() {
                text.clear();
                String heading = BRLoader.lang.localize("liquidizer.outputTank");
                if(getTileEntity().outputTank.getFluid() != null) {
                    heading += ": " + getTileEntity().outputTank.getFluid().getLocalizedName();
                }
                text.add(heading);
                text.add(Fluids.toCapactityString(getTileEntity().outputTank));
            }

        });

        dump1 = new IconButton(this, 1, 29, 62, IconBR.REDSTONE_MODE_NEVER);
        dump1.setToolTip(BRLoader.lang.localize("gui.machine.liquidizer.dump.1"));
        dump2 = new IconButton(this, 2, 131, 62, IconBR.REDSTONE_MODE_NEVER);
        dump2.setToolTip(BRLoader.lang.localize("gui.machine.liquidizer.dump.2"));

        addProgressTooltip(81, 63, 14, 14);
    }

    @Override
    public void initGui() {
        super.initGui();
        dump1.onGuiInit();
        dump2.onGuiInit();
    }

    @Override
    public void renderSlotHighlights(IoMode mode) {
        super.renderSlotHighlights(mode);

        int x = 30;
        int y = 12;
        if(mode == IoMode.PULL || mode == IoMode.PUSH_PULL) {
            renderSlotHighlight(PULL_COLOR, x - 2, y - 2, 15 + 4, 47 + 4);
        }
        if(mode == IoMode.PUSH || mode == IoMode.PUSH_PULL) {
            x = 132;
            renderSlotHighlight(PUSH_COLOR, x - 2, y - 2, 15 + 4, 47 + 4);
        }
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindGuiTexture();
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        TileEntityLiquidizer liquidizer = getTileEntity();

        if(shouldRenderProgress()) {
            int scaled = getProgressScaled(14) + 1;
            drawTexturedModalRect(guiLeft + 81, guiTop + 77 - scaled, 176, 14 - scaled, 14, scaled);

            IIcon inputIcon = null;
            if(liquidizer.currentTaskInputFluid != null) {
                inputIcon = liquidizer.currentTaskInputFluid.getStillIcon();
            }
            IIcon outputIcon = null;
            if(liquidizer.currentTaskOutputFluid != null) {
                outputIcon = liquidizer.currentTaskOutputFluid.getStillIcon();
            }

            if(inputIcon != null && outputIcon != null) {
                renderVat(inputIcon, outputIcon, liquidizer.getProgress());
            }

        }

        int x = guiLeft + 30;
        int y = guiTop + 12;
        RenderUtil.renderGuiTank(liquidizer.inputTank, x, y, zLevel, 15, 47);
        x = guiLeft + 132;
        RenderUtil.renderGuiTank(liquidizer.outputTank, x, y, zLevel, 15, 47);

        Fluid inputFluid;
        if (liquidizer.inputTank.getFluidAmount() > 0) {
            inputFluid = liquidizer.inputTank.getFluid().getFluid();
        } else {
            inputFluid = liquidizer.currentTaskInputFluid;
        }

        Fluid outputFluid;
        if (liquidizer.outputTank.getFluidAmount() > 0) {
            outputFluid = liquidizer.outputTank.getFluid().getFluid();
        } else {
            outputFluid = liquidizer.currentTaskOutputFluid;
        }

        bindGuiTexture();
        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
    }

    private void renderVat(IIcon inputIcon, IIcon outputIcon, float progress) {
        RenderUtil.bindBlockTexture();

        int x = guiLeft + 76;
        int y = guiTop + 34;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1, 1, 1, 0.75f * (1f - progress));
        drawTexturedModelRectFromIcon(x, y, inputIcon, 26, 28);

        GL11.glColor4f(1, 1, 1, 0.75f * progress);
        drawTexturedModelRectFromIcon(x, y, outputIcon, 26, 28);

        GL11.glDisable(GL11.GL_BLEND);

        GL11.glColor4f(1, 1, 1, 1);
        bindGuiTexture();
        drawTexturedModalRect(x, y, 0, 256 - 28, 26, 28);
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        super.actionPerformed(b);

        if(b == dump1) {
            dump(1);
        } else if(b == dump2) {
            dump(2);
        }
    }

    private void dump(int i) {
        CommonPacketHandler.INSTANCE.sendToServer(new PacketDumpTank(getTileEntity(), i));
    }

    @Override
    protected int getPowerX() {
        return 10;
    }

    @Override
    protected int getPowerY() {
        return 13;
    }

    @Override
    protected int getPowerHeight() {
        return 60;
    }

}
