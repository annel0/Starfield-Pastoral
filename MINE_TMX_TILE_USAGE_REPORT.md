# Mines TMX 用到的 Tile 统计报告

> 自动从 `.tmx` 的 `Back/Buildings/Front` 三层 CSV 数据统计出：每张模板实际用到的 tile gid、对应 tile id（gid-1）、以及 `Type=Stone` 的占比。
> 用途：告诉你原版到底有多少种地面/石头格，并指导你决定要做多少种方块贴图/变体。

## 1.tmx

- 尺寸：20x20
- Tilesheet：mine
- Back 层：非零格数 306，唯一 tile 种类 24
- Back 层 Type=Stone tile 种类：23
- Back 最常见 tileId（前 20）：
  - 138 x142 [Stone]
  - 77 x36 
  - 218 x34 [Stone]
  - 234 x14 [Stone]
  - 155 x11 [Stone]
  - 233 x8 [Stone]
  - 137 x8 [Stone]
  - 217 x7 [Stone]
  - 185 x6 [Stone]
  - 140 x5 [Stone]
  - 188 x4 [Stone]
  - 187 x4 [Stone]
  - 235 x4 [Stone]
  - 154 x3 [Stone]
  - 203 x3 [Stone]
  - 201 x3 [Stone]
  - 202 x3 [Stone]
  - 153 x2 [Stone]
  - 186 x2 [Stone]
  - 139 x2 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 169, 170, 171, 185, 186, 187, 188, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 10.tmx

- 尺寸：20x16
- Tilesheet：mine
- Back 层：非零格数 160，唯一 tile 种类 29
- Back 层 Type=Stone tile 种类：20
- Back 最常见 tileId（前 20）：
  - 138 x42 [Stone]
  - 218 x22 [Stone]
  - 33 x13 
  - 18 x12 
  - 234 x12 [Stone]
  - 35 x8 
  - 217 x6 [Stone]
  - 1 x5 
  - 233 x4 [Stone]
  - 17 x3 
  - 169 x3 [Stone]
  - 34 x3 
  - 3 x3 
  - 154 x2 [Stone]
  - 19 x2 
  - 140 x2 [Stone]
  - 235 x2 [Stone]
  - 185 x2 [Stone]
  - 202 x2 [Stone]
  - 2 x2 
- Back 的 Type=Stone tileId（最多 60 个）：
  - 138, 139, 140, 153, 154, 169, 185, 186, 187, 188, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 11.tmx

- 尺寸：45x30
- Tilesheet：mine
- Back 层：非零格数 883，唯一 tile 种类 35
- Back 层 Type=Stone tile 种类：28
- Back 最常见 tileId（前 20）：
  - 138 x256 [Stone]
  - 218 x87 [Stone]
  - 183 x75 [Dirt]
  - 165 x41 [Dirt]
  - 217 x33 [Stone]
  - 202 x30 [Stone]
  - 234 x29 [Stone]
  - 219 x29 [Stone]
  - 200 x19 [Stone]
  - 182 x18 [Stone]
  - 166 x18 [Stone]
  - 167 x17 [Stone]
  - 199 x17 [Stone]
  - 235 x16 [Stone]
  - 184 x16 [Stone]
  - 150 x15 
  - 168 x14 [Stone]
  - 152 x14 
  - 186 x14 [Stone]
  - 233 x13 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 138, 139, 153, 154, 166, 167, 168, 169, 170, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 12.tmx

- 尺寸：80x15
- Tilesheet：mine
- Back 层：非零格数 676，唯一 tile 种类 51
- Back 层 Type=Stone tile 种类：30
- Back 最常见 tileId（前 20）：
  - 138 x220 [Stone]
  - 218 x80 [Stone]
  - 234 x55 [Stone]
  - 210 x54 
  - 202 x50 [Stone]
  - 183 x17 [Dirt]
  - 233 x14 [Stone]
  - 33 x11 
  - 185 x10 [Stone]
  - 187 x10 [Stone]
  - 217 x9 [Stone]
  - 235 x9 [Stone]
  - 35 x9 
  - 169 x8 [Stone]
  - 201 x8 [Stone]
  - 203 x8 [Stone]
  - 2 x7 
  - 188 x7 [Stone]
  - 225 x7 
  - 18 x6 
- Back 的 Type=Stone tileId（最多 60 个）：
  - 138, 139, 140, 153, 154, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 120.tmx

- 尺寸：20x16
- Tilesheet：mine_lava
- Back 层：非零格数 160，唯一 tile 种类 28
- Back 层 Type=Stone tile 种类：19
- Back 最常见 tileId（前 20）：
  - 138 x41 [Stone]
  - 218 x23 [Stone]
  - 33 x13 
  - 18 x12 
  - 234 x10 [Stone]
  - 35 x8 
  - 217 x6 [Stone]
  - 1 x5 
  - 233 x5 [Stone]
  - 169 x3 [Stone]
  - 17 x3 
  - 34 x3 
  - 185 x3 [Stone]
  - 235 x3 [Stone]
  - 154 x2 [Stone]
  - 19 x2 
  - 140 x2 [Stone]
  - 186 x2 [Stone]
  - 202 x2 [Stone]
  - 201 x2 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 138, 139, 140, 154, 169, 185, 186, 187, 188, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 13.tmx

- 尺寸：35x35
- Tilesheet：mine
- Back 层：非零格数 803，唯一 tile 种类 37
- Back 层 Type=Stone tile 种类：30
- Back 最常见 tileId（前 20）：
  - 183 x201 [Dirt]
  - 165 x127 [Dirt]
  - 138 x113 [Stone]
  - 218 x50 [Stone]
  - 199 x20 [Stone]
  - 234 x18 [Stone]
  - 151 x18 
  - 182 x15 [Stone]
  - 167 x15 [Stone]
  - 217 x15 [Stone]
  - 202 x15 [Stone]
  - 152 x15 
  - 149 x15 
  - 168 x14 [Stone]
  - 184 x14 [Stone]
  - 150 x13 
  - 166 x11 [Stone]
  - 198 x11 [Stone]
  - 200 x9 [Stone]
  - 185 x8 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 138, 139, 140, 153, 154, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 14.tmx

- 尺寸：50x40
- Tilesheet：mine
- Back 层：非零格数 1371，唯一 tile 种类 45
- Back 层 Type=Stone tile 种类：30
- Back 最常见 tileId（前 20）：
  - 138 x618 [Stone]
  - 218 x141 [Stone]
  - 183 x84 [Dirt]
  - 234 x57 [Stone]
  - 137 x49 [Stone]
  - 202 x43 [Stone]
  - 217 x40 [Stone]
  - 219 x30 [Stone]
  - 235 x26 [Stone]
  - 201 x24 [Stone]
  - 233 x22 [Stone]
  - 188 x21 [Stone]
  - 186 x21 [Stone]
  - 203 x17 [Stone]
  - 185 x14 [Stone]
  - 199 x13 [Stone]
  - 167 x13 [Stone]
  - 187 x12 [Stone]
  - 169 x11 [Stone]
  - 166 x11 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 153, 154, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 15.tmx

- 尺寸：40x35
- Tilesheet：mine
- Back 层：非零格数 676，唯一 tile 种类 49
- Back 层 Type=Stone tile 种类：33
- Back 最常见 tileId（前 20）：
  - 138 x178 [Stone]
  - 218 x78 [Stone]
  - 183 x43 [Dirt]
  - 165 x32 [Dirt]
  - 202 x31 [Stone]
  - 234 x29 [Stone]
  - 217 x23 [Stone]
  - 155 x20 [Stone]
  - 233 x14 [Stone]
  - 219 x12 [Stone]
  - 137 x12 [Stone]
  - 235 x12 [Stone]
  - 185 x11 [Stone]
  - 167 x11 [Stone]
  - 188 x11 [Stone]
  - 201 x10 [Stone]
  - 186 x9 [Stone]
  - 184 x8 [Stone]
  - 18 x8 
  - 169 x8 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 172, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 16.tmx

