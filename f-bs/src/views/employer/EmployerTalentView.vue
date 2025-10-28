<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import {
  fetchEmployerTalent,
  type EmployerTalentCandidate,
  type EmployerTalentResponse,
  type EmployerTalentSummary,
  type JobApplicationStatus,
  type InterviewStatus,
} from '../../api/employer'

const userInfo = ref<{ id?: number; role?: string; fullName?: string; username?: string } | null>(null)
const userId = computed(() => userInfo.value?.id ?? null)
const loading = ref(false)
const error = ref('')
const talentData = ref<EmployerTalentResponse | null>(null)

const keyword = ref('')
const statusFilter = ref<JobApplicationStatus | ''>('')
const interviewFilter = ref<InterviewStatus | ''>('')

const summaryFallback: EmployerTalentSummary = {
  totalCandidates: 0,
  filteredCandidates: 0,
  pendingReviewCount: 0,
  interviewingCount: 0,
  offerCount: 0,
}

const summary = computed(() => talentData.value?.summary ?? summaryFallback)
const candidates = computed<EmployerTalentCandidate[]>(() => talentData.value?.candidates ?? [])

const statusOptions: { value: JobApplicationStatus | ''; label: string }[] = [
  { value: '', label: '全部简历状态' },
  { value: 'SUBMITTED', label: '待处理' },
  { value: 'REVIEWING', label: '筛选中' },
  { value: 'INTERVIEW', label: '进入面试' },
  { value: 'OFFERED', label: '已发 Offer' },
  { value: 'REJECTED', label: '已淘汰' },
]

const interviewOptions: { value: InterviewStatus | ''; label: string }[] = [
  { value: '', label: '全部面试状态' },
  { value: 'SCHEDULED', label: '已安排' },
  { value: 'COMPLETED', label: '已完成' },
  { value: 'CANCELLED', label: '已取消' },
]

const statusLabels: Record<JobApplicationStatus, string> = {
  SUBMITTED: '待处理',
  REVIEWING: '筛选中',
  INTERVIEW: '进入面试',
  OFFERED: '已发 Offer',
  REJECTED: '已淘汰',
}

const interviewLabels: Record<InterviewStatus, string> = {
  SCHEDULED: '已安排',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
}

const loadTalent = async () => {
  if (!userId.value) return
  loading.value = true
  error.value = ''
  try {
    talentData.value = await fetchEmployerTalent(userId.value, {
      keyword: keyword.value.trim() || undefined,
      status: statusFilter.value || undefined,
      interviewStatus: interviewFilter.value || undefined,
    })
  } catch (err) {
    console.error(err)
    error.value = (err as Error).message || '无法加载人才库数据'
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  if (!userId.value) return
  loadTalent()
}

const resetFilters = () => {
  keyword.value = ''
  statusFilter.value = ''
  interviewFilter.value = ''
  if (!userId.value) return
  loadTalent()
}

watch([statusFilter, interviewFilter], () => {
  if (!userId.value || userInfo.value?.role !== 'EMPLOYER') {
    return
  }
  loadTalent()
})

const formatDateTime = (value: string | null) => {
  if (!value) return '时间未知'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }
  return date.toLocaleString()
}

const formatCities = (cities: string[]) => {
  if (!cities || cities.length === 0) {
    return '未设置意向城市'
  }
  return cities.join('、')
}

const formatWorkType = (value: string | null) => {
  switch (value) {
    case 'FULL_TIME':
      return '全职'
    case 'PART_TIME':
      return '兼职'
    case 'INTERNSHIP':
      return '实习'
    case 'FLEXIBLE':
      return '灵活'
    default:
      return '未指定'
  }
}

onMounted(() => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    try {
      userInfo.value = JSON.parse(stored)
    } catch (err) {
      console.error('解析用户信息失败', err)
      userInfo.value = null
    }
  }

  if (!userId.value || userInfo.value?.role !== 'EMPLOYER') {
    error.value = '仅企业账号可查看人才库，请登录企业账号后重试'
    return
  }

  loadTalent()
})
</script>

