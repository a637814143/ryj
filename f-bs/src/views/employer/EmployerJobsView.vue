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
      <div class="title-block">
        <h1>💼 岗位发布与管理</h1>
        <p class="subtitle">维护企业招聘岗位，实时掌握申请数量与状态</p>
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
      <p>请登录企业账号后再管理岗位</p>
    </div>

    <div v-else class="layout">
      <form class="job-form" @submit.prevent="submit">
        <div class="form-header">
          <h2>{{ editingId ? '✏️ 编辑岗位' : '➕ 发布新岗位' }}</h2>
          <button type="button" class="btn-new" @click="startCreate" :disabled="saving || loading">
            <span class="plus-icon">+</span>
            新建
          </button>
        </div>

        <div class="form-content">
          <div class="form-section">
            <h3 class="section-title">基本信息</h3>
            <label class="form-field">
              <span class="field-label">岗位标题 <span class="required">*</span></span>
              <input v-model="form.title" type="text" placeholder="例如：Java后端开发工程师" :disabled="saving" />
            </label>
            
            <label class="form-field">
              <span class="field-label">岗位描述</span>
              <textarea
                v-model="form.description"
                rows="5"
                placeholder="填写岗位职责、任职要求、技术栈等详细信息"
                :disabled="saving"
              ></textarea>
            </label>
          </div>

          <div class="form-section">
            <h3 class="section-title">薪资与地点</h3>
            <div class="inline-group">
              <label class="form-field">
                <span class="field-label">薪资范围</span>
                <input v-model="form.salaryRange" type="text" placeholder="例如：15K-30K" :disabled="saving" />
              </label>
              <label class="form-field">
                <span class="field-label">工作地点</span>
                <input v-model="form.location" type="text" placeholder="例如：北京市海淀区" :disabled="saving" />
              </label>
            </div>
          </div>

          <div class="form-section">
            <h3 class="section-title">岗位设置</h3>
            <div class="inline-group-triple">
              <label class="form-field">
                <span class="field-label">工作类型</span>
                <select v-model="form.workType" :disabled="saving">
                  <option value="">请选择</option>
                  <option v-for="option in workTypeOptions" :key="option.value" :value="option.value">
                    {{ option.label }}
                  </option>
                </select>
              </label>
              <label class="form-field">
                <span class="field-label">岗位状态</span>
                <select v-model="form.status" :disabled="saving">
                  <option v-for="option in statusOptions" :key="option.value" :value="option.value">
                    {{ option.label }}
                  </option>
                </select>
              </label>
              <label class="form-field">
                <span class="field-label">截止日期</span>
                <input v-model="form.closingDate" type="date" :disabled="saving" />
              </label>
            </div>
          </div>

          <div class="form-section">
            <h3 class="section-title">岗位要求</h3>
            <label class="form-field">
              <span class="field-label">任职要求</span>
              <textarea
                v-model="form.requirementsText"
                rows="4"
                placeholder="每行一条要求，例如：&#10;• 熟悉Spring Boot框架&#10;• 3年以上Java开发经验&#10;• 良好的团队协作能力"
                :disabled="saving"
              ></textarea>
            </label>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn-submit" :disabled="saving">
            <span class="btn-icon">{{ saving ? '⏳' : '✓' }}</span>
            {{ saving ? '正在保存...' : editingId ? '更新岗位' : '发布岗位' }}
          </button>
          <button type="button" class="btn-reset" @click="resetForm" :disabled="saving">
            <span class="btn-icon">↺</span>
            重置
          </button>
        </div>
      </form>

      <section class="job-list-section" :class="{ loading }">
        <div class="section-header">
          <div class="header-content">
            <h2>📋 岗位列表</h2>
            <span class="count">{{ orderedJobs.length }} 个岗位</span>
          </div>
        </div>

        <p v-if="loading" class="empty">
          <span class="loading-spinner">⏳</span>
          正在加载岗位信息...
        </p>
        <p v-else-if="!orderedJobs.length" class="empty">
          <span class="empty-icon">📭</span>
          暂无岗位
        </p>

        <div v-else class="job-list">
          <article 
            v-for="job in orderedJobs" 
            :key="job.job.id" 
            :class="['job-card', { active: job.job.id === editingId, [job.job.status?.toLowerCase() || 'open']: true }]"
          >
            <div class="card-header">
              <div class="job-info">
                <h3>{{ job.job.title }}</h3>
                <div class="badges">
                  <span :class="['status-badge', job.job.status?.toLowerCase() || 'open']">
                    {{ statusOptions.find(s => s.value === job.job.status)?.label || 'OPEN' }}
                  </span>
                  <span class="work-type-badge">
                    {{ workTypeOptions.find(w => w.value === job.job.workType)?.label || '未设置' }}
                  </span>
                </div>
              </div>
              <div class="apply-count">
                <span class="count-number">{{ job.applicationCount }}</span>
                <span class="count-label">申请数</span>
              </div>
            </div>

            <p class="description">{{ job.job.description || '暂无岗位描述' }}</p>

            <div class="meta-grid">
              <div class="meta-item">
                <span class="icon">💰</span>
                <span class="label">薪资</span>
                <span class="value">{{ job.job.salaryRange || '面议' }}</span>
              </div>
              <div class="meta-item">
                <span class="icon">📍</span>
                <span class="label">地点</span>
                <span class="value">{{ job.job.location || '未填写' }}</span>
              </div>
              <div class="meta-item">
                <span class="icon">📅</span>
                <span class="label">截止</span>
                <span class="value">{{ job.job.closingDate || '未设置' }}</span>
              </div>
            </div>

            <div class="requirements-section" v-if="job.requirements.length">
              <span class="requirements-label">任职要求</span>
              <div class="requirements">
                <span v-for="(req, index) in job.requirements" :key="index" class="requirement">
                  {{ req }}
                </span>
              </div>
            </div>

            <div class="card-actions">
              <button type="button" class="btn-edit" @click="startEdit(job)">
                <span class="btn-icon">✏️</span>
                编辑
              </button>
              <button type="button" class="btn-delete" @click="removeJob(job)" :disabled="saving">
                <span class="btn-icon">🗑️</span>
                删除
              </button>
            </div>
          </article>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
