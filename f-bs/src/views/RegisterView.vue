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

    <p class="hint">已拥有账户？点击上方“登录”按钮返回登录页面。</p>
  </section>
</template>

<style scoped>
.card {
  width: min(520px, 100%);
  padding: 2.5rem 2rem;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 18px;
  box-shadow: 0 18px 40px rgba(99, 102, 241, 0.18);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.subtitle {
  margin: 0;
  color: #6b7280;
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
  color: #374151;
}

label span {
  color: #ef4444;
  margin-left: 0.25rem;
}

input,
select {
  padding: 0.75rem 1rem;
  border-radius: 10px;
  border: 1px solid #cbd5f5;
  font-size: 1rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

input:focus,
select:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 4px rgba(79, 70, 229, 0.2);
}

button {
  margin-top: 0.5rem;
  padding: 0.75rem;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #4f46e5, #4338ca);
  color: #fff;
  font-size: 1.05rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.7;
  box-shadow: none;
}

button:not(:disabled):hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(79, 70, 229, 0.25);
}

.message {
  padding: 0.75rem 1rem;
  border-radius: 8px;
  font-weight: 600;
}

.message.success {
  background-color: rgba(34, 197, 94, 0.15);
  color: #15803d;
}

.message.error {
  background-color: rgba(248, 113, 113, 0.18);
  color: #b91c1c;
}

.hint {
  color: #6b7280;
  margin: 0;
}

@media (max-width: 600px) {
  .card {
    padding: 2rem 1.5rem;
    box-shadow: none;
  }
}
</style>
