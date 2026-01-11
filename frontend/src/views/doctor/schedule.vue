<template>
  <div class="schedule-container">
    <el-card>
      <template #header>
        <div class="header-row">
          <span>日程安排</span>
          <div class="week-nav">
            <el-button @click="prevWeek" :icon="ArrowLeft" />
            <span class="week-label">{{ weekLabel }}</span>
            <el-button @click="nextWeek" :icon="ArrowRight" />
            <el-button @click="goToday" style="margin-left: 10px">今天</el-button>
          </div>
        </div>
      </template>

      <div class="calendar-grid" v-loading="loading">
        <div class="calendar-header">
          <div class="time-column"></div>
          <div class="day-column" v-for="day in weekDays" :key="day.date" :class="{ today: day.isToday, 'rest-day': isRestDay(day.date) }">
            <div class="day-name">{{ day.dayName }}</div>
            <div class="day-date">{{ day.dateStr }}</div>
            <div v-if="isRestDay(day.date)" class="rest-badge">休息</div>
          </div>
        </div>
        
        <div class="calendar-body">
          <!-- 上午 -->
          <div class="time-row">
            <div class="time-label">上午</div>
            <div 
              class="day-cell" 
              v-for="day in weekDays" 
              :key="'am-' + day.date" 
              :class="{ today: day.isToday, 'on-leave': isOnLeave(day.date, 'AM'), 'rest-day': isRestDay(day.date) }"
            >
              <template v-if="isRestDay(day.date)">
                <div class="rest-badge-cell">
                  <el-icon><Sunny /></el-icon>
                  <span>休息日</span>
                </div>
              </template>
              <template v-else-if="isOnLeave(day.date, 'AM')">
                <div class="leave-badge">
                  <el-icon><CircleClose /></el-icon>
                  <span>已请假</span>
                </div>
              </template>
              <template v-else>
                <div v-for="item in getAppointments(day.date, 'AM')" :key="item.id" class="appointment-card">
                  <div class="patient-name">{{ item.patientName }}</div>
                  <div class="patient-info">{{ item.patientGender }} · {{ item.patientAge }}岁</div>
                  <div class="patient-phone">{{ item.patientPhone }}</div>
                </div>
                <div v-if="getAppointments(day.date, 'AM').length === 0" class="no-appointment">
                  暂无预约
                </div>
              </template>
            </div>
          </div>
          
          <!-- 下午 -->
          <div class="time-row">
            <div class="time-label">下午</div>
            <div 
              class="day-cell" 
              v-for="day in weekDays" 
              :key="'pm-' + day.date" 
              :class="{ today: day.isToday, 'on-leave': isOnLeave(day.date, 'PM'), 'rest-day': isRestDay(day.date) }"
            >
              <template v-if="isRestDay(day.date)">
                <div class="rest-badge-cell">
                  <el-icon><Sunny /></el-icon>
                  <span>休息日</span>
                </div>
              </template>
              <template v-else-if="isOnLeave(day.date, 'PM')">
                <div class="leave-badge">
                  <el-icon><CircleClose /></el-icon>
                  <span>已请假</span>
                </div>
              </template>
              <template v-else>
                <div v-for="item in getAppointments(day.date, 'PM')" :key="item.id" class="appointment-card">
                  <div class="patient-name">{{ item.patientName }}</div>
                  <div class="patient-info">{{ item.patientGender }} · {{ item.patientAge }}岁</div>
                  <div class="patient-phone">{{ item.patientPhone }}</div>
                </div>
                <div v-if="getAppointments(day.date, 'PM').length === 0" class="no-appointment">
                  暂无预约
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>

      <!-- 图例 -->
      <div class="legend">
        <span class="legend-item"><span class="legend-color appointment"></span> 病人预约</span>
        <span class="legend-item"><span class="legend-color rest"></span> 休息日</span>
        <span class="legend-item"><span class="legend-color leave"></span> 已请假</span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ArrowLeft, ArrowRight, CircleClose, Sunny } from '@element-plus/icons-vue'
import { getWeekSchedule } from '../../api/doctorPortal'
import { getDoctorProfile } from '../../api/doctor'

const loading = ref(false)
const currentWeekStart = ref(getMonday(new Date()))
const appointments = ref([])
const leaves = ref([])
const doctorSchedule = ref(null) // 医生的排班信息

const dayNames = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

function getMonday(date) {
  const d = new Date(date)
  const day = d.getDay()
  const diff = d.getDate() - day + (day === 0 ? -6 : 1)
  return new Date(d.setDate(diff))
}

