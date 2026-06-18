import json
import os
import re

def to_snake_case(name):
    # Basic snake_case conversion: lowercase and replace spaces/hyphens with underscores
    # Remove special characters that usually don't appear in filenames
    s = name.lower()
    s = re.sub(r'[^a-z0-9\s_]', '', s)
    return s.replace(' ', '_')

def load_json(path):
    with open(path, 'r', encoding='utf-8') as f:
        # Handle possible UTF-8 BOM
        content = f.read()
        if content.startswith('\ufeff'):
            content = content[1:]
        return json.loads(content)

def main():
    objects_path = "源文件/Content/Data/Objects.json"
    weapons_path = "源文件/Content/Data/Weapons.json"
    shops_path = "源文件/Content/Data/Shops.json"
    models_dir = "src/main/resources/assets/stardewcraft/models/item"

    objects = load_json(objects_path)
    weapons = load_json(weapons_path)
    shops = load_json(shops_path)
    
    # Models set
    try:
        model_files = {f for f in os.listdir(models_dir) if f.endswith('.json')}
    except FileNotFoundError:
        model_files = set()

    # Shop entries
    for shop_id, shop_data in shops.items():
        if not (shop_id.startswith("DesertFestival_") and shop_id != "DesertFestival_EggShop"):
            continue
        
        print(f"\nNPC Shop: {shop_id}")
        items = shop_data.get('Items', [])
        
        deliverable_items = []
        weapons_to_add = 0
        skipped_count = 0
        
        for item in items:
            item_id_str = item.get('ItemId', '')
            # Match (Type)ID
            match = re.match(r'\((O|W|H|F|BC|TR)\)(.*)', item_id_str)
            if not match:
                # Sometimes it's just ID, handle as (O) if not qualified? 
                # But typically 1.6 uses (O) etc.
                skipped_count += 1
                continue
            
            item_type, item_id = match.groups()
            
            if item_type in ('H', 'F', 'BC', 'TR'):
                skipped_count += 1
                continue
            
            if item_type == 'O':
                obj_data = objects.get(item_id)
                if not obj_data:
                    skipped_count += 1
                    continue
                name = obj_data.get('Name', 'Unknown')
                snake_name = to_snake_case(name)
                model_name = f"{snake_name}.json"
                
                if model_name in model_files:
                    deliverable_items.append(f"[O] {name} (stardewcraft:{snake_name})")
                else:
                    skipped_count += 1
            
            elif item_type == 'W':
                wpn_data = weapons.get(item_id)
                if not wpn_data:
                    skipped_count += 1
                    continue
                name = wpn_data.get('Name', 'Unknown')
                snake_name = to_snake_case(name)
                model_name = f"{snake_name}.json"
                model_exists = model_name in model_files
                
                # Fetch fields
                w_type = wpn_data.get('Type', '?')
                min_dmg = wpn_data.get('MinDamage', '?')
                max_dmg = wpn_data.get('MaxDamage', '?')
                speed = wpn_data.get('Speed', '?')
                defense = wpn_data.get('Defense', '?')
                knockback = wpn_data.get('Knockback', '?')
                precision = wpn_data.get('Precision', '?')
                crit_chance = wpn_data.get('CriticalChance', '?')
                
                status = "EXIST" if model_exists else "MISSING MODEL"
                info = f"[W] {name} ({status}) | Type: {w_type}, Dmg: {min_dmg}-{max_dmg}, Spd: {speed}, Def: {defense}, KB: {knockback}, Prec: {precision}, Crit: {crit_chance}"
                deliverable_items.append(info)
                
                if not model_exists:
                    weapons_to_add += 1

        for line in deliverable_items:
            print(f"  {line}")
        
        print(f"  Summary: {len(deliverable_items)} items listed, {weapons_to_add} weapons need models, {skipped_count} skipped.")

if __name__ == "__main__":
    main()
