<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import {
  fetchEmployerInterviews,
  createEmployerInterview,
  updateEmployerInterview,
  deleteEmployerInterview,
  type EmployerInterviewOverview,
  type EmployerInterviewRequestPayload,
  type InterviewStatus,
} from '../../api/employer'

const userInfo = ref<{ id?: number; role?: string } | null>(null)
const userId = ref<number | null>(null)
const interviews = ref<EmployerInterviewOverview[]>([])
const loading = ref(false)
const saving = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error' | ''>('')
const editingId = ref<number | null>(null)

const form = reactive({
  jobId: '',
  applicationId: '',
  scheduledTime: '',
  location: '',
  meetingLink: '',
  status: 'SCHEDULED' as InterviewStatus,
  feedback: '',
})

const statusOptions: { value: InterviewStatus; label: string }[] = [
  { value: 'SCHEDULED', label: '已安排' },
  { value: 'COMPLETED', label: '已完成' },
  { value: 'CANCELLED', label: '已取消' },
]

const statusLabels: Record<InterviewStatus, string> = {
  SCHEDULED: '已安排',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
}

const orderedInterviews = computed(() =>
  [...interviews.value].sort((a, b) => {
    const timeA = a.scheduledTime ? new Date(a.scheduledTime).getTime() : 0
    const timeB = b.scheduledTime ? new Date(b.scheduledTime).getTime() : 0
    return timeB - timeA
  }),
)

const resetMessage = () => {
  message.value = ''
  messageType.value = ''
}

const resetForm = () => {
  editingId.value = null
  form.jobId = ''
  form.applicationId = ''
  form.scheduledTime = ''
  form.location = ''
  form.meetingLink = ''
  form.status = 'SCHEDULED'
  form.feedback = ''
}

const fillForm = (interview: EmployerInterviewOverview) => {
  editingId.value = interview.id
  form.jobId = interview.jobId ? String(interview.jobId) : ''
  form.applicationId = interview.applicationId ? String(interview.applicationId) : ''
  form.scheduledTime = interview.scheduledTime ? interview.scheduledTime.slice(0, 16) : ''
  form.location = interview.location || ''
  form.meetingLink = interview.meetingLink || ''
  form.status = interview.status || 'SCHEDULED'
  form.feedback = interview.feedback || ''
}

const loadInterviews = async () => {
  if (!userId.value) return
  loading.value = true
  resetMessage()
  try {
    interviews.value = await fetchEmployerInterviews(userId.value)
    if (!interviews.value.length) {
      message.value = '暂无面试安排，可以通过右侧表单新建'
      messageType.value = 'error'
    }
  } catch (err) {
    console.error(err)
    message.value = (err as Error).message || '加载面试安排失败'
    messageType.value = 'error'
  } finally {
    loading.value = false
  }
}

const toPayload = (): EmployerInterviewRequestPayload => {
  if (!form.scheduledTime) {
    throw new Error('请选择面试时间')
  }
  return {
    jobId: Number(form.jobId),
    applicationId: Number(form.applicationId),
    scheduledTime: new Date(form.scheduledTime).toISOString(),
    location: form.location.trim() || null,
    meetingLink: form.meetingLink.trim() || null,
    status: form.status,
    feedback: form.feedback.trim() || null,
  }
}

const submit = async () => {
  if (!userId.value) {
    message.value = '请先登录企业账号'
    messageType.value = 'error'
    return
  }
  if (!form.jobId || !form.applicationId) {
    message.value = '请填写岗位ID和申请ID'
    messageType.value = 'error'
    return
  }
  try {
    const payload = toPayload()
    saving.value = true
    resetMessage()
    if (editingId.value) {
      await updateEmployerInterview(userId.value, editingId.value, payload)
      message.value = '面试安排已更新'
    } else {
      await createEmployerInterview(userId.value, payload)
      message.value = '面试安排已创建'
    }
    messageType.value = 'success'
    resetForm()
    await loadInterviews()
  } catch (err) {
    console.error(err)
    message.value = (err as Error).message || '保存面试安排失败'
    messageType.value = 'error'
  } finally {
    saving.value = false
  }
}

