# README - 对话系统说明文档

## 概述
这是一个模仿星露谷物语的NPC对话系统原型。

## 特性
- 悬浮对话框UI，使用text_display和item_display实体
- NPC头像显示
- 多行对话文本支持
- 点击继续/关闭对话
- 数据驱动的对话内容

## 使用方法

### 1. 初始化
在主init函数中添加：
```mcfunction
function stardew:dialogue/init
```

### 2. 在主循环中添加
在main.mcfunction中添加：
```mcfunction
# 对话系统tick
execute as @a[tag=in_dialogue] at @s run function stardew:dialogue/player_tick
function stardew:dialogue/interact_menu
```

### 3. 测试对话
使用命令测试：
```
/function stardew:dialogue/npcs/alex
/function stardew:dialogue/npcs/abigail
/function stardew:dialogue/npcs/lewis
```

## 文件结构
```
dialogue/
├── init.mcfunction              # 初始化
├── player_open.mcfunction       # 打开对话框
├── player_close.mcfunction      # 关闭对话框
├── player_tick.mcfunction       # 玩家tick循环
├── player_click.mcfunction      # 点击处理
├── ray.mcfunction              # 光线投射
├── ray_hit.mcfunction          # 光线命中
├── interact_menu.mcfunction    # 交互检测
├── menus/
│   └── show_dialogue.mcfunction # 显示对话框UI
├── animation/
│   └── show.mcfunction         # 显示动画
├── icons_click/
│   ├── continue.mcfunction     # 继续按钮
│   └── option_select.mcfunction # 选项选择
└── npcs/
    ├── alex.mcfunction         # Alex的对话
    ├── abigail.mcfunction      # Abigail的对话
    └── lewis.mcfunction        # Lewis的对话
```

## 数据格式

对话数据存储在 `stardew:dialogue current` storage中：
```json
{
  "npc": "NPC内部名称",
  "npc_display_name": "显示名称（JSON文本）",
  "dialogue_page": 0,
  "total_pages": 1,
  "text": [
    "对话第一行",
    "对话第二行",
    "对话第三行"
  ],
  "portrait": {
    "id": "minecraft:player_head",
    "count": 1,
    "components": {
      "minecraft:profile": {
        "name": "MHF_Steve"
      }
    }
  }
}
```

## TODO
- [ ] 实现对话分页系统
- [ ] 添加对话选项支持
- [ ] 实现更好的UI材质
- [ ] 添加NPC表情变化
- [ ] 实现对话条件系统
- [ ] 添加友好度影响
- [ ] 实现对话记忆系统

## 注意事项
- 对话框会固定在玩家视角前方
- 玩家在对话中会被标记为`in_dialogue`
- 需要在主循环中调用相关函数
- 目前使用的是玩家头颅作为NPC头像占位符
