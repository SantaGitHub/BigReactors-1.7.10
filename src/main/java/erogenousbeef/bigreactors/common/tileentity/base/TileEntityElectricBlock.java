package erogenousbeef.bigreactors.common.tileentity.base;

import cpw.mods.fml.common.Optional;
import erogenousbeef.bigreactors.common.interfaces.IEnergyWrapper;
import erogenousbeef.bigreactors.utils.BRUtils;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.EnumSet;

public abstract class TileEntityElectricBlock extends TileEntityContainerBlock implements IEnergyWrapper {

    /** How much energy is stored in this block. */
    public double electricityStored;

    /** Maximum amount of energy this machine can hold. */
    public double BASE_MAX_ENERGY;

    /** Actual maximum energy storage, including upgrades */
    public double maxEnergy;

    /** Is this registered with IC2 */
    public boolean ic2Registered = false;

    /**
     * The base of all blocks that deal with electricity. It has a facing state, initialized state,
     * and a current amount of stored energy.
     * @param name - full name of this block
     * @param baseMaxEnergy - how much energy this block can store
     */
    public TileEntityElectricBlock(String name, double baseMaxEnergy)
    {
        super(name);
        BASE_MAX_ENERGY = baseMaxEnergy;
        maxEnergy = BASE_MAX_ENERGY;
    }

    @Override
    public EnumSet<ForgeDirection> getOutputtingSides()
    {
        return EnumSet.noneOf(ForgeDirection.class);
    }

    @Override
    public EnumSet<ForgeDirection> getConsumingSides()
    {
        return EnumSet.allOf(ForgeDirection.class);
    }

    @Override
    public double getMaxOutput()
    {
        return 0;
    }

    @Override
    public double getEnergy()
    {
        return electricityStored;
    }

    @Override
    public void setEnergy(double energy)
    {
        electricityStored = Math.max(Math.min(energy, getMaxEnergy()), 0);
        BRUtils.saveChunk(this);
    }

    @Override
    public double getMaxEnergy()
    {
        return maxEnergy;
    }

    @Override
    public void handlePacketData(ByteBuf dataStream)
    {
        super.handlePacketData(dataStream);

        setEnergy(dataStream.readDouble());
    }

    @Override
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);

        data.add(getEnergy());

        return data;
    }

    @Override
    public void onAdded()
    {
        super.onAdded();
    }

    @Override
    public void onChunkUnload()
    {
        super.onChunkUnload();
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTags)
    {
        super.readFromNBT(nbtTags);

        electricityStored = nbtTags.getDouble("electricityStored");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTags)
    {
        super.writeToNBT(nbtTags);

        nbtTags.setDouble("electricityStored", getEnergy());
    }

    /**
     * Gets the scaled energy level for the GUI.
     * @param i - multiplier
     * @return scaled energy
     */
    public int getScaledEnergyLevel(int i)
    {
        return (int)(getEnergy()*i / getMaxEnergy());
    }

    @Override
    @Optional.Method(modid = "CoFHCore")
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        if(getConsumingSides().contains(from))
        {
            double toAdd = (int)Math.min(getMaxEnergy()-getEnergy(), maxReceive);

            if(!simulate)
            {
                setEnergy(getEnergy() + toAdd);
            }

            return (int)Math.round(toAdd);
        }

        return 0;
    }

    @Override
    @Optional.Method(modid = "CoFHCore")
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        if(getOutputtingSides().contains(from))
        {
            double toSend = Math.min(getEnergy(), Math.min(getMaxOutput(), maxExtract));

            if(!simulate)
            {
                setEnergy(getEnergy() - toSend);
            }

            return (int)Math.round(toSend);
        }

        return 0;
    }

    @Override
    @Optional.Method(modid = "CoFHCore")
    public boolean canConnectEnergy(ForgeDirection from)
    {
        return getConsumingSides().contains(from) || getOutputtingSides().contains(from);
    }

    @Override
    @Optional.Method(modid = "CoFHCore")
    public int getEnergyStored(ForgeDirection from)
    {
        return (int)Math.round(getEnergy());
    }

    @Override
    @Optional.Method(modid = "CoFHCore")
    public int getMaxEnergyStored(ForgeDirection from)
    {
        return (int)Math.round(getMaxEnergy());
    }

    @Override
    public boolean canOutputTo(ForgeDirection side)
    {
        return getOutputtingSides().contains(side);
    }

    @Override
    public double transferEnergyToAcceptor(ForgeDirection side, double amount)
    {
        if(!(getConsumingSides().contains(side) || side == ForgeDirection.UNKNOWN))
        {
            return 0;
        }

        double toUse = Math.min(getMaxEnergy()-getEnergy(), amount);
        setEnergy(getEnergy() + toUse);

        return toUse;
    }
}
