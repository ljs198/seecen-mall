<template>
  <div class="product-detail">
    <!-- 顶部导航：返回 + 标题 -->
    <el-page-header @back="goBack" class="page-header">
      <template #content>
        <span class="page-title">商品详情</span>
      </template>
    </el-page-header>

    <!-- 操作栏：两个独立按钮切换详情 / 编辑 -->
    <el-card class="action-card" shadow="never">
      <div class="action-bar">
        <el-button-group>
          <el-button
            :type="viewMode === 'view' ? 'primary' : 'default'"
            :icon="ViewIcon"
            @click="viewMode = 'view'"
          >
            详情
          </el-button>
          <el-button
            :type="viewMode === 'edit' ? 'primary' : 'default'"
            :icon="EditIcon"
            @click="viewMode = 'edit'"
          >
            编辑
          </el-button>
        </el-button-group>
        <span class="action-hint">
          {{ viewMode === 'view' ? '当前为只读视图，点击"编辑"可修改' : '当前为编辑视图，修改后点击底部"保存"' }}
        </span>
      </div>
    </el-card>

    <!-- 详情视图：纯展示 -->
    <el-card v-show="viewMode === 'view'" v-loading="loading" class="content-card">
      <template v-if="form.id">
        <div class="view-cover">
          <el-image
            v-if="form.coverImage"
            :src="fullImageUrl(form.coverImage)"
            :preview-src-list="[fullImageUrl(form.coverImage)]"
            fit="cover"
            class="cover-large"
          />
          <div v-else class="cover-placeholder">暂无封面</div>
        </div>

        <el-descriptions :column="2" border class="view-desc">
          <el-descriptions-item label="商品名称">{{ form.name }}</el-descriptions-item>
          <el-descriptions-item label="所属分类">
            {{ categoryName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="售价">¥{{ form.price }}</el-descriptions-item>
          <el-descriptions-item label="原价">
            {{ form.originalPrice ? '¥' + form.originalPrice : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="库存">{{ form.stock }}</el-descriptions-item>
          <el-descriptions-item label="累计销量">{{ form.salesCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="form.status === 1 ? 'success' : 'info'">
              {{ form.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDate(form.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ formatDate(form.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="view-block">
          <div class="block-title">商品简介</div>
          <div class="block-content">{{ form.description || '（暂无简介）' }}</div>
        </div>

        <div class="view-block">
          <div class="block-title">商品详情</div>
          <div
            class="block-content detail-html"
            v-html="form.detail || '<p class=\'muted\'>（暂无详情）</p>'"
          />
        </div>
      </template>
    </el-card>

    <!-- 编辑视图：完整表单 -->
    <el-card v-show="viewMode === 'edit'" class="content-card">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="product-form"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="售价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="0.01" />
        </el-form-item>
        <el-form-item label="原价" prop="originalPrice">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" :step="0.01" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <el-upload
            class="cover-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            accept="image/*"
          >
            <img v-if="form.coverImage" :src="fullImageUrl(form.coverImage)" class="cover-preview" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="cover-tip">点击上传，支持 jpg / png / gif / webp / svg，单张不超过 5MB</div>
        </el-form-item>
        <el-form-item label="简要描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="详情" prop="detail">
          <el-input v-model="form.detail" type="textarea" :rows="6" placeholder="可填写 HTML 文本，例如 &lt;h2&gt;课程大纲&lt;/h2&gt;&lt;p&gt;...&lt;/p&gt;" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
          <el-button @click="goBack">返回列表</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, View, Edit } from '@element-plus/icons-vue'
import { getCategories } from '../../api/category'
import { getProduct, updateProduct } from '../../api/product'
import { fullImageUrl, uploadUrl } from '../../utils/image'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const saving = ref(false)
const loading = ref(false)
const categories = ref([])

// 当前视图模式：view = 只读详情，edit = 编辑表单
// 根据路由 meta.mode 决定初始值（/products/detail/:id → view，/products/edit/:id → edit）
const viewMode = ref(route.meta?.mode === 'edit' ? 'edit' : 'view')

// 按钮图标
const ViewIcon = View
const EditIcon = Edit

const form = reactive({
  id: null,
  name: '',
  categoryId: null,
  price: 0,
  originalPrice: 0,
  stock: 0,
  coverImage: '',
  description: '',
  detail: '',
  status: 1,
  salesCount: 0,
  createTime: null,
  updateTime: null
})
const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  coverImage: [{ required: true, message: '请上传封面图', trigger: 'change' }]
}

// 当前分类名称（用于详情展示）
const categoryName = computed(() => {
  const found = categories.value.find((c) => c.id === form.categoryId)
  return found ? found.name : ''
})

// el-upload 配置（地址与请求头来自 utils/image，开发走 proxy、生产直连后端）
const uploadHeaders = computed(() => ({
  Authorization: localStorage.getItem('token')
    ? `Bearer ${localStorage.getItem('token')}`
    : ''
}))

/** 加载分类下拉 + 商品详情 */
onMounted(async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('商品 ID 不存在')
    router.push('/products')
    return
  }
  loading.value = true
  try {
    const catRes = await getCategories()
    categories.value = flattenTree(catRes.data || [])

    const res = await getProduct(id)
    Object.assign(form, res.data)
  } catch (e) {
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
})

function flattenTree(tree, result = []) {
  tree.forEach((item) => {
    result.push({ id: item.id, name: item.name })
    if (item.children && item.children.length > 0) {
      flattenTree(item.children, result)
    }
  })
  return result
}

/** 上传相关 */
function beforeUpload(file) {
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过 5MB')
    return false
  }
  return true
}

function handleUploadSuccess(res) {
  if (res && res.code === 200) {
    form.coverImage = res.data
    ElMessage.success('封面上传成功')
  } else {
    ElMessage.error(res?.message || '上传失败')
  }
}

function handleUploadError() {
  ElMessage.error('封面上传失败')
}

/** 保存编辑 */
async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await updateProduct(form.id, form)
    ElMessage.success('保存成功')
    // 重新拉取详情，保证最新数据
    const res = await getProduct(form.id)
    Object.assign(form, res.data)
    // 切回详情视图
    viewMode.value = 'view'
  } finally {
    saving.value = false
  }
}

/** 返回列表 */
function goBack() {
  router.push('/products')
}

/** 时间格式化 */
function formatDate(value) {
  if (!value) return '-'
  const d = new Date(value)
  if (isNaN(d.getTime())) return value
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
</script>

<style scoped>
.page-header {
  margin-bottom: 12px;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
}
.action-card {
  margin-bottom: 16px;
}
.action-bar {
  display: flex;
  align-items: center;
  gap: 16px;
}
.action-hint {
  color: #909399;
  font-size: 13px;
}
.content-card {
  margin-bottom: 16px;
}
.view-cover {
  text-align: center;
  margin-bottom: 16px;
}
.cover-large {
  width: 200px;
  height: 200px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}
.cover-placeholder {
  display: inline-block;
  width: 200px;
  height: 200px;
  line-height: 200px;
  background: #f5f7fa;
  color: #909399;
  border-radius: 6px;
}
.view-desc {
  margin-bottom: 16px;
}
.view-block {
  margin-top: 16px;
}
.block-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  border-left: 3px solid #409eff;
  padding-left: 8px;
}
.block-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}
.detail-html :deep(h1),
.detail-html :deep(h2),
.detail-html :deep(h3) {
  margin: 12px 0 8px;
}
.detail-html :deep(p) {
  margin: 6px 0;
}
.muted { color: #c0c4cc; }

.product-form {
  max-width: 720px;
}
.cover-uploader {
  display: inline-block;
}
.cover-uploader :deep(.el-upload) {
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.cover-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}
.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}
.cover-preview {
  width: 120px;
  height: 120px;
  object-fit: cover;
  display: block;
}
.cover-tip {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
}
</style>
