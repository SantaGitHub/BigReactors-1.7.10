package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityExchangerPartStandard;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockExchangerPart extends BlockContainer {

    public BlockExchangerPart(Material material) {
        super(material);

        setStepSound(soundTypeMetal);
        setHardness(2.0f);
        setBlockName("blockExchangerPart");
        this.setBlockTextureName(BigReactors.TEXTURE_NAME_PREFIX + "blockExchangerPart");
        setCreativeTab(BigReactors.TAB);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityExchangerPartStandard();
    }
}
