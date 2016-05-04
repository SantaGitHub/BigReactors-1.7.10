package erogenousbeef.bigreactors.client.container;

import erogenousbeef.bigreactors.common.recipe.SlotDefinition;
import erogenousbeef.bigreactors.common.tileentity.base.TileEntityBasicMachine;
import erogenousbeef.bigreactors.core.IProgressTile;
import erogenousbeef.bigreactors.core.util.Util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.*;

public abstract class AbstractMachineContainer <T extends TileEntityBasicMachine> extends ContainerBR<T> {

    public AbstractMachineContainer(InventoryPlayer playerInv, T te) {
        super(playerInv, te);
    }

    @Override
    protected void addSlots(InventoryPlayer playerInv) {
        addMachineSlots(playerInv);
    }

    @Override
    public Point getPlayerInventoryOffset() {
        return new Point(8,84);
    }

    /**
     * ATTN: Do not access any non-static field from this method. Your object has
     * not yet been constructed when it is called!
     */
    protected abstract void addMachineSlots(InventoryPlayer playerInv);

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotNumber) {
        SlotDefinition slotDef = getInv().getSlotDefinition();

        ItemStack copystack = null;
        Slot slot = (Slot) inventorySlots.get(slotNumber);
        if(slot != null && slot.getHasStack()) {
            ItemStack origStack = slot.getStack();
            copystack = origStack.copy();

            boolean merged = false;
            for(SlotRange range : getTargetSlotsForTransfer(slotNumber, slot)) {
                if(mergeItemStack(origStack, range.start, range.end, range.reverse)) {
                    merged = true;
                    break;
                }
            }

            if(!merged) {
                return null;
            }

            if (slotDef.isOutputSlot(slot.getSlotIndex())) {
                slot.onSlotChange(origStack, copystack);
            }

            if(origStack.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }

            if(origStack.stackSize == copystack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(entityPlayer, origStack);
        }

        return copystack;
    }

    protected int getIndexOfFirstPlayerInvSlot(SlotDefinition slotDef) {
        return slotDef.getNumSlots();
    }

    protected SlotRange getPlayerInventorySlotRange(boolean reverse) {
        return new SlotRange(startPlayerSlot, endHotBarSlot, reverse);
    }

    protected SlotRange getPlayerInventoryWithoutHotbarSlotRange() {
        return new SlotRange(startPlayerSlot, endPlayerSlot, false);
    }

    protected SlotRange getPlayerHotbarSlotRange() {
        return new SlotRange(startHotBarSlot, endHotBarSlot, false);
    }

    protected void addInventorySlotRange(java.util.List<SlotRange> res, int start, int end) {
        for (int i = start; i < end; i++) {
            int slotNumber = getSlotFromInventory(getInv(), i).slotNumber;
            res.add(new SlotRange(slotNumber, slotNumber + 1, false));
        }
    }

    protected void addInputSlotRanges(java.util.List<SlotRange> res) {
        SlotDefinition slotDef = getInv().getSlotDefinition();
        if(slotDef.getNumInputSlots() > 0) {
            addInventorySlotRange(res, slotDef.getMinInputSlot(), slotDef.getMaxInputSlot() + 1);
        }
    }

    protected void addUpgradeSlotRanges(java.util.List<SlotRange> res) {
        SlotDefinition slotDef = getInv().getSlotDefinition();
        if(slotDef.getNumUpgradeSlots() > 0) {
            addInventorySlotRange(res, slotDef.getMinUpgradeSlot(), slotDef.getMaxUpgradeSlot() + 1);
        }
    }

    protected void addPlayerSlotRanges(java.util.List<SlotRange> res, int slotIndex) {
        if (slotIndex < endPlayerSlot) {
            res.add(getPlayerHotbarSlotRange());
        }
        if (slotIndex >= startHotBarSlot && slotIndex < endHotBarSlot) {
            res.add(getPlayerInventoryWithoutHotbarSlotRange());
        }
    }

    protected java.util.List<SlotRange> getTargetSlotsForTransfer(int slotNumber, Slot slot) {
        if (slot.inventory == getInv()) {
            SlotDefinition slotDef = getInv().getSlotDefinition();
            if (slotDef.isInputSlot(slot.getSlotIndex()) || slotDef.isUpgradeSlot(slot.getSlotIndex())) {
                return Collections.singletonList(getPlayerInventorySlotRange(false));
            }
            if (slotDef.isOutputSlot(slot.getSlotIndex())) {
                return Collections.singletonList(getPlayerInventorySlotRange(true));
            }
        } else if (slotNumber >= startPlayerSlot) {
            java.util.List<SlotRange> res = new ArrayList<SlotRange>();
            addInputSlotRanges(res);
            addUpgradeSlotRanges(res);
            addPlayerSlotRanges(res, slotNumber);
            return res;
        }
        return Collections.emptyList();
    }

    protected int getProgressScaled(int scale) {
        if(getInv() instanceof IProgressTile) {
            Util.getProgressScaled(scale, (IProgressTile) getInv());
        }
        return 0;
    }

    public static class SlotRange {
        final int start;
        final int end;
        final boolean reverse;

        public SlotRange(int start, int end, boolean reverse) {
            this.start = start;
            this.end = end;
            this.reverse = reverse;
        }
    }
}
