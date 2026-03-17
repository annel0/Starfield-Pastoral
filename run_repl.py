import os

def repl(file_path, old_str, new_str):
    with open(file_path, 'r', encoding='utf-8') as f:
        text = f.read()
    if old_str in text:
        text = text.replace(old_str, new_str)
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(text)
        print(f"Replaced in {file_path}")

# MoneyDial
file = 'src/main/java/com/stardew/craft/client/gui/overnight/MoneyDial.java'
repl(file, 'import net.neoforged.api.distmarker.OnlyIn;', 'import net.neoforged.api.distmarker.OnlyIn;\nimport net.minecraft.resources.ResourceLocation;\nimport com.stardew.craft.StardewCraft;')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, x + xPosition, y, 286, 502 - currentDigit * 8, 5, 8, 1.0f);', 'StardewGuiUtil.drawTexture(graphics, ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/digit_" + currentDigit + ".png"), x + xPosition, y, 5, 8);')

# LevelUpMenuScreen
file = 'src/main/java/com/stardew/craft/client/gui/overnight/LevelUpMenuScreen.java'
repl(file, 'import net.neoforged.api.distmarker.OnlyIn;', 'import net.neoforged.api.distmarker.OnlyIn;\nimport net.minecraft.resources.ResourceLocation;\nimport com.stardew.craft.StardewCraft;')
repl(file, 'public class LevelUpMenuScreen extends Screen {', 'public class LevelUpMenuScreen extends Screen {\n    private static final ResourceLocation TEX_LITTLE_STAR = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/little_star.png");\n    private static final ResourceLocation TEX_BTN_OK = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/btn_ok.png");')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, (int)star.x, (int)star.y, 364, 79, 5, 5, 1.0f);', 'StardewGuiUtil.drawTexture(graphics, TEX_LITTLE_STAR, (int)star.x, (int)star.y, 5, 5);')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, xPos + guiWidth / 2 - 28 - 16, yPos + 52, (leftId % 6) * 16, 624 + (leftId / 6) * 16, 16, 16, 2.0f);', 'StardewGuiUtil.drawScaledTexture(graphics, ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/profession_" + leftId + ".png"), xPos + guiWidth / 2 - 28 - 16, yPos + 52, 16, 16, 2.0f);')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, xPos + guiWidth - 28 - 16, yPos + 52, (rightId % 6) * 16, 624 + (rightId / 6) * 16, 16, 16, 2.0f);', 'StardewGuiUtil.drawScaledTexture(graphics, ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/profession_" + rightId + ".png"), xPos + guiWidth - 28 - 16, yPos + 52, 16, 16, 2.0f);')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, okX, okY, 128, 256, 64, 64, 0.25f);', 'StardewGuiUtil.drawTexture(graphics, TEX_BTN_OK, okX, okY, 64, 64, 0.25f);')

# ShippingMenuScreen
file = 'src/main/java/com/stardew/craft/client/gui/overnight/ShippingMenuScreen.java'
repl(file, 'import net.neoforged.api.distmarker.OnlyIn;', 'import net.neoforged.api.distmarker.OnlyIn;\nimport net.minecraft.resources.ResourceLocation;\nimport com.stardew.craft.StardewCraft;')
repl(file, 'public class ShippingMenuScreen extends Screen {', 'public class ShippingMenuScreen extends Screen {\n    private static final ResourceLocation TEX_BG_STARS = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/bg_stars.png");\n    private static final ResourceLocation TEX_BG_HILLS = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/bg_hills.png");\n    private static final ResourceLocation TEX_BTN_OK = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/btn_ok.png");\n    private static final ResourceLocation TEX_BTN_PLUS = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/btn_plus.png");\n    private static final ResourceLocation TEX_BTN_PLUS_HOVER = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/btn_plus_hover.png");\n    private static final ResourceLocation TEX_BTN_BACK = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/btn_back.png");\n    private static final ResourceLocation TEX_BTN_FORWARD = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/btn_forward.png");\n    private static final ResourceLocation TEX_DIAL_DOTS = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/dial_dots.png");\n    private static final ResourceLocation TEX_COIN = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/coin.png");\n')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, l, 0, 0, 1453, Math.min(639, w - l), 195, 1.0f, alpha); // The stars/sky top', 'StardewGuiUtil.drawTexture(graphics, TEX_BG_STARS, l, 0, Math.min(639, w - l), 195, alpha);')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, l, h - 48, 0, 737, 639, 48, 1.0f, alpha * 0.65f); // Distant hills', 'StardewGuiUtil.drawTexture(graphics, TEX_BG_HILLS, l, h - 48, 639, 48, alpha * 0.65f);')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, l, h - 32, 0, 737, 639, 32, 1.0f, alpha); // Closer hills', 'StardewGuiUtil.drawTextureRegion(graphics, TEX_BG_HILLS, l, h - 32, 0, 0, 639, 32, 639, 48, alpha);')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, plusButtonX, plusButtonY, hovering ? 403 : 392, hovering ? 377 : 361, 11, 11, 1.0f);', 'StardewGuiUtil.drawTexture(graphics, hovering ? TEX_BTN_PLUS_HOVER : TEX_BTN_PLUS, plusButtonX, plusButtonY, 11, 11);')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, boxX + 6 - 12, boxY + 5, 137, 277 + i * 9, 9, 9, 1.0f);', 'StardewGuiUtil.drawTexture(graphics, ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/overnight/icon_" + getCategoryKey(i) + ".png"), boxX + 6 - 12, boxY + 5, 9, 9);')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, dotsX + m * 6, startY + 3, 293, 360, 6, 8, 1.0f); // dots', 'StardewGuiUtil.drawTexture(graphics, TEX_DIAL_DOTS, dotsX + m * 6, startY + 3, 6, 8);')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, coinX, coinY, 280, 411, 5, 5, 1.0f);', 'StardewGuiUtil.drawTexture(graphics, TEX_COIN, coinX, coinY, 5, 5);')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, okX, okY, 128, 256, 64, 64, 0.25f);', 'StardewGuiUtil.drawTexture(graphics, TEX_BTN_OK, okX, okY, 64, 64, 0.25f);')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, backX, backY, 352, 495, 12, 11, 1.0f);', 'StardewGuiUtil.drawTexture(graphics, TEX_BTN_BACK, backX, backY, 12, 11);')
repl(file, 'StardewGuiUtil.drawFromCursors(graphics, fwX, fwY, 365, 495, 12, 11, 1.0f);', 'StardewGuiUtil.drawTexture(graphics, TEX_BTN_FORWARD, fwX, fwY, 12, 11);')

repl(file, 'private Component getCategoryName(int id) {', 'private String getCategoryKey(int id) {\n        return switch (id) {\n            case 0 -> "farming";\n            case 1 -> "foraging";\n            case 2 -> "fishing";\n            case 3 -> "mining";\n            case 4 -> "other";\n            default -> "other";\n        };\n    }\n\n    private Component getCategoryName(int id) {')

print("Done")
