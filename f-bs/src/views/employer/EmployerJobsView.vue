<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import {
  fetchEmployerJobs,
  createEmployerJob,
  updateEmployerJob,
  deleteEmployerJob,
  type EmployerJobDetail,
  type EmployerJobRequestPayload,
  type JobPostingWorkType,
  type JobPostingStatus,
} from '../../api/employer'

const userInfo = ref<{ id?: number; role?: string } | null>(null)
const userId = ref<number | null>(null)
const jobs = ref<EmployerJobDetail[]>([])
const loading = ref(false)
const saving = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error' | ''>('')

const editingId = ref<number | null>(null)

const form = reactive({
  title: '',
  description: '',
  salaryRange: '',
  location: '',
  workType: '' as JobPostingWorkType | '' | null,
  status: 'OPEN' as JobPostingStatus,
  closingDate: '',
  requirementsText: '',
})

const workTypeOptions: { value: JobPostingWorkType; label: string }[] = [
  { value: 'FULL_TIME', label: '全职' },
  { value: 'PART_TIME', label: '兼职' },
  { value: 'INTERNSHIP', label: '实习' },
  { value: 'REMOTE', label: '远程' },
]

const statusOptions: { value: JobPostingStatus; label: string }[] = [
  { value: 'OPEN', label: '开放' },
  { value: 'CLOSED', label: '关闭' },
  { value: 'DRAFT', label: '草稿' },
]

const orderedJobs = computed(() =>
  [...jobs.value].sort((a, b) => {
    const timeA = a.job.publishedDate ? new Date(a.job.publishedDate).getTime() : 0
    const timeB = b.job.publishedDate ? new Date(b.job.publishedDate).getTime() : 0
    return timeB - timeA
  }),
)

const resetMessage = () => {
  message.value = ''
  messageType.value = ''
}

const resetForm = () => {
  editingId.value = null
  form.title = ''
  form.description = ''
  form.salaryRange = ''
  form.location = ''
  form.workType = ''
  form.status = 'OPEN'
  form.closingDate = ''
  form.requirementsText = ''
}

const fillForm = (job: EmployerJobDetail) => {
  editingId.value = job.job.id
  form.title = job.job.title || ''
  form.description = job.job.description || ''
  form.salaryRange = job.job.salaryRange || ''
  form.location = job.job.location || ''
  form.workType = job.job.workType || ''
  form.status = job.job.status || 'OPEN'
  form.closingDate = job.job.closingDate || ''
  form.requirementsText = job.requirements.join('\n')
}

const loadJobs = async () => {
  if (!userId.value) return
  loading.value = true
  resetMessage()
  try {
    jobs.value = await fetchEmployerJobs(userId.value)
    if (!jobs.value.length) {
      message.value = '尚未发布岗位，完成资料后即可发布招聘信息'
      messageType.value = 'error'
    }
  } catch (err) {
    console.error(err)
    message.value = (err as Error).message || '加载岗位列表失败'
    messageType.value = 'error'
  } finally {
    loading.value = false
  }
}

const toPayload = (): EmployerJobRequestPayload => ({
  title: form.title.trim(),
  description: form.description.trim() || null,
  salaryRange: form.salaryRange.trim() || null,
  location: form.location.trim() || null,
  workType: form.workType ? (form.workType as JobPostingWorkType) : null,
  status: form.status || 'OPEN',
  closingDate: form.closingDate || null,
  requirements:
    form.requirementsText
      .split('\n')
      .map((item) => item.trim())
      .filter((item) => item.length > 0) || [],
})

const submit = async () => {
  if (!userId.value) {
    message.value = '请先登录企业账号'
    messageType.value = 'error'
    return
  }
  if (!form.title.trim()) {
    message.value = '岗位标题不能为空'
    messageType.value = 'error'
    return
  }
  saving.value = true
  resetMessage()
  try {
    if (editingId.value) {
      await updateEmployerJob(userId.value, editingId.value, toPayload())
      message.value = '岗位更新成功'
    } else {
      await createEmployerJob(userId.value, toPayload())
      message.value = '岗位创建成功'
    }
    messageType.value = 'success'
    resetForm()
    await loadJobs()
  } catch (err) {
    console.error(err)
    message.value = (err as Error).message || '保存岗位失败'
    messageType.value = 'error'
  } finally {
    saving.value = false
  }
}

