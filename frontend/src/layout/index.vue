<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px" class="aside-menu">
        <div class="logo">🏥 医院挂号系统</div>
        <el-menu
          active-text-color="#409EFF"
          background-color="#304156"
          text-color="#bfcbd9"
          :default-active="$route.path"
          router
          style="border-right: none;"
        >
          <el-menu-item index="/dashboard">
            <el-icon><DataLine /></el-icon> <span>首页统计</span>
          </el-menu-item>
          <el-menu-item index="/doctor">
            <el-icon><FirstAidKit /></el-icon> <span>医生管理</span>
          </el-menu-item>
          <el-menu-item index="/patient">
            <el-icon><User /></el-icon> <span>病人管理</span>
          </el-menu-item>
          <el-menu-item index="/department">
            <el-icon><OfficeBuilding /></el-icon> <span>科室管理</span>
          </el-menu-item>
          <el-menu-item index="/registration">
            <el-icon><Ticket /></el-icon> <span>挂号大厅</span>
          </el-menu-item>
          <el-menu-item index="/medicine">
            <el-icon><Box /></el-icon> <span>药品管理</span>
          </el-menu-item>
          <el-menu-item index="/bill">
            <el-icon><CreditCard /></el-icon> <span>收费管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header class="header">
          <div class="breadcrumb">欢迎使用医院挂号管理系统</div>
          <el-dropdown>
            <span class="el-dropdown-link">
              管理员 <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>退出登录</el-dropdown-item>
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
