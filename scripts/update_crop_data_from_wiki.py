from __future__ import annotations

import re
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

import requests
from bs4 import BeautifulSoup


WIKI_URL = "https://zh.stardewvalleywiki.com/%E5%86%9C%E4%BD%9C%E7%89%A9"

# Wiki display names don't always match our existing slugs / resourcepack folder names.
# Map wiki-derived slug -> our canonical slug.
SLUG_OVERRIDES: dict[str, str] = {
    # Wiki shows plural display name, but our existing data uses singular.
    "cranberries": "cranberry",
    # Wiki display name has no underscore.
    "powdermelon": "powder_melon",
}


@dataclass(frozen=True)
class CropRow:
    cn_name: str
    slug: str
    en_desc: str
    cn_desc: str
    seasons: str
    growth_days: int
    edible: bool
    price_n: int
    price_s: int
    price_g: int
    price_i: int
    energy_n: int
    energy_s: int
    energy_g: int
    energy_i: int
    health_n: int
    health_s: int
    health_g: int
    health_i: int
    can_regrow: bool
    regrow_days: int
    seed_buy_price: int

    def to_line(self) -> str:
        return "|".join(
            [
                self.cn_name,
                self.slug,
                self.en_desc,
                self.cn_desc,
                self.seasons,
                str(self.growth_days),
                "true" if self.edible else "false",
                str(self.price_n),
                str(self.price_s),
                str(self.price_g),
                str(self.price_i),
                str(self.energy_n),
                str(self.energy_s),
                str(self.energy_g),
                str(self.energy_i),
                str(self.health_n),
                str(self.health_s),
                str(self.health_g),
                str(self.health_i),
                "true" if self.can_regrow else "false",
                str(self.regrow_days),
                str(self.seed_buy_price),
            ]
        )


def slugify(display_name: str) -> str:
    s = display_name.strip().lower()
    s = s.replace("’", "").replace("'", "")
    s = re.sub(r"[^a-z0-9\s_-]", "", s)
    s = re.sub(r"[\s-]+", "_", s)
    s = re.sub(r"_+", "_", s)
    return s.strip("_")


def canonicalize_slug(slug: str) -> str:
    return SLUG_OVERRIDES.get(slug, slug)


def parse_ints(text: str) -> list[int]:
    return [int(x.replace(",", "")) for x in re.findall(r"\d[\d,]*", text)]


def parse_total_days(text: str) -> int | None:
    # Examples: "共：4 天"  "共：6-8 天"  "总计: 6 天"
    m = re.search(r"(?:共|总计)\s*[:：]\s*(\d+)(?:\s*[-~]\s*(\d+))?\s*天", text)
    if not m:
        return None
    a = int(m.group(1))
    b = int(m.group(2)) if m.group(2) else None
    return max(a, b) if b else a


def parse_regrow_days(text: str) -> int | None:
    # Examples: "持续收获 每3天" / "持续收获 每天" / "每隔3天持续产出"
    if "持续收获" not in text and "持续产出" not in text:
        return None
    if "每天" in text:
        return 1
    m = re.search(r"每\s*(\d+)\s*天", text)
    if m:
        return int(m.group(1))
    return None


def season_codes_from_text(text: str) -> set[int]:
    found: set[int] = set()
    if "春" in text:
        found.add(0)
    if "夏" in text:
        found.add(1)
    if "秋" in text:
        found.add(2)
    if "冬" in text:
        found.add(3)
    return found


def normalize_spaces(s: str) -> str:
    s = re.sub(r"\[\d+\]", "", s)
    s = re.sub(r"\s+", " ", s)
    return s.strip()


def first_match_int(text: str, pattern: str) -> int | None:
    m = re.search(pattern, text, flags=re.I | re.S)
    if not m:
        return None
    return int(m.group(1).replace(",", ""))


