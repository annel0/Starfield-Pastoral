package com.stardew.craft.dimension;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 新存档启动时安装预烘焙的星露谷区块文件（.mca）。
 *
 * 资源约定：
 * - pregen/stardew_valley/region_manifest.txt
 * - pregen/stardew_valley/region/<filename>.mca
 */
@SuppressWarnings("null")
public final class StardewValleyPrebuiltRegionInstaller {
    private StardewValleyPrebuiltRegionInstaller() {}

    private static final String MANIFEST_RESOURCE = "pregen/stardew_valley/region_manifest.txt";
    private static final String REGION_RESOURCE_PREFIX = "pregen/stardew_valley/region/";
    private static final String MARKER_FILE = "stardew_valley_pregen_installed.marker";
    private static final Pattern REGION_FILE_PATTERN = Pattern.compile("^r\\.(-?\\d+)\\.(-?\\d+)\\.mca$", Pattern.CASE_INSENSITIVE);

    public enum InstallResult {
        INSTALLED,
        ALREADY_PRESENT,
        NO_PREBUILT,
        FAILED
    }

    public static InstallResult installIfAvailable(MinecraftServer server) {
        try {
            Path worldRoot = server.getWorldPath(LevelResource.ROOT);
            Path marker = worldRoot.resolve(MARKER_FILE);
            Path targetRegionDir = getTargetRegionDir(worldRoot);

            List<String> files = readManifest();
            if (files.isEmpty()) {
                return InstallResult.NO_PREBUILT;
            }

            if (Files.exists(marker) && hasAnyRegionFiles(targetRegionDir)) {
                return InstallResult.ALREADY_PRESENT;
            }

            if (!Files.exists(marker) && hasAnyRegionFiles(targetRegionDir)) {
                Files.writeString(marker, "installed=existing\n", StandardCharsets.UTF_8);
                StardewCraft.LOGGER.info("[VALLEY_PREGEN] Existing save regions detected, skip overwrite and create marker");
                return InstallResult.ALREADY_PRESENT;
            }

            Files.createDirectories(targetRegionDir);

            int copied = 0;
            for (String fileName : files) {
                String resourcePath = REGION_RESOURCE_PREFIX + fileName;
                try (InputStream in = StardewValleyPrebuiltRegionInstaller.class.getClassLoader().getResourceAsStream(resourcePath)) {
                    if (in == null) {
                        StardewCraft.LOGGER.error("[VALLEY_PREGEN] Missing resource: {}", resourcePath);
                        return InstallResult.FAILED;
                    }
                    Path target = targetRegionDir.resolve(fileName);
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                    copied++;
                }
            }

            Files.writeString(marker,
                "installed=" + copied + "\n",
                StandardCharsets.UTF_8);

            StardewCraft.LOGGER.info("[VALLEY_PREGEN] Installed prebuilt regions to new save: {} files", copied);
            return InstallResult.INSTALLED;
        } catch (Exception e) {
            StardewCraft.LOGGER.error("[VALLEY_PREGEN] Failed installing prebuilt regions: {}", e.getMessage(), e);
            return InstallResult.FAILED;
        }
    }

    public static boolean hasInstalledPrebuilt(MinecraftServer server) {
        try {
            Path worldRoot = server.getWorldPath(LevelResource.ROOT);
            Path marker = worldRoot.resolve(MARKER_FILE);
            if (!Files.exists(marker)) {
                return false;
            }
            Path targetRegionDir = getTargetRegionDir(worldRoot);
            return hasAnyRegionFiles(targetRegionDir);
        } catch (Exception e) {
            StardewCraft.LOGGER.warn("[VALLEY_PREGEN] Failed checking prebuilt state: {}", e.getMessage());
            return false;
        }
    }

