package com.Pink_Cats.createschematicchecker.mixin;

import com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reject Create's unauthenticated radial-wrench state mutation packet. */
@Mixin(targets = "com.simibubi.create.content.contraptions.wrench.RadialWrenchMenuSubmitPacket", remap = false)
public abstract class RadialWrenchMenuSubmitPacketMixin {

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void csc$rejectClientSubmittedBlockState(NetworkEvent.Context context, CallbackInfoReturnable<Boolean> cir) {
        if (ConfigRegister.block_radial_wrench_menu_packet) {
            cir.setReturnValue(true);
        }
    }
}
