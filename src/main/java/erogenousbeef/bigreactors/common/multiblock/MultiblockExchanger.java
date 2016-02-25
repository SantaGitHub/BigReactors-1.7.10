package erogenousbeef.bigreactors.common.multiblock;

import cofh.api.energy.IEnergyProvider;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.interfaces.IMultipleFluidHandler;
import erogenousbeef.bigreactors.common.multiblock.block.BlockExchangerPart;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import erogenousbeef.bigreactors.common.multiblock.tileentity.*;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import erogenousbeef.bigreactors.net.message.multiblock.ExchangerUpdateMessage;
import erogenousbeef.core.multiblock.IMultiblockPart;
import erogenousbeef.core.multiblock.MultiblockControllerBase;
import erogenousbeef.core.multiblock.MultiblockValidationException;
import erogenousbeef.core.multiblock.rectangular.RectangularMultiblockControllerBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.HashSet;
import java.util.Set;

public class MultiblockExchanger extends RectangularMultiblockControllerBase implements IEnergyProvider, IMultipleFluidHandler, IActivateable {

    // Game stuff - stored
    protected boolean active;
    private float coolantHeat;

    // Lists of connected parts
    private Set<ITickableMultiblockPart> attachedTickables;

    private Set<TileEntityExchangerPartStandard> attachedControllers;

    // Updates
    private Set<EntityPlayer> updatePlayers;
    private int ticksSinceLastUpdate;
    private static final int ticksBetweenUpdates = 3;

    public MultiblockExchanger(World world) {
        super(world);

        // Game stuff
        active = false;

        attachedTickables = new HashSet<ITickableMultiblockPart>();
        attachedControllers = new HashSet<TileEntityExchangerPartStandard>();

        updatePlayers = new HashSet<EntityPlayer>();

        ticksSinceLastUpdate = 0;
    }

    public void beginUpdatingPlayer(EntityPlayer playerToUpdate) {
        updatePlayers.add(playerToUpdate);
        sendIndividualUpdate(playerToUpdate);
    }

    public void stopUpdatingPlayer(EntityPlayer playerToRemove) {
        updatePlayers.remove(playerToRemove);
    }

    @Override
    protected void onBlockAdded(IMultiblockPart part) {
        if(part instanceof TileEntityExchangerPartStandard) {
            TileEntityExchangerPartStandard exchangerPart = (TileEntityExchangerPartStandard) part;
            if(BlockExchangerPart.isController(exchangerPart.getBlockMetadata())) {
                attachedControllers.add(exchangerPart);
            }
        }

        if(part instanceof ITickableMultiblockPart) {
            attachedTickables.add((ITickableMultiblockPart)part);
        }
    }

    @Override
    protected void onBlockRemoved(IMultiblockPart part) {
        if(part instanceof TileEntityExchangerPartStandard) {
            TileEntityExchangerPartStandard exchangerPart = (TileEntityExchangerPartStandard)part;
            if(BlockExchangerPart.isController(exchangerPart.getBlockMetadata())) {
                attachedControllers.remove(exchangerPart);
            }
        }

        if(part instanceof ITickableMultiblockPart) {
            attachedTickables.remove((ITickableMultiblockPart)part);
        }
    }

    @Override
    protected void isMachineWhole() throws MultiblockValidationException {
        // Ensure that there is at least one controller attached.
        if(attachedControllers.size() < 1) {
            throw new MultiblockValidationException("Not enough controllers. Heat Exchangers require at least 1.");
        }

        super.isMachineWhole();
    }

    @Override
    public void updateClient() {}

    // Update loop. Only called when the machine is assembled.
    @Override
    public boolean updateServer() {
        // Send updates periodically
        ticksSinceLastUpdate++;
        if(ticksSinceLastUpdate >= ticksBetweenUpdates) {
            ticksSinceLastUpdate = 0;
            sendTickUpdate();
        }

        // TODO: Overload/overheat

        // Update any connected tickables
        for(ITickableMultiblockPart tickable : attachedTickables) {
            tickable.onMultiblockServerTick();
        }

        return false;
    }

    public void setActive(boolean act) {
        if(act == this.active) { return; }
        this.active = act;

        for(IMultiblockPart part : connectedParts) {
            if(this.active) { part.onMachineActivated(); }
            else { part.onMachineDeactivated(); }
        }

        if(worldObj.isRemote) {
            // Force controllers to re-render on client
            for(IMultiblockPart part : attachedControllers) {
                worldObj.markBlockForUpdate(part.xCoord, part.yCoord, part.zCoord);
            }
        }
        else {
            this.markReferenceCoordForUpdate();
        }
    }

    protected void addCoolantHeat(float additionalHeat) {
        if(Float.isNaN(additionalHeat)) { return; }

        coolantHeat += additionalHeat;
        if(-0.00001f < coolantHeat & coolantHeat < 0.00001f) { coolantHeat = 0f; }
    }

    public float getCoolantHeat() { return coolantHeat; }