- 尺寸：50x30
- Tilesheet：mine
- Back 层：非零格数 870，唯一 tile 种类 39
- Back 层 Type=Stone tile 种类：32
- Back 最常见 tileId（前 20）：
  - 138 x432 [Stone]
  - 218 x74 [Stone]
  - 137 x53 [Stone]
  - 155 x41 [Stone]
  - 234 x36 [Stone]
  - 202 x33 [Stone]
  - 217 x24 [Stone]
  - 165 x20 [Dirt]
  - 183 x15 [Dirt]
  - 201 x11 [Stone]
  - 203 x11 [Stone]
  - 233 x11 [Stone]
  - 219 x10 [Stone]
  - 235 x10 [Stone]
  - 186 x9 [Stone]
  - 187 x9 [Stone]
  - 185 x9 [Stone]
  - 188 x8 [Stone]
  - 167 x5 [Stone]
  - 169 x4 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 17.tmx

- 尺寸：40x50
- Tilesheet：mine
- Back 层：非零格数 990，唯一 tile 种类 42
- Back 层 Type=Stone tile 种类：33
- Back 最常见 tileId（前 20）：
  - 138 x394 [Stone]
  - 218 x112 [Stone]
  - 217 x45 [Stone]
  - 137 x37 [Stone]
  - 165 x34 [Dirt]
  - 234 x33 [Stone]
  - 155 x33 [Stone]
  - 183 x30 [Dirt]
  - 202 x21 [Stone]
  - 233 x19 [Stone]
  - 219 x18 [Stone]
  - 235 x15 [Stone]
  - 186 x14 [Stone]
  - 185 x14 [Stone]
  - 201 x13 [Stone]
  - 203 x13 [Stone]
  - 188 x13 [Stone]
  - 187 x12 [Stone]
  - 199 x12 [Stone]
  - 167 x11 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 172, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 18.tmx

- 尺寸：50x40
- Tilesheet：mine
- Back 层：非零格数 1219，唯一 tile 种类 54
- Back 层 Type=Stone tile 种类：32
- Back 最常见 tileId（前 20）：
  - 138 x524 [Stone]
  - 218 x129 [Stone]
  - 234 x57 [Stone]
  - 202 x43 [Stone]
  - 183 x37 [Dirt]
  - 217 x29 [Stone]
  - 137 x26 [Stone]
  - 210 x24 
  - 233 x23 [Stone]
  - 165 x23 [Dirt]
  - 219 x21 [Stone]
  - 185 x18 [Stone]
  - 203 x16 [Stone]
  - 209 x15 
  - 201 x15 [Stone]
  - 187 x15 [Stone]
  - 235 x14 [Stone]
  - 225 x12 
  - 188 x12 [Stone]
  - 228 x11 
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 166, 167, 168, 169, 170, 171, 172, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 19.tmx

- 尺寸：50x50
- Tilesheet：mine
- Back 层：非零格数 2500，唯一 tile 种类 34
- Back 层 Type=Stone tile 种类：22
- Back 最常见 tileId（前 20）：
  - 138 x1431 [Stone]
  - 218 x271 [Stone]
  - 202 x133 [Stone]
  - 234 x130 [Stone]
  - 217 x78 [Stone]
  - 219 x76 [Stone]
  - 137 x39 [Stone]
  - 233 x39 [Stone]
  - 257 x35 [Wood]
  - 203 x33 [Stone]
  - 235 x32 [Stone]
  - 201 x32 [Stone]
  - 155 x29 [Stone]
  - 185 x26 [Stone]
  - 187 x21 [Stone]
  - 188 x20 [Stone]
  - 186 x20 [Stone]
  - 169 x10 [Stone]
  - 273 x7 [Wood]
  - 241 x6 [Wood]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 140, 153, 154, 155, 169, 170, 171, 185, 186, 187, 188, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 2.tmx

- 尺寸：35x35
- Tilesheet：mine
- Back 层：非零格数 517，唯一 tile 种类 24
- Back 层 Type=Stone tile 种类：22
- Back 最常见 tileId（前 20）：
  - 138 x265 [Stone]
  - 218 x68 [Stone]
  - 155 x26 [Stone]
  - 234 x25 [Stone]
  - 217 x24 [Stone]
  - 77 x16 
  - 137 x10 [Stone]
  - 233 x9 [Stone]
  - 202 x9 [Stone]
  - 235 x8 [Stone]
  - 186 x7 [Stone]
  - 140 x7 [Stone]
  - 188 x6 [Stone]
  - 185 x6 [Stone]
  - 201 x5 [Stone]
  - 219 x5 [Stone]
  - 169 x5 [Stone]
  - 187 x4 [Stone]
  - 154 x3 [Stone]
  - 203 x3 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 140, 153, 154, 155, 169, 170, 171, 185, 186, 187, 188, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 20.tmx

- 尺寸：50x22
- Tilesheet：mine
- Back 层：非零格数 643，唯一 tile 种类 44
- Back 层 Type=Stone tile 种类：21
- Back 最常见 tileId（前 20）：
  - 138 x166 [Stone]
  - 276 x119 
  - 275 x49 
  - 218 x48 [Stone]
  - 234 x39 [Stone]
  - 247 x22 
  - 263 x20 
  - 33 x18 
  - 18 x16 
  - 233 x8 [Stone]
  - 277 x8 
  - 35 x8 
  - 155 x8 [Stone]
  - 34 x7 
  - 1 x7 
  - 140 x6 [Stone]
  - 19 x6 
  - 139 x5 [Stone]
  - 202 x5 [Stone]
  - 235 x5 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 138, 139, 140, 153, 154, 155, 169, 185, 186, 187, 188, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 21.tmx

- 尺寸：30x25
- Tilesheet：mine
- Back 层：非零格数 692，唯一 tile 种类 40
- Back 层 Type=Stone tile 种类：32
- Back 最常见 tileId（前 20）：
  - 77 x353 
  - 138 x121 [Stone]
  - 218 x38 [Stone]
  - 155 x20 [Stone]
  - 137 x19 [Stone]
  - 202 x18 [Stone]
  - 234 x16 [Stone]
  - 165 x13 [Dirt]
  - 217 x9 [Stone]
  - 183 x7 [Dirt]
  - 188 x6 [Stone]
  - 201 x6 [Stone]
  - 233 x5 [Stone]
  - 185 x5 [Stone]
  - 140 x4 [Stone]
  - 167 x3 [Stone]
  - 166 x3 [Stone]
  - 200 x3 [Stone]
  - 168 x3 [Stone]
  - 198 x3 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 22.tmx

- 尺寸：30x45
- Tilesheet：mine
- Back 层：非零格数 1274，唯一 tile 种类 24
- Back 层 Type=Stone tile 种类：23
- Back 最常见 tileId（前 20）：
  - 77 x691 
  - 138 x229 [Stone]
  - 218 x107 [Stone]
  - 155 x35 [Stone]
  - 234 x35 [Stone]
  - 217 x32 [Stone]
  - 202 x25 [Stone]
  - 137 x16 [Stone]
  - 233 x15 [Stone]
  - 185 x12 [Stone]
  - 203 x10 [Stone]
  - 219 x9 [Stone]
  - 187 x9 [Stone]
  - 201 x9 [Stone]
  - 235 x9 [Stone]
  - 154 x7 [Stone]
  - 188 x5 [Stone]
  - 169 x5 [Stone]
  - 186 x5 [Stone]
  - 140 x3 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 154, 155, 169, 170, 171, 185, 186, 187, 188, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 23.tmx

