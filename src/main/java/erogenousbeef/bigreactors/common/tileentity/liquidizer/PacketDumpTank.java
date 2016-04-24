package erogenousbeef.bigreactors.common.tileentity.liquidizer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erogenousbeef.bigreactors.core.network.MessageTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.FluidTank;

public class PacketDumpTank extends MessageTileEntity<TileEntityLiquidizer> implements IMessageHandler<PacketDumpTank, IMessage> {

    private int tank;

    public PacketDumpTank() {
        super();
    }

    public PacketDumpTank(TileEntityLiquidizer te, int tank) {
        super(te);
        this.tank = tank;
        getTank(te).setFluid(null);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(tank);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        tank = buf.readInt();
    }

    private FluidTank getTank(TileEntityLiquidizer te) {
        if (tank == 1) {
            return te.inputTank;
        } else if (tank == 2) {
            return te.outputTank;
        }
        return null;
    }

    @Override
    public IMessage onMessage(PacketDumpTank message, MessageContext ctx) {
        TileEntityLiquidizer te = message.getTileEntity(ctx.getServerHandler().playerEntity.worldObj);
        if (te != null) {
            message.getTank(te).setFluid(null);
        }
        return null;
    }
}
