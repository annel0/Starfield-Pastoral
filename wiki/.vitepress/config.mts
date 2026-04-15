import { defineConfig } from 'vitepress'

export default defineConfig({
  lang: 'zh-CN',
  title: 'StardewCraft Wiki',
  description: 'StardewCraft 模组百科 — Stardew Valley × Minecraft',
  base: '/Starfield-Pastoral/',
  head: [
    ['link', { rel: 'preconnect', href: 'https://fonts.googleapis.com' }],
    ['link', { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossorigin: '' }],
    ['link', { href: 'https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Noto+Sans+SC:wght@400;500;700&display=swap', rel: 'stylesheet' }],
  ],

  themeConfig: {
    logo: '/logo.svg',
    siteTitle: 'StardewCraft',

    nav: [
      { text: '首页', link: '/' },
      { text: '指南', link: '/guide/getting-started' },
      {
        text: '百科',
        items: [
          { text: '作物', link: '/wiki/crops/' },
          { text: '采集', link: '/wiki/foraging/' },
          { text: '矿石与采矿', link: '/wiki/mining/' },
          { text: '钓鱼', link: '/wiki/fishing/' },
          { text: 'NPC', link: '/wiki/npc/' },
          { text: '技能与职业', link: '/wiki/skills/' },
          { text: '烹饪与合成', link: '/wiki/crafting/' },
        ],
      },
      { text: '更新日志', link: '/changelog' },
    ],

    sidebar: {
      '/guide/': [
        {
          text: '入门',
          items: [
            { text: '快速开始', link: '/guide/getting-started' },
            { text: '安装与兼容性', link: '/guide/installation' },
          ],
        },
      ],
      '/wiki/crops/': [
        {
          text: '作物总览',
          items: [
            { text: '作物系统说明', link: '/wiki/crops/' },
            { text: '春季作物', link: '/wiki/crops/spring' },
            { text: '夏季作物', link: '/wiki/crops/summer' },
            { text: '秋季作物', link: '/wiki/crops/fall' },
            { text: '冬季作物', link: '/wiki/crops/winter' },
          ],
        },
      ],
      '/wiki/foraging/': [
        {
          text: '采集总览',
          items: [
            { text: '采集系统说明', link: '/wiki/foraging/' },
            { text: '春季采集', link: '/wiki/foraging/spring' },
            { text: '夏季采集', link: '/wiki/foraging/summer' },
            { text: '秋季采集', link: '/wiki/foraging/fall' },
            { text: '冬季采集', link: '/wiki/foraging/winter' },
          ],
        },
      ],
      '/wiki/mining/': [
        {
          text: '矿洞',
          items: [
            { text: '采矿总览', link: '/wiki/mining/' },
          ],
        },
      ],
      '/wiki/fishing/': [
        {
          text: '钓鱼',
          items: [
            { text: '钓鱼总览', link: '/wiki/fishing/' },
          ],
        },
      ],
      '/wiki/npc/': [
        {
          text: 'NPC',
          items: [
            { text: 'NPC 总览', link: '/wiki/npc/' },
          ],
        },
      ],
      '/wiki/skills/': [
        {
          text: '技能',
          items: [
            { text: '技能总览', link: '/wiki/skills/' },
          ],
        },
      ],
    },

    search: {
      provider: 'local',
      options: {
        translations: {
          button: { buttonText: '搜索', buttonAriaLabel: '搜索' },
          modal: {
            noResultsText: '没有找到相关内容',
            resetButtonTitle: '清除查询',
            footer: { selectText: '选择', navigateText: '切换', closeText: '关闭' },
          },
        },
      },
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/stardewcraft' },
    ],

    footer: {
      message: 'StardewCraft 是一个粉丝制作的 Minecraft 模组，与 ConcernedApe 无关。',
      copyright: '© 2024-2026 StardewCraft Team',
    },

    outline: { label: '本页目录', level: [2, 3] },
    docFooter: { prev: '上一篇', next: '下一篇' },
    lastUpdated: { text: '最后更新' },
    returnToTopLabel: '回到顶部',
    darkModeSwitchLabel: '外观',
    sidebarMenuLabel: '菜单',
  },
})
