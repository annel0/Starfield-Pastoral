# 初始化Pierre商店数据到# 第2页
data modify storage stardew:shop pierre.spring[2] append value {item_id:"garlic_seeds",loot_table:"stardew:items/seeds/crop_garlic",price:40,display_name:'{"text":"大蒜种子","color":"#853605"}'}
data modify storage stardew:shop pierre.spring[2] append value {item_id:"rice_shoots",loot_table:"stardew:items/seeds/crop_rice",price:40,display_name:'{"text":"水稻种子","color":"#853605"}'}
data modify storage stardew:shop pierre.spring[2] append value {item_id:"strawberry_seeds",loot_table:"stardew:items/seeds/crop_strawberry",price:100,display_name:'{"text":"草莓种子","color":"#853605"}'}

# 初始化夏季商品 (4页, 索引0-3)e
# 使用计分板变量: sd_shop_season (1=春 2=夏 3=秋 4=冬), sd_shop_page (当前页码)

# 清空storage
data remove storage stardew:shop pierre

# 初始化春季商品 (7页, 索引0-6: 3页种子+4页全年商品)
# 第0页 - 使用set value创建数组
data modify storage stardew:shop pierre.spring set value [[],[],[],[],[],[],[]]
data modify storage stardew:shop pierre.spring[0] append value {item_id:"parsnip_seeds",loot_table:"stardew:items/seeds/crop_parsnip",price:20,display_name:'{"text":"防风草种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.spring[0] append value {item_id:"green_bean_starter",loot_table:"stardew:items/seeds/crop_green_bean",price:60,display_name:'{"text":"绿豆种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.spring[0] append value {item_id:"cauliflower_seeds",loot_table:"stardew:items/seeds/crop_cauliflower",price:80,display_name:'{"text":"花椰菜种子","color":"#853605","bold":true}'}
# 第1页
data modify storage stardew:shop pierre.spring[1] append value {item_id:"potato_seeds",loot_table:"stardew:items/seeds/crop_potato",price:50,display_name:'{"text":"土豆种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.spring[1] append value {item_id:"tulip_bulb",loot_table:"stardew:items/seeds/crop_tulip",price:20,display_name:'{"text":"郁金香球茎","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.spring[1] append value {item_id:"kale_seeds",loot_table:"stardew:items/seeds/crop_kale",price:70,display_name:'{"text":"甘蓝种子","color":"#853605","bold":true}'}
# 第2页
data modify storage stardew:shop pierre.spring[2] append value {item_id:"jazz_seeds",loot_table:"stardew:items/seeds/crop_blue_jazz",price:30,display_name:'{"text":"爵士蓝种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.spring[2] append value {item_id:"garlic_seeds",loot_table:"stardew:items/seeds/crop_garlic",price:40,display_name:'{"text":"大蒜种子","color":"#853605","bold":true}'}

# 全年商品 - 第3页
data modify storage stardew:shop pierre.spring append value []
data modify storage stardew:shop pierre.spring[3] append value {item_id:"grass_starter",loot_table:"stardew:items/seeds/grass_starter",price:100,display_name:'{"text":"草籽","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.spring[3] append value {item_id:"sugar",loot_table:"stardew:items/cooking/sugar",price:100,display_name:'{"text":"糖","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.spring[3] append value {item_id:"wheat_flour",loot_table:"stardew:items/cooking/wheat_flour",price:100,display_name:'{"text":"小麦粉","color":"#853605","bold":true}'}
# 全年商品 - 第4页
data modify storage stardew:shop pierre.spring[4] append value {item_id:"rice",loot_table:"stardew:items/cooking/rice",price:200,display_name:'{"text":"大米","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.spring[4] append value {item_id:"oil",loot_table:"stardew:items/cooking/oil",price:200,display_name:'{"text":"油","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.spring[4] append value {item_id:"vinegar",loot_table:"stardew:items/cooking/vinegar",price:200,display_name:'{"text":"醋","color":"#853605","bold":true}'}
# 肥料 - 第5页
data modify storage stardew:shop pierre.spring[5] append value {item_id:"basic_fertilizer",loot_table:"stardew:items/fertilizer/basic_fertilizer",price:100,display_name:'{"text":"基础肥料","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.spring[5] append value {item_id:"quality_fertilizer",loot_table:"stardew:items/fertilizer/quality_fertilizer",price:150,display_name:'{"text":"高级肥料","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.spring[5] append value {item_id:"basic_retaining_soil",loot_table:"stardew:items/fertilizer/basic_retaining_soil",price:100,display_name:'{"text":"基础保湿土壤","color":"#853605","bold":true}'}
# 肥料 - 第6页
data modify storage stardew:shop pierre.spring[6] append value {item_id:"quality_retaining_soil",loot_table:"stardew:items/fertilizer/quality_retaining_soil",price:150,display_name:'{"text":"高级保湿土壤","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.spring[6] append value {item_id:"speed_gro",loot_table:"stardew:items/fertilizer/speed_gro",price:100,display_name:'{"text":"生长激素","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.spring[6] append value {item_id:"deluxe_speed_gro",loot_table:"stardew:items/fertilizer/deluxe_speed_gro",price:150,display_name:'{"text":"高级生长激素","color":"#853605","bold":true}'}

# 初始化夏季商品 (8页, 索引0-7: 4页种子+4页全年商品)
data modify storage stardew:shop pierre.summer set value [[],[],[],[],[],[],[],[]]
# 第0页
data modify storage stardew:shop pierre.summer[0] append value {item_id:"melon_seeds",loot_table:"stardew:items/seeds/crop_melon",price:80,display_name:'{"text":"甜瓜种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[0] append value {item_id:"tomato_seeds",loot_table:"stardew:items/seeds/crop_tomato",price:50,display_name:'{"text":"番茄种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[0] append value {item_id:"blueberry_seeds",loot_table:"stardew:items/seeds/crop_blueberry",price:80,display_name:'{"text":"蓝莓种子","color":"#853605","bold":true}'}
# 第1页
data modify storage stardew:shop pierre.summer[1] append value {item_id:"pepper_seeds",loot_table:"stardew:items/seeds/crop_hot_pepper",price:40,display_name:'{"text":"辣椒种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[1] append value {item_id:"wheat_seeds",loot_table:"stardew:items/seeds/crop_wheat",price:10,display_name:'{"text":"小麦种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[1] append value {item_id:"radish_seeds",loot_table:"stardew:items/seeds/crop_radish",price:40,display_name:'{"text":"萝卜种子","color":"#853605","bold":true}'}
# 第2页
data modify storage stardew:shop pierre.summer[2] append value {item_id:"poppy_seeds",loot_table:"stardew:items/seeds/crop_poppy",price:100,display_name:'{"text":"罂粟种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[2] append value {item_id:"spangle_seeds",loot_table:"stardew:items/seeds/crop_summer_spangle",price:50,display_name:'{"text":"夏季亮片种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[2] append value {item_id:"hops_starter",loot_table:"stardew:items/seeds/crop_hops",price:60,display_name:'{"text":"啤酒花种子","color":"#853605","bold":true}'}
# 第3页
data modify storage stardew:shop pierre.summer[3] append value {item_id:"corn_seeds",loot_table:"stardew:items/seeds/crop_corn",price:150,display_name:'{"text":"玉米种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[3] append value {item_id:"sunflower_seeds",loot_table:"stardew:items/seeds/crop_sunflower",price:200,display_name:'{"text":"向日葵种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[3] append value {item_id:"red_cabbage_seeds",loot_table:"stardew:items/seeds/crop_red_cabbage",price:100,display_name:'{"text":"红叶卷心菜种子","color":"#853605","bold":true}'}

# 全年商品 - 第4页
data modify storage stardew:shop pierre.summer append value []
data modify storage stardew:shop pierre.summer[4] append value {item_id:"grass_starter",loot_table:"stardew:items/seeds/grass_starter",price:100,display_name:'{"text":"草籽","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[4] append value {item_id:"sugar",loot_table:"stardew:items/cooking/sugar",price:100,display_name:'{"text":"糖","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[4] append value {item_id:"wheat_flour",loot_table:"stardew:items/cooking/wheat_flour",price:100,display_name:'{"text":"小麦粉","color":"#853605","bold":true}'}
# 全年商品 - 第5页
data modify storage stardew:shop pierre.summer append value []
data modify storage stardew:shop pierre.summer[5] append value {item_id:"rice",loot_table:"stardew:items/cooking/rice",price:200,display_name:'{"text":"大米","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[5] append value {item_id:"oil",loot_table:"stardew:items/cooking/oil",price:200,display_name:'{"text":"油","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[5] append value {item_id:"vinegar",loot_table:"stardew:items/cooking/vinegar",price:200,display_name:'{"text":"醋","color":"#853605","bold":true}'}
# 肥料 - 第6页
data modify storage stardew:shop pierre.summer[6] append value {item_id:"basic_fertilizer",loot_table:"stardew:items/fertilizer/basic_fertilizer",price:100,display_name:'{"text":"基础肥料","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[6] append value {item_id:"quality_fertilizer",loot_table:"stardew:items/fertilizer/quality_fertilizer",price:150,display_name:'{"text":"高级肥料","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[6] append value {item_id:"basic_retaining_soil",loot_table:"stardew:items/fertilizer/basic_retaining_soil",price:100,display_name:'{"text":"基础保湿土壤","color":"#853605","bold":true}'}
# 肥料 - 第7页
data modify storage stardew:shop pierre.summer[7] append value {item_id:"quality_retaining_soil",loot_table:"stardew:items/fertilizer/quality_retaining_soil",price:150,display_name:'{"text":"高级保湿土壤","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[7] append value {item_id:"speed_gro",loot_table:"stardew:items/fertilizer/speed_gro",price:100,display_name:'{"text":"生长激素","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.summer[7] append value {item_id:"deluxe_speed_gro",loot_table:"stardew:items/fertilizer/deluxe_speed_gro",price:150,display_name:'{"text":"高级生长激素","color":"#853605","bold":true}'}

# 初始化秋季商品 (8页, 索引0-7: 4页种子+4页全年商品)
data modify storage stardew:shop pierre.fall set value [[],[],[],[],[],[],[],[]]
# 第0页
data modify storage stardew:shop pierre.fall[0] append value {item_id:"eggplant_seeds",loot_table:"stardew:items/seeds/crop_eggplant",price:20,display_name:'{"text":"茄子种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[0] append value {item_id:"corn_seeds",loot_table:"stardew:items/seeds/crop_corn",price:150,display_name:'{"text":"玉米种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[0] append value {item_id:"pumpkin_seeds",loot_table:"stardew:items/seeds/crop_pumpkin",price:100,display_name:'{"text":"南瓜种子","color":"#853605","bold":true}'}
# 第1页
data modify storage stardew:shop pierre.fall[1] append value {item_id:"bok_choy_seeds",loot_table:"stardew:items/seeds/crop_bok_choy",price:50,display_name:'{"text":"小白菜种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[1] append value {item_id:"yam_seeds",loot_table:"stardew:items/seeds/crop_yam",price:60,display_name:'{"text":"山药种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[1] append value {item_id:"cranberry_seeds",loot_table:"stardew:items/seeds/crop_cranberries",price:240,display_name:'{"text":"蔓越莓种子","color":"#853605","bold":true}'}
# 第2页
data modify storage stardew:shop pierre.fall[2] append value {item_id:"sunflower_seeds",loot_table:"stardew:items/seeds/crop_sunflower",price:200,display_name:'{"text":"向日葵种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[2] append value {item_id:"fairy_seeds",loot_table:"stardew:items/seeds/crop_fairy_rose",price:200,display_name:'{"text":"仙女玫瑰种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[2] append value {item_id:"amaranth_seeds",loot_table:"stardew:items/seeds/crop_amaranth",price:70,display_name:'{"text":"苋菜种子","color":"#853605","bold":true}'}
# 第3页
data modify storage stardew:shop pierre.fall[3] append value {item_id:"grape_starter",loot_table:"stardew:items/seeds/crop_grape",price:60,display_name:'{"text":"葡萄种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[3] append value {item_id:"wheat_seeds",loot_table:"stardew:items/seeds/crop_wheat",price:10,display_name:'{"text":"小麦种子","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[3] append value {item_id:"artichoke_seeds",loot_table:"stardew:items/seeds/crop_artichoke",price:30,display_name:'{"text":"朝鲜蓟种子","color":"#853605","bold":true}'}

# 全年商品 - 第4页
data modify storage stardew:shop pierre.fall append value []
data modify storage stardew:shop pierre.fall[4] append value {item_id:"grass_starter",loot_table:"stardew:items/seeds/grass_starter",price:100,display_name:'{"text":"草籽","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[4] append value {item_id:"sugar",loot_table:"stardew:items/cooking/sugar",price:100,display_name:'{"text":"糖","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[4] append value {item_id:"wheat_flour",loot_table:"stardew:items/cooking/wheat_flour",price:100,display_name:'{"text":"小麦粉","color":"#853605","bold":true}'}
# 全年商品 - 第5页
data modify storage stardew:shop pierre.fall append value []
data modify storage stardew:shop pierre.fall[5] append value {item_id:"rice",loot_table:"stardew:items/cooking/rice",price:200,display_name:'{"text":"大米","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[5] append value {item_id:"oil",loot_table:"stardew:items/cooking/oil",price:200,display_name:'{"text":"油","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[5] append value {item_id:"vinegar",loot_table:"stardew:items/cooking/vinegar",price:200,display_name:'{"text":"醋","color":"#853605","bold":true}'}
# 肥料 - 第6页
data modify storage stardew:shop pierre.fall append value []
data modify storage stardew:shop pierre.fall[6] append value {item_id:"basic_fertilizer",loot_table:"stardew:items/fertilizer/basic_fertilizer",price:100,display_name:'{"text":"基础肥料","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[6] append value {item_id:"quality_fertilizer",loot_table:"stardew:items/fertilizer/quality_fertilizer",price:150,display_name:'{"text":"高级肥料","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[6] append value {item_id:"basic_retaining_soil",loot_table:"stardew:items/fertilizer/basic_retaining_soil",price:100,display_name:'{"text":"基础保湿土壤","color":"#853605","bold":true}'}
# 肥料 - 第7页
data modify storage stardew:shop pierre.fall[7] append value {item_id:"quality_retaining_soil",loot_table:"stardew:items/fertilizer/quality_retaining_soil",price:150,display_name:'{"text":"高级保湿土壤","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[7] append value {item_id:"speed_gro",loot_table:"stardew:items/fertilizer/speed_gro",price:100,display_name:'{"text":"生长激素","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.fall[7] append value {item_id:"deluxe_speed_gro",loot_table:"stardew:items/fertilizer/deluxe_speed_gro",price:150,display_name:'{"text":"高级生长激素","color":"#853605","bold":true}'}

# 初始化冬季商品 (5页, 索引0-4: 1页种子+4页全年商品)
data modify storage stardew:shop pierre.winter set value [[]]
data modify storage stardew:shop pierre.winter[0] append value {item_id:"winter_seeds",loot_table:"stardew:items/seeds/crop_winter_seeds",price:30,display_name:'{"text":"冬季种子","color":"#853605","bold":true}'}

# 全年商品 - 第1页
data modify storage stardew:shop pierre.winter append value []
data modify storage stardew:shop pierre.winter[1] append value {item_id:"grass_starter",loot_table:"stardew:items/seeds/grass_starter",price:100,display_name:'{"text":"草籽","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.winter[1] append value {item_id:"sugar",loot_table:"stardew:items/cooking/sugar",price:100,display_name:'{"text":"糖","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.winter[1] append value {item_id:"wheat_flour",loot_table:"stardew:items/cooking/wheat_flour",price:100,display_name:'{"text":"小麦粉","color":"#853605","bold":true}'}
# 全年商品 - 第2页
data modify storage stardew:shop pierre.winter append value []
data modify storage stardew:shop pierre.winter[2] append value {item_id:"rice",loot_table:"stardew:items/cooking/rice",price:200,display_name:'{"text":"大米","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.winter[2] append value {item_id:"oil",loot_table:"stardew:items/cooking/oil",price:200,display_name:'{"text":"油","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.winter[2] append value {item_id:"vinegar",loot_table:"stardew:items/cooking/vinegar",price:200,display_name:'{"text":"醋","color":"#853605","bold":true}'}
# 肥料 - 第3页
data modify storage stardew:shop pierre.winter append value []
data modify storage stardew:shop pierre.winter[3] append value {item_id:"basic_fertilizer",loot_table:"stardew:items/fertilizer/basic_fertilizer",price:100,display_name:'{"text":"基础肥料","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.winter[3] append value {item_id:"quality_fertilizer",loot_table:"stardew:items/fertilizer/quality_fertilizer",price:150,display_name:'{"text":"高级肥料","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.winter[3] append value {item_id:"basic_retaining_soil",loot_table:"stardew:items/fertilizer/basic_retaining_soil",price:100,display_name:'{"text":"基础保湿土壤","color":"#853605","bold":true}'}
# 肥料 - 第4页
data modify storage stardew:shop pierre.winter append value []
data modify storage stardew:shop pierre.winter[4] append value {item_id:"quality_retaining_soil",loot_table:"stardew:items/fertilizer/quality_retaining_soil",price:150,display_name:'{"text":"高级保湿土壤","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.winter[4] append value {item_id:"speed_gro",loot_table:"stardew:items/fertilizer/speed_gro",price:100,display_name:'{"text":"生长激素","color":"#853605","bold":true}'}
data modify storage stardew:shop pierre.winter[4] append value {item_id:"deluxe_speed_gro",loot_table:"stardew:items/fertilizer/deluxe_speed_gro",price:150,display_name:'{"text":"高级生长激素","color":"#853605","bold":true}'}

# 初始化完成
say Pierre商店数据已初始化