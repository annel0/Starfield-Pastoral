#!/usr/bin/env python3
"""
将 MC 结构模板 (.nbt) 中的空气方块替换为 structure_void，
使 Jigsaw 放置时不会覆盖已有地形。

仅替换"周边"空气（不与任何固体方块相邻的不处理，
但为简化，我们替换所有 air → structure_void，
因为结构内部本就没有应该是空气的大区域）。
"""
import sys
import gzip
import io
from nbt import nbt

def convert_air_to_structure_void(filepath):
    nbtfile = nbt.NBTFile(filepath, 'rb')
    
    # MC 1.21 .nbt 结构文件格式：
    # - palette: TAG_List of TAG_Compound (each has Name, optionally Properties)
    # - blocks: TAG_List of TAG_Compound (each has pos[3], state(palette idx), optionally nbt)
    # - size: TAG_List of 3 TAG_Int
    
    palette = nbtfile['palette']
    blocks = nbtfile['blocks']
    size = nbtfile['size']
    
    width = size[0].value
    height = size[1].value
    length = size[2].value
    
    print(f'Structure size: {width} x {height} x {length}')
    print(f'Palette entries: {len(palette)}')
    print(f'Block entries: {len(blocks)}')
    
    # Find air palette index(es)
    air_indices = set()
    structure_void_idx = None
    
    for i, entry in enumerate(palette):
        name = entry['Name'].value
        if name == 'minecraft:air':
            air_indices.add(i)
            print(f'  Palette[{i}] = minecraft:air')
        elif name == 'minecraft:structure_void':
            structure_void_idx = i
            print(f'  Palette[{i}] = minecraft:structure_void (already exists)')
    
    if not air_indices:
        print('No air blocks found in palette, nothing to do.')
        return
    
    # If structure_void not in palette, add it
    if structure_void_idx is None:
        sv_tag = nbt.TAG_Compound()
        sv_tag.tags.append(nbt.TAG_String(name='Name', value='minecraft:structure_void'))
        palette.append(sv_tag)
        structure_void_idx = len(palette) - 1
        print(f'  Added structure_void at palette index {structure_void_idx}')
    
    # Build a position set of all solid blocks to identify interior air
    solid_positions = set()
    air_blocks = []
    
    for block in blocks:
        pos = (block['pos'][0].value, block['pos'][1].value, block['pos'][2].value)
        state_idx = block['state'].value
        if state_idx in air_indices:
            air_blocks.append(block)
        else:
            solid_positions.add(pos)
    
    print(f'Found {len(air_blocks)} air blocks, {len(solid_positions)} solid blocks')
    
    # Keep air blocks that are clearly "interior" spaces (have solid neighbors on multiple sides)
    # Replace all other air blocks with structure_void
    converted = 0
    kept_air = 0
    
    for block in air_blocks:
        x = block['pos'][0].value
        y = block['pos'][1].value
        z = block['pos'][2].value
        
        # Count how many of the 6 cardinal neighbors are solid
        neighbors = [
            (x+1, y, z), (x-1, y, z),
            (x, y+1, z), (x, y-1, z),
            (x, y, z+1), (x, y, z-1),
        ]
        solid_count = sum(1 for n in neighbors if n in solid_positions)
        
        # If surrounded by ≥3 solid blocks, it's likely interior (keep as air)
        # Otherwise it's exterior padding (convert to structure_void)
        if solid_count >= 3:
            kept_air += 1
        else:
            block['state'].value = structure_void_idx
            converted += 1
    
    print(f'Converted {converted} exterior air → structure_void')
    print(f'Kept {kept_air} interior air blocks')
    
    # Save
    nbtfile.write_file(filepath)
    print(f'Saved to {filepath}')

if __name__ == '__main__':
    path = sys.argv[1] if len(sys.argv) > 1 else \
        'src/main/resources/data/stardewcraft/structure/wizard_tower_exterior.nbt'
    convert_air_to_structure_void(path)
