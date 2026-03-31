#!/usr/bin/env python3
"""
Fix dedicated-server crash by extracting client-only code from payload handle methods.

For each payload file that references net.minecraft.client.Minecraft in its handle method,
this script:
1. Extracts the body of `context.enqueueWork(() -> { ... });` lambda
2. Moves it to a new `handleClient(payload)` method annotated @OnlyIn(Dist.CLIENT)
3. Replaces the lambda body with `handleClient(payloadVar)`

The @OnlyIn annotation causes NeoForge's RuntimeDistCleaner to strip handleClient
on the dedicated server BEFORE JVM verification, preventing client class loading.
"""
import os
import re
import sys

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# All payload files that import Minecraft (from grep analysis)
PAYLOAD_FILES = [
    # network/payload
    "src/main/java/com/stardew/craft/network/payload/OpenAnimalMoveHomeScreenPayload.java",
    "src/main/java/com/stardew/craft/network/payload/OpenTreasureChestPayload.java",
    "src/main/java/com/stardew/craft/network/payload/OpenDecorationScreenPayload.java",
    "src/main/java/com/stardew/craft/network/payload/OpenSleepConfirmScreenPayload.java",
    "src/main/java/com/stardew/craft/network/payload/OpenNpcDialogueScreenPayload.java",
    "src/main/java/com/stardew/craft/network/payload/OpenSofaColorScreenPayload.java",
    "src/main/java/com/stardew/craft/network/payload/OpenAnimalPurchaseScreenPayload.java",
    # fishing/network
    "src/main/java/com/stardew/craft/fishing/network/FishingBitePromptPayload.java",
    "src/main/java/com/stardew/craft/fishing/network/FishingFailVisualPayload.java",
    "src/main/java/com/stardew/craft/fishing/network/FishingRodCastStatePayload.java",
    "src/main/java/com/stardew/craft/fishing/network/FishingStartPayload.java",
    "src/main/java/com/stardew/craft/fishing/network/FishingCatchVisualPayload.java",
    "src/main/java/com/stardew/craft/fishing/network/StartMinigamePayload.java",
    "src/main/java/com/stardew/craft/fishing/network/FishingHookedAnimPayload.java",
    # network/overnight
    "src/main/java/com/stardew/craft/network/overnight/OvernightSettlementPayload.java",
    # combat/network
    "src/main/java/com/stardew/craft/combat/network/DarkSwordBloodDebtPayload.java",
    "src/main/java/com/stardew/craft/combat/network/DarkSwordBloodMoonPayload.java",
    "src/main/java/com/stardew/craft/combat/network/BrokenTridentCatchPayload.java",
    "src/main/java/com/stardew/craft/combat/network/DragontoothShivBreathPayload.java",
    "src/main/java/com/stardew/craft/combat/network/WindSpirePayload.java",
    "src/main/java/com/stardew/craft/combat/network/IridiumNeedleFrenzyPayload.java",
    "src/main/java/com/stardew/craft/combat/network/SteelFalchionTracePayload.java",
    "src/main/java/com/stardew/craft/combat/network/DwarfFortressPayload.java",
    "src/main/java/com/stardew/craft/combat/network/DashMovementPayload.java",
    "src/main/java/com/stardew/craft/combat/network/IridiumNeedleThrustStrikePayload.java",
    "src/main/java/com/stardew/craft/combat/network/SteelSpineFuryStrikePayload.java",
    "src/main/java/com/stardew/craft/combat/network/DwarfDaggerRushPayload.java",
    "src/main/java/com/stardew/craft/combat/network/CarvingKnifeThrustStrikePayload.java",
    "src/main/java/com/stardew/craft/combat/network/CrystalDaggerLayerPayload.java",
    "src/main/java/com/stardew/craft/combat/network/SilverSaberFoldbackPayload.java",
    "src/main/java/com/stardew/craft/combat/network/CrystalDaggerBurstPayload.java",
    "src/main/java/com/stardew/craft/combat/network/ElfBladePayload.java",
    "src/main/java/com/stardew/craft/combat/network/InsectEyeStancePayload.java",
    "src/main/java/com/stardew/craft/combat/network/DwarfDaggerThrustPayload.java",
    "src/main/java/com/stardew/craft/combat/network/LavaKatanaReverbPayload.java",
    "src/main/java/com/stardew/craft/combat/network/SteelSpineFuryHitPayload.java",
    "src/main/java/com/stardew/craft/combat/network/SteelSpineFuryEnterPayload.java",
    "src/main/java/com/stardew/craft/combat/network/IridiumNeedleCritPayload.java",
    "src/main/java/com/stardew/craft/combat/network/BurglarShankLootPayload.java",
    "src/main/java/com/stardew/craft/combat/network/TremorBlockPayload.java",
    "src/main/java/com/stardew/craft/combat/network/SteelSpineFuryPayload.java",
    "src/main/java/com/stardew/craft/combat/network/ForestBlessingPayload.java",
    "src/main/java/com/stardew/craft/combat/network/BrokenTridentThrustStrikePayload.java",
    "src/main/java/com/stardew/craft/combat/network/WickedKrisPoisonStatusPayload.java",
    "src/main/java/com/stardew/craft/combat/network/ObsidianResonanceSyncPayload.java",
    "src/main/java/com/stardew/craft/combat/network/TemplarVowPayload.java",
]


