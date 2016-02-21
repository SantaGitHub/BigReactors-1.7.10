package erogenousbeef.bigreactors.common.multiblock.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erogenousbeef.bigreactors.common.BRLoader;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.MultiblockExchanger;
import erogenousbeef.bigreactors.common.multiblock.interfaces.INeighborUpdatableEntity;
import erogenousbeef.bigreactors.common.multiblock.tileentity.*;
import erogenousbeef.bigreactors.utils.StaticUtils;
import erogenousbeef.core.common.CoordTriplet;
import erogenousbeef.core.multiblock.IMultiblockPart;
import erogenousbeef.core.multiblock.MultiblockControllerBase;
import erogenousbeef.core.multiblock.rectangular.PartPosition;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class BlockExchangerPart extends BlockContainer {

    //Sub Blocks
    public static final int METADATA_CASING = 0;
    public static final int METADATA_CONTROLLER = 1;

    //States
    private static final int CONTROLLER_OFF = 0;
    private static final int CONTROLLER_IDLE = 1;
    private static final int CONTROLLER_ACTIVE = 2;

    private static final String[] _subBlocks = new String[] { "casing", "controller"};

    private static String[][] _states = new String[][] {
            {"default", "face", "corner", "eastwest", "northsouth", "vertical"}, // Casing
            {"off", "idle", "active"} 		// Controller
    };
    private IIcon[][] _icons = new IIcon[_states.length][];

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
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        IIcon icon = null;
        int metadata = blockAccess.getBlockMetadata(x,y,z);

        switch(metadata) {
            case METADATA_CASING:
                icon = getCasingIcon(blockAccess, x, y, z, side);
                break;
            case METADATA_CONTROLLER:
                icon = getControllerIcon(blockAccess, x, y, z, side);
                break;
        }

        return icon != null ? icon : getIcon(side, metadata);
    }

    @Override
    public IIcon getIcon(int side, int metadata)
    {
        if(metadata == METADATA_CASING) {
            if(side == 1) {
                return _icons[metadata][0];
            }
        }
        else {
            if(side > 1 && (metadata >= 0 && metadata < _icons.length)) {
                return _icons[metadata][0];
            }
        }
        return blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        String prefix = BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName() + ".";

        for(int metadata = 0; metadata < _states.length; ++metadata) {
            String[] blockStates = _states[metadata];
            _icons[metadata] = new IIcon[blockStates.length];

            for(int state = 0; state < blockStates.length; state++) {
                _icons[metadata][state] = par1IconRegister.registerIcon(prefix + _subBlocks[metadata] + "." + blockStates[state]);
            }
        }

        this.blockIcon = par1IconRegister.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName());
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        switch(metadata) {
            default:
                return new TileEntityExchangerPartStandard();
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        TileEntity te = StaticUtils.TE.getTileEntityUnsafe(world, x, y, z);

        // Signal power taps when their neighbors change, etc.
        if(te instanceof INeighborUpdatableEntity) {
            ((INeighborUpdatableEntity)te).onNeighborBlockChange(world, x, y, z, neighborBlock);
        }
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int neighborX, int neighborY, int neighborZ) {
        TileEntity te = StaticUtils.TE.getTileEntityUnsafe(world, x, y, z);

        // Signal power taps when their neighbors change, etc.
        if(te instanceof INeighborUpdatableEntity) {
            ((INeighborUpdatableEntity)te).onNeighborTileChange(world, x, y, z, neighborX, neighborY, neighborZ);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
        if(player.isSneaking()) {
            return false;
        }

        int metadata = world.getBlockMetadata(x, y, z);
        TileEntity te = world.getTileEntity(x, y, z);
        IMultiblockPart part = null;
        MultiblockControllerBase controller = null;

        if(te instanceof IMultiblockPart) {
            part = (IMultiblockPart)te;
            controller = part.getMultiblockController();
        }

        if(isCasing(metadata)) {
            // If the player's hands are empty and they rightclick on a multiblock, they get a
            // multiblock-debugging message if the machine is not assembled.
            if(player.getCurrentEquippedItem() == null) {
                if(controller != null) {
                    Exception e = controller.getLastValidationException();
                    if(e != null) {
                        player.addChatMessage(new ChatComponentText(e.getMessage()));
                        return true;
                    }
                }
                else {
                    player.addChatMessage(new ChatComponentText("Block is not connected to a reactor. This could be due to lag, or a bug. If the problem persists, try breaking and re-placing the block.")); //TODO Localize
                    return true;
                }
            }

            // If nonempty, or there was no error, just fall through
            return false;
        }

        /* Do toggly fiddly things for access/coolant ports
        if(!world.isRemote && (isAccessPort(metadata) || isCoolantPort(metadata))) {
            if(StaticUtils.Inventory.isPlayerHoldingWrench(player)) {
                if(te instanceof TileEntityReactorCoolantPort) {
                    TileEntityReactorCoolantPort cp = (TileEntityReactorCoolantPort)te;
                    cp.setInlet(!cp.isInlet(), true);
                    return true;
                }
                else if(te instanceof TileEntityReactorAccessPort) {
                    TileEntityReactorAccessPort cp = (TileEntityReactorAccessPort)te;
                    cp.setInlet(!cp.isInlet());
                    return true;
                }
            }
            else if(isCoolantPort(metadata)) {
                return false;
            }
        }*/

        // Don't open the controller GUI if the reactor isn't assembled
        if(isController(metadata) && (controller == null || !controller.isAssembled())) { return false; }

        if(!world.isRemote) {
            player.openGui(BRLoader.instance, 0, world, x, y, z);
        }
        return true;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return true;
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata;
    }

    public ItemStack getExchangerCasingItemStack() {
        return new ItemStack(this, 1, METADATA_CASING);
    }

    public ItemStack getExchangerControllerItemStack() {
        return new ItemStack(this, 1, METADATA_CONTROLLER);
    }

    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for(int metadata = 0; metadata < _subBlocks.length; metadata++) {
            par3List.add(new ItemStack(this, 1, metadata));
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        // Drop everything inside inventory blocks
        TileEntity te = world.getTileEntity(x, y, z);
        if(te instanceof IInventory)
        {
            IInventory inventory = ((IInventory)te);
            inv:		for(int i = 0; i < inventory.getSizeInventory(); i++)
            {
                ItemStack itemstack = inventory.getStackInSlot(i);
                if(itemstack == null)
                {
                    continue;
                }
                float xOffset = world.rand.nextFloat() * 0.8F + 0.1F;
                float yOffset = world.rand.nextFloat() * 0.8F + 0.1F;
                float zOffset = world.rand.nextFloat() * 0.8F + 0.1F;
                do
                {
                    if(itemstack.stackSize <= 0)
                    {
                        continue inv;
                    }
                    int amountToDrop = world.rand.nextInt(21) + 10;
                    if(amountToDrop > itemstack.stackSize)
                    {
                        amountToDrop = itemstack.stackSize;
                    }
                    itemstack.stackSize -= amountToDrop;
                    EntityItem entityitem = new EntityItem(world, (float)x + xOffset, (float)y + yOffset, (float)z + zOffset, new ItemStack(itemstack.getItem(), amountToDrop, itemstack.getItemDamage()));
                    if(itemstack.getTagCompound() != null)
                    {
                        entityitem.getEntityItem().setTagCompound(itemstack.getTagCompound());
                    }
                    float motionMultiplier = 0.05F;
                    entityitem.motionX = (float)world.rand.nextGaussian() * motionMultiplier;
                    entityitem.motionY = (float)world.rand.nextGaussian() * motionMultiplier + 0.2F;
                    entityitem.motionZ = (float)world.rand.nextGaussian() * motionMultiplier;
                    world.spawnEntityInWorld(entityitem);
                } while(true);
            }
        }

        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    private IIcon getControllerIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity te = blockAccess.getTileEntity(x, y, z);
        if(te instanceof TileEntityExchangerPartStandard) {
            TileEntityExchangerPartStandard controller = (TileEntityExchangerPartStandard) te;
            MultiblockExchanger exchanger = controller.getExchangerController();

            if(exchanger == null || !exchanger.isAssembled()) {
                return _icons[METADATA_CONTROLLER][CONTROLLER_OFF];
            }
            else if(!isOutwardsSide(controller, side)) {
                return blockIcon;
            }
            else if(exchanger.getActive()) {
                return _icons[METADATA_CONTROLLER][CONTROLLER_ACTIVE];
            }
            else {
                return _icons[METADATA_CONTROLLER][CONTROLLER_IDLE];
            }
        }
        return blockIcon;
    }

    private static final int DEFAULT = 0;
    private static final int FACE = 1;
    private static final int CORNER = 2;
    private static final int EASTWEST = 3;
    private static final int NORTHSOUTH = 4;
    private static final int VERTICAL = 5;

    private IIcon getCasingIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntity te = blockAccess.getTileEntity(x, y, z);
        if(te instanceof TileEntityExchangerPartStandard) {
            TileEntityExchangerPartStandard part = (TileEntityExchangerPartStandard)te;
            PartPosition position = part.getPartPosition();
            MultiblockExchanger exchanger = part.getExchangerController();
            if(exchanger == null || !exchanger.isAssembled()) {
                return _icons[METADATA_CASING][DEFAULT];
            }

            switch(position) {
                case BottomFace:
                case TopFace:
                case EastFace:
                case WestFace:
                case NorthFace:
                case SouthFace:
                    return _icons[METADATA_CASING][FACE];
                case FrameCorner:
                    return _icons[METADATA_CASING][CORNER];
                case Frame:
                    return getCasingEdgeIcon(part, exchanger, side);
                case Interior:
                case Unknown:
                default:
                    return _icons[METADATA_CASING][DEFAULT];
            }
        }
        return _icons[METADATA_CASING][DEFAULT];
    }

    private IIcon getCasingEdgeIcon(TileEntityExchangerPartStandard part, MultiblockExchanger exchanger, int side) {
        if(exchanger == null || !exchanger.isAssembled()) { return _icons[METADATA_CASING][DEFAULT]; }

        CoordTriplet minCoord = exchanger.getMinimumCoord();
        CoordTriplet maxCoord = exchanger.getMaximumCoord();

        boolean xExtreme, yExtreme, zExtreme;
        xExtreme = yExtreme = zExtreme = false;

        if(part.xCoord == minCoord.x || part.xCoord == maxCoord.x) { xExtreme = true; }
        if(part.yCoord == minCoord.y || part.yCoord == maxCoord.y) { yExtreme = true; }
        if(part.zCoord == minCoord.z || part.zCoord == maxCoord.z) { zExtreme = true; }

        int idx = DEFAULT;
        if(!xExtreme) {
            if(side < 4) { idx = EASTWEST; }
        }
        else if(!yExtreme) {
            if(side > 1) {
                idx = VERTICAL;
            }
        }
        else { // !zExtreme
            if(side < 2) {
                idx = NORTHSOUTH;
            }
            else if(side > 3) {
                idx = EASTWEST;
            }
        }
        return _icons[METADATA_CASING][idx];
    }

    /**
     * @param part The part whose sides we're checking
     * @param side The side to compare to the part
     * @return True if `side` is the outwards-facing face of `part`
     */
    private boolean isOutwardsSide(TileEntityExchangerPartStandard part, int side) {
        ForgeDirection outDir = part.getOutwardsDir();
        return outDir.ordinal() == side;
    }
}
