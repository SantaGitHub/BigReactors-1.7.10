package erogenousbeef.bigreactors.common.power;

import erogenousbeef.bigreactors.core.util.BlockCoord;

public interface IPowerContainer {

    int getEnergyStored();

    void setEnergyStored(int storedEnergy);

    BlockCoord getLocation();

}