const startCreate = () => {
  resetForm()
  resetMessage()
}

const startEdit = (job: EmployerJobDetail) => {
  resetMessage()
  fillForm(job)
}

const removeJob = async (job: EmployerJobDetail) => {
  if (!userId.value) return
  const confirmDelete = window.confirm(`确认删除岗位「${job.job.title}」吗？相关简历记录将无法继续投递。`)
  if (!confirmDelete) return
  resetMessage()
  saving.value = true
  try {
    await deleteEmployerJob(userId.value, job.job.id)
    message.value = '岗位已删除'
    messageType.value = 'success'
    if (editingId.value === job.job.id) {
      resetForm()
    }
    await loadJobs()
  } catch (err) {
    console.error(err)
    message.value = (err as Error).message || '删除岗位失败'
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
    message.value = '仅企业用户可管理招聘岗位'
    messageType.value = 'error'
    return
  }
  loadJobs()
})
</script>

<template>
  <div class="jobs-page">
    <header class="page-header">
      <div>
        <h1>岗位发布与管理</h1>
        <p class="subtitle">维护企业招聘岗位，实时掌握申请数量与状态</p>
      </div>
      <RouterLink class="back-link" to="/employer/overview">返回企业总览</RouterLink>
    </header>

    <section v-if="message" :class="['message', messageType]">{{ message }}</section>

    <div v-if="userInfo?.role !== 'EMPLOYER'" class="guard">请登录企业账号后再管理岗位。</div>

    <div v-else class="layout">
      <form class="job-form" @submit.prevent="submit">
        <div class="form-header">
          <h2>{{ editingId ? '编辑岗位' : '发布新岗位' }}</h2>
          <button type="button" class="link" @click="startCreate" :disabled="saving || loading">新建岗位</button>
        </div>
        <label>
          岗位标题<span>*</span>
          <input v-model="form.title" type="text" placeholder="例如：Java后端开发工程师" :disabled="saving" />
        </label>
        <label>
          岗位描述
          <textarea
            v-model="form.description"
            rows="6"
            placeholder="填写岗位职责、任职要求、技术栈等详细信息"
            :disabled="saving"
          ></textarea>
        </label>
        <div class="inline">
          <label>
            薪资范围
            <input v-model="form.salaryRange" type="text" placeholder="例如：15K-30K" :disabled="saving" />
          </label>
          <label>
            工作地点
            <input v-model="form.location" type="text" placeholder="例如：北京市海淀区" :disabled="saving" />
          </label>
        </div>
        <div class="inline">
          <label>
            工作类型
            <select v-model="form.workType" :disabled="saving">
              <option value="">请选择</option>
              <option v-for="option in workTypeOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
            </select>
          </label>
          <label>
            岗位状态
            <select v-model="form.status" :disabled="saving">
              <option v-for="option in statusOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
            </select>
          </label>
          <label>
            截止日期
            <input v-model="form.closingDate" type="date" :disabled="saving" />
          </label>
        </div>
        <label>
          岗位要求
          <textarea
            v-model="form.requirementsText"
            rows="4"
            placeholder="每行填写一条岗位要求，例如：熟悉Spring Boot框架"
            :disabled="saving"
          ></textarea>
        </label>
        <div class="form-actions">
          <button type="submit" :disabled="saving">{{ saving ? '正在保存...' : editingId ? '更新岗位' : '发布岗位' }}</button>
          <button type="button" class="secondary" @click="resetForm" :disabled="saving">重置表单</button>
        </div>
      </form>

      <section class="job-list" :class="{ loading }">
        <div class="list-header">
          <h2>岗位列表</h2>
          <span>{{ orderedJobs.length }} 个岗位</span>
        </div>
        <p v-if="loading" class="empty">正在加载岗位信息...</p>
        <p v-else-if="!orderedJobs.length" class="empty">暂无岗位，请先通过左侧表单发布。</p>
        <ul v-else>
          <li v-for="job in orderedJobs" :key="job.job.id" :class="{ active: job.job.id === editingId }">
            <div class="job-meta">
              <h3>{{ job.job.title }}</h3>
              <div class="badges">
                <span class="badge" :class="job.job.status?.toLowerCase()">{{ job.job.status || 'OPEN' }}</span>
                <span class="badge light">{{ job.job.workType || '未设置类型' }}</span>
                <span class="badge muted">{{ job.job.location || '地点未填写' }}</span>
              </div>
            </div>
            <p class="description">{{ job.job.description || '暂无岗位描述，请补充' }}</p>
            <div class="meta-line">
              <span>申请数量：{{ job.applicationCount }}</span>
              <span>薪资：{{ job.job.salaryRange || '未公开' }}</span>
              <span>截止：{{ job.job.closingDate || '未设置' }}</span>
            </div>
            <div class="requirements" v-if="job.requirements.length">
              <span v-for="req in job.requirements" :key="req" class="requirement">{{ req }}</span>
            </div>
            <div class="actions">
              <button type="button" class="primary" @click="startEdit(job)">编辑</button>
              <button type="button" class="danger" @click="removeJob(job)" :disabled="saving">删除</button>
            </div>
          </li>
        </ul>
      </section>
    </div>
  </div>
