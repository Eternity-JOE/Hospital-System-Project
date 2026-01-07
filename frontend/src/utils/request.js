import axios from 'axios'
import { ElMessage } from 'element-plus'

// 1. 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8080', // 这里对应后端的接口前缀
  timeout: 5000    // 请求超时时间
})

// 2. 请求拦截器 (发送请求前做的事)
service.interceptors.request.use(
  config => {
    // 假设 Token 存在 localStorage 里
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 3. 响应拦截器 (收到结果后做的事)
service.interceptors.response.use(
  response => {
    // 直接返回响应数据，让业务代码自己判断 code
    return response.data
  },
  error => {
    console.log('err' + error)
    ElMessage.error('网络异常，请稍后重试')
    return Promise.reject(error)
  }
)

export default service