- 尺寸：40x40
- Tilesheet：mine
- Back 层：非零格数 1428，唯一 tile 种类 40
- Back 层 Type=Stone tile 种类：32
- Back 最常见 tileId（前 20）：
  - 77 x664 
  - 138 x225 [Stone]
  - 165 x97 [Dirt]
  - 218 x87 [Stone]
  - 183 x54 [Dirt]
  - 234 x38 [Stone]
  - 155 x36 [Stone]
  - 137 x33 [Stone]
  - 202 x21 [Stone]
  - 217 x17 [Stone]
  - 169 x13 [Stone]
  - 167 x11 [Stone]
  - 233 x10 [Stone]
  - 201 x9 [Stone]
  - 199 x9 [Stone]
  - 185 x8 [Stone]
  - 154 x7 [Stone]
  - 139 x7 [Stone]
  - 140 x7 [Stone]
  - 188 x6 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 24.tmx

- 尺寸：55x40
- Tilesheet：mine
- Back 层：非零格数 1022，唯一 tile 种类 54
- Back 层 Type=Stone tile 种类：31
- Back 最常见 tileId（前 20）：
  - 138 x363 [Stone]
  - 218 x137 [Stone]
  - 234 x56 [Stone]
  - 202 x54 [Stone]
  - 217 x46 [Stone]
  - 155 x33 [Stone]
  - 183 x32 [Dirt]
  - 257 x32 [Wood]
  - 137 x31 [Stone]
  - 235 x17 [Stone]
  - 201 x17 [Stone]
  - 233 x16 [Stone]
  - 188 x15 [Stone]
  - 140 x14 [Stone]
  - 186 x14 [Stone]
  - 185 x11 [Stone]
  - 203 x10 [Stone]
  - 187 x9 [Stone]
  - 219 x8 [Stone]
  - 154 x6 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 25.tmx

- 尺寸：45x25
- Tilesheet：mine
- Back 层：非零格数 569，唯一 tile 种类 45
- Back 层 Type=Stone tile 种类：31
- Back 最常见 tileId（前 20）：
  - 138 x238 [Stone]
  - 218 x49 [Stone]
  - 183 x30 [Dirt]
  - 137 x29 [Stone]
  - 234 x25 [Stone]
  - 202 x20 [Stone]
  - 155 x18 [Stone]
  - 217 x15 [Stone]
  - 233 x12 [Stone]
  - 185 x9 [Stone]
  - 169 x8 [Stone]
  - 187 x8 [Stone]
  - 201 x7 [Stone]
  - 203 x7 [Stone]
  - 199 x7 [Stone]
  - 235 x6 [Stone]
  - 186 x5 [Stone]
  - 35 x5 
  - 34 x5 
  - 139 x5 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 153, 155, 166, 167, 168, 169, 170, 171, 172, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 26.tmx

- 尺寸：40x35
- Tilesheet：mine
- Back 层：非零格数 685，唯一 tile 种类 44
- Back 层 Type=Stone tile 种类：32
- Back 最常见 tileId（前 20）：
  - 138 x294 [Stone]
  - 218 x78 [Stone]
  - 234 x38 [Stone]
  - 137 x37 [Stone]
  - 155 x31 [Stone]
  - 202 x22 [Stone]
  - 217 x20 [Stone]
  - 183 x17 [Dirt]
  - 233 x15 [Stone]
  - 185 x13 [Stone]
  - 187 x10 [Stone]
  - 140 x9 [Stone]
  - 235 x8 [Stone]
  - 169 x7 [Stone]
  - 201 x7 [Stone]
  - 203 x7 [Stone]
  - 219 x6 [Stone]
  - 188 x6 [Stone]
  - 139 x5 [Stone]
  - 186 x5 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 27.tmx

- 尺寸：55x25
- Tilesheet：mine
- Back 层：非零格数 1028，唯一 tile 种类 38
- Back 层 Type=Stone tile 种类：31
- Back 最常见 tileId（前 20）：
  - 138 x434 [Stone]
  - 218 x117 [Stone]
  - 202 x59 [Stone]
  - 234 x58 [Stone]
  - 183 x29 [Dirt]
  - 155 x27 [Stone]
  - 137 x23 [Stone]
  - 165 x22 [Dirt]
  - 219 x20 [Stone]
  - 217 x20 [Stone]
  - 233 x12 [Stone]
  - 167 x12 [Stone]
  - 200 x12 [Stone]
  - 199 x12 [Stone]
  - 182 x11 [Stone]
  - 166 x11 [Stone]
  - 235 x11 [Stone]
  - 203 x11 [Stone]
  - 185 x10 [Stone]
  - 168 x9 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 28.tmx

- 尺寸：55x50
- Tilesheet：mine
- Back 层：非零格数 1322，唯一 tile 种类 51
- Back 层 Type=Stone tile 种类：33
- Back 最常见 tileId（前 20）：
  - 138 x597 [Stone]
  - 218 x155 [Stone]
  - 234 x71 [Stone]
  - 202 x60 [Stone]
  - 137 x40 [Stone]
  - 217 x39 [Stone]
  - 155 x39 [Stone]
  - 233 x24 [Stone]
  - 201 x21 [Stone]
  - 183 x19 [Dirt]
  - 185 x19 [Stone]
  - 188 x17 [Stone]
  - 235 x16 [Stone]
  - 203 x16 [Stone]
  - 210 x15 
  - 209 x15 
  - 169 x14 [Stone]
  - 187 x14 [Stone]
  - 186 x13 [Stone]
  - 228 x9 
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 172, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 29.tmx

- 尺寸：55x50
- Tilesheet：mine
- Back 层：非零格数 994，唯一 tile 种类 49
- Back 层 Type=Stone tile 种类：32
- Back 最常见 tileId（前 20）：
  - 138 x402 [Stone]
  - 218 x176 [Stone]
  - 234 x70 [Stone]
  - 217 x38 [Stone]
  - 202 x33 [Stone]
  - 257 x27 [Wood]
  - 137 x20 [Stone]
  - 233 x17 [Stone]
  - 183 x16 [Dirt]
  - 235 x16 [Stone]
  - 155 x15 [Stone]
  - 186 x15 [Stone]
  - 188 x13 [Stone]
  - 219 x13 [Stone]
  - 185 x12 [Stone]
  - 201 x12 [Stone]
  - 169 x9 [Stone]
  - 203 x7 [Stone]
  - 187 x7 [Stone]
  - 154 x6 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 3.tmx

- 尺寸：35x35
- Tilesheet：mine
- Back 层：非零格数 546，唯一 tile 种类 34
- Back 层 Type=Stone tile 种类：23
- Back 最常见 tileId（前 20）：
  - 138 x283 [Stone]
  - 218 x60 [Stone]
  - 234 x23 [Stone]
  - 217 x18 [Stone]
  - 202 x17 [Stone]
  - 233 x14 [Stone]
  - 155 x14 [Stone]
  - 235 x12 [Stone]
  - 257 x9 [Wood]
  - 137 x8 [Stone]
  - 219 x8 [Stone]
  - 201 x8 [Stone]
  - 185 x8 [Stone]
  - 203 x7 [Stone]
  - 186 x6 [Stone]
  - 154 x6 [Stone]
  - 188 x5 [Stone]
  - 140 x5 [Stone]
  - 187 x5 [Stone]
  - 169 x4 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 169, 170, 171, 185, 186, 187, 188, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 31.tmx