def parse_prices_from_cell(text: str) -> tuple[int, int, int, int] | None:
    # Usually 4 values (normal/silver/gold/iridium) inside a single cell.
    gold_nums = [int(m.group(1).replace(",", "")) for m in re.finditer(r"(\d[\d,]*)\s*金", text)]
    if len(gold_nums) < 4:
        return None

    def is_increasing(q: list[int]) -> bool:
        return q[0] < q[1] < q[2] < q[3]

    if len(gold_nums) == 4 and is_increasing(gold_nums):
        return (gold_nums[0], gold_nums[1], gold_nums[2], gold_nums[3])

    # If the cell contains more than 4 values, find the first plausible 4-number window.
    for i in range(0, len(gold_nums) - 3):
        window = gold_nums[i : i + 4]
        if is_increasing(window):
            return (window[0], window[1], window[2], window[3])

    return None


def parse_restores_from_cell(text: str) -> tuple[tuple[int, int, int, int], tuple[int, int, int, int], bool] | None:
    # Returns (energy_tuple, health_tuple, edible)
    if "不可食用" in text or re.search(r"Not\s*edible", text, re.I):
        return ((0, 0, 0, 0), (0, 0, 0, 0), False)

    nums = parse_ints(text)

    def parse_8(seq: list[int]) -> tuple[tuple[int, int, int, int], tuple[int, int, int, int]] | None:
        if len(seq) != 8:
            return None

        pairs = [(seq[i], seq[i + 1]) for i in range(0, 8, 2)]
        # Determine order by the first pair: in-game energy is always >= health.
        if all(a >= b for a, b in pairs):
            energy = (pairs[0][0], pairs[1][0], pairs[2][0], pairs[3][0])
            health = (pairs[0][1], pairs[1][1], pairs[2][1], pairs[3][1])
        elif all(a <= b for a, b in pairs):
            # Some text extraction can invert order; swap.
            energy = (pairs[0][1], pairs[1][1], pairs[2][1], pairs[3][1])
            health = (pairs[0][0], pairs[1][0], pairs[2][0], pairs[3][0])
        else:
            return None

        # Qualities should be non-decreasing.
        if not (energy[0] <= energy[1] <= energy[2] <= energy[3]):
            return None
        if not (health[0] <= health[1] <= health[2] <= health[3]):
            return None

        return (energy, health)

    # Expected: 8 numbers = 4 pairs
    if len(nums) == 8:
        parsed = parse_8(nums)
        if parsed is not None:
            energy, health = parsed
            return (energy, health, True)

    # If there are extra numbers (e.g., artifacts from other columns), find an 8-number window.
    if len(nums) > 8:
        for i in range(0, len(nums) - 7):
            window = nums[i : i + 8]
            parsed = parse_8(window)
            if parsed is not None:
                energy, health = parsed
                return (energy, health, True)

    # Some pages may only show a single pair.
    if len(nums) == 2:
        energy = (nums[0], nums[0], nums[0], nums[0])
        health = (nums[1], nums[1], nums[1], nums[1])
        return (energy, health, True)

    return None


def parse_seed_buy_price_from_cell(text: str) -> int | None:
    # Prefer Pierre/杂货店 price when multiple vendors exist.
    if "免费" in text:
        return 0

    # Common vendors on zh wiki crop page.
    for pat in [
        r"杂货店\s*.*?(\d[\d,]*)\s*金",
        r"皮埃尔\s*.*?(\d[\d,]*)\s*金",
        r"绿洲\s*.*?(\d[\d,]*)\s*金",
        r"Joja\s*超市\s*.*?(\d[\d,]*)\s*金",
        r"乔家\s*超市\s*.*?(\d[\d,]*)\s*金",
        r"复活节\s*.*?(\d[\d,]*)\s*金",
    ]:
        v = first_match_int(text, pat)
        if v is not None:
            return v

    # If there's exactly one unambiguous gold value, accept it.
    gold_nums = [int(m.group(1).replace(",", "")) for m in re.finditer(r"(\d[\d,]*)\s*金", text)]
    if len(gold_nums) == 1:
        return gold_nums[0]

    return None


