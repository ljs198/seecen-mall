<template>
  <div class="home-page container">
    <!-- 中秋主题 Hero Banner -->
    <section class="banner">
      <div class="banner-bg" aria-hidden="true">
        <svg class="banner-moon" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <radialGradient id="homeMoonGrad" cx="35%" cy="35%">
              <stop offset="0%" stop-color="#ffffff"/>
              <stop offset="60%" stop-color="#fff8c4"/>
              <stop offset="100%" stop-color="#ffd700"/>
            </radialGradient>
            <filter id="moonGlow">
              <feGaussianBlur stdDeviation="4"/>
            </filter>
          </defs>
          <circle cx="100" cy="100" r="80" fill="url(#homeMoonGrad)" filter="url(#moonGlow)" opacity="0.4"/>
          <circle cx="100" cy="100" r="60" fill="url(#homeMoonGrad)"/>
          <circle cx="80" cy="90" r="6" fill="#e6c200" opacity="0.4"/>
          <circle cx="115" cy="105" r="4" fill="#e6c200" opacity="0.4"/>
          <circle cx="95" cy="120" r="3" fill="#e6c200" opacity="0.3"/>
        </svg>
        <svg class="banner-rabbit" viewBox="0 0 80 100" xmlns="http://www.w3.org/2000/svg">
          <ellipse cx="40" cy="75" rx="22" ry="15" fill="#fff8c4"/>
          <circle cx="40" cy="50" r="20" fill="#fff8c4"/>
          <ellipse cx="30" cy="28" rx="5" ry="18" fill="#fff8c4"/>
          <ellipse cx="50" cy="28" rx="5" ry="18" fill="#fff8c4"/>
          <ellipse cx="30" cy="28" rx="2" ry="14" fill="#ffcce0"/>
          <ellipse cx="50" cy="28" rx="2" ry="14" fill="#ffcce0"/>
          <circle cx="33" cy="48" r="2" fill="#3e2723"/>
          <circle cx="47" cy="48" r="2" fill="#3e2723"/>
          <ellipse cx="40" cy="55" rx="2" ry="1.5" fill="#ff8888"/>
          <circle cx="22" cy="78" r="6" fill="#c0392b" opacity="0.8"/>
          <rect x="20" y="76" width="4" height="8" fill="#ffd700"/>
        </svg>
        <svg class="banner-cloud" viewBox="0 0 120 40" xmlns="http://www.w3.org/2000/svg">
          <path d="M10,30 Q5,20 15,18 Q20,10 35,15 Q45,8 55,18 Q70,12 75,22 Q90,18 90,28 Q100,25 105,32 Q100,38 90,36 L15,36 Q5,36 10,30 Z"
                fill="#d4b996" opacity="0.4" stroke="#c8901b" stroke-width="0.5"/>
        </svg>
      </div>
      <div class="banner-content">
        <h1>月圆人团圆 <span class="moon-deco"></span> 好物共此时</h1>
        <p>中秋礼遇季 · 灯火万家，礼传千里</p>
      </div>
    </section>

    <section class="section" v-if="categories.length">
      <h2 class="section-title">商品分类</h2>
      <div class="category-grid">
        <button
          v-for="category in categories"
          :key="category.id"
          class="category-card"
          @click="goCategory(category.id)"
        >
          <span class="category-icon">🏮</span>
          <span>{{ category.name }}</span>
        </button>
      </div>
    </section>

    <section class="section">
      <h2 class="section-title">🔥 中秋好物推荐</h2>
      <div class="product-grid" v-loading="loading">
        <ProductCard v-for="product in products" :key="product.id" :product="product" />
      </div>
      <el-empty v-if="!loading && products.length === 0" description="暂无商品" />
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import ProductCard from '../../components/ProductCard.vue'
import { getCategories, getProducts } from '../../api/product'

const router = useRouter()
const loading = ref(false)
const categories = ref([])
const products = ref([])

onMounted(async () => {
  await loadCategories()
  await loadProducts()
})

async function loadCategories() {
  const res = await getCategories()
  const data = Array.isArray(res) ? res : (res && res.data) || []
  categories.value = data.filter((c) => !c.parentId || c.parentId === 0)
}

