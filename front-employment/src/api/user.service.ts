import apiClient from './axios.config';

// 用户登录接口参数
export interface LoginParams {
  username: string;
  password: string;
  remember?: boolean;
}

// 用户注册接口参数
export interface RegisterParams {
  username: string;
  phone: string;
  email: string;
  password: string;
  userGroup: string;
}

// 用户信息接口
export interface UserInfo {
  userId: number;
  username: string;
  nickname: string;
  phone: string;
  email: string;
  userGroup: string;
  avatar?: string;
}

// 后端统一响应接口
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// 登录响应接口
export interface LoginResponse extends ApiResponse<UserInfo> {
  // 继承统一响应结构
}

// 用户服务类
class UserService {
  // 用户登录
  async login(params: LoginParams): Promise<LoginResponse> {
    try {
      // 这里应该调用后端的登录API
      // 暂时返回模拟数据
      const response = await apiClient.post<LoginResponse>('/user/login', params);
      return response;
    } catch (error) {
      console.error('登录失败:', error);
      throw error;
    }
  }

  // 用户注册
  async register(params: RegisterParams): Promise<ApiResponse<UserInfo>> {
    try {
      // 这里应该调用后端的注册API
      const response = await apiClient.post<ApiResponse<UserInfo>>('/user/register', params);
      return response;
    } catch (error) {
      console.error('注册失败:', error);
      throw error;
    }
  }

  // 获取用户信息
  async getUserInfo(): Promise<UserInfo> {
    try {
      const response = await apiClient.get('/user/info');
      return response;
    } catch (error) {
      console.error('获取用户信息失败:', error);
      throw error;
    }
  }

  // 退出登录
  async logout(): Promise<{ success: boolean }> {
    try {
      const response = await apiClient.post('/user/logout');
      // 清除本地存储的用户信息
      localStorage.removeItem('user');
      return response;
    } catch (error) {
      console.error('退出登录失败:', error);
      // 即使失败也清除本地存储
      localStorage.removeItem('user');
      return { success: true };
    }
  }

  // 更新用户信息
  async updateUserInfo(params: Partial<UserInfo>): Promise<UserInfo> {
    try {
      const response = await apiClient.put('/user/info', params);
      return response;
    } catch (error) {
      console.error('更新用户信息失败:', error);
      throw error;
    }
  }

  // 修改密码
  async changePassword(oldPassword: string, newPassword: string): Promise<{ success: boolean; message: string }> {
    try {
      const response = await apiClient.put('/user/password', {
        oldPassword,
        newPassword
      });
      return response;
    } catch (error) {
      console.error('修改密码失败:', error);
      throw error;
    }
  }
}

// 导出单例
export default new UserService();