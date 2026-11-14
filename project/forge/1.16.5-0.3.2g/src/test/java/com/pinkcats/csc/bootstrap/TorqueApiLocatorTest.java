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
        Files.write(cached, new byte[] {1});
        try { TorqueApiLocator.extractLibrary(self, root.resolve("cache")); throw new AssertionError("corrupt cache accepted"); }
        catch (IOException expected) { require(expected.getMessage().contains("hash mismatch"), "cache integrity failure"); }
        Path missing = root.resolve("missing.jar");
        Files.write(missing, jar("unrelated", new byte[0]));
        try { TorqueApiLocator.extractLibrary(missing, root.resolve("cache")); throw new AssertionError("missing library accepted"); }
        catch (IOException expected) { require(expected.getMessage().contains("Missing"), "missing entry failure"); }
        System.out.println("TorqueApiLocatorTest: all checks passed");
        try (var paths = Files.walk(root)) {
            for (Path p : (Iterable<Path>) paths.sorted(java.util.Comparator.reverseOrder())::iterator) Files.delete(p);
        }
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
