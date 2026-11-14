package com.pinkcats.csc.bootstrap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/** Dependency-free harness, run with the Forge 36 runtime classpath and Java 16. */
public final class TorqueApiLocatorTest {
    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            for (int i = 0; i < 20; i++) TorqueApiLocator.extractLibrary(Path.of(args[0]), Path.of(args[1]));
            return;
        }
        Path root = Files.createTempDirectory("csc-locator-test-");
        Path mods = Files.createDirectories(root.resolve("mods"));
        byte[] api = jar("META-INF/mods.toml", "[[mods]]\nmodId='torqueapi'\nversion='1.2.2'\n".getBytes(StandardCharsets.UTF_8));
        Path self = mods.resolve("checker.jar");
        Files.write(self, jar("META-INF/libraries/csc-torqueapi.jar", api));
        require(!TorqueApiLocator.hasExternalTorque(mods, self), "embedded is not external");
        Path misleading = mods.resolve("TorqueAPI-fake.jar");
        Files.write(misleading, jar("META-INF/mods.toml", "# torqueapi\n[[mods]]\nmodId='other'\n".getBytes(StandardCharsets.UTF_8)));
        require(!TorqueApiLocator.hasExternalTorque(mods, self), "filename/comment is not mod identity");
        Path renamed = mods.resolve("renamed-library.jar");
        Files.write(renamed, api);
        require(TorqueApiLocator.hasExternalTorque(mods, self), "renamed external API wins");
        Files.delete(renamed);
        byte[] multiple = "[[mods]]\nmodId='other'\n[[mods]]\nmodId='torqueapi'\nversion='1.0'\n".getBytes(StandardCharsets.UTF_8);
        Files.write(renamed, jar("META-INF/mods.toml", multiple));
        require(TorqueApiLocator.hasExternalTorque(mods, self), "multi-mod/old API is delegated to Forge version checking");
        Path cached = TorqueApiLocator.extractLibrary(self, root.resolve("cache"));
        require(java.util.Arrays.equals(api, Files.readAllBytes(cached)), "extraction preserves bytes");
        long before = Files.getLastModifiedTime(cached).toMillis();
        require(cached.equals(TorqueApiLocator.extractLibrary(self, root.resolve("cache"))), "cache path stable");
        require(before == Files.getLastModifiedTime(cached).toMillis(), "cache not rewritten");
        byte[] corrupt = {1, 2, 3};
        Files.write(cached, corrupt);
        require(cached.equals(TorqueApiLocator.extractLibrary(self, root.resolve("cache"))), "recovery path stable");
        require(java.util.Arrays.equals(api, Files.readAllBytes(cached)), "corrupt cache regenerated exactly");
        requireQuarantine(cached, corrupt, 1);
        long recoveredTime = Files.getLastModifiedTime(cached).toMillis();
        TorqueApiLocator.extractLibrary(self, root.resolve("cache"));
        require(recoveredTime == Files.getLastModifiedTime(cached).toMillis(), "recovered cache reused");
        requireQuarantine(cached, corrupt, 1);

        Path leftover = root.resolve("cache/torqueapi-interrupted.tmp");
        Files.write(leftover, corrupt);
        TorqueApiLocator.extractLibrary(self, root.resolve("cache"));
        require(java.util.Arrays.equals(corrupt, Files.readAllBytes(leftover)), "leftover temporary file preserved");

        // Both same-JVM and cross-JVM callers must serialize a repair, not race quarantine/write.
        Files.write(cached, corrupt);
        java.util.concurrent.CountDownLatch start = new java.util.concurrent.CountDownLatch(1);
        var pool = java.util.concurrent.Executors.newFixedThreadPool(8);
        try {
            var futures = new java.util.ArrayList<java.util.concurrent.Future<Path>>();
            for (int i = 0; i < 8; i++) futures.add(pool.submit(() -> {
                start.await();
                return TorqueApiLocator.extractLibrary(self, root.resolve("cache"));
            }));
            start.countDown();
            for (var future : futures) require(cached.equals(future.get(30, java.util.concurrent.TimeUnit.SECONDS)), "thread result");
        } finally { pool.shutdownNow(); }
        requireQuarantine(cached, corrupt, 2);
        Files.write(cached, corrupt);
        Process first = child(self, root.resolve("cache"));
        Process second = child(self, root.resolve("cache"));
        try {
            require(first.waitFor(30, java.util.concurrent.TimeUnit.SECONDS), "first subprocess timeout");
            require(second.waitFor(30, java.util.concurrent.TimeUnit.SECONDS), "second subprocess timeout");
            require(first.exitValue() == 0 && second.exitValue() == 0, "subprocess extraction failed");
        } finally { first.destroyForcibly(); second.destroyForcibly(); }
        requireQuarantine(cached, corrupt, 3);
        require(java.util.Arrays.equals(api, Files.readAllBytes(cached)), "concurrent extraction bytes");

        Path directoryCache = Files.createDirectories(root.resolve("directory-cache"));
        Path targetDirectory = Files.createDirectory(directoryCache.resolve(cached.getFileName()));
        expectIOException(() -> TorqueApiLocator.extractLibrary(self, directoryCache), "directory target");
        require(Files.isDirectory(targetDirectory), "target directory preserved");

        byte[] wrongApi = jar("META-INF/mods.toml", "[[mods]]\nmodId='other'\n".getBytes(StandardCharsets.UTF_8));
        Path wrongSelf = root.resolve("wrong.jar");
        Files.write(wrongSelf, jar("META-INF/libraries/csc-torqueapi.jar", wrongApi));
        Path badCache = Files.createDirectories(root.resolve("bad-cache"));
        StringBuilder wrongHash = new StringBuilder();
        for (byte value : java.security.MessageDigest.getInstance("SHA-256").digest(wrongApi))
            wrongHash.append(String.format(java.util.Locale.ROOT, "%02x", value & 255));
        Path badTarget = badCache.resolve("torqueapi-" + wrongHash + ".jar");
        Files.write(badTarget, corrupt);
        expectIOException(() -> TorqueApiLocator.extractLibrary(wrongSelf, badCache), "bad embedded mod identity");
        require(java.util.Arrays.equals(corrupt, Files.readAllBytes(badTarget)), "bad embedded identity must not quarantine target");
        requireQuarantine(badTarget, corrupt, 0);
        testSymlinks(root, self, cached.getFileName(), corrupt);
        Path missing = root.resolve("missing.jar");
        Files.write(missing, jar("unrelated", new byte[0]));
        try { TorqueApiLocator.extractLibrary(missing, root.resolve("cache")); throw new AssertionError("missing library accepted"); }
        catch (IOException expected) { require(expected.getMessage().contains("Missing"), "missing entry failure"); }
        System.out.println("TorqueApiLocatorTest: all checks passed");
        try (var paths = Files.walk(root)) {
            for (Path p : (Iterable<Path>) paths.sorted(java.util.Comparator.reverseOrder())::iterator) Files.delete(p);
        }
    }

    private static Process child(Path self, Path cache) throws IOException {
        String executable = System.getProperty("os.name").toLowerCase(java.util.Locale.ROOT).contains("windows") ? "java.exe" : "java";
        return new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", executable).toString(),
                "--add-opens=java.base/java.util.jar=ALL-UNNAMED", "-cp", System.getProperty("java.class.path"),
                TorqueApiLocatorTest.class.getName(), self.toString(), cache.toString()).inheritIO().start();
    }

    private static void requireQuarantine(Path target, byte[] expected, int count) throws IOException {
        try (var paths = Files.list(target.getParent())) {
            var quarantines = paths.filter(p -> p.getFileName().toString().startsWith(target.getFileName() + ".corrupt-"))
                    .collect(java.util.stream.Collectors.toList());
            require(quarantines.size() == count, "quarantine count expected " + count + " got " + quarantines.size());
            for (Path quarantine : quarantines) {
                java.util.UUID.fromString(quarantine.getFileName().toString().substring((target.getFileName() + ".corrupt-").length()));
                require(java.util.Arrays.equals(expected, Files.readAllBytes(quarantine)), "quarantine retains original bytes");
            }
        }
    }

    private static void testSymlinks(Path root, Path self, Path fileName, byte[] sentinel) throws Exception {
        Path referenced = root.resolve("referenced.bin");
        Files.write(referenced, sentinel);
        Path linkCache = Files.createDirectories(root.resolve("symlink-target-cache"));
        Path targetLink = linkCache.resolve(fileName);
        try { Files.createSymbolicLink(targetLink, referenced); }
        catch (UnsupportedOperationException | IOException | SecurityException unavailable) {
            System.out.println("Symlink checks skipped: platform does not permit symbolic links (" + unavailable.getClass().getSimpleName() + ")");
            return;
        }
        expectIOException(() -> TorqueApiLocator.extractLibrary(self, linkCache), "symlink target");
        require(Files.isSymbolicLink(targetLink), "target symlink preserved");
        require(java.util.Arrays.equals(sentinel, Files.readAllBytes(referenced)), "symlink referenced bytes preserved");
        Path referencedDirectory = Files.createDirectories(root.resolve("referenced-directory"));
        Files.write(referencedDirectory.resolve("sentinel"), sentinel);
        Path directoryLink = root.resolve("symlink-cache");
        Files.createSymbolicLink(directoryLink, referencedDirectory);
        expectIOException(() -> TorqueApiLocator.extractLibrary(self, directoryLink), "symlink cache directory");
        require(Files.isSymbolicLink(directoryLink), "cache symlink preserved");
        require(java.util.Arrays.equals(sentinel, Files.readAllBytes(referencedDirectory.resolve("sentinel"))), "directory contents preserved");
        try (var files = Files.list(referencedDirectory)) { require(files.count() == 1, "no writes through cache symlink"); }
    }

    @FunctionalInterface private interface IoAction { void run() throws IOException; }
    private static void expectIOException(IoAction action, String label) throws IOException {
        try { action.run(); } catch (IOException expected) { return; }
        throw new AssertionError("Expected rejection: " + label);
    }

    private static byte[] jar(String name, byte[] data) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (JarOutputStream jar = new JarOutputStream(bytes)) {
            jar.putNextEntry(new JarEntry(name)); jar.write(data); jar.closeEntry();
        }
        return bytes.toByteArray();
    }
    private static void require(boolean value, String message) { if (!value) throw new AssertionError(message); }
}
