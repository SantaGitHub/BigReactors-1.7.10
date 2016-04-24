package erogenousbeef.bigreactors.common.machine;

import erogenousbeef.bigreactors.core.util.BlockCoord;

public interface IItemBuffer {

    void setBufferStacks(boolean bufferStacks);

    boolean isBufferStacks();

    BlockCoord getLocation();

}