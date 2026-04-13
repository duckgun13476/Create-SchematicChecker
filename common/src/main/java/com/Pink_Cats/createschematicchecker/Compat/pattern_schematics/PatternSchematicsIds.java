package com.Pink_Cats.createschematicchecker.Compat.pattern_schematics;

public final class PatternSchematicsIds {
    public static final String MOD_ID = "create_pattern_schematics";
    public static final String EMPTY_PATTERN_SCHEMATIC_PATH = "empty_pattern_schematic";
    public static final String PATTERN_SCHEMATIC_PATH = "pattern_schematic";
    public static final String EMPTY_PATTERN_SCHEMATIC_ID = MOD_ID + ":" + EMPTY_PATTERN_SCHEMATIC_PATH;

    private PatternSchematicsIds() {
    }

    public static boolean isPatternSchematicId(String namespace, String path) {
        return MOD_ID.equals(namespace) && PATTERN_SCHEMATIC_PATH.equals(path);
    }
}
