package com.stardew.craft.shop;

import com.stardew.craft.item.ModItems;
import com.stardew.craft.network.ItemPickupHudPacket;
import com.stardew.craft.network.payload.PrizeTicketClaimResultPayload;
import com.stardew.craft.network.payload.PrizeTicketRewardPreview;
import com.stardew.craft.player.PlayerDataEventHandler;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

/** Server-side Prize Machine reward logic, matching SDV PrizeTicketMenu reward order. */
@SuppressWarnings("null")
public final class PrizeTicketRewardService {
    private static final int MAX_SKIP_SCAN = 32;

    private PrizeTicketRewardService() {}

    public record PrizeReward(ItemStack stack, int prizeLevel) {}

    public static Optional<PrizeReward> getNextAvailableReward(ServerPlayer player, int startingPrizeLevel) {
        int start = Math.max(0, startingPrizeLevel);
        for (int prizeLevel = start; prizeLevel < start + MAX_SKIP_SCAN; prizeLevel++) {
            ItemStack stack = getRewardForPrizeLevel(player, prizeLevel);
            if (!stack.isEmpty()) {
                return Optional.of(new PrizeReward(stack, prizeLevel));
            }
        }
        return Optional.empty();
    }

    public static List<PrizeTicketRewardPreview> getPreviewRewards(ServerPlayer player, int startingPrizeLevel, int count) {
        List<PrizeTicketRewardPreview> previews = new ArrayList<>();
        int nextPrizeLevel = Math.max(0, startingPrizeLevel);
        for (int index = 0; index < count; index++) {
            Optional<PrizeReward> reward = getNextAvailableReward(player, nextPrizeLevel);
            if (reward.isEmpty()) {
                break;
            }
            PrizeReward preview = reward.get();
            ItemStack stack = preview.stack();
            previews.add(new PrizeTicketRewardPreview(
                BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(),
                stack.getCount(),
                preview.prizeLevel()
            ));
            nextPrizeLevel = preview.prizeLevel() + 1;
        }
        return previews;
    }

    public static void handlePrizeTicketClaim(ServerPlayer player) {
        PlayerStardewData data = PlayerDataManager.getPlayerData(player);
        int previousClaimed = data.getTicketPrizesClaimed();
        Optional<PrizeReward> reward = getNextAvailableReward(player, previousClaimed);
        if (reward.isEmpty() || !consumePrizeTicket(player)) {
            PacketDistributor.sendToPlayer(player,
                new PrizeTicketClaimResultPayload(false, "", 0, previousClaimed, previousClaimed, -1,
                    getPreviewRewards(player, previousClaimed, 4)));
            return;
        }

        PrizeReward claimed = reward.get();
        ItemStack prize = claimed.stack().copy();
        ItemStack hudStack = prize.copy();
        data.setTicketPrizesClaimed(claimed.prizeLevel() + 1);
        PlayerDataEventHandler.syncPlayerData(player, data);

        if (!player.getInventory().add(prize)) {
            player.drop(prize, false);
        }
        ItemPickupHudPacket.sendTo(player, hudStack, hudStack.getCount(), false);
        player.inventoryMenu.broadcastChanges();

        String itemId = BuiltInRegistries.ITEM.getKey(hudStack.getItem()).toString();
        PacketDistributor.sendToPlayer(player, new PrizeTicketClaimResultPayload(
            true,
            itemId,
            hudStack.getCount(),
            previousClaimed,
            data.getTicketPrizesClaimed(),
            claimed.prizeLevel(),
            getPreviewRewards(player, data.getTicketPrizesClaimed(), 4)
        ));
    }

