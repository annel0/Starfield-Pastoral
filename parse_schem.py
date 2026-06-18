import gzip

def read_string(f):
    try:
        length = int.from_bytes(f.read(2), "big")
        return f.read(length).decode("utf-8")
    except: return ""

def skip_tag(f, t):
    if t == 1: f.read(1)
    elif t == 2: f.read(2)
    elif t == 3: f.read(4)
    elif t == 4: f.read(8)
    elif t == 5: f.read(4)
    elif t == 6: f.read(8)
    elif t in [7, 11, 12]:
        l = int.from_bytes(f.read(4), "big")
        f.read(l * {7:1, 11:4, 12:8}[t])
    elif t == 8: f.read(int.from_bytes(f.read(2), "big"))
    elif t == 9:
        st = int.from_bytes(f.read(1), "big")
        l = int.from_bytes(f.read(4), "big")
        for _ in range(l): skip_tag(f, st)
    elif t == 10:
        while True:
            t_buf = f.read(1)
            if not t_buf or t_buf[0] == 0: break
            read_string(f)
            skip_tag(f, t_buf[0])

def read_tag(f, targets):
    res = {}
    if not f.read(1): return res # root tag type
    read_string(f) # root name
    def walk(f):
        while True:
            t_buf = f.read(1)
            if not t_buf or t_buf[0] == 0: break
            t = t_buf[0]
            name = read_string(f)
            if name in targets:
                if t == 1: res[name] = int.from_bytes(f.read(1), "big", signed=True)
                elif t == 2: res[name] = int.from_bytes(f.read(2), "big", signed=True)
                elif t == 3: res[name] = int.from_bytes(f.read(4), "big", signed=True)
                elif t == 4: res[name] = int.from_bytes(f.read(8), "big", signed=True)
                elif t == 7: res[name] = f.read(int.from_bytes(f.read(4), "big"))
                elif t == 11:
                    l = int.from_bytes(f.read(4), "big")
                    res[name] = [int.from_bytes(f.read(4), "big", signed=True) for _ in range(l)]
                elif t == 10:
                    if name == "Palette":
                        p = {}
                        while True:
                            st_buf = f.read(1)
                            if not st_buf or st_buf[0] == 0: break
                            pn = read_string(f)
                            if st_buf[0] == 3: p[pn] = int.from_bytes(f.read(4), "big", signed=True)
                            else: skip_tag(f, st_buf[0])
                        res[name] = p
                    elif name == "Metadata":
                        m = {}
                        while True:
                            st_buf = f.read(1)
                            if not st_buf or st_buf[0] == 0: break
                            mn = read_string(f)
                            if st_buf[0] in [1,2,3,4]: m[mn] = int.from_bytes(f.read({1:1,2:2,3:4,4:8}[st_buf[0]]), "big", signed=True)
                            else: skip_tag(f, st_buf[0])
                        res[name] = m
                    else: walk(f)
                else: skip_tag(f, t)
            else: skip_tag(f, t)
    walk(f)
    return res

def read_varint(data, pos):
    val = 0; shift = 0
    while True:
        b = data[pos]; val |= (b & 0x7F) << shift; pos += 1
        if not (b & 0x80): break
        shift += 7
    return val, pos

with gzip.open('tmp_models/沙漠节.schem', 'rb') as f:
    res = read_tag(f, ["Width", "Height", "Length", "Offset", "WEOffset", "Metadata", "Palette", "BlockData"])

print(f"Dimensions: {res.get('Width')}x{res.get('Height')}x{res.get('Length')}")
print(f"Offset: {res.get('Offset')}")
print(f"WEOffset: {res.get('WEOffset')}")
print(f"Metadata: {res.get('Metadata')}")
pal = res.get('Palette', {})
air_indices = {v for k, v in pal.items() if k == "minecraft:air"}
print(f"Air names in palette: {[k for k in pal if pal[k] in air_indices]}")
bd = res.get('BlockData')
if bd and res.get('Width'):
    w, h, l = res['Width'], res['Height'], res['Length']
    pos = 0
    for y in range(h):
        cnt = 0
        for _ in range(w * l):
            val, pos = read_varint(bd, pos)
            if val not in air_indices: cnt += 1
        print(f"Layer {y}: {cnt} non-air")
