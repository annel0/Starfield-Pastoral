import io
import json

def process_file(path):
    with io.open(path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    keys_to_delete = [k for k in data.keys() if 'decor_anchor' in k]
    for k in keys_to_delete:
        del data[k]
    with io.open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

process_file('src/main/resources/assets/stardewcraft/lang/en_us.json')
process_file('src/main/resources/assets/stardewcraft/lang/zh_cn.json')
