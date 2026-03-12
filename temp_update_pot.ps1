
$content = Get-Content src/main/java/com/stardew/craft/client/gui/CookingPotScreen.java -Raw

# Move click logic BEFORE super.mouseClicked
$oldMouse = @"
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        int x = this.leftPos;
        int y = this.topPos;

        if (button == 0 && mouseX >= x + 280 && mouseX <= x + 360 && mouseY >= y + 0 && mouseY <= y + 20) {
            if (hasShiftDown()) recipePage = Math.max(0, recipePage - 1);
            else recipePage = Math.min((availableRecipes.size() - 1) / 45, recipePage + 1);
            return true;
        }

        int gridX = x + 156;
        int gridY = y + 24;
        int startIndex = recipePage * 45;
        
        for (int i = 0; i < 45; i++) {
            int cx = gridX + (i % 9) * 22;
            int cy = gridY + (i / 9) * 22;
            int dataIndex = startIndex + i;
            
            if (dataIndex >= availableRecipes.size()) break;

            if (mouseX >= cx - 2 && mouseX <= cx + 20 && mouseY >= cy - 2 && mouseY <= cy + 20) {
                int craftCount = button == 1 ? 5 : 1;
                if (hasShiftDown()) craftCount = -1;
                
                String itemPath = recipeIds.get(dataIndex).getPath();
                PacketDistributor.sendToServer(new CookingPotCookSubmitPayload(itemPath, craftCount));
                return true;
            }
        }

        return false;
    }
"@

$newMouse = @"
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = this.leftPos;
        int y = this.topPos;

        if (button == 0 && mouseX >= x + 280 && mouseX <= x + 360 && mouseY >= y + 0 && mouseY <= y + 20) {
            if (hasShiftDown()) recipePage = Math.max(0, recipePage - 1);
            else recipePage = Math.min((availableRecipes.size() - 1) / 45, recipePage + 1);
            return true;
        }

        int gridX = x + 156;
        int gridY = y + 24;
        int startIndex = recipePage * 45;
        
        for (int i = 0; i < 45; i++) {
            int cx = gridX + (i % 9) * 22;
            int cy = gridY + (i / 9) * 22;
            int dataIndex = startIndex + i;
            
            if (dataIndex >= availableRecipes.size()) break;

            if (mouseX >= cx - 2 && mouseX <= cx + 20 && mouseY >= cy - 2 && mouseY <= cy + 20) {
                int craftCount = button == 1 ? 5 : 1;
                if (hasShiftDown()) craftCount = -1;
                
                String itemPath = recipeIds.get(dataIndex).getPath();
                PacketDistributor.sendToServer(new CookingPotCookSubmitPayload(itemPath, craftCount));
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
"@

$content = $content.Replace($oldMouse, $newMouse)
Set-Content -Path src/main/java/com/stardew/craft/client/gui/CookingPotScreen.java -Value $content


