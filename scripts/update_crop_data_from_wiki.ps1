# ========================================================
# 浠庢槦闇茶胺Wiki鈥滃啘浣滅墿/Crops鈥濋〉闈㈣嚜鍔ㄧ敓鎴?crop_data.txt
# - 浠锋牸/鑳介噺/鐢熷懡锛氭寜 鏅€?閾?閲?閾?鍥涙。绮剧‘鍐欐锛堜笉鍋氫换浣曞€嶇巼鎺ㄧ畻锛?
# - 鍚屾椂鎶撳彇涓枃/鑻辨枃椤甸潰鐨勬弿杩版枃鏈紙鐢ㄤ簬 lang / tooltip锛?
# ========================================================

param(
    [string]$OutputPath = "E:\stardewcraft-template-1.21.1\scripts\crop_data.txt"
)

$zhUrl = "https://zh.stardewvalleywiki.com/%E5%86%9C%E4%BD%9C%E7%89%A9"
$enUrl = "https://stardewvalleywiki.com/Crops"

function Convert-ToSlug {
    param([string]$displayName)

    # 渚嬶細"Blue Jazz" -> blue_jazz
    #     "Qi Fruit"  -> qi_fruit
    $s = $displayName.Trim().ToLowerInvariant()
    $s = $s -replace "'", ""
    $s = $s -replace "\u2019", "" # 鈥?
    $s = $s -replace "[^a-z0-9\s_-]", ""
    $s = $s -replace "[\s-]+", "_"
    $s = $s -replace "_+", "_"
    return $s.Trim('_')
}

function Parse-IntList {
    param([string]$text)

    $matches = [regex]::Matches($text, "\d[\d,]*")
    $nums = @()
    foreach ($m in $matches) {
        $nums += [int]($m.Value -replace ",", "")
    }
    return $nums
}

function Get-SectionNameForH3 {
    param($h3)

    $node = $h3
    while ($node -ne $null) {
        $node = $node.previousSibling
        if ($node -eq $null) { break }
        try {
            if ($node.nodeType -eq 1 -and $node.tagName -eq "H2") {
                $spans = @($node.getElementsByTagName("span"))
                foreach ($s in $spans) {
                    try {
                        if ($s.className -match "mw-headline") {
                            return ($s.innerText.Trim())
                        }
                    } catch {}
                }
                return ($node.innerText.Trim())
            }
        } catch {}
    }

    return ""
}

function Find-NextElement {
    param($startNode, [string]$tagName)

    $node = $startNode
    while ($node -ne $null) {
        $node = $node.nextSibling
        if ($node -eq $null) { break }
        try {
            if ($node.nodeType -eq 1 -and $node.tagName -eq $tagName) {
                return $node
            }
        } catch {}
    }
    return $null
}

function Find-NextTableAfterH3 {
    param($h3)

    $node = $h3
    while ($true) {
        $node = $node.nextSibling
        if ($node -eq $null) { return $null }
        try {
            if ($node.nodeType -eq 1 -and $node.tagName -eq "TABLE") {
                # 纭繚鏄暟鎹〃锛堝惈鈥滃敭浠?Sell Price鈥濓級
                $t = "" + $node.innerText
                if ($t -match "鍞环" -or $t -match "Sell\s*Price") {
                    return $node
                }
            }
        } catch {}
    }
}

function Get-HeadlineTextFromH3 {
    param($h3)

    # 浼樺厛 mw-headline
    try {
        $spans = @($h3.getElementsByTagName("span"))
        foreach ($s in $spans) {
            try {
                if ($s.className -match "mw-headline") {
                    $t = ("" + $s.innerText).Trim()
                    if ($t) { return $t }
                }
            } catch {}
        }
    } catch {}

    return ("" + $h3.innerText).Trim()
}

function Get-DisplayNameFromH3ImageAlt {
    param($h3)

    try {
        $imgs = @($h3.getElementsByTagName("img"))
        foreach ($img in $imgs) {
            try {
                $alt = ("" + $img.alt).Trim()
                if ($alt -match "\.png$") {
                    return ($alt -replace "\.png$", "")
                }
            } catch {}
        }
    } catch {}

    return ""
}