def fetch_html(url: str) -> str:
    resp = requests.get(
        url,
        headers={
            "User-Agent": "stardewcraft-dev/1.0 (+https://example.invalid)",
            "Accept-Language": "zh-CN,zh;q=0.9,en;q=0.6",
        },
        timeout=30,
    )
    resp.raise_for_status()
    return resp.text


def read_existing_slugs(path: Path) -> tuple[list[str], dict[str, list[str]]]:
    # Returns: order, old_fields_by_slug
    order: list[str] = []
    old: dict[str, list[str]] = {}
    for raw in path.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        parts = line.split("|")
        if len(parts) < 2:
            continue
        slug = parts[1].strip()
        if slug and slug not in old:
            order.append(slug)
            old[slug] = parts
    return order, old


def iter_crop_sections(soup: BeautifulSoup) -> Iterable[tuple[str, str, BeautifulSoup]]:
    # yields: (section_h2_text, crop_display_en_from_img_alt, h3_tag)
    h2 = None
    for tag in soup.select("h2, h3"):
        if tag.name == "h2":
            h2 = normalize_spaces(tag.get_text(" ", strip=True))
            continue
        if tag.name != "h3":
            continue
        if not h2:
            continue

        img = tag.find("img")
        if not img or not img.get("alt"):
            continue
        alt = img.get("alt")
        if not alt.endswith(".png"):
            continue
        display = alt[: -len(".png")]
        yield h2, display, tag


