<script setup lang="ts">
import { reactive, ref } from 'vue'

const roles = [
  { value: 'STUDENT', label: '学生' },
  { value: 'TEACHER', label: '教师' },
  { value: 'EMPLOYER', label: '企业' },
  { value: 'ADMIN', label: '管理员' },
]

const form = reactive({
  username: '',
  password: '',
  fullName: '',
  email: '',
  phone: '',
  role: 'STUDENT',
})

const loading = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error' | ''>('')

const submit = async () => {
  if (!form.username || !form.password || !form.fullName || !form.email) {
    message.value = '请完整填写必填信息'
    messageType.value = 'error'
    return
  }

  loading.value = true
  message.value = ''
  messageType.value = ''

  try {
    const response = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(form),
    })

    const data = await response.json()

    if (response.ok && data.code === 200) {
      message.value = data.message || '注册成功'
      messageType.value = 'success'
    } else {
      message.value = data.message || '注册失败，请稍后再试'
      messageType.value = 'error'
    }
  } catch (error) {
    console.error(error)
    message.value = '无法连接服务器，请稍后重试'
    messageType.value = 'error'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="card">
    <h2>创建新用户</h2>
    <p class="subtitle">填写基本信息完成注册，可用于登录就业管理系统</p>

    <form class="form" @submit.prevent="submit">
      <label>
        用户名<span>*</span>
        <input v-model.trim="form.username" type="text" placeholder="请输入用户名" autocomplete="username" />
      </label>

      <label>
        密码<span>*</span>
        <input
          v-model.trim="form.password"
          type="password"
          placeholder="至少6位密码"
          autocomplete="new-password"
        />
      </label>

      <label>
        姓名<span>*</span>
        <input v-model.trim="form.fullName" type="text" placeholder="请输入真实姓名" />
      </label>

      <label>
        邮箱<span>*</span>
        <input v-model.trim="form.email" type="email" placeholder="请输入邮箱地址" autocomplete="email" />
      </label>

      <label>
        手机
        <input v-model.trim="form.phone" type="tel" placeholder="可选，填写联系电话" autocomplete="tel" />
      </label>

      <label>
        角色
        <select v-model="form.role">
          <option v-for="option in roles" :key="option.value" :value="option.value">{{ option.label }}</option>
        </select>
      </label>

      <button type="submit" :disabled="loading">
        {{ loading ? '正在提交...' : '注册' }}
      </button>
    </form>

    <p v-if="message" :class="['message', messageType]">{{ message }}</p>

    <div class="login-section">
      <p class="hint">已拥有账户？点击下方"登录"按钮返回登录页面。</p>
      <RouterLink to="/login" class="login-button">返回登录</RouterLink>
    </div>
  </section>
</template>

<style scoped>
.card {
  width: 100%;
  max-width: 500px;
  padding: 2rem 2rem;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 20px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(10px);
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.card h2 {
  font-size: 1.75rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.subtitle {
  margin: 0;
  color: #64748b;
  font-size: 0.875rem;
}

.form {
  display: grid;
  gap: 1rem;
}

label {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  font-weight: 600;
  color: #334155;
  font-size: 0.875rem;
}

label span {
  color: #ef4444;
  margin-left: 0.25rem;
}

input,
select {
  padding: 0.75rem 1rem;
  border-radius: 10px;
  border: 2px solid #e2e8f0;
  font-size: 0.95rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: #f8fafc;
}

input:focus,
select:focus {
  outline: none;
  border-color: #667eea;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
  transform: translateY(-1px);
}

button {
  margin-top: 0.25rem;
  padding: 0.75rem;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
  box-shadow: none;
}

button:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

button:not(:disabled):active {
  transform: translateY(0);
}

.message {
  padding: 0.75rem 1rem;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.875rem;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.success {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  color: #065f46;
  border: 1px solid #6ee7b7;
}

.message.error {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #991b1b;
  border: 1px solid #fca5a5;
}

.hint {
  color: #64748b;
  margin: 0;
  text-align: center;
  font-size: 0.85rem;
}

.login-section {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-top: 0.25rem;
  padding-top: 0.75rem;
  border-top: 1px solid #e2e8f0;
}

.login-button {
  display: block;
  padding: 0.75rem;
  border-radius: 10px;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
  text-align: center;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(99, 102, 241, 0.4);
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(99, 102, 241, 0.5);
}

.login-button:active {
  transform: translateY(0);
}

@media (max-width: 600px) {
  .card {
    padding: 1.75rem 1.5rem;
    box-shadow: 0 20px 40px -12px rgba(0, 0, 0, 0.2);
  }
  
  .card h2 {
    font-size: 1.5rem;
  }
}
</style>
