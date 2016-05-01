package erogenousbeef.bigreactors.common;


import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import erogenousbeef.bigreactors.common.data.StandardReactants;
import erogenousbeef.core.multiblock.MultiblockEventHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.event.world.ChunkDataEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import erogenousbeef.bigreactors.utils.StaticUtils;

public class BREventHandler {

	@SubscribeEvent
	public void chunkSave(ChunkDataEvent.Save saveEvent) {
		if(BigReactors.enableWorldGen) {
			NBTTagCompound saveData = saveEvent.getData();
			
			saveData.setInteger("BigReactorsWorldGen", BRConfig.WORLDGEN_VERSION);
			saveData.setInteger("BigReactorsUserWorldGen", BigReactors.userWorldGenVersion);
		}
	}
	
	@SubscribeEvent
	public void chunkLoad(ChunkDataEvent.Load loadEvent) {
		if(!BigReactors.enableWorldRegeneration || !BigReactors.enableWorldGen) {
			return;
		}

		NBTTagCompound loadData = loadEvent.getData();
		if(loadData.getInteger("BigReactorsWorldGen") == BRConfig.WORLDGEN_VERSION &&
				loadData.getInteger("BigReactorsUserWorldGen") == BigReactors.userWorldGenVersion) {
			return;
		}
		
		if(!StaticUtils.WorldGen.shouldGenerateInDimension(loadEvent.world.provider.dimensionId)) {
			return;
		}
		
		ChunkCoordIntPair coordPair = loadEvent.getChunk().getChunkCoordIntPair();
		BigReactors.tickHandler.addRegenChunk(loadEvent.world.provider.dimensionId, coordPair);
	}
	

        // Handle bucketing of reactor fluids
        @SubscribeEvent
    public void onBucketFill(FillBucketEvent e)
    {
        if(e.current.getItem() != Items.bucket)
        {
            return;
        }
        ItemStack filledBucket = fillBucket(e.world, e.target);
        if(filledBucket != null)
        {
            e.world.setBlockToAir(e.target.blockX, e.target.blockY, e.target.blockZ);
            e.result = filledBucket;
            e.setResult(Result.ALLOW);
        }
    }

    private ItemStack fillBucket(World world, MovingObjectPosition mop)
    {
        Block block = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
        if(block == BigReactors.fluidCyaniteStill) return new ItemStack(BigReactors.fluidCyaniteBucketItem);
        else if(block == BigReactors.fluidYelloriumStill) return new ItemStack(BigReactors.fluidYelloriumBucketItem);
        else return null;
    }

}