const startEdit = (interview: EmployerInterviewOverview) => {
  resetMessage()
  fillForm(interview)
}

const startCreate = () => {
  resetMessage()
  resetForm()
}

const removeInterview = async (interview: EmployerInterviewOverview) => {
  if (!userId.value) return
  const confirmed = window.confirm(`确认删除与「${interview.candidateName || '候选人'}」的面试安排吗？`)
  if (!confirmed) return
  saving.value = true
  resetMessage()
  try {
    await deleteEmployerInterview(userId.value, interview.id)
    message.value = '面试安排已删除'
    messageType.value = 'success'
    if (editingId.value === interview.id) {
      resetForm()
    }
    await loadInterviews()
  } catch (err) {
    console.error(err)
    message.value = (err as Error).message || '删除面试安排失败'
    messageType.value = 'error'
  } finally {
    saving.value = false
  }
}

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
    message.value = '仅企业用户可管理面试安排'
    messageType.value = 'error'
    return
  }
  loadInterviews()
})
</script>

<template>
  <div class="interviews-page">
    <header class="page-header">
      <div class="title-block">
        <h1>📋 面试安排管理</h1>
        <p class="subtitle">快速创建、更新面试邀约，跟进候选人进度</p>
      </div>
      <RouterLink class="back-link" to="/employer/overview">
        <span class="icon">←</span>
        <span>返回总览</span>
      </RouterLink>
    </header>

    <section v-if="message" :class="['message', messageType]">
      <span class="message-icon">{{ messageType === 'success' ? '✓' : '⚠' }}</span>
      {{ message }}
    </section>

    <div v-if="userInfo?.role !== 'EMPLOYER'" class="guard">
      <span class="guard-icon">🔒</span>
      <p>请使用企业账号登录以管理面试</p>
    </div>

    <div v-else class="layout">
      <section class="list-section" :class="{ loading }">
        <div class="section-header">
          <div class="header-content">
            <h2>📅 面试列表</h2>
            <span class="count">{{ orderedInterviews.length }} 场面试</span>
          </div>
        </div>
        
        <p v-if="loading" class="empty">
          <span class="loading-spinner">⏳</span>
          正在加载面试记录...
        </p>
        <p v-else-if="!orderedInterviews.length" class="empty">
          <span class="empty-icon">📭</span>
          暂无面试安排
        </p>
        
        <div v-else class="interview-list">
          <article 
            v-for="interview in orderedInterviews" 
            :key="interview.id" 
            :class="['interview-card', { active: editingId === interview.id, [interview.status.toLowerCase()]: true }]"
          >
            <div class="card-header">
              <div class="candidate-info">
                <div class="avatar">{{ (interview.candidateName || '候选人').charAt(0) }}</div>
                <div class="candidate-details">
                  <h3>{{ interview.candidateName || '候选人' }}</h3>
                  <p class="job-title">💼 {{ interview.jobTitle || '岗位未知' }}</p>
                </div>
              </div>
              <span :class="['status-badge', interview.status.toLowerCase()]">
                {{ statusLabels[interview.status] || interview.status }}
              </span>
            </div>
            
            <div class="card-body">
              <div class="info-row">
                <span class="icon">🕒</span>
                <span class="label">面试时间</span>
                <span class="value">{{ interview.scheduledTime ? new Date(interview.scheduledTime).toLocaleString() : '待定' }}</span>
              </div>
              <div class="info-row" v-if="interview.location">
                <span class="icon">📍</span>
                <span class="label">面试地点</span>
                <span class="value">{{ interview.location }}</span>
              </div>
              <div class="info-row" v-if="interview.meetingLink">
                <span class="icon">🔗</span>
                <span class="label">会议链接</span>
                <span class="value link">{{ interview.meetingLink }}</span>
              </div>
              <div class="feedback-section" v-if="interview.feedback">
                <span class="icon">💬</span>
                <span class="label">面试反馈</span>
                <p class="feedback-text">{{ interview.feedback }}</p>
              </div>
            </div>
            
            <div class="card-actions">
              <button type="button" class="btn-edit" @click="startEdit(interview)">
                <span class="btn-icon">✏️</span>
                编辑
              </button>
              <button type="button" class="btn-delete" @click="removeInterview(interview)" :disabled="saving">
                <span class="btn-icon">🗑️</span>
                删除
              </button>
            </div>
          </article>
        </div>
      </section>

      <form class="interview-form" @submit.prevent="submit">
        <div class="form-header">
          <h2>{{ editingId ? '✏️ 编辑面试' : '➕ 创建面试安排' }}</h2>
          <button type="button" class="btn-new" @click="startCreate" :disabled="saving">
            <span class="plus-icon">+</span>
            新建
          </button>
        </div>
        
        <div class="form-content">
          <div class="form-section">
            <h3 class="section-title">基本信息</h3>
            <div class="inline-group">
              <label class="form-field">
                <span class="field-label">岗位ID <span class="required">*</span></span>
                <input v-model="form.jobId" type="number" placeholder="输入岗位ID" :disabled="saving" />
              </label>
              <label class="form-field">
                <span class="field-label">申请ID <span class="required">*</span></span>
                <input v-model="form.applicationId" type="number" placeholder="输入申请ID" :disabled="saving" />
              </label>
            </div>
          </div>

          <div class="form-section">
            <h3 class="section-title">面试安排</h3>
            <label class="form-field">
              <span class="field-label">面试时间 <span class="required">*</span></span>
              <input v-model="form.scheduledTime" type="datetime-local" :disabled="saving" />
            </label>
            
            <label class="form-field">
              <span class="field-label">面试地点</span>
              <input v-model="form.location" type="text" placeholder="例如：总部大厦A座会议室" :disabled="saving" />
            </label>
            
            <label class="form-field">
              <span class="field-label">会议链接</span>
              <input v-model="form.meetingLink" type="text" placeholder="线上面试填写会议链接" :disabled="saving" />
            </label>
          </div>

          <div class="form-section">
            <h3 class="section-title">状态与反馈</h3>
            <label class="form-field">
              <span class="field-label">面试状态</span>
              <select v-model="form.status" :disabled="saving">
                <option v-for="option in statusOptions" :key="option.value" :value="option.value">
                  {{ option.label }}
                </option>
              </select>
            </label>
            
            <label class="form-field">
              <span class="field-label">面试反馈</span>
              <textarea 
                v-model="form.feedback" 
                rows="4" 
                placeholder="记录候选人表现、后续跟进建议等" 
                :disabled="saving"
              ></textarea>
            </label>
          </div>
        </div>
        
        <div class="form-actions">
          <button type="submit" class="btn-submit" :disabled="saving">
            <span class="btn-icon">{{ saving ? '⏳' : '✓' }}</span>
            {{ saving ? '正在保存...' : editingId ? '更新面试' : '创建面试' }}
          </button>
          <button type="button" class="btn-reset" @click="resetForm" :disabled="saving">
            <span class="btn-icon">↺</span>
            重置
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
/* 整体布局 */
.interviews-page {
  max-width: 1280px;
  margin: 0 auto;
  padding: 1.5rem 1.5rem 4rem;
  min-height: 100vh;
}

