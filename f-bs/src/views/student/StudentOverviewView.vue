<script setup lang="ts">
import { ref, onMounted } from 'vue'

const userInfo = ref<any>(null)

onMounted(() => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    try {
      userInfo.value = JSON.parse(stored)
    } catch (e) {
      console.error('Failed to parse user info:', e)
    }
  }
})
</script>

<template>
  <div class="student-overview">
    <div class="page-header">
      <h1>学生总览</h1>
      <p class="subtitle">欢迎使用高校就业管理系统</p>
    </div>

    <div class="welcome-card">
      <div class="welcome-content">
        <h2>👋 欢迎，{{ userInfo?.fullName || userInfo?.username || '同学' }}</h2>
        <p>这是您的学生信息中心，您可以在这里管理个人信息、简历、求职申请和面试安排。</p>
      </div>
    </div>

    <div class="quick-links">
      <h3>快速入口</h3>
      <div class="links-grid">
        <RouterLink to="/student/profile" class="link-card">
          <div class="icon">📝</div>
          <h4>个人档案</h4>
          <p>管理个人基本信息</p>
        </RouterLink>

        <RouterLink to="/student/resume" class="link-card">
          <div class="icon">📄</div>
          <h4>简历管理</h4>
          <p>创建和编辑简历</p>
        </RouterLink>

        <RouterLink to="/student/applications" class="link-card">
          <div class="icon">💼</div>
          <h4>求职申请</h4>
          <p>查看申请状态</p>
        </RouterLink>

        <RouterLink to="/student/interviews" class="link-card">
          <div class="icon">🎯</div>
          <h4>面试管理</h4>
          <p>管理面试安排</p>
        </RouterLink>

        <RouterLink to="/student/intention" class="link-card">
          <div class="icon">🎓</div>
          <h4>就业意向</h4>
          <p>设置就业意向</p>
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<style scoped>
.student-overview {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  text-align: center;
  margin-bottom: 3rem;
}

.page-header h1 {
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: #64748b;
  font-size: 1.1rem;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 2.5rem;
  margin-bottom: 3rem;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
}

.welcome-content h2 {
  color: white;
  font-size: 1.8rem;
  margin-bottom: 0.75rem;
}

.welcome-content p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 1.1rem;
  line-height: 1.6;
}

.quick-links h3 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #1e293b;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.link-card {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  text-decoration: none;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  text-align: center;
}

.link-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.link-card .icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.link-card h4 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.link-card p {
  color: #64748b;
  font-size: 0.95rem;
}

@media (max-width: 768px) {
  .student-overview {
    padding: 1rem;
  }

  .page-header h1 {
    font-size: 2rem;
  }

  .welcome-card {
    padding: 1.5rem;
  }

  .links-grid {
    grid-template-columns: 1fr;
  }
}
</style>

