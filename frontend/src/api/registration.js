import request from '@/utils/request'

// 查询挂号列表
export function getRegistrationList() {
    return request({
        url: '/registration/list',
        method: 'get'
    })
}

// 查询排队队列
export function getQueue(departmentId, date, timeSlot) {
    return request({
        url: '/registration/queue',
        method: 'get',
        params: { departmentId, date, timeSlot }
    })
}

// 查询号源情况
export function getSlots(departmentId, date) {
    return request({
        url: '/registration/slots',
        method: 'get',
        params: { departmentId, date }
    })
}

// 新增挂号
export function addRegistration(data) {
    return request({
        url: '/registration',
        method: 'post',
        data
    })
}

// 修改挂号
export function updateRegistration(data) {
    return request({
        url: '/registration',
        method: 'put',
        data
    })
}

// 取消挂号（退号）
export function cancelRegistration(id) {
    return request({
        url: '/registration/cancel/' + id,
        method: 'post'
    })
}

// 完成就诊
export function completeRegistration(id) {
    return request({
        url: '/registration/complete/' + id,
        method: 'post'
    })
}

// 标记爽约
export function markNoShow(id) {
    return request({
        url: '/registration/noshow/' + id,
        method: 'post'
    })
}

// 删除挂号
export function deleteRegistration(id) {
    return request({
        url: '/registration/' + id,
        method: 'delete'
    })
}
