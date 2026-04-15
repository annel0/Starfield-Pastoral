<script setup lang="ts">
defineProps<{
  type?: 'info' | 'tip' | 'warning' | 'danger'
  title?: string
}>()

const colorMap: Record<string, string> = {
  info: 'var(--sc-blue-400)',
  tip: 'var(--sc-green-400)',
  warning: 'var(--sc-amber-400)',
  danger: '#f87171',
}
</script>

<template>
  <div class="info-box glass-card" :style="{ borderLeftColor: colorMap[type || 'info'] }">
    <div v-if="title" class="info-box-title" :style="{ color: colorMap[type || 'info'] }">
      {{ title }}
    </div>
    <div class="info-box-content">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.info-box {
  padding: 16px 20px;
  margin: 16px 0;
  border-left: 3px solid;
  border-radius: var(--sc-radius-sm);
}

.info-box-title {
  font-weight: 700;
  font-size: 0.9rem;
  margin-bottom: 6px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.info-box-content {
  font-size: 0.925rem;
  line-height: 1.7;
  color: var(--vp-c-text-2);
}
</style>
