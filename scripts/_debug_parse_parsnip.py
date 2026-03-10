import re
import requests
from bs4 import BeautifulSoup

url = "https://zh.stardewvalleywiki.com/%E5%86%9C%E4%BD%9C%E7%89%A9"
html = requests.get(url, headers={"User-Agent": "test"}, timeout=30).text
soup = BeautifulSoup(html, "html.parser")

def norm(s: str) -> str:
    s = re.sub(r"\[\d+\]", "", s)
    s = re.sub(r"\s+", " ", s)
    return s.strip()

# find Parsnip h3 by alt
h3 = None
for t in soup.select('h3'):
    img = t.find('img')
    if img and (img.get('alt') or '') == 'Parsnip.png':
        h3 = t
        break

assert h3

# scan to next h2/h3, find table
found = None
for el in h3.next_elements:
    name = getattr(el, 'name', None)
    if name in {'h2','h3'} and el is not h3:
        break
    if name == 'table':
        txt = norm(el.get_text(' ', strip=True))
        if '售价' in txt and ('恢复' in txt or '能量' in txt):
            found = el
            break

print('found table', bool(found))
print('table text snippet:', norm(found.get_text(' ', strip=True))[:300])

# header row
header_row = None
for tr in found.select('tr'):
    ths = tr.find_all('th')
    if ths:
        header_row = tr
        headers = [norm(th.get_text(' ', strip=True)) for th in ths]
        print('headers:', headers)
        break

print('---- rows ----')
for r_i, tr in enumerate(found.select('tr')):
    tds = tr.find_all(['td', 'th'])
    if not tds:
        continue
    row_cells = []
    for td in tds:
        if td.name == 'th':
            kind = 'th'
        else:
            kind = 'td'
        rs = int(td.get('rowspan') or 1)
        cs = int(td.get('colspan') or 1)
        row_cells.append((kind, rs, cs, norm(td.get_text(' ', strip=True))))
    print(f'row {r_i}: {len(row_cells)} cells')
    for c_i, (kind, rs, cs, text) in enumerate(row_cells):
        print(' ', c_i, kind, f'rs={rs}', f'cs={cs}', text)
