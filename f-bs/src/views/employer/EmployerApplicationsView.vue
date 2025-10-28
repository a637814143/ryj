<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import {
  fetchEmployerApplications,
  updateEmployerApplicationStatus,
  type EmployerApplicationOverview,
  type JobApplicationStatus,
} from '../../api/employer'

const userInfo = ref<{ id?: number; role?: string } | null>(null)
const userId = ref<number | null>(null)
const applications = ref<EmployerApplicationOverview[]>([])
const loading = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error' | ''>('')
const filterStatus = ref<'ALL' | JobApplicationStatus>('ALL')

const selectedStatus = reactive<Record<number, JobApplicationStatus>>({})
const savingMap = reactive<Record<number, boolean>>({})

const statusOptions: { value: 'ALL' | JobApplicationStatus; label: string }[] = [
  { value: 'ALL', label: '全部状态' },
  { value: 'SUBMITTED', label: '已提交' },
  { value: 'REVIEWING', label: '筛选中' },
  { value: 'INTERVIEW', label: '面试中' },
  { value: 'OFFERED', label: '已录用' },
  { value: 'REJECTED', label: '已淘汰' },
]

const resetMessage = () => {
  message.value = ''
  messageType.value = ''
}

const applySelections = () => {
  selectedStatusClear()
  for (const item of applications.value) {
    selectedStatus[item.id] = item.status
  }
}

const selectedStatusClear = () => {
  for (const key of Object.keys(selectedStatus)) {
    delete selectedStatus[Number(key)]
  }
}

const loadApplications = async () => {
  if (!userId.value) return
  loading.value = true
  resetMessage()
  try {
    const status = filterStatus.value === 'ALL' ? undefined : filterStatus.value
    applications.value = await fetchEmployerApplications(userId.value, status)
    applySelections()
    if (!applications.value.length) {
      message.value = status ? `暂无状态为 ${status} 的申请` : '暂时没有新的投递记录'
      messageType.value = 'error'
    }
  } catch (err) {
    console.error(err)
    message.value = (err as Error).message || '加载投递记录失败'
    messageType.value = 'error'
  } finally {
    loading.value = false
  }
}

const updateStatus = async (application: EmployerApplicationOverview) => {
  if (!userId.value) return
  const nextStatus = selectedStatus[application.id]
  if (!nextStatus || nextStatus === application.status) {
    message.value = '请选择不同的状态后再保存'
    messageType.value = 'error'
    return
  }
  resetMessage()
  savingMap[application.id] = true
  try {
    await updateEmployerApplicationStatus(userId.value, application.id, { status: nextStatus })
    application.status = nextStatus
    message.value = `申请「${application.candidateName || '候选人'}」状态已更新`
    messageType.value = 'success'
  } catch (err) {
    console.error(err)
    message.value = (err as Error).message || '更新申请状态失败'
    messageType.value = 'error'
    selectedStatus[application.id] = application.status
  } finally {
    savingMap[application.id] = false
  }
}

watch(filterStatus, () => {
  loadApplications()
})

onMounted(() => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    try {
      userInfo.value = JSON.parse(stored)
      userId.value = userInfo.value?.id ?? null
    } catch (err) {
      console.error('解析用户信息失败', err)
      userInfo.value = null
      userId.value = null
    }
  }
  if (!userId.value || userInfo.value?.role !== 'EMPLOYER') {
    message.value = '仅企业用户可查看投递管理'
    messageType.value = 'error'
    return
  }
  loadApplications()
})
</script>

