package com.Pink_Cats.createschematicchecker.mixin;

import com.simibubi.create.content.fluids.OpenEndedPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.fix_quark_lava_fluidlogged_duplication;

/**
 * Preserve Create's native drain method so Sable can redirect the first
 * {@code getBlockState} call into an Aeronautics sub-level.  Only intercept the
 * Quark lava-logged branch after that resolution has already happened.
 */
@Mixin(value = OpenEndedPipe.class, remap = false)
public class OpenEndedPipeMixin {

    @Shadow private Level world;
    @Shadow private BlockPos outputPos;

    @Inject(
            method = "removeFluidFromSpace",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/fluids/pipes/VanillaFluidTargets;drainBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/neoforged/neoforge/fluids/FluidStack;"
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void csc$consumeQuarkLavaAfterSableResolution(boolean simulate, CallbackInfoReturnable<FluidStack> cir,
                                                            FluidStack empty, BlockState state, FluidState fluidState,
                                                            boolean waterlog) {
        if (!fix_quark_lava_fluidlogged_duplication || fluidState.getType() != Fluids.LAVA || !fluidState.isSource()) {
            return;
        }

        BooleanProperty lavalogged = state.getProperties().stream()
                .filter(property -> property instanceof BooleanProperty && property.getName().equals("lavalogged"))
                .map(BooleanProperty.class::cast)
                .findFirst()
                .orElse(null);
        if (lavalogged == null || !state.getValue(lavalogged)) {
            return;
        }

        FluidStack lava = new FluidStack(Fluids.LAVA, 1000);
        if (!simulate) {
            world.setBlock(outputPos, state.setValue(lavalogged, false), 3);
            world.scheduleTick(outputPos, Fluids.LAVA, 1);
        }
        cir.setReturnValue(lava);
    }
}
