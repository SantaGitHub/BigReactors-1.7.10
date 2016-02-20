package erogenousbeef.bigreactors.common.multiblock.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.MultiblockExchanger;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityExchangerPartStandard;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPart;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class BlockExchangerPart extends BlockContainer {

    public static final int METADATA_CASING = 0;
    public static final int METADATA_CONTROLLER = 1;

    private static final String[] _subBlocks = new String[] { "housing", "controller",};

    private IIcon[] _icons = new IIcon[_subBlocks.length];

    public static boolean isCasing(int metadata) { return metadata == METADATA_CASING; }
    public static boolean isController(int metadata) { return metadata == METADATA_CONTROLLER; }

    public BlockExchangerPart(Material material) {
        super(material);

        setStepSound(soundTypeMetal);
        setHardness(2.0f);
        setBlockName("blockExchangerPart");
        this.setBlockTextureName(BigReactors.TEXTURE_NAME_PREFIX + "blockExchangerPart");
        setCreativeTab(BigReactors.TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        // Base icons
        for(int i = 0; i < _subBlocks.length; ++i) {
            _icons[i] = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName() + "." + _subBlocks[i]);
        }

        this.blockIcon = _icons[0];
    }

    public ItemStack getExchangerCasingItemStack() {
        return new ItemStack(this, 1, METADATA_CASING);
    }

    public ItemStack getExchangerControllerItemStack() {
        return new ItemStack(this, 1, METADATA_CONTROLLER);
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        IIcon icon = null;
        int metadata = blockAccess.getBlockMetadata(x,y,z);

        if(metadata == METADATA_CASING) {
            return getIcon(side, metadata);
        }

        switch(metadata) {
            case METADATA_CONTROLLER:
                icon = getControllerIcon(blockAccess, x, y, z, side);
                break;
        }

        return icon != null ? icon : getIcon(side, metadata);
    }

    private IIcon getControllerIcon(IBlockAccess blockAccess, int x, int y,
                                    int z, int side) {
        TileEntity te = blockAccess.getTileEntity(x, y, z);
        if(te instanceof TileEntityExchangerPartStandard) {
            TileEntityExchangerPartStandard controller = (TileEntityExchangerPartStandard)te;
            MultiblockExchanger exchanger = controller.getExchangerController();

            if(exchanger == null || !exchanger.isAssembled()) {
                return _icons[METADATA_CONTROLLER];
            }
            else if(!isOutwardsSide(controller, side)) {
                return blockIcon;
            }
        }
        return blockIcon;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {

        return new TileEntityExchangerPartStandard();
    }

    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for(int i = 0; i < _subBlocks.length; i++) {
            par3List.add(new ItemStack(this, 1, i));
        }
    }

    private boolean isOutwardsSide(TileEntityExchangerPartStandard part, int side) {
        ForgeDirection outDir = part.getOutwardsDir();
        return outDir.ordinal() == side;
    }


}
