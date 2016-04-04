package erogenousbeef.bigreactors.common.tileentity.base;

import erogenousbeef.bigreactors.api.Coord4D;
import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.interfaces.IElectricMachine;
import erogenousbeef.bigreactors.common.recipe.inputs.MachineInput;
import erogenousbeef.bigreactors.common.recipe.machines.MachineRecipe;
import erogenousbeef.bigreactors.common.recipe.output.MachineOutput;
import erogenousbeef.bigreactors.net.packet.PacketTileEntity.TileEntityMessage;
import erogenousbeef.bigreactors.utils.BRUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.EnumSet;

public abstract class TileEntityBasicMachine <INPUT extends MachineInput<INPUT>, OUTPUT extends MachineOutput<OUTPUT>, RECIPE extends MachineRecipe<INPUT, OUTPUT, RECIPE>> extends TileEntityElectricBlock implements IElectricMachine<INPUT, OUTPUT, RECIPE> {

    /** How much energy this machine uses per tick, un-upgraded. */
    public double BASE_ENERGY_PER_TICK;

    /**	How much energy this machine uses per tick including upgrades */
    public double energyPerTick;

    /** How many ticks this machine has operated for. */
    public int operatingTicks = 0;

    /** Un-upgraded ticks required to operate -- or smelt an item. */
    public int BASE_TICKS_REQUIRED;

    /** Ticks required including upgrades */
    public int ticksRequired;

    /** How many ticks must pass until this block's active state can sync with the client. */
    public int updateDelay;

    /** Whether or not this block is in it's active state. */
    public boolean isActive;

    /** The client's current active state. */
    public boolean clientActive;

    /** The GUI texture path for this machine. */
    public ResourceLocation guiLocation;

    /** This machine's previous amount of energy. */
    public double prevEnergy;

    public RECIPE cachedRecipe = null;

    /**
     * The foundation of all machines - a simple tile entity with a facing, active state, initialized state, sound effect, and animated texture.
     * @param name - full name of this machine
     * @param location - GUI texture path of this machine
     * @param perTick - the energy this machine consumes every tick in it's active state
     * @param baseTicksRequired - how many ticks it takes to run a cycle
     * @param maxEnergy - how much energy this machine can store
     */
    public TileEntityBasicMachine(String name, ResourceLocation location, double perTick, int baseTicksRequired, double maxEnergy)
    {
        super(name, maxEnergy);
        BASE_ENERGY_PER_TICK = perTick;
        energyPerTick = perTick;
        BASE_TICKS_REQUIRED = baseTicksRequired;
        ticksRequired = baseTicksRequired;
        guiLocation = location;
        isActive = false;
    }

    @Override
    public void onUpdate() {

        if(worldObj.isRemote && updateDelay > 0)
        {
            updateDelay--;

            if(updateDelay == 0 && clientActive != isActive)
            {
                isActive = clientActive;
                BRUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
            }
        }

        if(!worldObj.isRemote)
        {
            if(updateDelay > 0)
            {
                updateDelay--;

                if(updateDelay == 0 && clientActive != isActive)
                {
                    BRLoader.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
                }
            }
        }
    }

    @Override
    public EnumSet<ForgeDirection> getConsumingSides()
    {
        return configComponent.getSidesForData(TransmissionType.ENERGY, facing, 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTags)
    {
        super.readFromNBT(nbtTags);

        operatingTicks = nbtTags.getInteger("operatingTicks");
        clientActive = isActive = nbtTags.getBoolean("isActive");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTags)
    {
        super.writeToNBT(nbtTags);

        nbtTags.setInteger("operatingTicks", operatingTicks);
        nbtTags.setBoolean("isActive", isActive);
    }

    @Override
    public void handlePacketData(ByteBuf dataStream)
    {
        super.handlePacketData(dataStream);

        operatingTicks = dataStream.readInt();
        clientActive = dataStream.readBoolean();
        ticksRequired = dataStream.readInt();

        if(updateDelay == 0 && clientActive != isActive)
        {
            updateDelay = general.UPDATE_DELAY;
            isActive = clientActive;
            BRUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
        }
    }

    @Override
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);

        data.add(operatingTicks);
        data.add(isActive);
        data.add(ticksRequired);

        return data;
    }

    /**
     * Gets the scaled progress level for the GUI.
     * @return
     */
    public double getScaledProgress()
    {
        return ((double)operatingTicks) / ((double)ticksRequired);
    }

    public boolean getActive()
    {
        return isActive;
    }

    public void setActive(boolean active)
    {
        isActive = active;

        if(clientActive != active && updateDelay == 0)
        {
            BRLoader.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));

            updateDelay = 10;
            clientActive = active;
        }
    }

    @Override
    public boolean canSetFacing(int facing)
    {
        return facing != 0 && facing != 1;
    }

    public int[] getAccessibleSlotsFromSide(int side)
    {
        return configComponent.getOutput(TransmissionType.ITEM, side, facing).availableSlots;
    }

    public TileComponentConfig getConfig()
    {
        return configComponent;
    }

    public int getOrientation()
    {
        return facing;
    }

    public boolean renderUpdate()
    {
        return true;
    }

    public boolean lightUpdate()
    {
        return true;
    }

}
