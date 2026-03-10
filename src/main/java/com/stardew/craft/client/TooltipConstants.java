package com.stardew.craft.client;

public class TooltipConstants {
    // 对应 font/default.json 中的 unicode
    public static final String ICON_MONEY = "\uE001";
    public static final String ICON_ENERGY = "\uE002";         // 正面能量（绿色闪电）
    public static final String ICON_HEALTH = "\uE003";         // 正面生命（红色心形）
    public static final String ICON_ENERGY_NEGATIVE = "\uE004"; // 负面能量（红色闪电）

    // Food Buff icons (bitmap font)
    public static final String ICON_BUFF_VIGOROUS = "\uE010";
    public static final String ICON_BUFF_SEA_KING_BLESSING = "\uE011";
    public static final String ICON_BUFF_SPIRIT_BLESSING = "\uE012";
    public static final String ICON_BUFF_SPEED = "\uE013";

    // Tooltip placeholder markers (should render as nothing)
    public static final String MARKER_WATER_AMOUNT = "\u200B"; // zero-width space
    public static final String MARKER_MAX_CHARGE_RANGE = "\u200C"; // zero-width non-joiner
	public static final String MARKER_FISHING_ROD_BAIT = "\u200D"; // zero-width joiner
	public static final String MARKER_FISHING_ROD_TACKLE = "\u200E"; // left-to-right mark
}
