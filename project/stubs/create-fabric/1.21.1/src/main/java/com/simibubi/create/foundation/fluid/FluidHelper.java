package com.simibubi.create.foundation.fluid;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public final class FluidHelper {
    private FluidHelper() {
    }

    public static boolean isWater(Fluid fluid) {
        return fluid == Fluids.WATER;
    }
}
