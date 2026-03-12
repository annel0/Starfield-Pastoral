
$content = Get-Content src/main/java/com/stardew/craft/client/gui/CookingPotScreen.java -Raw

if ($content -notmatch "getStardewBuffIcon") {
    $content = $content -replace "import net.minecraft.world.item.Items;", "import net.minecraft.world.item.Items;`nimport net.minecraft.util.FormattedCharSequence;`nimport com.mojang.blaze3d.platform.InputConstants;"
    
    $methodStr = @"
    private ResourceLocation getStardewBuffIcon(CookingDishItem.BuffType type) {
        String effectStr = switch (type) {
            case MAX_ENERGY -> "vigorous";
            case FISHING -> "sea_king_blessing";
            case LUCK -> "spirit_blessing";
            case SPEED -> "speed";
            case FARMING -> "farmer_blessing";
            case FORAGING -> "forager_blessing";
            case MINING -> "miner_blessing";
            case ATTACK -> "warrior_blessing";
            case DEFENSE -> "guardian_blessing";
            case MAGNETIC_RADIUS -> "magnetism";
        };
        return ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/mob_effect/" + effectStr + ".png");
    }
"@
    $content = $content -replace "private int countMatching\(String token\)", "$methodStr`n`n    private int countMatching(String token)"
    Set-Content -Path src/main/java/com/stardew/craft/client/gui/CookingPotScreen.java -Value $content
}