/* ===== Apple级美工设计 ===== */

/* 整体布局 - 优雅渐变背景 */
.jobs-page {
  max-width: 1800px;
  margin: 0 auto;
  padding: 3rem 1.5rem 5rem;
  min-height: 100vh;
  background: linear-gradient(to bottom, #fafbfc 0%, #ffffff 40%);
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'Helvetica Neue', Arial, sans-serif;
}

/* 页头 - Apple风格 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 2rem;
  margin-bottom: 2.75rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.title-block h1 {
  margin: 0;
  font-size: 2.75rem;
  font-weight: 700;
  background: linear-gradient(135deg, #0a0a0a 0%, #3a3a3a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.04em;
  line-height: 1.1;
}

.subtitle {
  margin: 1rem 0 0;
  font-size: 1.0625rem;
  color: #666666;
  font-weight: 400;
  line-height: 1.6;
  letter-spacing: -0.01em;
}

.back-link {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  padding: 0.875rem 1.625rem;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  color: #1d1d1f;
  text-decoration: none;
  font-weight: 500;
  font-size: 0.9375rem;
  border: 0.5px solid rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04), 0 4px 12px rgba(0, 0, 0, 0.02);
}

.back-link:hover {
  transform: translateX(-6px) translateY(-2px);
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08), 0 2px 8px rgba(0, 0, 0, 0.04);
  border-color: rgba(0, 0, 0, 0.12);
}

.back-link .icon {
  font-size: 1.2rem;
  transition: transform 0.3s;
}

.back-link:hover .icon {
  transform: translateX(-2px);
}

/* 消息提示 - Apple级动画 */
.message {
  display: flex;
  align-items: center;
  gap: 0.875rem;
  padding: 1.125rem 1.625rem;
  border-radius: 16px;
  margin-bottom: 2rem;
  font-size: 0.9375rem;
  font-weight: 500;
  letter-spacing: -0.01em;
  animation: slideIn 0.6s cubic-bezier(0.16, 1, 0.3, 1);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
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
  background: linear-gradient(135deg, rgba(209, 250, 229, 0.9), rgba(167, 243, 208, 0.9));
  color: #047857;
  border: 0.5px solid rgba(110, 231, 183, 0.5);
  box-shadow: 0 4px 16px rgba(16, 185, 129, 0.1);
}

.message.success .message-icon {
  background: #10b981;
  color: white;
}

.message.error {
  background: linear-gradient(135deg, rgba(254, 226, 226, 0.9), rgba(254, 202, 202, 0.9));
  color: #b91c1c;
  border: 0.5px solid rgba(252, 165, 165, 0.5);
  box-shadow: 0 4px 16px rgba(239, 68, 68, 0.1);
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

/* 主布局 - PC端上下布局 */
.layout {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* 表单区域 - Apple毛玻璃效果 */
.job-form {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border-radius: 20px;
  padding: 2.5rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06), 0 1px 3px rgba(0, 0, 0, 0.02);
  border: 0.5px solid rgba(0, 0, 0, 0.08);
  width: 100%;
}

.job-form::-webkit-scrollbar {
  width: 6px;
}

.job-form::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 10px;
}

.job-form::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}

