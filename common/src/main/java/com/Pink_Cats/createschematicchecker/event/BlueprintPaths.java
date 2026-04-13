package com.Pink_Cats.createschematicchecker.event;

public class BlueprintPaths {

    public static String removeFirstPathComponent(String path) {
        int firstSlashIndex = path.indexOf("/");
        if (firstSlashIndex != -1) {
            return path.substring(firstSlashIndex + 1);
        }
        return path;
    }
}
