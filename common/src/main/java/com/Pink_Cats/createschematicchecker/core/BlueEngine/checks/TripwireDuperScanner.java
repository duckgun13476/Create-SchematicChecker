package com.Pink_Cats.createschematicchecker.core.BlueEngine.checks;

import com.pinkcats.torque.layer.net.minecraft.nbt.CompoundTag;
import com.pinkcats.torque.layer.net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.Pink_Cats.createschematicchecker.core.BlueEngine.NbtInterFace.TagString;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.StrFunc.NoQuotes;
import static com.Pink_Cats.createschematicchecker.core.BlueEngine.TagFunc.PaletteGetId;
import static com.Pink_Cats.createschematicchecker.core.ChainEngine.ConveyorInterface.StringPickPos;

public final class TripwireDuperScanner {
    private TripwireDuperScanner() {
    }

    public static Result scanAndSanitize(ListTag palette, ListTag blocks) {
        Map<String, TripwireBlockState> tripwireBlocks = new HashMap<>();
        List<TripwireBlockState> hooks = new ArrayList<>();
        Set<String> waterUpdateSources = new HashSet<>();
        Set<String> redstoneUpdateSources = new HashSet<>();

        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag block = blocks.getCompound(i);
            if (block.isEmpty() || block.get("pos") == null || block.get("state") == null) {
                continue;
            }
            int state = block.getInt("state");
            if (state < 0 || state >= palette.size()) {
                continue;
            }

            CompoundTag paletteItem = palette.getCompound(state);
            String id = PaletteGetId(paletteItem);
            int[] pos = StringPickPos(String.valueOf(block.get("pos")));
            String posKey = positionKey(pos);
            CompoundTag properties = paletteItem.getCompound("Properties");

            if ("minecraft:tripwire".equals(id)) {
                TripwireBlockState tripwire = new TripwireBlockState(pos, properties);
                tripwireBlocks.put(posKey, tripwire);
            } else if ("minecraft:tripwire_hook".equals(id)) {
                hooks.add(new TripwireBlockState(pos, properties));
            } else if (isWaterUpdateSource(id, properties)) {
                waterUpdateSources.add(posKey);
            } else if (isRedstoneUpdateSource(id, properties)) {
                redstoneUpdateSources.add(posKey);
            }
        }

        boolean cheat = false;
        for (TripwireBlockState hook : hooks) {
            String facing = propertyValue(hook.properties, "facing");
            int[] direction = facingDirection(facing);
            if (direction == null) {
                continue;
            }

            int[] cursor = add(hook.pos, direction);
            int tripwireCount = 0;
            boolean hasDangerousState = hook.isPoweredOrAttached();
            boolean hasTripwireWaterUpdateSource = hasAdjacentPosition(cursor, waterUpdateSources);
            boolean hasTripwireRedstoneUpdateSource = hasAdjacentPosition(cursor, redstoneUpdateSources);

            for (int distance = 1; distance <= 42; distance++) {
                String cursorKey = positionKey(cursor);
                TripwireBlockState tripwire = tripwireBlocks.get(cursorKey);
                if (tripwire != null) {
                    tripwireCount++;
                    hasDangerousState = hasDangerousState || tripwire.isPoweredOrAttachedOrDisarmed();
                    hasTripwireWaterUpdateSource = hasTripwireWaterUpdateSource
                            || hasAdjacentPosition(cursor, waterUpdateSources);
                    hasTripwireRedstoneUpdateSource = hasTripwireRedstoneUpdateSource
                            || hasAdjacentPosition(cursor, redstoneUpdateSources);
                    cursor = add(cursor, direction);
                    continue;
                }

                TripwireBlockState oppositeHook = findHookAt(hooks, cursorKey);
                if (oppositeHook != null && isOppositeFacing(facing, propertyValue(oppositeHook.properties, "facing"))) {
                    hasDangerousState = hasDangerousState || oppositeHook.isPoweredOrAttached();
                    boolean hasUpdateSource = hasTripwireWaterUpdateSource || hasTripwireRedstoneUpdateSource;
                    if (tripwireCount > 0 && hasDangerousState && hasUpdateSource) {
                        cheat = true;
                    }
                }
                break;
            }
        }

