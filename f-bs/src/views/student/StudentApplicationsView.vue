<script setup lang="ts">
import { ref, onMounted } from 'vue'

const applications = ref<any[]>([])
const loading = ref(false)
const selectedStatus = ref('all')

const statusOptions = [
  { value: 'all', label: '全部' },
  { value: 'SUBMITTED', label: '已提交' },
  { value: 'REVIEWING', label: '筛选中' },
  { value: 'INTERVIEW', label: '面试' },
  { value: 'OFFERED', label: '已录用' },
  { value: 'REJECTED', label: '已拒绝' },
]

onMounted(() => {
  // TODO: 从后端加载申请数据
  loading.value = false
})

const getStatusClass = (status: string) => {
  const statusMap: Record<string, string> = {
    SUBMITTED: 'status-submitted',
    REVIEWING: 'status-reviewing',
    INTERVIEW: 'status-interview',
    OFFERED: 'status-offered',
    REJECTED: 'status-rejected',
  }
  return statusMap[status] || ''
}

const getStatusLabel = (status: string) => {
  const option = statusOptions.find(opt => opt.value === status)
  return option?.label || status
}
</script>

<template>
  <div class="student-applications">
    <div class="page-header">
      <h1>求职申请</h1>
      <div class="filter-group">
        <label>状态筛选：</label>
        <select v-model="selectedStatus" class="status-filter">
          <option v-for="opt in statusOptions" :key="opt.value" :value="opt.value">
            {{ opt.label }}
          </option>
        </select>
      </div>
    </div>

    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="applications.length === 0" class="empty-state">
        <div class="empty-icon">💼</div>
        <h3>还没有求职申请</h3>
        <p>浏览职位列表，开始申请您感兴趣的工作</p>
        <RouterLink to="/student/jobs" class="btn-primary">浏览职位</RouterLink>
      </div>

      <div v-else class="applications-list">
        <div v-for="app in applications" :key="app.id" class="application-card">
          <div class="card-header">
            <h3>{{ app.jobTitle }}</h3>
            <span :class="['status-badge', getStatusClass(app.status)]">
              {{ getStatusLabel(app.status) }}
            </span>
          </div>
          <div class="card-body">
            <p class="company">{{ app.companyName }}</p>
            <p class="date">申请时间：{{ app.appliedAt }}</p>
          </div>
          <div class="card-actions">
            <button class="btn-secondary">查看详情</button>
            <button v-if="app.status === 'SUBMITTED'" class="btn-danger">撤回申请</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.student-applications {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.page-header h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #1e293b;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.filter-group label {
  color: #64748b;
  font-weight: 500;
}

.status-filter {
  padding: 0.5rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  color: #1e293b;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.status-filter:focus {
  outline: none;
  border-color: #667eea;
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

.btn-primary {
  display: inline-block;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 10px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.applications-list {
  display: grid;
  gap: 1.5rem;
}

.application-card {
  padding: 1.5rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.application-card:hover {
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

.card-header h3 {
  font-size: 1.2rem;
  color: #1e293b;
  flex: 1;
}

.status-badge {
  padding: 0.375rem 0.75rem;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 600;
  white-space: nowrap;
}

.status-submitted {
  background: #dbeafe;
  color: #1e40af;
}

.status-reviewing {
  background: #fef3c7;
  color: #92400e;
}

.status-interview {
  background: #e0e7ff;
  color: #4338ca;
}

.status-offered {
  background: #d1fae5;
  color: #065f46;
}

.status-rejected {
  background: #fee2e2;
  color: #991b1b;
}

.card-body {
  margin-bottom: 1rem;
}

.company {
  color: #64748b;
  font-size: 1rem;
  margin-bottom: 0.5rem;
}

.date {
  color: #94a3b8;
  font-size: 0.875rem;
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
  .student-applications {
    padding: 1rem;
  }

  .page-header {
    flex-direction: column;
    align-items: stretch;
  }

  .page-header h1 {
    font-size: 1.5rem;
  }

  .filter-group {
    justify-content: space-between;
  }

  .card-header {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>

