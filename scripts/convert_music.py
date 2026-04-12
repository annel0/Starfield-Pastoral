#!/usr/bin/env python3
"""Convert SDV wave bank .wav files to .ogg for MC mod music system."""
import soundfile as sf
import os

tracks = {
    # Outdoor seasonal music (12)
    "spring1": "0000005d",
    "spring2": "0000005b",
    "spring3": "0000005c",
    "summer1": "0000007a",
    "summer2": "0000007b",
    "summer3": "00000073",
    "fall1": "00000079",
    "fall2": "00000077",
    "fall3": "00000078",
    "winter1": "0000007e",
    "winter2": "0000007c",
    "winter3": "0000007d",
    # Rain ambient (1)
    "rain": "00000074",
    # Night ambient (1)
    "spring_night_ambient": "00000159",
    # Day ambient (4)
    "spring_day_ambient": "000000b3",
    "summer_day_ambient": "00000153",
    "fall_day_ambient": "00000152",
    "winter_day_ambient": "00000162",
    # Town (1)
    "springtown": "0000005e",
    # Shop (1)
    "marnieShop": "000000b4",
    # Mine (6 - 3 per group, randomly selected)
    "crystal_bells": "00000040",
    "cavern": "00000041",
    "secret_gnomes": "00000042",
    "cloth": "00000043",
    "icicles": "00000044",
    "xor": "00000045",
}

src_dir = os.path.expanduser("~/\u6e38\u620f\u5236\u4f5c/StardewCraft/\u6e90\u6587\u4ef6/\u97f3\u9891\u6587\u4ef6")
dst_dir = os.path.expanduser("~/\u6e38\u620f\u5236\u4f5c/StardewCraft/src/main/resources/assets/stardewcraft/sounds/music")

os.makedirs(dst_dir, exist_ok=True)

converted = 0
failed = 0
for name, hex_id in tracks.items():
    src = os.path.join(src_dir, f"{hex_id}.wav")
    dst = os.path.join(dst_dir, f"{name}.ogg")
    if not os.path.exists(src):
        print(f"MISSING: {src}")
        failed += 1
        continue
    try:
        data, sr = sf.read(src)
        sf.write(dst, data, sr, format='OGG', subtype='VORBIS')
        size_mb = os.path.getsize(dst) / (1024 * 1024)
        print(f"OK: {name}.ogg ({size_mb:.1f}MB, {sr}Hz)")
        converted += 1
    except Exception as e:
        print(f"FAIL: {name} -> {e}")
        failed += 1

print(f"\nDone: {converted} converted, {failed} failed")
