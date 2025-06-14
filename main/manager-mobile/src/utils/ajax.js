// ajax.js

// 引入 uni-ajax 模块
import ajax from 'uni-ajax'

// 创建请求实例
const instance = ajax.create({
  // 初始配置
  baseURL: import.meta.env.VITE_APP_API_BASE_PATH,
})

// 添加请求拦截器
instance.interceptors.request.use(
  config => {
	let _header = { 'content-type': 'application/json; charset=utf-8' };
	if(getApp().globalData.userInfo.token){
		_header.Authorization = 'Bearer ' + getApp().globalData.userInfo.token
	}
	config.header = _header
    // 在发送请求前做些什么
    return config
  },
  error => {
    // 对请求错误做些什么
    return Promise.reject(error)
  }
)

// 添加响应拦截器
instance.interceptors.response.use(
  response => {
    if(response.statusCode === 200){
      if(response.data.code === 401){
        getApp().globalData.userInfo = {}
        uni.removeStorageSync('userInfo')
        uni.reLaunch({
          url: '/pages/login/login'
        })
      }
	  }else{
      uni.showToast({
        icon: 'none',
        position: 'bottom',
        title: '网络错误，请稍后重试'
      });
      console.error("网络错误，请稍后重试:", response.data.message)
    }
    // 对响应数据做些什么
    return response
  },
  error => {
    // 对响应错误做些什么
    return Promise.reject(error)
  }
)

// 导出 create 创建后的实例
export default instance