- 尺寸：35x30
- Tilesheet：mine_dark
- Back 层：非零格数 715，唯一 tile 种类 32
- Back 层 Type=Stone tile 种类：27
- Back 最常见 tileId（前 20）：
  - 138 x291 [Stone]
  - 139 x75 [Stone]
  - 218 x61 [Stone]
  - 154 x45 [Stone]
  - 234 x30 [Stone]
  - 202 x25 [Stone]
  - 155 x23 [Stone]
  - 217 x18 [Stone]
  - 219 x17 [Stone]
  - 233 x12 [Stone]
  - 235 x10 [Stone]
  - 183 x10 [Dirt]
  - 201 x9 [Stone]
  - 203 x8 [Stone]
  - 185 x8 [Stone]
  - 170 x7 [Stone]
  - 186 x7 [Stone]
  - 188 x7 [Stone]
  - 168 x6 [Stone]
  - 187 x6 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 154, 155, 166, 167, 168, 169, 170, 182, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 32.tmx

- 尺寸：40x40
- Tilesheet：mine_dark
- Back 层：非零格数 829，唯一 tile 种类 35
- Back 层 Type=Stone tile 种类：28
- Back 最常见 tileId（前 20）：
  - 138 x328 [Stone]
  - 218 x126 [Stone]
  - 154 x66 [Stone]
  - 234 x48 [Stone]
  - 202 x46 [Stone]
  - 217 x44 [Stone]
  - 219 x16 [Stone]
  - 137 x14 [Stone]
  - 233 x11 [Stone]
  - 165 x10 [Dirt]
  - 235 x10 [Stone]
  - 183 x9 [Dirt]
  - 155 x9 [Stone]
  - 185 x9 [Stone]
  - 201 x8 [Stone]
  - 188 x8 [Stone]
  - 186 x8 [Stone]
  - 203 x7 [Stone]
  - 187 x7 [Stone]
  - 169 x6 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 153, 154, 155, 166, 167, 168, 169, 170, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 33.tmx

- 尺寸：30x30
- Tilesheet：mine_dark
- Back 层：非零格数 401，唯一 tile 种类 20
- Back 层 Type=Stone tile 种类：20
- Back 最常见 tileId（前 20）：
  - 138 x134 [Stone]
  - 154 x71 [Stone]
  - 218 x44 [Stone]
  - 234 x24 [Stone]
  - 202 x21 [Stone]
  - 155 x15 [Stone]
  - 217 x13 [Stone]
  - 137 x13 [Stone]
  - 169 x8 [Stone]
  - 219 x8 [Stone]
  - 233 x7 [Stone]
  - 170 x7 [Stone]
  - 185 x6 [Stone]
  - 139 x6 [Stone]
  - 235 x5 [Stone]
  - 201 x5 [Stone]
  - 203 x4 [Stone]
  - 187 x4 [Stone]
  - 186 x3 [Stone]
  - 188 x3 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 154, 155, 169, 170, 185, 186, 187, 188, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 34.tmx

- 尺寸：45x30
- Tilesheet：mine_dark
- Back 层：非零格数 669，唯一 tile 种类 33
- Back 层 Type=Stone tile 种类：28
- Back 最常见 tileId（前 20）：
  - 138 x227 [Stone]
  - 218 x106 [Stone]
  - 234 x48 [Stone]
  - 154 x42 [Stone]
  - 202 x37 [Stone]
  - 217 x30 [Stone]
  - 139 x24 [Stone]
  - 155 x20 [Stone]
  - 137 x17 [Stone]
  - 169 x12 [Stone]
  - 235 x12 [Stone]
  - 203 x10 [Stone]
  - 233 x9 [Stone]
  - 185 x9 [Stone]
  - 219 x8 [Stone]
  - 187 x8 [Stone]
  - 188 x7 [Stone]
  - 186 x7 [Stone]
  - 183 x6 [Dirt]
  - 201 x6 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 154, 155, 166, 167, 168, 169, 170, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 35.tmx

- 尺寸：35x20
- Tilesheet：mine_dark
- Back 层：非零格数 348，唯一 tile 种类 19
- Back 层 Type=Stone tile 种类：19
- Back 最常见 tileId（前 20）：
  - 138 x139 [Stone]
  - 218 x49 [Stone]
  - 154 x28 [Stone]
  - 234 x24 [Stone]
  - 217 x14 [Stone]
  - 202 x13 [Stone]
  - 137 x11 [Stone]
  - 219 x10 [Stone]
  - 155 x8 [Stone]
  - 233 x8 [Stone]
  - 235 x7 [Stone]
  - 170 x5 [Stone]
  - 169 x5 [Stone]
  - 185 x5 [Stone]
  - 203 x5 [Stone]
  - 186 x5 [Stone]
  - 188 x4 [Stone]
  - 201 x4 [Stone]
  - 187 x4 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 154, 155, 169, 170, 185, 186, 187, 188, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 36.tmx

- 尺寸：40x35
- Tilesheet：mine_dark
- Back 层：非零格数 803，唯一 tile 种类 20
- Back 层 Type=Stone tile 种类：20
- Back 最常见 tileId（前 20）：
  - 138 x362 [Stone]
  - 218 x87 [Stone]
  - 154 x58 [Stone]
  - 234 x45 [Stone]
  - 202 x36 [Stone]
  - 137 x31 [Stone]
  - 155 x30 [Stone]
  - 139 x24 [Stone]
  - 217 x19 [Stone]
  - 219 x16 [Stone]
  - 233 x13 [Stone]
  - 203 x12 [Stone]
  - 235 x12 [Stone]
  - 185 x10 [Stone]
  - 169 x9 [Stone]
  - 187 x9 [Stone]
  - 186 x8 [Stone]
  - 201 x8 [Stone]
  - 188 x7 [Stone]
  - 170 x7 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 154, 155, 169, 170, 185, 186, 187, 188, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 37.tmx

- 尺寸：50x25
- Tilesheet：mine_dark
- Back 层：非零格数 691，唯一 tile 种类 33
- Back 层 Type=Stone tile 种类：28
- Back 最常见 tileId（前 20）：
  - 138 x251 [Stone]
  - 218 x94 [Stone]
  - 154 x64 [Stone]
  - 234 x45 [Stone]
  - 202 x39 [Stone]
  - 217 x21 [Stone]
  - 155 x20 [Stone]
  - 139 x16 [Stone]
  - 169 x14 [Stone]
  - 137 x13 [Stone]
  - 219 x13 [Stone]
  - 235 x13 [Stone]
  - 201 x12 [Stone]
  - 233 x11 [Stone]
  - 188 x10 [Stone]
  - 186 x10 [Stone]
  - 185 x9 [Stone]
  - 165 x7 [Dirt]
  - 203 x5 [Stone]
  - 170 x3 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 154, 155, 166, 167, 168, 169, 170, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 38.tmx

- 尺寸：40x30
- Tilesheet：mine_dark
- Back 层：非零格数 712，唯一 tile 种类 41
- Back 层 Type=Stone tile 种类：28
- Back 最常见 tileId（前 20）：
  - 138 x291 [Stone]
  - 218 x77 [Stone]
  - 139 x48 [Stone]
  - 154 x34 [Stone]
  - 234 x29 [Stone]
  - 217 x22 [Stone]
  - 202 x20 [Stone]
  - 183 x18 [Dirt]
  - 155 x15 [Stone]
  - 137 x12 [Stone]
  - 169 x12 [Stone]
  - 233 x11 [Stone]
  - 235 x9 [Stone]
  - 201 x9 [Stone]
  - 185 x9 [Stone]
  - 186 x8 [Stone]
  - 188 x8 [Stone]
  - 170 x7 [Stone]
  - 225 x7 
  - 210 x7 
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 154, 155, 166, 167, 168, 169, 170, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 39.tmx

