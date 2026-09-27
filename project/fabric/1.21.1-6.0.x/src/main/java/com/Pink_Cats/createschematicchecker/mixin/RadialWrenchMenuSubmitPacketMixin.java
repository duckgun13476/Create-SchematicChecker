package com.Pink_Cats.createschematicchecker.mixin;

import com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Reject Create's unauthenticated radial-wrench state mutation packet. */
@Pseudo
@Mixin(targets = "com.simibubi.create.content.contraptions.wrench.RadialWrenchMenuSubmitPacket", remap = false)
public abstract class RadialWrenchMenuSubmitPacketMixin {

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void csc$rejectClientSubmittedBlockState(ServerPlayer player, CallbackInfo ci) {
        if (ConfigRegister.block_radial_wrench_menu_packet) {
            ci.cancel();
        }
    }
}
