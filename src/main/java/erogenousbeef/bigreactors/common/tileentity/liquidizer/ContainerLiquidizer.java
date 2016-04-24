package erogenousbeef.bigreactors.common.tileentity.liquidizer;

import erogenousbeef.bigreactors.client.container.AbstractMachineContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerLiquidizer extends AbstractMachineContainer<TileEntityLiquidizer> {

    public ContainerLiquidizer(InventoryPlayer playerInv, TileEntityLiquidizer te) {
        super(playerInv, te);
    }

    @Override
    protected void addMachineSlots(InventoryPlayer playerInv) {
        addSlotToContainer(new Slot(getInv(), 0, 56, 12) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(0, itemStack);
            }
        });
        addSlotToContainer(new Slot(getInv(), 1, 105, 12) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return getInv().isItemValidForSlot(1, itemStack);
            }
        });
    }
}
