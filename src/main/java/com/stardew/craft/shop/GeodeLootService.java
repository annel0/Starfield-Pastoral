package com.stardew.craft.shop;

import com.stardew.craft.item.ModItems;
import com.stardew.craft.network.ItemPickupHudPacket;
import com.stardew.craft.network.payload.GeodeCrackResultPayload;
import com.stardew.craft.player.PlayerStardewDataAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Server-side geode processing service — SDV Utility.getTreasureFromGeode() parity.
 *
 * SDV loot resolution order:
 * 1. Omni geode: 0.8 % prismatic shard (after 16 geodes cracked)
 * 2. Data-driven GeodeDrops (Objects.json RandomItemId per geode type)
 *    → 50 % chance when GeodeDropsDefaultItems = true
 * 3. Hardcoded fallback:
 *    a. 50 % stone / clay / type-gem
 *    b. 50 % ore (pool differs by geode type)
 */
@SuppressWarnings({"unchecked", "null"})
public class GeodeLootService {

    private static final int GEODE_COST = 25;

    /**
     * Pending treasure per player — SDV only gives treasure when geodeAnimationTimer <= 0.
     * Stored here until the client sends GeodeClaimPayload after animation finishes.
     */
    private static final Map<UUID, ItemStack> pendingTreasure = new ConcurrentHashMap<>();

    // ────────────────────────────────────────────────────────────────────
    //  SDV Objects.json GeodeDrops — data-driven mineral pools
    // ────────────────────────────────────────────────────────────────────

    // Geode (535): 16 minerals
    private static final Supplier<Item>[] GEODE_MINERALS = new Supplier[]{
        ModItems.ALAMITE, ModItems.CALCITE, ModItems.JAMBORITE, ModItems.JAGOITE,
        ModItems.MALACHITE, ModItems.NEKOITE, ModItems.ORPIMENT, ModItems.PETRIFIED_SLIME,
        ModItems.THUNDER_EGG, ModItems.CELESTINE, ModItems.SANDSTONE, ModItems.GRANITE,
        ModItems.LIMESTONE_MINERAL, ModItems.MUDSTONE, ModItems.SLATE, ModItems.DWARVISH_HELM
    };

    // Frozen Geode (536): 15 minerals
    private static final Supplier<Item>[] FROZEN_GEODE_MINERALS = new Supplier[]{
        ModItems.AERINITE, ModItems.ESPERITE, ModItems.FLUORAPATITE, ModItems.GEMINITE,
        ModItems.KYANITE, ModItems.LUNARITE, ModItems.PYRITE, ModItems.OCEAN_STONE,
        ModItems.GHOST_CRYSTAL, ModItems.OPAL, ModItems.MARBLE, ModItems.SOAPSTONE,
        ModItems.HEMATITE, ModItems.FAIRY_STONE, ModItems.ANCIENT_DRUM
    };

    // Magma Geode (537): 13 minerals
    private static final Supplier<Item>[] MAGMA_GEODE_MINERALS = new Supplier[]{
        ModItems.BIXITE, ModItems.BARYTE, ModItems.DOLOMITE, ModItems.HELVITE,
        ModItems.NEPTUNITE, ModItems.LEMON_STONE, ModItems.TIGERSEYE, ModItems.JASPER,
        ModItems.FIRE_OPAL, ModItems.BASALT, ModItems.OBSIDIAN, ModItems.STAR_SHARDS,
        ModItems.DWARF_GADGET
    };

    // Omni Geode (749): all three pools combined
    private static final Supplier<Item>[] OMNI_GEODE_MINERALS;
    static {
        List<Supplier<Item>> all = new ArrayList<>();
        Collections.addAll(all, GEODE_MINERALS);
        Collections.addAll(all, FROZEN_GEODE_MINERALS);
        Collections.addAll(all, MAGMA_GEODE_MINERALS);
        OMNI_GEODE_MINERALS = all.toArray(new Supplier[0]);
    }

    // ────────────────────────────────────────────────────────────────────