/* 页头 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 2rem;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
}

.title-block h1 {
  margin: 0;
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #1e293b, #475569);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
}

.subtitle {
  margin: 0.75rem 0 0;
  font-size: 1rem;
  color: #64748b;
  font-weight: 500;
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

/* 消息提示 */
.message {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-radius: 14px;
  margin-bottom: 1.5rem;
  font-size: 0.95rem;
  font-weight: 500;
  animation: slideIn 0.3s ease;
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

.message-icon {
  font-size: 1.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 1.75rem;
  height: 1.75rem;
  border-radius: 50%;
}

.message.success {
  background: linear-gradient(135deg, #d1fae5, #a7f3d0);
  color: #065f46;
  border: 1px solid #6ee7b7;
}

.message.success .message-icon {
  background: #10b981;
  color: white;
}

.message.error {
  background: linear-gradient(135deg, #fee2e2, #fecaca);
  color: #991b1b;
  border: 1px solid #fca5a5;
}

.message.error .message-icon {
  background: #ef4444;
  color: white;
}

/* 权限提示 */
.guard {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 3rem;
  border-radius: 20px;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border: 1px solid #e2e8f0;
  text-align: center;
}

.guard-icon {
  font-size: 3rem;
}

.guard p {
  margin: 0;
  color: #475569;
  font-size: 1.1rem;
  font-weight: 500;
}

/* 主布局 */
.layout {
  display: grid;
  grid-template-columns: 1.3fr 1fr;
  gap: 1.5rem;
  align-items: start;
}

/* 面试列表区域 */
.list-section {
  background: #ffffff;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04), 0 1px 4px rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(226, 232, 240, 0.6);
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.list-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6, #ec4899);
}

.list-section.loading {
  opacity: 0.6;
}

.section-header {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f1f5f9;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #0f172a;
}

.count {
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #1e40af;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 600;
}

/* 空状态 */
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 3rem 1rem;
  color: #94a3b8;
  font-size: 1rem;
  text-align: center;
}

.empty-icon,
.loading-spinner {
  font-size: 3rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-spinner {
  animation: spin 2s linear infinite;
}

/* 面试卡片列表 */
.interview-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-height: calc(100vh - 320px);
  overflow-y: auto;
  padding-right: 0.5rem;
}

.interview-list::-webkit-scrollbar {
  width: 6px;
}

.interview-list::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 10px;
}

.interview-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
  transition: background 0.3s;
}

