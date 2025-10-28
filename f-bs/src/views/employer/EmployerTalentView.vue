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
      <RouterLink class="back-link" to="/employer/overview">
        <span class="icon">←</span>
        <span>返回总览</span>
      </RouterLink>
    </header>

    <div class="summary-container">
      <div class="summary-card">
        <div class="card-icon">👥</div>
        <div class="card-content">
          <span class="label">人才总数</span>
          <h3>{{ summary.totalCandidates }}</h3>
          <p class="hint">符合筛选：{{ summary.filteredCandidates }}</p>
        </div>
      </div>
      <div class="summary-card pending">
        <div class="card-icon">📋</div>
        <div class="card-content">
          <span class="label">待处理候选人</span>
          <h3>{{ summary.pendingReviewCount }}</h3>
          <p class="hint">等待筛选处理</p>
        </div>
      </div>
      <div class="summary-card interview">
        <div class="card-icon">💼</div>
        <div class="card-content">
          <span class="label">面试中</span>
          <h3>{{ summary.interviewingCount }}</h3>
          <p class="hint">正在推进中</p>
        </div>
      </div>
      <div class="summary-card offer">
        <div class="card-icon">✨</div>
        <div class="card-content">
          <span class="label">已发 Offer</span>
          <h3>{{ summary.offerCount }}</h3>
          <p class="hint">录用意向发出</p>
        </div>
      </div>
    </div>

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
              <RouterLink v-if="candidate.latestResumeId" :to="`/resume/detail?id=${candidate.latestResumeId}`" class="resume-link">
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
  margin-bottom: 0.75rem;
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

.summary-container {
  display: flex;
  gap: 1rem;
  margin: 1.25rem 0 2rem;
  overflow-x: auto;
  padding: 0.25rem;
}

.summary-card {
  flex: 1;
  min-width: 200px;
  padding: 1.75rem 1.5rem;
  border-radius: 1.25rem;
  background: linear-gradient(135deg, #ffffff, #f8fafc);
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04), 0 1px 3px rgba(0, 0, 0, 0.02);
  display: flex;
  align-items: center;
  gap: 1.25rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.summary-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #60a5fa, #3b82f6);
  opacity: 0;
  transition: opacity 0.3s;
}

.summary-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08), 0 4px 12px rgba(0, 0, 0, 0.04);
  border-color: rgba(147, 197, 253, 0.5);
}

.summary-card:hover::before {
  opacity: 1;
}

.summary-card.pending::before {
  background: linear-gradient(90deg, #fbbf24, #f59e0b);
}

.summary-card.interview::before {
  background: linear-gradient(90deg, #8b5cf6, #7c3aed);
}

.summary-card.offer::before {
  background: linear-gradient(90deg, #10b981, #059669);
}

.card-icon {
  font-size: 2.5rem;
  width: 3.5rem;
  height: 3.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0f9ff, #e0f2fe);
  border-radius: 1rem;
  flex-shrink: 0;
  transition: all 0.3s;
}

.summary-card:hover .card-icon {
  transform: scale(1.1) rotate(5deg);
}

.summary-card.pending .card-icon {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
}

.summary-card.interview .card-icon {
  background: linear-gradient(135deg, #f3e8ff, #e9d5ff);
}

.summary-card.offer .card-icon {
  background: linear-gradient(135deg, #d1fae5, #a7f3d0);
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.summary-card .label {
  font-size: 0.8rem;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.summary-card h3 {
  margin: 0;
  font-size: 2.25rem;
  font-weight: 700;
  background: linear-gradient(135deg, #1e293b, #475569);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1.2;
}

.summary-card .hint {
  margin: 0;
  color: #94a3b8;
  font-size: 0.75rem;
  font-weight: 500;
}

@media (max-width: 1024px) {
  .summary-container {
    overflow-x: auto;
    padding-bottom: 0.5rem;
  }
  
  .summary-card {
    min-width: 180px;
  }
}

@media (max-width: 768px) {
  .summary-container {
    flex-direction: column;
  }
  
  .summary-card {
    min-width: auto;
  }
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
