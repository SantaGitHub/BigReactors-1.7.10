package erogenousbeef.bigreactors.world;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.feature.WorldGenMinable;

/**
 * This class handles simple ore generation.
 * @author Erogenous Beef
 *
 */
public class BRSimpleOreGenerator extends WorldGenMinable {

	protected Block blockToGenerate;
	protected int blockToGenerateMetadata;
	protected Block blockToReplace;
	
	protected int minClustersPerChunk;
	protected int maxClustersPerChunk;
	protected int minY;
	protected int maxY;
	
	// For now, we never generate in those dimensions.
	protected Set<Integer> dimensionBlacklist;
	
	public BRSimpleOreGenerator(Block blockToGenerate, int blockMetadata, Block blockToReplace, int clustersPerChunk, int maxY, int maxOrePerCluster) {
		super(blockToGenerate, blockMetadata, maxOrePerCluster, blockToReplace);
		this.minClustersPerChunk = maxClustersPerChunk = clustersPerChunk;
		this.minY = 0;
		this.maxY = maxY;
		
		// this is only used for equality checks
		this.blockToGenerate = blockToGenerate;
		this.blockToGenerateMetadata = blockMetadata;
		this.blockToReplace = blockToReplace;

		dimensionBlacklist = new CopyOnWriteArraySet<Integer>();
	}
	
	public BRSimpleOreGenerator(Block blockToGenerate, int blockMetadata, Block blockToReplace, int minClustersPerChunk, int maxClustersPerChunk, int minY, int maxY, int maxOrePerCluster) {
		this(blockToGenerate, blockMetadata, blockToReplace, maxClustersPerChunk, maxY, maxOrePerCluster);
		this.minClustersPerChunk = minClustersPerChunk;
		this.minY = minY;
	}
	
	public void blacklistDimension(int dimensionId) {
		dimensionBlacklist.add(dimensionId);
	}
	
	/**
	 * Call to generate in the given chunk in the given world.
	 * Performs no validation as to whether this generator should run (per user settings).
	 * @param world
	 * @param random
	 * @param chunkX
	 * @param chunkZ
	 */
	public void generateChunk(World world, Random random, int chunkX, int chunkZ) {
		int clustersToGen = minClustersPerChunk;
		if(maxClustersPerChunk > minClustersPerChunk) {
			clustersToGen += random.nextInt(maxClustersPerChunk - minClustersPerChunk);
		}

		int chunkBaseX = chunkX << 4;
		int chunkBaseZ = chunkZ << 4;
		int y;

		for(int i = 0; i < clustersToGen; i++) {
			y = this.minY + random.nextInt(this.maxY - this.minY);
			
			generate(world, random, chunkBaseX, y, chunkBaseZ);
		}
	}
	
	/**
	 * Call to discover if this generator WISHES to generate in this world.
	 * @param world
	 * @return
	 */
	public boolean shouldGenerateInWorld(World world) {
		IChunkProvider chunkProvider = world.getChunkProvider();
		return ! ( (chunkProvider instanceof ChunkProviderHell) || (chunkProvider instanceof ChunkProviderEnd) || (dimensionBlacklist.contains(world.provider.dimensionId)) );
	}

	@Override
	public boolean equals(Object o) {
		if( o == null || !(o instanceof BRSimpleOreGenerator) ) return false;
		BRSimpleOreGenerator other = (BRSimpleOreGenerator)o;
		return this.blockToGenerate == other.blockToGenerate && this.blockToGenerateMetadata == other.blockToGenerateMetadata;
	}
}
