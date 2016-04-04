package erogenousbeef.bigreactors.net.packet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erogenousbeef.bigreactors.api.Coord4D;
import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.PacketHandler;
import erogenousbeef.bigreactors.net.packet.PacketTileEntity.TileEntityMessage;
import erogenousbeef.bigreactors.net.packet.PacketDataRequest.DataRequestMessage;
import erogenousbeef.bigreactors.common.interfaces.ITileNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

public class PacketDataRequest implements IMessageHandler<DataRequestMessage, IMessage> {
    @Override
    public IMessage onMessage(DataRequestMessage message, MessageContext context)
    {
        EntityPlayer player = PacketHandler.getPlayer(context);
        World worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(message.coord4D.dimensionId);

        if(worldServer != null && message.coord4D.getTileEntity(worldServer) instanceof ITileNetwork)
        {
            TileEntity tileEntity = message.coord4D.getTileEntity(worldServer);
            BRLoader.packetHandler.sendTo(new TileEntityMessage(Coord4D.get(tileEntity), ((ITileNetwork)tileEntity).getNetworkedData(new ArrayList())), (EntityPlayerMP)player);
        }

        return null;
    }

    public static class DataRequestMessage implements IMessage
    {
        public Coord4D coord4D;

        public DataRequestMessage() {}

        public DataRequestMessage(Coord4D coord)
        {
            coord4D = coord;
        }

        @Override
        public void toBytes(ByteBuf dataStream)
        {
            dataStream.writeInt(coord4D.xCoord);
            dataStream.writeInt(coord4D.yCoord);
            dataStream.writeInt(coord4D.zCoord);

            dataStream.writeInt(coord4D.dimensionId);
        }

        @Override
        public void fromBytes(ByteBuf dataStream)
        {
            coord4D = new Coord4D(dataStream.readInt(), dataStream.readInt(), dataStream.readInt(), dataStream.readInt());
        }
    }
}