function formatDate(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const weekDays = computed(() => {
  const days = []
  const today = formatDate(new Date())
  for (let i = 0; i < 7; i++) {
    const d = new Date(currentWeekStart.value)
    d.setDate(d.getDate() + i)
    days.push({
      date: formatDate(d),
      dateStr: `${d.getMonth() + 1}/${d.getDate()}`,
      dayName: dayNames[i],
      dayOfWeek: i + 1, // 1-7 表示周一到周日
      isToday: formatDate(d) === today
    })
  }
  return days
})

const weekLabel = computed(() => {
  const start = weekDays.value[0]
  const end = weekDays.value[6]
  return `${start.dateStr} - ${end.dateStr}`
})

const getAppointments = (date, timeSlot) => {
  return appointments.value.filter(a => a.appointmentDate === date && a.timeSlot === timeSlot)
}

const isOnLeave = (date, timeSlot) => {
  return leaves.value.some(l => l.leaveDate === date && l.timeSlot === timeSlot)
}

// 判断某天是否是休息日
const isRestDay = (date) => {
  if (!doctorSchedule.value) return false
  
  const d = new Date(date)
  const dayOfWeek = d.getDay() || 7 // 0是周日，转为7
  
  // 动态排班：休息日是今天往后第4天和第6天
  if (doctorSchedule.value === 'DYNAMIC') {
    const today = new Date()
    const todayDow = today.getDay() || 7 // 1-7
    
    // 计算往后第4天和第6天是星期几
    const rest1 = ((todayDow - 1 + 4) % 7) + 1 // 往后第4天
    const rest2 = ((todayDow - 1 + 6) % 7) + 1 // 往后第6天
    
    return dayOfWeek === rest1 || dayOfWeek === rest2
  }
  
  // 普通医生：检查schedule字段
  const workDays = doctorSchedule.value.split(',').map(d => parseInt(d.trim()))
  return !workDays.includes(dayOfWeek)
}

const loadSchedule = async () => {
  loading.value = true
  try {
    const refId = localStorage.getItem('refId')
    if (!refId) return
    
    const startDate = weekDays.value[0].date
    const endDate = weekDays.value[6].date
    const res = await getWeekSchedule(refId, startDate, endDate)
    if (res.code === '200' && res.data) {
      appointments.value = res.data.appointments || []
      leaves.value = res.data.leaves || []
    }
  } catch (error) {
    console.error('获取日程失败', error)
  } finally {
    loading.value = false
  }
}

// 加载医生排班信息
const loadDoctorSchedule = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (!userId) return
    
    const res = await getDoctorProfile(userId)
    if (res.code === '200' && res.data) {
      doctorSchedule.value = res.data.schedule
    }
  } catch (error) {
    console.error('获取医生排班失败', error)
  }
}

const prevWeek = () => {
  const d = new Date(currentWeekStart.value)
  d.setDate(d.getDate() - 7)
  currentWeekStart.value = d
  loadSchedule()
}

const nextWeek = () => {
  const d = new Date(currentWeekStart.value)
  d.setDate(d.getDate() + 7)
  // 限制只能看未来一个月
  const maxDate = new Date()
  maxDate.setMonth(maxDate.getMonth() + 1)
  if (d <= maxDate) {
    currentWeekStart.value = d
    loadSchedule()
  }
}

const goToday = () => {
  currentWeekStart.value = getMonday(new Date())
  loadSchedule()
}

onMounted(() => {
  loadDoctorSchedule()
  loadSchedule()
})
</script>

<style scoped>
.schedule-container {
  padding: 20px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.week-nav {
  display: flex;
  align-items: center;
  gap: 10px;
}

.week-label {
  min-width: 120px;
  text-align: center;
  font-weight: 500;
}

.calendar-grid {
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.calendar-header {
  display: flex;
  background: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

.time-column {
  width: 80px;
  flex-shrink: 0;
}

.day-column {
  flex: 1;
  text-align: center;
  padding: 10px;
  border-left: 1px solid #ebeef5;
}

.day-column.today {
  background: #ecf5ff;
}

.day-name {
  font-weight: 500;
  color: #303133;
}

.day-date {
  font-size: 12px;
  color: #909399;
}

.calendar-body {
  min-height: 300px;
}

.time-row {
  display: flex;
  border-bottom: 1px solid #ebeef5;
}

.time-row:last-child {
  border-bottom: none;
}

.time-label {
  width: 80px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  font-weight: 500;
  color: #606266;
}

.day-cell {
  flex: 1;
  min-height: 150px;
  padding: 8px;
  border-left: 1px solid #ebeef5;
}

.day-cell.today {
  background: #f0f9ff;
}

.day-cell.on-leave {
  background: #fef0f0;
}

.day-cell.rest-day {
  background: #f0f9eb;
}

.day-column.rest-day {
  background: #f0f9eb;
}

.rest-badge {
  display: inline-block;
  background: #67c23a;
  color: white;
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 10px;
  margin-top: 3px;
}

.rest-badge-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 130px;
  color: #67c23a;
  font-size: 14px;
}

.rest-badge-cell .el-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.appointment-card {
  background: #409eff;
  color: white;
  padding: 8px;
  border-radius: 4px;
  margin-bottom: 5px;
  font-size: 12px;
}

.patient-name {
  font-weight: 500;
  margin-bottom: 3px;
}

.patient-info, .patient-phone {
  opacity: 0.9;
}

.no-appointment {
  color: #c0c4cc;
  font-size: 12px;
  text-align: center;
  padding-top: 50px;
}

.leave-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 130px;
  color: #f56c6c;
  font-size: 14px;
}

.leave-badge .el-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.legend {
  margin-top: 15px;
  display: flex;
  gap: 20px;
  justify-content: flex-end;
}

.legend-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #606266;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 3px;
  margin-right: 5px;
}

.legend-color.appointment {
  background: #409eff;
}

.legend-color.leave {
  background: #fef0f0;
  border: 1px solid #f56c6c;
}

.legend-color.rest {
  background: #f0f9eb;
  border: 1px solid #67c23a;
}
</style>
