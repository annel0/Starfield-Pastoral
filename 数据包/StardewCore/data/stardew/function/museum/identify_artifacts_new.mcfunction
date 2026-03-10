# stardew:museum/identify_artifacts
# 检查玩家是否已捐赠对应古物,并进行鉴定

# 标记是否有物品被鉴定
tag @s remove sd_identified_success

# 矮人卷轴I
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"dwarf_scroll_i_unknown"}] if score @s sd_donated matches 100.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"dwarf_scroll_i",display_name:"矮人卷轴I",cmd:7300}

# 矮人卷轴II
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"dwarf_scroll_ii_unknown"}] if score @s sd_donated matches 101.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"dwarf_scroll_ii",display_name:"矮人卷轴II",cmd:7302}

# 矮人卷轴III
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"dwarf_scroll_iii_unknown"}] if score @s sd_donated matches 102.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"dwarf_scroll_iii",display_name:"矮人卷轴III",cmd:7304}

# 矮人卷轴IV
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"dwarf_scroll_iv_unknown"}] if score @s sd_donated matches 103.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"dwarf_scroll_iv",display_name:"矮人卷轴IV",cmd:7306}

# 破损的双耳瓶
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"chipped_amphora_unknown"}] if score @s sd_donated matches 104.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"chipped_amphora",display_name:"破损的双耳瓶",cmd:7308}

# 箭头
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"arrowhead_unknown"}] if score @s sd_donated matches 105.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"arrowhead",display_name:"箭头",cmd:7310}

# 远古玩偶
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"ancient_doll_unknown"}] if score @s sd_donated matches 106.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"ancient_doll",display_name:"远古玩偶",cmd:7312}

# 精灵珠宝
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"elvish_jewelry_unknown"}] if score @s sd_donated matches 107.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"elvish_jewelry",display_name:"精灵珠宝",cmd:7314}

# 咀嚼棒
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"chewing_stick_unknown"}] if score @s sd_donated matches 108.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"chewing_stick",display_name:"咀嚼棒",cmd:7316}

# 装饰扇
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"ornamental_fan_unknown"}] if score @s sd_donated matches 109.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"ornamental_fan",display_name:"装饰扇",cmd:7318}

# 恐龙蛋
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"dinosaur_egg_unknown"}] if score @s sd_donated matches 110.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"dinosaur_egg",display_name:"恐龙蛋",cmd:7320}

# 稀有光盘
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"rare_disc_unknown"}] if score @s sd_donated matches 111.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"rare_disc",display_name:"稀有光盘",cmd:7322}

# 远古之剑
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"ancient_sword_unknown"}] if score @s sd_donated matches 112.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"ancient_sword",display_name:"远古之剑",cmd:7324}

# 生锈的汤匙
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"rusty_spoon_unknown"}] if score @s sd_donated matches 113.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"rusty_spoon",display_name:"生锈的汤匙",cmd:7326}

# 生锈的马刺
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"rusty_spur_unknown"}] if score @s sd_donated matches 114.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"rusty_spur",display_name:"生锈的马刺",cmd:7328}

# 生锈的齿轮
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"rusty_cog_unknown"}] if score @s sd_donated matches 115.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"rusty_cog",display_name:"生锈的齿轮",cmd:7330}

# 鸡雕像
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"chicken_statue_unknown"}] if score @s sd_donated matches 116.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"chicken_statue",display_name:"鸡雕像",cmd:7332}

# 远古种子
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"ancient_seed_unknown"}] if score @s sd_donated matches 117.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"ancient_seed",display_name:"远古种子",cmd:7334}

# 史前工具
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"prehistoric_tool_unknown"}] if score @s sd_donated matches 118.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_tool",display_name:"史前工具",cmd:7336}

# 干海星
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"dried_starfish_unknown"}] if score @s sd_donated matches 119.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"dried_starfish",display_name:"干海星",cmd:7338}

# 锚
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"anchor_unknown"}] if score @s sd_donated matches 120.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"anchor",display_name:"锚",cmd:7340}

