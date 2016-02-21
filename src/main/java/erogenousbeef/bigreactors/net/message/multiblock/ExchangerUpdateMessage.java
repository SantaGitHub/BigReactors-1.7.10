package erogenousbeef.bigreactors.net.message.multiblock;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erogenousbeef.bigreactors.common.multiblock.MultiblockExchanger;
import erogenousbeef.bigreactors.net.message.base.ExchangerMessageClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

public class ExchangerUpdateMessage extends ExchangerMessageClient {
    ByteBuf data;

    public ExchangerUpdateMessage() { super(); }
    public ExchangerUpdateMessage(MultiblockExchanger exchanger) {
        super(exchanger);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        data = buf.readBytes(buf.readableBytes());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        exchanger.serialize(buf);
    }

    public static class Handler extends ExchangerMessageClient.Handler<ExchangerUpdateMessage> {
        @Override
        protected IMessage handleMessage(ExchangerUpdateMessage message,
                                         MessageContext ctx, MultiblockExchanger exchanger) {
            exchanger.deserialize(message.data);
            return null;
        }

        @Override
        protected IMessage handleMessage(ExchangerUpdateMessage message, MessageContext ctx, TileEntity te) {
            return null;
        }

        @Override
        public IMessage onMessage(ExchangerUpdateMessage message, MessageContext ctx) {
            return null;
        }
    }
}
