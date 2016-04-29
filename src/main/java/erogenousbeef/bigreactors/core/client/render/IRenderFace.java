package erogenousbeef.bigreactors.core.client.render;

import erogenousbeef.bigreactors.core.util.vecmath.Vertex;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public interface IRenderFace {

    void renderFace(CustomRenderBlocks rb, ForgeDirection face, Block par1Block, double x, double y, double z, IIcon texture, List<Vertex> refVertices,
                    boolean translateToXyz);

}
