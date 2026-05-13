#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
MANIFEST="$ROOT_DIR/tools/ui_atlas_slices.csv"
GUI_DIR="$ROOT_DIR/src/main/resources/assets/stardewcraft/textures/gui"

atlas_path() {
    case "$1" in
        cursors) printf '%s\n' "$GUI_DIR/cursors.png" ;;
        mouse_cursors2) printf '%s\n' "$GUI_DIR/mouse_cursors2.png" ;;
        cursors_1_6) printf '%s\n' "$GUI_DIR/cursors_1_6.png" ;;
        menu_tiles) printf '%s\n' "$GUI_DIR/animal_query/menu_tiles.png" ;;
        billboard) printf '%s\n' "$GUI_DIR/billboard.png" ;;
        *)
            printf 'Unknown atlas alias: %s\n' "$1" >&2
            return 1
            ;;
    esac
}

while IFS=, read -r output atlas u v w h || [[ -n "${output:-}" ]]; do
    case "${output:-}" in
        ''|'#'*) continue ;;
    esac

    source_png="$(atlas_path "$atlas")"
    dest_png="$GUI_DIR/$output.png"
    mkdir -p "$(dirname "$dest_png")"

    tmp_png="$(mktemp -t stardewcraft-ui-slice).png"
    cp "$source_png" "$tmp_png"
    if [[ "$output" == "animal_query/menu_tile_0" ]]; then
        sips --cropOffset 0 192 --cropToHeightWidth 64 64 "$tmp_png" --out "$dest_png" >/dev/null
        sips --flip horizontal "$dest_png" --out "$dest_png" >/dev/null
    else
        sips --cropOffset "$v" "$u" --cropToHeightWidth "$h" "$w" "$tmp_png" --out "$dest_png" >/dev/null
    fi
    rm -f "$tmp_png"

    printf 'wrote %s (%sx%s from %s:%s,%s)\n' "$dest_png" "$w" "$h" "$atlas" "$u" "$v"
done < "$MANIFEST"