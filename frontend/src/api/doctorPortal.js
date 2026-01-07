import request from '../utils/request'

// 获取医生个人信息
export function getDoctorProfile(userId) {
  return request({
    url: '/doctor/profile',
    method: 'get',
    params: { userId }
  })
}

// 获取医生周日程
export function getWeekSchedule(doctorId, startDate, endDate) {
  return request({
    url: '/api/doctor/schedule/week',
    method: 'get',
    params: { doctorId, startDate, endDate }
  })
}

// 获取请假记录
export function getLeaveList(doctorId) {
  return request({
    url: '/api/doctor/leave/list',
    method: 'get',
    params: { doctorId }
  })
}

// 获取本月已用请假次数
export function getMonthLeaveCount(doctorId) {
  return request({
    url: '/api/doctor/leave/monthCount',
    method: 'get',
    params: { doctorId }
  })
}

// 申请请假
export function applyLeave(data) {
  return request({
    url: '/api/doctor/leave/apply',
    method: 'post',
    data
  })
}

// 取消请假
export function cancelLeave(id) {
  return request({
    url: `/api/doctor/leave/${id}`,
    method: 'delete'
  })
}