.job-form::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.form-header h2 {
  margin: 0;
  font-size: 1.75rem;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.03em;
}

.btn-new {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.375rem;
  background: linear-gradient(135deg, #007aff 0%, #0051d5 100%);
  color: white;
  border: 0.5px solid rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.9375rem;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 3px 12px rgba(0, 122, 255, 0.3);
  letter-spacing: -0.01em;
}

.btn-new:hover {
  background: linear-gradient(135deg, #0066cc 0%, #0040a3 100%);
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 6px 20px rgba(0, 122, 255, 0.4);
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
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 2rem;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  padding: 0;
  background: transparent;
  border-radius: 0;
  border: none;
}

.form-section:first-child {
  grid-column: 1 / -1;
}

.section-title {
  margin: 0 0 1rem;
  font-size: 1.125rem;
  font-weight: 700;
  color: #1d1d1f;
  text-transform: none;
  letter-spacing: -0.02em;
  display: flex;
  align-items: center;
  gap: 0;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.section-title::before {
  display: none;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  width: 100%;
}

.field-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #1d1d1f;
  display: flex;
  align-items: center;
  gap: 0.25rem;
  letter-spacing: -0.01em;
}

.required {
  color: #ef4444;
  font-size: 1rem;
}

.inline-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  align-items: start;
}

.inline-group-triple {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
  align-items: start;
}

input,
select,
textarea {
  width: 100%;
  padding: 0.875rem 1.125rem;
  border-radius: 11px;
  border: 0.5px solid rgba(0, 0, 0, 0.1);
  font-size: 0.9375rem;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'Helvetica Neue', Arial, sans-serif;
  background: rgba(255, 255, 255, 0.9);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  box-sizing: border-box;
  letter-spacing: -0.01em;
}

input:hover,
select:hover,
textarea:hover {
  border-color: rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 1);
}

input:focus,
select:focus,
textarea:focus {
  outline: none;
  border-color: #007aff;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(0, 122, 255, 0.08), 0 4px 12px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px);
}

textarea {
  resize: vertical;
  min-height: 100px;
  line-height: 1.6;
  font-family: inherit;
}

