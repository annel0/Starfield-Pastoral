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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    /**
     * 预置地图版本号。改动 jar 里的 .mca 文件后把这个数 +1，
     * 老存档下次启动会检测到 marker 里 version 过期 → 覆盖安装 → 镇子更新到最新版。
     *
     * 注意：覆盖会抹掉玩家在 pregen 区域内放的方块（比如摆了椅子、铺了地板之类）。
     * 所以每次 +1 都是"强制小镇重置"的操作，要和版本发布节奏绑定。
     */
    private static final int CURRENT_PREGEN_VERSION = 3;

    private static final String MARKER_VERSION_PREFIX = "version=";

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

            boolean hasRegions = hasAnyRegionFiles(targetRegionDir);
            boolean hasMarker = Files.exists(marker);
            int installedVersion = hasMarker ? readMarkerVersion(marker) : 0;

            if (hasMarker && hasRegions && installedVersion >= CURRENT_PREGEN_VERSION) {
                return InstallResult.ALREADY_PRESENT;
            }

            // 到这里意味着需要（重新）安装 — 全新存档、老 marker 缺 version、或显式版本过期。
            // 无 marker 但有 region 文件 = 老存档第一次跑带版本管理的 installer，installedVersion=0 < CURRENT → 升级。
            boolean upgrading = hasRegions && installedVersion < CURRENT_PREGEN_VERSION;
            if (upgrading) {
                StardewCraft.LOGGER.warn(
                    "[VALLEY_PREGEN] Upgrading pregen regions: {} -> {}. Player changes inside pregen area will be overwritten.",
                    installedVersion, CURRENT_PREGEN_VERSION);
            }

            Files.createDirectories(targetRegionDir);

            // 升级时清除 region/ 中新 manifest 不再包含的多余 .mca 文件。
            // manifest 中存在的文件由后续 Files.copy(REPLACE_EXISTING) 直接覆写。
            // 运行时 BiomePatcher 等也可能产生 manifest 之外的 .mca，
            // 这些残留文件如果不清除会和新 pregen 内容冲突 → 区块错位。
            if (hasRegions) {
                Set<String> manifestFileNames = new HashSet<>();
                for (String f : files) {
                    manifestFileNames.add(f.toLowerCase());
                }
                int deleted = cleanStaleRegionFiles(targetRegionDir, manifestFileNames);
                if (deleted > 0) {
                    StardewCraft.LOGGER.info("[VALLEY_PREGEN] Removed {} stale .mca files not in new manifest", deleted);
                }
            }

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

            writeMarker(marker, copied, CURRENT_PREGEN_VERSION);

            StardewCraft.LOGGER.info("[VALLEY_PREGEN] {} prebuilt regions (version {}): {} files",
                upgrading ? "Upgraded" : "Installed", CURRENT_PREGEN_VERSION, copied);
            return InstallResult.INSTALLED;
        } catch (Exception e) {
            StardewCraft.LOGGER.error("[VALLEY_PREGEN] Failed installing prebuilt regions: {}", e.getMessage(), e);
            return InstallResult.FAILED;
        }
    }

    private static void writeMarker(Path marker, int fileCount, int version) throws IOException {
        Files.writeString(marker,
            "installed=" + fileCount + "\n" + MARKER_VERSION_PREFIX + version + "\n",
            StandardCharsets.UTF_8);
    }

    private static int readMarkerVersion(Path marker) {
        try (BufferedReader r = Files.newBufferedReader(marker, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith(MARKER_VERSION_PREFIX)) {
                    try {
                        return Integer.parseInt(trimmed.substring(MARKER_VERSION_PREFIX.length()).trim());
                    } catch (NumberFormatException ignored) {
                        return 1;
                    }
                }
            }
        } catch (IOException ignored) {}
        // 没 version= 行 = 老 marker（只写了 installed=N），当作 version 1
        return 1;
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

    /**
     * 删除 region 目录中不在新 manifest 里的多余 .mca 文件。
     * manifest 里有的文件由 Files.copy(REPLACE_EXISTING) 直接覆写，不需要删除。
     */
    private static int cleanStaleRegionFiles(Path regionDir, Set<String> keepLowerCase) {
        int deleted = 0;
        try (Stream<Path> stream = Files.list(regionDir)) {
            for (Path file : stream.toList()) {
                String name = file.getFileName().toString();
                if (!name.toLowerCase().endsWith(".mca") || !Files.isRegularFile(file)) {
                    continue;
                }
                if (!keepLowerCase.contains(name.toLowerCase())) {
                    Files.delete(file);
                    deleted++;
                }
            }
        } catch (IOException e) {
            StardewCraft.LOGGER.warn("[VALLEY_PREGEN] Failed cleaning stale region files in {}: {}", regionDir, e.getMessage());
        }
        return deleted;
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
