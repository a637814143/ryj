<template>
  <div class="login-container">
    <!-- 只保留OS化的登录窗口 -->
    <div class="login-card">
      <!-- 标题栏 -->
      <div class="window-header">
        <div class="window-controls">
          <div class="control red"></div>
          <div class="control yellow"></div>
          <div class="control green"></div>
        </div>
        <div class="window-title">用户登录</div>
      </div>
      
      <!-- 登录表单 -->
      <div class="login-content">
        <h1 class="login-title">高校就业服务平台</h1>
        
        <div class="form-group">
          <label for="username">用户名</label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input 
              type="text" 
              id="username" 
              v-model="loginForm.username" 
              placeholder="请输入用户名"
              class="form-input"
              autocomplete="username"
            />
          </div>
        </div>
        
        <div class="form-group">
          <label for="password">密码</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input 
              type="password" 
              id="password" 
              v-model="loginForm.password" 
              placeholder="请输入密码"
              class="form-input"
              autocomplete="current-password"
            />
          </div>
        </div>
        
        <div class="form-options">
          <label class="checkbox-label">
            <input type="checkbox" v-model="loginForm.remember" />
            <span class="checkbox-text">记住我</span>
          </label>
          <a href="#" class="forgot-password">忘记密码?</a>
        </div>
        
        <button 
          class="login-button" 
          @click="handleLogin"
          :disabled="loading"
        >
          <span v-if="loading">登录中...</span>
          <span v-else>登录</span>
        </button>
        
        <div class="register-link">
          还没有账号? <a href="/register" @click.prevent="$router.push('/register')">立即注册</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import userService, { type LoginParams } from '../api/user.service';

const router = useRouter();
const loading = ref(false);
const loginForm = ref({
  username: '',
  password: '',
  remember: false
});

// 处理登录请求
const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    alert('请输入用户名和密码');
    return;
  }
  
  loading.value = true;
  
  try {
    const params: LoginParams = {
      username: loginForm.value.username,
      password: loginForm.value.password,
      remember: loginForm.value.remember
    };
    
    // 调用登录API
    const response = await userService.login(params);
    
    // 登录成功后的处理
    console.log('登录成功:', response);
    
    // 检查响应是否成功
    if (response.code === 200 && response.data) {
      // 保存用户信息到本地存储
      const userData = {
        username: response.data.username,
        token: 'mock-token-' + Date.now(), // 后端没有返回token，使用模拟token
        userInfo: response.data,
        userGroup: response.data.userGroup || '学生' // 保存用户组信息
      };
      localStorage.setItem('user', JSON.stringify(userData));
      
      alert('登录成功!');
      
      // 直接跳转到学生控制台，不检查信息完整性
      router.push('/student/dashboard');
    } else {
      alert(response.message || '登录失败');
    }
  } catch (error) {
    console.error('登录失败:', error);
    // 错误信息已经在axios拦截器中处理
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
/* 重置默认样式，确保页面从顶部开始 */
:deep(body) {
  margin: 0;
  padding: 0;
  min-height: 100vh;
  overflow-x: hidden;
}

/* 覆盖全局样式，确保登录页面全屏显示 */
:deep(#app) {
  max-width: none !important;
  margin: 0 !important;
  padding: 0 !important;
  display: block !important;
  grid-template-columns: none !important;
}

.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  padding: 20px;
  box-sizing: border-box;
  width: 100vw;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.login-card {
  position: relative;
  width: 100%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  z-index: 1;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  margin: 0 auto;
  flex-shrink: 0;
}

.login-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 25px 70px rgba(0, 0, 0, 0.2);
}

.window-header {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  background: #f5f5f7;
  border-bottom: 1px solid #e0e0e0;
}

.window-controls {
  display: flex;
  gap: 8px;
  margin-right: 16px;
}

.control {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.control:hover {
  transform: scale(1.1);
}

.control.red { background: #ff5f56; }
.control.yellow { background: #ffbd2e; }
.control.green { background: #27c93f; }

.window-title {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.login-content {
  padding: 32px;
}

.login-title {
  text-align: center;
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.form-group {
  margin-bottom: 24px;
}

label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 0 16px;
  transition: all 0.3s ease;
}

.input-wrapper:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: #fff;
}

.input-icon {
  font-size: 18px;
  margin-right: 12px;
  color: #666;
}

.form-input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 14px 0;
  font-size: 16px;
  color: #333;
  outline: none;
}

.form-input::placeholder {
  color: #9ca3af;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  color: #666;
}

.checkbox-label input[type="checkbox"] {
  margin-right: 8px;
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.forgot-password {
  font-size: 14px;
  color: #667eea;
  text-decoration: none;
  transition: color 0.2s ease;
}

.forgot-password:hover {
  color: #5a67d8;
  text-decoration: underline;
}

.login-button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.login-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.login-button:hover::before {
  left: 100%;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.register-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #666;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s ease;
}

.register-link a:hover {
  color: #5a67d8;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    padding: 10px;
  }
  
  .login-card {
    width: 100%;
    max-width: 380px;
  }
  
  .login-content {
    padding: 24px;
  }
  
  .login-title {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 5px;
  }
  
  .login-card {
    width: 100%;
    max-width: 350px;
  }
  
  .login-content {
    padding: 20px;
  }
  
  .login-title {
    font-size: 18px;
  }
}

/* 确保在大屏幕上也能完美居中 */
@media (min-width: 1200px) {
  .login-card {
    max-width: 450px;
  }
}
</style>