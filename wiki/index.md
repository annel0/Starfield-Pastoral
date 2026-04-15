---
layout: home
hero:
  name: StardewCraft
  text: 模组百科
  tagline: 在 Minecraft 中重现星露谷物语的一切 — 作物、矿洞、钓鱼、NPC 与更多
  actions:
    - theme: brand
      text: 快速开始
      link: /guide/getting-started
    - theme: alt
      text: 浏览百科
      link: /wiki/crops/
features:
  - icon: 🌾
    title: 作物系统
    details: 39 种作物完整还原，包含生长阶段、季节限制、再生机制与 Speed-Gro 加速。
    link: /wiki/crops/
    linkText: 查看作物
  - icon: 🌿
    title: 采集系统
    details: 16 种季节采集物 + 海滩贝类 + 蘑菇，按区域和概率自然刷新。
    link: /wiki/foraging/
    linkText: 查看采集
  - icon: ⛏️
    title: 矿洞与采矿
    details: 120 层矿洞探险，矿石、宝石、怪物掉落完全对标原版数据。
    link: /wiki/mining/
    linkText: 查看矿洞
  - icon: 🎣
    title: 钓鱼
    details: 完整钓鱼小游戏、鱼饵系统、浮标选择，按季节/天气/地点决定鱼种。
    link: /wiki/fishing/
    linkText: 查看钓鱼
  - icon: 👥
    title: NPC 与剧情
    details: 可互动的村民、好感度系统、日程安排，对话与事件逐步还原。
    link: /wiki/npc/
    linkText: 查看 NPC
  - icon: 📊
    title: 技能与职业
    details: 5 大技能 + 30 种职业分支，经验获取与升级效果 1:1 复刻。
    link: /wiki/skills/
    linkText: 查看技能
---

<script setup>
import HeroSection from './.vitepress/theme/components/HeroSection.vue'
</script>

<HeroSection />
