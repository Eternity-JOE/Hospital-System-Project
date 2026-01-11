import request from '@/utils/request'

// 获取所有医生
export function getDoctorList() {
  return request({
    url: '/doctor/list',
    method: 'get'
  })
}

// 根据科室获取医生
export function getDoctorsByDepartment(departmentId) {
  return request({
    url: '/doctor/listByDepartment',
    method: 'get',
    params: { departmentId }
  })
}

// 获取医生个人信息（包含排班）
export function getDoctorProfile(userId) {
  return request({
    url: '/doctor/profile',
    method: 'get',
    params: { userId }
  })
}

// 新增医生
export function addDoctor(data) {
  return request({
    url: '/doctor',
    method: 'post',
    data
  })
}

// 修改医生
export function updateDoctor(data) {
  return request({
    url: '/doctor',
    method: 'put',
    data
  })
}

// 删除医生
export function deleteDoctor(id) {
  return request({
    url: `/doctor/${id}`,
    method: 'delete'
  })
}
