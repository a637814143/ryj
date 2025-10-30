<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  approveProfileUpdate,
  getTeacherByUserId,
  getTeacherProfileApprovalDetail,
  rejectProfileUpdate,
  type TeacherProfileApprovalDetail,
} from '@/api/teacher'

const route = useRoute()
const router = useRouter()

const teacherId = ref<number | null>(null)
const detail = ref<TeacherProfileApprovalDetail | null>(null)
const loading = ref(true)
const pageError = ref('')
const actionError = ref('')
const comment = ref('')
const submitting = ref(false)
const successMessage = ref('')

const requestId = computed(() => {
  const raw = Number(route.params.requestId)
  return Number.isFinite(raw) ? raw : null
})

const isPending = computed(() => detail.value?.status === 'PENDING')

const differences = computed(() => {
  const current = detail.value?.currentProfile || {}
  const requested = detail.value?.requestedProfile || {}
  return {
    gender: requested.gender !== current.gender,
    age: requested.age !== current.age,
    major: requested.major !== current.major,
    biography: requested.biography !== current.biography,
    graduationYear: requested.graduationYear !== current.graduationYear,
  }
})

const statusMeta = computed(() => {
  switch (detail.value?.status) {
    case 'APPROVED':
      return { text: '教师已审核通过', klass: 'status-badge success' }
    case 'REJECTED':
      return { text: '教师已驳回', klass: 'status-badge danger' }
    case 'PENDING':
    default:
      return { text: '待教师审核', klass: 'status-badge warning' }
  }
})

const formatDateTime = (value: string | null | undefined) => {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(
    date.getHours(),
  ).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const fieldText = (value: string | number | null | undefined) => {
  if (value === null || value === undefined || value === '') {
    return '—'
  }
  return value
}

const goBack = () => {
  router.push({ name: 'teacher-approvals' })
}

const ensureTeacher = async () => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (!userInfoStr) {
    throw new Error('请先登录后再访问该页面')
  }
  const userInfo = JSON.parse(userInfoStr)
  if (userInfo.role !== 'TEACHER') {
    throw new Error('当前账号不是教师角色，无法访问档案审核页面')
  }
  const record = await getTeacherByUserId(userInfo.id)
  teacherId.value = record.id
}

const fetchDetail = async () => {
  if (!teacherId.value) {
    throw new Error('未能识别教师身份')
  }
  if (!requestId.value) {
    throw new Error('无效的档案申请编号')
  }
  detail.value = await getTeacherProfileApprovalDetail(teacherId.value, requestId.value)
}

onMounted(async () => {
  loading.value = true
  pageError.value = ''
  try {
    await ensureTeacher()
    await fetchDetail()
  } catch (e) {
    pageError.value = e instanceof Error ? e.message : '加载档案审核信息失败'
  } finally {
    loading.value = false
  }
})

const handleApprove = async () => {
  if (!teacherId.value || !requestId.value || !isPending.value) return
  submitting.value = true
  actionError.value = ''
  successMessage.value = ''
  try {
    const payload = comment.value.trim() ? { reviewComment: comment.value.trim() } : {}
    await approveProfileUpdate(teacherId.value, requestId.value, payload)
    successMessage.value = '已通过该档案申请，学生档案信息已同步更新。'
    await fetchDetail()
    comment.value = ''
  } catch (e) {
    actionError.value = e instanceof Error ? e.message : '审核通过操作失败'
  } finally {
    submitting.value = false
  }
}

