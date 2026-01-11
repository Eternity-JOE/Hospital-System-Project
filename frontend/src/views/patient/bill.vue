<template>
  <div class="app-container" style="padding: 20px;">
    <el-card shadow="never">
      <template #header>
        <span style="font-weight: bold;">我的账单</span>
      </template>
      
      <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading" empty-text="暂无缴费记录">
        <el-table-column prop="id" label="账单号" width="80" align="center" />
        <el-table-column prop="doctorName" label="开单医生" align="center" width="100" />
        <el-table-column prop="diagnosis" label="诊断信息" align="center" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.diagnosis || '无' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="开单时间" align="center" width="160" />
        <el-table-column prop="totalAmount" label="金额" align="center" width="100">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold;">{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '已缴费' : '待缴费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="120">
          <template #default="scope">
            <el-button v-if="scope.row.status === 0" type="primary" size="small" @click="openPayDialog(scope.row)">
              去缴费
            </el-button>
            <span v-else style="color: #909399; font-size: 12px;">已完成</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 支付弹窗 -->
    <el-dialog v-model="payDialogVisible" title="扫码支付" width="400px" :close-on-click-modal="false" center>
      <div class="pay-dialog-content">
        <div class="pay-amount">
          <span>支付金额：</span>
          <span class="amount">{{ currentBill?.totalAmount }}</span>
        </div>
        <div class="qrcode-container">
          <img src="/qrcode.png" alt="支付二维码" class="qrcode-img" />
          <p class="qrcode-tip">请使用微信或支付宝扫码支付</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消支付</el-button>
        <el-button type="primary" @click="confirmPay" :loading="payLoading">支付完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPatientBillList, payPatientBill } from '@/api/bill'
import { getDoctorList } from '@/api/doctor'

const loading = ref(false)
const payLoading = ref(false)
const tableData = ref([])
const doctorList = ref([])

const payDialogVisible = ref(false)
const currentBill = ref(null)

const loadDoctorList = async () => {
  try {
    const res = await getDoctorList()
    if (res.code === '200' && res.data) {
      doctorList.value = res.data
    }
  } catch (error) {
    console.error('获取医生列表失败:', error)
  }
}

const getDoctorName = (doctorId) => {
  if (!doctorId) return '未知医生'
  const doctor = doctorList.value.find(item => item.id === doctorId)
  return doctor ? doctor.name : `医生(${doctorId})`
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

const loadPatientBills = async () => {
  const patientId = localStorage.getItem('refId') || ''
  if (!patientId) {
    ElMessage.warning('请先登录')
    return
  }

  loading.value = true
  try {
    const res = await getPatientBillList(patientId)
    if (res.code === '200' && res.data) {
      tableData.value = res.data.map(item => ({
        ...item,
        doctorName: getDoctorName(item.doctorId),
        createTime: formatTime(item.createTime),
        totalAmount: `¥${Number(item.totalAmount).toFixed(2)}`
      }))
    } else {
      tableData.value = []
    }
  } catch (error) {
    console.error('加载账单失败:', error)
    ElMessage.error('加载账单失败')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const openPayDialog = (row) => {
  currentBill.value = row
  payDialogVisible.value = true
}

const confirmPay = async () => {
  if (!currentBill.value) return
  
  payLoading.value = true
  try {
    const res = await payPatientBill(currentBill.value.id)
    if (res.code === '200') {
      ElMessage.success('缴费成功！')
      payDialogVisible.value = false
      loadPatientBills()
    } else {
      ElMessage.error(res.msg || '缴费失败')
    }
  } catch (error) {
    console.error('缴费失败:', error)
    ElMessage.error('缴费失败，请重试')
  } finally {
    payLoading.value = false
  }
}

onMounted(() => {
  loadDoctorList().then(() => {
    loadPatientBills()
  })
})
</script>

<style scoped>
.pay-dialog-content {
  text-align: center;
  padding: 20px 0;
}

.pay-amount {
  font-size: 16px;
  margin-bottom: 20px;
}

.pay-amount .amount {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
}

.qrcode-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.qrcode-img {
  width: 200px;
  height: 200px;
  border: 1px solid #eee;
  border-radius: 8px;
}

.qrcode-tip {
  margin-top: 15px;
  color: #909399;
  font-size: 14px;
}
</style>