- 尺寸：20x60
- Tilesheet：mine_dark
- Back 层：非零格数 605，唯一 tile 种类 30
- Back 层 Type=Stone tile 种类：25
- Back 最常见 tileId（前 20）：
  - 138 x184 [Stone]
  - 218 x88 [Stone]
  - 154 x75 [Stone]
  - 217 x37 [Stone]
  - 234 x33 [Stone]
  - 137 x24 [Stone]
  - 155 x23 [Stone]
  - 202 x22 [Stone]
  - 233 x16 [Stone]
  - 185 x12 [Stone]
  - 219 x12 [Stone]
  - 187 x10 [Stone]
  - 203 x9 [Stone]
  - 201 x8 [Stone]
  - 235 x8 [Stone]
  - 183 x6 [Dirt]
  - 200 x4 [Stone]
  - 188 x4 [Stone]
  - 166 x4 [Stone]
  - 186 x4 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 154, 155, 166, 167, 168, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 4.tmx

- 尺寸：35x35
- Tilesheet：mine
- Back 层：非零格数 716，唯一 tile 种类 23
- Back 层 Type=Stone tile 种类：23
- Back 最常见 tileId（前 20）：
  - 138 x347 [Stone]
  - 218 x82 [Stone]
  - 137 x47 [Stone]
  - 234 x43 [Stone]
  - 217 x29 [Stone]
  - 202 x22 [Stone]
  - 155 x21 [Stone]
  - 233 x19 [Stone]
  - 203 x14 [Stone]
  - 219 x12 [Stone]
  - 187 x12 [Stone]
  - 185 x12 [Stone]
  - 235 x11 [Stone]
  - 201 x8 [Stone]
  - 186 x7 [Stone]
  - 188 x6 [Stone]
  - 169 x5 [Stone]
  - 154 x5 [Stone]
  - 153 x4 [Stone]
  - 140 x4 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 169, 170, 171, 185, 186, 187, 188, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 40.tmx

- 尺寸：40x31
- Tilesheet：mine_desert
- Back 层：非零格数 1200，唯一 tile 种类 62
- Back 层 Type=Stone tile 种类：31
- Back 最常见 tileId（前 20）：
  - 137 x670 [Stone]
  - 218 x101 [Stone]
  - 210 x48 
  - 18 x27 
  - 225 x23 
  - 234 x20 [Stone]
  - 17 x19 
  - 1 x19 
  - 183 x15 [Dirt]
  - 19 x14 
  - 217 x13 [Stone]
  - 34 x12 
  - 2 x12 
  - 35 x12 
  - 33 x11 
  - 155 x9 [Stone]
  - 257 x8 
  - 242 x7 
  - 184 x7 [Stone]
  - 274 x7 
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 172, 182, 184, 185, 186, 188, 198, 199, 200, 201, 202, 204, 217, 218, 219, 233, 234, 235

## 41.tmx

- 尺寸：40x40
- Tilesheet：mine
- Back 层：非零格数 1600，唯一 tile 种类 36
- Back 层 Type=Stone tile 种类：29
- Back 最常见 tileId（前 20）：
  - 165 x951 [Dirt]
  - 218 x93 [Stone]
  - 138 x90 [Stone]
  - 183 x64 [Dirt]
  - 167 x44 [Stone]
  - 234 x36 [Stone]
  - 199 x33 [Stone]
  - 184 x27 [Stone]
  - 152 x26 
  - 181 x24 [Dirt]
  - 149 x24 
  - 151 x23 
  - 150 x21 
  - 182 x18 [Stone]
  - 166 x16 [Stone]
  - 168 x13 [Stone]
  - 153 x13 [Stone]
  - 198 x10 [Stone]
  - 217 x9 [Stone]
  - 200 x7 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 204, 217, 218, 219, 233, 234, 235

## 42.tmx

- 尺寸：29x55
- Tilesheet：mine
- Back 层：非零格数 1595，唯一 tile 种类 35
- Back 层 Type=Stone tile 种类：28
- Back 最常见 tileId（前 20）：
  - 138 x823 [Stone]
  - 218 x127 [Stone]
  - 183 x117 [Dirt]
  - 137 x51 [Stone]
  - 165 x39 [Dirt]
  - 217 x36 [Stone]
  - 155 x30 [Stone]
  - 167 x30 [Stone]
  - 184 x29 [Stone]
  - 234 x27 [Stone]
  - 199 x25 [Stone]
  - 168 x24 [Stone]
  - 166 x21 [Stone]
  - 181 x21 [Dirt]
  - 153 x21 [Stone]
  - 200 x20 [Stone]
  - 182 x20 [Stone]
  - 198 x19 [Stone]
  - 154 x16 [Stone]
  - 151 x12 
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 204, 217, 218, 219, 233, 234, 235

## 43.tmx

- 尺寸：48x52
- Tilesheet：mine
- Back 层：非零格数 2496，唯一 tile 种类 44
- Back 层 Type=Stone tile 种类：29
- Back 最常见 tileId（前 20）：
  - 138 x1386 [Stone]
  - 183 x181 [Dirt]
  - 218 x138 [Stone]
  - 165 x105 [Dirt]
  - 234 x62 [Stone]
  - 137 x49 [Stone]
  - 167 x47 [Stone]
  - 155 x42 [Stone]
  - 217 x41 [Stone]
  - 168 x31 [Stone]
  - 199 x28 [Stone]
  - 184 x28 [Stone]
  - 166 x27 [Stone]
  - 181 x24 [Dirt]
  - 200 x24 [Stone]
  - 182 x23 [Stone]
  - 154 x22 [Stone]
  - 233 x20 [Stone]
  - 140 x20 [Stone]
  - 139 x19 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 204, 217, 218, 219, 233, 234, 235

## 44.tmx

- 尺寸：32x82
- Tilesheet：mine
- Back 层：非零格数 2560，唯一 tile 种类 48
- Back 层 Type=Stone tile 种类：28
- Back 最常见 tileId（前 20）：
  - 138 x1981 [Stone]
  - 218 x109 [Stone]
  - 209 x46 
  - 217 x42 [Stone]
  - 183 x32 [Dirt]
  - 234 x28 [Stone]
  - 228 x21 
  - 155 x14 [Stone]
  - 184 x14 [Stone]
  - 168 x14 [Stone]
  - 154 x13 [Stone]
  - 182 x12 [Stone]
  - 153 x12 [Stone]
  - 200 x12 [Stone]
  - 18 x11 
  - 167 x11 [Stone]
  - 33 x10 
  - 1 x9 
  - 198 x9 [Stone]
  - 166 x9 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 204, 217, 218, 219, 233, 234, 235

## 45.tmx

- 尺寸：46x42
- Tilesheet：mine
- Back 层：非零格数 1932，唯一 tile 种类 44
- Back 层 Type=Stone tile 种类：29
- Back 最常见 tileId（前 20）：
  - 138 x1004 [Stone]
  - 218 x161 [Stone]
  - 234 x87 [Stone]
  - 183 x86 [Dirt]
  - 202 x51 [Stone]
  - 155 x50 [Stone]
  - 217 x38 [Stone]
  - 167 x28 [Stone]
  - 219 x26 [Stone]
  - 137 x24 [Stone]
  - 139 x19 [Stone]
  - 235 x18 [Stone]
  - 184 x18 [Stone]
  - 168 x17 [Stone]
  - 233 x17 [Stone]
  - 182 x16 [Stone]
  - 201 x15 [Stone]
  - 169 x14 [Stone]
  - 35 x13 
  - 166 x13 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 46.tmx

- 尺寸：45x30
- Tilesheet：mine
- Back 层：非零格数 1350，唯一 tile 种类 20
- Back 层 Type=Stone tile 种类：20
- Back 最常见 tileId（前 20）：
  - 138 x961 [Stone]
  - 218 x140 [Stone]
  - 234 x54 [Stone]
  - 137 x31 [Stone]
  - 217 x22 [Stone]
  - 139 x20 [Stone]
  - 155 x17 [Stone]
  - 169 x16 [Stone]
  - 153 x15 [Stone]
  - 154 x14 [Stone]
  - 235 x12 [Stone]
  - 186 x8 [Stone]
  - 233 x8 [Stone]
  - 185 x7 [Stone]
  - 202 x5 [Stone]
  - 201 x5 [Stone]
  - 170 x5 [Stone]
  - 171 x5 [Stone]
  - 219 x4 [Stone]
  - 204 x1 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 153, 154, 155, 169, 170, 171, 185, 186, 201, 202, 204, 217, 218, 219, 233, 234, 235

