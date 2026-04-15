<script setup lang="ts">
export interface CropRow {
  name: string
  nameEn: string
  img: string
  seedImg?: string
  days: number
  regrow: number | null
  sell: number
  link: string
}

defineProps<{
  crops: CropRow[]
}>()
</script>

<template>
  <div class="crop-table-wrap">
    <table class="crop-table">
      <thead>
        <tr>
          <th class="col-img"></th>
          <th>作物</th>
          <th>成熟天数</th>
          <th>再生</th>
          <th>基础售价</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="c in crops" :key="c.nameEn" class="crop-row">
          <td class="col-img">
            <div class="crop-img-pair">
              <img :src="c.img" :alt="c.name" class="crop-img" />
              <img v-if="c.seedImg" :src="c.seedImg" :alt="c.name + '种子'" class="seed-img" />
            </div>
          </td>
          <td>
            <a :href="c.link" class="crop-name-link">
              <strong>{{ c.name }}</strong>
              <span class="crop-name-en">{{ c.nameEn }}</span>
            </a>
          </td>
          <td class="num-cell">{{ c.days }} 天</td>
          <td class="num-cell">
            <span v-if="c.regrow" class="regrow-badge">{{ c.regrow }} 天</span>
            <span v-else class="no-regrow">—</span>
          </td>
          <td class="num-cell gold-text">{{ c.sell }}g</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.crop-table-wrap {
  overflow-x: auto;
  margin: 20px 0;
  border-radius: var(--sc-radius-sm);
}

.crop-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background: var(--sc-glass-bg);
  border: 1px solid var(--sc-glass-border);
  border-radius: var(--sc-radius-sm);
  overflow: hidden;
}

.crop-table thead th {
  background: rgba(255, 255, 255, 0.04);
  padding: 12px 16px;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--vp-c-text-2);
  text-align: left;
  border-bottom: 1px solid var(--sc-glass-border);
}

.crop-table tbody tr {
  transition: background 0.2s ease;
}

.crop-table tbody tr:hover {
  background: rgba(255, 255, 255, 0.03);
}

.crop-table td {
  padding: 10px 16px;
  border-bottom: 1px solid var(--sc-glass-border);
  vertical-align: middle;
}

.crop-table tbody tr:last-child td {
  border-bottom: none;
}

.col-img {
  width: 72px;
  padding: 8px 12px !important;
}

.crop-img-pair {
  display: flex;
  align-items: center;
  gap: 4px;
}

.crop-img {
  width: 36px;
  height: 36px;
  image-rendering: pixelated;
  image-rendering: crisp-edges;
}

.seed-img {
  width: 24px;
  height: 24px;
  image-rendering: pixelated;
  image-rendering: crisp-edges;
  opacity: 0.6;
}

.crop-name-link {
  display: flex;
  flex-direction: column;
  text-decoration: none !important;
  color: inherit !important;
}

.crop-name-link:hover strong {
  color: var(--sc-green-400);
}

.crop-name-link strong {
  font-size: 0.95rem;
  transition: color 0.2s;
}

.crop-name-en {
  font-size: 0.75rem;
  color: var(--vp-c-text-3);
  font-style: italic;
}

.num-cell {
  text-align: center;
  font-variant-numeric: tabular-nums;
  font-weight: 500;
}

.regrow-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 600;
  background: rgba(74, 222, 128, 0.1);
  color: var(--sc-green-400);
  border: 1px solid rgba(74, 222, 128, 0.2);
}

.no-regrow {
  color: var(--vp-c-text-3);
}

.gold-text {
  color: var(--sc-amber-400);
  font-weight: 600;
}
</style>
