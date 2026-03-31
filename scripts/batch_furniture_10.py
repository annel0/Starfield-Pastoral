#!/usr/bin/env python3
"""Batch create resource files for 10 new furniture items."""
import json, shutil, os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TMP = os.path.join(ROOT, "tmp_models")
RES = os.path.join(ROOT, "src/main/resources/assets/stardewcraft")

def model_dir(sub): return os.path.join(RES, "models", sub)
def tex_dir(sub): return os.path.join(RES, "textures", sub)
def bs_dir(): return os.path.join(RES, "blockstates")
def geo_dir(): return os.path.join(RES, "geo/block/decor")
def anim_dir(): return os.path.join(RES, "animations/block/decor")

def ensure(path):
    os.makedirs(os.path.dirname(path), exist_ok=True)

def write_json(path, data):
    ensure(path)
    with open(path, 'w') as f:
        json.dump(data, f, indent='\t')
    print(f"  wrote {os.path.relpath(path, ROOT)}")

def copy_tex(src_name, dst_path):
    ensure(dst_path)
    shutil.copy2(os.path.join(TMP, src_name), dst_path)
    print(f"  copied {src_name} -> {os.path.relpath(dst_path, ROOT)}")

def fix_textures(model_data, tex_map):
    """Replace texture references in model JSON."""
    d = json.loads(json.dumps(model_data))
    new_tex = {}
    for k, v in d.get("textures", {}).items():
        if v in tex_map:
            new_tex[k] = tex_map[v]
        else:
            new_tex[k] = v
    d["textures"] = new_tex
    return d

def blockstate_static(model_path):
    """Standard MapDecorStaticBlock blockstate with part+facing."""
    return {
        "variants": {
            "part=main,facing=north": {"model": model_path},
            "part=main,facing=east": {"model": model_path, "y": 90},
            "part=main,facing=south": {"model": model_path, "y": 180},
            "part=main,facing=west": {"model": model_path, "y": 270},
            "part=extension,facing=north": {"model": "stardewcraft:block/decor/extensions/furniture_extension_empty"},
            "part=extension,facing=east": {"model": "stardewcraft:block/decor/extensions/furniture_extension_empty"},
            "part=extension,facing=south": {"model": "stardewcraft:block/decor/extensions/furniture_extension_empty"},
            "part=extension,facing=west": {"model": "stardewcraft:block/decor/extensions/furniture_extension_empty"}
        }
    }

def blockstate_wall(model_path):
    """Wall-thin block blockstate (facing only, no part)."""
    return {
        "variants": {
            "facing=north": {"model": model_path},
            "facing=east": {"model": model_path, "y": 90},
            "facing=south": {"model": model_path, "y": 180},
            "facing=west": {"model": model_path, "y": 270}
        }
    }

def item_model(parent_model, particle_tex):
    return {
        "parent": parent_model,
        "textures": {"particle": particle_tex}
    }

# ──────────────────────────────────────────
# 1. Joja Vending Machine
# ──────────────────────────────────────────
print("=== 1. Joja Vending Machine ===")
name = "joja_vending_machine"
tex_path = f"stardewcraft:block/decor/common/{name}"
with open(os.path.join(TMP, "1.json")) as f:
    m = json.load(f)
m = fix_textures(m, {"1": tex_path, "particle": tex_path})
write_json(os.path.join(model_dir("decor/common"), f"{name}.json"), m)
copy_tex("1.png", os.path.join(tex_dir("block/decor/common"), f"{name}.png"))
write_json(os.path.join(bs_dir(), f"{name}.json"), blockstate_static(f"stardewcraft:decor/common/{name}"))
write_json(os.path.join(model_dir("item"), f"{name}.json"), item_model(f"stardewcraft:decor/common/{name}", tex_path))

# ──────────────────────────────────────────
# 2. White Teacup
# ──────────────────────────────────────────
print("=== 2. White Teacup ===")
name = "white_teacup"
tex_path = f"stardewcraft:block/decor/common/{name}"
with open(os.path.join(TMP, "2.json")) as f:
    m = json.load(f)
