package com.stardew.craft.item.bomb;

import com.stardew.craft.item.ModItems;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

/**
 * SDV 三种炸弹的参数定义。
 *
 * <p>SDV 原版半径（瓦片 = MC 方块）：Cherry=1, Bomb=2, Mega=3。
 * MC 中炸弹保留 3D 爆破直觉，但中大型炸弹的方块破坏半径需要小于旧实现。</p>
 *
 * <pre>
 * Cherry Bomb: radius=1 (break=1.8), damage=6~8(r*6~r*8), playerDmg=3(r*3), fuse=2400ms
 * Bomb:        radius=2 (break=2.5), damage=12~16,       playerDmg=6,      fuse=2400ms
 * Mega Bomb:   radius=3 (break=3.5), damage=18~24,       playerDmg=9,      fuse=2400ms
 * </pre>
 */
public enum BombType {
    CHERRY_BOMB("cherry_bomb", 1, 1.8f, 2400, () -> ModItems.CHERRY_BOMB),
    BOMB("bomb_item", 2, 2.5f, 2400, () -> ModItems.BOMB_ITEM),
    MEGA_BOMB("mega_bomb", 3, 3.5f, 2400, () -> ModItems.MEGA_BOMB);

    private final String id;
    private final int radius;
    private final float blockBreakRadius;
    private final int fuseTimeMs;
    private final Supplier<DeferredItem<Item>> itemSupplier;

    BombType(String id, int radius, float blockBreakRadius, int fuseTimeMs, Supplier<DeferredItem<Item>> itemSupplier) {
        this.id = id;
        this.radius = radius;
        this.blockBreakRadius = blockBreakRadius;
        this.fuseTimeMs = fuseTimeMs;
        this.itemSupplier = itemSupplier;
    }

    public String getId() { return id; }

    /** SDV 原版半径（用于伤害计算） */
    public int getRadius() { return radius; }

    /** 实际方块破坏半径。伤害仍使用 SDV 原版半径计算。 */
    public float getScaledRadius() { return blockBreakRadius; }

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
