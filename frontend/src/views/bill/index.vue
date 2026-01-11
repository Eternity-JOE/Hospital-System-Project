<template>
  <div class="app-container" style="padding: 20px;">
    <el-card shadow="never" class="mb-20">
      <el-row :gutter="20" justify="space-between" align="middle">
        <el-col :span="16">
          <el-space>
            <el-select v-model="queryParams.status" placeholder="支付状态" clearable style="width: 120px" @change="handleQuery">
              <el-option label="待缴费" :value="0" />
              <el-option label="已缴费" :value="1" />
            </el-select>
            <el-date-picker v-model="queryParams.date" type="date" placeholder="选择日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" @change="handleQuery" clearable />
          </el-space>
        </el-col>
        <el-col :span="8" style="text-align: right;">
          <span style="font-size: 14px; color: #606266;">
            总收入：<span style="color: #67c23a; font-weight: bold; font-size: 18px;">￥{{ totalRevenue.toFixed(2) }}</span>
          </span>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px;">
      <el-table :data="pageData" border stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="账单号" width="80" align="center" />
        <el-table-column prop="doctorName" label="开单医生" width="100" align="center" />
        <el-table-column prop="patientName" label="患者姓名" width="100" align="center" />
        <el-table-column prop="diagnosis" label="诊断信息" align="center" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.diagnosis || '无' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="金额" align="center" width="100">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold;">￥{{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="开单时间" align="center" width="160" />
        <el-table-column prop="status" label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '已缴费' : '待缴费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="缴费时间" align="center" width="160">
          <template #default="scope">
            {{ scope.row.payTime || '-' }}
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getBillList } from '@/api/bill'
import { getDoctorList } from '@/api/doctor'
import { getPatientList } from '@/api/patient'

const loading = ref(false)
const allTableData = ref([])
const pageData = ref([])
const total = ref(0)
const doctorList = ref([])
const patientList = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: null,
  date: null
})

const totalRevenue = computed(() => {
  return allTableData.value
    .filter(b => b.status === 1)
    .reduce((sum, b) => sum + Number(b.totalAmount || 0), 0)
})

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

const getList = async () => {
  loading.value = true
  try {
    const res = await getBillList()
    const data = res.data || []
    
    // 补充医生和患者姓名
    allTableData.value = data.map(item => ({
      ...item,
      doctorName: getDoctorName(item.doctorId),
      patientName: getPatientName(item.patientId),
      createTime: formatTime(item.createTime),
      payTime: formatTime(item.payTime)
    }))
    
    handlePageChange()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getDoctorName = (doctorId) => {
  if (!doctorId) return '-'
  const doctor = doctorList.value.find(d => d.id === doctorId)
  return doctor ? doctor.name : `医生${doctorId}`
}

const getPatientName = (patientId) => {
  if (!patientId) return '-'
  const patient = patientList.value.find(p => p.id === patientId)
  return patient ? patient.name : `患者${patientId}`
}

const handlePageChange = () => {
  let temp = allTableData.value
  
  // 筛选状态
  if (queryParams.status !== null && queryParams.status !== undefined && queryParams.status !== '') {
    temp = temp.filter(i => i.status === queryParams.status)
  }
  
  // 筛选日期
  if (queryParams.date) {
    temp = temp.filter(i => i.createTime && i.createTime.startsWith(queryParams.date))
  }
  
  total.value = temp.length
  const start = (queryParams.pageNum - 1) * queryParams.pageSize
  pageData.value = temp.slice(start, start + queryParams.pageSize)
}

const handleQuery = () => {
  queryParams.pageNum = 1
  handlePageChange()
}

const loadBaseData = async () => {
  try {
    const [doctorRes, patientRes] = await Promise.all([
      getDoctorList(),
      getPatientList()
    ])
    doctorList.value = doctorRes.data || []
    patientList.value = patientRes.data || []
  } catch (error) {
    console.error('加载基础数据失败:', error)
  }
}

onMounted(async () => {
  await loadBaseData()
  getList()
})
</script>
