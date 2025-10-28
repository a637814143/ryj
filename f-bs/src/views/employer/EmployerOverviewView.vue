<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  fetchEmployerDashboard,
  type EmployerDashboardResponse,
  type EmployerJobOverview,
  type EmployerApplicationOverview,
  type EmployerInterviewOverview,
} from '../../api/employer'

const overview = ref<EmployerDashboardResponse | null>(null)
const loading = ref(false)
const error = ref('')

const userInfo = ref<{ id?: number; role?: string; fullName?: string; username?: string } | null>(null)
const userId = computed(() => userInfo.value?.id ?? null)

const header = computed(() => overview.value?.header)
const modules = computed(() => overview.value?.modules ?? [])
const jobs = computed<EmployerJobOverview[]>(() => overview.value?.jobs ?? [])
const applications = computed<EmployerApplicationOverview[]>(() => overview.value?.applications ?? [])
const interviews = computed<EmployerInterviewOverview[]>(() => overview.value?.interviews ?? [])
const companyProfile = computed(() => overview.value?.companyProfile ?? null)

const loadOverview = async () => {
  if (!userId.value) return
  loading.value = true
  error.value = ''
  try {
    overview.value = await fetchEmployerDashboard(userId.value)
  } catch (err) {
    console.error(err)
    error.value = (err as Error).message || '无法加载企业总览信息'
  } finally {
    loading.value = false
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
    error.value = '请使用企业账号登录后访问企业总览模块'
    return
  }
  loadOverview()
})
</script>

<template>
  <div class="employer-overview">
    <header class="page-header">
      <div class="title-block">
        <h1>企业运营总览</h1>
        <p class="subtitle">集中掌握岗位发布、候选人进展与面试安排</p>
      </div>
      <RouterLink class="profile-link" to="/employer/profile">完善企业资料</RouterLink>
    </header>

    <section v-if="error" class="error-box">{{ error }}</section>

    <section v-else>
      <div v-if="loading" class="loading-box">正在加载企业概览...</div>

      <template v-else>
        <div v-if="header" class="stats-grid">
          <div class="stat-card">
            <span class="label">企业名称</span>
            <h3>{{ header.companyName }}</h3>
            <p class="hint">欢迎回来，{{ userInfo?.fullName || userInfo?.username || '企业用户' }}</p>
          </div>
          <div class="stat-card">
            <span class="label">在招岗位</span>
            <h3>{{ header.openJobCount }}</h3>
            <p class="hint">共 {{ header.totalJobCount }} 个岗位</p>
          </div>
          <div class="stat-card">
            <span class="label">活跃简历</span>
            <h3>{{ header.activeApplicationCount }}</h3>
            <p class="hint">待处理候选人</p>
          </div>
          <div class="stat-card">
            <span class="label">面试排期</span>
            <h3>{{ header.scheduledInterviewCount }}</h3>
            <p class="hint">人才库：{{ header.talentPoolSize }}</p>
          </div>
        </div>

        <div v-if="!companyProfile" class="profile-alert">
          <h3>完善企业信息以解锁更多功能</h3>
          <p>请填写企业资料，系统将自动关联岗位管理、投递与面试流程。</p>
          <RouterLink class="alert-link" to="/employer/profile">立即前往完善资料</RouterLink>
        </div>

        <section class="modules">
          <h2>核心功能模块</h2>
          <div class="module-grid">
            <article v-for="module in modules" :key="module.key" class="module-card">
              <h3>{{ module.name }}</h3>
              <p class="description">{{ module.description }}</p>
              <ul>
                <li v-for="item in module.capabilities" :key="item">{{ item }}</li>
              </ul>
            </article>
          </div>
        </section>

        <section class="content-grid">
          <div class="panel">
            <div class="panel-header">
              <h2>岗位动态</h2>
              <RouterLink to="/employer/jobs">管理岗位</RouterLink>
            </div>
            <p v-if="jobs.length === 0" class="empty">暂无岗位，请先发布新的招聘信息。</p>
            <ul v-else class="job-list">
              <li v-for="job in jobs.slice(0, 5)" :key="job.id">
                <div class="job-title">{{ job.title }}</div>
                <div class="job-meta">
                  <span class="badge" :class="job.status?.toLowerCase()">{{ job.status || 'UNKNOWN' }}</span>
                  <span>{{ job.location || '地点待定' }}</span>
                  <span>{{ job.applicationCount }} 份申请</span>
                </div>
              </li>
            </ul>
          </div>

          <div class="panel">
            <div class="panel-header">
              <h2>最新投递</h2>
              <RouterLink to="/employer/applications">查看全部</RouterLink>
            </div>
            <p v-if="applications.length === 0" class="empty">暂时没有新的简历投递。</p>
            <ul v-else class="application-list">
              <li v-for="application in applications.slice(0, 6)" :key="application.id">
                <div class="candidate">
                  <strong>{{ application.candidateName || '候选人' }}</strong>
                  <span>申请岗位：{{ application.jobTitle || '已下架岗位' }}</span>
                </div>
                <div class="status">{{ application.status }}</div>
                <div class="time">{{ application.appliedAt ? new Date(application.appliedAt).toLocaleString() : '时间未知' }}</div>
              </li>
            </ul>
          </div>

          <div class="panel">
            <div class="panel-header">
              <h2>面试安排</h2>
              <RouterLink to="/employer/interviews">管理面试</RouterLink>
            </div>
            <p v-if="interviews.length === 0" class="empty">尚未安排新的面试。</p>
            <ul v-else class="interview-list">
              <li v-for="interview in interviews.slice(0, 6)" :key="interview.id">
                <div class="candidate">
                  <strong>{{ interview.candidateName || '候选人' }}</strong>
                  <span>{{ interview.jobTitle || '岗位未知' }}</span>
                </div>
                <div class="time">{{ interview.scheduledTime ? new Date(interview.scheduledTime).toLocaleString() : '待定时间' }}</div>
                <div class="status">{{ interview.status }}</div>
              </li>
            </ul>
          </div>
        </section>
      </template>
    </section>
  </div>
