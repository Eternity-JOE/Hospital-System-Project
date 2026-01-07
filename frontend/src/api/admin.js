import request from '../utils/request'

// 获取管理员列表
export function getAdminList() {
  return request({
    url: '/api/admin/list',
    method: 'get'
  })
}

// 新增管理员
export function addAdmin(data) {
  return request({
    url: '/api/admin',
    method: 'post',
    data
  })
}

// 删除管理员
export function deleteAdmin(id) {
  return request({
    url: `/api/admin/${id}`,
    method: 'delete'
  })
}
