<script setup lang="ts">
import { ref, onMounted } from 'vue'

const resumes = ref<any[]>([])
const loading = ref(false)

onMounted(() => {
  // TODO: 从后端加载简历数据
  loading.value = false
})

const createNewResume = () => {
  // TODO: 创建新简历
  alert('创建新简历功能开发中...')
}
</script>

<template>
  <div class="student-resume">
    <div class="page-header">
      <h1>简历管理</h1>
      <button class="btn-primary" @click="createNewResume">
        <span>➕</span>
        创建新简历
      </button>
    </div>

    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="resumes.length === 0" class="empty-state">
        <div class="empty-icon">📄</div>
        <h3>还没有简历</h3>
        <p>创建您的第一份简历，开始求职之旅</p>
        <button class="btn-primary" @click="createNewResume">创建简历</button>
      </div>

      <div v-else class="resumes-grid">
        <div v-for="resume in resumes" :key="resume.id" class="resume-card">
          <h3>{{ resume.title }}</h3>
          <p>{{ resume.summary }}</p>
          <div class="resume-actions">
            <button class="btn-secondary">编辑</button>
            <button class="btn-secondary">预览</button>
            <button class="btn-danger">删除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.student-resume {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #1e293b;
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.content {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #64748b;
  font-size: 1.1rem;
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-state h3 {
  font-size: 1.5rem;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.empty-state p {
  color: #64748b;
  margin-bottom: 2rem;
}

.resumes-grid {
  display: grid;
  gap: 1.5rem;
}

.resume-card {
  padding: 1.5rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.resume-card:hover {
  border-color: #667eea;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.1);
}

.resume-card h3 {
  font-size: 1.2rem;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.resume-card p {
  color: #64748b;
  margin-bottom: 1rem;
}

.resume-actions {
  display: flex;
  gap: 0.75rem;
}

.btn-secondary {
  padding: 0.5rem 1rem;
  background: #f1f5f9;
  color: #475569;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-secondary:hover {
  background: #e2e8f0;
}

.btn-danger {
  padding: 0.5rem 1rem;
  background: #fee2e2;
  color: #dc2626;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-danger:hover {
  background: #fecaca;
}

@media (max-width: 768px) {
  .student-resume {
    padding: 1rem;
  }

  .page-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .page-header h1 {
    font-size: 1.5rem;
  }
}
</style>

