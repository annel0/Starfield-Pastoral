#!/usr/bin/env python3
"""
Batch fix: remove top-level client imports from payload/network files,
replace bare class names with fully-qualified names.
Only touches files that have client imports AND are outside /client/ packages.
"""
import re
import sys
from pathlib import Path

SRC_ROOT = Path(__file__).resolve().parent.parent / "src" / "main" / "java"

# Patterns for client imports to remove
CLIENT_IMPORT_RE = re.compile(
    r'^import\s+(com\.stardew\.craft\.client\.[A-Za-z0-9_.]+|net\.minecraft\.client\.[A-Za-z0-9_.]+)\s*;$'
)

# Files/dirs to SKIP (already client-only, or already handled manually)
SKIP_PATHS = {
    "com/stardew/craft/client",           # client package itself
    "com/stardew/craft/StardewCraftClient.java",  # client mod class
    "com/stardew/craft/mixin",            # mixins have separate config
    "com/stardew/craft/integration/jei",  # JEI is client-only mod
    "com/stardew/craft/integration/jade", # Jade is client-only mod
    "com/stardew/craft/weather",          # weather renderers are client-only
    "com/stardew/craft/communitycenter/cutscene/ScreenFade.java",  # already fixed
    "com/stardew/craft/communitycenter/client",  # client sub-package
    "com/stardew/craft/network/overnight/ClientOvernightHandler.java",  # client-only handler
    "com/stardew/craft/TestRT.java",      # P3 deletion target
    "com/stardew/craft/PrintMethods.java", # P3 deletion target
}

# Files already manually fixed in P0/P2
ALREADY_FIXED = {
    "com/stardew/craft/StardewCraft.java",
    "com/stardew/craft/block/BulletinBoardBlock.java",
    "com/stardew/craft/block/JojaVendingMachineBlock.java",
    "com/stardew/craft/npc/DwarfService.java",
    "com/stardew/craft/combat/WeaponTooltipBuilder.java",
    "com/stardew/craft/item/artisan/SmokedFishItem.java",
    "com/stardew/craft/event/FertilizerSyncEvents.java",
    "com/stardew/craft/event/MuseumDonationSyncEvents.java",
    "com/stardew/craft/item/cooking/CookingDishItem.java",  # false positive, but skip
}

def should_skip(rel_path: str) -> bool:
    for skip in SKIP_PATHS | ALREADY_FIXED:
        if rel_path.startswith(skip) or rel_path == skip:
            return True
    return False

def extract_simple_name(fqn: str) -> str:
    """com.stardew.craft.client.weapon.Foo -> Foo"""
    return fqn.rsplit('.', 1)[-1]

def process_file(filepath: Path) -> dict:
    """Process a single file. Returns info dict or None if no changes."""
    rel = str(filepath.relative_to(SRC_ROOT))
    if should_skip(rel):
        return None

    text = filepath.read_text(encoding='utf-8')
    lines = text.split('\n')

    # Find client imports
    client_imports = {}  # simple_name -> fqn
    import_lines_to_remove = set()

    for i, line in enumerate(lines):
        m = CLIENT_IMPORT_RE.match(line.strip())
        if m:
            fqn = m.group(1)
            simple = extract_simple_name(fqn)
            client_imports[simple] = fqn
            import_lines_to_remove.add(i)

    if not client_imports:
        return None

    # Remove import lines
    new_lines = [line for i, line in enumerate(lines) if i not in import_lines_to_remove]

    # Replace bare class names with FQN in the remaining text
    new_text = '\n'.join(new_lines)
    for simple, fqn in client_imports.items():
        # Use word boundary to avoid partial matches
        # But be careful: e.g. "Minecraft" should not match "net.minecraft"
        # We replace standalone occurrences only (not already part of FQN)
        # Negative lookbehind for dot (not part of fqn already)
        pattern = r'(?<!\.)(?<![A-Za-z0-9_])' + re.escape(simple) + r'(?![A-Za-z0-9_])'
        new_text = re.sub(pattern, fqn, new_text)

    if new_text == text:
        return None

    return {
        'path': filepath,
        'rel': rel,
        'imports_removed': client_imports,
        'new_text': new_text,
    }

def main():
    dry_run = '--dry-run' in sys.argv

    java_files = list(SRC_ROOT.rglob('*.java'))
    results = []

    for f in sorted(java_files):
        result = process_file(f)
        if result:
            results.append(result)

    print(f"Found {len(results)} files to fix:\n")
    for r in results:
        imports = ', '.join(r['imports_removed'].keys())
        print(f"  {r['rel']}: [{imports}]")

    if dry_run:
        print("\n[DRY RUN] No files modified.")
        return

    print()
    for r in results:
        r['path'].write_text(r['new_text'], encoding='utf-8')
        print(f"  FIXED: {r['rel']}")

    print(f"\nDone. {len(results)} files modified.")

if __name__ == '__main__':
    main()
