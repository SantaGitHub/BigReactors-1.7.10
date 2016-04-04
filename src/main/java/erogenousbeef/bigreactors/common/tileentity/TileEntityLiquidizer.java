package erogenousbeef.bigreactors.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.client.gui.GuiLiquidizer;
import erogenousbeef.bigreactors.common.recipe.RecipeHandler;
import erogenousbeef.bigreactors.common.recipe.inputs.AdvancedMachineFluidInput;
import erogenousbeef.bigreactors.common.recipe.machines.AdvancedMachineRecipe;
import erogenousbeef.bigreactors.common.tileentity.base.TileEntityPoweredInventoryFluid;
import erogenousbeef.bigreactors.gui.container.ContainerLiquidizer;
import erogenousbeef.bigreactors.utils.StaticUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class TileEntityLiquidizer extends TileEntityPoweredInventoryFluid {

    public static final int SLOT_INLET_1 = 0;
    public static final int SLOT_INLET_2 = 1;
    public static final int NUM_SLOTS = 2;

    public static final int FLUIDTANK_IN = 0;
    public static final int FLUIDTANK_OUT = 1;
    public static final int NUM_TANKS = 2;

    protected static final int FLUID_CONSUMED = FluidContainerRegistry.BUCKET_VOLUME * 1;
    protected static final int COOLANT_AMOUNT = FluidContainerRegistry.BUCKET_VOLUME / 2;
    protected static final int INGOTS_CONSUMED = 1;

    protected static final int MAX_ENERGY_STORE = 10000;
    protected static final int CYCLE_ENERGY_COST = 2000;

    public TileEntityLiquidizer() {
        super();

        // Do not transmit energy from the internal buffer.
        m_ProvidesEnergy = false;
    }

    @Override
    public int getSizeInventory() {
        return NUM_SLOTS;
    }

    @Override
    protected int getMaxEnergyStored() {
        return MAX_ENERGY_STORE;
    }

    @Override
    public int getCycleEnergyCost() {
        return CYCLE_ENERGY_COST;
    }

    @Override
    public int getCycleLength() {
        return 200; // 10 seconds / 20tps;
    }

    @Override
    public boolean canBeginCycle() {

        return true;
    }

    @Override
    public String getInventoryName() {
        return "Liquidizer";
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {

        return false;
    }

    @Override
    protected int getExposedInventorySlotFromSide(int side) {
        return 0;
    }



    @Override
    public void onPoweredCycleBegin() {

    }

    @Override
    public void onPoweredCycleEnd() {

    }

    @Override
    public int getNumTanks() {
        return NUM_TANKS;
    }

    @Override
    public int getTankSize(int tankIndex) {
        return FluidContainerRegistry.BUCKET_VOLUME * 5;
    }

    @Override
    public int getExposedTankFromSide(int side) {
        return 0;
    }

    @Override
    protected boolean isFluidValidForTank(int tankIdx, FluidStack type) {
        if(type == null) { return false; }
        return type.getFluid().getID() == FluidRegistry.getFluid("water").getID();
    }

    /// BeefGUI
    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getGUI(EntityPlayer player) {
        return new GuiLiquidizer(getContainer(player), this);
    }

    @Override
    public Container getContainer(EntityPlayer player) {
        return new ContainerLiquidizer(this, player);
    }

    @Override
    protected int getDefaultTankForFluid(Fluid fluid) {
        if(fluid.getName() == "water")
            return 0;
        else
            return FLUIDTANK_NONE;
    }

    @Override
    public IIcon getIconForSide(int referenceSide) {
        return null;
    }

    @Override
    public int getNumConfig(int i) {
        return 0;
    }

    public Map getRecipes()
    {
        return RecipeHandler.Recipe.LIQUIDIZER.get();
    }
}