<template>
  <div class="applications-page">
    <header class="page-header">
      <div>
        <h1>应聘管理</h1>
        <p class="subtitle">集中处理候选人投递，及时反馈面试邀约与录用结果</p>
      </div>
      <RouterLink class="back-link" to="/employer/overview">
        <span class="icon">←</span>
        <span>返回总览</span>
      </RouterLink>
    </header>

    <section v-if="message" :class="['message', messageType]">{{ message }}</section>

    <div v-if="userInfo?.role !== 'EMPLOYER'" class="guard">请使用企业账号登录以管理投递。</div>

    <section v-else class="content">
      <div class="filters">
        <label>
          状态筛选
          <select v-model="filterStatus">
            <option v-for="option in statusOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
          </select>
        </label>
      </div>

      <div class="list" :class="{ loading }">
        <p v-if="loading" class="empty">正在加载投递记录...</p>
        <p v-else-if="!applications.length" class="empty">暂无符合条件的投递记录。</p>
        <ul v-else>
          <li v-for="application in applications" :key="application.id">
            <div class="header-line">
              <div class="candidate">
                <h3>{{ application.candidateName || '候选人' }}</h3>
                <p>申请岗位：{{ application.jobTitle || '岗位已下架' }}</p>
              </div>
              <div class="status-select">
                <select v-model="selectedStatus[application.id]">
                  <option value="SUBMITTED">已提交</option>
                  <option value="REVIEWING">筛选中</option>
                  <option value="INTERVIEW">面试中</option>
                  <option value="OFFERED">已录用</option>
                  <option value="REJECTED">已淘汰</option>
                </select>
                <button type="button" @click="updateStatus(application)" :disabled="savingMap[application.id]">
                  {{ savingMap[application.id] ? '保存中...' : '更新状态' }}
                </button>
              </div>
            </div>
            <div class="meta">
              <span>申请时间：{{ application.appliedAt ? new Date(application.appliedAt).toLocaleString() : '未知' }}</span>
              <span>申请编号：{{ application.id }}</span>
              <span>简历ID：{{ application.resumeId || '未记录' }}</span>
              <RouterLink v-if="application.resumeId" :to="`/resume/detail?id=${application.resumeId}`" class="view-resume-link">
                查看简历
              </RouterLink>
            </div>
          </li>
        </ul>
      </div>
    </section>
  </div>
</template>

<style scoped>
.applications-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem 1.5rem 4rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.page-header h1 {
  margin: 0;
  font-size: 2rem;
  color: #1e293b;
}

.subtitle {
  margin: 0.35rem 0 0;
  color: #64748b;
}

.back-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border-radius: 12px;
  background: linear-gradient(135deg, #ffffff, #f8fafc);
  color: #475569;
  text-decoration: none;
  font-weight: 600;
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.back-link:hover {
  transform: translateX(-4px);
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border-color: #cbd5e1;
}

.back-link .icon {
  font-size: 1.2rem;
  transition: transform 0.3s;
}

.back-link:hover .icon {
  transform: translateX(-2px);
}

.message {
  padding: 0.9rem 1.1rem;
  border-radius: 12px;
  margin-bottom: 1.2rem;
  font-size: 0.95rem;
}

.message.success {
  background: rgba(34, 197, 94, 0.12);
  color: #15803d;
  border: 1px solid rgba(34, 197, 94, 0.25);
}

.message.error {
  background: rgba(248, 113, 113, 0.12);
  color: #b91c1c;
  border: 1px solid rgba(248, 113, 113, 0.25);
}

.guard {
  padding: 2rem;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  color: #475569;
}

.content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.filters {
  display: flex;
  justify-content: flex-end;
}

.filters label {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  color: #334155;
}

.filters select {
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border: 1px solid #cbd5f5;
  font-size: 0.95rem;
}

.list {
  background: #ffffff;
  border-radius: 18px;
  padding: 1.5rem;
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.08);
  min-height: 200px;
}

.list.loading {
  opacity: 0.7;
}

.list ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.list li {
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 1.2rem;
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.header-line {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}

.candidate h3 {
  margin: 0;
  color: #0f172a;
}

.candidate p {
  margin: 0.2rem 0 0;
  color: #475569;
  font-size: 0.95rem;
}

.status-select {
  display: flex;
  gap: 0.5rem;
}

.status-select select {
  padding: 0.6rem 0.8rem;
  border-radius: 8px;
  border: 1px solid #cbd5f5;
  font-weight: 600;
}

.status-select button {
  padding: 0.6rem 1.1rem;
  border-radius: 8px;
  border: none;
  background: #2563eb;
  color: #fff;
  font-weight: 600;
  cursor: pointer;
}

.status-select button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1.2rem;
  align-items: center;
  font-size: 0.9rem;
  color: #64748b;
}

.view-resume-link {
  padding: 0.4rem 0.9rem;
  border-radius: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  text-decoration: none;
  font-size: 0.85rem;
  font-weight: 600;
  transition: all 0.2s;
}

.view-resume-link:hover {
  background: linear-gradient(135deg, #5568d3 0%, #653a91 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(102, 126, 234, 0.3);
}

.empty {
  color: #94a3b8;
  font-size: 0.95rem;
}

@media (max-width: 640px) {
  .status-select {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
