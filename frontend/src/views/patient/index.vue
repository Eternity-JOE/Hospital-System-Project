<template>
  <div class="app-container" style="padding: 20px;">
    <el-card shadow="never" class="mb-20">
      <el-row :gutter="20" justify="space-between">
        <el-col :span="8">
          <el-input
              v-model="queryParams.keyword"
              placeholder="请输入病人姓名或手机号"
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
            <el-icon><Plus /></el-icon> 新增病人
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
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="姓名" align="center" width="120" />
        <el-table-column prop="gender" label="性别" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.gender === '男' ? '' : 'danger'">
              {{ scope.row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" align="center" width="100" />
        <el-table-column prop="phone" label="手机号码" align="center" width="150" />
        <el-table-column prop="address" label="家庭住址" align="center" show-overflow-tooltip />

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

    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="500px"
        @close="resetForm"
    >
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入病人姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="form.age" :min="1" :max="120" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="住址" prop="address">
          <el-input v-model="form.address" type="textarea" placeholder="请输入详细地址" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue' // 引入图标
// 1. 引入真实的 API
import { getPatientList, addPatient, updatePatient, deletePatient } from '@/api/patient'

// --- 状态定义 ---
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

// 存放从后端拿到的所有数据
const allTableData = ref([])
// 存放当前页显示的数据
const pageData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

const form = reactive({
  id: undefined,
  name: '',
  gender: '男',
  age: 18,
  phone: '',
  address: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { min: 11, max: 11, message: '手机号长度必须为11位', trigger: 'blur' }
  ]
}

// --- 核心方法 ---

// 1. 从后端获取数据
const getList = async () => {
  loading.value = true
  try {
    // 📡 发送请求给后端 8080
    const res = await getPatientList()
    // 拿到所有数据
    allTableData.value = res.data
    // 处理前端分页和搜索
    handlePageChange()
  } catch (error) {
    console.error('获取失败', error)
  } finally {
    loading.value = false
  }
}

// 2. 处理前端搜索和分页 (因为后端目前只写了查全部，还没写分页SQL，所以前端先自己处理)
const handlePageChange = () => {
  // 第一步：搜索过滤
  let temp = allTableData.value
  if (queryParams.keyword) {
    temp = temp.filter(item =>
        (item.name && item.name.includes(queryParams.keyword)) ||
        (item.phone && item.phone.includes(queryParams.keyword))
    )
  }
  total.value = temp.length

  // 第二步：切片分页
  const start = (queryParams.pageNum - 1) * queryParams.pageSize
  const end = start + queryParams.pageSize
  pageData.value = temp.slice(start, end)
}

// 搜索按钮
const handleQuery = () => {
  queryParams.pageNum = 1
  handlePageChange()
}

// 打开新增
const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增病人'
  dialogVisible.value = true
}

// 打开编辑
const handleEdit = (row) => {
  resetForm()
  dialogTitle.value = '编辑病人'
  // 复制数据到表单
  Object.assign(form, row)
  dialogVisible.value = true
}

// 3. 提交表单 (新增或修改)
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          // 📡 调用后端修改接口
          await updatePatient(form)
          ElMessage.success('修改成功')
        } else {
          // 📡 调用后端新增接口
          await addPatient(form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        getList() // 重新拉取最新数据
      } catch (error) {
        // 报错会在 request.js 里被拦截，或者在这里处理
        console.error(error)
      }
    }
  })
}

// 4. 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认要删除病人 "${row.name}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      // 📡 调用后端删除接口
      await deletePatient(row.id)
      ElMessage.success('删除成功')
      getList() // 重新拉取
    } catch (error) {
      console.error(error)
    }
  })
}

// 重置表单
const resetForm = () => {
  form.id = undefined
  form.name = ''
  form.gender = '男'
  form.age = 18
  form.phone = ''
  form.address = ''
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 初始化
onMounted(() => {
  getList()
})
</script>