<template>
  <div class="employer-talent">
    <header class="page-header">
      <div class="title-block">
        <h1>企业人才库</h1>
        <p class="subtitle">沉淀候选人资产，洞察招聘进展与候选人画像</p>
      </div>
      <RouterLink class="back-link" to="/employer/overview">返回企业总览</RouterLink>
    </header>

    <section v-if="error" class="error-box">{{ error }}</section>

    <section v-else>
      <div class="filters">
        <div class="search-group">
          <input
            v-model="keyword"
            type="text"
            placeholder="搜索候选人姓名、职位、专业或意向城市"
            @keyup.enter="handleSearch"
          />
          <button type="button" class="primary" @click="handleSearch">搜索</button>
        </div>
        <div class="select-group">
          <label>
            简历状态
            <select v-model="statusFilter">
              <option v-for="option in statusOptions" :key="option.value || 'all'" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </label>
          <label>
            面试状态
            <select v-model="interviewFilter">
              <option v-for="option in interviewOptions" :key="option.value || 'all'" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </label>
          <button type="button" class="ghost" @click="resetFilters">重置筛选</button>
        </div>
      </div>

      <div class="summary-grid">
        <div class="summary-card">
          <span class="label">人才总数</span>
          <h3>{{ summary.totalCandidates }}</h3>
          <p class="hint">符合筛选条件：{{ summary.filteredCandidates }}</p>
        </div>
        <div class="summary-card">
          <span class="label">待处理候选人</span>
          <h3>{{ summary.pendingReviewCount }}</h3>
          <p class="hint">包含已提交与筛选中的简历</p>
        </div>
        <div class="summary-card">
          <span class="label">面试中</span>
          <h3>{{ summary.interviewingCount }}</h3>
          <p class="hint">当前正在推进的面试安排</p>
        </div>
        <div class="summary-card">
          <span class="label">已发 Offer</span>
          <h3>{{ summary.offerCount }}</h3>
          <p class="hint">发出录用意向的候选人</p>
        </div>
      </div>

      <div v-if="loading" class="loading-box">正在加载人才库数据...</div>

      <div v-else>
        <p v-if="candidates.length === 0" class="empty">
          暂无候选人记录，待候选人向贵司投递或完善筛选条件后查看。
        </p>

        <div v-else class="talent-list">
          <article
            v-for="candidate in candidates"
            :key="candidate.studentId ?? candidate.latestResumeId ?? Math.random()"
            class="talent-card"
          >
            <header class="talent-header">
              <div>
                <h3>{{ candidate.candidateName || '候选人' }}</h3>
                <p class="meta">
                  <span>{{ candidate.major || '专业未填写' }}</span>
                  <span v-if="candidate.graduationYear">预计毕业：{{ candidate.graduationYear }}</span>
                </p>
              </div>
              <div class="status-group">
                <span class="badge status">{{ candidate.latestStatus ? statusLabels[candidate.latestStatus] : '状态未知' }}</span>
                <span class="badge interview" :class="candidate.latestInterviewStatus?.toLowerCase()">
                  {{ candidate.latestInterviewStatus ? interviewLabels[candidate.latestInterviewStatus] : '无面试' }}
                </span>
              </div>
            </header>

            <div class="talent-body">
              <div class="info">
                <p>
                  <strong>最近申请岗位：</strong>
                  {{ candidate.latestJobTitle || '岗位已下架' }}
                  <span class="time">（{{ formatDateTime(candidate.lastAppliedAt) }}）</span>
                </p>
                <p>
                  <strong>求职意向：</strong>
                  {{ candidate.expectedPosition || '未填写期望职位' }} · {{ formatWorkType(candidate.expectedWorkType || null) }}
                </p>
                <p>
                  <strong>意向城市：</strong>
                  {{ formatCities(candidate.intentionCities) }}
                </p>
              </div>

              <div class="metrics">
                <div>
                  <span class="metric-value">{{ candidate.applicationCount }}</span>
                  <span class="metric-label">累计申请</span>
                </div>
                <div>
                  <span class="metric-value">{{ candidate.interviewCount }}</span>
                  <span class="metric-label">面试轮次</span>
                </div>
                <div>
                  <span class="metric-value">{{ candidate.latestResumeId ?? '-' }}</span>
                  <span class="metric-label">最新简历 ID</span>
                </div>
              </div>
            </div>

            <footer class="talent-footer">
              <div>
                <span><strong>邮箱：</strong>{{ candidate.email || '未提供' }}</span>
                <span><strong>电话：</strong>{{ candidate.phone || '未提供' }}</span>
              </div>
              <RouterLink v-if="candidate.latestResumeId" :to="`/student/resume?id=${candidate.latestResumeId}`" class="resume-link">
                查看简历详情
              </RouterLink>
            </footer>
          </article>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.employer-talent {
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

.title-block h1 {
  margin: 0;
  font-size: 2.4rem;
  font-weight: 700;
  color: #111827;
}

.subtitle {
  margin: 0.5rem 0 0;
  color: #6b7280;
}

.back-link {
  align-self: flex-start;
  padding: 0.5rem 1rem;
  border-radius: 999px;
  background: #eef2ff;
  color: #3730a3;
  text-decoration: none;
  transition: background 0.2s;
}

.back-link:hover {
  background: #e0e7ff;
}

.filters {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1.25rem;
  border: 1px solid #e5e7eb;
  border-radius: 1rem;
  background: #f9fafb;
}

.search-group {
  display: flex;
  gap: 0.75rem;
}

.search-group input {
  flex: 1;
  padding: 0.75rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 0.75rem;
  font-size: 1rem;
}

.search-group .primary {
  padding: 0 1.5rem;
  border: none;
  border-radius: 0.75rem;
  background: linear-gradient(135deg, #2563eb, #4f46e5);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s;
}

.search-group .primary:hover {
  transform: translateY(-1px);
}

.select-group {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.select-group label {
  display: flex;
  flex-direction: column;
  font-size: 0.95rem;
  color: #374151;
  gap: 0.4rem;
}

.select-group select {
  padding: 0.6rem 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 0.65rem;
  background: #fff;
}

.select-group .ghost {
  padding: 0.6rem 1.2rem;
  border: 1px solid #c7d2fe;
  border-radius: 0.75rem;
  background: #eef2ff;
  color: #3730a3;
  cursor: pointer;
  transition: background 0.2s;
}

.select-group .ghost:hover {
  background: #e0e7ff;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1rem;
  margin: 1.5rem 0;
}

.summary-card {
  padding: 1.25rem;
  border-radius: 1rem;
  background: linear-gradient(135deg, #eef2ff, #e0f2fe);
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.summary-card .label {
  font-size: 0.9rem;
  color: #4c1d95;
}

.summary-card h3 {
  margin: 0;
  font-size: 2rem;
  color: #111827;
}

.summary-card .hint {
  margin: 0;
  color: #4338ca;
  font-size: 0.85rem;
}

.loading-box {
  padding: 2rem;
  text-align: center;
  color: #4f46e5;
}

.error-box {
  padding: 1rem 1.5rem;
  border-radius: 0.75rem;
  background: #fee2e2;
  color: #991b1b;
}

.empty {
  padding: 2rem;
  text-align: center;
  color: #6b7280;
}

.talent-list {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.talent-card {
  border: 1px solid #e5e7eb;
  border-radius: 1.25rem;
  padding: 1.5rem;
  background: #fff;
  box-shadow: 0 10px 30px rgba(79, 70, 229, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.talent-header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: center;
}

.talent-header h3 {
  margin: 0;
  font-size: 1.4rem;
  color: #1f2937;
}

.talent-header .meta {
  margin: 0.3rem 0 0;
  color: #6b7280;
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.status-group {
  display: flex;
  gap: 0.6rem;
  flex-wrap: wrap;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 0.35rem 0.75rem;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 600;
}

.badge.status {
  background: #ede9fe;
  color: #5b21b6;
}

.badge.interview {
  background: #dbeafe;
  color: #1e40af;
}

.badge.interview.completed {
  background: #dcfce7;
  color: #166534;
}

.badge.interview.cancelled {
  background: #fee2e2;
  color: #991b1b;
}

.talent-body {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.talent-body .info {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  color: #374151;
}

.talent-body .time {
  color: #6b7280;
  font-size: 0.85rem;
}

.metrics {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 0.75rem;
}

.metrics div {
  border: 1px solid #ede9fe;
  border-radius: 0.85rem;
  padding: 0.75rem;
  text-align: center;
  background: #f5f3ff;
}

.metric-value {
  display: block;
  font-size: 1.4rem;
  font-weight: 700;
  color: #4c1d95;
}

.metric-label {
  font-size: 0.85rem;
  color: #5b21b6;
}

.talent-footer {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 0.75rem;
  color: #4b5563;
  font-size: 0.95rem;
}

.talent-footer strong {
  color: #1f2937;
}

.resume-link {
  align-self: flex-start;
  padding: 0.5rem 1rem;
  border-radius: 0.75rem;
  background: #eef2ff;
  color: #3730a3;
  text-decoration: none;
}

.resume-link:hover {
  background: #e0e7ff;
}

@media (max-width: 768px) {
  .search-group {
    flex-direction: column;
  }

  .search-group .primary {
    width: 100%;
  }

  .talent-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .talent-footer {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
