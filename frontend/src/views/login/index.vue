<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">医院管理系统</h2>
      
      <!-- 登录方式选择 -->
      <div v-if="!selectedRole" class="role-selection">
        <p class="hint">请选择您的登录方式</p>
        <div class="role-cards">
          <div class="role-card" @click="selectRole('patient')">
            <div class="role-name">就诊人</div>
          </div>
          <div class="role-card" @click="selectRole('doctor')">
            <div class="role-name">医生</div>
          </div>
          <div class="role-card" @click="selectRole('admin')">
            <div class="role-name">管理员</div>
          </div>
        </div>
      </div>

      <!-- 登录/注册表单 -->
      <div v-else class="login-form">
        <div class="selected-role" @click="goBackToRoleSelect">
          <span>{{ roleLabels[selectedRole] }}</span>
          <span class="change-role">切换身份</span>
        </div>
        
        <!-- 登录表单 -->
        <el-form v-if="!isRegister" :model="loginForm" :rules="loginRules" ref="loginFormRef">
          <el-form-item prop="username">
            <el-input 
              v-model="loginForm.username" 
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" style="width: 100%" @click="handleLogin" :loading="loading">
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 注册表单（仅就诊人） -->
        <el-form v-else :model="registerForm" :rules="registerRules" ref="registerFormRef">
          <el-form-item prop="phone">
            <el-input 
              v-model="registerForm.phone" 
              placeholder="请输入手机号"
              prefix-icon="Phone"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="username">
            <el-input 
              v-model="registerForm.username" 
              placeholder="请设置账号"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="registerForm.password" 
              type="password" 
              placeholder="请设置密码"
              prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" style="width: 100%" @click="handleRegister" :loading="loading">
              注 册
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 就诊人身份下显示注册/登录切换 -->
        <div v-if="selectedRole === 'patient'" class="toggle-mode">
          <span v-if="!isRegister">没有账号？<a @click="isRegister = true">立即注册</a></span>
          <span v-else>已有账号？<a @click="isRegister = false">返回登录</a></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '../../api/login'

const router = useRouter()
const loginFormRef = ref(null)
const registerFormRef = ref(null)
const selectedRole = ref(null)
const loading = ref(false)
const isRegister = ref(false)

const roleLabels = {
  patient: '病人',
  doctor: '医生',
  admin: '管理员'
}

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  phone: '',
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { len: 11, message: '手机号需要11位', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请设置账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ]
}

const selectRole = (role) => {
  selectedRole.value = role
  isRegister.value = false
}

const goBackToRoleSelect = () => {
  selectedRole.value = null
  isRegister.value = false
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login({
          username: loginForm.username,
          password: loginForm.password,
          role: selectedRole.value
        })
        
        if (res.code === '200') {
          localStorage.setItem('userRole', res.data.role)
          localStorage.setItem('username', res.data.username)
          localStorage.setItem('userId', res.data.id)
          localStorage.setItem('refId', res.data.refId || '')
          
          // 保存患者年龄
          if (res.data.age) {
            localStorage.setItem('patientAge', res.data.age)
          }
          
          ElMessage.success('登录成功')
          
          const redirectMap = {
            admin: '/admin/dashboard',
            doctor: '/doctor/index',
            patient: '/patient/index'
          }
          router.push(redirectMap[res.data.role])
        } else {
          const roleText = { patient: '就诊人', doctor: '医生', admin: '管理员' }
          ElMessage.error(`账号或密码错误，或该账号不是${roleText[selectedRole.value]}身份`)
        }
      } catch (error) {
        ElMessage.error('网络异常，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await register({
          username: registerForm.username,
          password: registerForm.password
        })
        
        if (res.code === '200') {
          ElMessage.success('注册成功，请登录')
          isRegister.value = false
          loginForm.username = registerForm.username
          loginForm.password = ''
          // 清空注册表单
          registerForm.phone = ''
          registerForm.username = ''
          registerForm.password = ''
        } else {
          ElMessage.error(res.msg || '注册失败')
        }
      } catch (error) {
        ElMessage.error('网络异常，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: url('/login-bg.jpg') center/cover no-repeat;
  background-attachment: fixed;
}

.login-box {
  width: 420px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

.hint {
  text-align: center;
  color: #666;
  margin-bottom: 20px;
}

.role-cards {
  display: flex;
  justify-content: space-between;
  gap: 15px;
}

.role-card {
  flex: 1;
  padding: 25px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.role-card:hover {
  border-color: #409EFF;
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(64, 158, 255, 0.2);
}

.role-name {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.selected-role {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
  cursor: pointer;
}

.selected-role span:first-child {
  font-size: 16px;
  font-weight: 500;
}

.change-role {
  color: #409EFF;
  font-size: 14px;
}

.change-role:hover {
  text-decoration: underline;
}

.toggle-mode {
  text-align: center;
  margin-top: 15px;
  color: #666;
  font-size: 14px;
}

.toggle-mode a {
  color: #409EFF;
  cursor: pointer;
}

.toggle-mode a:hover {
  text-decoration: underline;
}
</style>