    public static BlockPos getInstalledRegionCenter(MinecraftServer server) {
        try {
            Path worldRoot = server.getWorldPath(LevelResource.ROOT);
            Path targetRegionDir = getTargetRegionDir(worldRoot);
            if (!Files.isDirectory(targetRegionDir)) {
                return new BlockPos(0, 66, 0);
            }

            Integer minRx = null;
            Integer maxRx = null;
            Integer minRz = null;
            Integer maxRz = null;

            try (Stream<Path> stream = Files.list(targetRegionDir)) {
                List<Path> files = stream.toList();
                for (Path file : files) {
                    String name = file.getFileName().toString();
                    Matcher matcher = REGION_FILE_PATTERN.matcher(name);
                    if (!matcher.matches()) {
                        continue;
                    }
                    int rx = Integer.parseInt(matcher.group(1));
                    int rz = Integer.parseInt(matcher.group(2));

                    minRx = (minRx == null) ? rx : Math.min(minRx, rx);
                    maxRx = (maxRx == null) ? rx : Math.max(maxRx, rx);
                    minRz = (minRz == null) ? rz : Math.min(minRz, rz);
                    maxRz = (maxRz == null) ? rz : Math.max(maxRz, rz);
                }
            }

            if (minRx == null || maxRx == null || minRz == null || maxRz == null) {
                return new BlockPos(0, 66, 0);
            }

            int minBlockX = minRx * 512;
            int maxBlockX = (maxRx + 1) * 512 - 1;
            int minBlockZ = minRz * 512;
            int maxBlockZ = (maxRz + 1) * 512 - 1;
            int centerX = (minBlockX + maxBlockX) / 2;
            int centerZ = (minBlockZ + maxBlockZ) / 2;
            return new BlockPos(centerX, 66, centerZ);
        } catch (Exception e) {
            StardewCraft.LOGGER.warn("[VALLEY_PREGEN] Failed getting installed region center: {}", e.getMessage());
            return new BlockPos(0, 66, 0);
        }
    }

    public static List<BlockPos> getInstalledRegionSampleCenters(MinecraftServer server) {
        List<BlockPos> result = new ArrayList<>();
        try {
            Path worldRoot = server.getWorldPath(LevelResource.ROOT);
            Path targetRegionDir = getTargetRegionDir(worldRoot);
            if (!Files.isDirectory(targetRegionDir)) {
                return result;
            }

            try (Stream<Path> stream = Files.list(targetRegionDir)) {
                for (Path file : stream.toList()) {
                    String name = file.getFileName().toString();
                    Matcher matcher = REGION_FILE_PATTERN.matcher(name);
                    if (!matcher.matches()) {
                        continue;
                    }
                    int rx = Integer.parseInt(matcher.group(1));
                    int rz = Integer.parseInt(matcher.group(2));
                    int centerX = rx * 512 + 256;
                    int centerZ = rz * 512 + 256;
                    result.add(new BlockPos(centerX, 66, centerZ));
                }
            }
        } catch (Exception e) {
            StardewCraft.LOGGER.warn("[VALLEY_PREGEN] Failed collecting region sample centers: {}", e.getMessage());
        }
        return result;
    }

    private static Path getTargetRegionDir(Path worldRoot) {
        return worldRoot
            .resolve("dimensions")
            .resolve("stardewcraft")
            .resolve("stardew_valley")
            .resolve("region");
    }

    private static boolean hasAnyRegionFiles(Path targetRegionDir) throws IOException {
        if (!Files.isDirectory(targetRegionDir)) {
            return false;
        }
        try (Stream<Path> stream = Files.list(targetRegionDir)) {
            return stream.anyMatch(path -> {
                String name = path.getFileName().toString().toLowerCase();
                return name.endsWith(".mca") && Files.isRegularFile(path);
            });
        }
    }

    private static List<String> readManifest() throws IOException {
        try (InputStream in = StardewValleyPrebuiltRegionInstaller.class.getClassLoader().getResourceAsStream(MANIFEST_RESOURCE)) {
            if (in == null) {
                return List.of();
            }

            List<String> files = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                boolean firstLine = true;
                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
                            line = line.substring(1);
                        }
                    }

                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                        continue;
                    }

                    if (!trimmed.toLowerCase().endsWith(".mca")) {
                        StardewCraft.LOGGER.warn("[VALLEY_PREGEN] Ignore non-region manifest entry: {}", trimmed);
                        continue;
                    }

                    files.add(trimmed);
                }
            }
            return files;
        }
    }
}
