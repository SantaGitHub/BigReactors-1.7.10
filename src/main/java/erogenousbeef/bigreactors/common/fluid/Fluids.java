package erogenousbeef.bigreactors.common.fluid;

import erogenousbeef.bigreactors.common.BRLoader;
import net.minecraftforge.fluids.IFluidTank;

public class Fluids {

    public static final String NUTRIENT_DISTILLATION_NAME = "nutrient_distillation";

    public static final String HOOTCH_NAME = "hootch";

    public static final String ROCKET_FUEL_NAME = "rocket_fuel";

    public static final String FIRE_WATER_NAME = "fire_water";

    public static String toCapactityString(IFluidTank tank) {
        if(tank == null) {
            return "0/0 " + MB();
        }
        return tank.getFluidAmount() + "/" + tank.getCapacity() + " " + MB();
    }

    public static String MB() {
        return BRLoader.lang.localize("fluid.millibucket.abr");
    }
}
