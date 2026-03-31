import json, shutil, os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TMP = os.path.join(ROOT, 'tmp_models')
RES = os.path.join(ROOT, 'src/main/resources/assets/stardewcraft')

def ensure(path):
    os.makedirs(os.path.dirname(path), exist_ok=True)

def write_json(path, data):
    ensure(path)
    with open(path, 'w') as f:
        json.dump(data, f, indent='\t')
    print(f'  wrote {os.path.relpath(path, ROOT)}')

def copy_tex(src_name, dst_path):
    ensure(dst_path)
    shutil.copy2(os.path.join(TMP, src_name), dst_path)
    print(f'  copied {src_name} -> {os.path.relpath(dst_path, ROOT)}')

def fix_textures(model_data, tex_map):
    d = json.loads(json.dumps(model_data))
    new_tex = {}
    for k, v in d.get('textures', {}).items():
        new_tex[k] = tex_map.get(v, v)
    d['textures'] = new_tex
    return d

def blockstate_static(mp):
    ext = 'stardewcraft:block/decor/extensions/furniture_extension_empty'
    return {'variants': {
        'part=main,facing=north': {'model': mp},
        'part=main,facing=east': {'model': mp, 'y': 90},
        'part=main,facing=south': {'model': mp, 'y': 180},
        'part=main,facing=west': {'model': mp, 'y': 270},
        'part=extension,facing=north': {'model': ext},
        'part=extension,facing=east': {'model': ext},
        'part=extension,facing=south': {'model': ext},
        'part=extension,facing=west': {'model': ext}
    }}

def item_model(parent, particle):
    return {'parent': parent, 'textures': {'particle': particle}}

items = [
    ('drum_set', '1', {'0': 'REPLACE', '1': 'REPLACE', 'particle': 'REPLACE'}),
    ('wine_cabinet_1', '2_1', {'2': 'REPLACE', 'particle': 'REPLACE'}),
    ('wine_cabinet_2', '2_2', {'2': 'REPLACE', 'particle': 'REPLACE'}),
    ('wine_cabinet_3', '2_3', {'2': 'REPLACE', 'particle': 'REPLACE'}),
]

for name, src, tex_keys in items:
    print(f'=== {name} ===')
    tp = f'stardewcraft:block/decor/common/{name}'
    
    with open(os.path.join(TMP, f'{src}.json')) as f:
        m = json.load(f)
    
    # Build tex_map: map original texture values to new path
    orig_textures = m.get('textures', {})
    tex_map = {}
    for k, v in orig_textures.items():
        tex_map[v] = tp
    
    m = fix_textures(m, tex_map)
    
    model_path = os.path.join(RES, 'models', 'decor', 'common', f'{name}.json')
    write_json(model_path, m)
    
    tex_path = os.path.join(RES, 'textures', 'block', 'decor', 'common', f'{name}.png')
    copy_tex(f'{src}.png', tex_path)
    
    bs_path = os.path.join(RES, 'blockstates', f'{name}.json')
    write_json(bs_path, blockstate_static(f'stardewcraft:decor/common/{name}'))
    
    im_path = os.path.join(RES, 'models', 'item', f'{name}.json')
    write_json(im_path, item_model(f'stardewcraft:decor/common/{name}', tp))

print('\nDone!')
