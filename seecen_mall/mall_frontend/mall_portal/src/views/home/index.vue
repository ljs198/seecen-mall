<template>
  <div class="home-page container">
    <!-- 中秋夜空 Banner -->
    <section class="banner">
      <!-- 满月光晕 -->
      <div class="moon" aria-hidden="true">
        <div class="moon-crater" style="top:20%;left:25%;width:18%;height:18%"></div>
        <div class="moon-crater" style="top:55%;left:60%;width:12%;height:12%"></div>
        <div class="moon-crater" style="top:35%;left:55%;width:8%;height:8%"></div>
      </div>
      <!-- 祥云飘动 -->
      <svg class="cloud cloud-1" viewBox="0 0 100 40" aria-hidden="true">
        <path d="M10,30 Q15,15 30,20 Q40,5 55,20 Q70,15 75,30 Q60,38 40,35 Q20,38 10,30 Z" fill="rgba(255,215,0,0.15)"/>
      </svg>
      <svg class="cloud cloud-2" viewBox="0 0 100 40" aria-hidden="true">
        <path d="M10,30 Q15,15 30,20 Q40,5 55,20 Q70,15 75,30 Q60,38 40,35 Q20,38 10,30 Z" fill="rgba(255,215,0,0.1)"/>
      </svg>
      <!-- 玉兔抱灯笼 -->
      <svg class="jade-rabbit" viewBox="0 0 100 120" aria-hidden="true">
        <!-- 兔身 -->
        <ellipse cx="50" cy="85" rx="28" ry="22" fill="#fff8e7"/>
        <!-- 兔头 -->
        <circle cx="50" cy="55" r="20" fill="#fff8e7"/>
        <!-- 长耳朵 -->
        <ellipse cx="40" cy="30" rx="5" ry="18" fill="#fff8e7" transform="rotate(-10 40 30)"/>
        <ellipse cx="60" cy="30" rx="5" ry="18" fill="#fff8e7" transform="rotate(10 60 30)"/>
        <ellipse cx="40" cy="32" rx="2.5" ry="12" fill="#ffc0cb" transform="rotate(-10 40 32)"/>
        <ellipse cx="60" cy="32" rx="2.5" ry="12" fill="#ffc0cb" transform="rotate(10 60 32)"/>
        <!-- 眼睛 -->
        <circle cx="43" cy="55" r="2.5" fill="#1a1b3a"/>
        <circle cx="57" cy="55" r="2.5" fill="#1a1b3a"/>
        <!-- 鼻子 -->
        <ellipse cx="50" cy="62" rx="2" ry="1.5" fill="#ffc0cb"/>
        <!-- 灯笼 -->
        <ellipse cx="50" cy="88" rx="10" ry="12" fill="#c0392b"/>
        <rect x="46" y="76" width="8" height="3" fill="#ffd700"/>
        <rect x="46" y="99" width="8" height="3" fill="#ffd700"/>
        <line x1="50" y1="73" x2="50" y2="68" stroke="#ffd700" stroke-width="1"/>
      </svg>
      <div class="banner-content">
        <h1>智能商城</h1>
        <p class="banner-subtitle">从分类开始浏览商品，发现更多好物</p>
        <p class="banner-poem">花好月圆夜 · 好礼共此时</p>
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
      <h2 class="section-title">热门推荐</h2>
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

/* 中秋夜空 Banner */
.banner {
  position: relative;
  padding: 60px 40px;
  margin-bottom: 28px;
  border-radius: 16px;
  background: linear-gradient(180deg, #0f1444 0%, #1a1b3a 40%, #2c3e50 80%, #c8901b 100%);
  color: #ffd700;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(26, 27, 58, 0.4);
}

/* 满月光晕 */
.moon {
  position: absolute;
  top: 30px;
  right: 60px;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: radial-gradient(circle at 35% 35%, #fffacd 0%, #fff8c4 40%, #ffd700 100%);
  box-shadow:
    0 0 40px rgba(255, 248, 196, 0.6),
    0 0 80px rgba(255, 215, 0, 0.4),
    0 0 120px rgba(255, 215, 0, 0.2);
  animation: moon-float 6s ease-in-out infinite alternate;
}

.moon-crater {
  position: absolute;
  border-radius: 50%;
  background: rgba(200, 144, 27, 0.2);
}

@keyframes moon-float {
  from { transform: translateY(0); }
  to { transform: translateY(-10px); }
}

/* 祥云 */
.cloud {
  position: absolute;
  width: 120px;
  height: 48px;
  opacity: 0.7;
}

.cloud-1 {
  top: 40px;
  left: 10%;
  animation: cloud-drift 20s linear infinite;
}

.cloud-2 {
  top: 80px;
  left: 50%;
  width: 80px;
  height: 32px;
  animation: cloud-drift 25s linear infinite reverse;
}

@keyframes cloud-drift {
  from { transform: translateX(0); }
  to { transform: translateX(60vw); }
}

/* 玉兔 */
.jade-rabbit {
  position: absolute;
  bottom: 20px;
  right: 200px;
  width: 80px;
  height: 96px;
  animation: rabbit-bounce 3s ease-in-out infinite alternate;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
}

@keyframes rabbit-bounce {
  from { transform: translateY(0); }
  to { transform: translateY(-6px); }
}

.banner-content {
  position: relative;
  z-index: 2;
  max-width: 60%;
}

.banner h1 {
  margin: 0 0 10px;
  font-size: 36px;
  font-family: 'STKaiti', 'KaiTi', serif;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5), 0 0 20px rgba(255, 215, 0, 0.3);
  letter-spacing: 4px;
}

.banner-subtitle {
  margin: 0 0 8px;
  font-size: 16px;
  color: #fdf6e3;
  opacity: 0.9;
}

.banner-poem {
  margin: 8px 0 0;
  font-size: 18px;
  font-family: 'STKaiti', 'KaiTi', serif;
  color: #ffd700;
  letter-spacing: 6px;
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
}

.section {
  margin-bottom: 32px;
}

.section-title {
  margin: 0 0 16px;
  color: #3e2723;
  font-size: 22px;
  font-family: 'STKaiti', 'KaiTi', serif;
  position: relative;
  padding-left: 14px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, #c8901b 0%, #c0392b 100%);
  border-radius: 2px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 14px;
}

.category-card {
  height: 72px;
  border: 1.5px solid #d4b996;
  border-radius: 10px;
  background: linear-gradient(135deg, #fffdf5 0%, #fdf6e3 100%);
  color: #3e2723;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.category-card:hover {
  box-shadow: 0 8px 20px rgba(200, 144, 27, 0.2);
  transform: translateY(-3px);
  color: #c8901b;
  border-color: #c8901b;
}

.category-icon {
  font-size: 18px;
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
    padding: 40px 24px;
  }

  .banner h1 {
    font-size: 26px;
  }

  .moon {
    width: 60px;
    height: 60px;
    top: 16px;
    right: 16px;
  }

  .jade-rabbit {
    width: 50px;
    height: 60px;
    right: 90px;
    bottom: 12px;
  }

  .banner-content {
    max-width: 100%;
  }

  .product-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }
}
</style>
