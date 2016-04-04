package erogenousbeef.bigreactors.common.tileentity.base;

import cpw.mods.fml.common.Optional;
import erogenousbeef.bigreactors.api.Coord4D;
import erogenousbeef.bigreactors.api.Range4D;
import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.interfaces.IChunkLoadHandler;
import erogenousbeef.bigreactors.common.interfaces.ITileComponent;
import erogenousbeef.bigreactors.common.interfaces.ITileNetwork;
import erogenousbeef.bigreactors.net.packet.PacketTileEntity.TileEntityMessage;
import erogenousbeef.bigreactors.net.packet.PacketDataRequest.DataRequestMessage;
import erogenousbeef.bigreactors.utils.BRUtils;
import ic2.api.tile.IWrenchable;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Optional.Interface(iface = "ic2.api.tile.IWrenchable", modid = "IC2")
public abstract class TileEntityBasicBlock extends TileEntity implements IWrenchable, ITileNetwork, IChunkLoadHandler
{
    /** The direction this block is facing. */
    public int facing;

    public int clientFacing;

    public HashSet<EntityPlayer> openedThisTick = new HashSet<EntityPlayer>();

    /** The players currently using this block. */
    public HashSet<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

    /** A timer used to send packets to clients. */
    public int ticker;

    public boolean redstone = false;
    public boolean redstoneLastTick = false;

    public boolean doAutoSync = true;

    public List<ITileComponent> components = new ArrayList<ITileComponent>();

    @Override
    public void updateEntity()
    {
        for(ITileComponent component : components)
        {
            component.tick();
        }

        onUpdate();

        if(!worldObj.isRemote)
        {
            openedThisTick.clear();

            if(doAutoSync && playersUsing.size() > 0)
            {
                for(EntityPlayer player : playersUsing)
                {
                    BRLoader.packetHandler.sendTo(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), (EntityPlayerMP)player);
                }
            }
        }

        ticker++;
        redstoneLastTick = redstone;
    }

    @Override
    public void onChunkLoad()
    {
        markDirty();
    }

    public void open(EntityPlayer player)
    {
        playersUsing.add(player);
    }

    public void close(EntityPlayer player)
    {
        playersUsing.remove(player);
    }

    @Override
    public void handlePacketData(ByteBuf dataStream)
    {
        facing = dataStream.readInt();
        redstone = dataStream.readBoolean();

        if(clientFacing != facing)
        {
            BRUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
            clientFacing = facing;
        }

        for(ITileComponent component : components)
        {
            component.read(dataStream);
        }
    }

    @Override
    public ArrayList getNetworkedData(ArrayList data)
    {
        data.add(facing);
        data.add(redstone);

        for(ITileComponent component : components)
        {
            component.write(data);
        }

        return data;
    }

    @Override
    public void validate()
    {
        super.validate();

        if(worldObj.isRemote)
        {
            BRLoader.packetHandler.sendToServer(new DataRequestMessage(Coord4D.get(this)));
        }
    }

    /**
     * Update call for machines. Use instead of updateEntity -- it's called every tick.
     */
    public abstract void onUpdate();

    @Override
    public void readFromNBT(NBTTagCompound nbtTags)
    {
        super.readFromNBT(nbtTags);

        facing = nbtTags.getInteger("facing");
        redstone = nbtTags.getBoolean("redstone");

        for(ITileComponent component : components)
        {
            component.read(nbtTags);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTags)
    {
        super.writeToNBT(nbtTags);

        nbtTags.setInteger("facing", facing);
        nbtTags.setBoolean("redstone", redstone);

        for(ITileComponent component : components)
        {
            component.write(nbtTags);
        }
    }

    @Override
    @Optional.Method(modid = "IC2")
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side)
    {
        return true;
    }

    @Override
    @Optional.Method(modid = "IC2")
    public short getFacing()
    {
        return (short)facing;
    }

    @Override
    public void setFacing(short direction)
    {
        if(canSetFacing(direction))
        {
            facing = direction;
        }

        if(!(facing == clientFacing || worldObj.isRemote))
        {
            BRLoader.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
            markDirty();
            clientFacing = facing;
        }
    }

    /**
     * Whether or not this block's orientation can be changed to a specific direction. True by default.
     * @param facing - facing to check
     * @return if the block's orientation can be changed
     */
    public boolean canSetFacing(int facing)
    {
        return true;
    }

    @Override
    @Optional.Method(modid = "IC2")
    public boolean wrenchCanRemove(EntityPlayer entityPlayer)
    {
        return true;
    }

    @Override
    @Optional.Method(modid = "IC2")
    public float getWrenchDropRate()
    {
        return 1.0F;
    }

    @Override
    @Optional.Method(modid = "IC2")
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer)
    {
        return getBlockType().getPickBlock(null, worldObj, xCoord, yCoord, zCoord, entityPlayer);
    }

    public boolean isPowered()
    {
        return redstone;
    }

    public boolean wasPowered()
    {
        return redstoneLastTick;
    }

    public void onPowerChange() {}

    public void onNeighborChange(Block block)
    {
        if(!worldObj.isRemote)
        {
            updatePower();
        }
    }

    private void updatePower()
    {
        boolean power = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

        if(redstone != power)
        {
            redstone = power;
            BRLoader.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));

            onPowerChange();
        }
    }

    /**
     * Called when block is placed in world
     */
    public void onAdded()
    {
        updatePower();
    }
}
