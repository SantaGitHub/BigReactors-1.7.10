package erogenousbeef.bigreactors.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erogenousbeef.bigreactors.GuiHandler;
import erogenousbeef.bigreactors.ModObject;
import erogenousbeef.bigreactors.common.block.base.BlockMachine;
import erogenousbeef.bigreactors.common.machine.IoMode;
import erogenousbeef.bigreactors.common.tileentity.liquidizer.*;
import erogenousbeef.bigreactors.net.CommonPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BlockLiquidizer extends BlockMachine<TileEntityLiquidizer> {

    public static BlockLiquidizer create() {
        CommonPacketHandler.INSTANCE.registerMessage(PacketTanks.class, PacketTanks.class, CommonPacketHandler.nextID(), Side.CLIENT);
        CommonPacketHandler.INSTANCE.registerMessage(PacketLiquidizerProgress.class, PacketLiquidizerProgress.class, CommonPacketHandler.nextID(), Side.CLIENT);
        CommonPacketHandler.INSTANCE.registerMessage(PacketDumpTank.class, PacketDumpTank.class, CommonPacketHandler.nextID(), Side.SERVER);
        BlockLiquidizer res = new BlockLiquidizer();
        res.init();
        return res;
    }

    public BlockLiquidizer() {
        super(ModObject.blockLiquidizer, TileEntityLiquidizer.class);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // The server needs the container as it manages the adding and removing of
        // items, which are then sent to the client for display
        TileEntity te = world.getTileEntity(x, y, z);
        if(te instanceof TileEntityLiquidizer) {
            return new ContainerLiquidizer(player.inventory, (TileEntityLiquidizer) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        return new GuiLiquidizer(player.inventory, (TileEntityLiquidizer) te);
    }

    @Override
    protected int getGuiId() {
        return GuiHandler.GUI_ID_LIQUIDIZER;
    }

    @Override
    protected String getMachineFrontIconKey(boolean active) {
        if(active) {
            return "bigreactors:LiquidizerFrontOn";
        }
        return "bigreactors:LiquidizerFront";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        // Spit some "steam" out the spout
        if (isActive(world, x, y, z)) {
            TileEntityLiquidizer te = (TileEntityLiquidizer) world.getTileEntity(x, y, z);
            float pX = x + 0.5f;
            float pY = y + 0.7f;
            float pZ = z + 0.5f;

            ForgeDirection dir = te.getFacingDir();
            pX += 0.6f * dir.offsetX;
            pZ += 0.6f * dir.offsetZ;

            double velX = ((rand.nextDouble() * 0.075) + 0.025) * dir.offsetX;
            double velZ = ((rand.nextDouble() * 0.075) + 0.025) * dir.offsetZ;

            int num = rand.nextInt(4) + 2;
            for (int k = 0; k < num; k++) {
                EffectRenderer er = Minecraft.getMinecraft().effectRenderer;
                EntitySmokeFX fx = new EntitySmokeFX(world, pX, pY, pZ, 1, 1, 1);
                fx.setRBGColorF(1 - (rand.nextFloat() * 0.2f), 1 - (rand.nextFloat() * 0.1f), 1 - (rand.nextFloat() * 0.2f));
                fx.setVelocity(velX, -0.06, velZ);
                er.addEffect(fx);
            }
        }
    }
}
