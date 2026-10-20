<template>
  <article class="product-card" @click="goToDetail">
    <!-- 左上角小灯笼角饰 -->
    <span class="corner-lantern" aria-hidden="true">🏮</span>

    <div class="card-image">
      <img :src="product.coverImage || placeholderImage" :alt="product.name" loading="lazy" @error="handleImageError" />
    </div>
    <div class="card-body">
      <div class="card-title">{{ product.name }}</div>
      <div class="card-price-row">
        <span class="current-price">¥{{ formatPrice(product.price) }}</span>
        <span v-if="product.originalPrice && Number(product.originalPrice) > Number(product.price)" class="original-price">
          ¥{{ formatPrice(product.originalPrice) }}
        </span>
      </div>
      <div class="card-meta">
        <span>已售 {{ product.salesCount ?? 0 }}</span>
        <span>库存 {{ product.stock ?? 0 }}</span>
      </div>
    </div>
  </article>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const placeholderImage = '/placeholder.svg'
function formatPrice(price) {
  if (price == null) return '0.00'
  return Number(price).toFixed(2)
}

function handleImageError(event) {
  if (event.target.src !== placeholderImage) {
    event.target.src = placeholderImage
  }
}

function goToDetail() {
  if (props.product.id) {
    router.push(`/product/${props.product.id}`)
  }
}
</script>

<style scoped>
.product-card {
  position: relative;
  background: linear-gradient(135deg, #fffdf5 0%, #fdf6e3 100%);
  border: 1.5px solid #d4b996;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.product-card:hover {
  box-shadow: 0 12px 28px rgba(200, 144, 27, 0.15);
  transform: translateY(-4px);
  border-color: #c8901b;
  background: linear-gradient(135deg, #fffaf0 0%, #fff3d6 100%);
}

/* 左上角小灯笼 */
.corner-lantern {
  position: absolute;
  top: 6px;
  left: 6px;
  font-size: 20px;
  z-index: 2;
  filter: drop-shadow(0 2px 4px rgba(192, 57, 43, 0.5));
  transform-origin: top center;
  animation: lantern-sway 3s ease-in-out infinite alternate;
}

@keyframes lantern-sway {
  from { transform: rotate(-8deg); }
  to { transform: rotate(8deg); }
}

.card-image {
  position: relative;
  width: 100%;
  padding-top: 100%;
  background: linear-gradient(135deg, #f5ebd5 0%, #fdf6e3 100%);
  overflow: hidden;
}

.card-image img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}

.product-card:hover .card-image img {
  transform: scale(1.05);
}

.card-body {
  padding: 12px;
  border-top: 1px dashed #d4b996;
}

.card-title {
  min-height: 40px;
  color: #3e2723;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-shadow: 0 1px 0 rgba(255, 255, 255, 0.9);
  transition: all 0.3s;
  -webkit-font-smoothing: antialiased;
  text-rendering: optimizeLegibility;
}

.product-card:hover .card-title {
  -webkit-line-clamp: 3;
  color: #0d0e1f;
  font-weight: 700;
  text-shadow: 0 1px 0 rgba(255, 255, 255, 1), 0 0 1px rgba(0, 0, 0, 0.1);
}

.card-price-row {
  margin-top: 10px;
  display: flex;
  align-items: baseline;
  gap: 8px;
  transition: transform 0.3s;
}

.product-card:hover .card-price-row {
  transform: scale(1.05);
  transform-origin: left center;
}

.current-price {
  color: #b12708;
  font-size: 20px;
  font-weight: 800;
  font-family: 'STKaiti', 'KaiTi', serif;
  text-shadow: 0 1px 0 rgba(255, 255, 255, 1);
  -webkit-font-smoothing: antialiased;
}

.original-price {
  color: #a89678;
  font-size: 12px;
  text-decoration: line-through;
}

.card-meta {
  margin-top: 6px;
  color: #6b4423;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  gap: 12px;
  text-shadow: 0 1px 0 rgba(255, 255, 255, 1);
}
</style>
