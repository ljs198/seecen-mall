<template>
  <div class="product-edit">
    <el-card>
      <template #header>
        <span>新增商品</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="product-form">
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
            <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="cover-tip">点击上传，支持 jpg / png / gif / webp / svg，单张不超过 5MB</div>
        </el-form-item>
        <el-form-item label="简要描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="详情" prop="detail">
          <el-input v-model="form.detail" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCategories } from '../../api/category'
import { createProduct } from '../../api/product'

const router = useRouter()
const formRef = ref()
const saving = ref(false)
const categories = ref([])
const form = reactive({
  name: '',
  categoryId: null,
  price: 0,
  originalPrice: 0,
  stock: 0,
  coverImage: '',
  description: '',
  detail: '',
  status: 1
})
const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  coverImage: [{ required: true, message: '请上传封面图', trigger: 'change' }]
}

// el-upload 配置
const uploadUrl = '/api/admin/upload/image'
const uploadHeaders = computed(() => ({
  Authorization: localStorage.getItem('token') ? `Bearer ${localStorage.getItem('token')}` : ''
}))

onMounted(async () => {
  const res = await getCategories()
  categories.value = flattenTree(res.data || [])
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

/**
 * 上传前校验：限制大小、确认是图片
 */
function beforeUpload(file) {
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过 5MB')
    return false
  }
  return true
}

/**
 * 上传成功：把后端返回的 URL 写回表单
 */
function handleUploadSuccess(res) {
  if (res && res.code === 200) {
    form.coverImage = res.data
    ElMessage.success('封面上传成功')
  } else {
    ElMessage.error(res?.message || '上传失败')
  }
}

/**
 * 取消新增，返回商品列表页
 */
function handleCancel() {
  router.push('/products')
}

function handleUploadError() {
  ElMessage.error('封面上传失败')
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await createProduct(form)
    ElMessage.success('保存成功')
    router.push('/products')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
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