const handleReject = async () => {
  if (!teacherId.value || !requestId.value || !isPending.value) return
  if (!comment.value.trim()) {
    actionError.value = '请填写退回原因后再提交'
    return
  }
  submitting.value = true
  successMessage.value = ''
  actionError.value = ''
  try {
    await rejectProfileUpdate(teacherId.value, requestId.value, { reviewComment: comment.value.trim() })
    successMessage.value = '已退回该档案申请，学生可根据反馈重新提交。'
    await fetchDetail()
    comment.value = ''
  } catch (e) {
    actionError.value = e instanceof Error ? e.message : '退回操作失败'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="page">
    <header class="page-header">
      <div class="header-main">
        <button class="back-btn" type="button" @click="goBack">返回审核列表</button>
        <div class="title-block">
          <h1>档案审核详情</h1>
          <p>查看学生提交的档案改动，与当前档案对照后给出审核结论。</p>
        </div>
      </div>
      <div v-if="detail" :class="statusMeta.klass">{{ statusMeta.text }}</div>
    </header>

    <section v-if="loading" class="loading">正在加载档案审核详情...</section>
    <section v-else-if="pageError" class="error-block">
      <p>{{ pageError }}</p>
      <button class="back-btn" type="button" @click="goBack">返回列表</button>
    </section>
    <section v-else-if="detail" class="content">
      <div class="summary">
        <div>
          <h2>{{ detail.studentName }}</h2>
          <p class="meta">
            <span>申请编号：{{ detail.requestId }}</span>
            <span>提交时间：{{ formatDateTime(detail.submittedAt) }}</span>
            <span v-if="detail.reviewedAt">审核时间：{{ formatDateTime(detail.reviewedAt) }}</span>
          </p>
        </div>
        <div class="summary-note" v-if="detail.reviewComment">
          <strong>历史审核意见：</strong>
          <p>{{ detail.reviewComment }}</p>
        </div>
      </div>

      <div class="comparison">
        <div class="panel">
          <header>
            <h3>当前档案</h3>
            <small>系统中学生已生效的档案信息</small>
          </header>
          <ul>
            <li :class="{ changed: differences.gender }">
              <span class="label">性别</span>
              <span class="value">{{ fieldText(detail.currentProfile?.gender) }}</span>
            </li>
            <li :class="{ changed: differences.age }">
              <span class="label">年龄</span>
              <span class="value">{{ fieldText(detail.currentProfile?.age) }}</span>
            </li>
            <li :class="{ changed: differences.major }">
              <span class="label">专业</span>
              <span class="value">{{ fieldText(detail.currentProfile?.major) }}</span>
            </li>
            <li :class="{ changed: differences.graduationYear }">
              <span class="label">毕业年份</span>
              <span class="value">{{ fieldText(detail.currentProfile?.graduationYear) }}</span>
            </li>
            <li :class="{ changed: differences.biography }">
              <span class="label">个人简介</span>
              <span class="value">{{ fieldText(detail.currentProfile?.biography) }}</span>
            </li>
          </ul>
        </div>
        <div class="panel highlight">
          <header>
            <h3>本次申请的改动</h3>
            <small>学生提交的档案更新内容</small>
          </header>
          <ul>
            <li :class="{ changed: differences.gender }">
              <span class="label">性别</span>
              <span class="value">{{ fieldText(detail.requestedProfile?.gender) }}</span>
            </li>
            <li :class="{ changed: differences.age }">
              <span class="label">年龄</span>
              <span class="value">{{ fieldText(detail.requestedProfile?.age) }}</span>
            </li>
            <li :class="{ changed: differences.major }">
              <span class="label">专业</span>
              <span class="value">{{ fieldText(detail.requestedProfile?.major) }}</span>
            </li>
            <li :class="{ changed: differences.graduationYear }">
              <span class="label">毕业年份</span>
              <span class="value">{{ fieldText(detail.requestedProfile?.graduationYear) }}</span>
            </li>
            <li :class="{ changed: differences.biography }">
              <span class="label">个人简介</span>
              <span class="value">{{ fieldText(detail.requestedProfile?.biography) }}</span>
            </li>
          </ul>
        </div>
      </div>

      <div class="review-box">
        <label class="review-label" for="review-comment">审核意见</label>
        <textarea
          id="review-comment"
          v-model="comment"
          class="review-textarea"
          :placeholder="isPending ? '可填写给学生的建议或退回原因' : '本次审核已完成，如需补充可重新填写并再次提交'"
          :disabled="submitting"
        ></textarea>

        <div v-if="successMessage" class="success-msg">{{ successMessage }}</div>
        <div v-if="actionError" class="error-msg">{{ actionError }}</div>

        <div class="actions">
          <button type="button" class="btn approve" :disabled="submitting || !isPending" @click="handleApprove">
            {{ submitting ? '提交中...' : '通过申请' }}
          </button>
          <button
            type="button"
            class="btn reject"
            :disabled="submitting || !isPending"
            @click="handleReject"
          >
            退回修改
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  padding: 32px clamp(16px, 4vw, 72px) 72px;
  background: linear-gradient(180deg, #eef2ff 0%, #f8fafc 20%, #ffffff 100%);
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
}

.header-main {
  display: flex;
  align-items: center;
  gap: 24px;
}

.title-block h1 {
  margin: 0;
  font-size: 28px;
  color: #0f172a;
}

.title-block p {
  margin: 6px 0 0;
  color: #475569;
}

.back-btn {
  border: none;
  border-radius: 999px;
  padding: 10px 18px;
  background: rgba(59, 130, 246, 0.12);
  color: #1d4ed8;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: rgba(59, 130, 246, 0.2);
}

.status-badge {
  padding: 10px 18px;
  border-radius: 999px;
  font-weight: 600;
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.12);
}

.status-badge.warning {
  background: rgba(251, 191, 36, 0.2);
  color: #b45309;
}

.status-badge.success {
  background: rgba(34, 197, 94, 0.18);
  color: #047857;
}

.status-badge.danger {
  background: rgba(248, 113, 113, 0.2);
  color: #b91c1c;
}

.loading,
.error-block {
  background: #fff;
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.1);
  color: #475569;
}

