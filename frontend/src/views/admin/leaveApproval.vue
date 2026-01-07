<template>
  <div class="leave-approval-container">
    <el-card>
      <template #header>
        <div class="header-row">
          <span>请假审批</span>
          <el-radio-group v-model="viewMode" @change="loadData">
            <el-radio-button label="pending">待审批</el-radio-button>
            <el-radio-button label="all">全部记录</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="leaveList" v-loading="loading" stripe>
        <el-table-column prop="doctorName" label="医生姓名" width="120" />
        <el-table-column prop="leaveDate" label="请假日期" width="120" />
        <el-table-column prop="timeSlot" label="时段" width="80">
          <template #default="scope">
            {{ scope.row.timeSlot === 'AM' ? '上午' : '下午' }}
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="请假原因" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="statusType(scope.row.status)">
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="150" v-if="viewMode === 'pending'">
          <template #default="scope">
            <el-button link type="success" @click="handleApprove(scope.row, 1)">批准</el-button>
            <el-button link type="danger" @click="handleApprove(scope.row, 2)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPendingLeaves, getAllLeaves, approveLeave } from '../../api/adminLeave'

const loading = ref(false)
const viewMode = ref('pending')
const leaveList = ref([])

const statusText = (status) => {
  const map = { 0: '待审批', 1: '已批准', 2: '已拒绝' }
  return map[status] || '未知'
}

const statusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = viewMode.value === 'pending' 
      ? await getPendingLeaves() 
      : await getAllLeaves()
    
    if (res.code === '200') {
      leaveList.value = res.data || []
    }
  } catch (error) {
    console.error('获取数据失败', error)
  } finally {
    loading.value = false
  }
}

const handleApprove = (row, status) => {
  const action = status === 1 ? '批准' : '拒绝'
  ElMessageBox.confirm(`确定要${action}这个请假申请吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const userId = localStorage.getItem('userId')
      const res = await approveLeave({
        id: row.id,
        status: status,
        approverId: userId
      })
      
      if (res.code === '200') {
        ElMessage.success(res.data || `已${action}`)
        loadData()
      } else {
        ElMessage.error(res.msg || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.leave-approval-container {
  padding: 20px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