async function loadProducts() {
  loading.value = true
  try {
    const res = await getProducts({ page: 1, size: 8 })
    products.value = (res && res.data && res.data.records) || []
  } finally {
    loading.value = false
  }
}

function goCategory(id) {
  router.push({ path: '/category', query: { id } })
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 中秋主题 Banner */
.banner {
  position: relative;
  padding: 80px 28px 60px;
  margin-bottom: 28px;
  border-radius: 12px;
  overflow: hidden;
  background: linear-gradient(180deg, #0f1444 0%, #1a1b3a 50%, #2c1810 100%);
  border: 1px solid #c8901b;
  box-shadow: 0 8px 24px rgba(15, 20, 68, 0.3);
}

.banner-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.banner-moon {
  position: absolute;
  top: 20px;
  right: 60px;
  width: 140px;
  height: 140px;
  filter: drop-shadow(0 0 30px rgba(255, 215, 0, 0.5));
  animation: moon-pulse 4s ease-in-out infinite alternate;
}

@keyframes moon-pulse {
  from { filter: drop-shadow(0 0 20px rgba(255, 215, 0, 0.4)); }
  to { filter: drop-shadow(0 0 40px rgba(255, 215, 0, 0.7)); }
}

.banner-rabbit {
  position: absolute;
  bottom: 20px;
  left: 40px;
  width: 80px;
  height: 100px;
  animation: rabbit-bounce 3s ease-in-out infinite alternate;
}

@keyframes rabbit-bounce {
  from { transform: translateY(0); }
  to { transform: translateY(-8px); }
}

.banner-cloud {
  position: absolute;
  top: 30%;
  left: 5%;
  width: 120px;
  height: 40px;
  opacity: 0.6;
  animation: cloud-drift 12s linear infinite;
}

@keyframes cloud-drift {
  0% { transform: translateX(0); }
  50% { transform: translateX(60px); }
  100% { transform: translateX(0); }
}

.banner-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #ffd700;
}

.banner-content h1 {
  margin: 0 0 14px;
  font-size: 40px;
  font-family: 'STKaiti', 'KaiTi', serif;
  color: #ffd700;
  text-shadow: 0 0 20px rgba(255, 215, 0, 0.6);
  letter-spacing: 4px;
}

.banner-content p {
  margin: 0;
  font-size: 16px;
  color: #d4b996;
  letter-spacing: 2px;
}

.moon-deco {
  display: inline-block;
  width: 20px;
  height: 20px;
  background: radial-gradient(circle, #fff 30%, #ffd700 70%);
  border-radius: 50%;
  box-shadow: 0 0 16px #ffd700;
  vertical-align: middle;
  margin: 0 8px;
  animation: moon-pulse 2s ease-in-out infinite alternate;
}

.section {
  margin-bottom: 32px;
}

.section-title {
  margin: 0 0 16px;
  color: #c8901b;
  font-size: 24px;
  font-family: 'STKaiti', 'KaiTi', serif;
  letter-spacing: 2px;
  border-bottom: 2px solid #d4b996;
  padding-bottom: 8px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 14px;
}

.category-card {
  height: 80px;
  border: 1.5px solid #d4b996;
  border-radius: 10px;
  background: linear-gradient(135deg, #fdf6e3 0%, #f5ebd5 100%);
  color: #5d4037;
  cursor: pointer;
  font-size: 15px;
  font-family: 'STKaiti', 'KaiTi', serif;
  letter-spacing: 1px;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.category-card:hover {
  box-shadow: 0 6px 20px rgba(200, 144, 27, 0.3);
  transform: translateY(-3px);
  color: #c8901b;
  border-color: #c8901b;
  background: linear-gradient(135deg, #fff8c4 0%, #ffd700 100%);
}

.category-icon {
  font-size: 24px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  min-height: 120px;
}

@media (max-width: 767px) {
  .container {
    padding: 14px;
  }

  .banner {
    padding: 50px 20px 40px;
  }

  .banner-content h1 {
    font-size: 26px;
  }

  .banner-moon {
    width: 90px;
    height: 90px;
    right: 20px;
  }

  .banner-rabbit {
    width: 60px;
    height: 75px;
    left: 15px;
  }

  .product-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }
}
</style>
