<template>
  <div class="profile-container">
    <el-card v-loading="loading">
      <template #header>
        <div class="header-row">
          <span>个人信息</span>
          <el-button v-if="doctorInfo && !isEditing" type="primary" @click="startEdit">修改信息</el-button>
        </div>
      </template>
      
      <!-- 查看模式 -->
      <template v-if="!isEditing">
        <el-descriptions :column="2" border v-if="doctorInfo">
          <el-descriptions-item label="姓名">{{ doctorInfo.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ doctorInfo.gender === 1 ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="职称">{{ doctorInfo.title }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ doctorInfo.phone }}</el-descriptions-item>
          <el-descriptions-item label="所属科室">{{ departmentName }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="doctorInfo.status === 1 ? 'success' : 'danger'">
              {{ doctorInfo.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="擅长领域" :span="2">{{ doctorInfo.specialty || '-' }}</el-descriptions-item>
          <el-descriptions-item label="关联病种" :span="2">
            <template v-if="doctorInfo.diseaseIds && doctorInfo.diseaseIds.length > 0">
              <el-tag v-for="diseaseId in doctorInfo.diseaseIds" :key="diseaseId" style="margin-right: 5px;">
                {{ getDiseaseName(diseaseId) }}
              </el-tag>
            </template>
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>
        <el-empty v-else description="暂无个人信息" />
      </template>

      <!-- 编辑模式 -->
      <template v-else>
        <el-form :model="editForm" :rules="rules" ref="formRef" label-width="100px" style="max-width: 500px;">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="editForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="editForm.gender">
              <el-radio :label="1">男</el-radio>
              <el-radio :label="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="职称" prop="title">
            <el-input v-model="editForm.title" placeholder="请输入职称" />
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="editForm.phone" placeholder="请输入手机号" maxlength="11" />
          </el-form-item>
          <el-form-item label="擅长领域">
            <el-input v-model="editForm.specialty" type="textarea" rows="2" placeholder="请输入擅长领域" />
          </el-form-item>
          <el-form-item label="关联病种" prop="diseaseIds">
            <el-select
              v-model="editForm.diseaseIds"
              placeholder="请选择病种（最多3个）"
              multiple
              filterable
              style="width: 100%"
              @change="handleDiseaseChange"
            >
              <el-option
                v-for="disease in filteredDiseaseList"
                :key="disease.id"
                :label="disease.name"
                :value="disease.id"
              />
            </el-select>
            <div style="color: #909399; font-size: 12px; margin-top: 5px;">
              已选择 {{ editForm.diseaseIds ? editForm.diseaseIds.length : 0 }} 个病种（限制1-3个）
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveEdit" :loading="saving">保存</el-button>
            <el-button @click="cancelEdit">取消</el-button>
          </el-form-item>
        </el-form>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDoctorProfile } from '../../api/doctorPortal'
import { getDepartmentList } from '../../api/department'
import { getDiseaseList } from '../../api/disease'
import { updateDoctor } from '../../api/doctor'

const loading = ref(false)
const saving = ref(false)
const isEditing = ref(false)
const formRef = ref(null)
const doctorInfo = ref(null)
const departmentName = ref('-')
const allDiseaseList = ref([])

const editForm = reactive({
  id: null,
  name: '',
  gender: 1,
  title: '',
  phone: '',
  specialty: '',
  departmentId: null,
  status: 1,
  diseaseIds: []
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  title: [{ required: true, message: '请输入职称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { len: 11, message: '手机号需要11位', trigger: 'blur' }
  ],
  diseaseIds: [
    { required: true, message: '请至少选择1个病种', trigger: 'change' }
  ]
}

// 根据科室过滤病种列表
const filteredDiseaseList = computed(() => {
  if (!editForm.departmentId) return []
  return allDiseaseList.value.filter(d => d.departmentId === editForm.departmentId)
})

// 获取病种名称
const getDiseaseName = (diseaseId) => {
  const disease = allDiseaseList.value.find(d => d.id === diseaseId)
  return disease ? disease.name : '-'
}

// 处理病种选择变化
const handleDiseaseChange = (value) => {
  if (value && value.length > 3) {
    ElMessage.warning('最多只能选择3个病种')
    editForm.diseaseIds = value.slice(0, 3)
  }
}

const loadProfile = async () => {
  loading.value = true
  try {
    const userId = localStorage.getItem('userId')
    const res = await getDoctorProfile(userId)
    if (res.code === '200' && res.data) {
      doctorInfo.value = res.data
      // 获取科室名称
      const deptRes = await getDepartmentList()
      if (deptRes.code === '200') {
        const dept = deptRes.data.find(d => d.id === res.data.departmentId)
        departmentName.value = dept ? dept.name : '-'
      }
    }
  } catch (error) {
    console.error('获取个人信息失败', error)
  } finally {
    loading.value = false
  }
}

const loadDiseaseList = async () => {
  try {
    const res = await getDiseaseList()
    if (res.code === '200') {
      allDiseaseList.value = res.data || []
    }
  } catch (error) {
    console.error('获取病种列表失败', error)
  }
}

const startEdit = () => {
  if (doctorInfo.value) {
    Object.assign(editForm, doctorInfo.value)
    // 确保 diseaseIds 是数组
    if (!editForm.diseaseIds) {
      editForm.diseaseIds = []
    }
  }
  isEditing.value = true
}

const cancelEdit = () => {
  isEditing.value = false
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const saveEdit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      // 验证病种数量
      if (!editForm.diseaseIds || editForm.diseaseIds.length === 0) {
        ElMessage.warning('请至少选择1个病种')
        return
      }
      if (editForm.diseaseIds.length > 3) {
        ElMessage.warning('最多只能选择3个病种')
        return
      }
      
      saving.value = true
      try {
        const res = await updateDoctor(editForm)
        if (res.code === '200') {
          ElMessage.success('保存成功')
          isEditing.value = false
          loadProfile()
        } else {
          ElMessage.error(res.msg || '保存失败')
        }
      } catch (error) {
        ElMessage.error('保存失败')
      } finally {
        saving.value = false
      }
    }
  })
}

onMounted(() => {
  loadDiseaseList()
  loadProfile()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 800px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
