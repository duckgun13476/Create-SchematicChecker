package com.Pink_Cats.createschematicchecker.mixin;

import com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The packet accepts an arbitrary same-block BlockState from the client.
 * There is no server-side menu/session proof for that mutation, so reject it.
 */
@Mixin(targets = "com.simibubi.create.content.contraptions.wrench.RadialWrenchMenuSubmitPacket", remap = false)
public abstract class RadialWrenchMenuSubmitPacketMixin {

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void csc$rejectClientSubmittedBlockState(ServerPlayer player, CallbackInfo ci) {
        if (ConfigRegister.block_radial_wrench_menu_packet) {
            ci.cancel();
        }
    }
}
