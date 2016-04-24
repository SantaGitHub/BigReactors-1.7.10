package erogenousbeef.bigreactors.common.machine;

import erogenousbeef.bigreactors.core.util.BlockCoord;
import net.minecraftforge.common.util.ForgeDirection;

public interface IIoConfigurable {

    public IoMode toggleIoModeForFace(ForgeDirection faceHit);

    public boolean supportsMode(ForgeDirection faceHit, IoMode mode);

    public void setIoMode(ForgeDirection faceHit, IoMode mode);

    public IoMode getIoMode(ForgeDirection face) ;

    public void clearAllIoModes();

    BlockCoord getLocation();

}