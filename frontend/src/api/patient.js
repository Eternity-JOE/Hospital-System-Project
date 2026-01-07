import request from '../utils/request'

// 获取病人列表
export function getPatientList(params) {
  return request({
    url: '/patient/list',
    method: 'get',
    params
  })
}

// 新增病人
export function addPatient(data) {
  return request({
    url: '/patient',
    method: 'post',
    data
  })
}

// 修改病人
export function updatePatient(data) {
  return request({
    url: '/patient',
    method: 'put',
    data
  })
}

// 删除病人
export function deletePatient(id) {
  return request({
    url: `/patient/${id}`,
    method: 'delete'
  })
}

// 获取当前登录病人的个人信息
export function getMyProfile(userId) {
  return request({
    url: '/api/patient/profile',
    method: 'get',
    params: { userId }
  })
}

// 保存病人个人信息
export function saveProfile(data) {
  return request({
    url: '/api/patient/profile',
    method: 'post',
    data
  })
}
