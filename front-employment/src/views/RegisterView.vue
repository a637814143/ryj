<template>
  <div class="register-container">
    <div class="register-card">
      <!-- 标题栏 -->
      <div class="window-header">
        <div class="window-controls">
          <div class="control red"></div>
          <div class="control yellow"></div>
          <div class="control green"></div>
        </div>
        <div class="window-title">用户注册</div>
      </div>
      
      <!-- 注册表单 -->
      <div class="register-content">
        <h1 class="register-title">创建新账号</h1>
        <p class="register-subtitle">加入高校就业服务平台</p>
        
        <div class="form-group">
          <label for="register-username">用户名</label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input 
              type="text" 
              id="register-username" 
              v-model="registerForm.username" 
              placeholder="请设置用户名"
              class="form-input"
            />
          </div>
        </div>
        
        <div class="form-group">
          <label for="register-phone">手机号码</label>
          <div class="input-wrapper">
            <span class="input-icon">📱</span>
            <input 
              type="tel" 
              id="register-phone" 
              v-model="registerForm.phone" 
              placeholder="请输入手机号码"
              class="form-input"
            />
          </div>
        </div>
        
        <div class="form-group">
          <label for="register-email">电子邮箱</label>
          <div class="input-wrapper">
            <span class="input-icon">📧</span>
            <input 
              type="email" 
              id="register-email" 
              v-model="registerForm.email" 
              placeholder="请输入电子邮箱"
              class="form-input"
            />
          </div>
        </div>
        
        <div class="form-group">
          <label for="register-password">设置密码</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input 
              type="password" 
              id="register-password" 
              v-model="registerForm.password" 
              placeholder="请设置密码（不少于8位）"
              class="form-input"
            />
          </div>
        </div>
        
        <div class="form-group">
          <label for="register-confirm-password">确认密码</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input 
              type="password" 
              id="register-confirm-password" 
              v-model="registerForm.confirmPassword" 
              placeholder="请再次输入密码"
              class="form-input"
            />
          </div>
        </div>
        
        <div class="form-group">
          <label for="user-group">用户类型</label>
          <div class="input-wrapper">
            <span class="input-icon">👥</span>
            <select id="user-group" v-model="registerForm.userGroup" class="form-input">
              <option value="student">学生</option>
              <option value="teacher">教师</option>
              <option value="enterprise">企业</option>
            </select>
          </div>
        </div>
        
        <div class="form-options">
          <label class="checkbox-label">
            <input type="checkbox" v-model="registerForm.acceptTerms" />
            <span class="checkbox-text">我已阅读并同意 <a href="#" class="terms-link">用户协议</a> 和 <a href="#" class="terms-link">隐私政策</a></span>
          </label>
        </div>
        
        <button 
          class="register-button" 
          @click="handleRegister"
          :disabled="loading || !registerForm.acceptTerms"
        >
          <span v-if="loading">注册中...</span>
          <span v-else>注册</span>
        </button>
        
        <div class="login-link">
          已有账号? <a href="/login" @click.prevent="$router.push('/login')">立即登录</a>
        </div>
      </div>
    </div>
    
    <!-- 背景效果 -->
    <div class="background-effects">
      <div class="effect-circle circle-1"></div>
      <div class="effect-circle circle-2"></div>
      <div class="effect-circle circle-3"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import userService, { type RegisterParams } from '../api/user.service';

const router = useRouter();
const loading = ref(false);
const registerForm = ref({
  username: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
  userGroup: 'student',
  acceptTerms: false
});

// 表单验证
const validateForm = () => {
  if (!registerForm.value.username) {
    alert('请输入用户名');
    return false;
  }
  
  if (!registerForm.value.phone) {
    alert('请输入手机号码');
    return false;
  }
  
  if (!registerForm.value.email) {
    alert('请输入电子邮箱');
    return false;
  }
  
  if (!registerForm.value.password) {
    alert('请设置密码');
    return false;
  }
  
  if (registerForm.value.password.length < 8) {
    alert('密码长度至少为8位');
    return false;
  }
  
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    alert('两次输入的密码不一致');
    return false;
  }
  
  if (!registerForm.value.acceptTerms) {
    alert('请阅读并同意用户协议和隐私政策');
    return false;
  }
  
  return true;
};