</template>

<style scoped>
.employer-overview {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1.5rem 4rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.title-block h1 {
  margin: 0;
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #2563eb, #9333ea);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  margin: 0.4rem 0 0;
  color: #64748b;
}

.profile-link {
  padding: 0.6rem 1.2rem;
  border-radius: 999px;
  background: #2563eb;
  color: #fff;
  text-decoration: none;
  font-weight: 600;
  transition: background 0.3s ease;
}

.profile-link:hover {
  background: #1d4ed8;
}

.error-box {
  background: rgba(248, 113, 113, 0.12);
  border: 1px solid rgba(248, 113, 113, 0.3);
  color: #b91c1c;
  padding: 1rem 1.5rem;
  border-radius: 12px;
}

.loading-box {
  background: #f8fafc;
  border-radius: 12px;
  padding: 1.5rem;
  text-align: center;
  color: #475569;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.25rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: #ffffff;
  border-radius: 18px;
  padding: 1.5rem;
  box-shadow: 0 12px 30px rgba(37, 99, 235, 0.08);
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.stat-card .label {
  font-size: 0.85rem;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.stat-card h3 {
  margin: 0;
  font-size: 1.9rem;
  color: #1e293b;
}

.stat-card .hint {
  margin: 0;
  color: #475569;
  font-size: 0.95rem;
}

.profile-alert {
  border-radius: 16px;
  padding: 1.75rem;
  margin-bottom: 2.5rem;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12), rgba(147, 51, 234, 0.12));
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.profile-alert h3 {
  margin: 0 0 0.5rem;
  color: #1e293b;
}

.profile-alert p {
  margin: 0 0 1rem;
  color: #475569;
}

.alert-link {
  display: inline-block;
  padding: 0.5rem 1.2rem;
  border-radius: 10px;
  background: #9333ea;
  color: #fff;
  text-decoration: none;
  font-weight: 600;
}

.modules {
  margin-bottom: 2.5rem;
}

.modules h2 {
  font-size: 1.5rem;
  margin-bottom: 1.2rem;
  color: #1e293b;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(230px, 1fr));
  gap: 1.2rem;
}

.module-card {
  background: #ffffff;
  border-radius: 14px;
  padding: 1.5rem;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.08);
}

.module-card h3 {
  margin: 0 0 0.6rem;
  color: #0f172a;
}

.module-card .description {
  margin: 0 0 0.8rem;
  color: #475569;
  min-height: 48px;
}

.module-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.module-card li {
  position: relative;
  padding-left: 1rem;
  color: #334155;
  font-size: 0.95rem;
}

.module-card li::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #2563eb;
  position: absolute;
  left: 0;
  top: 0.5rem;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.panel {
  background: #ffffff;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.panel-header h2 {
  margin: 0;
  font-size: 1.25rem;
  color: #0f172a;
}

.panel-header a {
  font-size: 0.9rem;
  color: #2563eb;
  text-decoration: none;
}

.empty {
  margin: 0;
  color: #94a3b8;
  font-size: 0.95rem;
}

.job-list,
.application-list,
.interview-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.job-title {
  font-weight: 600;
  color: #1e293b;
}

.job-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  color: #475569;
  font-size: 0.9rem;
}

.badge {
  padding: 0.1rem 0.6rem;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.1);
  color: #2563eb;
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.75rem;
}

.badge.open {
  background: rgba(34, 197, 94, 0.14);
  color: #16a34a;
}

.badge.closed {
  background: rgba(248, 113, 113, 0.14);
  color: #dc2626;
}

.application-list li,
.interview-list li {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.candidate {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  color: #1f2937;
}

.status {
  font-size: 0.85rem;
  font-weight: 600;
  color: #2563eb;
}

.time {
  font-size: 0.85rem;
  color: #64748b;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .profile-link {
    align-self: flex-start;
  }
}
</style>
