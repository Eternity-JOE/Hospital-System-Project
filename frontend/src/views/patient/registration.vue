<template>
  <div class="registration-container">
    <!-- 挂号方式选择 -->
    <el-card class="mode-card">
      <el-radio-group v-model="mode" size="large" @change="handleModeChange">
        <el-radio-button label="free">
          <el-icon><Search /></el-icon> 自由挂号
        </el-radio-button>
        <el-radio-button label="target">
          <el-icon><Aim /></el-icon> 精准挂号
        </el-radio-button>
      </el-radio-group>
      <span class="mode-desc">
        {{ mode === 'free' ? '按科室浏览所有医生，自由选择' : '根据病种匹配专业医生' }}
      </span>
    </el-card>

    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :inline="true">
        <!-- 自由挂号：选科室 -->
        <el-form-item v-if="mode === 'free'" label="选择科室">
          <el-select v-model="filters.departmentId" placeholder="全部科室" clearable @change="loadDoctors">
            <el-option v-for="d in departmentList" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        
        <!-- 精准挂号：选病种 -->
        <el-form-item v-if="mode === 'target'" label="选择病种">
          <el-select v-model="filters.diseaseId" placeholder="请选择病种" filterable @change="loadDoctors">
            <el-option-group v-for="dept in groupedDiseases" :key="dept.id" :label="dept.name">
              <el-option v-for="disease in dept.diseases" :key="disease.id" :label="disease.name" :value="disease.id" />
            </el-option-group>
          </el-select>
        </el-form-item>

        <el-form-item label="预约日期">
          <el-date-picker
            v-model="filters.date"
            type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
            value-format="YYYY-MM-DD"
            @change="loadDoctors"
          />
        </el-form-item>

        <el-form-item label="时段">
          <el-radio-group v-model="filters.timeSlot" @change="loadDoctors">
            <el-radio-button label="AM">上午</el-radio-button>
            <el-radio-button label="PM">下午</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="排序">
          <el-select v-model="filters.sortBy" @change="loadDoctors">
            <el-option label="默认排序" value="default" />
            <el-option label="空闲优先" value="available" />
            <el-option label="职称优先" value="title" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 医生列表 -->
    <el-card class="doctor-list-card" v-loading="loading">
      <template #header>
        <span>可预约医生 ({{ doctorList.length }}位)</span>
      </template>
      
      <el-empty v-if="doctorList.length === 0" description="暂无可预约医生，请调整筛选条件" />
      
      <div class="doctor-grid" v-else>
        <div class="doctor-card" v-for="doc in doctorList" :key="doc.id">
          <div class="doctor-avatar">
            <el-avatar :size="60">{{ doc.name.charAt(0) }}</el-avatar>
          </div>
          <div class="doctor-info">
            <div class="doctor-name">
              {{ doc.name }}
              <el-tag size="small" type="info">{{ doc.title || '医师' }}</el-tag>
            </div>
            <div class="doctor-dept">{{ doc.departmentName }}</div>
            <div class="doctor-specialty" v-if="doc.specialty">
              <el-icon><InfoFilled /></el-icon> {{ doc.specialty }}
            </div>
            <div class="doctor-booked" :class="{ warning: doc.bookedCount >= 5 }">
              当前时段已预约：{{ doc.bookedCount }}人
            </div>
          </div>
          <div class="doctor-action">
            <el-button type="primary" @click="handleBook(doc)">立即挂号</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 挂号确认对话框 -->
    <el-dialog v-model="bookDialogVisible" title="确认挂号" width="500px">
      <el-form :model="bookForm" label-width="100px">
        <el-form-item label="医生">
          <span>{{ selectedDoctor?.name }} ({{ selectedDoctor?.title }})</span>
        </el-form-item>
        <el-form-item label="科室">
          <span>{{ selectedDoctor?.departmentName }}</span>
        </el-form-item>
        <el-form-item label="日期">
          <span>{{ filters.date }}</span>
        </el-form-item>
        <el-form-item label="时段">
          <span>{{ filters.timeSlot === 'AM' ? '上午' : '下午' }}</span>
        </el-form-item>
        <el-divider />
        <el-form-item label="是否复诊">
          <el-radio-group v-model="bookForm.isReturn">
            <el-radio :label="0">初诊</el-radio>
            <el-radio :label="1">复诊</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="患者类型">
          <div style="display: flex; gap: 20px;">
            <!-- 自动判断的类型 -->
            <div v-if="autoPatientTypes.length > 0">
              <span style="color: #606266; font-size: 14px;">自动识别：</span>
              <el-tag v-for="type in autoPatientTypes" :key="type" style="margin-right: 8px;">
                {{ patientTypeLabel(type) }}
              </el-tag>
            </div>
            <!-- 可选的类型 -->
            <div>
              <el-checkbox-group v-model="bookForm.selectableTypes">
                <el-checkbox label="1">军人</el-checkbox>
              </el-checkbox-group>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBook" :loading="booking">确认挂号</el-button>
      </template>
    </el-dialog>

    <!-- 人数较多提示 -->
    <el-dialog v-model="crowdWarningVisible" title="温馨提示" width="350px">
      <div style="text-align: center;">
        <el-icon style="font-size: 48px; color: #E6A23C;"><WarningFilled /></el-icon>
        <p style="margin-top: 15px;">当前时段预约人数较多（{{ selectedDoctor?.bookedCount }}人）</p>
        <p style="color: #909399;">建议您更换其他时段以减少等待时间</p>
      </div>
      <template #footer>
        <el-button @click="crowdWarningVisible = false">更换时段</el-button>
        <el-button type="primary" @click="proceedBook">继续挂号</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Aim, InfoFilled, WarningFilled } from '@element-plus/icons-vue'
