<template>
  <div class="app-container" style="padding: 20px;">
    <!-- 顶部操作栏 -->
    <el-card shadow="never">
      <el-row :gutter="20" justify="space-between" align="middle">
        <el-col :span="16">
          <el-space>
            <el-select v-model="queryParams.departmentId" placeholder="选择科室" clearable style="width: 150px" @change="handleQuery">
              <el-option v-for="d in departmentList" :key="d.id" :label="d.name" :value="d.id" />
            </el-select>
            <el-date-picker v-model="queryParams.date" type="date" placeholder="选择日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" @change="handleQuery" />
            <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleQuery">
              <el-option label="待就诊" :value="1" />
              <el-option label="已完成" :value="2" />
              <el-option label="已取消" :value="0" />
              <el-option label="爽约" :value="3" />
            </el-select>
          </el-space>
        </el-col>
        <el-col :span="8" style="text-align: right;">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增挂号
          </el-button>
          <el-button type="success" @click="showQueueDialog">
            <el-icon><List /></el-icon> 查看排队
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 挂号列表 -->
    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="pageData" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="patientName" label="患者姓名" align="center" width="100" />
        <el-table-column prop="departmentName" label="科室" align="center" width="120" />
        <el-table-column prop="appointmentDate" label="预约日期" align="center" width="120" />
        <el-table-column prop="timeSlot" label="时段" align="center" width="80">
          <template #default="scope">
            <el-tag size="small">{{ scope.row.timeSlot === 'AM' ? '上午' : '下午' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registrationType" label="类型" align="center" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.registrationType === 2 ? 'danger' : ''">
              {{ scope.row.registrationType === 2 ? '急诊' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="患者年龄" align="center" width="100">
          <template #default="scope">
            <span>{{ getAgeCategory(scope.row.patientType) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="军人" align="center" width="70">
          <template #default="scope">
            <span>{{ (scope.row.patientType & 2) !== 0 ? '是' : '否' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="doctorName" label="医生" align="center" width="100" />
        <el-table-column prop="isReturn" label="复诊" align="center" width="70">
          <template #default="scope">
            <el-tag v-if="scope.row.isReturn === 1" type="warning" size="small">复诊</el-tag>
            <span v-else>初诊</span>
          </template>
        </el-table-column>
        <el-table-column prop="priorityScore" label="优先级" align="center" width="80">
          <template #default="scope">
            <el-tag :type="getPriorityType(scope.row.priorityScore)">{{ scope.row.priorityScore }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" align="center" width="90">
          <template #default="scope">
            <el-tag :type="statusTypeMap[scope.row.status]">{{ statusMap[scope.row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="scope">
            <template v-if="scope.row.status === 1">
              <el-button link type="success" size="small" @click="handleComplete(scope.row)">完成</el-button>
              <el-button link type="warning" size="small" @click="handleCancel(scope.row)">退号</el-button>
              <el-button link type="danger" size="small" @click="handleNoShow(scope.row)">爽约</el-button>
            </template>
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 新增/编辑挂号对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="患者" prop="patientId">
          <el-select v-model="form.patientId" placeholder="选择患者" filterable style="width: 100%">
            <el-option v-for="p in patientList" :key="p.id" :label="`${p.name} (${p.phone})`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="选择科室" style="width: 100%" @change="onDepartmentChange">
            <el-option v-for="d in departmentList" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生">
          <el-select v-model="form.doctorId" placeholder="选择医生（可选）" clearable style="width: 100%" :disabled="!form.departmentId">
            <el-option v-for="doc in doctorList" :key="doc.id" :label="`${doc.name} (${doc.title || '医师'})`" :value="doc.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期" prop="appointmentDate">
          <el-date-picker v-model="form.appointmentDate" type="date" placeholder="选择日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" :disabled-date="disabledDate" style="width: 100%" @change="onDateChange" />
        </el-form-item>
        <el-form-item label="时段" prop="timeSlot">
          <el-radio-group v-model="form.timeSlot" @change="loadSlots">
            <el-radio-button label="AM">上午 ({{ slotsInfo.AM?.available || 0 }}号)</el-radio-button>
            <el-radio-button label="PM">下午 ({{ slotsInfo.PM?.available || 0 }}号)</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="挂号类型" prop="registrationType">
          <el-radio-group v-model="form.registrationType">
            <el-radio :label="1">普通</el-radio>
            <el-radio :label="2">急诊</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="军人">
          <el-switch v-model="form.isMilitary" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="是否复诊">
          <el-switch v-model="form.isReturn" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 排队队列对话框 -->
    <el-dialog v-model="queueDialogVisible" title="实时排队队列" width="700px">
      <el-form :inline="true" style="margin-bottom: 15px;">
        <el-form-item label="科室">
          <el-select v-model="queueQuery.departmentId" placeholder="选择科室" @change="onQueueDepartmentChange">
            <el-option v-for="d in departmentList" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生">
          <el-select v-model="queueQuery.doctorId" placeholder="选择医生" @change="loadQueue" :disabled="!queueQuery.departmentId">
            <el-option v-for="doc in queueDoctorList" :key="doc.id" :label="doc.name" :value="doc.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="queueQuery.date" type="date" value-format="YYYY-MM-DD" @change="loadQueue" />
        </el-form-item>
        <el-form-item label="时段">
          <el-radio-group v-model="queueQuery.timeSlot" @change="loadQueue">
            <el-radio-button label="AM">上午</el-radio-button>
            <el-radio-button label="PM">下午</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <el-table :data="queueList" border stripe>
        <el-table-column prop="queueNumber" label="排队号" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.$index === 0 ? 'danger' : (scope.$index < 3 ? 'warning' : '')">
              {{ scope.row.queueNumber }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="patientName" label="患者姓名" align="center" />
        <el-table-column prop="registrationType" label="类型" align="center" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.registrationType === 2 ? 'danger' : ''">
              {{ scope.row.registrationType === 2 ? '急诊' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priorityScore" label="优先级分数" align="center" width="100" />
        <el-table-column label="优先级说明" align="center">
          <template #default="scope">
            <span style="font-size: 12px; color: #666;">{{ getPriorityDesc(scope.row) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, List } from '@element-plus/icons-vue'
import { getRegistrationList, addRegistration, updateRegistration, cancelRegistration, completeRegistration, markNoShow, deleteRegistration, getQueue, getSlots } from '@/api/registration'
import { getDepartmentList } from '@/api/department'
import { getPatientList } from '@/api/patient'
import { getDoctorList, getDoctorsByDepartment } from '@/api/doctor'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const allTableData = ref([])
const pageData = ref([])
const total = ref(0)
const departmentList = ref([])
const patientList = ref([])
const doctorList = ref([])
const slotsInfo = ref({})

// 排队相关
const queueDialogVisible = ref(false)
const queueList = ref([])
const queueDoctorList = ref([])
const queueQuery = reactive({
  departmentId: null,
  doctorId: null,
  date: new Date().toISOString().split('T')[0],
  timeSlot: 'AM'
})

const statusMap = { 0: '已取消', 1: '待就诊', 2: '已完成', 3: '爽约' }
const statusTypeMap = { 0: 'info', 1: 'primary', 2: 'success', 3: 'danger' }
const patientTypeMap = { 0: '普通', 1: '军人', 2: '老人', 3: '儿童' }

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  departmentId: null,
  date: null,
  status: null
})

const form = reactive({
  id: undefined,
  patientId: null,
  departmentId: null,
  doctorId: null,
  appointmentDate: '',
  timeSlot: 'AM',
  registrationType: 1,
  isMilitary: 0,
  isReturn: 0
})

const rules = {
  patientId: [{ required: true, message: '请选择患者', trigger: 'change' }],
  departmentId: [{ required: true, message: '请选择科室', trigger: 'change' }],
  appointmentDate: [{ required: true, message: '请选择预约日期', trigger: 'change' }],
  timeSlot: [{ required: true, message: '请选择时段', trigger: 'change' }]
}

const disabledDate = (time) => time.getTime() < Date.now() - 24 * 60 * 60 * 1000

const getPriorityType = (score) => {
  if (score >= 1000) return 'danger'
  if (score >= 200) return 'warning'
  return ''
}

const getAgeCategory = (patientType) => {
  if ((patientType & 4) !== 0) return '老人'
  if ((patientType & 8) !== 0) return '小孩'
  return '成年人'
}

const getPriorityDesc = (row) => {
  const parts = []
  if (row.registrationType === 2) parts.push('急诊+1000')
  // 使用位运算检查patient_type
  if ((row.patientType & 2) !== 0) parts.push('军人+30')
  if ((row.patientType & 4) !== 0) parts.push('老人+20')
  if ((row.patientType & 8) !== 0) parts.push('儿童+15')
  if (row.isReturn === 1) parts.push('复诊+50')
  return parts.length ? parts.join(', ') : '基础分100'
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getRegistrationList()
    const data = res.data || []
    allTableData.value = data
    handlePageChange()
  } finally {
    loading.value = false
  }
}

const handlePageChange = () => {
  let temp = allTableData.value
  if (queryParams.departmentId) temp = temp.filter(i => i.departmentId === queryParams.departmentId)
  if (queryParams.date) temp = temp.filter(i => i.appointmentDate === queryParams.date)
  if (queryParams.status !== null && queryParams.status !== undefined && queryParams.status !== '') {
    temp = temp.filter(i => i.status === queryParams.status)
  }
  total.value = temp.length
  const start = (queryParams.pageNum - 1) * queryParams.pageSize
  pageData.value = temp.slice(start, start + queryParams.pageSize)
}

const handleQuery = () => { queryParams.pageNum = 1; handlePageChange() }

const loadSlots = async () => {
  if (form.departmentId && form.appointmentDate) {
    const res = await getSlots(form.departmentId, form.appointmentDate)
    slotsInfo.value = res.data || {}
  }
}

const onDepartmentChange = async () => {
  form.doctorId = null  // 切换科室时清空医生选择
  doctorList.value = []
  if (form.departmentId) {
    const res = await getDoctorsByDepartment(form.departmentId)
    doctorList.value = res.data || []
  }
  loadSlots()
}
const onDateChange = () => loadSlots()

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增挂号'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  dialogTitle.value = '编辑挂号'
  Object.assign(form, row)
  // 从patientType中提取军人标记
  form.isMilitary = (row.patientType & 2) !== 0 ? 1 : 0
  loadSlots()
  dialogVisible.value = true
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      // 根据患者年龄自动计算patient_type
      const patient = patientList.value.find(p => p.id === form.patientId)
      let patientType = 0
      
      if (patient && patient.age) {
        if (patient.age >= 65) {
          patientType |= 4  // 老人
        } else if (patient.age <= 14) {
          patientType |= 8  // 儿童
        }
      }
      
      // 添加军人标记
      if (form.isMilitary === 1) {
        patientType |= 2  // 军人
      }
      
      const submitData = {
        ...form,
        patientType: patientType
      }
      
      if (form.id) {
        await updateRegistration(submitData)
        ElMessage.success('修改成功')
      } else {
        await addRegistration(submitData)
        ElMessage.success('挂号成功')
      }
      dialogVisible.value = false
      getList()
    }
  })
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确认退号吗？（需在就诊前24小时操作）', '提示', { type: 'warning' })
    .then(async () => {
      const res = await cancelRegistration(row.id)
      ElMessage.success(res.data || '退号成功')
      getList()
    })
    .catch(() => {})
}

const handleComplete = (row) => {
  ElMessageBox.confirm('确认该患者已完成就诊？', '提示', { type: 'success' })
    .then(async () => {
      await completeRegistration(row.id)
      ElMessage.success('已标记为完成')
      getList()
    })
}

const handleNoShow = (row) => {
  ElMessageBox.confirm('确认标记该患者为爽约？这将影响其未来挂号优先级', '警告', { type: 'warning' })
    .then(async () => {
      await markNoShow(row.id)
      ElMessage.warning('已标记为爽约')
      getList()
    })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该挂号记录？', '警告', { type: 'warning' })
    .then(async () => {
      await deleteRegistration(row.id)
      ElMessage.success('删除成功')
      getList()
    })
}

const showQueueDialog = async () => {
  queueDialogVisible.value = true
  if (departmentList.value.length && !queueQuery.departmentId) {
    queueQuery.departmentId = departmentList.value[0].id
    await onQueueDepartmentChange()
    if (queueDoctorList.value.length && !queueQuery.doctorId) {
      queueQuery.doctorId = queueDoctorList.value[0].id
    }
  }
  loadQueue()
}

const loadQueue = async () => {
  if (queueQuery.departmentId && queueQuery.doctorId && queueQuery.date && queueQuery.timeSlot) {
    const res = await getQueue(queueQuery.departmentId, queueQuery.date, queueQuery.timeSlot)
    queueList.value = res.data || []
  }
}

const onQueueDepartmentChange = async () => {
  queueQuery.doctorId = null
  queueDoctorList.value = []
  if (queueQuery.departmentId) {
    const res = await getDoctorsByDepartment(queueQuery.departmentId)
    queueDoctorList.value = res.data || []
  }
}

const resetForm = () => {
  form.id = undefined
  form.patientId = null
  form.departmentId = null
  form.doctorId = null
  form.appointmentDate = ''
  form.timeSlot = 'AM'
  form.registrationType = 1
  form.isMilitary = 0
  form.isReturn = 0
  slotsInfo.value = {}
  doctorList.value = []
  formRef.value?.clearValidate()
}

const loadBaseData = async () => {
  const [deptRes, patientRes, doctorRes] = await Promise.all([getDepartmentList(), getPatientList(), getDoctorList()])
  departmentList.value = deptRes.data || []
  patientList.value = patientRes.data || []
  doctorList.value = doctorRes.data || []
}

onMounted(() => {
  loadBaseData()
  getList()
})
</script>
