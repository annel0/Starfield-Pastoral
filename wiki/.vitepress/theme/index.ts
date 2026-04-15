import DefaultTheme from 'vitepress/theme'
import type { Theme } from 'vitepress'
import './custom.css'
import ItemCard from './components/ItemCard.vue'
import InfoBox from './components/InfoBox.vue'
import HeroSection from './components/HeroSection.vue'
import CropTable from './components/CropTable.vue'

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.component('ItemCard', ItemCard)
    app.component('InfoBox', InfoBox)
    app.component('HeroSection', HeroSection)
    app.component('CropTable', CropTable)
  },
} satisfies Theme