## 47.tmx

- 尺寸：70x45
- Tilesheet：mine
- Back 层：非零格数 3150，唯一 tile 种类 50
- Back 层 Type=Stone tile 种类：30
- Back 最常见 tileId（前 20）：
  - 138 x1872 [Stone]
  - 218 x234 [Stone]
  - 183 x136 [Dirt]
  - 234 x102 [Stone]
  - 155 x73 [Stone]
  - 137 x53 [Stone]
  - 167 x50 [Stone]
  - 217 x48 [Stone]
  - 165 x40 [Dirt]
  - 184 x30 [Stone]
  - 153 x30 [Stone]
  - 199 x29 [Stone]
  - 235 x27 [Stone]
  - 169 x26 [Stone]
  - 166 x25 [Stone]
  - 233 x25 [Stone]
  - 168 x23 [Stone]
  - 182 x23 [Stone]
  - 154 x23 [Stone]
  - 139 x21 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 48.tmx

- 尺寸：80x25
- Tilesheet：mine
- Back 层：非零格数 2000，唯一 tile 种类 46
- Back 层 Type=Stone tile 种类：22
- Back 最常见 tileId（前 20）：
  - 138 x1251 [Stone]
  - 218 x150 [Stone]
  - 210 x104 
  - 234 x63 [Stone]
  - 18 x37 
  - 225 x34 
  - 33 x27 
  - 3 x24 
  - 35 x23 
  - 19 x21 
  - 1 x19 
  - 155 x18 [Stone]
  - 34 x17 
  - 2 x17 
  - 137 x13 [Stone]
  - 140 x13 [Stone]
  - 257 x11 [Wood]
  - 169 x11 [Stone]
  - 139 x11 [Stone]
  - 17 x10 
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 169, 170, 171, 185, 186, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 49.tmx

- 尺寸：35x35
- Tilesheet：mine
- Back 层：非零格数 1225，唯一 tile 种类 31
- Back 层 Type=Stone tile 种类：24
- Back 最常见 tileId（前 20）：
  - 138 x704 [Stone]
  - 183 x163 [Dirt]
  - 218 x64 [Stone]
  - 165 x52 [Dirt]
  - 167 x24 [Stone]
  - 184 x20 [Stone]
  - 181 x16 [Dirt]
  - 152 x15 
  - 151 x14 
  - 150 x13 
  - 234 x13 [Stone]
  - 182 x12 [Stone]
  - 149 x10 
  - 199 x9 [Stone]
  - 168 x8 [Stone]
  - 166 x8 [Stone]
  - 155 x7 [Stone]
  - 153 x7 [Stone]
  - 185 x7 [Stone]
  - 200 x6 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 217, 218, 233, 234, 235

## 5.tmx

- 尺寸：35x35
- Tilesheet：mine
- Back 层：非零格数 649，唯一 tile 种类 43
- Back 层 Type=Stone tile 种类：25
- Back 最常见 tileId（前 20）：
  - 138 x252 [Stone]
  - 218 x83 [Stone]
  - 234 x35 [Stone]
  - 217 x26 [Stone]
  - 202 x18 [Stone]
  - 155 x18 [Stone]
  - 18 x17 
  - 137 x14 [Stone]
  - 257 x12 [Wood]
  - 233 x11 [Stone]
  - 169 x10 [Stone]
  - 201 x10 [Stone]
  - 188 x10 [Stone]
  - 185 x8 [Stone]
  - 186 x8 [Stone]
  - 187 x8 [Stone]
  - 235 x8 [Stone]
  - 33 x7 
  - 140 x6 [Stone]
  - 139 x6 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 169, 170, 171, 172, 185, 186, 187, 188, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 50.tmx

- 尺寸：90x65
- Tilesheet：mine
- Back 层：非零格数 5850，唯一 tile 种类 55
- Back 层 Type=Stone tile 种类：30
- Back 最常见 tileId（前 20）：
  - 138 x3995 [Stone]
  - 218 x643 [Stone]
  - 234 x212 [Stone]
  - 183 x110 [Dirt]
  - 155 x98 [Stone]
  - 137 x63 [Stone]
  - 217 x57 [Stone]
  - 139 x49 [Stone]
  - 235 x42 [Stone]
  - 154 x41 [Stone]
  - 233 x40 [Stone]
  - 169 x37 [Stone]
  - 153 x37 [Stone]
  - 140 x36 [Stone]
  - 167 x34 [Stone]
  - 185 x34 [Stone]
  - 257 x26 [Wood]
  - 186 x25 [Stone]
  - 184 x21 [Stone]
  - 165 x18 [Dirt]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 51.tmx

- 尺寸：60x82
- Tilesheet：mine
- Back 层：非零格数 4920，唯一 tile 种类 55
- Back 层 Type=Stone tile 种类：30
- Back 最常见 tileId（前 20）：
  - 138 x3431 [Stone]
  - 218 x577 [Stone]
  - 234 x168 [Stone]
  - 155 x64 [Stone]
  - 183 x57 [Dirt]
  - 217 x51 [Stone]
  - 137 x51 [Stone]
  - 139 x42 [Stone]
  - 257 x40 [Wood]
  - 153 x39 [Stone]
  - 185 x28 [Stone]
  - 233 x27 [Stone]
  - 169 x26 [Stone]
  - 154 x24 [Stone]
  - 235 x21 [Stone]
  - 167 x19 [Stone]
  - 140 x19 [Stone]
  - 184 x17 [Stone]
  - 182 x16 [Stone]
  - 168 x15 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 188, 198, 199, 200, 201, 202, 204, 217, 218, 219, 233, 234, 235

## 52.tmx

- 尺寸：60x47
- Tilesheet：mine
- Back 层：非零格数 2820，唯一 tile 种类 36
- Back 层 Type=Stone tile 种类：29
- Back 最常见 tileId（前 20）：
  - 138 x1926 [Stone]
  - 218 x274 [Stone]
  - 234 x79 [Stone]
  - 165 x58 [Dirt]
  - 137 x55 [Stone]
  - 183 x35 [Dirt]
  - 153 x32 [Stone]
  - 139 x31 [Stone]
  - 217 x29 [Stone]
  - 140 x26 [Stone]
  - 169 x22 [Stone]
  - 167 x21 [Stone]
  - 233 x18 [Stone]
  - 184 x18 [Stone]
  - 185 x17 [Stone]
  - 154 x17 [Stone]
  - 198 x14 [Stone]
  - 168 x14 [Stone]
  - 235 x14 [Stone]
  - 199 x13 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 53.tmx

- 尺寸：37x39
- Tilesheet：mine
- Back 层：非零格数 937，唯一 tile 种类 20
- Back 层 Type=Stone tile 种类：20
- Back 最常见 tileId（前 20）：
  - 138 x386 [Stone]
  - 218 x179 [Stone]
  - 137 x53 [Stone]
  - 155 x50 [Stone]
  - 169 x38 [Stone]
  - 234 x33 [Stone]
  - 217 x28 [Stone]
  - 153 x18 [Stone]
  - 139 x17 [Stone]
  - 170 x16 [Stone]
  - 171 x16 [Stone]
  - 140 x15 [Stone]
  - 235 x14 [Stone]
  - 233 x14 [Stone]
  - 154 x13 [Stone]
  - 185 x12 [Stone]
  - 202 x11 [Stone]
  - 186 x9 [Stone]
  - 201 x8 [Stone]
  - 219 x7 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 169, 170, 171, 185, 186, 201, 202, 217, 218, 219, 233, 234, 235

