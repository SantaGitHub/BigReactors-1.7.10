package erogenousbeef.bigreactors.common.interfaces;

import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.tile.IEnergyStorage;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;

@Optional.InterfaceList({
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2"),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2"),
        @Optional.Interface(iface = "ic2.api.tile.IEnergyStorage", modid = "IC2"),
        @Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = "CoFHCore"),
})
public interface IEnergyWrapper extends IStrictEnergyStorage, IEnergyHandler, IEnergySink, IEnergySource, IEnergyStorage, IStrictEnergyAcceptor, ICableOutputter, IInventory
{
    public EnumSet<ForgeDirection> getOutputtingSides();

    public EnumSet<ForgeDirection> getConsumingSides();

    public double getMaxOutput();
}