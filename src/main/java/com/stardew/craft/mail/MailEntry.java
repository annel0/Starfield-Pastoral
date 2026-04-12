package com.stardew.craft.mail;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 数据驱动的邮件定义，对齐 SDV Data/mail 格式。
 * <p>
 * JSON 结构示例:
 * <pre>
 * {
 *   "id": "spring_2_1",
 *   "text": "亲爱的@：...[#]来自皮埃尔",
 *   "background": 0,
 *   "textColor": null,
 *   "attachedItems": [{"id": "stardewcraft:parsnip_seeds", "count": 15}],
 *   "money": 0,
 *   "learnedRecipe": null,
 *   "recipeIsCooking": false,
 *   "questId": null,
 *   "specialOrderId": null
 * }
 * </pre>
 */
public class MailEntry {

    private final String id;
    private final String text;
    private final int background;           // whichBG index (0-4)
    @Nullable private final String customBgTexture;  // [letterbg path index] override
    @Nullable private final String textColor;        // [textcolor xxx]
    private final List<AttachedItem> attachedItems;
    private final int money;
    @Nullable private final String learnedRecipe;
    private final boolean recipeIsCooking;
    @Nullable private final String questId;
    @Nullable private final String specialOrderId;

    public MailEntry(String id, String text, int background,
                     @Nullable String customBgTexture, @Nullable String textColor,
                     List<AttachedItem> attachedItems, int money,
                     @Nullable String learnedRecipe, boolean recipeIsCooking,
                     @Nullable String questId, @Nullable String specialOrderId) {
        this.id = id;
        this.text = text;
        this.background = background;
        this.customBgTexture = customBgTexture;
        this.textColor = textColor;
        this.attachedItems = attachedItems;
        this.money = money;
        this.learnedRecipe = learnedRecipe;
        this.recipeIsCooking = recipeIsCooking;
        this.questId = questId;
        this.specialOrderId = specialOrderId;
    }

    public String getId() { return id; }
    public String getText() { return text; }
    public int getBackground() { return background; }
    @Nullable public String getCustomBgTexture() { return customBgTexture; }
    @Nullable public String getTextColor() { return textColor; }
    public List<AttachedItem> getAttachedItems() { return attachedItems; }
    public int getMoney() { return money; }
    @Nullable public String getLearnedRecipe() { return learnedRecipe; }
    public boolean isRecipeIsCooking() { return recipeIsCooking; }
    @Nullable public String getQuestId() { return questId; }
    @Nullable public String getSpecialOrderId() { return specialOrderId; }

    /**
     * 邮件附带物品。
     */
    public record AttachedItem(String id, int count) {}
}
