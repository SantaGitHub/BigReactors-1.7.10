package erogenousbeef.bigreactors.common.tileentity.base;

import erogenousbeef.bigreactors.common.machine.PacketPowerStorage;
import erogenousbeef.bigreactors.common.power.ICapacitor;
import erogenousbeef.bigreactors.common.power.IInternalPoweredTile;
import erogenousbeef.bigreactors.common.power.PowerHandlerUtil;
import erogenousbeef.bigreactors.common.recipe.SlotDefinition;
import erogenousbeef.bigreactors.core.util.vecmath.VecmathUtil;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityPoweredBasicMachine extends TileEntityBasicMachine implements IInternalPoweredTile{

    // Power
    private ICapacitor capacitor;
    private int maxEnergyReceived = 100;
    private int maxEnergyStored = 10000;
    private int maxEnergyExtracted = 80;

    private int storedEnergyRF;
    protected float lastSyncPowerStored = -1;

    protected TileEntityPoweredBasicMachine(SlotDefinition slotDefinition) {
        super(slotDefinition);
    }

    @Override
    public void init() {
        super.init();
        onCapacitorTypeChange();
    }

    @Override
    public void doUpdate() {

        super.doUpdate();

        if (worldObj.isRemote) {
            return;
        }
        boolean powerChanged = (lastSyncPowerStored != storedEnergyRF && shouldDoWorkThisTick(5));
        if(powerChanged) {
            lastSyncPowerStored = storedEnergyRF;
            CommonPacketHandler.sendToAllAround(new PacketPowerStorage(this), this);
        }
    }

    //RF API Power

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return !isSideDisabled(from.ordinal());
    }

    @Override
    public int getMaxEnergyRecieved(ForgeDirection dir) {
        if(isSideDisabled(dir.ordinal())) {
            return 0;
        }
        return maxEnergyReceived;
    }

    @Override
    public int getMaxEnergyStored() {
        return maxEnergyStored;
    }

    @Override
    public void setEnergyStored(int stored) {
        storedEnergyRF = MathHelper.clamp_int(stored, 0, getMaxEnergyStored());
    }

    @Override
    public int getEnergyStored() {
        return storedEnergyRF;
    }

    //----- Common Machine Functions

    @Override
    public boolean displayPower() {
        return true;
    }

    public boolean hasPower() {
        return storedEnergyRF > 0;
    }

    public int getEnergyStoredScaled(int scale) {
        // NB: called on the client so can't use the power provider
        return VecmathUtil.clamp(Math.round(scale * ((float) storedEnergyRF / getMaxEnergyStored())), 0, scale);
    }

    protected void setCapacitor(ICapacitor capacitor) {
        this.capacitor = capacitor;
        //Force a check that the new value is in bounds
        setEnergyStored(getEnergyStored());
    }

    public void onCapacitorTypeChange() {}

    public int getPowerUsePerTick() {
        return maxEnergyExtracted;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack contents) {
        super.setInventorySlotContents(slot, contents);
    }

    @Override
    public ItemStack decrStackSize(int fromSlot, int amount) {
        ItemStack res = super.decrStackSize(fromSlot, amount);
        return res;
    }

    //--------- NBT

    /**
     * Read state common to both block and item
     */
    @Override
    public void readCommon(NBTTagCompound nbtRoot) {
        super.readCommon(nbtRoot);
        int energy;
        if(nbtRoot.hasKey("storedEnergy")) {
            float storedEnergyMJ = nbtRoot.getFloat("storedEnergy");
            energy = (int) (storedEnergyMJ * 10);
        } else {
            energy = nbtRoot.getInteger(PowerHandlerUtil.STORED_ENERGY_NBT_KEY);
        }
        setEnergyStored(energy);
    }

    /**
     * Write state common to both block and item
     */
    @Override
    public void writeCommon(NBTTagCompound nbtRoot) {
        super.writeCommon(nbtRoot);
        nbtRoot.setInteger(PowerHandlerUtil.STORED_ENERGY_NBT_KEY, storedEnergyRF);
    }
}
