package erogenousbeef.bigreactors.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erogenousbeef.bigreactors.GuiHandler;
import erogenousbeef.bigreactors.ModObject;
import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.BRLog;
import erogenousbeef.bigreactors.common.block.base.BlockMachine;
import erogenousbeef.bigreactors.common.machine.IoMode;
import erogenousbeef.bigreactors.common.recipe.SlotDefinition;
import erogenousbeef.bigreactors.common.tileentity.liquidizer.*;
import erogenousbeef.bigreactors.net.PacketHandler;
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

    public static int renderId;

    public static BlockLiquidizer create() {
        PacketHandler.INSTANCE.registerMessage(PacketTanks.class,PacketTanks.class,PacketHandler.nextID(), Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketLiquidizerProgress.class, PacketLiquidizerProgress.class, PacketHandler.nextID(), Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketDumpTank.class,PacketDumpTank.class,PacketHandler.nextID(), Side.SERVER);
        BlockLiquidizer res = new BlockLiquidizer();
        res.init();
        return res;
    }

    protected IIcon onIcon;
    protected IIcon topIcon;
    protected IIcon blockIconSingle;
    protected IIcon blockIconSingleOn;
    protected IIcon[][] overlays;

    public BlockLiquidizer() {
        super(ModObject.blockLiquidizer, TileEntityLiquidizer.class);
    }

    protected String getModelIconKey(boolean active) {
        return "bigreactors:liquidizerModel";
    }

    @Override
    public int getLightOpacity() {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void registerOverlayIcons(IIconRegister iIconRegister) {
        super.registerOverlayIcons(iIconRegister);

        overlays = new IIcon[2][IoMode.values().length];

        overlays[0][IoMode.PULL.ordinal()] = iIconRegister.registerIcon("bigreactors:overlays/pullSides");
        overlays[0][IoMode.PUSH.ordinal()] = iIconRegister.registerIcon("bigreactors:overlays/pushSides");
        overlays[0][IoMode.PUSH_PULL.ordinal()] = iIconRegister.registerIcon("bigreactors:overlays/pushPullSides");
        overlays[0][IoMode.DISABLED.ordinal()] = iIconRegister.registerIcon("bigreactors:overlays/disabledNoCenter");

        overlays[1][IoMode.PULL.ordinal()] = iIconRegister.registerIcon("ebigreactors:overlays/pullTopBottom");
        overlays[1][IoMode.PUSH.ordinal()] = iIconRegister.registerIcon("bigreactors:overlays/pushTopBottom");
        overlays[1][IoMode.PUSH_PULL.ordinal()] = iIconRegister.registerIcon("bigreactors:overlays/pushPullTopBottom");
        overlays[1][IoMode.DISABLED.ordinal()] = overlays[0][IoMode.DISABLED.ordinal()];
    }

    @Override
    public IIcon getOverlayIconForMode(TileEntityLiquidizer tile, ForgeDirection face, IoMode mode) {
        ForgeDirection side = tile.getFacingDir().getRotation(ForgeDirection.DOWN);
        if(mode == IoMode.DISABLED || face == side || face == side.getOpposite()) {
            return super.getOverlayIconForMode(tile, face, mode);
        } else {
            if(face == ForgeDirection.UP) {
                return overlays[1][mode.ordinal()];
            }
            return overlays[0][mode.ordinal()];
        }
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
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
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
        return true;
    }

    @Override
    protected String getMachineFrontIconKey(boolean active) {
        return getBackIconKey(active);
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
