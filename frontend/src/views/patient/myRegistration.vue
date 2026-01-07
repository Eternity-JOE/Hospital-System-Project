<template>
  <div class="my-registration-container">
    <el-card>
      <template #header>
        <span>我的挂号记录</span>
      </template>

      <el-table :data="registrationList" v-loading="loading" stripe>
        <el-table-column prop="appointmentDate" label="预约日期" width="120" />
        <el-table-column prop="timeSlot" label="时段" width="80">
          <template #default="scope">
            <el-tag size="small">{{ scope.row.timeSlot === 'AM' ? '上午' : '下午' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="科室" width="120" />
        <el-table-column prop="doctorName" label="医生" width="100" />
        <el-table-column prop="doctorTitle" label="职称" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="statusTypeMap[scope.row.status]">
              {{ statusMap[scope.row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 1" 
              link 
              type="primary" 
              @click="handleViewQueue(scope.row)"
            >
              查看排队
            </el-button>
            <el-button 
              v-if="scope.row.status === 1" 
              link 
              type="danger" 
              @click="handleCancel(scope.row)"
            >
              取消挂号
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 排队情况对话框 -->
    <el-dialog v-model="queueDialogVisible" title="排队情况" width="400px" :close-on-click-modal="false">
      <div v-loading="queueLoading">
        <div v-if="queueList.length === 0" style="text-align: center; color: #909399;">
          当前时段暂无排队患者
        </div>
        <div v-else>
          <p style="margin-bottom: 15px; color: #606266;">
            <strong>{{ selectedRegistration?.doctorName }}</strong> - {{ selectedRegistration?.appointmentDate }} {{ selectedRegistration?.timeSlot === 'AM' ? '上午' : '下午' }}
          </p>
          <el-table :data="queueList" stripe size="small">
            <el-table-column prop="queueNumber" label="排队号" width="80" align="center" />
            <el-table-column prop="patientName" label="患者" />
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyRegistrations, cancelRegistration, getQueue } from '../../api/patientRegistration'

const loading = ref(false)
const queueLoading = ref(false)
const queueDialogVisible = ref(false)
const registrationList = ref([])
const queueList = ref([])
const selectedRegistration = ref(null)

const statusMap = { 0: '已取消', 1: '待就诊', 2: '已完成', 3: '爽约' }
const statusTypeMap = { 0: 'info', 1: 'primary', 2: 'success', 3: 'danger' }

const loadData = async () => {
  const refId = localStorage.getItem('refId')
  if (!refId) {
    ElMessage.warning('请先完善个人信息')
    return
  }
  
  loading.value = true
  try {
    const res = await getMyRegistrations(refId)
    if (res.code === '200') {
      registrationList.value = res.data || []
    }
  } catch (error) {
    console.error('获取挂号记录失败', error)
  } finally {
    loading.value = false
  }
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消这个挂号吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await cancelRegistration(row.id)
      if (res.code === '200') {
        ElMessage.success('取消成功')
        loadData()
      } else {
        ElMessage.error(res.msg || '取消失败')
      }
    } catch (error) {
      ElMessage.error('取消失败')
    }
  })
}

const handleViewQueue = async (row) => {
  selectedRegistration.value = row
  queueLoading.value = true
  
  try {
    const refId = localStorage.getItem('refId')
    const res = await getQueue({
      doctorId: row.doctorId,
      date: row.appointmentDate,
      timeSlot: row.timeSlot,
      patientId: parseInt(refId)
    })
    
    if (res.code === '200') {
      queueList.value = res.data || []
      queueDialogVisible.value = true
    } else {
      ElMessage.error('获取排队信息失败')
    }
  } catch (error) {
    ElMessage.error('获取排队信息失败')
  } finally {
    queueLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.my-registration-container {
  padding: 20px;
}
</style>