/* 表单操作按钮 */
.form-actions {
  grid-column: 1 / -1;
  display: flex;
  gap: 1.25rem;
  margin-top: 1rem;
  padding-top: 1.5rem;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  justify-content: flex-end;
}

.btn-submit,
.btn-reset {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.625rem;
  padding: 1rem 2.5rem;
  border: none;
  border-radius: 14px;
  font-weight: 600;
  font-size: 0.9375rem;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  letter-spacing: -0.01em;
  min-width: 140px;
}

.btn-submit {
  background: linear-gradient(135deg, #007aff 0%, #0051d5 100%);
  color: white;
  box-shadow: 0 4px 16px rgba(0, 122, 255, 0.3), 0 2px 4px rgba(0, 0, 0, 0.1);
  border: 0.5px solid rgba(255, 255, 255, 0.15);
}

.btn-submit:hover {
  background: linear-gradient(135deg, #0066cc 0%, #0040a3 100%);
  transform: translateY(-3px) scale(1.01);
  box-shadow: 0 8px 24px rgba(0, 122, 255, 0.4), 0 4px 8px rgba(0, 0, 0, 0.15);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.btn-reset {
  background: rgba(255, 255, 255, 0.95);
  color: #1d1d1f;
  border: 0.5px solid rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.btn-reset:hover {
  background: rgba(248, 248, 248, 1);
  border-color: rgba(0, 0, 0, 0.15);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.btn-reset:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  transform: none;
}

.btn-icon {
  font-size: 1rem;
}

/* 岗位列表区域 - Apple卡片 */
.job-list-section {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border-radius: 20px;
  padding: 2.5rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06), 0 1px 3px rgba(0, 0, 0, 0.02);
  border: 0.5px solid rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
  width: 100%;
}

.job-list-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #f59e0b, #3b82f6, #8b5cf6);
}

.job-list-section.loading {
  opacity: 0.6;
}

.section-header {
  margin-bottom: 2rem;
  padding-bottom: 1.25rem;
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.06);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h2 {
  margin: 0;
  font-size: 1.625rem;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.02em;
}

.count {
  padding: 0.5rem 1.125rem;
  background: linear-gradient(135deg, rgba(254, 243, 199, 0.8), rgba(253, 230, 138, 0.8));
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  color: #92400e;
  border-radius: 20px;
  font-size: 0.8125rem;
  font-weight: 600;
  border: 0.5px solid rgba(146, 64, 14, 0.1);
  letter-spacing: -0.01em;
}

/* 空状态 - Apple风格 */
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.25rem;
  padding: 4rem 2rem;
  color: #86868b;
  font-size: 1.0625rem;
  font-weight: 500;
  text-align: center;
  letter-spacing: -0.01em;
}

.empty-icon,
.loading-spinner {
  font-size: 4rem;
  filter: grayscale(0.3) opacity(0.8);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-spinner {
  animation: spin 2s linear infinite;
}

/* 岗位卡片列表 - PC端网格布局 */
.job-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 1.5rem;
  max-height: none;
}

@media (max-width: 1400px) {
  .job-list {
    grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
  }
}

@media (max-width: 1200px) {
  .job-list {
    grid-template-columns: 1fr;
  }
}

.job-list::-webkit-scrollbar {
  width: 6px;
}

.job-list::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 10px;
}

.job-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}

