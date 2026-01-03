<template>
  <div class="app-container" style="padding: 20px;">
    <el-card shadow="never" class="mb-20">
      <el-row :gutter="20" justify="space-between">
        <el-col :span="8">
          <el-input
              v-model="queryParams.keyword"
              placeholder="请输入医生姓名、手机号或职称"
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
            <el-icon><Plus /></el-icon> 新增医生
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
            <el-tag :type="scope.row.gender === 1 ? '' : 'danger'">
              {{ scope.row.gender === 1 ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="职称" align="center" width="150" />
        <el-table-column prop="departmentName" label="所属科室" align="center" width="150">
          <template #default="scope">
            {{ getDepartmentName(scope.row.departmentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" align="center" width="150" />
        <el-table-column prop="specialty" label="擅长领域" align="center" show-overflow-tooltip />
        <el-table-column label="关联病种" align="center" width="200" show-overflow-tooltip>
          <template #default="scope">
            <el-tag
                v-for="diseaseId in scope.row.diseaseIds"
                :key="diseaseId"
                size="small"
                style="margin-right: 5px;"
            >
              {{ getDiseaseName(diseaseId) }}
            </el-tag>
            <span v-if="!scope.row.diseaseIds || scope.row.diseaseIds.length === 0">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="schedule" label="排班时间" align="center" width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '在职' : '离职' }}
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

    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="600px"
        @close="resetForm"
    >
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入医生姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-input v-model="form.title" placeholder="如：主任医师、副主任医师、主治医师" />
        </el-form-item>
        <el-form-item label="所属科室" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="请选择科室" style="width: 100%" filterable>
            <el-option
                v-for="dept in departmentList"
                :key="dept.id"
                :label="dept.name"
                :value="dept.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="擅长领域" prop="specialty">
          <el-input v-model="form.specialty" type="textarea" placeholder="请输入擅长领域，如：心血管疾病、高血压" />
        </el-form-item>
        <el-form-item label="关联病种" prop="diseaseIds">
          <el-select
              v-model="form.diseaseIds"
              placeholder="请选择病种（最多选择3个）"
              multiple
              filterable
              style="width: 100%"
              :max-collapse-tags="2"
              @change="handleDiseaseChange"
          >
            <el-option
                v-for="disease in diseaseList"
                :key="disease.id"
                :label="disease.name"
                :value="disease.id"
            />
          </el-select>
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            已选择 {{ form.diseaseIds ? form.diseaseIds.length : 0 }} 个病种（限制1-3个）
          </div>
        </el-form-item>
        <el-form-item label="排班时间" prop="schedule">
          <el-input
              v-model="form.schedule"
              placeholder="如：周一上午,周二下午,周三全天"
              clearable
          />
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            格式：周一上午,周二下午,周三全天（用逗号分隔）
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="0">离职</el-radio>
          </el-radio-group>
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
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getDoctorList, addDoctor, updateDoctor, deleteDoctor } from '@/api/doctor'
import { getDepartmentList } from '@/api/department'
import { getDiseaseList } from '@/api/disease'

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

// 科室列表（用于下拉选择）
const departmentList = ref([])
// 病种列表（用于下拉选择）
const diseaseList = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

const form = reactive({
  id: undefined,
  name: '',
  gender: 1, // 1-男，2-女
  title: '',
  departmentId: undefined,
  phone: '',
  specialty: '',
  diseaseIds: [], // 关联的病种ID列表
  schedule: '', // 排班时间
  status: 1 // 1-在职，0-离职
})

const rules = {
  name: [{ required: true, message: '请输入医生姓名', trigger: 'blur' }],
  title: [{ required: true, message: '请输入职称', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择所属科室', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { min: 11, max: 11, message: '手机号长度必须为11位', trigger: 'blur' }
  ],
  diseaseIds: [
    { required: true, message: '请至少选择1个病种', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (!value || value.length === 0) {
          callback(new Error('请至少选择1个病种'))
        } else if (value.length > 3) {
          callback(new Error('最多只能选择3个病种'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// --- 核心方法 ---

// 1. 从后端获取数据
const getList = async () => {
  loading.value = true
  try {
    // 📡 发送请求给后端 8080
    const res = await getDoctorList()
    // 拿到所有数据
    allTableData.value = res.data || []
    // 处理前端分页和搜索
    handlePageChange()
  } catch (error) {
    console.error('获取失败', error)
    ElMessage.error('获取医生列表失败')
  } finally {
    loading.value = false
  }
}

// 2. 加载科室列表
const loadDepartmentList = async () => {
  try {
    const res = await getDepartmentList()
    departmentList.value = res.data || []
  } catch (error) {
    console.error('获取科室列表失败', error)
  }
}

// 3. 加载病种列表
const loadDiseaseList = async () => {
  try {
    const res = await getDiseaseList()
    diseaseList.value = res.data || []
  } catch (error) {
    console.error('获取病种列表失败', error)
  }
}

// 4. 根据科室ID获取科室名称
const getDepartmentName = (departmentId) => {
  if (!departmentId) return '-'
  const dept = departmentList.value.find(d => d.id === departmentId)
  return dept ? dept.name : '-'
}

// 5. 根据病种ID获取病种名称
const getDiseaseName = (diseaseId) => {
  if (!diseaseId) return '-'
  const disease = diseaseList.value.find(d => d.id === diseaseId)
  return disease ? disease.name : '-'
}

// 6. 处理病种选择变化（限制1-3个）
const handleDiseaseChange = (value) => {
  if (value && value.length > 3) {
    ElMessage.warning('最多只能选择3个病种')
    form.diseaseIds = value.slice(0, 3)
  } else if (value && value.length === 0) {
    ElMessage.warning('请至少选择1个病种')
  }
}

// 7. 处理前端搜索和分页
const handlePageChange = () => {
  // 第一步：搜索过滤
  let temp = allTableData.value
  if (queryParams.keyword) {
    const keyword = queryParams.keyword.toLowerCase()
    temp = temp.filter(item =>
        (item.name && item.name.toLowerCase().includes(keyword)) ||
        (item.phone && item.phone.includes(keyword)) ||
        (item.title && item.title.toLowerCase().includes(keyword))
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
  dialogTitle.value = '新增医生'
  dialogVisible.value = true
}

// 打开编辑
const handleEdit = (row) => {
  resetForm()
  dialogTitle.value = '编辑医生'
  // 复制数据到表单
  Object.assign(form, row)
  dialogVisible.value = true
}

// 8. 提交表单 (新增或修改)
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      // 验证病种数量
      if (!form.diseaseIds || form.diseaseIds.length === 0) {
        ElMessage.warning('请至少选择1个病种')
        return
      }
      if (form.diseaseIds.length > 3) {
        ElMessage.warning('最多只能选择3个病种')
        return
      }
      
      try {
        if (form.id) {
          // 📡 调用后端修改接口
          await updateDoctor(form)
          ElMessage.success('修改成功')
        } else {
          // 📡 调用后端新增接口
          await addDoctor(form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        getList() // 重新拉取最新数据
      } catch (error) {
        // 报错会在 request.js 里被拦截，或者在这里处理
        console.error(error)
        ElMessage.error('操作失败，请重试')
      }
    }
  })
}

// 6. 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认要删除医生 "${row.name}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      // 📡 调用后端删除接口
      await deleteDoctor(row.id)
      ElMessage.success('删除成功')
      getList() // 重新拉取
    } catch (error) {
      console.error(error)
      ElMessage.error('删除失败，请重试')
    }
  })
}

// 重置表单
const resetForm = () => {
  form.id = undefined
  form.name = ''
  form.gender = 1
  form.title = ''
  form.departmentId = undefined
  form.phone = ''
  form.specialty = ''
  form.diseaseIds = []
  form.schedule = ''
  form.status = 1
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 初始化
onMounted(() => {
  loadDepartmentList()
  loadDiseaseList()
  getList()
})
</script>
