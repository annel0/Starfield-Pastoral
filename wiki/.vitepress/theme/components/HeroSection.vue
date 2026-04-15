<script setup lang="ts">
</script>

<template>
  <div class="hero-bg">
    <div class="hero-glow hero-glow-1" />
    <div class="hero-glow hero-glow-2" />
    <div class="hero-glow hero-glow-3" />
    <div class="hero-grid" />
  </div>
</template>

<style scoped>
.hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  opacity: 0.4;
  animation: glow-float 8s ease-in-out infinite alternate;
}

.hero-glow-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(74, 222, 128, 0.3), transparent 70%);
  top: -120px;
  left: 20%;
  animation-delay: 0s;
}

.hero-glow-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.25), transparent 70%);
  top: 40px;
  right: 15%;
  animation-delay: -3s;
}

.hero-glow-3 {
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, rgba(192, 132, 252, 0.2), transparent 70%);
  bottom: -80px;
  left: 40%;
  animation-delay: -5s;
}

.hero-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.02) 1px, transparent 1px);
  background-size: 60px 60px;
  mask-image: radial-gradient(ellipse at center, black 30%, transparent 70%);
  -webkit-mask-image: radial-gradient(ellipse at center, black 30%, transparent 70%);
}

@keyframes glow-float {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(30px, -20px) scale(1.1); }
}
</style>