</template>

<style scoped>
.jobs-page {
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
  padding: 0.85rem 1.1rem;
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
  grid-template-columns: minmax(320px, 1fr) minmax(380px, 1.2fr);
  gap: 1.5rem;
  align-items: start;
}

.job-form {
  background: #ffffff;
  border-radius: 18px;
  padding: 1.5rem;
  box-shadow: 0 16px 35px rgba(15, 23, 42, 0.08);
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
  font-size: 1.3rem;
  color: #1e293b;
}

.form-header .link {
  background: none;
  border: none;
  color: #2563eb;
  cursor: pointer;
  font-size: 0.9rem;
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

textarea {
  resize: vertical;
}

.inline {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 1rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-start;
}

button {
  padding: 0.75rem 1.4rem;
  border-radius: 10px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
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

.job-list {
  background: #ffffff;
  border-radius: 18px;
  padding: 1.5rem;
  box-shadow: 0 16px 35px rgba(15, 23, 42, 0.08);
  min-height: 300px;
}

.job-list.loading {
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

.job-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.job-list li {
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 1.2rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.job-list li.active {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.job-meta h3 {
  margin: 0;
  color: #0f172a;
}

.badges {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.4rem;
}

.badge {
  padding: 0.25rem 0.7rem;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
}

.badge.open {
  background: rgba(34, 197, 94, 0.15);
  color: #16a34a;
}

.badge.closed {
  background: rgba(248, 113, 113, 0.15);
  color: #dc2626;
}

.badge.draft {
  background: rgba(234, 179, 8, 0.15);
  color: #ca8a04;
}

.badge.light {
  background: rgba(59, 130, 246, 0.12);
  color: #2563eb;
}

.badge.muted {
  background: rgba(148, 163, 184, 0.18);
  color: #475569;
}

.description {
  margin: 0;
  color: #475569;
  line-height: 1.5;
  max-height: 4.5rem;
  overflow: hidden;
}

.meta-line {
  display: flex;
  flex-wrap: wrap;
  gap: 1.2rem;
  color: #64748b;
  font-size: 0.9rem;
}

.requirements {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.requirement {
  padding: 0.2rem 0.75rem;
  background: #eff6ff;
  color: #1d4ed8;
  border-radius: 999px;
  font-size: 0.85rem;
}

.actions {
  display: flex;
  gap: 0.75rem;
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

@media (max-width: 900px) {
  .layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .inline {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }

  .actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