def parse_crop_from_h3(section: str, display_en: str, h3) -> tuple[str, str, str, str, int, bool, tuple[int, int, int, int], tuple[int, int, int, int], int | None]:
    slug = canonicalize_slug(slugify(display_en))

    # Chinese name: first link text in h3 (after image)
    cn_name = ""
    for a in h3.find_all("a"):
        href = a.get("href") or ""
        text = normalize_spaces(a.get_text(" ", strip=True))
        if not text:
            continue
        # Skip file/image links
        if "File:" in href or href.startswith("/File:"):
            continue
        cn_name = text
        break

    # Scan forward until next h2/h3: collect first paragraph and first matching table
    cn_desc = ""
    table = None
    for el in h3.next_elements:
        try:
            name = getattr(el, "name", None)
        except Exception:
            name = None

        # Stop at next section/crop
        if name in {"h2", "h3"} and el is not h3:
            break

        if cn_desc == "" and name == "p":
            cn_desc = normalize_spaces(el.get_text(" ", strip=True))

        if table is None and name == "table":
            t = normalize_spaces(el.get_text(" ", strip=True))
            if "售价" in t and ("恢复" in t or "能量" in t):
                table = el
                # do not break: still allow cn_desc earlier in the flow, but table is found

    if table is None:
        raise ValueError(f"No data table for {slug}")

    # The zh wiki crop table is wide (stages + seed + price + restore ...).
    # We must NOT scan the whole row for numbers because the seed price will pollute sell prices.
    # Instead, locate columns by header text and parse values from the correct cells.

    def expanded_cells(tr, tag: str) -> list[str]:
        out: list[str] = []
        for el in tr.find_all(tag):
            txt = normalize_spaces(el.get_text(" ", strip=True))
            try:
                colspan = int(el.get("colspan") or 1)
            except Exception:
                colspan = 1
            colspan = max(1, colspan)
            out.extend([txt] * colspan)
        return out

    prices: tuple[int, int, int, int] | None = None
    energy: tuple[int, int, int, int] | None = None
    health: tuple[int, int, int, int] | None = None
    edible = True
    seed_buy_price: int | None = None
    growth_days: int | None = None

    def is_seedish(text: str) -> bool:
        return any(
            k in text
            for k in [
                "杂货店",
                "皮埃尔",
                "绿洲",
                "Joja",
                "乔家",
                "旅行货车",
                "打造",
                "复活节",
                "种子",
                "Seeds",
                "Starter",
                "Bulb",
                "Shoot",
                "苗",
            ]
        )

    def is_daily_profit(text: str) -> bool:
        # Avoid accidentally parsing the "≈ X金/天" cells as sell prices.
        return "金/天" in text or "/天" in text or "≈" in text

    for tr in table.select("tr"):
        tds_expanded = expanded_cells(tr, "td")
        if not tds_expanded:
            continue

        # Growth phase row: contains multiple "X 天" and a total "共：Y 天"
        if growth_days is None:
            joined = normalize_spaces(tr.get_text(" ", strip=True))
            day_nums = [int(m.group(1)) for m in re.finditer(r"(\d+)\s*天", joined)]
            if len(day_nums) >= 4 and ("共" in joined or "总计" in joined):
                total = parse_total_days(joined)
                growth_days = total if total is not None else sum(day_nums)

        # Content-based extraction across the row's cells. This is more robust than relying on
        # column indices because the wiki tables can include colspan/rowspan and extra rows.
        for cell in tds_expanded:
            if not cell:
                continue

            if seed_buy_price is None and is_seedish(cell):
                seed_buy_price = parse_seed_buy_price_from_cell(cell)

            if prices is None and ("金" in cell) and (not is_daily_profit(cell)):
                if is_seedish(cell):
                    continue
                maybe_prices = parse_prices_from_cell(cell)
                if maybe_prices is not None:
                    prices = maybe_prices

            if energy is None or health is None:
                # Restore cells typically contain ONLY numbers (8 ints for 4 qualities) or "不可食用".
                # Avoid cells that represent growth days or prices.
                if ("金" not in cell) and ("天" not in cell) and (not is_daily_profit(cell)):
                    restore_parsed = parse_restores_from_cell(cell)
                    if restore_parsed is not None:
                        energy, health, edible = restore_parsed
                elif "不可食用" in cell:
                    restore_parsed = parse_restores_from_cell(cell)
                    if restore_parsed is not None:
                        energy, health, edible = restore_parsed

        if prices is not None and (not edible or (energy is not None and health is not None)):
            # We have enough; keep scanning only for growth_days/regrow.
            pass

    if growth_days is None:
        # fallback: sometimes appears in plain text within the table
        for tr in table.select("tr"):
            txt = normalize_spaces(tr.get_text(" ", strip=True))
            gd = parse_total_days(txt)
            if gd is not None:
                growth_days = gd
                break

    if growth_days is None:
        raise ValueError(f"Missing growth days for {slug}")

    if prices is None:
        raise ValueError(f"Sell prices not complete for {slug}")

    if not edible:
        energy = (0, 0, 0, 0)
        health = (0, 0, 0, 0)
    else:
        if energy is None or health is None:
            raise ValueError(f"Restore values not complete for {slug}")

    # Total days + regrow in following (usually next) row within same table
    regrow_days = None
    for tr in table.select("tr"):
        txt = normalize_spaces(tr.get_text(" ", strip=True))
        rd = parse_regrow_days(txt)
        if rd is not None:
            regrow_days = rd

    return (
        cn_name,
        slug,
        cn_desc,
        section,
        growth_days,
        edible,
        prices,
        energy,
        health,
        seed_buy_price,
        regrow_days,
    )


def primary_seasons_from_section(section: str) -> set[int]:
    if "春季作物" in section:
        return {0}
    if "夏季作物" in section:
        return {1}
    if "秋季作物" in section:
        return {2}
    if "冬季作物" in section:
        return {3}
    return set()