m = fix_textures(m, {"2": tex_path, "particle": tex_path})
write_json(os.path.join(model_dir("decor/common"), f"{name}.json"), m)
copy_tex("2.png", os.path.join(tex_dir("block/decor/common"), f"{name}.png"))
write_json(os.path.join(bs_dir(), f"{name}.json"), blockstate_static(f"stardewcraft:decor/common/{name}"))
write_json(os.path.join(model_dir("item"), f"{name}.json"), item_model(f"stardewcraft:decor/common/{name}", tex_path))

# ──────────────────────────────────────────
# 3. Pool Table (2 textures)
# ──────────────────────────────────────────
print("=== 3. Pool Table ===")
name = "pool_table"
tex1 = f"stardewcraft:block/decor/common/{name}_1"
tex2 = f"stardewcraft:block/decor/common/{name}_2"
with open(os.path.join(TMP, "3.json")) as f:
    m = json.load(f)
m = fix_textures(m, {"3_1": tex1, "3_2": tex2, "particle": tex1})
write_json(os.path.join(model_dir("decor/common"), f"{name}.json"), m)
copy_tex("3_1.png", os.path.join(tex_dir("block/decor/common"), f"{name}_1.png"))
copy_tex("3_2.png", os.path.join(tex_dir("block/decor/common"), f"{name}_2.png"))
write_json(os.path.join(bs_dir(), f"{name}.json"), blockstate_static(f"stardewcraft:decor/common/{name}"))
write_json(os.path.join(model_dir("item"), f"{name}.json"), item_model(f"stardewcraft:decor/common/{name}", tex1))

# ──────────────────────────────────────────
# 4. Hospital Counter
# ──────────────────────────────────────────
print("=== 4. Hospital Counter ===")
name = "hospital_counter"
tex_path = f"stardewcraft:block/decor/common/{name}"
with open(os.path.join(TMP, "4.json")) as f:
    m = json.load(f)
m = fix_textures(m, {"4": tex_path, "particle": tex_path})
write_json(os.path.join(model_dir("decor/common"), f"{name}.json"), m)
copy_tex("4.png", os.path.join(tex_dir("block/decor/common"), f"{name}.png"))
write_json(os.path.join(bs_dir(), f"{name}.json"), blockstate_static(f"stardewcraft:decor/common/{name}"))
write_json(os.path.join(model_dir("item"), f"{name}.json"), item_model(f"stardewcraft:decor/common/{name}", tex_path))

# ──────────────────────────────────────────
# 5. Hospital Posters (5 variants, wall-mounted)
# ──────────────────────────────────────────
print("=== 5. Hospital Posters ===")
with open(os.path.join(TMP, "5_1.json")) as f:
    poster_base = json.load(f)
for i in range(1, 6):
    name = f"hospital_poster_{i}"
    tex_path = f"stardewcraft:block/decor/common/{name}"
    m = fix_textures(poster_base, {f"5_{i}": tex_path, "5_1": tex_path, "particle": tex_path})
    # Also handle case where texture key is just the number
    for k in list(m["textures"].keys()):
        if m["textures"][k] in [f"5_{i}", f"5_1"]:
            m["textures"][k] = tex_path
    write_json(os.path.join(model_dir("decor/wall_decor/common"), f"{name}.json"), m)
    copy_tex(f"5_{i}.png", os.path.join(tex_dir("block/decor/common"), f"{name}.png"))
    write_json(os.path.join(bs_dir(), f"{name}.json"), blockstate_wall(f"stardewcraft:decor/wall_decor/common/{name}"))
    write_json(os.path.join(model_dir("item"), f"{name}.json"), item_model(f"stardewcraft:decor/wall_decor/common/{name}", tex_path))

# ──────────────────────────────────────────
# 6. Electric Piano
# ──────────────────────────────────────────
print("=== 6. Electric Piano ===")
name = "electric_piano"
tex_path = f"stardewcraft:block/decor/common/{name}"
with open(os.path.join(TMP, "6.json")) as f:
    m = json.load(f)
m = fix_textures(m, {"6": tex_path, "particle": tex_path})
# Also add particle if missing
if "particle" not in m["textures"]:
    m["textures"]["particle"] = tex_path
