import request from '@/utils/request'

// 1. 查询
export function getPatientList() {
    return request({
        url: '/patient/list',
        method: 'get'
    })
}

// 2. 新增
export function addPatient(data) {
    return request({
        url: '/patient',
        method: 'post',
        data
    })
}

// 3. 修改
export function updatePatient(data) {
    return request({
        url: '/patient',
        method: 'put',
        data
    })
}

// 4. 删除
export function deletePatient(id) {
    return request({
        url: '/patient/' + id,
        method: 'delete'
    })
}