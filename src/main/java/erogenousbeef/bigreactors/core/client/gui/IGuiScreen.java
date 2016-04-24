package erogenousbeef.bigreactors.core.client.gui;

import erogenousbeef.bigreactors.core.client.gui.widget.GhostSlot;
import erogenousbeef.bigreactors.core.client.gui.widget.GuiToolTip;
import net.minecraft.client.gui.GuiButton;

import java.util.List;

public interface IGuiScreen {

    void addToolTip(GuiToolTip toolTip);

    boolean removeToolTip(GuiToolTip toolTip);

    int getGuiLeft();

    int getGuiTop();

    int getXSize();

    int getYSize();

    void addButton(GuiButton button);

    void removeButton(GuiButton button);

    int getOverlayOffsetX();

    void doActionPerformed(GuiButton but);

    List<GhostSlot> getGhostSlots();

}