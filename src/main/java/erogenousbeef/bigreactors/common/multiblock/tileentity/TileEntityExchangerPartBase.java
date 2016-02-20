package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.multiblock.MultiblockExchanger;
import erogenousbeef.core.multiblock.MultiblockControllerBase;
import erogenousbeef.core.multiblock.rectangular.RectangularMultiblockTileEntityBase;

public abstract class TileEntityExchangerPartBase extends RectangularMultiblockTileEntityBase {

    @Override
    public MultiblockControllerBase createNewMultiblock() {
        return new MultiblockExchanger(worldObj);
    }

    @Override
    public Class<? extends MultiblockControllerBase> getMultiblockControllerType() {
        return MultiblockExchanger.class;
    }

    public MultiblockExchanger getExchangerController() { return (MultiblockExchanger) this.getMultiblockController(); }
}
