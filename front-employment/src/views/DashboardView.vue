<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>欢迎回来，{{ userInfo?.username }}</h1>
      <p>高校就业服务平台</p>
    </div>
    
    <div class="dashboard-content">
      <div class="card">
        <div class="card-icon">📊</div>
        <div class="card-content">
          <h3>就业统计</h3>
          <p>查看最新的就业数据和趋势分析</p>
        </div>
      </div>
      
      <div class="card">
        <div class="card-icon">💼</div>
        <div class="card-content">
          <h3>企业信息</h3>
          <p>浏览合作企业和招聘信息</p>
        </div>
      </div>
      
      <div class="card">
        <div class="card-icon">📋</div>
        <div class="card-content">
          <h3>个人中心</h3>
          <p>管理个人信息和就业状态</p>
        </div>
      </div>
      
      <div class="card">
        <div class="card-icon">📢</div>
        <div class="card-content">
          <h3>通知公告</h3>
          <p>查看最新的就业政策和通知</p>
        </div>
      </div>
    </div>
    
    <div class="dashboard-footer">
      <p>© 2026 高校就业服务平台 - 祝您求职顺利！</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const userInfo = ref<any>(null);

// 获取用户信息
const loadUserInfo = () => {
  const user = localStorage.getItem('user');
  if (user) {
    userInfo.value = JSON.parse(user);
  }
};

// 组件挂载时加载用户信息
onMounted(() => {
  loadUserInfo();
});
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
  color: white;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 3rem;
}

.dashboard-header h1 {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.dashboard-header p {
  font-size: 1.2rem;
  opacity: 0.9;
}

.dashboard-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto 3rem;
}

.card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
  background: rgba(255, 255, 255, 0.15);
}

.card-icon {
  font-size: 3rem;
}

.card-content h3 {
  font-size: 1.3rem;
  margin-bottom: 0.5rem;
}

.card-content p {
  opacity: 0.8;
  margin: 0;
}

.dashboard-footer {
  text-align: center;
  padding-top: 2rem;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  opacity: 0.7;
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 1rem;
  }
  
  .dashboard-header h1 {
    font-size: 2rem;
  }
  
  .dashboard-content {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
}
</style>