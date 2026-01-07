<template>
  <div class="leave-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="header-row">
              <span>请假申请</span>
              <el-button type="primary" @click="showApplyDialog">申请请假</el-button>
            </div>
          </template>

          <el-table :data="leaveList" v-loading="loading" stripe>
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
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button 
                  v-if="scope.row.status === 0" 
                  link 
                  type="danger" 
                  @click="handleCancel(scope.row)"
                >
                  取消
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <template #header>本月请假额度</template>
          <div class="quota-info">
            <div class="quota-used">
              <span class="number">{{ monthUsed }}</span>
              <span class="label">已使用</span>
            </div>
            <div class="quota-divider">/</div>
            <div class="quota-total">
              <span class="number">3</span>
              <span class="label">总额度</span>
            </div>
          </div>
          <div class="quota-remaining">
            剩余 <strong>{{ 3 - monthUsed }}</strong> 次请假机会
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 申请请假对话框 -->
    <el-dialog v-model="dialogVisible" title="申请请假" width="400px">
      <el-form :model="applyForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="请假日期" prop="leaveDate">
          <el-date-picker
            v-model="applyForm.leaveDate"
            type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="时段" prop="timeSlot">
          <el-radio-group v-model="applyForm.timeSlot">
            <el-radio label="AM">上午</el-radio>
            <el-radio label="PM">下午</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="请假原因" prop="reason">
          <el-input v-model="applyForm.reason" type="textarea" rows="3" placeholder="请输入请假原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApply" :loading="submitting">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getLeaveList, getMonthLeaveCount, applyLeave, cancelLeave } from '../../api/doctorPortal'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const leaveList = ref([])
const monthUsed = ref(0)

const applyForm = reactive({
  leaveDate: '',
  timeSlot: 'AM',
  reason: ''
})

const rules = {
  leaveDate: [{ required: true, message: '请选择请假日期', trigger: 'change' }],
  timeSlot: [{ required: true, message: '请选择时段', trigger: 'change' }]
}

const statusText = (status) => {
  const map = { 0: '待审批', 1: '已批准', 2: '已拒绝' }
  return map[status] || '未知'
}

const statusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const disabledDate = (date) => {
  // 只能选择未来一个月内的日期
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const maxDate = new Date()
  maxDate.setMonth(maxDate.getMonth() + 1)
  return date <= today || date > maxDate
}

const loadData = async () => {
  loading.value = true
  try {
    const refId = localStorage.getItem('refId')
    if (!refId) return

    const [listRes, countRes] = await Promise.all([
      getLeaveList(refId),
      getMonthLeaveCount(refId)
    ])

    if (listRes.code === '200') {
      leaveList.value = listRes.data || []
    }
    if (countRes.code === '200') {
      monthUsed.value = countRes.data || 0
    }
  } catch (error) {
    console.error('获取数据失败', error)
  } finally {
    loading.value = false
  }
}

const showApplyDialog = () => {
  if (monthUsed.value >= 3) {
    ElMessage.warning('本月请假次数已达上限')
    return
  }
  applyForm.leaveDate = ''
  applyForm.timeSlot = 'AM'
  applyForm.reason = ''
  dialogVisible.value = true
}

const submitApply = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const refId = localStorage.getItem('refId')
        const res = await applyLeave({
          doctorId: refId,
          leaveDate: applyForm.leaveDate,
          timeSlot: applyForm.timeSlot,
          reason: applyForm.reason
        })
        
        if (res.code === '200') {
          ElMessage.success(res.data || '申请已提交')
          dialogVisible.value = false
          loadData()
        } else {
          ElMessage.error(res.msg || '申请失败')
        }
      } catch (error) {
        ElMessage.error('申请失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消这个请假申请吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await cancelLeave(row.id)
      if (res.code === '200') {
        ElMessage.success('已取消')
        loadData()
      } else {
        ElMessage.error(res.msg || '取消失败')
      }
    } catch (error) {
      ElMessage.error('取消失败')
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.leave-container {
  padding: 20px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quota-info {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
}

.quota-used, .quota-total {
  text-align: center;
}

.quota-used .number {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
}

.quota-total .number {
  font-size: 36px;
  font-weight: bold;
  color: #909399;
}

.quota-divider {
  font-size: 24px;
  color: #dcdfe6;
  margin: 0 20px;
}

.label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.quota-remaining {
  text-align: center;
  color: #606266;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
}
</style>
