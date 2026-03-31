#!/usr/bin/env python3
"""Debug XWB entry format."""
import struct

f = open('源文件/音频文件/Wave Bank.xwb', 'rb')
magic = struct.unpack('<I', f.read(4))[0]
ver = struct.unpack('<I', f.read(4))[0]
hver = struct.unpack('<I', f.read(4))[0]
segs = [(struct.unpack('<I', f.read(4))[0], struct.unpack('<I', f.read(4))[0]) for _ in range(5)]

print(f"Version: {ver}, HeaderVersion: {hver}")
for i, (off, ln) in enumerate(segs):
    print(f"  Seg[{i}]: offset={off}, length={ln}")

# Bank data
f.seek(segs[0][0])
flags = struct.unpack('<I', f.read(4))[0]
count = struct.unpack('<I', f.read(4))[0]
bank_name = f.read(64).split(b'\x00')[0].decode('ascii')
meta_elem_size = struct.unpack('<I', f.read(4))[0]
name_elem_size = struct.unpack('<I', f.read(4))[0]
alignment = struct.unpack('<I', f.read(4))[0]
compact_fmt = struct.unpack('<I', f.read(4))[0]
print(f"Bank: {bank_name}, count={count}, meta_elem={meta_elem_size}, name_elem={name_elem_size}, align={alignment}")

# Entry 210
idx = 210
f.seek(segs[1][0] + idx * meta_elem_size)
fad = struct.unpack('<I', f.read(4))[0]
fmt_packed = struct.unpack('<I', f.read(4))[0]
play_off = struct.unpack('<I', f.read(4))[0]
play_len = struct.unpack('<I', f.read(4))[0]

tag = fmt_packed & 0x3
ch = ((fmt_packed >> 2) & 0x7) + 1
sr = (fmt_packed >> 5) & 0x3FFFF
ba_raw = (fmt_packed >> 23) & 0xFF
bps_flag = (fmt_packed >> 31) & 0x1
ba = (ba_raw + 22) * ch

print(f"\nEntry {idx} (0x{idx:08x}):")
print(f"  fmt_packed = 0x{fmt_packed:08X}")
print(f"  format_tag={tag} channels={ch} sample_rate={sr}")
print(f"  block_align_raw={ba_raw} block_align={ba} bps_flag={bps_flag}")
print(f"  play_offset={play_off} play_length={play_len}")
if ba > 0:
    spb = ((ba - 7 * ch) * 2) // ch + 2
    print(f"  samples_per_block={spb}")

# Also check first bytes of the entry data
wave_data_offset = segs[4][0]
f.seek(wave_data_offset + play_off)
first_bytes = f.read(min(32, play_len))
print(f"  First 32 bytes: {first_bytes.hex()}")

f.close()
