import axios from 'axios';

// 创建axios实例
const apiClient = axios.create({
  baseURL: 'http://localhost:8080', // 后端API基础路径
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: true // 允许携带凭证
});

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 从本地存储获取token
    const userInfo = localStorage.getItem('user');
    if (userInfo) {
      const token = JSON.parse(userInfo).token;
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    // 直接返回响应中的data部分
    return response.data;
  },
  (error) => {
    // 处理错误响应
    if (error.response) {
      // 服务器返回错误状态码
      switch (error.response.status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          localStorage.removeItem('user');
          alert('登录已过期，请重新登录');
          window.location.href = '/login';
          break;
        case 403:
          alert('没有权限访问该资源');
          break;
        case 404:
          alert('请求的资源不存在');
          break;
        case 500:
          alert('服务器内部错误');
          break;
        default:
          alert(`请求错误: ${error.response.data?.message || error.response.data?.error || '未知错误'}`);
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      console.error('网络请求失败:', error.request);
      alert('网络连接失败，请检查后端服务是否启动（http://localhost:8080）');
    } else {
      // 请求配置出错
      console.error('请求配置错误:', error.message);
      alert(`请求配置错误: ${error.message}`);
    }
    return Promise.reject(error);
  }
);

export default apiClient;