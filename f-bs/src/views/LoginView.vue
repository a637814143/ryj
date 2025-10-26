<script setup lang="ts">
import { reactive, ref } from 'vue'

const form = reactive({
  username: '',
  password: '',
})

const loading = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error' | ''>('')

const submit = async () => {
  if (!form.username || !form.password) {
    message.value = '请输入用户名和密码'
    messageType.value = 'error'
    return
  }

  loading.value = true
  message.value = ''
  messageType.value = ''

  try {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(form),
    })

    const data = await response.json()

    if (response.ok && data.code === 200) {
      message.value = data.message || '登录成功'
      messageType.value = 'success'
    } else {
      message.value = data.message || '用户名或密码错误'
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
    <h2>账户登录</h2>
    <p class="subtitle">使用注册时填写的用户名和密码进行登录</p>

    <form class="form" @submit.prevent="submit">
      <label>
        用户名
        <input v-model.trim="form.username" type="text" placeholder="请输入用户名" autocomplete="username" />
      </label>

      <label>
        密码
        <input
          v-model.trim="form.password"
          type="password"
          placeholder="请输入密码"
          autocomplete="current-password"
        />
      </label>

      <button type="submit" :disabled="loading">
        {{ loading ? '正在登录...' : '登录' }}
      </button>
    </form>

    <p v-if="message" :class="['message', messageType]">{{ message }}</p>

    <p class="hint">还没有账户？点击上方“注册”按钮创建新用户。</p>
  </section>
</template>

<style scoped>
.card {
  width: min(420px, 100%);
  padding: 2.5rem 2rem;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 18px;
  box-shadow: 0 18px 40px rgba(37, 99, 235, 0.15);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.subtitle {
  margin: 0;
  color: #6b7280;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

label {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  font-weight: 600;
  color: #374151;
}

input {
  padding: 0.75rem 1rem;
  border-radius: 10px;
  border: 1px solid #cbd5f5;
  font-size: 1rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.2);
}

button {
  margin-top: 0.5rem;
  padding: 0.75rem;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
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
  box-shadow: 0 12px 20px rgba(37, 99, 235, 0.25);
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
