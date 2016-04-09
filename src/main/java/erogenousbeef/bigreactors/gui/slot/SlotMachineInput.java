package erogenousbeef.bigreactors.gui.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotMachineInput extends Slot {

    public SlotMachineInput(IInventory inventory, int slotIndex, int xPos,
                            int yPos) {
        super(inventory, slotIndex, xPos, yPos);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if(stack == null) { return false; }

        return true;
    }
}
