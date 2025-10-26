<script setup lang="ts">
import { ref, onMounted } from 'vue'

const interviews = ref<any[]>([])
const loading = ref(false)

onMounted(() => {
  // TODO: 从后端加载面试数据
  loading.value = false
})

const getStatusClass = (status: string) => {
  const statusMap: Record<string, string> = {
    SCHEDULED: 'status-scheduled',
    COMPLETED: 'status-completed',
    CANCELLED: 'status-cancelled',
  }
  return statusMap[status] || ''
}

const getStatusLabel = (status: string) => {
  const labelMap: Record<string, string> = {
    SCHEDULED: '已安排',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
  }
  return labelMap[status] || status
}
</script>

<template>
  <div class="student-interviews">
    <div class="page-header">
      <h1>面试管理</h1>
      <p class="subtitle">管理您的面试安排和反馈</p>
    </div>

    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="interviews.length === 0" class="empty-state">
        <div class="empty-icon">🎯</div>
        <h3>还没有面试安排</h3>
        <p>当企业向您发送面试邀请时，将在这里显示</p>
      </div>

      <div v-else class="interviews-list">
        <div v-for="interview in interviews" :key="interview.id" class="interview-card">
          <div class="card-header">
            <div class="header-left">
              <h3>{{ interview.jobTitle }}</h3>
              <p class="company">{{ interview.companyName }}</p>
            </div>
            <span :class="['status-badge', getStatusClass(interview.status)]">
              {{ getStatusLabel(interview.status) }}
            </span>
          </div>
          
          <div class="card-body">
            <div class="info-row">
              <span class="label">📅 面试时间：</span>
              <span class="value">{{ interview.scheduledTime }}</span>
            </div>
            <div class="info-row" v-if="interview.location">
              <span class="label">📍 面试地点：</span>
              <span class="value">{{ interview.location }}</span>
            </div>
            <div class="info-row" v-if="interview.meetingLink">
              <span class="label">🔗 会议链接：</span>
              <a :href="interview.meetingLink" target="_blank" class="link">点击加入</a>
            </div>
            <div class="info-row" v-if="interview.feedback && interview.status === 'COMPLETED'">
              <span class="label">💬 面试反馈：</span>
              <span class="value">{{ interview.feedback }}</span>
            </div>
          </div>

          <div class="card-actions">
            <button class="btn-secondary">查看详情</button>
            <button v-if="interview.status === 'SCHEDULED'" class="btn-danger">取消面试</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.student-interviews {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: #64748b;
  font-size: 1rem;
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
}

.interviews-list {
  display: grid;
  gap: 1.5rem;
}

.interview-card {
  padding: 1.5rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.interview-card:hover {
  border-color: #667eea;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
  gap: 1rem;
}

.header-left {
  flex: 1;
}

.header-left h3 {
  font-size: 1.2rem;
  color: #1e293b;
  margin-bottom: 0.25rem;
}

.company {
  color: #64748b;
  font-size: 0.95rem;
}

.status-badge {
  padding: 0.375rem 0.75rem;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 600;
  white-space: nowrap;
}

.status-scheduled {
  background: #dbeafe;
  color: #1e40af;
}

.status-completed {
  background: #d1fae5;
  color: #065f46;
}

.status-cancelled {
  background: #fee2e2;
  color: #991b1b;
}

.card-body {
  margin-bottom: 1rem;
}

.info-row {
  display: flex;
  margin-bottom: 0.75rem;
  gap: 0.5rem;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  color: #64748b;
  font-weight: 500;
  min-width: 120px;
}

.value {
  color: #1e293b;
  flex: 1;
}

.link {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.link:hover {
  text-decoration: underline;
}

.card-actions {
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
  .student-interviews {
    padding: 1rem;
  }

  .page-header h1 {
    font-size: 1.5rem;
  }

  .card-header {
    flex-direction: column;
    align-items: stretch;
  }

  .info-row {
    flex-direction: column;
  }

  .label {
    min-width: auto;
  }
}
</style>