## 54.tmx

- 尺寸：42x28
- Tilesheet：mine
- Back 层：非零格数 1176，唯一 tile 种类 35
- Back 层 Type=Stone tile 种类：29
- Back 最常见 tileId（前 20）：
  - 138 x703 [Stone]
  - 218 x163 [Stone]
  - 234 x44 [Stone]
  - 183 x23 [Dirt]
  - 155 x17 [Stone]
  - 137 x16 [Stone]
  - 139 x13 [Stone]
  - 217 x13 [Stone]
  - 235 x12 [Stone]
  - 165 x11 [Dirt]
  - 167 x11 [Stone]
  - 154 x11 [Stone]
  - 169 x11 [Stone]
  - 184 x11 [Stone]
  - 168 x10 [Stone]
  - 153 x10 [Stone]
  - 186 x10 [Stone]
  - 182 x9 [Stone]
  - 200 x8 [Stone]
  - 199 x8 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 204, 217, 218, 219, 233, 234, 235

## 55.tmx

- 尺寸：45x65
- Tilesheet：mine
- Back 层：非零格数 1565，唯一 tile 种类 51
- Back 层 Type=Stone tile 种类：31
- Back 最常见 tileId（前 20）：
  - 165 x289 [Dirt]
  - 218 x253 [Stone]
  - 138 x169 [Stone]
  - 183 x157 [Dirt]
  - 167 x53 [Stone]
  - 184 x44 [Stone]
  - 199 x41 [Stone]
  - 234 x40 [Stone]
  - 217 x36 [Stone]
  - 155 x29 [Stone]
  - 210 x29 
  - 168 x26 [Stone]
  - 151 x25 
  - 181 x25 [Dirt]
  - 182 x25 [Stone]
  - 152 x23 
  - 149 x20 
  - 150 x20 
  - 200 x19 [Stone]
  - 166 x19 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 56.tmx

- 尺寸：40x40
- Tilesheet：mine
- Back 层：非零格数 1600，唯一 tile 种类 36
- Back 层 Type=Stone tile 种类：29
- Back 最常见 tileId（前 20）：
  - 138 x1101 [Stone]
  - 218 x112 [Stone]
  - 183 x43 [Dirt]
  - 234 x41 [Stone]
  - 155 x29 [Stone]
  - 217 x23 [Stone]
  - 137 x21 [Stone]
  - 154 x19 [Stone]
  - 167 x19 [Stone]
  - 139 x15 [Stone]
  - 169 x14 [Stone]
  - 153 x14 [Stone]
  - 140 x13 [Stone]
  - 235 x11 [Stone]
  - 184 x9 [Stone]
  - 168 x9 [Stone]
  - 182 x8 [Stone]
  - 165 x8 [Dirt]
  - 199 x8 [Stone]
  - 198 x7 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 204, 217, 218, 219, 233, 234, 235

## 57.tmx

- 尺寸：70x30
- Tilesheet：mine
- Back 层：非零格数 2100，唯一 tile 种类 55
- Back 层 Type=Stone tile 种类：30
- Back 最常见 tileId（前 20）：
  - 138 x1263 [Stone]
  - 218 x216 [Stone]
  - 183 x82 [Dirt]
  - 234 x68 [Stone]
  - 155 x54 [Stone]
  - 137 x37 [Stone]
  - 217 x24 [Stone]
  - 167 x21 [Stone]
  - 139 x19 [Stone]
  - 235 x18 [Stone]
  - 184 x17 [Stone]
  - 165 x16 [Dirt]
  - 182 x16 [Stone]
  - 154 x16 [Stone]
  - 169 x16 [Stone]
  - 153 x16 [Stone]
  - 233 x16 [Stone]
  - 168 x14 [Stone]
  - 166 x14 [Stone]
  - 140 x12 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 58.tmx

- 尺寸：30x25
- Tilesheet：mine
- Back 层：非零格数 750，唯一 tile 种类 36
- Back 层 Type=Stone tile 种类：29
- Back 最常见 tileId（前 20）：
  - 138 x409 [Stone]
  - 218 x62 [Stone]
  - 183 x30 [Dirt]
  - 165 x30 [Dirt]
  - 234 x17 [Stone]
  - 167 x15 [Stone]
  - 184 x13 [Stone]
  - 199 x13 [Stone]
  - 198 x12 [Stone]
  - 182 x11 [Stone]
  - 217 x11 [Stone]
  - 200 x10 [Stone]
  - 166 x10 [Stone]
  - 155 x10 [Stone]
  - 137 x10 [Stone]
  - 168 x10 [Stone]
  - 181 x8 [Dirt]
  - 139 x7 [Stone]
  - 149 x7 
  - 169 x7 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 59.tmx

- 尺寸：30x45
- Tilesheet：mine
- Back 层：非零格数 1350，唯一 tile 种类 34
- Back 层 Type=Stone tile 种类：28
- Back 最常见 tileId（前 20）：
  - 138 x809 [Stone]
  - 218 x158 [Stone]
  - 234 x44 [Stone]
  - 183 x39 [Dirt]
  - 155 x29 [Stone]
  - 137 x23 [Stone]
  - 217 x22 [Stone]
  - 169 x16 [Stone]
  - 199 x16 [Stone]
  - 139 x15 [Stone]
  - 167 x14 [Stone]
  - 165 x14 [Dirt]
  - 154 x13 [Stone]
  - 184 x12 [Stone]
  - 140 x12 [Stone]
  - 235 x11 [Stone]
  - 153 x10 [Stone]
  - 166 x9 [Stone]
  - 182 x8 [Stone]
  - 186 x8 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 217, 218, 219, 233, 234, 235

## 6.tmx

- 尺寸：35x45
- Tilesheet：mine
- Back 层：非零格数 762，唯一 tile 种类 38
- Back 层 Type=Stone tile 种类：31
- Back 最常见 tileId（前 20）：
  - 138 x303 [Stone]
  - 218 x82 [Stone]
  - 217 x34 [Stone]
  - 234 x32 [Stone]
  - 202 x30 [Stone]
  - 137 x26 [Stone]
  - 155 x26 [Stone]
  - 165 x20 [Dirt]
  - 201 x18 [Stone]
  - 233 x16 [Stone]
  - 235 x16 [Stone]
  - 188 x16 [Stone]
  - 219 x15 [Stone]
  - 186 x14 [Stone]
  - 183 x14 [Dirt]
  - 203 x12 [Stone]
  - 166 x7 [Stone]
  - 200 x7 [Stone]
  - 185 x7 [Stone]
  - 187 x6 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 60.tmx

- 尺寸：60x60
- Tilesheet：mine
- Back 层：非零格数 3600，唯一 tile 种类 36
- Back 层 Type=Stone tile 种类：29
- Back 最常见 tileId（前 20）：
  - 138 x2061 [Stone]
  - 218 x483 [Stone]
  - 183 x115 [Dirt]
  - 234 x114 [Stone]
  - 155 x87 [Stone]
  - 137 x68 [Stone]
  - 217 x48 [Stone]
  - 184 x46 [Stone]
  - 235 x38 [Stone]
  - 167 x37 [Stone]
  - 165 x36 [Dirt]
  - 168 x32 [Stone]
  - 199 x32 [Stone]
  - 182 x31 [Stone]
  - 140 x27 [Stone]
  - 169 x26 [Stone]
  - 186 x25 [Stone]
  - 198 x25 [Stone]
  - 166 x25 [Stone]
  - 154 x24 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 198, 199, 200, 201, 202, 204, 217, 218, 219, 233, 234, 235

## 7.tmx