    public static void handleGeodeCrack(ServerPlayer player, int slot) {
        if (slot < 0 || slot >= player.getInventory().getContainerSize()) return;

        ItemStack geodeStack = player.getInventory().getItem(slot);
        if (geodeStack.isEmpty()) return;

        String geodeType = getGeodeType(geodeStack);
        if (geodeType == null) return;

        int money = PlayerStardewDataAPI.getMoney(player);
        if (money < GEODE_COST) return;

        int freeSlots = countFreeSlots(player);
        if (freeSlots < 1) return;

        // Deduct cost (SDV: Game1.player.Money -= 25)
        PlayerStardewDataAPI.removeMoney(player, GEODE_COST);

        // Consume one geode
        geodeStack.shrink(1);

        // Determine treasure (SDV parity)
        ItemStack treasure = getTreasureFromGeode(geodeType, player);

        // SDV: treasure is given to player ONLY when geodeAnimationTimer <= 0 in update().
        // Store pending treasure — the client will send GeodeClaimPayload after animation finishes.
        if (!treasure.isEmpty()) {
            pendingTreasure.put(player.getUUID(), treasure.copy());
        }

        // Sync the geode consumption to client (geode stack shrunk above).
        player.inventoryMenu.broadcastChanges();

        int newMoney = PlayerStardewDataAPI.getMoney(player);
        String treasureId = BuiltInRegistries.ITEM.getKey(treasure.getItem()).toString();
        PacketDistributor.sendToPlayer(player,
            new GeodeCrackResultPayload(treasureId, geodeType, newMoney));
    }

    /**
     * Called when client sends GeodeClaimPayload (animation finished).
     * Matches SDV: Game1.player.addItemToInventoryBool(geodeTreasure) at timer <= 0.
     */
    public static void handleGeodeClaim(ServerPlayer player) {
        ItemStack treasure = pendingTreasure.remove(player.getUUID());
        if (treasure != null && !treasure.isEmpty()) {
            if (!player.getInventory().add(treasure)) {
                player.drop(treasure, false);
            }
            ItemPickupHudPacket.sendTo(player, treasure, treasure.getCount(), false);
            player.inventoryMenu.broadcastChanges();
        }
    }

    /**
     * Clean up pending treasure on player disconnect.
     * If the player had a pending geode treasure, give it back so it's not lost.
     */
    public static void onPlayerLogout(ServerPlayer player) {
        ItemStack treasure = pendingTreasure.remove(player.getUUID());
        if (treasure != null && !treasure.isEmpty()) {
            if (!player.getInventory().add(treasure)) {
                player.drop(treasure, false);
            }
        }
    }

    /**
     * SDV Utility.getTreasureFromGeode() parity (excluding mystery boxes / artifact trove).
     */
    private static ItemStack getTreasureFromGeode(String geodeType, ServerPlayer player) {
        Random r = new Random();
        // SDV: prewarm random (mimics seed-based RNG warm-up)
        int prewarm = r.nextInt(9) + 1;
        for (int i = 0; i < prewarm; i++) r.nextDouble();
        prewarm = r.nextInt(9) + 1;
        for (int i = 0; i < prewarm; i++) r.nextDouble();

        // ── Omni geode: 0.8 % prismatic shard ──
        // SDV: requires ≥ 16 geodes cracked; we always allow for simplicity
        if (geodeType.equals("omni_geode") && r.nextDouble() < 0.008) {
            return new ItemStack(ModItems.PRISMATIC_SHARD.get());
        }

        // ── Data-driven GeodeDrops ──
        // SDV: GeodeDropsDefaultItems = true for all four geode types
        //      → 50 % chance (!defaultItems || r.NextBool()) to use data pool
        Supplier<Item>[] pool = getMineralPool(geodeType);
        if (pool.length > 0 && r.nextBoolean()) {
            Item chosen = pool[r.nextInt(pool.length)].get();
            return new ItemStack(chosen);
        }

        // ── Hardcoded fallback ──
        // SDV: amount = r.Next(3)*2+1, 10% → 10, 1% → 20
        int amount = r.nextInt(3) * 2 + 1;
        if (r.nextDouble() < 0.1) amount = 10;
        if (r.nextDouble() < 0.01) amount = 20;

        // SDV: 50 % stone / clay / type-gem
        if (r.nextBoolean()) {
            switch (r.nextInt(4)) {
                case 0: case 1:
                    return new ItemStack(ModItems.STONE.get(), amount);
                case 2:
                    return new ItemStack(ModItems.CLAY.get());
                default:
                    return getTypeGem(geodeType, r);
            }
        }

        // SDV: ore fallback (differs by geode type)
        return getOreByType(geodeType, r, amount);
    }

