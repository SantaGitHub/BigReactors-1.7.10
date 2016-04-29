package erogenousbeef.bigreactors.common.machine;

import erogenousbeef.bigreactors.common.block.base.BlockMachine;
import erogenousbeef.bigreactors.common.tileentity.base.TileEntityBasicMachine;
import erogenousbeef.bigreactors.core.client.render.CustomCubeRenderer;
import erogenousbeef.bigreactors.core.client.render.CustomRenderBlocks;
import erogenousbeef.bigreactors.core.client.render.IRenderFace;
import erogenousbeef.bigreactors.core.client.render.RenderUtil;
import erogenousbeef.bigreactors.core.util.BlockCoord;
import erogenousbeef.bigreactors.core.util.ForgeDirectionOffsets;
import erogenousbeef.bigreactors.core.util.vecmath.Vector3d;
import erogenousbeef.bigreactors.core.util.vecmath.Vertex;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class OverlayRenderer implements IRenderFace {

    private static final CustomCubeRenderer ccr = CustomCubeRenderer.instance;
    private TileEntityBasicMachine te;

    public void setTile(TileEntityBasicMachine te) {
        this.te = te;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void renderFace(CustomRenderBlocks rb, ForgeDirection face, Block par1Block, double x, double y, double z, IIcon texture, List<Vertex> refVertices,
                           boolean translateToXyz) {

        if(te != null && par1Block instanceof BlockMachine) {
            BlockCoord bc = new BlockCoord(x, y, z);
            if(par1Block.isOpaqueCube()) {
                bc = bc.getLocation(face);
            }
            RenderUtil.setTesselatorBrightness(Minecraft.getMinecraft().theWorld, bc.x, bc.y, bc.z);
            Vector3d offset = ForgeDirectionOffsets.offsetScaled(face, 0.001);
            Tessellator.instance.addTranslation((float) offset.x, (float) offset.y, (float) offset.z);

            IoMode mode = te.getIoMode(face);
            IIcon tex = ((BlockMachine<TileEntityBasicMachine>) par1Block).getOverlayIconForMode(te, face, mode);
            if(tex != null) {
                ccr.getCustomRenderBlocks().setRenderBoundsFromBlock(par1Block);
                ccr.getCustomRenderBlocks().doDefaultRenderFace(face, par1Block, x, y, z, tex);
            }

            Tessellator.instance.addTranslation(-(float) offset.x, -(float) offset.y, -(float) offset.z);
        }
    }
}