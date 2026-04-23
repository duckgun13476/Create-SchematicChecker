package com.simibubi.create.content.fluids;

import com.simibubi.create.infrastructure.fabric.transfer.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class OpenEndedPipe {
    private Level world;
    private BlockPos outputPos;
    private BlockPos pos;

    public FluidStack removeFluidFromSpace(TransactionContext ctx) {
        return FluidStack.EMPTY;
    }

    public boolean isEndpoint() {
        return false;
    }
}
