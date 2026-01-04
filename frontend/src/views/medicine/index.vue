<template>
  <div class="app-container" style="padding: 20px;">
    <el-card shadow="never" class="mb-20">
      <el-row :gutter="20" justify="space-between">
        <el-col :span="8">
          <el-input
              v-model="queryParams.keyword"
              placeholder="请输入药品名称查询"
              clearable
              @clear="handleQuery"
              @keyup.enter="handleQuery"
          >
            <template #append>
              <el-button @click="handleQuery"><el-icon><Search /></el-icon></el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4" style="text-align: right;">
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增药品
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="pageData" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="药品名称" align="center" />
        <el-table-column prop="type" label="类别" align="center" width="120" />
        <el-table-column prop="price" label="单价" align="center" width="120">
          <template #default="scope">￥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.stock < 10 ? 'danger' : 'info'">
              {{ scope.row.stock }} {{ scope.row.unit }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
        <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="类别" prop="type">
          <el-select v-model="form.type" placeholder="请选择类别" style="width: 100%">
            <el-option label="西药" value="西药" />
            <el-option label="中成药" value="中成药" />
            <el-option label="耗材" value="耗材" />
          </el-select>
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :precision="2" :step="0.1" :min="0" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="如：盒/瓶/袋" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
// 注意：这里要引入你新建的 medicine API
import { getMedicineList, addMedicine, updateMedicine, deleteMedicine } from '@/api/medicine'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const allTableData = ref([])
const pageData = ref([])
const total = ref(0)

const queryParams = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const form = reactive({
  id: undefined,
  name: '',
  type: '西药',
  price: 0,
  stock: 0,
  unit: '盒'
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getMedicineList()
    allTableData.value = res.data
    handlePageChange()
  } finally {
    loading.value = false
  }
}

const handlePageChange = () => {
  let temp = allTableData.value
  if (queryParams.keyword) {
    temp = temp.filter(item => item.name && item.name.includes(queryParams.keyword))
  }
  total.value = temp.length
  const start = (queryParams.pageNum - 1) * queryParams.pageSize
  pageData.value = temp.slice(start, start + queryParams.pageSize)
}

const handleQuery = () => { queryParams.pageNum = 1; handlePageChange() }

const handleAdd = () => {
  Object.assign(form, { id: undefined, name: '', type: '西药', price: 0, stock: 0, unit: '盒' })
  dialogTitle.value = '新增药品'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogTitle.value = '编辑药品'
  dialogVisible.value = true
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) await updateMedicine(form)
      else await addMedicine(form)
      ElMessage.success('保存成功')
      dialogVisible.value = false
      getList()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除 ${row.name} 吗？`, '警告', { type: 'warning' }).then(async () => {
    await deleteMedicine(row.id)
    ElMessage.success('删除成功')
    getList()
  })
}

onMounted(() => { getList() })
</script>