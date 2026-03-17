import re
import os

def fix_money_dial():
    p = 'src/main/java/com/stardew/craft/client/gui/overnight/MoneyDial.java'
    with open(p, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Original Stardew: 
    # b.Draw(Game1.mouseCursors, position + new Vector2(xPosition, ... ), new Rectangle(286, 502 - currentDigit * 8, 5, 8), Color.Maroon, 0f, Vector2.Zero, 4f + ..., SpriteEffects.None, 1f);
    
    rep = r'''
                    if (significant) {
                        float scale = 4.0f + (moneyShineTimer / 60 == numDigits - j ? 0.3f : 0.0f);
                        StardewGuiUtil.drawFromCursors(graphics, x + xPosition, y, 286, 502 - currentDigit * 8, 5, 8, scale);
                    }
'''
    # Wait, the current MoneyDial has what?
    # Let me just rewrite MoneyDial.java mostly
    pass

fix_money_dial()
