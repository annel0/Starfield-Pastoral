import json, os

tmp = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "tmp_models")

for f in sorted(os.listdir(tmp)):
    if not f.endswith('.json'):
        continue
    path = os.path.join(tmp, f)
    with open(path) as fh:
        d = json.load(fh)
    
    if 'geo.json' in f or 'animation' in f:
        print(f"=== {f} ===")
        if 'minecraft:geometry' in d:
            for geo in d['minecraft:geometry']:
                desc = geo.get('description', {})
                print(f"  geo id: {desc.get('identifier', 'N/A')}")
                print(f"  tex size: {desc.get('texture_width','?')}x{desc.get('texture_height','?')}")
                bones = geo.get('bones', [])
                print(f"  bones: {len(bones)}")
        if 'animations' in d:
            for k in d['animations']:
                print(f"  anim: {k}")
        continue
    
    texs = d.get('textures', {})
    elems = d.get('elements', [])
    ts = d.get('texture_size', 'default')
    if elems:
        ys = [e['from'][1] for e in elems] + [e['to'][1] for e in elems]
        xs = [e['from'][0] for e in elems] + [e['to'][0] for e in elems]
        zs = [e['from'][2] for e in elems] + [e['to'][2] for e in elems]
        print(f"=== {f} === texsize={ts} elems={len(elems)}")
        print(f"  tex_vals: {list(texs.values())}")
        print(f"  X:[{min(xs):.1f},{max(xs):.1f}] Y:[{min(ys):.1f},{max(ys):.1f}] Z:[{min(zs):.1f},{max(zs):.1f}]")
    
    if '_display' in f:
        print(f"  display_keys: {list(d.get('display',{}).keys())}")
        print(f"  top_keys: {list(d.keys())}")
