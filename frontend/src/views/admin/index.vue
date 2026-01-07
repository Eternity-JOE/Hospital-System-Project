<template>
  <div class="app-container" style="padding: 20px;">
    <el-card shadow="never" class="mb-20">
      <el-row :gutter="20" justify="space-between">
        <el-col :span="8">
          <el-input
              v-model="queryParams.keyword"
              placeholder="请输入管理员账号"
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
            <el-icon><Plus /></el-icon> 新增管理员
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px;">
      <el-table
          :data="pageData"
          border
          stripe
          style="width: 100%"
          v-loading="loading"
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="username" label="账号" align="center" />
        <el-table-column prop="status" label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" align="center" width="180" />
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="scope">
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)" :disabled="scope.row.username === 'admin'">
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

    <el-dialog
        v-model="dialogVisible"
        title="新增管理员"
        width="400px"
        @close="resetForm"
    >
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
      >
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入管理员账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入登录密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Delete } from '@element-plus/icons-vue'
import { getAdminList, addAdmin, deleteAdmin } from '@/api/admin'

const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)

const allTableData = ref([])
const pageData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getAdminList()
    allTableData.value = res.data || []
    handlePageChange()
  } catch (error) {
    console.error('获取失败', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = () => {
  let temp = allTableData.value
  if (queryParams.keyword) {
    temp = temp.filter(item => item.username && item.username.includes(queryParams.keyword))
  }
  total.value = temp.length
  const start = (queryParams.pageNum - 1) * queryParams.pageSize
  const end = start + queryParams.pageSize
  pageData.value = temp.slice(start, end)
}

const handleQuery = () => {
  queryParams.pageNum = 1
  handlePageChange()
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await addAdmin(form)
        if (res.code === '200') {
          ElMessage.success('新增成功')
          dialogVisible.value = false
          getList()
        } else {
          ElMessage.error(res.msg || '新增失败')
        }
      } catch (error) {
        console.error(error)
      }
    }
  })
}

const handleDelete = (row) => {
  if (row.username === 'admin') {
    ElMessage.warning('默认管理员账号不能删除')
    return
  }
  ElMessageBox.confirm(`确认要删除管理员 "${row.username}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await deleteAdmin(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error(error)
    }
  })
}

const resetForm = () => {
  form.username = ''
  form.password = ''
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

onMounted(() => {
  getList()
})
</script>