// 处理注册请求
const handleRegister = async () => {
  if (!validateForm()) {
    return;
  }
  
  loading.value = true;
  
  try {
    const params: RegisterParams = {
      username: registerForm.value.username,
      phone: registerForm.value.phone,
      email: registerForm.value.email,
      password: registerForm.value.password,
      userGroup: registerForm.value.userGroup
    };
    
    // 调用注册API
    const response = await userService.register(params);
    
    // 注册成功后的处理
    console.log('注册成功:', response);
    
    // 检查响应是否成功
    if (response.code === 200) {
      alert(response.message || '注册成功！请登录');
      
      // 跳转到登录页面
      router.push('/login');
    } else {
      alert(response.message || '注册失败');
    }
  } catch (error) {
    console.error('注册失败:', error);
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

/* 覆盖全局样式，确保注册页面全屏显示 */
:deep(#app) {
  max-width: none !important;
  margin: 0 !important;
  padding: 0 !important;
  display: block !important;
  grid-template-columns: none !important;
}

.register-container {
  min-height: 100vh;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  padding: 20px;
  box-sizing: border-box;
  width: 100vw;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.background-effects {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.effect-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
}

.circle-1 {
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.4);
  bottom: -100px;
  left: -100px;
  animation: float 12s ease-in-out infinite;
}

.circle-2 {
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.3);
  top: -50px;
  right: -50px;
  animation: float 10s ease-in-out infinite reverse;
}

.circle-3 {
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.2);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: float 8s ease-in-out infinite alternate;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(20px, 20px); }
}

.register-card {
  position: relative;
  width: 100%;
  max-width: 480px;
  min-height: 600px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  z-index: 1;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  margin: 20px auto;
  flex-shrink: 0;
}

.register-card:hover {
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

.register-content {
  padding: 32px;
}

.register-title {
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.register-subtitle {
  text-align: center;
  font-size: 16px;
  color: #666;
  margin-bottom: 32px;
}

.form-group {
  margin-bottom: 20px;
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
  border-color: #84fab0;
  box-shadow: 0 0 0 3px rgba(132, 250, 176, 0.1);
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

.form-input[type="select"] {
  cursor: pointer;
}

.form-options {
  margin-bottom: 24px;
}

.checkbox-label {
  display: flex;
  align-items: flex-start;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.checkbox-label input[type="checkbox"] {
  margin-right: 8px;
  margin-top: 2px;
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.terms-link {
  color: #84fab0;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s ease;
}

.terms-link:hover {
  color: #5ddcaa;
  text-decoration: underline;
}

.register-button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
  color: #000;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.register-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.register-button:hover::before {
  left: 100%;
}

.register-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(132, 250, 176, 0.4);
}

.register-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.login-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #84fab0;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s ease;
}

.login-link a:hover {
  color: #5ddcaa;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-container {
    padding: 10px;
    align-items: flex-start;
  }
  
  .register-card {
    width: 100%;
    max-width: 420px;
    min-height: 500px;
    margin: 10px auto;
  }
  
  .register-content {
    padding: 24px;
  }
  
  .register-title {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .register-container {
    padding: 5px;
    align-items: flex-start;
  }
  
  .register-card {
    width: 100%;
    max-width: 380px;
    min-height: 450px;
    margin: 5px auto;
  }
  
  .register-content {
    padding: 20px;
  }
  
  .register-title {
    font-size: 20px;
  }
  
  .register-subtitle {
    font-size: 14px;
  }
}

/* 确保在大屏幕上也能完美居中 */
@media (min-width: 1200px) {
  .register-card {
    max-width: 520px;
    min-height: 650px;
  }
}

/* 当屏幕高度较小时，确保内容能够滚动 */
@media (max-height: 700px) {
  .register-container {
    align-items: flex-start;
  }
  
  .register-card {
    margin-top: 10px;
    margin-bottom: 10px;
  }
}
</style>