def main() -> int:
    out_path = Path(sys.argv[1]) if len(sys.argv) > 1 else Path(__file__).with_name("crop_data.txt")

    order, old = read_existing_slugs(out_path)

    html = fetch_html(WIKI_URL)
    # Avoid requiring lxml; html.parser is good enough for this page.
    soup = BeautifulSoup(html, "html.parser")

    parsed: dict[str, CropRow] = {}

    ignore = {
        "mixed_seeds",
        "mixed_flower_seeds",
        "spring_seeds",
        "summer_seeds",
        "fall_seeds",
        "winter_seeds",
    }

    for section, display_en, h3 in iter_crop_sections(soup):
        slug = canonicalize_slug(slugify(display_en))
        if slug in ignore:
            continue

        try:
            (
                cn_name,
                slug,
                cn_desc,
                _section,
                growth_days,
                edible,
                prices,
                energy,
                health,
                seed_buy_price,
                regrow_days,
            ) = parse_crop_from_h3(section, display_en, h3)
        except Exception as e:
            # Keep going, but make failures visible so we can fix parser/slug mapping.
            print(f"WARN: Failed to parse crop '{display_en}' in section '{section}': {e}")
            continue

        # Only update crops that exist in current crop_data.txt (keeps scope under control)
        if slug not in old:
            continue

        seasons = set()
        # section-based
        seasons |= primary_seasons_from_section(section)
        # description-based extra seasons (e.g. wheat, sunflower, corn, coffee)
        seasons |= season_codes_from_text(cn_desc)
        seasons_str = ",".join(str(x) for x in sorted(seasons)) if seasons else old[slug][4]

        old_parts = old[slug]
        # Preserve English description if already present (we are scraping zh page only here)
        en_desc = old_parts[2] if len(old_parts) > 2 else ""

        can_regrow = regrow_days is not None
        regrow = regrow_days or 0

        # Preserve seed buy price if ambiguous
        old_seed_buy = 0
        try:
            old_seed_buy = int(old_parts[-1])
        except Exception:
            old_seed_buy = 0

        seed_buy = seed_buy_price if seed_buy_price is not None else old_seed_buy

        parsed[slug] = CropRow(
            cn_name=cn_name or old_parts[0],
            slug=slug,
            en_desc=en_desc,
            cn_desc=cn_desc or (old_parts[3] if len(old_parts) > 3 else ""),
            seasons=seasons_str,
            growth_days=int(growth_days) if growth_days else int(old_parts[5]),
            edible=edible,
            price_n=prices[0],
            price_s=prices[1],
            price_g=prices[2],
            price_i=prices[3],
            energy_n=energy[0],
            energy_s=energy[1],
            energy_g=energy[2],
            energy_i=energy[3],
            health_n=health[0],
            health_s=health[1],
            health_g=health[2],
            health_i=health[3],
            can_regrow=can_regrow,
            regrow_days=regrow,
            seed_buy_price=seed_buy,
        )

    header = [
        "# 星露谷物语作物数据（来自 中文 Stardew Valley Wiki《农作物》页）",
        "# 格式:",
        "# 中文名|英文名|英文描述|中文描述|季节(0=春,1=夏,2=秋,3=冬)|生长天数|可食用|售价_普|售价_银|售价_金|售价_铱|能量_普|能量_银|能量_金|能量_铱|生命_普|生命_银|生命_金|生命_铱|能否再生长|再生长天数|种子购买价",
        "# 注意：售价/能量/生命均按品质逐项写死，不做倍数推断。",
        "",
    ]

    lines: list[str] = []
    # Keep original order from old file, but do not silently keep outdated rows.
    missing = [s for s in order if s not in parsed]
    if missing:
        print("ERROR: Some crops were not refreshed from wiki:")
        for s in missing:
            print(f" - {s}")
        print("Refusing to write mixed/incorrect crop_data.txt. Fix parser or slug mapping.")
        return 1

    for slug in order:
        lines.append(parsed[slug].to_line())

    out_path.write_text("\n".join(header + lines) + "\n", encoding="utf-8")
    print(f"Updated {out_path} ({len(parsed)}/{len(order)} crops refreshed)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
