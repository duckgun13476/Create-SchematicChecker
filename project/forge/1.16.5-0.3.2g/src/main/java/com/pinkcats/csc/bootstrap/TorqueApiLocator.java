package com.pinkcats.csc.bootstrap;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.forgespi.locating.IModFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/** Forge 36 does not discover ContainedDeps. Expose only CSC's bundled TorqueAPI. */
public final class TorqueApiLocator extends AbstractJarFileLocator {
    private static final String EMBEDDED_PATH = "META-INF/libraries/csc-torqueapi.jar";
    private static final int MAX_LIBRARY_BYTES = 32 * 1024 * 1024;

    @Override
    public List<IModFile> scanMods() {
        try {
            Path self = Path.of(TorqueApiLocator.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            // The provider is packaged only in the distributable. Development uses FG's dependency.
            if (!Files.isRegularFile(self)) return List.of();
            List<IModFile> files = new ArrayList<>();
            // Forge excludes jars supplying IModLocator from its ordinary mods-folder scan.
            addMod(files, self);
            if (!hasExternalTorque(FMLPaths.MODSDIR.get(), self)) {
                Path library = extractLibrary(self, FMLPaths.GAMEDIR.get().resolve(".csc-libraries"));
                addMod(files, library);
            }
            return files;
        } catch (Exception e) {
            throw new IllegalStateException("CSC could not discover its bundled TorqueAPI", e);
        }
    }

    private void addMod(List<IModFile> files, Path path) {
        ModFile file = ModFile.newFMLInstance(path, this);
        modJars.put(file, createFileSystem(file));
        files.add(file);
    }

    static boolean hasExternalTorque(Path mods, Path self) throws IOException {
        if (!Files.isDirectory(mods)) return false;
        try (Stream<Path> paths = Files.list(mods)) {
            for (Path jar : (Iterable<Path>) paths.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jar"))::iterator) {
                if (!Files.isSameFile(jar, self) && declaresTorque(jar)) return true;
            }
        }
        return false;
    }

    static boolean declaresTorque(Path path) throws IOException {
        try (JarFile jar = new JarFile(path.toFile())) {
            var entry = jar.getJarEntry("META-INF/mods.toml");
            if (entry == null) return false;
            try (InputStream input = jar.getInputStream(entry)) {
                List<? extends UnmodifiableConfig> mods = new TomlParser().parse(input).get("mods");
                if (mods == null) return false;
                return mods.stream().anyMatch(mod -> "torqueapi".equals(mod.get("modId")));
            }
        }
    }

    static Path extractLibrary(Path self, Path cache) throws IOException {
        byte[] bytes;
        try (JarFile jar = new JarFile(self.toFile())) {
            var entry = jar.getJarEntry(EMBEDDED_PATH);
            if (entry == null) throw new IOException("Missing " + EMBEDDED_PATH);
            try (InputStream input = jar.getInputStream(entry)) {
                bytes = input.readNBytes(MAX_LIBRARY_BYTES + 1);
                if (bytes.length > MAX_LIBRARY_BYTES) throw new IOException("Embedded TorqueAPI exceeds size limit");
            }
        }
        String hash = sha256(bytes);
        Files.createDirectories(cache);
        Path target = cache.resolve("torqueapi-" + hash + ".jar");
        if (Files.exists(target)) {
            if (!hash.equals(sha256(Files.readAllBytes(target)))) {
                throw new IOException("Bundled TorqueAPI cache hash mismatch: " + target);
            }
        } else {
            Path temporary = Files.createTempFile(cache, "torqueapi-", ".tmp");
            try {
                Files.write(temporary, bytes);
                // No overwriting another version or a pre-existing invalid cache.
                Files.move(temporary, target);
            } finally {
                Files.deleteIfExists(temporary);
            }
        }
        if (!declaresTorque(target)) throw new IOException("Embedded library is not TorqueAPI");
        return target;
    }

    private static String sha256(byte[] bytes) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
            StringBuilder hex = new StringBuilder(64);
            for (byte b : digest) hex.append(String.format(Locale.ROOT, "%02x", b & 255));
            return hex.toString();
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException(impossible);
        }
    }

    @Override public String name() { return "CSC bundled TorqueAPI (Forge 36)"; }
    @Override public void initArguments(Map<String, ?> arguments) { }
}