def find_handle_method(content):
    """Find the handle method and extract its components."""
    pattern = re.compile(
        r'(\n([ \t]+))(?:@\w+\s*(?:\([^)]*\))?\s*\n\s*)*'
        r'public\s+static\s+void\s+handle\s*\('
        r'\s*(?:final\s+)?(\w+)\s+(\w+)\s*,'
        r'\s*(?:final\s+)?(?:IPayloadContext)\s+(\w+)\s*\)',
        re.MULTILINE
    )
    m = pattern.search(content)
    if not m:
        return None
    indent = m.group(2)  # leading whitespace (spaces/tabs)
    payload_type = m.group(3)
    payload_var = m.group(4)
    context_var = m.group(5)

    # method_start = position of the first non-ws char of the method line
    method_start = m.start() + 1  # skip the leading \n

    # Find matching braces for the method body
    brace_start = content.index('{', m.end())
    depth = 1
    i = brace_start + 1
    while i < len(content) and depth > 0:
        if content[i] == '{':
            depth += 1
        elif content[i] == '}':
            depth -= 1
        i += 1
    method_end = i

    method_body = content[brace_start + 1:method_end - 1]

    return {
        'indent': indent,
        'payload_type': payload_type,
        'payload_var': payload_var,
        'context_var': context_var,
        'method_start': method_start,
        'method_end': method_end,
        'method_body': method_body,
    }


def extract_enqueue_body(method_body, context_var):
    """Extract the lambda body from context.enqueueWork(() -> { ... })"""
    eq_pattern = re.compile(
        rf'{re.escape(context_var)}\.enqueueWork\s*\(\s*\(\s*\)\s*->\s*',
        re.MULTILINE
    )
    m = eq_pattern.search(method_body)
    if not m:
        return None, None

    after_arrow = method_body[m.end():]
    stripped = after_arrow.lstrip()

    if stripped.startswith('{'):
        brace_pos = method_body.index('{', m.end())
        depth = 1
        i = brace_pos + 1
        while i < len(method_body) and depth > 0:
            if method_body[i] == '{':
                depth += 1
            elif method_body[i] == '}':
                depth -= 1
            i += 1
        lambda_body = method_body[brace_pos + 1:i - 1]
        return lambda_body, True
    else:
        depth = 1
        i = m.end()
        while i < len(method_body) and depth > 0:
            if method_body[i] == '(':
                depth += 1
            elif method_body[i] == ')':
                depth -= 1
            i += 1
        expr = method_body[m.end():i - 1].strip()
        return expr, False


def reindent_block(text, target_indent):
    """Re-indent a block of code. Preserves original indent characters (tabs/spaces)."""
    lines = text.split('\n')
    non_empty = [l for l in lines if l.strip()]
    if not non_empty:
        return ''
    min_indent = min(len(l) - len(l.lstrip()) for l in non_empty)
    result = []
    for line in lines:
        s = line.strip()
        if not s:
            continue  # skip blank lines
        # Keep the original indent characters beyond the minimum prefix
        orig_ws = line[:len(line) - len(line.lstrip())]
        beyond_min = orig_ws[min_indent:]
        result.append(target_indent + beyond_min + s)
    return '\n'.join(result)


def process_file(filepath):
    """Process a single payload file."""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    info = find_handle_method(content)
    if not info:
        print(f"  WARNING: Could not find handle method in {filepath}")
        return False

    indent = info['indent']
    payload_type = info['payload_type']
    payload_var = info['payload_var']
    context_var = info['context_var']

    lambda_body, is_block = extract_enqueue_body(info['method_body'], context_var)
    if lambda_body is None:
        print(f"  WARNING: Could not find enqueueWork lambda in {filepath}")
        return False

    # Build new handle method (thin wrapper)
    indent_step = '\t' if '\t' in indent else '    '
    new_handle = (
        f"{indent}public static void handle({payload_type} {payload_var}, IPayloadContext {context_var}) {{\n"
        f"{indent}{indent_step}{context_var}.enqueueWork(() -> handleClient({payload_var}));\n"
        f"{indent}}}"
    )

    # Build handleClient body
    indent_step = '\t' if '\t' in indent else '    '
    body_indent = indent + indent_step
    if is_block:
        body_text = reindent_block(lambda_body, body_indent)
    else:
        body_text = f"{body_indent}{lambda_body};"

    new_handle_client = (
        f"\n\n{indent}@net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)\n"
        f"{indent}private static void handleClient({payload_type} {payload_var}) {{\n"
        f"{body_text}\n"
        f"{indent}}}"
    )

    new_content = content[:info['method_start']] + new_handle + new_handle_client + content[info['method_end']:]

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(new_content)

    return True


def main():
    success = 0
    fail = 0
    
    for relpath in PAYLOAD_FILES:
        filepath = os.path.join(BASE, relpath)
        if not os.path.exists(filepath):
            print(f"SKIP (not found): {relpath}")
            fail += 1
            continue
        
        print(f"Processing: {relpath}")
        try:
            if process_file(filepath):
                success += 1
                print(f"  OK")
            else:
                fail += 1
        except Exception as e:
            print(f"  ERROR: {e}")
            fail += 1
    
    print(f"\nDone: {success} succeeded, {fail} failed")
    return 0 if fail == 0 else 1


if __name__ == '__main__':
    sys.exit(main())
