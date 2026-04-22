import json
d = json.load(open('源文件/Content/Data/Locations.json', encoding='utf-8-sig'))
for e in d['Beach']['Fish']:
    iid = e.get('ItemId') or e.get('Id') or ''
    print(f'{iid:25s} chance={e.get("Chance")!r:6}  prec={e.get("Precedence")!r:6}  lvl={e.get("MinFishingLevel")!r:5}  season={e.get("Season")!r}')
