<template>
  <div class="category-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="openDialog()">新增分类</el-button>
        </div>
      </template>

      <el-table
        :data="treeData"
        row-key="id"
        :tree-props="{ children: 'children' }"
        v-loading="loading"
        default-expand-all
      >
        <el-table-column prop="name" label="分类名称" min-width="200" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="图标" width="100" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon" class="category-icon">
              <component :is="resolveIcon(row.icon)" />
            </el-icon>
            <span v-else class="muted">—</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确认删除该分类吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" text size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑分类' : '新增分类'" width="450px">
      <el-form ref="dialogFormRef" :model="dialogForm" :rules="dialogRules" label-width="90px">
        <el-form-item label="父分类" prop="parentId">
          <el-select v-model="dialogForm.parentId" placeholder="选择父分类" style="width: 100%">
            <el-option label="顶级分类" :value="0" />
            <el-option
              v-for="item in flatList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="dialogForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序值" prop="sort">
          <el-input-number v-model="dialogForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="图标名" prop="icon">
          <el-input v-model="dialogForm.icon" placeholder="可选：Element Plus 图标名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createCategory, deleteCategory, getCategories, updateCategory } from '../../api/category'

const loading = ref(false)
const saving = ref(false)
const treeData = ref([])
const flatList = ref([])
const dialogVisible = ref(false)
const editId = ref(null)
const dialogFormRef = ref()
const dialogForm = reactive({ name: '', parentId: 0, sort: 0, icon: '' })
const dialogRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const result = await getCategories()
    treeData.value = result.data || []
    flatList.value = flattenTree(treeData.value)
  } finally {
    loading.value = false
  }
}

/** 把树形分类展开为一维数组，用于父分类选择 */
/**
 * 把分类表里的 icon 字符串（如 'Reading'）解析成真实的 Element Plus 图标组件。
 * 如果数据库里写了不存在的图标名，回退显示文字。
 */
function resolveIcon(name) {
  return ElementPlusIconsVue[name] || name
}

function flattenTree(tree, result = []) {
  tree.forEach((item) => {
    result.push({ id: item.id, name: item.name })
    if (item.children && item.children.length > 0) {
      flattenTree(item.children, result)
    }
  })
  return result
}

function openDialog(row) {
  if (row) {
    editId.value = row.id
    dialogForm.name = row.name
    dialogForm.parentId = row.parentId
    dialogForm.sort = row.sort
    dialogForm.icon = row.icon || ''
  } else {
    editId.value = null
    dialogForm.name = ''
    dialogForm.parentId = 0
    dialogForm.sort = 0
    dialogForm.icon = ''
  }
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await dialogFormRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (editId.value) {
      await updateCategory(editId.value, dialogForm)
    } else {
      await createCategory(dialogForm)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    await loadData()
  } finally {
    saving.value = false
  }
}

async function handleDelete(id) {
  await deleteCategory(id)
  ElMessage.success('删除成功')
  await loadData()
}
</script>

<style scoped>
.category-icon {
  font-size: 20px;
  color: #409eff;
}
.muted { color: #c0c4cc; }

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