        int sanitizedPaletteCount = sanitizePaletteStates(palette);
        return new Result(
                cheat,
                sanitizedPaletteCount,
                hooks.size(),
                tripwireBlocks.size(),
                !waterUpdateSources.isEmpty(),
                !redstoneUpdateSources.isEmpty()
        );
    }

    private static int sanitizePaletteStates(ListTag palette) {
        int changedCount = 0;
        for (int i = 0; i < palette.size(); i++) {
            CompoundTag paletteItem = palette.getCompound(i);
            String id = PaletteGetId(paletteItem);
            if (!"minecraft:tripwire".equals(id) && !"minecraft:tripwire_hook".equals(id)) {
                continue;
            }

            CompoundTag properties = paletteItem.getCompound("Properties");
            boolean changed = false;
            for (String key : new ArrayList<>(properties.getAllKeys())) {
                String value = propertyValue(properties, key);
                if ("true".equals(value)) {
                    properties.put(key, TagString("false"));
                    changed = true;
                }
            }
            if (changed) {
                changedCount++;
            }
        }
        return changedCount;
    }

    private static boolean isWaterUpdateSource(String id, CompoundTag properties) {
        if ("minecraft:water".equals(id)) {
            return true;
        }
        return "true".equals(propertyValue(properties, "waterlogged"));
    }

    private static boolean isRedstoneUpdateSource(String id, CompoundTag properties) {
        if ("minecraft:lever".equals(id) || id.endsWith("_button") || id.endsWith("_pressure_plate")) {
            return "true".equals(propertyValue(properties, "powered"));
        }
        if (id.endsWith("_trapdoor")) {
            return "true".equals(propertyValue(properties, "open"))
                    || "true".equals(propertyValue(properties, "powered"));
        }
        return false;
    }

    private static boolean hasAdjacentPosition(int[] pos, Set<String> positionKeys) {
        int[][] offsets = {
                {0, 0, 0},
                {1, 0, 0}, {-1, 0, 0},
                {0, 1, 0}, {0, -1, 0},
                {0, 0, 1}, {0, 0, -1}
        };
        for (int[] offset : offsets) {
            int[] nearby = {pos[0] + offset[0], pos[1] + offset[1], pos[2] + offset[2]};
            if (positionKeys.contains(positionKey(nearby))) {
                return true;
            }
        }
        return false;
    }

    private static TripwireBlockState findHookAt(List<TripwireBlockState> hooks, String posKey) {
        for (TripwireBlockState hook : hooks) {
            if (positionKey(hook.pos).equals(posKey)) {
                return hook;
            }
        }
        return null;
    }

    private static boolean isOppositeFacing(String first, String second) {
        return ("north".equals(first) && "south".equals(second))
                || ("south".equals(first) && "north".equals(second))
                || ("east".equals(first) && "west".equals(second))
                || ("west".equals(first) && "east".equals(second));
    }

    private static int[] facingDirection(String facing) {
        if ("north".equals(facing)) {
            return new int[]{0, 0, -1};
        }
        if ("south".equals(facing)) {
            return new int[]{0, 0, 1};
        }
        if ("west".equals(facing)) {
            return new int[]{-1, 0, 0};
        }
        if ("east".equals(facing)) {
            return new int[]{1, 0, 0};
        }
        return null;
    }

    private static int[] add(int[] pos, int[] direction) {
        return new int[]{pos[0] + direction[0], pos[1] + direction[1], pos[2] + direction[2]};
    }

    private static String positionKey(int[] pos) {
        return pos[0] + "," + pos[1] + "," + pos[2];
    }

    private static String propertyValue(CompoundTag properties, String key) {
        if (properties == null || properties.get(key) == null) {
            return "";
        }
        return NoQuotes(String.valueOf(properties.get(key)));
    }

    public static final class Result {
        public final boolean cheat;
        public final int sanitizedPaletteCount;
        public final int hookCount;
        public final int tripwireCount;
        public final boolean hasWaterUpdateSource;
        public final boolean hasRedstoneUpdateSource;

        private Result(
                boolean cheat,
                int sanitizedPaletteCount,
                int hookCount,
                int tripwireCount,
                boolean hasWaterUpdateSource,
                boolean hasRedstoneUpdateSource
        ) {
            this.cheat = cheat;
            this.sanitizedPaletteCount = sanitizedPaletteCount;
            this.hookCount = hookCount;
            this.tripwireCount = tripwireCount;
            this.hasWaterUpdateSource = hasWaterUpdateSource;
            this.hasRedstoneUpdateSource = hasRedstoneUpdateSource;
        }
    }

    private static final class TripwireBlockState {
        private final int[] pos;
        private final CompoundTag properties;

        private TripwireBlockState(int[] pos, CompoundTag properties) {
            this.pos = pos;
            this.properties = properties;
        }

        private boolean isPoweredOrAttached() {
            return "true".equals(propertyValue(properties, "powered"))
                    || "true".equals(propertyValue(properties, "attached"));
        }

        private boolean isPoweredOrAttachedOrDisarmed() {
            return isPoweredOrAttached()
                    || "true".equals(propertyValue(properties, "disarmed"));
        }
    }
}
