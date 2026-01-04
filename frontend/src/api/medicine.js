import request from '@/utils/request'

export function getMedicineList() {
    return request({ url: '/medicine/list', method: 'get' })
}
export function addMedicine(data) {
    return request({ url: '/medicine', method: 'post', data })
}
export function updateMedicine(data) {
    return request({ url: '/medicine', method: 'put', data })
}
export function deleteMedicine(id) {
    return request({ url: '/medicine/' + id, method: 'delete' })
}