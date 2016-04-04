package erogenousbeef.bigreactors.utils;

import erogenousbeef.bigreactors.api.Coord4D;
import erogenousbeef.bigreactors.common.interfaces.IActiveState;
import ic2.api.energy.EnergyNet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public final class BRUtils {

    /**
     * Updates a block's light value and marks it for a render update.
     * @param world - world the block is in
     * @param x - x coordinate
     * @param y - y coordinate
     * @param z - z coordinate
     */
    public static void updateBlock(World world, int x, int y, int z)
    {
        Coord4D pos = new Coord4D(x, y, z);
        if(!(pos.getTileEntity(world) instanceof IActiveState) || ((IActiveState)pos.getTileEntity(world)).renderUpdate())
        {
            world.func_147479_m(pos.xCoord, pos.yCoord, pos.zCoord);
        }

        if(!(pos.getTileEntity(world) instanceof IActiveState) || ((IActiveState)pos.getTileEntity(world)).lightUpdate())
        {
            updateAllLightTypes(world, pos);
        }
    }

    /**
     * Updates all light types at the given coordinates.
     * @param world - the world to perform the lighting update in
     * @param pos - coordinates of the block to update
     */
    public static void updateAllLightTypes(World world, Coord4D pos)
    {
        world.updateLightByType(EnumSkyBlock.Block, pos.xCoord, pos.yCoord, pos.zCoord);
        world.updateLightByType(EnumSkyBlock.Sky, pos.xCoord, pos.yCoord, pos.zCoord);
    }

    /**
     * Marks the chunk this TileEntity is in as modified. Call this method to be sure NBT is written by the defined tile entity.
     * @param tileEntity - TileEntity to save
     */
    public static void saveChunk(TileEntity tileEntity)
    {
        if(tileEntity == null || tileEntity.isInvalid() || tileEntity.getWorldObj() == null)
        {
            return;
        }

        tileEntity.getWorldObj().markTileEntityChunkModified(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, tileEntity);
    }
}
