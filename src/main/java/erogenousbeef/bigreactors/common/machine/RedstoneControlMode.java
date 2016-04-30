package erogenousbeef.bigreactors.common.machine;

import com.google.common.collect.Lists;
import erogenousbeef.bigreactors.client.gui.base.IconBR;
import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.core.client.gui.button.CycleButton.ICycleEnum;
import erogenousbeef.bigreactors.core.client.render.IWidgetIcon;
import net.minecraft.tileentity.TileEntity;

import java.util.List;
import java.util.Locale;

public enum RedstoneControlMode implements ICycleEnum {

    IGNORE(IconBR.REDSTONE_MODE_ALWAYS),
    ON(IconBR.REDSTONE_MODE_WITH_SIGNAL),
    OFF(IconBR.REDSTONE_MODE_WITHOUT_SIGNAL),
    NEVER(IconBR.REDSTONE_MODE_NEVER);


    private IWidgetIcon icon;

    RedstoneControlMode(IWidgetIcon icon) {
        this.icon = icon;
    }

    public String getTooltip() {
        return BRLoader.lang.localize("gui.tooltip.redstoneControlMode." + name().toLowerCase(Locale.US));
    }

    @Override
    public IWidgetIcon getIcon() {
        return icon;
    }

    @Override
    public List<String> getTooltipLines() {
        return Lists.newArrayList(getTooltip());
    }

    public static boolean isConditionMet(RedstoneControlMode redstoneControlMode, int powerLevel) {
        boolean redstoneCheckPassed = true;
        if(redstoneControlMode == RedstoneControlMode.NEVER) {
            redstoneCheckPassed = false;
        } else if(redstoneControlMode == RedstoneControlMode.ON) {
            if(powerLevel < 1) {
                redstoneCheckPassed = false;
            }
        } else if(redstoneControlMode == RedstoneControlMode.OFF) {
            if(powerLevel > 0) {
                redstoneCheckPassed = false;
            }
        }
        return redstoneCheckPassed;
    }

    public static boolean isConditionMet(RedstoneControlMode redstoneControlMode, TileEntity te) {
        return isConditionMet(redstoneControlMode, te.getWorldObj().getStrongestIndirectPower(te.xCoord, te.yCoord, te.zCoord));
    }

    public RedstoneControlMode next() {
        int ord = ordinal();
        if(ord == values().length - 1) {
            ord = 0;
        } else {
            ord++;
        }
        return values()[ord];
    }

    public RedstoneControlMode previous() {
        int ord = ordinal();
        ord--;
        if(ord < 0) {
            ord = values().length - 1;
        }
        return values()[ord];
    }

}