import { getDepartments, getDiseases, getDoctors, getDoctorsByDisease, bookRegistration } from '../../api/patientRegistration'

const loading = ref(false)
const booking = ref(false)
const mode = ref('free')
const bookDialogVisible = ref(false)
const crowdWarningVisible = ref(false)
const selectedDoctor = ref(null)

const departmentList = ref([])
const diseaseList = ref([])
const doctorList = ref([])
const patientAge = ref(null)
const autoPatientTypes = ref([])

const filters = reactive({
  departmentId: null,
  diseaseId: null,
  date: getDefaultDate(),
  timeSlot: 'AM',
  sortBy: 'default'
})

const bookForm = reactive({
  isReturn: 0,
  selectableTypes: []
})

// 获取默认日期（明天）
function getDefaultDate() {
  const d = new Date()
  d.setDate(d.getDate() + 1)
  return d.toISOString().split('T')[0]
}

// 禁用过去的日期
const disabledDate = (date) => {
  return date.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 按科室分组的病种
const groupedDiseases = computed(() => {
  const groups = []
  const deptMap = {}
  
  departmentList.value.forEach(dept => {
    deptMap[dept.id] = { id: dept.id, name: dept.name, diseases: [] }
  })
  
  diseaseList.value.forEach(disease => {
    if (disease.departmentId && deptMap[disease.departmentId]) {
      deptMap[disease.departmentId].diseases.push(disease)
    }
  })
  
  Object.values(deptMap).forEach(group => {
    if (group.diseases.length > 0) {
      groups.push(group)
    }
  })
  
  return groups
})

const handleModeChange = () => {
  filters.departmentId = null
  filters.diseaseId = null
  doctorList.value = []
}

const loadDoctors = async () => {
  if (mode.value === 'free') {
    await loadDoctorsFree()
  } else {
    await loadDoctorsTarget()
  }
}

const loadDoctorsFree = async () => {
  loading.value = true
  try {
    const res = await getDoctors({
      departmentId: filters.departmentId,
      date: filters.date,
      timeSlot: filters.timeSlot,
      sortBy: filters.sortBy
    })
    if (res.code === '200') {
      doctorList.value = res.data || []
    }
  } catch (error) {
    console.error('获取医生列表失败', error)
  } finally {
    loading.value = false
  }
}

const loadDoctorsTarget = async () => {
  if (!filters.diseaseId) {
    doctorList.value = []
    return
  }
  
  loading.value = true
  try {
    const res = await getDoctorsByDisease({
      diseaseId: filters.diseaseId,
      date: filters.date,
      timeSlot: filters.timeSlot,
      sortBy: filters.sortBy
    })
    if (res.code === '200') {
      doctorList.value = res.data || []
    }
  } catch (error) {
    console.error('获取医生列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleBook = (doc) => {
  if (!filters.date) {
    ElMessage.warning('请选择预约日期')
    return
  }
  
  selectedDoctor.value = doc
  
  // 获取患者年龄并计算自动类型
  const refId = localStorage.getItem('refId')
  if (refId) {
    calculatePatientTypes(parseInt(refId))
  }
  
  // 检查人数是否较多
  if (doc.bookedCount >= 5) {
    crowdWarningVisible.value = true
  } else {
    bookDialogVisible.value = true
  }
}

const calculatePatientTypes = (patientId) => {
  // 这里需要从后端获取患者年龄
  // 暂时从localStorage中获取（需要在登录时保存）
  const age = localStorage.getItem('patientAge')
  if (age) {
    patientAge.value = parseInt(age)
    autoPatientTypes.value = []
    
    // 判断老人（>=65岁）
    if (patientAge.value >= 65) {
      autoPatientTypes.value.push(2)
    }
    // 判断儿童（<=14岁）
    if (patientAge.value <= 14) {
      autoPatientTypes.value.push(3)
    }
  }
}

const patientTypeLabel = (type) => {
  const labels = { 0: '普通', 1: '军人', 2: '老人', 3: '儿童' }
  return labels[type] || ''
}

const proceedBook = () => {
  crowdWarningVisible.value = false
  bookDialogVisible.value = true
}

const confirmBook = async () => {
  const refId = localStorage.getItem('refId')
  if (!refId) {
    ElMessage.error('请先完善个人信息')
    return
  }
  
  // 计算最终的patientType（可以叠加）
  let patientTypes = [...autoPatientTypes.value]
  
  // 添加可选的类型（军人）
  bookForm.selectableTypes.forEach(type => {
    if (!patientTypes.includes(parseInt(type))) {
      patientTypes.push(parseInt(type))
    }
  })
  
  // 验证儿童不能是军人
  if (patientTypes.includes(3) && patientTypes.includes(1)) {
    ElMessage.error('儿童不能选择军人身份')
    return
  }
  
  booking.value = true
  try {
    const res = await bookRegistration({
      patientId: refId,
      doctorId: selectedDoctor.value.id,
      appointmentDate: filters.date,
      timeSlot: filters.timeSlot,
      isReturn: bookForm.isReturn,
      patientTypes: patientTypes
    })
    
    if (res.code === '200') {
      ElMessage.success('挂号成功！')
      bookDialogVisible.value = false
      loadDoctors()
    } else {
      ElMessage.error(res.msg || '挂号失败')
    }
  } catch (error) {
    ElMessage.error('挂号失败')
  } finally {
    booking.value = false
  }
}

const loadBaseData = async () => {
  try {
    const [deptRes, diseaseRes] = await Promise.all([
      getDepartments(),
      getDiseases()
    ])
    
    if (deptRes.code === '200') {
      departmentList.value = deptRes.data || []
    }
    if (diseaseRes.code === '200') {
      diseaseList.value = diseaseRes.data || []
    }
  } catch (error) {
    console.error('加载基础数据失败', error)
  }
}

onMounted(() => {
  loadBaseData()
  loadDoctors()
})
</script>

<style scoped>
.registration-container {
  padding: 20px;
}

.mode-card {
  margin-bottom: 15px;
}

.mode-card .el-radio-group {
  margin-right: 20px;
}

.mode-desc {
  color: #909399;
  font-size: 14px;
}

.filter-card {
  margin-bottom: 15px;
}

.doctor-list-card {
  min-height: 400px;
}

.doctor-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 15px;
}

.doctor-card {
  display: flex;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.3s;
}

.doctor-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
}

.doctor-avatar {
  margin-right: 15px;
}

.doctor-info {
  flex: 1;
}

.doctor-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 5px;
}

.doctor-dept {
  color: #606266;
  font-size: 14px;
  margin-bottom: 5px;
}

.doctor-specialty {
  color: #909399;
  font-size: 12px;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
  gap: 3px;
}

.doctor-booked {
  font-size: 12px;
  color: #67c23a;
}

.doctor-booked.warning {
  color: #e6a23c;
}

.doctor-action {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
