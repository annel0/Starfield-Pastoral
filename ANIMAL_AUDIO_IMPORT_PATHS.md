# Animal / AnimalQuery 音频导入路径（原版ID对照）

把 `.ogg` 文件放到以下路径（相对项目根目录）。

## 1) AnimalQueryMenu 按钮与界面音

- 原版 `smallSelect`  
  文件路径：`src/main/resources/assets/stardewcraft/sounds/ui/small_select.ogg`  
  事件ID：`stardewcraft:small_select`

- 原版 `drumkit6`（生育开关）  
  文件路径：`src/main/resources/assets/stardewcraft/sounds/ui/drumkit6.ogg`  
  事件ID：`stardewcraft:drumkit6`

- 原版 `newRecipe`（售出确认第一段）  
  文件路径：`src/main/resources/assets/stardewcraft/sounds/ui/new_recipe.ogg`  
  事件ID：`stardewcraft:new_recipe`

- 原版 `money`（售出确认第二段）  
  文件路径：`src/main/resources/assets/stardewcraft/sounds/ui/money.ogg`  
  事件ID：`stardewcraft:money`

## 2) 动物被抚摸/叫声音

- 原版 `cluck`（白鸡/金鸡/虚空鸡，三变种）  
  文件路径：  
  - `src/main/resources/assets/stardewcraft/sounds/animals/cluck_1.ogg`  
  - `src/main/resources/assets/stardewcraft/sounds/animals/cluck_2.ogg`  
  - `src/main/resources/assets/stardewcraft/sounds/animals/cluck_3.ogg`  
  事件ID：`stardewcraft:cluck`

- 原版 `Duck`（鸭）  
  文件路径：`src/main/resources/assets/stardewcraft/sounds/animals/duck.ogg`  
  事件ID：`stardewcraft:duck`

- 原版 `rabbit`（兔）  
  文件路径：`src/main/resources/assets/stardewcraft/sounds/animals/rabbit.ogg`  
  事件ID：`stardewcraft:rabbit`

- 原版 `Ostrich`（鸵鸟）  
  文件路径：`src/main/resources/assets/stardewcraft/sounds/animals/ostrich.ogg`  
  事件ID：`stardewcraft:ostrich`

- 原版 `Dinosaur`（恐龙）  
  `FarmAnimals.json` 中 `Sound: null`，原版无对应叫声ID。

## 3) 已接线代码

- 注册：`src/main/java/com/stardew/craft/sound/ModSounds.java`
- 资源映射：`src/main/resources/assets/stardewcraft/sounds.json`
- 抚摸触发：`src/main/java/com/stardew/craft/entity/animal/BaseCoopAnimalEntity.java`

> 注：`sounds.json` 已按上述事件ID配置完成，你只需要把同名 `.ogg` 放到对应目录。
