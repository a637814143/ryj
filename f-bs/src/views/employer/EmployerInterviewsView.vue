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
      <div>
        <h1>面试安排管理</h1>
        <p class="subtitle">快速创建、更新面试邀约，跟进候选人进度</p>
      </div>
      <RouterLink class="back-link" to="/employer/overview">返回企业总览</RouterLink>
    </header>

    <section v-if="message" :class="['message', messageType]">{{ message }}</section>

    <div v-if="userInfo?.role !== 'EMPLOYER'" class="guard">请使用企业账号登录以管理面试。</div>

    <div v-else class="layout">
      <section class="list" :class="{ loading }">
        <div class="list-header">
          <h2>面试列表</h2>
          <span>{{ orderedInterviews.length }} 场面试</span>
        </div>
        <p v-if="loading" class="empty">正在加载面试记录...</p>
        <p v-else-if="!orderedInterviews.length" class="empty">暂无面试安排。</p>
        <ul v-else>
          <li v-for="interview in orderedInterviews" :key="interview.id" :class="{ active: editingId === interview.id }">
            <div class="header-line">
              <div class="candidate">
                <h3>{{ interview.candidateName || '候选人' }}</h3>
                <p>岗位：{{ interview.jobTitle || '岗位未知' }}</p>
              </div>
              <div class="status">{{ interview.status }}</div>
            </div>
            <div class="meta">
              <span>时间：{{ interview.scheduledTime ? new Date(interview.scheduledTime).toLocaleString() : '待定' }}</span>
              <span>地点：{{ interview.location || '待定' }}</span>
              <span>会议链接：{{ interview.meetingLink || '无' }}</span>
            </div>
            <p class="feedback" v-if="interview.feedback">面试反馈：{{ interview.feedback }}</p>
            <div class="actions">
              <button type="button" class="primary" @click="startEdit(interview)">编辑</button>
              <button type="button" class="danger" @click="removeInterview(interview)" :disabled="saving">删除</button>
            </div>
          </li>
        </ul>
      </section>

      <form class="interview-form" @submit.prevent="submit">
        <div class="form-header">
          <h2>{{ editingId ? '编辑面试' : '创建面试安排' }}</h2>
          <button type="button" class="link" @click="startCreate" :disabled="saving">新建安排</button>
        </div>
        <div class="inline">
          <label>
            岗位ID<span>*</span>
            <input v-model="form.jobId" type="number" placeholder="关联的岗位ID" :disabled="saving" />
          </label>
          <label>
            申请ID<span>*</span>
            <input v-model="form.applicationId" type="number" placeholder="关联的申请ID" :disabled="saving" />
          </label>
        </div>
        <label>
          面试时间<span>*</span>
          <input v-model="form.scheduledTime" type="datetime-local" :disabled="saving" />
        </label>
        <label>
          面试地点
          <input v-model="form.location" type="text" placeholder="例如：总部大厦A座会议室" :disabled="saving" />
        </label>
        <label>
          会议链接
          <input v-model="form.meetingLink" type="url" placeholder="线上面试可填写会议链接" :disabled="saving" />
        </label>
        <label>
          面试状态
          <select v-model="form.status" :disabled="saving">
            <option v-for="option in statusOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
          </select>
        </label>
        <label>
          面试反馈
          <textarea v-model="form.feedback" rows="4" placeholder="可记录候选人表现、后续跟进建议" :disabled="saving"></textarea>
        </label>
        <div class="form-actions">
          <button type="submit" :disabled="saving">{{ saving ? '正在保存...' : editingId ? '更新面试' : '创建面试' }}</button>
          <button type="button" class="secondary" @click="resetForm" :disabled="saving">重置表单</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.interviews-page {
  max-width: 1200px;
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
  padding: 0.5rem 1.1rem;
  border-radius: 999px;
  background: #2563eb;
  color: #fff;
  text-decoration: none;
  font-weight: 600;
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

.layout {
  display: grid;
  grid-template-columns: minmax(380px, 1.2fr) minmax(320px, 1fr);
  gap: 1.5rem;
  align-items: start;
}

.list {
  background: #ffffff;
  border-radius: 18px;
  padding: 1.5rem;
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.08);
  min-height: 240px;
}

.list.loading {
  opacity: 0.7;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.list-header h2 {
  margin: 0;
  font-size: 1.25rem;
  color: #1e293b;
}

.list-header span {
  color: #64748b;
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
  gap: 0.75rem;
}

.list li.active {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
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

.status {
  padding: 0.35rem 0.9rem;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.12);
  color: #2563eb;
  font-weight: 600;
  font-size: 0.85rem;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1.2rem;
  color: #64748b;
  font-size: 0.9rem;
}

.feedback {
  margin: 0;
  color: #475569;
  font-size: 0.95rem;
}

.actions {
  display: flex;
  gap: 0.75rem;
}

.actions button {
  padding: 0.65rem 1.2rem;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  cursor: pointer;
}

.actions .primary {
  background: #2563eb;
  color: #fff;
}

.actions .danger {
  background: #ef4444;
  color: #fff;
}

.empty {
  color: #94a3b8;
  font-size: 0.95rem;
}

.interview-form {
  background: #ffffff;
  border-radius: 18px;
  padding: 1.5rem;
  box-shadow: 0 16px 32px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-header h2 {
  margin: 0;
  font-size: 1.25rem;
  color: #1e293b;
}

.form-header .link {
  background: none;
  border: none;
  color: #2563eb;
  cursor: pointer;
}

label {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  color: #334155;
  font-size: 0.95rem;
}

label span {
  color: #ef4444;
  margin-left: 0.3rem;
}

input,
select,
textarea {
  padding: 0.75rem 1rem;
  border-radius: 10px;
  border: 2px solid #e2e8f0;
  font-size: 0.95rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

input:focus,
select:focus,
textarea:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
}

.inline {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 1rem;
}

textarea {
  resize: vertical;
}

.form-actions {
  display: flex;
  gap: 1rem;
}

button {
  padding: 0.75rem 1.4rem;
  border-radius: 10px;
  border: none;
  font-weight: 600;
  cursor: pointer;
}

button[type='submit'] {
  background: linear-gradient(135deg, #2563eb, #9333ea);
  color: #fff;
}

button.secondary {
  background: #e2e8f0;
  color: #1f2937;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 900px) {
  .layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .inline {
    grid-template-columns: 1fr;
  }

  .actions,
  .form-actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