function Parse-CropsPage {
    param(
        [string]$Url,
        [ValidateSet('zh','en')][string]$Lang
    )

    Write-Host "鎶撳彇: $Url" -ForegroundColor Cyan
    $resp = Invoke-WebRequest -Uri $Url
    $doc = $resp.ParsedHtml

    $result = @{}

    $h3s = @($doc.getElementsByTagName("h3"))
    foreach ($h3 in $h3s) {
        $sectionName = Get-SectionNameForH3 $h3
        if (-not $sectionName) { continue }

        # 鍙鐞嗕綔鐗╁垎缁?
        $isCropSection = $sectionName -match "浣滅墿" -or $sectionName -match "Crops"
        if (-not $isCropSection) { continue }

        $headline = Get-HeadlineTextFromH3 $h3
        $imgDisplay = Get-DisplayNameFromH3ImageAlt $h3

        if (-not $imgDisplay) {
            # 娌℃湁鍥剧墖鍚嶇殑 h3 澶氬崐涓嶆槸浣滅墿鏉＄洰
            continue
        }

        $englishDisplayName = $imgDisplay
        $slug = Convert-ToSlug $englishDisplayName

        # 杩囨护闈炰綔鐗╂潯鐩?
        $ignore = @(
            "mixed_seeds",
            "mixed_flower_seeds",
            "spring_seeds",
            "summer_seeds",
            "fall_seeds",
            "winter_seeds"
        )
        if ($ignore -contains $slug) { continue }

        # 鎻忚堪锛氬彇绱ч殢鍏跺悗鐨勭涓€涓钀?
        $p = Find-NextElement $h3 "P"
        $descText = ""
        if ($p -ne $null) {
            $descText = ("" + $p.innerText).Trim()
            # 娓呯悊鑴氭敞绛?
            $descText = $descText -replace "\[\d+\]", ""
            $descText = ($descText -replace "\s+", " ").Trim()
        }

        $table = Find-NextTableAfterH3 $h3
        if ($table -eq $null) { continue }

        # 鎵捐〃澶磋 -> 鍒楃储寮?
        $rows = @($table.getElementsByTagName("tr"))
        if ($rows.Count -lt 2) { continue }

        $headerRow = $null
        foreach ($r in $rows) {
            $ths = @($r.getElementsByTagName("th"))
            if ($ths.Count -gt 0) { $headerRow = $r; break }
        }
        if ($headerRow -eq $null) { continue }

        $headers = @($headerRow.getElementsByTagName("th"))
        $colSeed = -1
        $colSell = -1
        $colRestore = -1

        for ($i = 0; $i -lt $headers.Count; $i++) {
            $ht = ("" + $headers[$i].innerText).Trim()
            if ($ht -match "^绉嶅瓙$" -or $ht -match "Seeds") { $colSeed = $i }
            if ($ht -match "鍞环" -or $ht -match "Sell") { $colSell = $i }
            if ($ht -match "鎭㈠" -or $ht -match "Restore" -or $ht -match "鑳介噺") { $colRestore = $i }
        }

        if ($colSell -lt 0 -or $colRestore -lt 0) {
            continue
        }

        # 鎵剧涓€琛屾暟鎹紙鍚?td锛?
        $dataRow = $null
        foreach ($r in $rows) {
            $tds = @($r.getElementsByTagName("td"))
            if ($tds.Count -gt 0) {
                $dataRow = $r
                break
            }
        }
        if ($dataRow -eq $null) { continue }

        $cells = @($dataRow.getElementsByTagName("td"))
        if ($cells.Count -le [Math]::Max($colSell, $colRestore)) { continue }

        $seedPrice = 0
        if ($colSeed -ge 0 -and $colSeed -lt $cells.Count) {
            $seedText = ("" + $cells[$colSeed].innerText)
            $seedNums = Parse-IntList $seedText
            if ($seedNums.Count -gt 0) { $seedPrice = $seedNums[0] }
        }

        $sellNums = Parse-IntList ("" + $cells[$colSell].innerText)
        if ($sellNums.Count -lt 4) {
            # 鏈変簺鏉＄洰鍙兘鍑虹幇鍒殑鏁板瓧锛屽皾璇曞彧鍙栧墠4涓?
            if ($sellNums.Count -eq 0) { continue }
        }
        $priceN = $sellNums[0]
        $priceS = if ($sellNums.Count -gt 1) { $sellNums[1] } else { $sellNums[0] }
        $priceG = if ($sellNums.Count -gt 2) { $sellNums[2] } else { $sellNums[0] }
        $priceI = if ($sellNums.Count -gt 3) { $sellNums[3] } else { $sellNums[0] }

        $restoreText = ("" + $cells[$colRestore].innerText)
        $edible = $true
        $energyN = 0; $energyS = 0; $energyG = 0; $energyI = 0
        $healthN = 0; $healthS = 0; $healthG = 0; $healthI = 0

        if ($restoreText -match "涓嶅彲椋熺敤" -or $restoreText -match "Not\s*edible") {
            $edible = $false
        } else {
            $restoreNums = Parse-IntList $restoreText
            # 鏈熸湜 8 涓暟瀛楋細E/H * 4 鍝佽川
            if ($restoreNums.Count -ge 8) {
                $energyN = $restoreNums[0]; $healthN = $restoreNums[1]
                $energyS = $restoreNums[2]; $healthS = $restoreNums[3]
                $energyG = $restoreNums[4]; $healthG = $restoreNums[5]
                $energyI = $restoreNums[6]; $healthI = $restoreNums[7]
            } elseif ($restoreNums.Count -ge 2) {
                # 閫€鍖栨儏鍐碉細鍙嬁鏅€?
                $energyN = $restoreNums[0]; $healthN = $restoreNums[1]
            }
        }

        # 鐢熼暱/鍐嶇敓淇℃伅锛氫粠琛ㄦ牸鐨勫悗缁閲屾壘鈥滃叡锛歑 澶┾€濆拰鈥滄寔缁敹鑾?姣廥澶┾€?
        $growthDays = 0
        $regrowDays = 0
        foreach ($r in $rows) {
            $t = ("" + $r.innerText)
            if (-not $growthDays -and $t -match "(鍏眧鎬昏)\s*[:锛歖\s*(\d+)\s*澶?) {
                $growthDays = [int]$Matches[2]
            }
            if (-not $growthDays -and $t -match "(鍏眧鎬昏)\s*[:锛歖\s*(\d+)\s*-\s*(\d+)\s*澶?) {
                $growthDays = [int]$Matches[3] # 鍙栦笂鐣?
            }

            if ($t -match "鎸佺画鏀惰幏" -and $t -match "姣廫s*(\d+)\s*澶?) {
                $regrowDays = [int]$Matches[1]
            } elseif ($t -match "鎸佺画鏀惰幏" -and $t -match "姣忓ぉ") {
                $regrowDays = 1
            }
        }

        $canRegrow = $regrowDays -gt 0

        $result[$slug] = [PSCustomObject]@{
            EnglishDisplayName = $englishDisplayName
            EnglishName = $slug
            Headline = $headline
            Section = $sectionName
            Description = $descText
            SeedPrice = $seedPrice
            GrowthDays = $growthDays
            CanRegrow = $canRegrow
            RegrowDays = $regrowDays
            Edible = $edible
            PriceN = $priceN
            PriceS = $priceS
            PriceG = $priceG
            PriceI = $priceI
            EnergyN = $energyN
            EnergyS = $energyS
            EnergyG = $energyG
            EnergyI = $energyI
            HealthN = $healthN
            HealthS = $healthS
            HealthG = $healthG
            HealthI = $healthI
        }
    }

    return $result
}

