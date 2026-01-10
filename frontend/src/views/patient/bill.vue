<template>
  <div class="app-container">
    <el-table
      :data="tableData"
      border
      stripe
      style="width: 100%"
      v-loading="loading"
      empty-text="暂无缴费记录"
    >
      <el-table-column prop="doctorName" label="医生" align="center" width="150"></el-table-column>
      <el-table-column prop="createTime" label="缴费开始时间" align="center" width="180"></el-table-column>
      <el-table-column prop="totalAmount" label="总价" align="center" width="120"></el-table-column>
      <el-table-column label="缴费" align="center" width="100">
        <template #default="scope">
          <el-button
            v-if="scope.row.status === 0"
            type="primary"
            size="small"
            @click="goToPayment(scope.row)"
            :loading="payLoading && currentPayId === scope.row.id"
          >
            缴费
          </el-button>
          <span v-else style="color: #909399; font-size: 12px;">已缴费</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" align="center" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.status === 1 ? '完成缴费' : '未缴费' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPatientBillList, payPatientBill } from '@/api/bill'
import { getDoctorList } from '@/api/doctor'

const loading = ref(false)
const payLoading = ref(false)
const currentPayId = ref(null)
const tableData = ref([])
const doctorList = ref([])

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
  if (doctor) {
    return doctor.name || `医生(${doctorId})`
  }
  return `医生(${doctorId})`
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
    ElMessage.warning('请先登录或选择病人')
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
      ElMessage.info(res.message || '暂无缴费记录')
    }
  } catch (error) {
    console.error('加载真实账单失败:', error)
    ElMessage.error('加载账单失败，请检查网络连接')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const goToPayment = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要支付 ${row.totalAmount} 给 ${row.doctorName} 医生吗？`,
      '缴费确认',
      {
        confirmButtonText: '确定支付',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    currentPayId.value = row.id
    payLoading.value = true

    try {
      const res = await payPatientBill(row.id)

      if (res.code === '200') {
        window.open('https://www.baidu.com', '_blank')

        setTimeout(() => {
          const index = tableData.value.findIndex(item => item.id === row.id)
          if (index !== -1) {
            tableData.value[index].status = 1
          }
          ElMessage.success('缴费成功！')
        }, 100)
      } else {
        ElMessage.error('缴费失败：' + (res.message || '未知错误'))
      }
    } catch (error) {
      console.error('调用真实缴费接口失败:', error)
      ElMessage.error('缴费接口调用失败，请检查网络或联系管理员')
    }
  } catch (error) {
  } finally {
    payLoading.value = false
    currentPayId.value = null
  }
}

onMounted(() => {
  loadDoctorList().then(() => {
    loadPatientBills()
  })
})

const reloadBills = () => {
  loadPatientBills()
}

defineExpose({
  reloadBills
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>