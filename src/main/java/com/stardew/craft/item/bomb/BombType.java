package com.stardew.craft.item.bomb;

import com.stardew.craft.item.ModItems;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

/**
 * SDV 三种炸弹的参数定义。
 *
 * <p>SDV 原版半径（瓦片 = MC 方块）：Cherry=1, Bomb=2, Mega=3。
 * 本 mod 在原版半径基础上 +20% 作为实际爆炸范围。</p>
 *
 * <pre>
 * Cherry Bomb: radius=1 (+20%=1.2), damage=6~8(r*6~r*8), playerDmg=3(r*3), fuse=2400ms
 * Bomb:        radius=2 (+20%=2.4), damage=12~16,        playerDmg=6,      fuse=2400ms
 * Mega Bomb:   radius=3 (+20%=3.6), damage=18~24,        playerDmg=9,      fuse=2400ms
 * </pre>
 */
public enum BombType {
    CHERRY_BOMB("cherry_bomb", 1, 2400, () -> ModItems.CHERRY_BOMB),
    BOMB("bomb_item", 2, 2400, () -> ModItems.BOMB_ITEM),
    MEGA_BOMB("mega_bomb", 3, 2400, () -> ModItems.MEGA_BOMB);

    /** 爆炸范围相对 SDV 原版的缩放比例 */
    private static final float RADIUS_SCALE = 2.4f;

    private final String id;
    private final int radius;
    private final int fuseTimeMs;
    private final Supplier<DeferredItem<Item>> itemSupplier;

    BombType(String id, int radius, int fuseTimeMs, Supplier<DeferredItem<Item>> itemSupplier) {
        this.id = id;
        this.radius = radius;
        this.fuseTimeMs = fuseTimeMs;
        this.itemSupplier = itemSupplier;
    }

    public String getId() { return id; }

    /** SDV 原版半径（用于伤害计算） */
    public int getRadius() { return radius; }

    /** 实际爆炸半径 = SDV 原版 * 1.2 */
    public float getScaledRadius() { return radius * RADIUS_SCALE; }

    /** Fuse time in game ticks (20 ticks/sec). SDV uses 2400ms = 48 ticks. */
    public int getFuseTicks() { return fuseTimeMs / 50; }

    /** Monster min damage = radius * 6 */
    public int getMinDamage() { return radius * 6; }

    /** Monster max damage = radius * 8 */
    public int getMaxDamage() { return radius * 8; }

    /** Player self-damage = radius * 3 */
    public int getPlayerDamage() { return radius * 3; }

    /** Screen shake duration in ticks: (300 + radius * 100) ms → ticks */
    public int getShakeDurationTicks() { return (300 + radius * 100) / 50; }

    public Item getItem() { return itemSupplier.get().get(); }

    public static BombType fromId(String id) {
        for (BombType t : values()) {
            if (t.id.equals(id)) return t;
        }
        return CHERRY_BOMB;
    }

    public static BombType fromOrdinal(int ordinal) {
        BombType[] vals = values();
        return (ordinal >= 0 && ordinal < vals.length) ? vals[ordinal] : CHERRY_BOMB;
    }
}
