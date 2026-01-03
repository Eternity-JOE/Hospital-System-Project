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
// ... 前面的代码
      const res = response.data

      // 👇 修改这里！兼容字符串 "200" 和数字 200
      // 如果 code 不等于 "200" 且不等于 200，才算错
      if (res.code !== '200' && res.code !== 200) {

          // ... 这里是报错处理逻辑 (Message.error 等)
          return Promise.reject(new Error(res.msg || 'Error'))

      } else {
          // 🟢 如果是 200，直接把数据放行！
          return res
      }
      // ... 后面的代码
  },
  error => {
    console.log('err' + error)
    ElMessage.error(error.message)
    return Promise.reject(error)
  }
)

export default service