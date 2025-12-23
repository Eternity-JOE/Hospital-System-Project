<template>
  <div class="app-container" style="padding: 20px;">
    <el-card shadow="never" class="mb-20">
      <el-row :gutter="20" justify="space-between">
        <el-col :span="8">
          <el-input
              v-model="queryParams.keyword"
              placeholder="请输入科室名称"
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
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增科室
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="pageData" border stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="科室名称" align="center" width="150" />
        <el-table-column prop="code" label="科室编码" align="center" width="120" />
        <el-table-column prop="location" label="位置" align="center" width="150" />
        <el-table-column prop="description" label="描述" align="center" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
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
            :page-sizes="[10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handlePageChange"
            @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入科室名称" />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="如：NEI、WAI、ER" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" placeholder="如：门诊楼2层" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="科室简介" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
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
import { getDepartmentList, addDepartment, updateDepartment, deleteDepartment } from '@/api/department'

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
  code: '',
  location: '',
  description: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入科室名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入科室编码', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getDepartmentList()
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
const handleAdd = () => { resetForm(); dialogTitle.value = '新增科室'; dialogVisible.value = true }
const handleEdit = (row) => { resetForm(); dialogTitle.value = '编辑科室'; Object.assign(form, row); dialogVisible.value = true }

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updateDepartment(form)
        ElMessage.success('修改成功')
      } else {
        await addDepartment(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      getList()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除科室 "${row.name}" 吗？`, '警告', { type: 'warning' })
    .then(async () => {
      await deleteDepartment(row.id)
      ElMessage.success('删除成功')
      getList()
    })
}

const resetForm = () => {
  form.id = undefined
  form.name = ''
  form.code = ''
  form.location = ''
  form.description = ''
  form.status = 1
  formRef.value?.clearValidate()
}

onMounted(() => getList())
</script>