.job-list::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 岗位卡片 */
.job-card {
  padding: 1.75rem;
  border: 0.5px solid rgba(0, 0, 0, 0.08);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  transition: all 0.5s cubic-bezier(0.16, 1, 0.3, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.job-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(180deg, #007aff 0%, #0051d5 100%);
  transform: scaleY(0);
  transition: transform 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 0 12px rgba(0, 122, 255, 0.4);
}

.job-card:hover {
  border-color: rgba(0, 122, 255, 0.2);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1), 0 4px 12px rgba(0, 122, 255, 0.1);
  transform: translateY(-6px) scale(1.005);
  background: rgba(255, 255, 255, 1);
}

.job-card:hover::before {
  transform: scaleY(1);
}

.job-card.active {
  border-color: #f59e0b;
  background: linear-gradient(135deg, #fffbeb, #fef3c7);
  box-shadow: 0 8px 24px rgba(245, 158, 11, 0.15);
}

.job-card.active::before {
  transform: scaleY(1);
}

/* 状态颜色 */
.job-card.open::before {
  background: linear-gradient(180deg, #10b981, #059669);
}

.job-card.closed::before {
  background: linear-gradient(180deg, #ef4444, #dc2626);
}

.job-card.draft::before {
  background: linear-gradient(180deg, #f59e0b, #d97706);
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  margin-bottom: 1rem;
}

.job-info {
  flex: 1;
}

.job-info h3 {
  margin: 0 0 0.5rem;
  font-size: 1.1rem;
  font-weight: 700;
  color: #0f172a;
}

.badges {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

/* 状态徽章 */
.status-badge {
  padding: 0.35rem 0.75rem;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  text-transform: uppercase;
}

.status-badge.open {
  background: linear-gradient(135deg, #d1fae5, #a7f3d0);
  color: #065f46;
  border: 1px solid #6ee7b7;
}

.status-badge.closed {
  background: linear-gradient(135deg, #fee2e2, #fecaca);
  color: #991b1b;
  border: 1px solid #fca5a5;
}

.status-badge.draft {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  color: #92400e;
  border: 1px solid #fcd34d;
}

.work-type-badge {
  padding: 0.35rem 0.75rem;
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #1e40af;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
  border: 1px solid #93c5fd;
}

/* 申请数量 */
.apply-count {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0.75rem 1rem;
  background: linear-gradient(135deg, #f0f9ff, #e0f2fe);
  border-radius: 12px;
  min-width: 4rem;
}

.count-number {
  font-size: 1.5rem;
  font-weight: 800;
  color: #0c4a6e;
  line-height: 1;
}

.count-label {
  font-size: 0.7rem;
  color: #0369a1;
  margin-top: 0.25rem;
  font-weight: 600;
}

/* 描述 */
.description {
  margin: 0 0 1rem;
  color: #64748b;
  line-height: 1.6;
  font-size: 0.9rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 元数据网格 */
.meta-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.75rem;
  margin-bottom: 1rem;
  padding: 1rem;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  align-items: center;
  text-align: center;
}

.meta-item .icon {
  font-size: 1.25rem;
}

.meta-item .label {
  font-size: 0.7rem;
  color: #64748b;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.meta-item .value {
  font-size: 0.85rem;
  color: #334155;
  font-weight: 600;
}

/* 要求区域 */
.requirements-section {
  margin-bottom: 1rem;
}

.requirements-label {
  display: block;
  font-size: 0.85rem;
  font-weight: 600;
  color: #475569;
  margin-bottom: 0.5rem;
}

.requirements {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.requirement {
  padding: 0.35rem 0.75rem;
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  color: #1e40af;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 500;
  border: 1px solid #bfdbfe;
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .form-content {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .form-section:first-child {
    grid-column: 1;
  }
  
  .form-actions {
    grid-column: 1;
  }
}

@media (max-width: 768px) {
  .jobs-page {
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
  
  .inline-group,
  .inline-group-triple {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  input,
  select,
  textarea {
    font-size: 1rem;
  }

  .meta-grid {
    grid-template-columns: 1fr;
  }
  
  .card-actions {
    flex-direction: column;
  }
  
  .btn-submit,
  .btn-reset {
    width: 100%;
  }
  
  .form-actions {
    flex-direction: column-reverse;
  }
  
  .job-list {
    max-height: none;
  }

  .card-header {
    flex-direction: column;
  }

  .apply-count {
    align-self: flex-start;
  }
}

@media (max-width: 480px) {
  .title-block h1 {
    font-size: 1.75rem;
  }
}
</style>
