#!/bin/bash
# Convert SDV wave bank .wav files to .ogg for MC mod music system
FFMPEG="/tmp/ffmpeg"
SRC_DIR="$HOME/游戏制作/StardewCraft/源文件/音频文件"
DST_DIR="$HOME/游戏制作/StardewCraft/src/main/resources/assets/stardewcraft/sounds/music"

mkdir -p "$DST_DIR"

declare -A TRACKS
# Outdoor seasonal music (12)
TRACKS[spring1]=0000005d
TRACKS[spring2]=0000005b
TRACKS[spring3]=0000005c
TRACKS[summer1]=0000007a
TRACKS[summer2]=0000007b
TRACKS[summer3]=00000073
TRACKS[fall1]=00000079
TRACKS[fall2]=00000077
TRACKS[fall3]=00000078
TRACKS[winter1]=0000007e
TRACKS[winter2]=0000007c
TRACKS[winter3]=0000007d
# Rain ambient
TRACKS[rain]=00000074
# Night ambient
TRACKS[spring_night_ambient]=00000159
# Day ambient
TRACKS[spring_day_ambient]=000000b3
TRACKS[summer_day_ambient]=00000153
TRACKS[fall_day_ambient]=00000152
TRACKS[winter_day_ambient]=00000162
# Town
TRACKS[springtown]=0000005e
# Shop
TRACKS[marnieShop]=000000b4
# Mine tracks
TRACKS[crystal_bells]=00000040
TRACKS[cavern]=00000041
TRACKS[secret_gnomes]=00000042
TRACKS[cloth]=00000043
TRACKS[icicles]=00000044
TRACKS[xor]=00000045

CONVERTED=0
FAILED=0

for name in "${!TRACKS[@]}"; do
    hex_id="${TRACKS[$name]}"
    src="$SRC_DIR/${hex_id}.wav"
    dst="$DST_DIR/${name}.ogg"
    
    if [ ! -f "$src" ]; then
        echo "MISSING: $src"
        ((FAILED++))
        continue
    fi
    
    "$FFMPEG" -y -i "$src" -c:a libvorbis -q:a 4 "$dst" 2>/dev/null
    if [ $? -eq 0 ]; then
        size=$(du -h "$dst" | cut -f1)
        echo "OK: ${name}.ogg (${size})"
        ((CONVERTED++))
    else
        echo "FAIL: $name"
        ((FAILED++))
    fi
done

echo ""
echo "Done: $CONVERTED converted, $FAILED failed"
