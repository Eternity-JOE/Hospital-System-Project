<template>
  <div class="app-container" style="padding: 20px;">
    <!-- Tab切换 -->
    <el-tabs v-model="activeTab" type="card" @tab-change="onTabChange">
      <el-tab-pane label="今日问诊" name="today">
        <el-card shadow="never" class="mb-20">
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div style="font-weight: bold; font-size: 16px;">今日问诊列表 - {{ today }}</div>
            <el-radio-group v-model="timeSlot" @change="loadTodayRegistrations">
              <el-radio-button label="AM">上午</el-radio-button>
              <el-radio-button label="PM">下午</el-radio-button>
            </el-radio-group>
          </div>
        </el-card>

        <el-card shadow="never" style="margin-top: 20px;">
          <el-table :data="registrationList" border stripe style="width: 100%" v-loading="loading" empty-text="今日暂无挂号">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="patientName" label="患者姓名" width="100" align="center" />
            <el-table-column prop="priorityScore" label="优先级" width="80" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.priorityScore >= 1000 ? 'danger' : (scope.row.priorityScore >= 150 ? 'warning' : '')">
                  {{ scope.row.priorityScore }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="registrationType" label="类型" width="80" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.registrationType === 2 ? 'danger' : ''">
                  {{ scope.row.registrationType === 2 ? '急诊' : '普通' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center">
              <template #default="scope">
                <el-button type="primary" size="small" @click="openDiagnoseDialog(scope.row)" :disabled="scope.row.status !== 1">
                  问诊
                </el-button>
                <el-button type="info" size="small" @click="viewPatientInfo(scope.row)">
                  查看信息
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="账单历史" name="history">
        <el-card shadow="never">
          <el-table :data="billList" border stripe style="width: 100%" v-loading="billLoading" empty-text="暂无账单记录">
            <el-table-column prop="id" label="账单号" width="80" align="center" />
            <el-table-column prop="patientName" label="患者姓名" width="100" align="center" />
            <el-table-column prop="diagnosis" label="诊断信息" align="center" show-overflow-tooltip>
              <template #default="scope">
                {{ scope.row.diagnosis || '无' }}
              </template>
            </el-table-column>
            <el-table-column prop="totalAmount" label="金额" width="100" align="center">
              <template #default="scope">
                <span style="color: #f56c6c; font-weight: bold;">￥{{ scope.row.totalAmount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="开单时间" width="160" align="center" />
            <el-table-column prop="status" label="支付状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                  {{ scope.row.status === 1 ? '已缴费' : '待缴费' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="payTime" label="缴费时间" width="160" align="center">
              <template #default="scope">
                {{ scope.row.payTime || '-' }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 问诊对话框 -->
    <el-dialog v-model="diagnoseDialogVisible" title="问诊诊断" width="650px" :close-on-click-modal="false">
      <el-form :model="diagnoseForm" label-width="100px">
        <el-form-item label="患者姓名">
          <span style="font-weight: bold;">{{ currentPatient?.patientName }}</span>
        </el-form-item>
        <el-form-item label="诊断简评">
          <el-input v-model="diagnoseForm.diagnosis" type="textarea" :rows="3" placeholder="请输入诊断信息（非必填）" />
        </el-form-item>
        <el-divider content-position="left">开药处方</el-divider>
        <div v-for="(med, index) in diagnoseForm.medicines" :key="index" class="medicine-row">
          <el-row :gutter="10" align="middle">
            <el-col :span="10">
              <el-form-item :label="'药品' + (index + 1)" label-width="70px">
                <el-select v-model="med.medicineId" filterable placeholder="搜索或选择药品" style="width: 100%" @change="onMedicineChange(index)">
                  <el-option v-for="m in medicineList" :key="m.id" :label="`${m.name} (￥${m.price}/${m.unit} 库存:${m.stock})`" :value="m.id" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="数量" label-width="50px">
                <el-input-number v-model="med.quantity" :min="1" :max="getMaxStock(med.medicineId)" size="small" />
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <span v-if="med.medicineId" style="color: #f56c6c;">￥{{ getMedicineSubtotal(med) }}</span>
            </el-col>
            <el-col :span="4">
              <el-button v-if="index > 0" type="danger" size="small" @click="removeMedicine(index)">删除</el-button>
            </el-col>
          </el-row>
        </div>
        <el-button type="default" size="small" @click="addMedicine" :disabled="diagnoseForm.medicines.length >= 5">
          + 增加药品
        </el-button>
        <el-form-item label="挂号费" style="margin-top: 15px;">
          <span style="color: #909399;">￥{{ registrationFee.toFixed(2) }}（固定）</span>
        </el-form-item>
        <el-divider />
        <el-form-item label="费用合计">
          <span style="font-size: 18px; font-weight: bold; color: #f56c6c;">￥{{ totalAmount.toFixed(2) }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="diagnoseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDiagnose" :loading="submitting">确认诊断并生成账单</el-button>
      </template>
    </el-dialog>

    <!-- 患者信息对话框 -->
    <el-dialog v-model="patientInfoDialogVisible" title="患者详细信息" width="450px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="姓名">{{ patientInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ patientInfo.gender }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ patientInfo.age }} 岁</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ patientInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="住址">{{ patientInfo.address || '未填写' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { getMedicineList } from '@/api/medicine'
import { getPatientList } from '@/api/patient'

const doctorId = ref(localStorage.getItem('refId') || '')
const today = new Date().toISOString().split('T')[0]
const timeSlot = ref(new Date().getHours() < 12 ? 'AM' : 'PM')

const activeTab = ref('today')
const loading = ref(false)
const billLoading = ref(false)
const submitting = ref(false)
const registrationList = ref([])
const medicineList = ref([])
const billList = ref([])
const patientList = ref([])

const diagnoseDialogVisible = ref(false)
const patientInfoDialogVisible = ref(false)
const currentPatient = ref(null)
const patientInfo = ref({})

const diagnoseForm = reactive({
  diagnosis: '',
  medicines: [{ medicineId: null, quantity: 1 }]
})

// 固定挂号费15元
const registrationFee = 15

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'primary', 2: 'success', 3: 'danger' }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = { 0: '已取消', 1: '待就诊', 2: '已完成', 3: '爽约' }
  return map[status] || '未知'
}

const getMaxStock = (medicineId) => {
  if (!medicineId) return 100
  const med = medicineList.value.find(m => m.id === medicineId)
  return med ? med.stock : 100
}

const getMedicineSubtotal = (med) => {
  if (!med.medicineId) return '0.00'
  const medicine = medicineList.value.find(m => m.id === med.medicineId)
  if (!medicine) return '0.00'
  return (medicine.price * med.quantity).toFixed(2)
}

const totalAmount = computed(() => {
  let total = registrationFee // 固定挂号费
  diagnoseForm.medicines.forEach(med => {
    if (med.medicineId) {
      const medicine = medicineList.value.find(m => m.id === med.medicineId)
      if (medicine) {
        total += medicine.price * med.quantity
      }
    }
  })
  return total
})

const onMedicineChange = (index) => {
  const med = diagnoseForm.medicines[index]
  const maxStock = getMaxStock(med.medicineId)
  if (med.quantity > maxStock) {
    med.quantity = maxStock
  }
}

const addMedicine = () => {
  if (diagnoseForm.medicines.length < 5) {
    diagnoseForm.medicines.push({ medicineId: null, quantity: 1 })
  } else {
    ElMessage.warning('最多只能开5种药品')
  }
}

const removeMedicine = (index) => {
  diagnoseForm.medicines.splice(index, 1)
}

const loadTodayRegistrations = async () => {
  if (!doctorId.value) {
    ElMessage.warning('未获取到医生ID，请重新登录')
    return
  }
  loading.value = true
  try {
    const res = await request({
      url: '/api/doctor/schedule/today',
      method: 'get',
      params: { doctorId: doctorId.value, date: today, timeSlot: timeSlot.value }
    })
    if (res.code === '200') {
      registrationList.value = res.data || []
    }
  } catch (error) {
    console.error('获取今日挂号失败:', error)
  } finally {
    loading.value = false
  }
}

const loadMedicineList = async () => {
  try {
    const res = await getMedicineList()
    if (res.code === '200') {
      medicineList.value = res.data.filter(m => m.status === 1 && m.stock > 0) || []
    }
  } catch (error) {
    console.error('获取药品列表失败:', error)
  }
}

const loadPatientList = async () => {
  try {
    const res = await getPatientList()
    if (res.code === '200') {
      patientList.value = res.data || []
    }
  } catch (error) {
    console.error('获取患者列表失败:', error)
  }
}

const getPatientName = (patientId) => {
  if (!patientId) return '-'
  const patient = patientList.value.find(p => p.id === patientId)
  return patient ? patient.name : `患者${patientId}`
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  try {
    const date = new Date(timeStr)
    const year = date.getFullYear()
    const month = (date.getMonth() + 1).toString().padStart(2, '0')
    const day = date.getDate().toString().padStart(2, '0')
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch (err) {
    return timeStr
  }
}

const loadBillHistory = async () => {
  if (!doctorId.value) return
  billLoading.value = true
  try {
    const res = await request({
      url: '/bill/doctor/list',
      method: 'get',
      params: { doctorId: doctorId.value }
    })
    if (res.code === '200') {
      billList.value = (res.data || []).map(item => ({
        ...item,
        patientName: getPatientName(item.patientId),
        createTime: formatTime(item.createTime),
        payTime: formatTime(item.payTime)
      }))
    }
  } catch (error) {
    console.error('获取账单历史失败:', error)
  } finally {
    billLoading.value = false
  }
}

const onTabChange = (tab) => {
  if (tab === 'history') {
    loadBillHistory()
  }
}

const openDiagnoseDialog = (row) => {
  currentPatient.value = row
  diagnoseForm.diagnosis = ''
  diagnoseForm.medicines = [{ medicineId: null, quantity: 1 }]
  diagnoseDialogVisible.value = true
}

const viewPatientInfo = async (row) => {
  try {
    const res = await request({
      url: '/patient/' + row.patientId,
      method: 'get'
    })
    if (res.code === '200' && res.data) {
      patientInfo.value = res.data
      patientInfoDialogVisible.value = true
    } else {
      ElMessage.error('获取患者信息失败')
    }
  } catch (error) {
    ElMessage.error('获取患者信息失败')
  }
}

const submitDiagnose = async () => {
  // 验证至少开了一种药（挂号费固定存在，所以可以不开药）
  // 移除强制开药验证，因为有固定挂号费

  // 验证库存
  for (const med of diagnoseForm.medicines) {
    if (med.medicineId) {
      const medicine = medicineList.value.find(m => m.id === med.medicineId)
      if (medicine && med.quantity > medicine.stock) {
        ElMessage.error(`药品"${medicine.name}"库存不足，当前库存：${medicine.stock}`)
        return
      }
    }
  }

  submitting.value = true
  try {
    const billData = {
      doctorId: Number(doctorId.value),
      patientId: currentPatient.value.patientId,
      registrationId: currentPatient.value.id,
      diagnosis: diagnoseForm.diagnosis,
      medicines: diagnoseForm.medicines.filter(m => m.medicineId).map(m => ({
        medicineId: m.medicineId,
        quantity: m.quantity
      })),
      registrationFee: registrationFee,
      totalAmount: totalAmount.value
    }

    const res = await request({
      url: '/bill/diagnose',
      method: 'post',
      data: billData
    })

    if (res.code === '200') {
      ElMessage.success('诊断完成，账单已生成！')
      diagnoseDialogVisible.value = false
      loadTodayRegistrations()
      loadMedicineList() // 刷新药品库存
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('提交诊断失败:', error)
    ElMessage.error('系统异常，请稍后重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadPatientList().then(() => {
    loadTodayRegistrations()
  })
  loadMedicineList()
})
</script>

<style scoped>
.medicine-row {
  margin-bottom: 10px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>