write_json(os.path.join(model_dir("decor/common"), f"{name}.json"), m)
copy_tex("6.png", os.path.join(tex_dir("block/decor/common"), f"{name}.png"))
write_json(os.path.join(bs_dir(), f"{name}.json"), blockstate_static(f"stardewcraft:decor/common/{name}"))
write_json(os.path.join(model_dir("item"), f"{name}.json"), item_model(f"stardewcraft:decor/common/{name}", tex_path))

# ──────────────────────────────────────────
# 7. Wizard Cauldron
# ──────────────────────────────────────────
print("=== 7. Wizard Cauldron ===")
name = "wizard_cauldron"
tex_path = f"stardewcraft:block/decor/common/{name}"
with open(os.path.join(TMP, "7.json")) as f:
    m = json.load(f)
m = fix_textures(m, {"7": tex_path, "particle": tex_path})
write_json(os.path.join(model_dir("decor/common"), f"{name}.json"), m)
copy_tex("7.png", os.path.join(tex_dir("block/decor/common"), f"{name}.png"))
write_json(os.path.join(bs_dir(), f"{name}.json"), blockstate_static(f"stardewcraft:decor/common/{name}"))
write_json(os.path.join(model_dir("item"), f"{name}.json"), item_model(f"stardewcraft:decor/common/{name}", tex_path))

# ──────────────────────────────────────────
# 8. Guitar
# ──────────────────────────────────────────
print("=== 8. Guitar ===")
name = "guitar"
tex_path = f"stardewcraft:block/decor/common/{name}"
with open(os.path.join(TMP, "8.json")) as f:
    m = json.load(f)
m = fix_textures(m, {"8": tex_path, "particle": tex_path})
write_json(os.path.join(model_dir("decor/common"), f"{name}.json"), m)
copy_tex("8.png", os.path.join(tex_dir("block/decor/common"), f"{name}.png"))
write_json(os.path.join(bs_dir(), f"{name}.json"), blockstate_static(f"stardewcraft:decor/common/{name}"))
write_json(os.path.join(model_dir("item"), f"{name}.json"), item_model(f"stardewcraft:decor/common/{name}", tex_path))

# ──────────────────────────────────────────
# 9. Microwave
# ──────────────────────────────────────────
print("=== 9. Microwave ===")
name = "microwave"
tex_path = f"stardewcraft:block/decor/common/{name}"
with open(os.path.join(TMP, "9.json")) as f:
    m = json.load(f)
m = fix_textures(m, {"9": tex_path, "particle": tex_path})
write_json(os.path.join(model_dir("decor/common"), f"{name}.json"), m)
copy_tex("9.png", os.path.join(tex_dir("block/decor/common"), f"{name}.png"))
write_json(os.path.join(bs_dir(), f"{name}.json"), blockstate_static(f"stardewcraft:decor/common/{name}"))
write_json(os.path.join(model_dir("item"), f"{name}.json"), item_model(f"stardewcraft:decor/common/{name}", tex_path))

# ──────────────────────────────────────────
# 10. Grandfather Clock (GeckoLib geo model)
# ──────────────────────────────────────────
print("=== 10. Grandfather Clock (GeckoLib) ===")
name = "grandfather_clock"
# Copy geo model
copy_tex("10.geo.json", os.path.join(geo_dir(), f"{name}.geo.json"))
# Copy animation
copy_tex("10.animation.json", os.path.join(anim_dir(), f"{name}.animation.json"))
# Copy texture
copy_tex("10.png", os.path.join(tex_dir("block/deco/misc/common"), f"{name}.png"))
# Copy display model for item rendering
with open(os.path.join(TMP, "10_display.json")) as f:
    display = json.load(f)
display_tex = f"stardewcraft:block/deco/misc/common/{name}"
display = fix_textures(display, {"block/10": display_tex, "particle": display_tex})
write_json(os.path.join(model_dir("decor/common"), f"{name}_display.json"), display)
# Blockstate (uses ENTITYBLOCK_ANIMATED, but still needs a valid blockstate JSON)
write_json(os.path.join(bs_dir(), f"{name}.json"), blockstate_static(f"stardewcraft:decor/common/{name}_display"))
# Item model
write_json(os.path.join(model_dir("item"), f"{name}.json"), item_model(f"stardewcraft:decor/common/{name}_display", display_tex))

print("\n=== Done! All resource files created. ===")