    /** SDV type-specific gem: geode→earth_crystal, frozen→frozen_tear, magma→fire_quartz, omni→random */
    private static ItemStack getTypeGem(String geodeType, Random r) {
        return switch (geodeType) {
            case "omni_geode" -> switch (r.nextInt(3)) {
                // SDV: "(O)" + (82 + r.Next(3)*2) → 82, 84, 86
                case 0 -> new ItemStack(ModItems.FIRE_QUARTZ.get());
                case 1 -> new ItemStack(ModItems.FROZEN_TEAR.get());
                default -> new ItemStack(ModItems.EARTH_CRYSTAL.get());
            };
            case "geode" -> new ItemStack(ModItems.EARTH_CRYSTAL.get());       // SDV "(O)86"
            case "frozen_geode" -> new ItemStack(ModItems.FROZEN_TEAR.get());  // SDV "(O)84"
            default -> new ItemStack(ModItems.FIRE_QUARTZ.get());              // magma → SDV "(O)82"
        };
    }

    /** SDV ore fallback by geode type. */
    private static ItemStack getOreByType(String geodeType, Random r, int amount) {
        return switch (geodeType) {
            // SDV geode (535): copper / iron(if deep) / coal — 3 choices
            case "geode" -> switch (r.nextInt(3)) {
                case 0 -> new ItemStack(ModItems.COPPER_ORE.get(), amount);
                case 1 -> new ItemStack(ModItems.IRON_ORE.get(), amount);
                default -> new ItemStack(ModItems.COAL.get(), amount);
            };
            // SDV frozen geode (536): copper / iron / coal / gold(if deep) — 4 choices
            case "frozen_geode" -> switch (r.nextInt(4)) {
                case 0 -> new ItemStack(ModItems.COPPER_ORE.get(), amount);
                case 1 -> new ItemStack(ModItems.IRON_ORE.get(), amount);
                case 2 -> new ItemStack(ModItems.COAL.get(), amount);
                default -> new ItemStack(ModItems.GOLD_ORE.get(), amount);
            };
            // SDV magma geode (537) / omni geode (749): copper / iron / coal / gold / iridium
            default -> switch (r.nextInt(5)) {
                case 0 -> new ItemStack(ModItems.COPPER_ORE.get(), amount);
                case 1 -> new ItemStack(ModItems.IRON_ORE.get(), amount);
                case 2 -> new ItemStack(ModItems.COAL.get(), amount);
                case 3 -> new ItemStack(ModItems.GOLD_ORE.get(), amount);
                default -> new ItemStack(ModItems.IRIDIUM_ORE.get(), amount / 2 + 1);
            };
        };
    }

    private static Supplier<Item>[] getMineralPool(String geodeType) {
        return switch (geodeType) {
            case "geode" -> GEODE_MINERALS;
            case "frozen_geode" -> FROZEN_GEODE_MINERALS;
            case "magma_geode" -> MAGMA_GEODE_MINERALS;
            case "omni_geode" -> OMNI_GEODE_MINERALS;
            default -> new Supplier[0];
        };
    }

    private static String getGeodeType(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String path = id.getPath();
        return switch (path) {
            case "geode" -> "geode";
            case "frozen_geode" -> "frozen_geode";
            case "magma_geode" -> "magma_geode";
            case "omni_geode" -> "omni_geode";
            default -> null;
        };
    }

    private static int countFreeSlots(ServerPlayer player) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            if (player.getInventory().getItem(i).isEmpty()) count++;
        }
        return count;
    }
}
