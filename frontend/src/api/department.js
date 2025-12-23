import request from '@/utils/request'

// 查询科室列表
export function getDepartmentList() {
    return request({
        url: '/department/list',
        method: 'get'
    })
}

// 新增科室
export function addDepartment(data) {
    return request({
        url: '/department',
        method: 'post',
        data
    })
}

// 修改科室
export function updateDepartment(data) {
    return request({
        url: '/department',
        method: 'put',
        data
    })
}

// 删除科室
export function deleteDepartment(id) {
    return request({
        url: '/department/' + id,
        method: 'delete'
    })
}