# 玻璃碎片
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"glass_shards_unknown"}] if score @s sd_donated matches 121.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"glass_shards",display_name:"玻璃碎片",cmd:7342}

# 骨笛
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"bone_flute_unknown"}] if score @s sd_donated matches 122.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"bone_flute",display_name:"骨笛",cmd:7344}

# 史前手斧
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"prehistoric_handaxe_unknown"}] if score @s sd_donated matches 123.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_handaxe",display_name:"史前手斧",cmd:7346}

# 矮人头盔
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"dwarvish_helm_unknown"}] if score @s sd_donated matches 124.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"dwarvish_helm",display_name:"矮人头盔",cmd:7348}

# 矮人小工具
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"dwarf_gadget_unknown"}] if score @s sd_donated matches 125.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"dwarf_gadget",display_name:"矮人小工具",cmd:7350}

# 远古鼓
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"ancient_drum_unknown"}] if score @s sd_donated matches 126.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"ancient_drum",display_name:"远古鼓",cmd:7352}

# 黄金面具
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"golden_mask_unknown"}] if score @s sd_donated matches 127.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"golden_mask",display_name:"黄金面具",cmd:7354}

# 黄金文物
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"golden_relic_unknown"}] if score @s sd_donated matches 128.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"golden_relic",display_name:"黄金文物",cmd:7356}

# 奇怪的玩偶(绿)
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"strange_doll_green_unknown"}] if score @s sd_donated matches 129.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"strange_doll_green",display_name:"奇怪的玩偶(绿)",cmd:7358}

# 奇怪的玩偶(黄)
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"strange_doll_yellow_unknown"}] if score @s sd_donated matches 130.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"strange_doll_yellow",display_name:"奇怪的玩偶(黄)",cmd:7360}

# 史前肩胛骨
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"prehistoric_scapula_unknown"}] if score @s sd_donated matches 131.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_scapula",display_name:"史前肩胛骨",cmd:7362}

# 史前胫骨
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"prehistoric_tibia_unknown"}] if score @s sd_donated matches 132.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_tibia",display_name:"史前胫骨",cmd:7364}

# 史前头骨
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"prehistoric_skull_unknown"}] if score @s sd_donated matches 133.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_skull",display_name:"史前头骨",cmd:7366}

# 骨手
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"skeletal_hand_unknown"}] if score @s sd_donated matches 134.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"skeletal_hand",display_name:"骨手",cmd:7368}

# 史前肋骨
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"prehistoric_rib_unknown"}] if score @s sd_donated matches 135.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_rib",display_name:"史前肋骨",cmd:7370}

# 史前椎骨
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"prehistoric_vertebra_unknown"}] if score @s sd_donated matches 136.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"prehistoric_vertebra",display_name:"史前椎骨",cmd:7372}

# 骨尾
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"skeletal_tail_unknown"}] if score @s sd_donated matches 137.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"skeletal_tail",display_name:"骨尾",cmd:7374}

# 鹦鹉螺化石
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"nautilus_fossil_unknown"}] if score @s sd_donated matches 138.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"nautilus_fossil",display_name:"鹦鹉螺化石",cmd:7376}

# 两栖动物化石
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"amphibian_fossil_unknown"}] if score @s sd_donated matches 139.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"amphibian_fossil",display_name:"两栖动物化石",cmd:7378}

# 棕榈化石
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"palm_fossil_unknown"}] if score @s sd_donated matches 140.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"palm_fossil",display_name:"棕榈化石",cmd:7380}

# 三叶虫
execute if items entity @s weapon.offhand paper[custom_data~{artifact_type:"trilobite_unknown"}] if score @s sd_donated matches 141.. run function stardew:museum/identify_item {item_type:"artifact",item_name:"trilobite",display_name:"三叶虫",cmd:7382}


# 如果没有成功鉴定任何物品，显示失败提示
execute unless entity @s[tag=sd_identified_success] run tellraw @s {"text":" 你还没有向博物馆捐赠过此类物品,无法鉴别!","color":"red"}

# 清理标签
tag @s remove sd_identified_success
