package erogenousbeef.bigreactors.common.multiblock.tileentity;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.data.RadiationData;
import erogenousbeef.bigreactors.common.data.RadiationPacket;
import erogenousbeef.bigreactors.common.multiblock.block.BlockExchangerPart;
import erogenousbeef.core.common.CoordTriplet;
import erogenousbeef.core.multiblock.MultiblockControllerBase;
import erogenousbeef.core.multiblock.MultiblockValidationException;
import net.minecraft.entity.player.InventoryPlayer;

public class TileEntityExchangerPartStandard extends TileEntityExchangerPartBase {

    public TileEntityExchangerPartStandard() {
        super();
    }

    @Override
    public void isGoodForFrame() throws MultiblockValidationException {
        int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if(BlockExchangerPart.isCasing(metadata)) { return; }

        throw new MultiblockValidationException(String.format("%d, %d, %d - Only casing may be used as part of a heat exchanger's frame", xCoord, yCoord, zCoord)); //TODO: Localization
    }

    @Override
    public void isGoodForSides() throws MultiblockValidationException {

    }

    @Override
    public void isGoodForTop() throws MultiblockValidationException {

    }

    @Override
    public void isGoodForBottom() throws MultiblockValidationException {

    }

    @Override
    public void isGoodForInterior() throws MultiblockValidationException {
        throw new MultiblockValidationException(String.format("%d, %d, %d - This heat exchanger part may not be placed in the structure's interior", xCoord, yCoord, zCoord)); //TODO: Localization
    }

    @Override
    public void onMachineAssembled(MultiblockControllerBase multiblockController) {
        super.onMachineAssembled(multiblockController);
    }

    @Override
    public void onMachineBroken() {
        super.onMachineBroken();
    }

    @Override
    public void onMachineActivated() {
        // Re-render controllers on client
        if(this.worldObj.isRemote) {
            if(getBlockType() == BigReactors.blockExchangerPart) {
                int metadata = this.getBlockMetadata();
                if(BlockExchangerPart.isController(metadata)) {
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
            }
        }
    }

    @Override
    public void onMachineDeactivated() {
        // Re-render controllers on client
        if(this.worldObj.isRemote) {
            if(getBlockType() == BigReactors.blockExchangerPart) {
                int metadata = this.getBlockMetadata();
                if(BlockExchangerPart.isController(metadata)) {
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
            }
        }
    }

    @Override
    public CoordTriplet getReferenceCoord() {
        return null;
    }

    @Override
    public float getHeat() {
        return 0;
    }

    @Override
    public float getThermalConductivity() {
        return 0;
    }

    @Override
    public Object getContainer(InventoryPlayer inventoryPlayer) {
        return null;
    }

    @Override
    public Object getGuiElement(InventoryPlayer inventoryPlayer) {
        return null;
    }

    @Override
    public void moderateRadiation(RadiationData returnData, RadiationPacket radiation) {

    }

    /* IMultiblockGuiHandler
    /**
     * @return The Container object for use by the GUI. Null if there isn't any.

    @Override
    public Object getContainer(InventoryPlayer inventoryPlayer) {
        if(!this.isConnected()) {
            return null;
        }

        int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if(BlockReactorPart.isController(metadata)) {
            return new ContainerReactorController(this, inventoryPlayer.player);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Object getGuiElement(InventoryPlayer inventoryPlayer) {
        if(!this.isConnected()) {
            return null;
        }

        int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if(BlockReactorPart.isController(metadata)) {
            return new GuiReactorStatus(new ContainerReactorController(this, inventoryPlayer.player), this);
        }
        return null;
    }*/
}
