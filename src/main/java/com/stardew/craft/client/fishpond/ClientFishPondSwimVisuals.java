package com.stardew.craft.client.fishpond;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stardew.craft.blockentity.FishPondBucketBlockEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ClientFishPondSwimVisuals {
    private static final Map<String, PondSchool> ACTIVE_SCHOOLS = new HashMap<>();
    private static final double BOUNDS_PADDING = 0.35D;
    private static final int JUMP_BUFFER_TICKS = 2;

    private ClientFishPondSwimVisuals() {
    }

    public static JumpFishBinding reserveForJump(String dimensionId,
                                                 ItemStack fishStack,
                                                 Vec3 requestedStartPosition,
                                                 int holdTicks) {
        if (fishStack.isEmpty() || dimensionId == null || dimensionId.isBlank()) {
            return null;
        }

        PondSchool bestSchool = null;
        SwimmingFish bestFish = null;
        double bestDistance = Double.MAX_VALUE;

        for (Map.Entry<String, PondSchool> entry : ACTIVE_SCHOOLS.entrySet()) {
            PondSchool school = entry.getValue();
            if (!school.matches(dimensionId, fishStack) || !school.contains(requestedStartPosition)) {
                continue;
            }

            SwimmingFish candidate = school.findNearestAvailableFish(requestedStartPosition);
            if (candidate == null) {
                continue;
            }

            double distance = candidate.distanceToSqr(requestedStartPosition.x, requestedStartPosition.z);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestSchool = school;
                bestFish = candidate;
            }
        }

        if (bestSchool == null || bestFish == null) {
            return null;
        }

        int reservationTicks = Math.max(1, holdTicks + JUMP_BUFFER_TICKS);
        bestFish.reserve(bestSchool.lastSeenTick + reservationTicks);
        return new JumpFishBinding(
            new Vec3(bestFish.x, bestSchool.surfaceY - 0.05D, bestFish.z),
            bestFish.scale,
            bestFish.getYawDegrees(),
            bestFish.getPitch(0.0F)
        );
    }

    public static void render(FishPondBucketBlockEntity be,
                              float partialTick,
                              PoseStack poseStack,
                              MultiBufferSource buffer,
                              int packedLight,
                              int packedOverlay) {
        if (!(be.getLevel() instanceof ClientLevel level)) {
            return;
        }

        long gameTime = level.getGameTime();
        pruneInactive(gameTime);
        String schoolKey = buildKey(level, be);

        if (!be.hasFishVisuals()) {
            ACTIVE_SCHOOLS.remove(schoolKey);
            return;
        }

        PondSchool school = ACTIVE_SCHOOLS.computeIfAbsent(schoolKey, ignored -> new PondSchool());
        school.sync(level, be, gameTime);
        school.advance(level, gameTime, level.random);
        school.render(level, be, partialTick, poseStack, buffer, packedLight, packedOverlay);
    }

    private static void pruneInactive(long gameTime) {
        Iterator<PondSchool> iterator = ACTIVE_SCHOOLS.values().iterator();
        while (iterator.hasNext()) {
            PondSchool school = iterator.next();
            if (gameTime - school.lastSeenTick > 40L) {
                iterator.remove();
            }
        }
    }

    private static String buildKey(ClientLevel level, FishPondBucketBlockEntity be) {
        return buildDimensionPrefix(level.dimension().location().toString()) + be.getBlockPos().asLong();
    }

    private static final class PondSchool {
        private final List<SwimmingFish> fish = new ArrayList<>();
        private ItemStack fishStack = ItemStack.EMPTY;
        private String dimensionId = "";
        private double minX;
        private double maxX;
        private double minZ;
        private double maxZ;
        private double surfaceY;
        private long simulatedTick = Long.MIN_VALUE;
        private long lastSeenTick;

        public void sync(ClientLevel level, FishPondBucketBlockEntity be, long gameTime) {
            ItemStack nextStack = be.getFishSignPreview();
            int targetCount = Math.min(10, Math.max(0, be.getFishPopulation()));
            double nextMinX = Math.min(be.getFishAreaMinX(), be.getFishAreaMaxX());
            double nextMaxX = Math.max(be.getFishAreaMinX(), be.getFishAreaMaxX());
            double nextMinZ = Math.min(be.getFishAreaMinZ(), be.getFishAreaMaxZ());
            double nextMaxZ = Math.max(be.getFishAreaMinZ(), be.getFishAreaMaxZ());
            double nextSurfaceY = be.getWaterSurfaceY();
            boolean geometryChanged = fish.isEmpty()
                || Math.abs(minX - nextMinX) > 0.001D
                || Math.abs(maxX - nextMaxX) > 0.001D
                || Math.abs(minZ - nextMinZ) > 0.001D
                || Math.abs(maxZ - nextMaxZ) > 0.001D
                || Math.abs(surfaceY - nextSurfaceY) > 0.001D;
            boolean stackChanged = !ItemStack.matches(fishStack, nextStack);

            fishStack = nextStack.copy();
            dimensionId = level.dimension().location().toString();
            minX = nextMinX;
            maxX = nextMaxX;
            minZ = nextMinZ;
            maxZ = nextMaxZ;
            surfaceY = nextSurfaceY;
            lastSeenTick = gameTime;

            if (geometryChanged || stackChanged) {
                fish.clear();
            }

            RandomSource random = level.random;
            while (fish.size() < targetCount) {
                fish.add(SwimmingFish.create(random, minX, maxX, minZ, maxZ));
            }
            while (fish.size() > targetCount) {
                fish.remove(fish.size() - 1);
            }

            if (simulatedTick == Long.MIN_VALUE) {
                simulatedTick = gameTime;
            }
        }

        public void advance(ClientLevel level, long gameTime, RandomSource random) {
            if (fish.isEmpty()) {
                simulatedTick = gameTime;
                return;
            }

            long stepCount = Mth.clamp(gameTime - simulatedTick, 0L, 5L);
            for (long step = 0; step < stepCount; step++) {
                for (SwimmingFish swimmer : fish) {
                    swimmer.tick(random, minX, maxX, minZ, maxZ);
                    swimmer.spawnAmbientParticles(level, random, surfaceY, lastSeenTick);
                }
                nudgeApart();
                simulatedTick++;
            }
            if (simulatedTick < gameTime) {
                simulatedTick = gameTime;
            }
        }

        public boolean matches(String dimensionId, ItemStack stack) {
            return this.dimensionId.equals(dimensionId) && ItemStack.matches(fishStack, stack);
        }

        public boolean contains(Vec3 position) {
            return position.x >= minX - 0.35D
                && position.x <= maxX + 0.35D
                && position.z >= minZ - 0.35D
                && position.z <= maxZ + 0.35D;
        }

        public SwimmingFish findNearestAvailableFish(Vec3 position) {
            SwimmingFish bestFish = null;
            double bestDistance = Double.MAX_VALUE;
            for (SwimmingFish swimmer : fish) {
                if (swimmer.isReserved(lastSeenTick)) {
                    continue;
                }
                double distance = swimmer.distanceToSqr(position.x, position.z);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestFish = swimmer;
                }
            }
            return bestFish;
        }

        private void nudgeApart() {
            for (int i = 0; i < fish.size(); i++) {
                SwimmingFish left = fish.get(i);
                for (int j = i + 1; j < fish.size(); j++) {
                    SwimmingFish right = fish.get(j);
                    double dx = right.x - left.x;
                    double dz = right.z - left.z;
                    double distanceSq = dx * dx + dz * dz;
                    if (distanceSq < 0.0001D || distanceSq > 0.09D) {
                        continue;
                    }
                    double distance = Math.sqrt(distanceSq);
                    double push = (0.3D - distance) * 0.018D;
                    double pushX = (dx / distance) * push;
                    double pushZ = (dz / distance) * push;
                    left.x -= pushX;
                    left.z -= pushZ;
                    right.x += pushX;
                    right.z += pushZ;
                }
            }
        }

        public void render(ClientLevel level,
                   FishPondBucketBlockEntity be,
                           float partialTick,
                           PoseStack poseStack,
                           MultiBufferSource buffer,
                           int packedLight,
                           int packedOverlay) {
            if (fishStack.isEmpty() || fish.isEmpty()) {
                return;
            }

            for (SwimmingFish swimmer : fish) {
                if (swimmer.isReserved(lastSeenTick)) {
                    continue;
                }
                float swimPitch = 10.0F + swimmer.getPitch(partialTick);
                float swimRoll = swimmer.getRoll(partialTick);
                poseStack.pushPose();
                poseStack.translate(
                    swimmer.x,
                    surfaceY - 0.38D - swimmer.depth + swimmer.getBob(partialTick),
                    swimmer.z
                );
                ClientFishPondFishRenderer.renderFish(
                    fishStack,
                    poseStack,
                    buffer,
                    level,
                    packedLight,
                    swimmer.getYawDegrees(),
                    swimPitch,
                    swimRoll,
                    swimmer.scale,
                    false
                );
                poseStack.popPose();
            }
        }
    }

    private static final class SwimmingFish {
        private double x;
        private double z;
        private double heading;
        private double targetHeading;
        private double speed;
        private double targetSpeed;
        private double depth;
        private double targetDepth;
        private final float phase;
        private final float scale;
        private int ageTicks;
        private int retargetTicks;
        private long reservedUntilTick = Long.MIN_VALUE;

        private SwimmingFish(double x, double z, double heading, double speed, double depth, float phase, float scale) {
            this.x = x;
            this.z = z;
            this.heading = heading;
            this.targetHeading = heading;
            this.speed = speed;
            this.targetSpeed = speed;
            this.depth = depth;
            this.targetDepth = depth;
            this.phase = phase;
            this.scale = scale;
        }

        public static SwimmingFish create(RandomSource random, double minX, double maxX, double minZ, double maxZ) {
            double safeMinX = minX + BOUNDS_PADDING;
            double safeMaxX = Math.max(safeMinX + 0.1D, maxX - BOUNDS_PADDING);
            double safeMinZ = minZ + BOUNDS_PADDING;
            double safeMaxZ = Math.max(safeMinZ + 0.1D, maxZ - BOUNDS_PADDING);
            return new SwimmingFish(
                Mth.lerp(random.nextDouble(), safeMinX, safeMaxX),
                Mth.lerp(random.nextDouble(), safeMinZ, safeMaxZ),
                random.nextDouble() * (Math.PI * 2.0D),
                0.008D + random.nextDouble() * 0.008D,
                0.14D + random.nextDouble() * 0.14D,
                random.nextFloat() * (float) (Math.PI * 2.0D),
                0.78F + random.nextFloat() * 0.12F
            );
        }

        public void tick(RandomSource random, double minX, double maxX, double minZ, double maxZ) {
            ageTicks++;
            if (retargetTicks-- <= 0) {
                targetHeading += (random.nextDouble() - 0.5D) * 1.35D;
                targetSpeed = Mth.clamp(0.0065D + random.nextDouble() * 0.015D, 0.0065D, 0.021D);
                targetDepth = Mth.clamp(0.11D + random.nextDouble() * 0.24D, 0.11D, 0.35D);
                retargetTicks = 12 + random.nextInt(24);
            }

            heading = rotateToward(heading, targetHeading, 0.08D);
            speed = Mth.lerp(0.08D, speed, targetSpeed);
            depth = Mth.lerp(0.06D, depth, targetDepth);

            x += Math.cos(heading) * speed;
            z += Math.sin(heading) * speed;

            double safeMinX = minX + BOUNDS_PADDING;
            double safeMaxX = Math.max(safeMinX + 0.1D, maxX - BOUNDS_PADDING);
            double safeMinZ = minZ + BOUNDS_PADDING;
            double safeMaxZ = Math.max(safeMinZ + 0.1D, maxZ - BOUNDS_PADDING);

            if (x <= safeMinX || x >= safeMaxX) {
                x = Mth.clamp(x, safeMinX, safeMaxX);
                heading = Math.PI - heading;
                targetHeading = heading;
            }
            if (z <= safeMinZ || z >= safeMaxZ) {
                z = Mth.clamp(z, safeMinZ, safeMaxZ);
                heading = -heading;
                targetHeading = heading;
            }
        }

        public double distanceToSqr(double targetX, double targetZ) {
            double dx = x - targetX;
            double dz = z - targetZ;
            return dx * dx + dz * dz;
        }

        public boolean isReserved(long gameTime) {
            return gameTime <= reservedUntilTick;
        }

        public void reserve(long untilTick) {
            reservedUntilTick = Math.max(reservedUntilTick, untilTick);
        }

        public void spawnAmbientParticles(ClientLevel level, RandomSource random, double surfaceY, long gameTime) {
            if (isReserved(gameTime) || random.nextFloat() >= 0.025F) {
                return;
            }

            level.addParticle(
                ParticleTypes.BUBBLE,
                x + (random.nextDouble() - 0.5D) * 0.08D,
                surfaceY - depth + 0.04D,
                z + (random.nextDouble() - 0.5D) * 0.08D,
                (random.nextDouble() - 0.5D) * 0.005D,
                0.012D + random.nextDouble() * 0.008D,
                (random.nextDouble() - 0.5D) * 0.005D
            );
        }

        public float getYawDegrees() {
            return (float) Math.toDegrees(heading);
        }

        public float getPitch(float partialTick) {
            return (float) Math.sin((ageTicks + partialTick) * 0.25D + phase) * 4.0F;
        }

        public float getRoll(float partialTick) {
            return (float) Math.sin((ageTicks + partialTick) * 0.35D + phase) * 10.0F;
        }

        public double getBob(float partialTick) {
            return Math.sin((ageTicks + partialTick) * 0.18D + phase) * 0.025D;
        }

        private static double rotateToward(double current, double target, double maxStep) {
            double delta = Mth.wrapDegrees((float) Math.toDegrees(target - current));
            double step = Math.toRadians(Mth.clamp((float) delta, (float) -Math.toDegrees(maxStep), (float) Math.toDegrees(maxStep)));
            return current + step;
        }
    }

    public record JumpFishBinding(Vec3 startPosition, float scale, float yawDegrees, float pitchDegrees) {
    }

    private static String buildDimensionPrefix(String dimensionId) {
        return dimensionId + ":";
    }
}