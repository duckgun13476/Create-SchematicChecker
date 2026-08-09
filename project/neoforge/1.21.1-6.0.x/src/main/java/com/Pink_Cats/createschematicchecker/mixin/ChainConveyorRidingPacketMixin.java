package com.Pink_Cats.createschematicchecker.mixin;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ServerChainConveyorHandler;
import com.simibubi.create.content.kinetics.chainConveyor.ServerboundChainConveyorRidingPacket;
import com.Pink_Cats.createschematicchecker.event.ChainRidingState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.Pink_Cats.createschematicchecker.FancyConfig.ConfigRegister.validate_chain_conveyor_riding_packet;

/**
 * Create normally trusts the client to keep a chain-riding heartbeat alive and resets
 * vanilla's airborne counters for every packet. Bound that heartbeat to the actual
 * conveyor path and its server-side speed before allowing the reset.
 */
@Mixin(value = ServerboundChainConveyorRidingPacket.class, remap = false)
public abstract class ChainConveyorRidingPacketMixin {

    @Unique
    private static final Map<UUID, ChainRidingState> CSC_RIDE_STATES = new ConcurrentHashMap<>();

    @Shadow
    @Final
    private boolean stop;

    @Inject(method = "applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;)V", at = @At("HEAD"), cancellable = true)
    private void csc$validateChainRidingHeartbeat(ServerPlayer sender, ChainConveyorBlockEntity conveyor, CallbackInfo ci) {
        if (!validate_chain_conveyor_riding_packet) {
            return;
        }
        UUID playerId = sender.getUUID();
        if (stop) {
            CSC_RIDE_STATES.remove(playerId);
            return;
        }

        long tick = sender.serverLevel().getGameTime();
        Vec3 position = sender.position();
        if (!csc$isNearConveyorPath(sender, conveyor)) {
            csc$rejectHeartbeat(sender, playerId, ci);
            return;
        }

        ChainRidingState previous = CSC_RIDE_STATES.put(playerId, new ChainRidingState(position, tick));
        if (previous == null) {
            return;
        }

        long elapsedTicks = tick - previous.tick();
        if (elapsedTicks <= 0 || elapsedTicks > 40) {
            return;
        }

        // Create advances a rider by abs(speed / 360) blocks each server tick.
        double maximumDistance = 0.6D + Math.abs(conveyor.getSpeed()) / 360.0D * elapsedTicks * 1.25D;
        if (position.distanceToSqr(previous.position()) > maximumDistance * maximumDistance) {
            csc$rejectHeartbeat(sender, playerId, ci);
        }
    }

    @Unique
    private static void csc$rejectHeartbeat(ServerPlayer sender, UUID playerId, CallbackInfo ci) {
        CSC_RIDE_STATES.remove(playerId);
        ServerChainConveyorHandler.handleStopRidingPacket(sender);
        ci.cancel();
    }

    @Unique
    private static boolean csc$isNearConveyorPath(ServerPlayer player, ChainConveyorBlockEntity conveyor) {
        Vec3 handPosition = player.position().add(0, player.getBbHeight() + 0.5D, 0);
        double closestDistance = Double.POSITIVE_INFINITY;

        // The closed chain is a small loop. Sampling it is sufficient for a tight
        // interaction bound and does not trust any coordinate sent by the client.
        for (int angle = 0; angle < 360; angle += 15) {
            closestDistance = Math.min(closestDistance,
                    handPosition.distanceToSqr(conveyor.getPackagePosition(angle, null)));
        }

        for (BlockPos connection : conveyor.connections) {
            Vec3 start = conveyor.getPackagePosition(0, connection);
            Vec3 end = conveyor.getPackagePosition(Float.MAX_VALUE, connection);
            closestDistance = Math.min(closestDistance, csc$distanceToSegmentSqr(handPosition, start, end));
        }

        return closestDistance <= 2.25D;
    }

    @Unique
    private static double csc$distanceToSegmentSqr(Vec3 point, Vec3 start, Vec3 end) {
        Vec3 segment = end.subtract(start);
        double lengthSquared = segment.lengthSqr();
        if (lengthSquared == 0) {
            return point.distanceToSqr(start);
        }
        double progress = point.subtract(start).dot(segment) / lengthSquared;
        progress = Math.max(0, Math.min(1, progress));
        return point.distanceToSqr(start.add(segment.scale(progress)));
    }
}
