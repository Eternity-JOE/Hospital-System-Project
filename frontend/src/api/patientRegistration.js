import request from '../utils/request'

// 获取科室列表
export function getDepartments() {
  return request({
    url: '/api/patient/registration/departments',
    method: 'get'
  })
}

// 获取病种列表
export function getDiseases() {
  return request({
    url: '/api/patient/registration/diseases',
    method: 'get'
  })
}

// 获取可挂号医生列表（自由挂号）
export function getDoctors(params) {
  return request({
    url: '/api/patient/registration/doctors',
    method: 'get',
    params
  })
}

// 根据病种获取医生（定向挂号）
export function getDoctorsByDisease(params) {
  return request({
    url: '/api/patient/registration/doctors/byDisease',
    method: 'get',
    params
  })
}

// 挂号
export function bookRegistration(data) {
  return request({
    url: '/api/patient/registration/book',
    method: 'post',
    data
  })
}

// 获取我的挂号记录
export function getMyRegistrations(patientId) {
  return request({
    url: '/api/patient/registration/myList',
    method: 'get',
    params: { patientId }
  })
}

// 提交评价
export function submitRating(data) {
  return request({
    url: '/api/patient/registration/rate',
    method: 'post',
    data
  })
}

// 取消挂号
export function cancelRegistration(id) {
  return request({
    url: `/api/patient/registration/cancel/${id}`,
    method: 'post'
  })
}

// 获取排队列表
export function getQueue(params) {
  return request({
    url: '/api/patient/registration/queue',
    method: 'get',
    params
  })
}
