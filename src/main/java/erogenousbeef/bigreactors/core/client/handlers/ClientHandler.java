package erogenousbeef.bigreactors.core.client.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import erogenousbeef.bigreactors.core.Handlers.Handler;

@Handler
public class ClientHandler {

    private static int ticksElapsed;

    public static int getTicksElapsed() {
        return ticksElapsed;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ticksElapsed++;
        }
    }
}
