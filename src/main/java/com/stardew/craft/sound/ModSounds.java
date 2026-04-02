package com.stardew.craft.sound;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Stardew Valley cue -> Minecraft SoundEvent mapping.
 *
 * Note: Minecraft sound IDs must be lowercase, so we use snake_case.
 */
public final class ModSounds {
	@SuppressWarnings("null")
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, StardewCraft.MODID);

	// FishingRod.cs / BobberBar.cs cues
	public static final DeferredHolder<SoundEvent, SoundEvent> SIN_WAVE = register("sin_wave");
	public static final DeferredHolder<SoundEvent, SoundEvent> CAST = register("cast");
	// Stardew: Farmer.PlayFishBiteChime() -> fishBite / fishBite_alternate_0..2
	public static final DeferredHolder<SoundEvent, SoundEvent> FISH_BITE = register("fish_bite");
	public static final DeferredHolder<SoundEvent, SoundEvent> FISH_BITE_ALTERNATE_0 = register("fish_bite_alternate_0");
	public static final DeferredHolder<SoundEvent, SoundEvent> FISH_BITE_ALTERNATE_1 = register("fish_bite_alternate_1");
	public static final DeferredHolder<SoundEvent, SoundEvent> FISH_BITE_ALTERNATE_2 = register("fish_bite_alternate_2");
	public static final DeferredHolder<SoundEvent, SoundEvent> FISH_HIT = register("fish_hit");
	public static final DeferredHolder<SoundEvent, SoundEvent> FISHING_ROD_BEND = register("fishing_rod_bend");
	public static final DeferredHolder<SoundEvent, SoundEvent> SHINY4 = register("shiny4");
	public static final DeferredHolder<SoundEvent, SoundEvent> FAST_REEL = register("fast_reel");
	public static final DeferredHolder<SoundEvent, SoundEvent> SLOW_REEL = register("slow_reel");
	public static final DeferredHolder<SoundEvent, SoundEvent> JINGLE1 = register("jingle1");
	public static final DeferredHolder<SoundEvent, SoundEvent> FISH_ESCAPE = register("fish_escape");
	public static final DeferredHolder<SoundEvent, SoundEvent> TINY_WHIP = register("tiny_whip");
	public static final DeferredHolder<SoundEvent, SoundEvent> WATER_SLOSH = register("water_slosh");
	public static final DeferredHolder<SoundEvent, SoundEvent> DWOP = register("dwop");
	public static final DeferredHolder<SoundEvent, SoundEvent> PULL_ITEM_FROM_WATER = register("pull_item_from_water");
	public static final DeferredHolder<SoundEvent, SoundEvent> DROP_ITEM_IN_WATER = register("drop_item_in_water");
	public static final DeferredHolder<SoundEvent, SoundEvent> NEW_ARTIFACT = register("new_artifact");
	// 宝箱打开音效
	public static final DeferredHolder<SoundEvent, SoundEvent> OPEN_CHEST = register("open_chest");
	public static final DeferredHolder<SoundEvent, SoundEvent> DOOR_CREAK = register("door_creak");
	public static final DeferredHolder<SoundEvent, SoundEvent> DOOR_CREAK_REVERSE = register("door_creak_reverse");
	public static final DeferredHolder<SoundEvent, SoundEvent> WOODY_STEP = register("woody_step");
	public static final DeferredHolder<SoundEvent, SoundEvent> BACKPACK_IN = register("backpack_in");
	public static final DeferredHolder<SoundEvent, SoundEvent> SHWIP = register("shwip");
	public static final DeferredHolder<SoundEvent, SoundEvent> BIG_SELECT = register("big_select");
	public static final DeferredHolder<SoundEvent, SoundEvent> BIG_DESELECT = register("big_deselect");
	public static final DeferredHolder<SoundEvent, SoundEvent> BREATHIN = register("breathin");
	public static final DeferredHolder<SoundEvent, SoundEvent> BREATHOUT = register("breathout");
	public static final DeferredHolder<SoundEvent, SoundEvent> COWBOY_GUNSHOT = register("cowboy_gunshot");
	public static final DeferredHolder<SoundEvent, SoundEvent> DIALOGUE_CHARACTER = register("dialogue_character");
	public static final DeferredHolder<SoundEvent, SoundEvent> DIALOGUE_CHARACTER_CLOSE = register("dialogue_character_close");
	public static final DeferredHolder<SoundEvent, SoundEvent> SHADOW_DIE = register("shadow_die");
	public static final DeferredHolder<SoundEvent, SoundEvent> THUD_STEP = register("thud_step");
	public static final DeferredHolder<SoundEvent, SoundEvent> STONE_STEP = register("stone_step");
	public static final DeferredHolder<SoundEvent, SoundEvent> HARVEST = register("harvest");
	public static final DeferredHolder<SoundEvent, SoundEvent> LEAFRUSTLE = register("leafrustle");
	public static final DeferredHolder<SoundEvent, SoundEvent> BUTTON1 = register("button1");
	public static final DeferredHolder<SoundEvent, SoundEvent> HAMMER = register("hammer");
	public static final DeferredHolder<SoundEvent, SoundEvent> COIN = register("coin");
	public static final DeferredHolder<SoundEvent, SoundEvent> MONEY_DIAL = register("money_dial");
	public static final DeferredHolder<SoundEvent, SoundEvent> TRASHCANLID = register("trashcanlid");
	public static final DeferredHolder<SoundEvent, SoundEvent> TRASHCAN = register("trashcan");
	public static final DeferredHolder<SoundEvent, SoundEvent> EXPLOSION = register("explosion");
	public static final DeferredHolder<SoundEvent, SoundEvent> CRIT = register("crit");
	public static final DeferredHolder<SoundEvent, SoundEvent> THROW_DOWN_ITEM = register("throw_down_item");

	// 武器音效
	public static final DeferredHolder<SoundEvent, SoundEvent> MEOW = register("weapon.meow");

	// Tree fall (from StardewValley.TerrainFeatures.Tree.performTreeFall)
	public static final DeferredHolder<SoundEvent, SoundEvent> TREE_CRACK = register("tree_crack");
	public static final DeferredHolder<SoundEvent, SoundEvent> TREE_THUD = register("tree_thud");

	// Utility / Machine cues
	public static final DeferredHolder<SoundEvent, SoundEvent> SHIP = register("ship");
	public static final DeferredHolder<SoundEvent, SoundEvent> BUBBLES = register("bubbles");
	public static final DeferredHolder<SoundEvent, SoundEvent> SIP_TEA = register("siptea");
	public static final DeferredHolder<SoundEvent, SoundEvent> YOBA = register("yoba");
	public static final DeferredHolder<SoundEvent, SoundEvent> FISH_SLAP = register("fishslap");
	public static final DeferredHolder<SoundEvent, SoundEvent> FURNACE = register("furnace");
	public static final DeferredHolder<SoundEvent, SoundEvent> OPENBOX = register("openbox");
	public static final DeferredHolder<SoundEvent, SoundEvent> FIREBALL = register("fireball");
	public static final DeferredHolder<SoundEvent, SoundEvent> CANCEL = register("cancel");
	public static final DeferredHolder<SoundEvent, SoundEvent> SELECT = register("select");

	// AnimalQueryMenu / FarmAnimal original cues
	public static final DeferredHolder<SoundEvent, SoundEvent> SMALL_SELECT = register("small_select");
	public static final DeferredHolder<SoundEvent, SoundEvent> GIVE_GIFT = register("give_gift");
	public static final DeferredHolder<SoundEvent, SoundEvent> DRUMKIT6 = register("drumkit6");
	public static final DeferredHolder<SoundEvent, SoundEvent> NEW_RECIPE = register("new_recipe");
	public static final DeferredHolder<SoundEvent, SoundEvent> MONEY = register("money");

	// ShopMenu cues
	public static final DeferredHolder<SoundEvent, SoundEvent> PURCHASE = register("purchase");
	public static final DeferredHolder<SoundEvent, SoundEvent> SELL = register("sell");
	public static final DeferredHolder<SoundEvent, SoundEvent> PURCHASE_CLICK = register("purchase_click");
	public static final DeferredHolder<SoundEvent, SoundEvent> PURCHASE_REPEAT = register("purchase_repeat");

	public static final DeferredHolder<SoundEvent, SoundEvent> CLUCK = register("cluck");
	public static final DeferredHolder<SoundEvent, SoundEvent> DUCK = register("duck");
	public static final DeferredHolder<SoundEvent, SoundEvent> RABBIT = register("rabbit");
	public static final DeferredHolder<SoundEvent, SoundEvent> OSTRICH = register("ostrich");
	public static final DeferredHolder<SoundEvent, SoundEvent> COW = register("cow");
	public static final DeferredHolder<SoundEvent, SoundEvent> GOAT = register("goat");
	public static final DeferredHolder<SoundEvent, SoundEvent> SHEEP = register("sheep");
	public static final DeferredHolder<SoundEvent, SoundEvent> PIG = register("pig");

    // Level up sound
    public static final DeferredHolder<SoundEvent, SoundEvent> LEVEL_UP = register("level_up");

	@SuppressWarnings("null")
	private static DeferredHolder<SoundEvent, SoundEvent> register(String path) {
		@SuppressWarnings("null")
		ResourceLocation id = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, path);
		return SOUND_EVENTS.register(path, () -> SoundEvent.createVariableRangeEvent(id));
	}
}
