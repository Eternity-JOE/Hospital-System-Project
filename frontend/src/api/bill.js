import request from '@/utils/request'

// 管理员接口
export function getBillList() {
    return request({ url: '/bill/list', method: 'get' })
}
export function payBill(id) {
    return request({ url: '/bill/pay/' + id, method: 'put' })
}

// 医生接口
export function getDoctorBillList(doctorId) {
    return request({ url: '/bill/doctor/list', method: 'get', params: {doctorId: doctorId} })
}
export function createDoctorBill(billData) {
    return request({ url: '/bill/doctor/create', method: 'post', data: billData })
}
export function payDoctorBill(id) {
    return request({ url: '/bill/doctor/pay/' + id, method: 'put' })
}




// 获取病人账单列表
export function getPatientBillList(patientId) {
  return request({
    url: '/bill/patient/list',
    method: 'get',
    params: { patientId }
  })
}

// 病人缴费接口
export function payPatientBill(id) {
  return request({
    url: `/bill/patient/pay/${id}`,
    method: 'put'
  })
}
