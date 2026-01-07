import request from '../utils/request'

// 获取待审批请假列表
export function getPendingLeaves() {
  return request({
    url: '/api/admin/leave/pending',
    method: 'get'
  })
}

// 获取所有请假记录
export function getAllLeaves() {
  return request({
    url: '/api/admin/leave/all',
    method: 'get'
  })
}

// 审批请假
export function approveLeave(data) {
  return request({
    url: '/api/admin/leave/approve',
    method: 'post',
    data
  })
}
