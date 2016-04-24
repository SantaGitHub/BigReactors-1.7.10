package erogenousbeef.bigreactors.common.tileentity.base;

import erogenousbeef.bigreactors.common.power.IInternalPowerReceiver;
import erogenousbeef.bigreactors.common.power.PowerHandlerUtil;
import erogenousbeef.bigreactors.common.recipe.SlotDefinition;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityPowerConsumer extends TileEntityPoweredBasicMachine implements IInternalPowerReceiver {

    public TileEntityPowerConsumer(SlotDefinition slotDefinition) {
        super(slotDefinition);
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if(isSideDisabled(from.ordinal())) {
            return 0;
        }
        return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return getMaxEnergyStored();
    }
}
