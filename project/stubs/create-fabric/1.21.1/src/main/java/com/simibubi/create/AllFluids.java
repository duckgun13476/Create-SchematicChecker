package com.simibubi.create;

import net.minecraft.world.level.material.Fluid;

public final class AllFluids {
    public static final FluidEntry HONEY = new FluidEntry();

    private AllFluids() {
    }

    public static final class FluidEntry {
        public boolean is(Fluid fluid) {
            return false;
        }
    }
}
