package com.Pink_Cats.createschematicchecker.event;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BlueprintPaths {
    private static final Path UPLOADED_ROOT = Paths.get("schematics", "uploaded").toAbsolutePath().normalize();

    public static UploadedBlueprint parseUploadedBlueprint(String id) throws IOException {
        if (id == null || id.isEmpty() || id.indexOf('\\') >= 0) {
            throw new IOException("Invalid uploaded blueprint id: " + id);
        }

        String[] components = id.split("/", -1);
        if (components.length != 2 || !isSafePathComponent(components[0]) || !isSafePathComponent(components[1])) {
            throw new IOException("Invalid uploaded blueprint id: " + id);
        }

        Path path = UPLOADED_ROOT.resolve(components[0]).resolve(components[1]).normalize();
        if (!path.startsWith(UPLOADED_ROOT)) {
            throw new IOException("Uploaded blueprint escapes upload directory: " + id);
        }

        return new UploadedBlueprint(components[0], components[1], path);
    }

    private static boolean isSafePathComponent(String component) {
        return !component.isEmpty()
                && !".".equals(component)
                && !"..".equals(component)
                && component.indexOf('\\') < 0
                && component.indexOf('\0') < 0;
    }

    public static final class UploadedBlueprint {
        private final String user;
        private final String fileName;
        private final Path path;

        private UploadedBlueprint(String user, String fileName, Path path) {
            this.user = user;
            this.fileName = fileName;
            this.path = path;
        }

        public String user() {
            return user;
        }

        public String fileName() {
            return fileName;
        }

        public Path path() {
            return path;
        }
    }

    public static String removeFirstPathComponent(String path) {
        int firstSlashIndex = path.indexOf("/");
        if (firstSlashIndex != -1) {
            return path.substring(firstSlashIndex + 1);
        }
        return path;
    }
}
