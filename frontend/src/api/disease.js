import request from '@/utils/request'

// 获取所有病种列表
export function getDiseaseList() {
  return request({
    url: '/disease/list',
    method: 'get'
  })
}

