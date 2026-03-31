#!/usr/bin/env python3
"""
XWB (XACT Wave Bank) extractor for Stardew Valley audio.
Handles PCM and MS-ADPCM formats.
Outputs WAV files that ffmpeg can then convert to OGG.
"""
import struct
import os
import sys
import wave

WAVEBANK_HEADER_MAGIC = 0x444E4257  # "WBND"
BANKDATA_SEGMENT = 0
ENTRYMETADATA_SEGMENT = 1
SEEKTABLE_SEGMENT = 2
ENTRYNAME_SEGMENT = 3
ENTRYWAVEDATA_SEGMENT = 4

FORMAT_PCM = 0
FORMAT_XMA = 1
FORMAT_ADPCM = 2
FORMAT_WMA = 3

BANKDATA_FLAGS_COMPACT = 0x00020000

MSADPCM_COEFFICIENTS = [
    (256, 0), (512, -256), (0, 0), (192, 64),
    (240, 0), (460, -208), (392, -232)
]


def read_uint32(f):
    return struct.unpack('<I', f.read(4))[0]


def decode_miniwaveformat(packed):
    format_tag = packed & 0x3
    channels = (packed >> 2) & 0x7  # raw channel count (NOT minus-1)
    samples_per_sec = (packed >> 5) & 0x3FFFF
    block_align_raw = (packed >> 23) & 0xFF
    bits_per_sample_flag = (packed >> 31) & 0x1
    if format_tag == FORMAT_PCM:
        bits_per_sample = 16 if bits_per_sample_flag else 8
        block_align = channels * (bits_per_sample // 8)
    elif format_tag == FORMAT_ADPCM:
        bits_per_sample = 4
        block_align = (block_align_raw + 22) * channels
    else:
        bits_per_sample = 16
        block_align = block_align_raw
    return {
        'format_tag': format_tag,
        'channels': channels,
        'samples_per_sec': samples_per_sec,
        'block_align': block_align,
        'bits_per_sample': bits_per_sample,
    }


def write_wav_pcm(path, pcm_data, channels, sample_rate, bits_per_sample):
    with wave.open(path, 'wb') as wf:
        wf.setnchannels(channels)
        wf.setsampwidth(bits_per_sample // 8)
        wf.setframerate(sample_rate)
        wf.writeframes(pcm_data)


def write_wav_adpcm(path, adpcm_data, channels, sample_rate, block_align):
    samples_per_block = ((block_align - (7 * channels)) * 2) // channels + 2
    num_coeff = len(MSADPCM_COEFFICIENTS)
    cb_size = 2 + 2 + 4 * num_coeff  # 32
    avg_bytes_per_sec = (sample_rate * block_align) // samples_per_block if samples_per_block > 0 else 0

    fmt_data = struct.pack('<HHIIHHH',
        0x0002, channels, sample_rate, avg_bytes_per_sec,
        block_align, 4, cb_size)
    fmt_extra = struct.pack('<HH', samples_per_block, num_coeff)
    for c1, c2 in MSADPCM_COEFFICIENTS:
        fmt_extra += struct.pack('<hh', c1, c2)
    fmt_data += fmt_extra

    total_samples = 0
    if len(adpcm_data) > 0 and block_align > 0:
        num_blocks = len(adpcm_data) // block_align
        remainder = len(adpcm_data) % block_align
        total_samples = num_blocks * samples_per_block
        if remainder > (7 * channels):
            total_samples += ((remainder - (7 * channels)) * 2) // channels + 2

    fmt_chunk = b'fmt ' + struct.pack('<I', len(fmt_data)) + fmt_data
    fact_chunk = b'fact' + struct.pack('<II', 4, total_samples)
    data_chunk = b'data' + struct.pack('<I', len(adpcm_data)) + adpcm_data
    riff_size = 4 + len(fmt_chunk) + len(fact_chunk) + len(data_chunk)

    with open(path, 'wb') as out:
        out.write(b'RIFF')
        out.write(struct.pack('<I', riff_size))
        out.write(b'WAVE')
        out.write(fmt_chunk)
        out.write(fact_chunk)
        out.write(data_chunk)


class WaveBankEntry:
    def __init__(self):
        self.flags_and_duration = 0
        self.format = {}
        self.play_offset = 0
        self.play_length = 0


def extract_xwb(xwb_path, output_dir, indices=None):
    os.makedirs(output_dir, exist_ok=True)
    results = {}
    with open(xwb_path, 'rb') as f:
        magic = read_uint32(f)
        if magic != WAVEBANK_HEADER_MAGIC:
            raise ValueError(f"Bad magic: 0x{magic:08X}")
        version = read_uint32(f)
        header_version = read_uint32(f)
        segments = []
        for _ in range(5):
            segments.append((read_uint32(f), read_uint32(f)))

        f.seek(segments[BANKDATA_SEGMENT][0])
        bank_flags = read_uint32(f)
        entry_count = read_uint32(f)
        bank_name = f.read(64).split(b'\x00')[0].decode('ascii', errors='replace')
        entry_meta_element_size = read_uint32(f)
        entry_name_element_size = read_uint32(f)
        alignment = read_uint32(f)
        compact_format = decode_miniwaveformat(read_uint32(f))
        is_compact = (bank_flags & BANKDATA_FLAGS_COMPACT) != 0

        print(f"Bank: {bank_name}, Entries: {entry_count}, Compact: {is_compact}")

        entry_names = {}
        if segments[ENTRYNAME_SEGMENT][1] > 0 and entry_name_element_size > 0:
            f.seek(segments[ENTRYNAME_SEGMENT][0])
            for i in range(entry_count):
                name = f.read(entry_name_element_size).split(b'\x00')[0].decode('ascii', errors='replace')
                if name:
                    entry_names[i] = name

        entries = []
        wave_data_offset = segments[ENTRYWAVEDATA_SEGMENT][0]

        if is_compact:
            f.seek(segments[ENTRYMETADATA_SEGMENT][0])
            offsets = []
            for i in range(entry_count):
                val = read_uint32(f)
                offsets.append((val & 0x1FFFFF) * alignment)
            for i in range(entry_count):
                e = WaveBankEntry()
                e.format = compact_format.copy()
                e.play_offset = offsets[i]
                e.play_length = (offsets[i+1] if i < entry_count-1 else segments[ENTRYWAVEDATA_SEGMENT][1]) - offsets[i]
                entries.append(e)
        else:
            f.seek(segments[ENTRYMETADATA_SEGMENT][0])
            for i in range(entry_count):
                e = WaveBankEntry()
                if entry_meta_element_size >= 24:
                    e.flags_and_duration = read_uint32(f)
                    e.format = decode_miniwaveformat(read_uint32(f))
                    e.play_offset = read_uint32(f)
                    e.play_length = read_uint32(f)
                    read_uint32(f)  # loop offset
                    read_uint32(f)  # loop length
                else:
                    e.play_offset = read_uint32(f)
                    e.play_length = read_uint32(f)
                    e.format = compact_format.copy()
                entries.append(e)

        extracted = 0
        for i, entry in enumerate(entries):
            if indices is not None and i not in indices:
                continue
            fmt = entry.format
            f.seek(wave_data_offset + entry.play_offset)
            raw_data = f.read(entry.play_length)
            if not raw_data:
                continue
            name = entry_names.get(i, f"{i:08x}")
            out_path = os.path.join(output_dir, f"{name}.wav")
            if fmt['format_tag'] == FORMAT_PCM:
                write_wav_pcm(out_path, raw_data, fmt['channels'], fmt['samples_per_sec'], fmt['bits_per_sample'])
            elif fmt['format_tag'] == FORMAT_ADPCM:
                write_wav_adpcm(out_path, raw_data, fmt['channels'], fmt['samples_per_sec'], fmt['block_align'])
            else:
                print(f"  [{i:04d}] Unsupported format tag {fmt['format_tag']}")
                continue
            results[i] = out_path
            extracted += 1
        print(f"Extracted {extracted}/{entry_count} entries to {output_dir}")
    return results


if __name__ == '__main__':
    if len(sys.argv) < 3:
        print("Usage: python extract_xwb.py <input.xwb> <output_dir> [idx1,idx2,...]")
        sys.exit(1)
    xwb_path = sys.argv[1]
    output_dir = sys.argv[2]
    indices = None
    if len(sys.argv) > 3:
        indices = set()
        for part in sys.argv[3].split(','):
            p = part.strip()
            indices.add(int(p, 16) if p.startswith(('0x', '0X')) else int(p))
    extract_xwb(xwb_path, output_dir, indices)
