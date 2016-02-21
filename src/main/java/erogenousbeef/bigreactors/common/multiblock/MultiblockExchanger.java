package erogenousbeef.bigreactors.common.multiblock;

import cofh.api.energy.IEnergyProvider;
import cofh.lib.util.helpers.ItemHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import erogenousbeef.bigreactors.api.registry.ReactorInterior;
import erogenousbeef.bigreactors.common.interfaces.IMultipleFluidHandler;
import erogenousbeef.bigreactors.common.interfaces.IReactorFuelInfo;
import erogenousbeef.bigreactors.common.multiblock.block.BlockReactorPart;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import erogenousbeef.bigreactors.common.multiblock.interfaces.ITickableMultiblockPart;
import erogenousbeef.bigreactors.common.multiblock.tileentity.*;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import erogenousbeef.core.multiblock.IMultiblockPart;
import erogenousbeef.core.multiblock.MultiblockControllerBase;
import erogenousbeef.core.multiblock.MultiblockValidationException;
import erogenousbeef.core.multiblock.rectangular.RectangularMultiblockControllerBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.HashSet;
import java.util.Set;

public class MultiblockExchanger extends RectangularMultiblockControllerBase implements IEnergyProvider, IReactorFuelInfo, IMultipleFluidHandler, IActivateable {

    // Game stuff - stored
    protected boolean active;

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

    /**
     * Sends a full state update to a player.
     */
    protected void sendIndividualUpdate(EntityPlayer player) {
        if(this.worldObj.isRemote) { return; }

        CommonPacketHandler.INSTANCE.sendTo(getUpdatePacket(), (EntityPlayerMP)player);
    }

    @Override
    protected void onBlockAdded(IMultiblockPart part) {
        if(part instanceof TileEntityReactorPart) {
            TileEntityExchangerPartStandard exchangerPart = (TileEntityExchangerPartStandard) part;
            if(BlockReactorPart.isController(exchangerPart.getBlockMetadata())) {
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
            if(BlockReactorPart.isController(exchangerPart.getBlockMetadata())) {
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
            throw new MultiblockValidationException("Not enough controllers. Reactors require at least 1.");
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

    @Override
    protected void isBlockGoodForInterior(World world, int x, int y, int z) throws MultiblockValidationException {
        if(world.isAirBlock(x, y, z)) { return; } // Air is OK

        Material material = world.getBlock(x, y, z).getMaterial();
        if(material == net.minecraft.block.material.MaterialLiquid.water) {
            return;
        }

        Block block = world.getBlock(x, y, z);
        if(block == Blocks.iron_block || block == Blocks.gold_block || block == Blocks.diamond_block || block == Blocks.emerald_block) {
            return;
        }

        // Permit registered moderator blocks
        int metadata = world.getBlockMetadata(x, y, z);

        if(ReactorInterior.getBlockData(ItemHelper.oreProxy.getOreName(new ItemStack(block, 1, metadata))) != null) {
            return;
        }

        // Permit TE fluids
        if(block != null) {
            if(block instanceof IFluidBlock) {
                Fluid fluid = ((IFluidBlock)block).getFluid();
                String fluidName = fluid.getName();
                if(ReactorInterior.getFluidData(fluidName) != null) { return; }

                throw new MultiblockValidationException(String.format("%d, %d, %d - The fluid %s is not valid for the reactor's interior", x, y, z, fluidName));
            }
            else {
                throw new MultiblockValidationException(String.format("%d, %d, %d - %s is not valid for the reactor's interior", x, y, z, block.getLocalizedName()));
            }
        }
        else {
            throw new MultiblockValidationException(String.format("%d, %d, %d - Null block found, not valid for the reactor's interior", x, y, z));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        data.setBoolean("reactorActive", this.active);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        if(data.hasKey("reactorActive")) {
            setActive(data.getBoolean("reactorActive"));
        }
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
	 * Serialize a reactor into a given Byte buffer
	 * @param buf The byte buffer to serialize into
	 */
    public void serialize(ByteBuf buf) {

        // Basic data
        buf.writeBoolean(active);
    }

    /*
     * Deserialize a reactor's data from a given Byte buffer
     * @param buf The byte buffer containing reactor data
     */
    public void deserialize(ByteBuf buf) {
        // Basic data
        setActive(buf.readBoolean());
    }

    

    @Override
    public void onAttachedPartWithMultiblockData(IMultiblockPart part, NBTTagCompound data) {

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
    protected void onMachinePaused() {

    }

    @Override
    protected void onMachineDisassembled() {

    }

    @Override
    protected int getMaximumXSize() {
        return 0;
    }

    @Override
    protected int getMaximumZSize() {
        return 0;
    }

    @Override
    protected int getMaximumYSize() {
        return 0;
    }

    @Override
    protected void onAssimilate(MultiblockControllerBase assimilated) {

    }

    @Override
    protected void onAssimilated(MultiblockControllerBase assimilator) {

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

    @Override
    public int getFuelAmount() {
        return 0;
    }

    @Override
    public int getWasteAmount() {
        return 0;
    }

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public int getFuelRodCount() {
        return 0;
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
