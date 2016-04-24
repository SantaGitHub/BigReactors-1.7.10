package erogenousbeef.bigreactors.client.gui.base;

import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.machine.IIoConfigurable;
import erogenousbeef.bigreactors.common.machine.PacketIoMode;
import erogenousbeef.bigreactors.core.client.gui.IGuiScreen;
import erogenousbeef.bigreactors.core.client.gui.button.ToggleButton;
import erogenousbeef.bigreactors.core.client.handlers.SpecialTooltipHandler;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class GuiButtonIoConfig extends ToggleButton {

    private final IIoConfigurable config;
    private final GuiOverlayIoConfig configOverlay;

    @SuppressWarnings("LeakingThisInConstructor")
    public GuiButtonIoConfig(IGuiScreen gui, int id, int x, int y, IIoConfigurable config, GuiOverlayIoConfig configOverlay) {
        super(gui, id, x, y, IconBR.IO_CONFIG_UP, IconBR.IO_CONFIG_DOWN);
        this.config = config;
        this.configOverlay = configOverlay;
        this.configOverlay.setConfigB(this);

        String configTooltip = BRLoader.lang.localize("gui.machine.ioMode.overlay.tooltip");
        setUnselectedToolTip(configTooltip);

        ArrayList<String> list = new ArrayList<String>();
        list.add(configTooltip);
        SpecialTooltipHandler.addTooltipFromResources(list, "bigreactors.gui.machine.ioMode.overlay.tooltip.visible.line");
        if(list.size() > 1) {
            setSelectedToolTip(list.toArray(new String[list.size()]));
        }
    }

    @Override
    protected boolean toggleSelected() {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            if(!configOverlay.isVisible()) {
                return false;
            }
            config.clearAllIoModes();
            CommonPacketHandler.INSTANCE.sendToServer(new PacketIoMode(config));
        } else {
            boolean vis = !configOverlay.isVisible();
            configOverlay.setVisible(vis);
        }
        return true;
    }
}