.interview-list::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 面试卡片 */
.interview-card {
  padding: 1.25rem;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  background: #ffffff;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.interview-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(180deg, #3b82f6, #2563eb);
  transform: scaleY(0);
  transition: transform 0.3s;
}

.interview-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
  border-color: #cbd5e1;
}

.interview-card:hover::before {
  transform: scaleY(1);
}

.interview-card.active {
  border-color: #3b82f6;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.15);
}

.interview-card.active::before {
  transform: scaleY(1);
  background: linear-gradient(180deg, #3b82f6, #1d4ed8);
}

/* 状态颜色 */
.interview-card.scheduled::before {
  background: linear-gradient(180deg, #3b82f6, #2563eb);
}

.interview-card.completed::before {
  background: linear-gradient(180deg, #10b981, #059669);
}

.interview-card.cancelled::before {
  background: linear-gradient(180deg, #ef4444, #dc2626);
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.25rem;
}

.candidate-info {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex: 1;
}

.avatar {
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.candidate-details h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 700;
  color: #0f172a;
}

.job-title {
  margin: 0.25rem 0 0;
  font-size: 0.85rem;
  color: #64748b;
  font-weight: 500;
}

/* 状态徽章 */
.status-badge {
  padding: 0.5rem 1rem;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  flex-shrink: 0;
  text-transform: uppercase;
}

.status-badge.scheduled {
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #1e40af;
  border: 1px solid #93c5fd;
}

.status-badge.completed {
  background: linear-gradient(135deg, #d1fae5, #a7f3d0);
  color: #065f46;
  border: 1px solid #6ee7b7;
}

.status-badge.cancelled {
  background: linear-gradient(135deg, #fee2e2, #fecaca);
  color: #991b1b;
  border: 1px solid #fca5a5;
}

/* 卡片主体 */
.card-body {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.info-row {
  display: grid;
  grid-template-columns: 1.5rem auto 1fr;
  gap: 0.75rem;
  align-items: start;
  font-size: 0.9rem;
}

.info-row .icon {
  font-size: 1.2rem;
  display: flex;
  align-items: center;
}

.info-row .label {
  font-weight: 600;
  color: #475569;
  white-space: nowrap;
}

.info-row .value {
  color: #64748b;
}

.info-row .value.link {
  color: #2563eb;
  text-decoration: underline;
  cursor: pointer;
}

.feedback-section {
  display: grid;
  grid-template-columns: 1.5rem auto;
  gap: 0.75rem;
  margin-top: 0.5rem;
  padding: 1rem;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

.feedback-section .icon {
  font-size: 1.2rem;
  grid-row: 1;
}

.feedback-section .label {
  font-weight: 600;
  color: #475569;
  font-size: 0.9rem;
  grid-column: 2;
}

.feedback-text {
  margin: 0.5rem 0 0;
  color: #64748b;
  font-size: 0.9rem;
  line-height: 1.6;
  grid-column: 2;
}

/* 卡片操作按钮 */
.card-actions {
  display: flex;
  gap: 0.75rem;
  padding-top: 1rem;
  border-top: 1px solid #f1f5f9;
}

.btn-edit,
.btn-delete {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-edit {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
}

.btn-edit:hover {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
}

.btn-delete {
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
  color: #64748b;
  border: 1px solid #cbd5e1;
}

.btn-delete:hover {
  background: linear-gradient(135deg, #fee2e2, #fecaca);
  color: #dc2626;
  border-color: #fca5a5;
}

.btn-delete:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 表单区域 */
.interview-form {
  background: #ffffff;
  border-radius: 16px;
  padding: 1.75rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04), 0 1px 4px rgba(0, 0, 0, 0.02);
  border: 1px solid #e2e8f0;
  position: sticky;
  top: 1.5rem;
  max-height: calc(100vh - 3rem);
  overflow-y: auto;
}

.interview-form::-webkit-scrollbar {
  width: 6px;
}

.interview-form::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 10px;
}

.interview-form::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}

.interview-form::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f1f5f9;
}

.form-header h2 {
  margin: 0;
  font-size: 1.4rem;
  font-weight: 700;
  color: #0f172a;
}

.btn-new {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1.25rem;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-new:hover {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
}

.btn-new:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.plus-icon {
  font-size: 1.2rem;
  font-weight: 700;
}

/* 表单内容 */
.form-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1.5rem;
  background: linear-gradient(135deg, #fafbfc, #f8fafc);
  border-radius: 14px;
  border: 1px solid #e2e8f0;
}

.section-title {
  margin: 0 0 0.5rem;
  font-size: 0.95rem;
  font-weight: 700;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.section-title::before {
  content: '';
  width: 3px;
  height: 1rem;
  background: linear-gradient(180deg, #3b82f6, #8b5cf6);
  border-radius: 2px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.field-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #334155;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.required {
  color: #ef4444;
  font-size: 1rem;
}

.inline-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.875rem;
}

input,
select,
textarea {
  padding: 0.875rem 1rem;
  border-radius: 10px;
  border: 1.5px solid #e2e8f0;
  font-size: 0.95rem;
  font-family: inherit;
  background: #ffffff;
  transition: all 0.2s;
}

input:hover,
select:hover,
textarea:hover {
  border-color: #cbd5e1;
}

input:focus,
select:focus,
textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

textarea {
  resize: vertical;
  min-height: 80px;
  line-height: 1.6;
  font-family: inherit;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
}

.btn-submit,
.btn-reset {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-submit {
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  color: white;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.btn-submit:hover {
  background: linear-gradient(135deg, #2563eb, #7c3aed);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-reset {
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  color: #475569;
  border: 1px solid #e2e8f0;
}

.btn-reset:hover {
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
  border-color: #cbd5e1;
}

.btn-reset:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-icon {
  font-size: 1rem;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .layout {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .interview-form {
    position: static;
  }
}

@media (max-width: 768px) {
  .interviews-page {
    padding: 1.5rem 1rem 3rem;
  }
  
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
  }
  
  .title-block h1 {
    font-size: 2rem;
  }
  
  .back-link {
    align-self: flex-start;
  }
  
  .inline-group {
    grid-template-columns: 1fr;
  }
  
  .card-actions,
  .form-actions {
    flex-direction: column;
  }
  
  .interview-list {
    max-height: none;
  }
}

@media (max-width: 480px) {
  .title-block h1 {
    font-size: 1.75rem;
  }
  
  .candidate-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .status-badge {
    align-self: flex-start;
  }
}
</style>