.content {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.summary {
  background: #fff;
  border-radius: 24px;
  padding: 28px;
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.12);
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.summary h2 {
  margin: 0;
  font-size: 24px;
  color: #0f172a;
}

.summary .meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: 8px 0 0;
  color: #64748b;
}

.summary-note {
  background: rgba(59, 130, 246, 0.08);
  border-radius: 16px;
  padding: 16px;
  color: #1d4ed8;
  line-height: 1.6;
}

.comparison {
  display: grid;
  gap: 24px;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.panel {
  background: #fff;
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 20px 42px rgba(15, 23, 42, 0.1);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel.highlight {
  border: 2px solid rgba(59, 130, 246, 0.18);
  box-shadow: 0 24px 50px rgba(59, 130, 246, 0.18);
}

.panel header h3 {
  margin: 0;
  font-size: 20px;
  color: #0f172a;
}

.panel header small {
  color: #64748b;
}

.panel ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.panel li {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 14px;
  border-radius: 16px;
  background: rgba(248, 250, 252, 0.8);
}

.panel li.changed {
  border: 1px solid rgba(37, 99, 235, 0.25);
  background: rgba(59, 130, 246, 0.08);
}

.label {
  font-size: 13px;
  color: #64748b;
}

.value {
  font-size: 15px;
  color: #0f172a;
  line-height: 1.5;
}

.review-box {
  background: #fff;
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 20px 42px rgba(15, 23, 42, 0.1);
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-label {
  font-weight: 600;
  color: #1e293b;
}

.review-textarea {
  width: 100%;
  min-height: 140px;
  border-radius: 16px;
  border: 1px solid rgba(203, 213, 225, 0.8);
  padding: 14px;
  font-size: 14px;
  resize: vertical;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.review-textarea:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
}

.success-msg {
  color: #047857;
  background: rgba(34, 197, 94, 0.12);
  padding: 10px 14px;
  border-radius: 12px;
}

.error-msg {
  color: #b91c1c;
}

.actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.btn {
  border: none;
  border-radius: 14px;
  padding: 12px 22px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.btn.approve {
  background: linear-gradient(135deg, #34d399, #059669);
  color: #fff;
  box-shadow: 0 16px 30px rgba(16, 185, 129, 0.25);
}

.btn.reject {
  background: linear-gradient(135deg, #f97316, #ef4444);
  color: #fff;
  box-shadow: 0 16px 30px rgba(239, 68, 68, 0.25);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

@media (max-width: 768px) {
  .panel li {
    padding: 12px;
  }

  .actions {
    flex-direction: column;
  }
}
</style>

