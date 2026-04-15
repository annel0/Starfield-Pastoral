<script setup lang="ts">
defineProps<{
  icon?: string
  img?: string
  seedImg?: string
  name: string
  nameEn?: string
  category?: string
  rarity?: 'common' | 'uncommon' | 'rare' | 'legendary'
  season?: 'spring' | 'summer' | 'fall' | 'winter' | 'multi'
}>()

const rarityClass: Record<string, string> = {
  common: 'sc-badge-green',
  uncommon: 'sc-badge-blue',
  rare: 'sc-badge-purple',
  legendary: 'sc-badge-amber',
}

const seasonClass: Record<string, string> = {
  spring: 'sc-badge-green',
  summer: 'sc-badge-amber',
  fall: 'sc-badge-purple',
  winter: 'sc-badge-blue',
  multi: 'sc-badge-blue',
}

const seasonLabel: Record<string, string> = {
  spring: '🌸 春季',
  summer: '☀️ 夏季',
  fall: '🍂 秋季',
  winter: '❄️ 冬季',
  multi: '🔄 跨季',
}
</script>

<template>
  <div class="item-card glass-card">
    <div class="item-card-header">
      <div v-if="img" class="item-card-img-wrap">
        <img :src="img" :alt="name" class="item-card-img" />
        <img v-if="seedImg" :src="seedImg" :alt="name + ' 种子'" class="item-card-seed-img" />
      </div>
      <span v-else-if="icon" class="item-card-icon">{{ icon }}</span>
      <div class="item-card-title-area">
        <div class="item-card-name">{{ name }}</div>
        <div v-if="nameEn" class="item-card-name-en">{{ nameEn }}</div>
        <div class="item-card-badges">
          <span v-if="season" :class="['sc-badge', seasonClass[season]]">{{ seasonLabel[season] }}</span>
          <span v-if="rarity" :class="['sc-badge', rarityClass[rarity] || 'sc-badge-green']">{{ rarity }}</span>
        </div>
      </div>
    </div>
    <div v-if="category" class="item-card-category">{{ category }}</div>
    <div class="item-card-body">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.item-card {
  padding: 24px;
  margin: 20px 0;
}

.item-card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.item-card-img-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.item-card-img {
  width: 48px;
  height: 48px;
  image-rendering: pixelated;
  image-rendering: crisp-edges;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.04);
  padding: 4px;
}

.item-card-seed-img {
  width: 32px;
  height: 32px;
  image-rendering: pixelated;
  image-rendering: crisp-edges;
  opacity: 0.7;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.03);
  padding: 3px;
}

.item-card-icon {
  font-size: 2.5rem;
  line-height: 1;
}

.item-card-title-area {
  flex: 1;
  min-width: 0;
}

.item-card-name {
  font-size: 1.25rem;
  font-weight: 700;
  margin-bottom: 2px;
}

.item-card-name-en {
  font-size: 0.8rem;
  color: var(--vp-c-text-3);
  margin-bottom: 6px;
  font-style: italic;
}

.item-card-badges {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.item-card-category {
  font-size: 0.8rem;
  color: var(--vp-c-text-3);
  text-transform: uppercase;
  letter-spacing: 0.06em;
  margin-bottom: 12px;
}

.item-card-body {
  font-size: 0.95rem;
  line-height: 1.7;
  color: var(--vp-c-text-2);
}

.item-card-body :deep(table) {
  width: 100%;
  margin: 8px 0 0;
}

.item-card-body :deep(td),
.item-card-body :deep(th) {
  padding: 6px 12px;
  font-size: 0.875rem;
}
</style>
