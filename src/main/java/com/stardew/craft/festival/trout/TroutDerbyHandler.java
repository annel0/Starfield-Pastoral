package com.stardew.craft.festival.trout;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.festival.FestivalDefinition;
import com.stardew.craft.festival.FestivalSessionState;
import com.stardew.craft.festival.PassiveFestivalHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class TroutDerbyHandler implements PassiveFestivalHandler {
    private static final String DISPLAY_TAG = "stardewcraft_trout_derby_display";
    private static final AABB DISPLAY_CLEANUP_BOUNDS = new AABB(-150.0D, 64.0D, 81.0D, -135.0D, 69.0D, 89.0D);

    private static final List<DisplaySpec> DISPLAYS = List.of(
        new DisplaySpec(
            "rainbow_trout",
            new Vec3(-141.0625D, 67.5D, 88.0D),
            "{item:{components:{\"minecraft:custom_data\":{Quality:0}},count:1,id:\"stardewcraft:rainbow_trout\"},transformation:{left_rotation:[0.0f,0.0f,0.38268346f,0.9238795f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[2.0f,2.0f,1.0f],translation:[0.0f,0.0f,0.0f]}}"
        ),
        new DisplaySpec(
            "mystery_box",
            new Vec3(-146.75D, 66.0D, 88.0D),
            "{item:{components:{\"minecraft:custom_data\":{StardewForge:{AppearanceWeaponId:\"\",DiamondForge:0b,DragonToothEnchantment:\"\",GalaxySoulLevel:0,GemForges:[],PreviousEnchantments:[],PrismaticEnchantment:\"\"},StardewWeapon:{CritChance:0.02f,CritPower:0.0f,Defense:0,Knockback:0.0f,MaxDamage:5.0f,MinDamage:2.0f,Precision:0.0f,Speed:0,Type:0}},\"minecraft:custom_name\":'{\"color\":\"yellow\",\"italic\":false,\"text\":\"stardewcraft:item/forge_appearance/dwarf_sword\"}'},count:1,id:\"stardewcraft:mystery_box\"},transformation:{left_rotation:[0.0f,1.0f,0.0f,0.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[1.0f,1.0f,1.0f],translation:[0.0f,0.0f,0.0f]}}"
        ),
        new DisplaySpec(
            "golden_tag",
            new Vec3(-148.375D, 66.0D, 88.0D),
            "{item:{count:1,id:\"stardewcraft:golden_tag\"},transformation:{left_rotation:[0.0f,1.0f,0.0f,0.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],scale:[1.0f,1.0f,1.0f],translation:[0.0f,0.0f,0.0f]}}"
        )
    );

    @Override
    public String festivalId() {
        return TroutDerbyService.FESTIVAL_ID;
    }

    @Override
    public void onNewDay(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
        TroutDerbyService.forceRefreshNpcSchedules(level);
    }

    @Override
    public void onOpen(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
        installDisplays(level);
        TroutDerbyService.forceRefreshNpcSchedules(level);
    }

    @Override
    public void onMapOverlayApplyStarted(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
        cleanupDisplays(level);
    }

    @Override
    public void onMapOverlayApplied(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
        installDisplays(level);
    }

    @Override
    public void onMapOverlayRestoreStarted(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
        cleanupDisplays(level);
    }

    @Override
    public void onMapOverlayRestored(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
        cleanupDisplays(level);
    }

    @Override
    public void onCleanup(ServerLevel level, FestivalDefinition definition, FestivalSessionState session) {
        cleanupDisplays(level);
        TroutDerbyService.forceRefreshNpcSchedules(level);
    }

    private static void installDisplays(ServerLevel level) {
        cleanupDisplays(level);
        for (DisplaySpec display : DISPLAYS) {
            spawnDisplay(level, display);
        }
    }

    private static void cleanupDisplays(ServerLevel level) {
        level.getEntitiesOfClass(Display.ItemDisplay.class, DISPLAY_CLEANUP_BOUNDS, entity -> entity.getTags().contains(DISPLAY_TAG))
            .forEach(Entity::discard);
    }

    private static void spawnDisplay(ServerLevel level, DisplaySpec display) {
        try {
            CompoundTag tag = TagParser.parseTag(display.snbt());
            tag.putString("id", "minecraft:item_display");
            tag.put("Pos", posTag(display.pos()));
            tag.put("Tags", tagsTag());

            Entity entity = EntityType.loadEntityRecursive(tag, level, loadedEntity -> loadedEntity);
            if (entity != null) {
                level.addFreshEntity(entity);
            }
        } catch (Exception exception) {
            StardewCraft.LOGGER.error("[TROUT_DERBY] Failed to spawn display {}", display.id(), exception);
        }
    }

    private static ListTag posTag(Vec3 pos) {
        ListTag tag = new ListTag();
        tag.add(DoubleTag.valueOf(pos.x));
        tag.add(DoubleTag.valueOf(pos.y));
        tag.add(DoubleTag.valueOf(pos.z));
        return tag;
    }

    private static ListTag tagsTag() {
        ListTag tags = new ListTag();
        tags.add(StringTag.valueOf(DISPLAY_TAG));
        return tags;
    }

    private record DisplaySpec(String id, Vec3 pos, String snbt) {
    }
}