package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.api.IHeatEntity;
import erogenousbeef.bigreactors.api.IRadiationModerator;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.interfaces.IBeefDebuggableTile;
import erogenousbeef.bigreactors.common.multiblock.MultiblockExchanger;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IMultiblockGuiHandler;
import erogenousbeef.core.common.CoordTriplet;
import erogenousbeef.core.multiblock.MultiblockControllerBase;
import erogenousbeef.core.multiblock.rectangular.RectangularMultiblockTileEntityBase;

public abstract class TileEntityExchangerPartBase extends RectangularMultiblockTileEntityBase implements IMultiblockGuiHandler,
                                                                                                            IHeatEntity,
                                                                                                            IRadiationModerator, IActivateable,
                                                                                                            IBeefDebuggableTile {

    public TileEntityExchangerPartBase() {
    }

    public MultiblockExchanger getExchangerController() { return (MultiblockExchanger) this.getMultiblockController(); }

    @Override
    public boolean canUpdate() { return false; }

    @Override
    public MultiblockControllerBase createNewMultiblock() {
        return new MultiblockExchanger(worldObj);
    }

    @Override
    public Class<? extends MultiblockControllerBase> getMultiblockControllerType() {
        return MultiblockExchanger.class;
    }

    @Override
    public void onMachineAssembled(MultiblockControllerBase controller) {
        super.onMachineAssembled(controller);

        // Re-render this block on the client
        if(worldObj.isRemote) {
            this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void onMachineBroken() {
        super.onMachineBroken();

        // Re-render this block on the client
        if(worldObj.isRemote) {
            this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    // IActivateable
    @Override
    public CoordTriplet getReferenceCoord() {
        if(isConnected()) {
            return getMultiblockController().getReferenceCoord();
        }
        else {
            return new CoordTriplet(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public boolean getActive() {
        if(isConnected()) {
            return getExchangerController().getActive();
        }
        else {
            return false;
        }
    }

    @Override
    public void setActive(boolean active) {
        if(isConnected()) {
            getExchangerController().setActive(active);
        }
        else {
            BRLog.error("Received a setActive command at %d, %d, %d, but not connected to a multiblock controller!", xCoord, yCoord, zCoord);
        }
    }

    @Override
    public String getDebugInfo() {
        MultiblockExchanger e = getExchangerController();
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().toString()).append("\n");
        if(e == null) {
            sb.append("Not attached to controller!");
            return sb.toString();
        }
        sb.append(e.getDebugInfo());
        return sb.toString();
    }
}
