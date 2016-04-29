package erogenousbeef.bigreactors.common.machine;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import erogenousbeef.bigreactors.common.block.base.BlockMachine;
import erogenousbeef.bigreactors.common.tileentity.base.TileEntityBasicMachine;
import erogenousbeef.bigreactors.core.client.render.*;
import erogenousbeef.bigreactors.core.util.vecmath.Vertex;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class BlockMachineRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {

    private OverlayRenderer overlayRenderer = new OverlayRenderer() {
        @Override
        public void renderFace(CustomRenderBlocks rb, ForgeDirection face, Block par1Block, double x, double y, double z, IIcon texture, List<Vertex> refVertices,
                               boolean translateToXyz) {

            ccr.getCustomRenderBlocks().doDefaultRenderFace(face, par1Block, x, y, z, texture);
            super.renderFace(rb, face, par1Block, x, y, z, texture, refVertices, translateToXyz);
        }
    };

    private CustomCubeRenderer ccr = new CustomCubeRenderer();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

        renderInventoryBlock(Block.getBlockFromItem(item.getItem()), item.getItemDamage(), 0, (RenderBlocks) data[0]);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

        BoundingBox bb = BoundingBox.UNIT_CUBE;
        bb = bb.translate(0, -0.1f, 0);

        Tessellator.instance.startDrawingQuads();

        IIcon[] textures = RenderUtil.getBlockTextures(block, metadata);

        float[] brightnessPerSide = new float[6];
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            brightnessPerSide[dir.ordinal()] = Math.max(RenderUtil.getColorMultiplierForFace(dir) + 0.1f, 1f);
        }
        CubeRenderer.render(bb, textures, null, brightnessPerSide);
        Tessellator.instance.draw();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        TileEntity te = world.getTileEntity(x, y, z);
        TileEntityBasicMachine machine = null;

        if(te instanceof TileEntityBasicMachine) {
            machine = (TileEntityBasicMachine) te;
        }

        overlayRenderer.setTile(machine);

        ccr.setOverrideTexture(renderer.overrideBlockTexture);
        ccr.renderBlock(world, block, x, y, z, overlayRenderer);
        ccr.setOverrideTexture(null);

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return BlockMachine.renderId;
    }
}