    private static boolean consumePrizeTicket(ServerPlayer player) {
        Item ticket = ModItems.PRIZE_TICKET.get();
        if (player.getInventory().countItem(ticket) <= 0) {
            return false;
        }
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ticket)) {
                stack.shrink(1);
                player.inventoryMenu.broadcastChanges();
                return true;
            }
        }
        ItemStack offhand = player.getOffhandItem();
        if (offhand.is(ticket)) {
            offhand.shrink(1);
            player.inventoryMenu.broadcastChanges();
            return true;
        }
        return false;
    }

    private static ItemStack getRewardForPrizeLevel(ServerPlayer player, int prizeLevel) {
        if (prizeLevel < 0) {
            return ItemStack.EMPTY;
        }
        Random random = randomFor(player, prizeLevel);
        if (prizeLevel < 22) {
            return getFixedReward(player, prizeLevel, random);
        }
        int groupStart = prizeLevel - prizeLevel % 9;
        return getLoopReward(prizeLevel % 9, randomFor(player, groupStart));
    }

    private static ItemStack getFixedReward(ServerPlayer player, int prizeLevel, Random random) {
        return switch (prizeLevel) {
            case 0 -> stack(currentRaccoonSeed(), 12);
            case 1 -> randomFrom(random, List.of(
                stack(ModItems.PEACH_SAPLING, 1),
                stack(ModItems.ORANGE_SAPLING, 1)
            ));
            case 2 -> randomFrom(random, List.of(stack(ModItems.MIXED_SEEDS, 10)));
            case 3 -> stack(ModItems.MYSTERY_BOX, 3);
            case 4 -> stack(ModItems.STARDROP_TEA, 1);
            case 5 -> ItemStack.EMPTY;
            case 6 -> randomFrom(random, List.of(
                stack(ModItems.QUALITY_SPRINKLER, 4),
                stack(ModItems.PRESERVES_JAR, 4)
            ));
            case 7 -> randomFrom(random, List.of(
                stack(ModItems.APPLE_SAPLING, 1),
                stack(ModItems.POMEGRANATE_SAPLING, 1)
            ));
            case 8 -> stack(ModItems.BOOK_FRIENDSHIP, 1);
            case 9 -> randomFrom(random, List.of(
                stack(ModItems.CHERRY_BOMB, 20),
                stack(ModItems.BOMB_ITEM, 12),
                stack(ModItems.MEGA_BOMB, 6)
            ));
            case 10 -> ItemStack.EMPTY;
            case 11 -> randomFrom(random, List.of(
                stack(ModItems.FISH_SMOKER, 1),
                stack(ModItems.DEHYDRATOR, 1)
            ));
            case 12 -> randomFrom(random, List.of(
                stack(ModItems.ARTIFACT_TROVE, 4),
                stack(ModItems.MYSTERY_BOX, 4)
            ));
            case 13 -> ItemStack.EMPTY;
            case 14 -> randomFrom(random, List.of(
                stack(ModItems.SKILL_BOOK_0, 1),
                stack(ModItems.SKILL_BOOK_1, 1),
                stack(ModItems.SKILL_BOOK_2, 1),
                stack(ModItems.SKILL_BOOK_3, 1),
                stack(ModItems.SKILL_BOOK_4, 1)
            ));
            case 15 -> stack(ModItems.STARDROP_TEA, 1);
            case 16 -> ItemStack.EMPTY;
            case 17 -> stack(ModItems.OMNI_GEODE, 8);
            case 18 -> randomFrom(random, List.of(
                stack(ModItems.BEE_HOUSE, 4),
                stack(ModItems.KEG, 4)
            ));
            case 19 -> stack(ModItems.DIAMOND, 5);
            case 20 -> stack(ModItems.MYSTERY_BOX, 5);
            case 21 -> stack(ModItems.MAGIC_ROCK_CANDY, 1);
            default -> ItemStack.EMPTY;
        };
    }

    private static ItemStack getLoopReward(int remainder, Random random) {
        return switch (remainder) {
            case 0 -> stack(ModItems.MYSTERY_BOX, 5);
            case 1 -> stack(ModItems.FAIRY_DUST, random.nextInt(2) + 1);
            case 2 -> randomFrom(random, List.of(
                stack(ModItems.IRIDIUM_BAR, 5),
                stack(ModItems.ARTIFACT_TROVE, 5)
            ));
            case 3 -> ItemStack.EMPTY;
            case 4 -> stack(ModItems.STARDROP_TEA, 1);
            case 5 -> stack(ModItems.TREASURE_CHEST, 1);
            case 6 -> stack(ModItems.IRIDIUM_SPRINKLER, 1);
            case 7 -> ItemStack.EMPTY;
            case 8 -> randomFrom(random, List.of(
                stack(ModItems.BOMB_ITEM, 15),
                stack(ModItems.MEGA_BOMB, 8)
            ));
            default -> ItemStack.EMPTY;
        };
    }

    private static Supplier<? extends Item> currentRaccoonSeed() {
        int season = StardewTimeManager.get().getCurrentSeason();
        return switch (season) {
            case 1 -> ModItems.SUMMER_SQUASH_SEEDS;
            case 2 -> ModItems.BROCCOLI_SEEDS;
            case 3 -> ModItems.POWDER_MELON_SEEDS;
            default -> ModItems.CARROT_SEEDS;
        };
    }

    private static ItemStack randomFrom(Random random, List<ItemStack> candidates) {
        List<ItemStack> available = new ArrayList<>();
        for (ItemStack candidate : candidates) {
            if (!candidate.isEmpty()) {
                available.add(candidate);
            }
        }
        if (available.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return available.get(random.nextInt(available.size())).copy();
    }

    private static ItemStack stack(Supplier<? extends Item> item, int count) {
        return new ItemStack(item.get(), count);
    }

    private static Random randomFor(ServerPlayer player, int prizeLevel) {
        long uuidBits = player.getUUID().getMostSignificantBits() ^ player.getUUID().getLeastSignificantBits();
        long seed = player.serverLevel().getSeed() ^ uuidBits ^ ((long) prizeLevel * 0x9E3779B97F4A7C15L);
        return new Random(seed);
    }
}
