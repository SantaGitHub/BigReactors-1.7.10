package erogenousbeef.bigreactors.common.tileentity.liquidizer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.machine.IPoweredTask;
import erogenousbeef.bigreactors.common.machine.PoweredTaskProgress;
import erogenousbeef.bigreactors.common.recipe.IMachineRecipe;
import erogenousbeef.bigreactors.common.recipe.MachineRecipeInput;
import erogenousbeef.bigreactors.core.network.MessageTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.FluidRegistry;

public class PacketLiquidizerProgress extends MessageTileEntity<TileEntityLiquidizer> implements IMessageHandler<PacketLiquidizerProgress, IMessage> {

    private float progress = 0;

    private int inputFluidId = -1;
    private int outputFluidId = -1;

    public PacketLiquidizerProgress() {

    }

    public PacketLiquidizerProgress(TileEntityLiquidizer liquidizer) {
        super(liquidizer);
        progress = liquidizer.getProgress();

        IPoweredTask task = liquidizer.getCurrentTask();
        if(task == null) {
            return;
        }

        for (MachineRecipeInput input : task.getInputs()) {
            if(input.fluid != null && input.fluid.getFluid() != null) {
                inputFluidId = input.fluid.getFluid().getID();
                break;
            }
        }

        IMachineRecipe rec = task.getRecipe();
        if (rec == null) {
            return;
        }
        for (IMachineRecipe.ResultStack res : rec.getCompletedResult(1.0f, task.getInputs())) {
            if(res.fluid != null && res.fluid.getFluid() != null) {
                outputFluidId = res.fluid.getFluid().getID();
            }
        }

    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeFloat(progress);
        buf.writeInt(inputFluidId);
        buf.writeInt(outputFluidId);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        progress = buf.readFloat();
        inputFluidId = buf.readInt();
        outputFluidId = buf.readInt();
    }

    @Override
    public IMessage onMessage(PacketLiquidizerProgress message, MessageContext ctx) {
        TileEntityLiquidizer tile = message.getTileEntity(BRLoader.proxy.getClientWorld());
        if(tile != null) {
            tile.currentTaskInputFluid = null;
            tile.currentTaskOutputFluid = null;
            if(message.progress < 0) {
                tile.setClientTask(null);
            } else {
                tile.setClientTask(new PoweredTaskProgress(message.progress));
                if(message.inputFluidId > 0) {
                    tile.currentTaskInputFluid = FluidRegistry.getFluid(message.inputFluidId);
                }
                if(message.outputFluidId > 0) {
                    tile.currentTaskOutputFluid = FluidRegistry.getFluid(message.outputFluidId);
                }
            }
        }
        return null;
    }
}