function Infer-Seasons {
    param([string]$slug, [string]$zhSectionName)

    # 榛樿鎸夊垎缁?
    $default = switch -Regex ($zhSectionName) {
        "鏄? { "0"; break }
        "澶? { "1"; break }
        "绉? { "2"; break }
        "鍐? { "3"; break }
        default { "0,1,2,3" }
    }

    # 鐗规畩/璺ㄥ鑺備綔鐗╂寜Wiki瑙勫垯纭紪鐮侊紙鍥犱负鍗曢〉寰堥毦绋冲畾鎶藉彇鈥滃彲鍦╔鍜孻鐢熼暱鈥濈殑璇箟锛?
    $overrides = @{
        "corn" = "1,2"
        "sunflower" = "1,2"
        "wheat" = "1,2"
        "coffee_bean" = "0,1"
        "ancient_fruit" = "0,1,2"
        "sweet_gem_berry" = "2"
        "powdermelon" = "3"
        "qi_fruit" = "0,1,2,3"
        "fiber" = "0,1,2,3"
        "tea_leaves" = "0,1,2,3"
        "cactus_fruit" = "0,1,2,3"
        "taro_root" = "0,1,2,3"   # Wiki: 鍐滃満澶忓锛涘宀涘叏骞淬€傝繖閲屽彇鈥滃叏骞村彲绉嶁€濓紝閬垮厤璇垽
        "pineapple" = "0,1,2,3"  # 鍚屼笂
    }

    if ($overrides.ContainsKey($slug)) {
        return $overrides[$slug]
    }

    return $default
}

