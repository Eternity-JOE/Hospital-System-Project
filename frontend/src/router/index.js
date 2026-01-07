import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '../layout/AdminLayout.vue'
import DoctorLayout from '../layout/DoctorLayout.vue'
import PatientLayout from '../layout/PatientLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/login/index.vue')
    },
    {
      path: '/',
      redirect: '/login'
    },
    // 管理员路由
    {
      path: '/admin',
      component: AdminLayout,
      redirect: '/admin/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'AdminDashboard',
          component: () => import('../views/dashboard/index.vue'),
          meta: { title: '首页统计', role: 'admin' }
        },
        {
          path: 'doctor',
          name: 'AdminDoctor',
          component: () => import('../views/doctor/index.vue'),
          meta: { title: '医生管理', role: 'admin' }
        },
        {
          path: 'patient',
          name: 'AdminPatient',
          component: () => import('../views/patient/index.vue'),
          meta: { title: '病人管理', role: 'admin' }
        },
        {
          path: 'department',
          name: 'AdminDepartment',
          component: () => import('../views/department/index.vue'),
          meta: { title: '科室管理', role: 'admin' }
        },
        {
          path: 'registration',
          name: 'AdminRegistration',
          component: () => import('../views/registration/index.vue'),
          meta: { title: '挂号大厅', role: 'admin' }
        },
        {
          path: 'medicine',
          name: 'AdminMedicine',
          component: () => import('../views/medicine/index.vue'),
          meta: { title: '药品管理', role: 'admin' }
        },
        {
          path: 'bill',
          name: 'AdminBill',
          component: () => import('../views/bill/index.vue'),
          meta: { title: '收费管理', role: 'admin' }
        },
        {
          path: 'adminManage',
          name: 'AdminManage',
          component: () => import('../views/admin/index.vue'),
          meta: { title: '管理员管理', role: 'admin' }
        },
        {
          path: 'leaveApproval',
          name: 'LeaveApproval',
          component: () => import('../views/admin/leaveApproval.vue'),
          meta: { title: '请假审批', role: 'admin' }
        }
      ]
    },
    // 医生路由
    {
      path: '/doctor',
      component: DoctorLayout,
      redirect: '/doctor/schedule',
      children: [
        {
          path: 'profile',
          name: 'DoctorProfile',
          component: () => import('../views/doctor/profile.vue'),
          meta: { title: '个人信息', role: 'doctor' }
        },
        {
          path: 'schedule',
          name: 'DoctorSchedule',
          component: () => import('../views/doctor/schedule.vue'),
          meta: { title: '日程安排', role: 'doctor' }
        },
        {
          path: 'leave',
          name: 'DoctorLeave',
          component: () => import('../views/doctor/leave.vue'),
          meta: { title: '请假申请', role: 'doctor' }
        }
      ]
    },
    // 患者路由
    {
      path: '/patient',
      component: PatientLayout,
      redirect: '/patient/registration',
      children: [
        {
          path: 'profile',
          name: 'PatientProfile',
          component: () => import('../views/patient/profile.vue'),
          meta: { title: '个人信息', role: 'patient' }
        },
        {
          path: 'registration',
          name: 'PatientRegistration',
          component: () => import('../views/patient/registration.vue'),
          meta: { title: '挂号大厅', role: 'patient' }
        },
        {
          path: 'myRegistration',
          name: 'PatientMyRegistration',
          component: () => import('../views/patient/myRegistration.vue'),
          meta: { title: '我的挂号', role: 'patient' }
        }
      ]
    }
  ]
})

// 路由守卫 - 检查登录状态
router.beforeEach((to, from, next) => {
  const userRole = localStorage.getItem('userRole')
  
  // 访问登录页直接放行
  if (to.path === '/login') {
    next()
    return
  }
  
  // 未登录跳转到登录页
  if (!userRole) {
    next('/login')
    return
  }
  
  // 检查角色权限
  if (to.meta.role && to.meta.role !== userRole) {
    // 角色不匹配，跳转到对应角色的首页
    const redirectMap = {
      admin: '/admin/dashboard',
      doctor: '/doctor/schedule',
      patient: '/patient/registration'
    }
    next(redirectMap[userRole])
    return
  }
  
  next()
})

export default router
