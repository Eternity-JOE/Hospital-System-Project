<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px" class="aside-menu">
        <div class="logo">🏥 医生工作台</div>
        <el-menu
          active-text-color="#409EFF"
          background-color="#304156"
          text-color="#bfcbd9"
          :default-active="$route.path"
          router
          style="border-right: none;"
        >
          <el-menu-item index="/doctor/index">
            <el-icon><House /></el-icon> <span>工作台首页</span>
          </el-menu-item>
          <el-menu-item index="/doctor/profile">
            <el-icon><User /></el-icon> <span>个人信息</span>
          </el-menu-item>
          <el-menu-item index="/doctor/schedule">
            <el-icon><Calendar /></el-icon> <span>日程安排</span>
          </el-menu-item>
          <el-menu-item index="/doctor/leave">
            <el-icon><Document /></el-icon> <span>请假申请</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header class="header">
          <div class="breadcrumb">医生工作台</div>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ username }} <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-header>
        
        <el-main style="background-color: #f0f2f5;">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = computed(() => localStorage.getItem('username') || '医生')

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('userRole')
    localStorage.removeItem('username')
    router.push('/login')
  }
}
</script>

<style scoped>
.aside-menu {
  background-color: #304156;
  min-height: 100vh;
}
.logo {
  height: 60px;
  line-height: 60px;
  color: white;
  text-align: center;
  font-weight: bold;
  font-size: 18px;
  background-color: #2b3648;
}
.header {
  background-color: white;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
}
</style>
