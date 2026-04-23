package com.simibubi.create.infrastructure.fabric.transfer.fluid;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidStack {
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);

    private final Fluid fluid;
    private final long amount;

    public FluidStack(Fluid fluid, long amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public boolean isEmpty() {
        return fluid == Fluids.EMPTY || amount <= 0;
    }
}
