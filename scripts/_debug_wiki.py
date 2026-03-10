import re

import requests
from bs4 import BeautifulSoup

url = "https://zh.stardewvalleywiki.com/%E5%86%9C%E4%BD%9C%E7%89%A9"
html = requests.get(url, headers={"User-Agent": "test"}, timeout=30).text
soup = BeautifulSoup(html, "lxml")

def slugify(display_name: str) -> str:
    s = display_name.strip().lower().replace(chr(8217), "").replace("'", "")
    s = re.sub(r"[^a-z0-9\s_-]", "", s)
    s = re.sub(r"[\s-]+", "_", s)
    s = re.sub(r"_+", "_", s)
    return s.strip("_")

out = []
for h3 in soup.select("h3"):
    img = h3.find("img")
    alt = (img.get("alt") if img else "") or ""
    if alt.endswith(".png"):
        disp = alt[:-4]
        out.append((disp, slugify(disp)))

print("h3 img count", len(out))
for a, b in out[:40]:
    print(a, "->", b)
