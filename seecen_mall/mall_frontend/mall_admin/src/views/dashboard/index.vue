<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6" v-for="item in statCards" :key="item.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-info">
              <div class="stat-label">{{ item.label }}</div>
              <div class="stat-value">{{ item.value }}</div>
            </div>
            <el-icon :size="40" :style="{ color: item.color }">
              <component :is="item.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="chart-card">
      <template #header>近 7 天销售趋势</template>
      <div ref="chartRef" class="chart"></div>
      <el-empty v-if="!loading && (!data.recent7Days || data.recent7Days.length === 0)" description="暂无销售数据" />
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { Goods, ShoppingCart, Money, User } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboard } from '../../api/dashboard'

const chartRef = ref(null)
let chartInstance = null
const loading = ref(true)
const data = ref({
  productCount: 0,
  userCount: 0,
  orderCount: 0,
  totalSales: 0,
  todaySales: 0,
  todayOrders: 0,
  recent7Days: []
})

const statCards = computed(() => [
  { label: '商品总数', value: data.value.productCount, icon: Goods, color: '#409EFF' },
  { label: '用户总数', value: data.value.userCount, icon: User, color: '#67C23A' },
  { label: '订单总数', value: data.value.orderCount, icon: ShoppingCart, color: '#E6A23C' },
  { label: '今日销售额', value: '¥' + Number(data.value.todaySales || 0).toFixed(2), icon: Money, color: '#F56C6C' }
])

function initChart() {
  if (!chartRef.value) return
  if (chartInstance) chartInstance.dispose()
  chartInstance = echarts.init(chartRef.value)
  const days = data.value.recent7Days || []
  chartInstance.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['销售额', '订单数'] },
    grid: { left: 50, right: 50, top: 50, bottom: 40 },
    xAxis: { type: 'category', data: days.map((d) => d.date) },
    yAxis: [
      { type: 'value', name: '销售额(元)', axisLabel: { formatter: '{value}' } },
      { type: 'value', name: '订单数', axisLabel: { formatter: '{value}' } }
    ],
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        data: days.map((d) => Number(d.sales || 0).toFixed(2)),
        itemStyle: { color: '#409EFF' },
        areaStyle: { color: 'rgba(64,158,255,0.15)' }
      },
      {
        name: '订单数',
        type: 'bar',
        yAxisIndex: 1,
        data: days.map((d) => d.orders || 0),
        itemStyle: { color: '#67C23A' }
      }
    ]
  })
  window.addEventListener('resize', resizeChart)
}

function resizeChart() {
  if (chartInstance) chartInstance.resize()
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getDashboard()
    // request 拦截器返回的是完整 Result，从 res.data 取真实数据
    data.value = (res && res.data) || data.value
    setTimeout(initChart, 0)
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeChart)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-card {
  border-radius: 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.stat-value {
  color: #303133;
  font-size: 24px;
  font-weight: 700;
}

.chart-card {
  border-radius: 8px;
}

.chart {
  height: 400px;
}
</style>
