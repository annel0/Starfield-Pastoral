#!/usr/bin/env python3
"""Convert totem pole models from tmp_models/ and create all resource files."""
import json, shutil, os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

mapping = {
    "3_1": ("totem_pole_farm", "1"),
    "3_2": ("totem_pole_mountain", "2"),
    "3_3": ("totem_pole_beach", "1"),
}

models_dir = os.path.join(ROOT, "src/main/resources/assets/stardewcraft/models/block/utility")
textures_dir = os.path.join(ROOT, "src/main/resources/assets/stardewcraft/textures/block/utility")
bs_dir = os.path.join(ROOT, "src/main/resources/assets/stardewcraft/blockstates")
os.makedirs(models_dir, exist_ok=True)
os.makedirs(textures_dir, exist_ok=True)
os.makedirs(bs_dir, exist_ok=True)

for prefix, (name, tex_key) in mapping.items():
    src = os.path.join(ROOT, f"tmp_models/{prefix}.json")
    with open(src) as f:
        model = json.load(f)

    # activated model (default)
    activated = json.loads(json.dumps(model))
    tex_dict = {tex_key: f"stardewcraft:block/utility/{name}_activated"}
    if prefix == "3_2":
        tex_dict["particle"] = f"stardewcraft:block/utility/{name}_activated"
    activated["textures"] = tex_dict
    with open(os.path.join(models_dir, f"{name}.json"), "w") as f:
        json.dump(activated, f, indent=2)

    # deactivated model
    deactivated = json.loads(json.dumps(model))
    tex_dict2 = {tex_key: f"stardewcraft:block/utility/{name}_deactivated"}
    if prefix == "3_2":
        tex_dict2["particle"] = f"stardewcraft:block/utility/{name}_deactivated"
    deactivated["textures"] = tex_dict2
    with open(os.path.join(models_dir, f"{name}_deactivated.json"), "w") as f:
        json.dump(deactivated, f, indent=2)

    # copy textures
    for state in ["activated", "deactivated"]:
        src_tex = os.path.join(ROOT, f"tmp_models/{prefix}_{state}.png")
        dst_tex = os.path.join(textures_dir, f"{name}_{state}.png")
        shutil.copy(src_tex, dst_tex)

    # blockstate JSON
    variants = {}
    for activated_val in ["true", "false"]:
        model_path = f"stardewcraft:block/utility/{name}" if activated_val == "true" else f"stardewcraft:block/utility/{name}_deactivated"
        for facing, y_rot in [("north", None), ("east", 90), ("south", 180), ("west", 270)]:
            key = f"activated={activated_val},facing={facing},part=main"
            entry = {"model": model_path}
            if y_rot is not None:
                entry["y"] = y_rot
            variants[key] = entry

            # extension variants point to empty model
            ext_key = f"activated={activated_val},facing={facing},part=extension"
            variants[ext_key] = {"model": "stardewcraft:block/utility/totem_pole_extension_empty"}

    blockstate = {"variants": variants}
    with open(os.path.join(bs_dir, f"{name}.json"), "w") as f:
        json.dump(blockstate, f, indent=2)

    print(f"OK: {name}")

# Extension empty model
ext = {"textures": {}, "elements": []}
with open(os.path.join(models_dir, "totem_pole_extension_empty.json"), "w") as f:
    json.dump(ext, f, indent=2)
print("OK: extension_empty model")

# Item models (simple block parent)
items_dir = os.path.join(ROOT, "src/main/resources/assets/stardewcraft/models/item")
os.makedirs(items_dir, exist_ok=True)
for _, (name, _) in mapping.items():
    item_model = {"parent": f"stardewcraft:block/utility/{name}"}
    with open(os.path.join(items_dir, f"{name}.json"), "w") as f:
        json.dump(item_model, f, indent=2)
    print(f"OK: item model {name}")

# Warp totem + rain totem item models (simple generated)
for totem_name in ["warp_totem_farm", "warp_totem_mountain", "warp_totem_beach", "rain_totem"]:
    item_model = {
        "parent": "minecraft:item/generated",
        "textures": {"layer0": f"stardewcraft:item/{totem_name}"}
    }
    with open(os.path.join(items_dir, f"{totem_name}.json"), "w") as f:
        json.dump(item_model, f, indent=2)
    print(f"OK: item model {totem_name}")

print("\nAll totem resources created successfully!")
