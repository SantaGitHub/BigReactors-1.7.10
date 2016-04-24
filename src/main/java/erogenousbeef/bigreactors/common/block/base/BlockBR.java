package erogenousbeef.bigreactors.common.block.base;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.tileentity.base.TileEntityBR;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockBR extends BlockBase {

    protected BlockBR(String name, Class<? extends TileEntityBR> teClass) {
        super(name, teClass);
        setCreativeTab(BigReactors.TAB);
    }

    protected BlockBR(String name, Class<? extends TileEntityBR> teClass, Material mat) {
        super(name, teClass, mat);
        setCreativeTab(BigReactors.TAB);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float par7, float par8, float par9) {

        /*if(shouldWrench(world, x, y, z, entityPlayer, side) && ToolUtil.breakBlockWithTool(this, world, x, y, z, entityPlayer)) {
            return true;
        }
        TileEntity te = world.getTileEntity(x, y, z);

        ITool tool = ToolUtil.getEquippedTool(entityPlayer);
        if(tool != null && !entityPlayer.isSneaking() && tool.canUse(entityPlayer.getCurrentEquippedItem(), entityPlayer, x, y, z)) {
            if(te instanceof TileEntityBasicMachine) {
                ((TileEntityBasicMachine) te).toggleIoModeForFace(ForgeDirection.getOrientation(side));
                world.markBlockForUpdate(x, y, z);
                return true;
            }
        }*/

        return super.onBlockActivated(world, x, y, z, entityPlayer, side, par7, par8, par9);
    }
}