    @Override
    protected void isBlockGoodForInterior(World world, int x, int y, int z) throws MultiblockValidationException {
        if(world.isAirBlock(x, y, z)) { return; } // Air is OK
        //TODO: ADD Blocks
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        data.setBoolean("exchangerActive", this.active);
        data.setFloat("coolantHeat", coolantHeat);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        if(data.hasKey("exchangerActive")) {
            setActive(data.getBoolean("exchangerActive"));
        }

        if(data.hasKey("coolantHeat")) {
            setCoolantHeat(data.getFloat("coolantHeat"));
        }
    }

    public void setCoolantHeat(float newCoolantHeat) {
        if(Float.isNaN(newCoolantHeat)) { coolantHeat = 0f; }
        else { coolantHeat = newCoolantHeat; }
    }

    @Override
    protected int getMinimumNumberOfBlocksForAssembledMachine() {
        // Hollow cube.
        return 26;
    }

    @Override
    public void formatDescriptionPacket(NBTTagCompound data) {
        writeToNBT(data);
    }

    @Override
    public void decodeDescriptionPacket(NBTTagCompound data) {
        readFromNBT(data);
    }

    // Network & Storage methods
	/*
	 * Serialize a heat exchanger into a given Byte buffer
	 * @param buf The byte buffer to serialize into
	 */
    public void serialize(ByteBuf buf) {

        // Basic data
        buf.writeBoolean(active);
    }

    /*
     * Deserialize a heat exchanger's data from a given Byte buffer
     * @param buf The byte buffer containing heat exchanger data
     */
    public void deserialize(ByteBuf buf) {
        // Basic data
        setActive(buf.readBoolean());
    }

    protected IMessage getUpdatePacket() {
        return new ExchangerUpdateMessage(this);
    }

    /**
     * Sends a full state update to a player.
     */
    protected void sendIndividualUpdate(EntityPlayer player) {
        if(this.worldObj.isRemote) { return; }

        CommonPacketHandler.INSTANCE.sendTo(getUpdatePacket(), (EntityPlayerMP)player);
    }

    /**
     * Send an update to any clients with GUIs open
     */
    protected void sendTickUpdate() {
        if(this.worldObj.isRemote) { return; }
        if(this.updatePlayers.size() <= 0) { return; }

        for(EntityPlayer player : updatePlayers) {
            CommonPacketHandler.INSTANCE.sendTo(getUpdatePacket(), (EntityPlayerMP)player);
        }
    }

    @Override
    protected void onAssimilated(MultiblockControllerBase otherMachine) {
        this.attachedTickables.clear();
        this.attachedControllers.clear();
    }

    @Override
    protected void onAssimilate(MultiblockControllerBase otherMachine) {
        if(!(otherMachine instanceof MultiblockExchanger)) {
            BRLog.warning("[%s] Heat Exchanger @ %s is attempting to assimilate a non-Heat Exchanger machine! That machine's data will be lost!", worldObj.isRemote?"CLIENT":"SERVER", getReferenceCoord());
            return;
        }

        MultiblockExchanger otherExchanger = (MultiblockExchanger)otherMachine;
    }

    @Override
    public void onAttachedPartWithMultiblockData(IMultiblockPart part, NBTTagCompound data) {
        this.readFromNBT(data);
    }

    @Override
    protected void onMachinePaused() {
    }

    @Override
    protected void onMachineDisassembled() {
        this.active = false;
    }

    @Override
    protected int getMaximumXSize() {
        return BigReactors.maximumExchangerSize;
    }

    @Override
    protected int getMaximumZSize() {
        return BigReactors.maximumExchangerSize;
    }

    @Override
    protected int getMaximumYSize() {
        return BigReactors.maximumExchangerHeight;
    }

    @Override
    public boolean getActive() {
        return this.active;
    }

    @Override
    protected void onMachineAssembled() {

    }

    @Override
    protected void onMachineRestored() {

    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return 0;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo() {
        return new FluidTankInfo[0];
    }

    public String getDebugInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Assembled: ").append(Boolean.toString(isAssembled())).append("\n");
        sb.append("Attached Blocks: ").append(Integer.toString(connectedParts.size())).append("\n");
        if(getLastValidationException() != null) {
            sb.append("Validation Exception:\n").append(getLastValidationException().getMessage()).append("\n");
        }

        if(isAssembled()) {
            sb.append("\nActive: ").append(Boolean.toString(getActive()));
            /*sb.append("\nStored Energy: ").append(Float.toString(getEnergyStored()));
            sb.append("\nCasing Heat: ").append(Float.toString(getReactorHeat()));
            sb.append("\nFuel Heat: ").append(Float.toString(getFuelHeat()));
            sb.append("\n\nReactant Tanks:\n");
            sb.append( fuelContainer.getDebugInfo() );
            sb.append("\n\nActively Cooled: ").append(Boolean.toString(!isPassivelyCooled()));
            if(!isPassivelyCooled()) {
                sb.append("\n\nCoolant Tanks:\n");
                sb.append( coolantContainer.getDebugInfo() );
            }*/
        }

        return sb.toString();
    }
}
