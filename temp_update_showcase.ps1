
$content = Get-Content src/main/java/com/stardew/craft/client/gui/CookingPotScreen.java -Raw

$startIndex = $content.IndexOf("private void drawElegantShowcase(")
$endIndex = $content.IndexOf("private ResourceLocation getStardewBuffIcon(")
if ($endIndex -eq -1) { $endIndex = $content.IndexOf("private int countMatching(") }

$oldMethod = $content.Substring($startIndex, $endIndex - $startIndex)

$newMethod = @"
    private void drawElegantShowcase(GuiGraphics g, int px, int py, int focusIdx, float partialTick) {
        int width = 130;
        ItemStack selStack = availableRecipes.get(focusIdx);
        ResourceLocation recipeId = recipeIds.get(focusIdx);

        int alpha = (int)(showcaseAlpha * 255);
        alpha = Mth.clamp(alpha, 0, 255);
        int alphaMask = alpha << 24;

        // 1. 标题置顶
        Component title = selStack.getHoverName();
        String printTitle = title.getString();
        if (this.font.width(printTitle) > width - 4) {
            printTitle = this.font.plainSubstrByWidth(printTitle, width - 12) + "...";
        }
        int tx = px + (width - this.font.width(printTitle)) / 2;
        g.drawString(this.font, printTitle, tx, py, alphaMask | 0xFFE0C66D, true);

        // 2. 描述自动换行居中融合
        Component desc = Component.translatable(selStack.getItem().getDescriptionId() + ".desc");
        List<FormattedCharSequence> descLines = this.font.split(desc, width - 6);
        int descY = py + 12;
        for (int i = 0; i < Math.min(2, descLines.size()); i++) {
            int dx = px + (width - this.font.width(descLines.get(i))) / 2;
            g.drawString(this.font, descLines.get(i), dx, descY, alphaMask | 0xFFAAAAAA, true);
            descY += this.font.lineHeight;
        }

        // 3. 超大浮动图标 (位置动态调整)
        float floatY = (float)Math.sin((System.currentTimeMillis() % 6000) / 6000.0f * Math.PI * 2) * 5.0f;
        g.pose().pushPose();
        g.pose().translate(px + 65, py + 56 + floatY, 150); 
        g.pose().scale(3.5f, 3.5f, 1.0f);
        g.renderItem(selStack, -8, -8);
        g.pose().popPose();

        // 4. 生命/能量 & 药水Buff 联排展示
        Item item = selStack.getItem();
        int statY = py + 100;
        if (item instanceof CookingDishItem dish) {
            int e = dish.getEnergy(selStack);
            int h = dish.getHealth(selStack);
            List<CookingDishItem.DishBuff> buffs = dish.getBuffs();
            
            int eWidth = e > 0 ? 10 + this.font.width(String.valueOf(e)) + 6 : 0;
            int hWidth = h > 0 ? 10 + this.font.width(String.valueOf(h)) + 6 : 0;
            int buffsWidth = 0;
            for (int i = 0; i < Math.min(3, buffs.size()); i++) {
                buffsWidth += 12; 
            }
            if (buffsWidth > 0 && (e > 0 || h > 0)) buffsWidth += 4;
            
            int statWidth = eWidth + hWidth + buffsWidth;
            int curX = px + (width - statWidth) / 2;
            
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, showcaseAlpha); 
            
            if (e > 0) {
                g.blit(ENERGY_ICON, curX, statY - 1, 0, 0, 10, 10, 16, 16);
                g.drawString(this.font, String.valueOf(e), curX + 12, statY, alphaMask | 0xFFFFFF, true);
                curX += eWidth;
            }
            if (h > 0) {
                g.blit(HEALTH_ICON, curX, statY - 1, 0, 0, 10, 10, 16, 16);
                g.drawString(this.font, String.valueOf(h), curX + 12, statY, alphaMask | 0xFFFFFF, true);
                curX += hWidth;
            }
            
            if (buffsWidth > 0 && (e > 0 || h > 0)) {
                g.fill(curX - 2, statY, curX - 1, statY + 8, alphaMask | 0x40FFFFFF);
                curX += 4;
            }
            
            for (int i = 0; i < Math.min(3, buffs.size()); i++) {
                CookingDishItem.DishBuff buff = buffs.get(i);
                ResourceLocation buffIcon = getStardewBuffIcon(buff.type());
                g.blit(buffIcon, curX, statY - 1, 0, 0, 10, 10, 16, 16);
                // Draw amount on top/right of buff if needed, but usually icon is enough
                curX += 12;
            }
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        // 5. 简约食材比例
        List<VanillaCookingRecipeData.IngredientRequirement> reqs = VanillaCookingRecipeData.getRequirements(recipeId.getPath());
        if (!reqs.isEmpty()) {
            int startY = py + 116;
            int exactCount = Math.min(4, reqs.size());
            int reqSpacing = 28; 
            int totalReqWidth = (exactCount - 1) * reqSpacing + 16;
            int startX = px + (width - totalReqWidth) / 2;

            for (int i = 0; i < exactCount; i++) {
                VanillaCookingRecipeData.IngredientRequirement req = reqs.get(i);
                ItemStack icon = resolveRequirementIcon(req.token());
                int has = countMatching(req.token());
                int need = req.count();
                boolean enough = has >= need;
                
                int rx = startX + i * reqSpacing;
                
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, showcaseAlpha);
                g.pose().pushPose();
                g.pose().translate(0, 0, 50);
                g.renderItem(icon, rx, startY);
                g.pose().popPose();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                
                String ratio = has + "/" + need;
                int ratioColor = enough ? (alphaMask | 0xEEFFEE) : (alphaMask | 0xFF8888);
                float rScale = 0.8f;
                g.pose().pushPose();
                g.pose().translate(rx + 8 - (this.font.width(ratio)*rScale)/2, startY + 16, 200);
                g.pose().scale(rScale, rScale, 1.0f);
                g.drawString(this.font, ratio, 0, 0, ratioColor, true);
                g.pose().popPose();
            }
        }
    }

"@

$content = $content.Replace($oldMethod, $newMethod)
Set-Content -Path src/main/java/com/stardew/craft/client/gui/CookingPotScreen.java -Value $content


