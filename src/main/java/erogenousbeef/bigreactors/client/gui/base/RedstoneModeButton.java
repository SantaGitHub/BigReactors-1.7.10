package erogenousbeef.bigreactors.client.gui.base;

import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.machine.IRedstoneModeControlable;
import erogenousbeef.bigreactors.common.machine.PacketRedstoneMode;
import erogenousbeef.bigreactors.common.machine.RedstoneControlMode;
import erogenousbeef.bigreactors.core.client.gui.IGuiScreen;
import erogenousbeef.bigreactors.core.client.gui.button.CycleButton;
import erogenousbeef.bigreactors.core.util.BlockCoord;
import erogenousbeef.bigreactors.net.CommonPacketHandler;

public class RedstoneModeButton extends CycleButton<RedstoneControlMode> {

    private IRedstoneModeControlable model;

    private BlockCoord bc;

    private String tooltipKey = "bigreactors.gui.tooltip.redstoneControlMode";

    public RedstoneModeButton(IGuiScreen gui, int id, int x, int y, IRedstoneModeControlable model) {
        this(gui, id, x, y, model, null);
    }

    public RedstoneModeButton(IGuiScreen gui, int id, int x, int y, IRedstoneModeControlable model, BlockCoord bc) {
        super(gui, id, x, y, RedstoneControlMode.class);
        this.model = model;
        this.bc = bc;
        setMode(model.getRedstoneControlMode());
    }

    public void setMode(RedstoneControlMode newMode) {
        if (model == null) {
            return;
        }
        super.setMode(newMode);
        model.setRedstoneControlMode(getMode());
        if (bc != null) {
            CommonPacketHandler.INSTANCE.sendToServer(new PacketRedstoneMode(model, bc.x, bc.y, bc.z));
        }
        setTooltipKey(tooltipKey); // forces our behavior
    }

    public void setTooltipKey(String key) {
        tooltipKey = key;
        setToolTip(BRLoader.lang.localizeExact(tooltipKey), getMode().getTooltip());
    }
}