import json
import re
import os

def get_width(text):
    if not text: return 0
    width = 0
    for char in text:
        if ord(char) > 127:
            width += 9
        elif char.isalnum():
            width += 6
        elif char == ' ':
            width += 4
        else:
            width += 4
    return width

def wrap_text_zh(text, max_width):
    if not text: return 0
    lines = 0
    current_line_width = 0
    for char in text:
        char_width = get_width(char)
        if current_line_width + char_width > max_width:
            lines += 1
            current_line_width = char_width
        else:
            current_line_width += char_width
    if current_line_width > 0:
        lines += 1
    return lines

def wrap_text_en(text, max_width):
    if not text: return 0
    lines = 0
    current_line_width = 0
    words = text.split(' ')
    for word in words:
        word_width = get_width(word) + (4 if word != words[-1] else 0)
        if current_line_width + word_width > max_width:
            lines += 1
            current_line_width = word_width
        else:
            current_line_width += word_width
    if current_line_width > 0:
        lines += 1
    return lines

def analyze():
    with open('src/main/java/com/stardew/craft/book/BookDefinition.java', 'r') as f:
        content = f.read()
    
    # Correct extraction using regex groups for the registry name (3rd quoted string usually)
    # e.g., skill("SkillBook_0", "skill_book_0", ...
    pattern = r'(?:skill|purple|powerNoRepeat|powerAll|powerSkill|queen)\("[^"]+",\s*"([^"]+)"'
    registry_names = re.findall(pattern, content)

    with open('src/main/resources/assets/stardewcraft/lang/zh_cn.json', 'r') as f:
        zh = json.load(f)
    with open('src/main/resources/assets/stardewcraft/lang/en_us.json', 'r') as f:
        en = json.load(f)

    max_desc_lines_zh = 0
    max_desc_lines_en = 0
    max_effect_width_zh = 0
    max_effect_width_en = 0
    overflow_risks = []

    print(f"{'Registry Name':<25} | ZH L | EN L | Eff W ZH | Eff W EN")
    print("-" * 75)

    for name in registry_names:
        desc_key = f"item.stardewcraft.{name}.desc"
        effect_key = f"item.stardewcraft.{name}.effect"
        
        desc_zh = zh.get(desc_key, "")
        desc_en = en.get(desc_key, "")
        
        lines_zh = wrap_text_zh(desc_zh, 165)
        lines_en = wrap_text_en(desc_en, 165)
        
        effect_zh = zh.get(effect_key, "")
        effect_en = en.get(effect_key, "")
        
        # Searching for alternative effect patterns if missing
        if not effect_zh:
            alt_patterns = [f"item.stardewcraft.{name}.effect_1", f"item.stardewcraft.{name}.power"]
            for ap in alt_patterns:
                if ap in zh:
                    effect_zh = zh[ap]
                    break
        if not effect_en:
            alt_patterns = [f"item.stardewcraft.{name}.effect_1", f"item.stardewcraft.{name}.power"]
            for ap in alt_patterns:
                if ap in en:
                    effect_en = en[ap]
                    break

        width_zh = get_width(effect_zh)
        width_en = get_width(effect_en)

        print(f"{name:<25} | {lines_zh:<4} | {lines_en:<4} | {width_zh:<8} | {width_en}")

        max_desc_lines_zh = max(max_desc_lines_zh, lines_zh)
        max_desc_lines_en = max(max_desc_lines_en, lines_en)
        max_effect_width_zh = max(max_effect_width_zh, width_zh)
        max_effect_width_en = max(max_effect_width_en, width_en)

        if width_zh > 150:
            overflow_risks.append(f"{name} (ZH)")
        if width_en > 150:
            overflow_risks.append(f"{name} (EN)")

    print("\nSummary:")
    print(f"Max Description Lines (ZH): {max_desc_lines_zh}")
    print(f"Max Description Lines (EN): {max_desc_lines_en}")
    print(f"Max Effect Width (ZH): {max_effect_width_zh}")
    print(f"Max Effect Width (EN): {max_effect_width_en}")
    print(f"Overflow Risks (>150px): {', '.join(overflow_risks) if overflow_risks else 'None'}")

    # Check StardewCraft.java
    with open('src/main/java/com/stardew/craft/StardewCraft.java', 'r') as f:
        sc_content = f.read()
    
    has_loop = "ModItems.BOOKS.values()" in sc_content
    print(f"\nModItems.BOOKS.values() in StardewCraft.java: {has_loop}")

analyze()