- 尺寸：45x35
- Tilesheet：mine
- Back 层：非零格数 825，唯一 tile 种类 42
- Back 层 Type=Stone tile 种类：31
- Back 最常见 tileId（前 20）：
  - 138 x358 [Stone]
  - 218 x111 [Stone]
  - 234 x55 [Stone]
  - 202 x36 [Stone]
  - 137 x28 [Stone]
  - 155 x28 [Stone]
  - 217 x24 [Stone]
  - 233 x19 [Stone]
  - 203 x17 [Stone]
  - 219 x14 [Stone]
  - 235 x14 [Stone]
  - 185 x14 [Stone]
  - 187 x13 [Stone]
  - 201 x11 [Stone]
  - 186 x10 [Stone]
  - 188 x10 [Stone]
  - 169 x9 [Stone]
  - 18 x8 
  - 154 x6 [Stone]
  - 165 x4 [Dirt]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 153, 154, 155, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 77377.tmx

- 尺寸：80x105
- Tilesheet：mine_quarryshaft
- Back 层：非零格数 3570，唯一 tile 种类 59
- Back 层 Type=Stone tile 种类：38
- Back 最常见 tileId（前 20）：
  - 138 x1323 [Stone]
  - 218 x417 [Stone]
  - 234 x212 [Stone]
  - 183 x165 [Dirt]
  - 217 x105 [Stone]
  - 202 x103 [Stone]
  - 18 x77 [Stone]
  - 155 x71 [Stone]
  - 153 x67 [Stone]
  - 170 x60 [Stone]
  - 140 x58 [Stone]
  - 219 x48 [Stone]
  - 137 x47 [Stone]
  - 154 x46 [Stone]
  - 77 x46 
  - 139 x43 [Stone]
  - 235 x37 [Stone]
  - 19 x36 [Stone]
  - 33 x34 [Stone]
  - 35 x33 
- Back 的 Type=Stone tileId（最多 60 个）：
  - 2, 17, 18, 19, 33, 34, 137, 138, 139, 140, 153, 154, 155, 166, 167, 168, 169, 170, 171, 172, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 217, 218, 219, 233, 234, 235

## 8.tmx

- 尺寸：45x35
- Tilesheet：mine
- Back 层：非零格数 751，唯一 tile 种类 45
- Back 层 Type=Stone tile 种类：29
- Back 最常见 tileId（前 20）：
  - 138 x330 [Stone]
  - 218 x76 [Stone]
  - 137 x43 [Stone]
  - 234 x40 [Stone]
  - 202 x36 [Stone]
  - 217 x20 [Stone]
  - 233 x20 [Stone]
  - 219 x18 [Stone]
  - 201 x16 [Stone]
  - 235 x14 [Stone]
  - 203 x12 [Stone]
  - 185 x12 [Stone]
  - 188 x10 [Stone]
  - 186 x10 [Stone]
  - 165 x9 [Dirt]
  - 139 x8 [Stone]
  - 187 x8 [Stone]
  - 154 x6 [Stone]
  - 183 x5 [Dirt]
  - 169 x5 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 140, 153, 154, 166, 167, 168, 169, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## 9.tmx

- 尺寸：45x35
- Tilesheet：mine
- Back 层：非零格数 920，唯一 tile 种类 46
- Back 层 Type=Stone tile 种类：30
- Back 最常见 tileId（前 20）：
  - 138 x428 [Stone]
  - 218 x80 [Stone]
  - 137 x68 [Stone]
  - 202 x39 [Stone]
  - 234 x37 [Stone]
  - 183 x29 [Dirt]
  - 235 x18 [Stone]
  - 201 x17 [Stone]
  - 217 x17 [Stone]
  - 165 x16 [Dirt]
  - 186 x16 [Stone]
  - 188 x14 [Stone]
  - 233 x12 [Stone]
  - 203 x10 [Stone]
  - 219 x10 [Stone]
  - 257 x9 [Wood]
  - 185 x8 [Stone]
  - 169 x8 [Stone]
  - 187 x7 [Stone]
  - 154 x5 [Stone]
- Back 的 Type=Stone tileId（最多 60 个）：
  - 137, 138, 139, 153, 154, 166, 167, 168, 169, 170, 171, 182, 184, 185, 186, 187, 188, 198, 199, 200, 201, 202, 203, 204, 217, 218, 219, 233, 234, 235

## Volcano_DwarfShop.tmx

- 尺寸：5x4
- Tilesheet：volcano_dungeon
- Back 层：非零格数 0，唯一 tile 种类 0
- Back 层 Type=Stone tile 种类：0
- Back 最常见 tileId（前 20）：


## Volcano_SetPieces_16.tmx

- 尺寸：64x48
- Tilesheet：volcano_dungeon
- Back 层：非零格数 707，唯一 tile 种类 22
- Back 层 Type=Stone tile 种类：0
- Back 最常见 tileId（前 20）：
  - 544 x441 
  - 555 x77 
  - 528 x63 
  - 571 x21 
  - 518 x17 
  - 560 x15 
  - 565 x14 
  - 564 x14 
  - 545 x9 
  - 517 x5 
  - 519 x5 
  - 534 x5 
  - 502 x5 
  - 438 x2 
  - 535 x2 
  - 1 x2 
  - 501 x2 
  - 561 x2 
  - 533 x2 
  - 503 x2 

## Volcano_SetPieces_3.tmx

- 尺寸：12x12
- Tilesheet：volcano_dungeon
- Back 层：非零格数 0，唯一 tile 种类 0
- Back 层 Type=Stone tile 种类：0
- Back 最常见 tileId（前 20）：


## Volcano_SetPieces_32.tmx

- 尺寸：64x96
- Tilesheet：volcano_dungeon
- Back 层：非零格数 4991，唯一 tile 种类 54
- Back 层 Type=Stone tile 种类：0
- Back 最常见 tileId（前 20）：
  - 544 x3367 
  - 528 x350 
  - 555 x177 
  - 1 x161 
  - 518 x110 
  - 564 x83 
  - 561 x82 
  - 568 x71 
  - 565 x60 
  - 13 x46 
  - 502 x36 
  - 560 x32 
  - 517 x32 
  - 534 x31 
  - 519 x30 
  - 496 x26 
  - 28 x26 
  - 45 x24 
  - 571 x21 
  - 30 x19 

## Volcano_SetPieces_4.tmx

- 尺寸：16x16
- Tilesheet：volcano_dungeon
- Back 层：非零格数 0，唯一 tile 种类 0
- Back 层 Type=Stone tile 种类：0
- Back 最常见 tileId（前 20）：


## Volcano_SetPieces_8.tmx

- 尺寸：48x24
- Tilesheet：volcano_dungeon
- Back 层：非零格数 50，唯一 tile 种类 8
- Back 层 Type=Stone tile 种类：0
- Back 最常见 tileId（前 20）：
  - 555 x22 
  - 571 x8 
  - 544 x5 
  - 560 x5 
  - 561 x4 
  - 528 x3 
  - 564 x2 
  - 565 x1 

## Volcano_Well.tmx

- 尺寸：6x4
- Tilesheet：volcano_dungeon
- Back 层：非零格数 18，唯一 tile 种类 10
- Back 层 Type=Stone tile 种类：0
- Back 最常见 tileId（前 20）：
  - 126 x4 
  - 158 x2 
  - 157 x2 
  - 141 x2 
  - 188 x2 
  - 142 x2 
  - 174 x1 
  - 175 x1 
  - 172 x1 
  - 173 x1 

## VolcanoTemplate.tmx

- 尺寸：16x16
- Tilesheet：volcano_dungeon
- Back 层：非零格数 256，唯一 tile 种类 7
- Back 层 Type=Stone tile 种类：0
- Back 最常见 tileId（前 20）：
  - 1 x219 
  - 2 x7 
  - 3 x6 
  - 124 x6 
  - 76 x6 
  - 92 x6 
  - 108 x6 


