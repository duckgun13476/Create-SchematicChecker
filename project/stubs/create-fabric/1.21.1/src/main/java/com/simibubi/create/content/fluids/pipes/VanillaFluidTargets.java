package com.simibubi.create.content.fluids.pipes;

import com.simibubi.create.infrastructure.fabric.transfer.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class VanillaFluidTargets {
    private VanillaFluidTargets() {
    }

    public static FluidStack drainBlock(Level level, BlockPos pos, BlockState state, TransactionContext ctx) {
        return FluidStack.EMPTY;
    }
}