function Split-DescFlavor {
    param([string]$text, [ValidateSet('zh','en')][string]$lang)

    if ($null -eq $text) {
        $text = ""
    }
    $text = $text.Trim()
    if (-not $text) {
        return @{ Desc = ""; Flavor = "" }
    }

    if ($lang -eq 'zh') {
        # 鎸変腑鏂囧彞鍙峰垎涓ゅ彞
        $parts = $text -split "銆? | Where-Object { $_.Trim() -ne "" }
        if ($parts.Count -ge 2) {
            return @{ Desc = ($parts[0].Trim() + "銆?); Flavor = ($parts[1].Trim() + "銆?) }
        }
        return @{ Desc = ($parts[0].Trim() + "銆?); Flavor = "" }
    }

    # en: 鍙ョ偣
    $parts = $text -split "\." | Where-Object { $_.Trim() -ne "" }
    if ($parts.Count -ge 2) {
        return @{ Desc = ($parts[0].Trim() + "."); Flavor = ($parts[1].Trim() + ".") }
    }
    return @{ Desc = ($parts[0].Trim() + "."); Flavor = "" }
}

# 鎶撳彇骞惰В鏋?
$zh = Parse-CropsPage -Url $zhUrl -Lang zh
$en = Parse-CropsPage -Url $enUrl -Lang en

# 鍚堝苟
$crops = @()
foreach ($slug in ($zh.Keys | Sort-Object)) {
    $z = $zh[$slug]
    $e = $null
    if ($en.ContainsKey($slug)) { $e = $en[$slug] }

    $zhTitle = $z.Headline
    # zh 鐨?headline 褰㈠ "钃濈埖"锛宔n 鐨?headline 鍒欐槸鑻辨枃
    $chineseName = $zhTitle

    $seasonStr = Infer-Seasons -slug $slug -zhSectionName $z.Section

    $zhSplit = Split-DescFlavor -text $z.Description -lang zh
    $enSplit = if ($e -ne $null) { Split-DescFlavor -text $e.Description -lang en } else { @{ Desc = ""; Flavor = "" } }

    $crops += [PSCustomObject]@{
        ChineseName = $chineseName
        EnglishName = $slug
        Season = $seasonStr
        GrowthDays = $z.GrowthDays
        CanRegrow = [string]$z.CanRegrow
        RegrowDays = $z.RegrowDays
        SeedPrice = $z.SeedPrice
        Edible = [string]$z.Edible
        PriceN = $z.PriceN; PriceS = $z.PriceS; PriceG = $z.PriceG; PriceI = $z.PriceI
        EnergyN = $z.EnergyN; EnergyS = $z.EnergyS; EnergyG = $z.EnergyG; EnergyI = $z.EnergyI
        HealthN = $z.HealthN; HealthS = $z.HealthS; HealthG = $z.HealthG; HealthI = $z.HealthI
        EnglishDesc = $enSplit.Desc
        EnglishFlavor = $enSplit.Flavor
        ChineseDesc = $zhSplit.Desc
        ChineseFlavor = $zhSplit.Flavor
    }
}

# 杈撳嚭
$header = @(
"# 鏄熼湶璋蜂綔鐗╂暟鎹紙鑷姩浠?Stardew Valley Wiki 鐢熸垚锛?,
"# 鏍煎紡:",
"# 涓枃鍚峾鑻辨枃鍚峾瀛ｈ妭(0鏄?1澶?2绉?3鍐?閫楀彿鍒嗛殧)|鐢熼暱澶╂暟|鍙啀鐢?true/false)|鍐嶇敓澶╂暟|绉嶅瓙鍞环|鍙鐢?true/false)",
"# 鍞环N|鍞环S|鍞环G|鍞环I|鑳介噺N|鑳介噺S|鑳介噺G|鑳介噺I|鐢熷懡N|鐢熷懡S|鐢熷懡G|鐢熷懡I",
"# 鑻辨枃desc|鑻辨枃flavor|涓枃desc|涓枃flavor"
)

$lines = @()
$lines += $header
$lines += ""

foreach ($c in $crops) {
    $lines += (
        "$($c.ChineseName)|$($c.EnglishName)|$($c.Season)|$($c.GrowthDays)|$($c.CanRegrow)|$($c.RegrowDays)|$($c.SeedPrice)|$($c.Edible)|" +
        "$($c.PriceN)|$($c.PriceS)|$($c.PriceG)|$($c.PriceI)|" +
        "$($c.EnergyN)|$($c.EnergyS)|$($c.EnergyG)|$($c.EnergyI)|" +
        "$($c.HealthN)|$($c.HealthS)|$($c.HealthG)|$($c.HealthI)|" +
        "$($c.EnglishDesc)|$($c.EnglishFlavor)|$($c.ChineseDesc)|$($c.ChineseFlavor)"
    )
}

Set-Content -Path $OutputPath -Value $lines -Encoding UTF8
Write-Host "\n鉁?宸茬敓鎴? $OutputPath" -ForegroundColor Green
Write-Host "  浣滅墿鏁伴噺: $($crops.Count)" -ForegroundColor Green

