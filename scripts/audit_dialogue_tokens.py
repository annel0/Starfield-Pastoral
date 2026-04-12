#!/usr/bin/env python3
"""Audit all NPC dialogue entries for special tokens that need escaping."""
import json, re, collections

with open('src/main/resources/assets/stardewcraft/lang/zh_cn.json', encoding='utf-8-sig') as f:
    data = json.load(f)

npc_entries = {k: v for k, v in data.items() if k.startswith('stardewcraft.npc.') and isinstance(v, str)}
print(f'Total NPC dialogue entries: {len(npc_entries)}')

all_text = '\n'.join(npc_entries.values())

# $ tokens
dollar_tokens = re.findall(r'\$[a-zA-Z][a-zA-Z0-9]*', all_text)
dollar_counter = collections.Counter(dollar_tokens)
print(f'\n=== $ tokens ===')
for token, count in dollar_counter.most_common():
    print(f'  {token}: {count}')

# % tokens
percent_tokens = re.findall(r'%[a-zA-Z][a-zA-Z0-9]*', all_text)
percent_counter = collections.Counter(percent_tokens)
print(f'\n=== % tokens ===')
for token, count in percent_counter.most_common():
    print(f'  {token}: {count}')

# || random dialogue
pipe2 = all_text.count('||')
print(f'\n=== || (random dialogue): {pipe2} ===')

# single | (branch separator)
single_pipe = len(re.findall(r'(?<!\|)\|(?!\|)', all_text))
print(f'=== single | (branch): {single_pipe} ===')

# # (page break / command delimiter)
hash_entries = sum(1 for v in npc_entries.values() if '#' in v)
print(f'=== entries with #: {hash_entries} ===')

# ^ (gender split)
caret_entries = sum(1 for v in npc_entries.values() if '^' in v)
print(f'=== entries with ^: {caret_entries} ===')

# @ (player name)
at_entries = sum(1 for v in npc_entries.values() if '@' in v)
print(f'=== entries with @: {at_entries} ===')

# ${...}$ inline gender
inline_gender = sum(1 for v in npc_entries.values() if '${' in v)
print(f'=== entries with ${{...}}$: {inline_gender} ===')

# / in values (sometimes used as path separator in SDV)
slash_entries = sum(1 for v in npc_entries.values() if '/' in v)
print(f'=== entries with /: {slash_entries} ===')

# Show sample entries with | that are NOT ||
print('\n=== Sample entries with single | (not ||) ===')
count = 0
for k, v in npc_entries.items():
    # Find single | not part of ||
    stripped = v.replace('||', '')
    if '|' in stripped:
        print(f'  {k}: ...{v[:120]}...')
        count += 1
        if count >= 10:
            break

# Show sample entries with remaining problematic tokens
print('\n=== Sample entries with $8 ===')
count = 0
for k, v in npc_entries.items():
    if '$8' in v:
        print(f'  {k}: ...{v[:120]}...')
        count += 1
        if count >= 5:
            break

# Show unique $ token patterns not commonly handled
known_dollar = {'$b', '$e', '$h', '$s', '$u', '$l', '$a', '$q', '$r', '$c', '$d', '$p', '$k', '$v', '$y', '$1'}
unknown = {t for t in dollar_counter if t not in known_dollar}
if unknown:
    print(f'\n=== UNKNOWN $ tokens (not in handler): {unknown} ===')
    for t in sorted(unknown):
        # Show example
        for k, v in npc_entries.items():
            if t in v:
                print(f'  {t} in {k}: {v[:100]}')
                break
