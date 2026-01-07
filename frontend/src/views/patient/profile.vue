<template>
  <div class="profile-container">
    <el-card>
      <template #header>
        <span>个人信息</span>
      </template>
      
      <el-form :model="profileForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="profileForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="profileForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="profileForm.age" :min="1" :max="150" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="家庭住址" prop="address">
          <el-input v-model="profileForm.address" placeholder="请输入家庭住址" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            {{ hasProfile ? '更新信息' : '确认提交' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyProfile, saveProfile } from '../../api/patient'

const formRef = ref(null)
const loading = ref(false)
const hasProfile = ref(false)

const profileForm = reactive({
  name: '',
  gender: '',
  age: null,
  phone: '',
  address: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const loadProfile = async () => {
  try {
    const userId = localStorage.getItem('userId')
    const res = await getMyProfile(userId)
    if (res.code === '200' && res.data) {
      hasProfile.value = true
      Object.assign(profileForm, res.data)
    }
  } catch (error) {
    console.log('暂无个人信息')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const userId = localStorage.getItem('userId')
        const res = await saveProfile({ ...profileForm, userId })
        
        if (res.code === '200') {
          ElMessage.success(hasProfile.value ? '更新成功' : '保存成功')
          hasProfile.value = true
        } else {
          ElMessage.error(res.msg || '保存失败')
        }
      } catch (error) {
        ElMessage.error('网络异常')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 500px;
}
</style>
