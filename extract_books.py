import json
import re

def snake_case(s):
    # Remove Book_ or SkillBook prefix and Name/Description suffix
    s = re.sub(r'^(Book_|SkillBook)', '', s)
    s = re.sub(r'_(Name|Description)$', '', s)
    # Convert to snake_case
    return re.sub(r'(?<!^)(?=[A-Z])', '_', s).lower()

def main():
    try:
        with open('源文件/Content/Strings/Objects.json', 'r', encoding='utf-8') as f:
            en_data = json.load(f)
        with open('源文件/Content/Strings/Objects.zh-CN.json', 'r', encoding='utf-8') as f:
            zh_data = json.load(f)
    except Exception as e:
        print(f"Error loading files: {e}")
        return

    books = {}
    # Combine Book_ and SkillBook prefixes
    patterns = [r'^Book_.*_(Name|Description)$', r'^SkillBook.*_(Name|Description)$']
    
    for key in en_data:
        if any(re.match(p, key) for p in patterns):
            base_key = key.rsplit('_', 1)[0]
            if base_key not in books:
                books[base_key] = {}
            
            snake = snake_case(base_key)
            suffix = 'desc' if key.endswith('_Description') else 'name'
            
            # Special case for SkillBook which is SkillBook0, SkillBook1 etc.
            if key.startswith('SkillBook'):
                snake = f"skill_{snake}"

            final_key = f"item.stardewcraft.{snake}"
            if suffix == 'desc':
                final_key += ".desc"
            
            books[base_key][suffix] = {
                'key': final_key,
                'en': en_data.get(key, '').strip(),
                'zh': zh_data.get(key, '').strip()
            }

    # Sorting to ensure consistent order
    sorted_keys = sorted(books.keys())
    
    output = []
    for bk in sorted_keys:
        if 'name' in books[bk]:
            output.append({books[bk]['name']['key']: books[bk]['name']['zh']})
        if 'desc' in books[bk]:
            output.append({books[bk]['desc']['key']: books[bk]['desc']['zh']})

    print(json.dumps(output, ensure_ascii=False, indent=2))

if __name__ == "__main__":
    main()
