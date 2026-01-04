import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/login/index.vue'),
      hidden: true
    },
    {
      path: '/',
      component: Layout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('../views/dashboard/index.vue'),
          meta: { title: '首页统计' }
        },
        {
          path: 'doctor',
          name: 'Doctor',
          component: () => import('../views/doctor/index.vue'),
          meta: { title: '医生管理' }
        },
        {
          path: 'patient',
          name: 'Patient',
          component: () => import('../views/patient/index.vue'),
          meta: { title: '病人管理' }
        },
        {
          path: 'department',
          name: 'Department',
          component: () => import('../views/department/index.vue'),
          meta: { title: '科室管理' }
        },
        {
          path: 'registration',
          name: 'Registration',
          component: () => import('../views/registration/index.vue'),
          meta: { title: '挂号大厅' }
        },
        {
          path: 'medicine',
          name: 'Medicine',
          component: () => import('../views/medicine/index.vue'),
          meta: { title: '药品管理' }
        },
        {
          path: 'bill',
          name: 'Bill',
          component: () => import('../views/bill/index.vue'),
          meta: { title: '收费管理' }
        }
      ]
    }
  ]
})

export default router
