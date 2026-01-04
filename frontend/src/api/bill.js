import request from '@/utils/request'

export function getBillList() {
    return request({ url: '/bill/list', method: 'get' })
}
export function payBill(id) {
    return request({ url: '/bill/pay/' + id, method: 'put' })
}