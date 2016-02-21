package erogenousbeef.bigreactors.net.message.base;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.multiblock.MultiblockExchanger;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityExchangerPartBase;
import erogenousbeef.core.common.CoordTriplet;
import net.minecraft.tileentity.TileEntity;

public class ExchangerMessageClient extends WorldMessageClient {
    protected MultiblockExchanger exchanger;

    protected ExchangerMessageClient() { super(); exchanger = null; }
    protected ExchangerMessageClient(MultiblockExchanger exchanger, CoordTriplet referenceCoord) {
        super(referenceCoord.x, referenceCoord.y, referenceCoord.z);
        this.exchanger = exchanger;
    }
    protected ExchangerMessageClient(MultiblockExchanger exchanger) {
        this(exchanger, exchanger.getReferenceCoord());
    }

    public static abstract class Handler<M extends ExchangerMessageClient> extends WorldMessageClient.Handler<M> {
        protected abstract IMessage handleMessage(M message, MessageContext ctx, MultiblockExchanger exchanger);

        @Override
        protected IMessage handleMessage(M message, MessageContext ctx, TileEntity te) {
            if(te instanceof TileEntityExchangerPartBase) {
                MultiblockExchanger exchanger = ((TileEntityExchangerPartBase)te).getExchangerController();
                if(exchanger != null) {
                    return handleMessage(message, ctx, exchanger);
                }
                else {
                    BRLog.error("Received ReactorMessageClient for a reactor part @ %d, %d, %d which has no attached reactor", te.xCoord, te.yCoord, te.zCoord); //TODO: Change Text
                }
            }
            else {
                BRLog.error("Received ReactorMessageClient for a non-reactor-part block @ %d, %d, %d", message.x, message.y, message.z); //TODO: Change Text
            }
            return null;
        }
    }
}
