<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'

interface Statistics {
  totalJobs: number
  activeEmployers: number
  registeredStudents: number
  serviceTeachers: number
}

interface HighlightModule {
  name: string
  description: string
  features: string[]
  accentColor: string
}

interface QuickLink {
  label: string
  description: string
  target: string
}

interface NotificationItem {
  id: number
  title: string
  content: string
  category: string | null
  createdAt: string
}

interface JobSummary {
  id: number
  title: string
  location: string
  salaryRange: string
  workType: string | null
  publishedDate: string
}

interface ResourceSummary {
  id: number
  fileName: string
  description: string | null
  fileType: string | null
  fileSize: number
  downloadUrl: string
  createdAt: string
}

interface OverviewResponse {
  hero: {
    title: string
    subtitle: string
    description: string
    badges: string[]
  }
  statistics: Statistics
  modules: HighlightModule[]
  focusAreas: string[]
  quickLinks: QuickLink[]
  notifications: NotificationItem[]
  trendingJobs: JobSummary[]
  resources: ResourceSummary[]
}

interface SearchResult {
  type: string
  title: string
  subtitle: string | null
  description: string | null
  link: string | null
  timestamp: string | null
  metadata: Record<string, unknown>
}

interface SearchResponse {
  results: SearchResult[]
  total: number
  suggestions: string[]
  breakdown: Record<string, number>
}

interface SearchHistoryEntry {
  id: number
  keyword: string
  roleFilter: string | null
  categoryFilter: string | null
  locationFilter: string | null
  createdAt: string
}

interface UserPayload {
  id: number
  username: string
  fullName: string | null
  role: string | null
}

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080'

const overview = ref<OverviewResponse | null>(null)
const overviewLoading = ref(false)
const overviewError = ref('')

const statistics = computed(() => overview.value?.statistics)
const modules = computed(() => overview.value?.modules ?? [])
const focusAreas = computed(() => overview.value?.focusAreas ?? [])
const notifications = computed(() => overview.value?.notifications ?? [])
const trendingJobs = computed(() => overview.value?.trendingJobs ?? [])

const searchForm = reactive({
  keyword: '',
  role: 'ALL',
  location: '',
  categories: ['job', 'employer', 'notification', 'resource'] as string[],
})

const availableCategories = [
  { key: 'job', label: '招聘岗位' },
  { key: 'employer', label: '企业信息' },
  { key: 'notification', label: '消息通知' },
  { key: 'resource', label: '资料下载' },
]

const searchLoading = ref(false)
const searchError = ref('')
const searchResponse = ref<SearchResponse | null>(null)

const suggestions = computed(() => searchResponse.value?.suggestions ?? [])
const results = computed(() => searchResponse.value?.results ?? [])

const historyLoading = ref(false)
const historyEntries = ref<SearchHistoryEntry[]>([])

const resources = ref<ResourceSummary[]>([])
const resourceUploadLoading = ref(false)
const resourceUploadMessage = ref('')
const selectedFile = ref<File | null>(null)
const uploadDescription = ref('')

const user = ref<UserPayload | null>(null)

const toggleCategory = (key: string) => {
  const index = searchForm.categories.indexOf(key)
  if (index !== -1) {
    if (searchForm.categories.length === 1) return
    searchForm.categories.splice(index, 1)
  } else {
    searchForm.categories.push(key)
  }
}

const isCategoryActive = (key: string) => searchForm.categories.includes(key)

const loadOverview = async () => {
  overviewLoading.value = true
  overviewError.value = ''
  try {
    const response = await fetch(`${API_BASE}/api/public/overview`)
    const data = await response.json()
    if (response.ok && data.code === 200) {
      overview.value = data.data as OverviewResponse
      resources.value = data.data?.resources ?? []
    } else {
      overviewError.value = data.message || '无法加载首页数据'
    }
  } catch (error) {
    console.error(error)
    overviewError.value = '获取首页数据失败，请稍后重试'
  } finally {
    overviewLoading.value = false
  }
}

const loadResources = async () => {
  try {
    const response = await fetch(`${API_BASE}/api/public/files`)
    const data = await response.json()
    if (response.ok && data.code === 200) {
      resources.value = data.data as ResourceSummary[]
    }
  } catch (error) {
    console.error(error)
  }
}

const loadHistory = async () => {
  if (!user.value?.id) return
  historyLoading.value = true
  try {
    const response = await fetch(`${API_BASE}/api/public/search/history?userId=${user.value.id}`)
    const data = await response.json()
    if (response.ok && data.code === 200) {
      historyEntries.value = data.data as SearchHistoryEntry[]
    }
  } catch (error) {
    console.error(error)
  } finally {
    historyLoading.value = false
  }
}

const performSearch = async () => {
  if (!searchForm.keyword.trim()) {
    searchError.value = '请输入搜索关键字'
    return
  }
  searchError.value = ''
  searchLoading.value = true
  try {
    const params = new URLSearchParams()
    params.set('keyword', searchForm.keyword.trim())
    if (searchForm.role && searchForm.role !== 'ALL') {
      params.set('role', searchForm.role)
    }
    if (searchForm.location.trim()) {
      params.set('location', searchForm.location.trim())
    }
    if (searchForm.categories.length && searchForm.categories.length < availableCategories.length) {
      params.set('category', searchForm.categories.join(','))
    }
    if (user.value?.id) {
      params.set('userId', String(user.value.id))
    }

    const response = await fetch(`${API_BASE}/api/public/search?${params.toString()}`)
    const data = await response.json()
    if (response.ok && data.code === 200) {
      searchResponse.value = data.data as SearchResponse
      if (user.value?.id) {
        await loadHistory()
      }
    } else {
      searchError.value = data.message || '搜索失败，请稍后重试'
    }
  } catch (error) {
    console.error(error)
    searchError.value = '无法连接搜索服务，请稍后重试'
  } finally {
    searchLoading.value = false
  }
}

const removeHistory = async (entry: SearchHistoryEntry) => {
  if (!user.value?.id) return
  try {
    await fetch(`${API_BASE}/api/public/search/history/${entry.id}?userId=${user.value.id}`, {
      method: 'DELETE',
    })
    historyEntries.value = historyEntries.value.filter((item) => item.id !== entry.id)
  } catch (error) {
    console.error(error)
  }
}

const clearHistory = async () => {
  if (!user.value?.id) return
  try {
    await fetch(`${API_BASE}/api/public/search/history?userId=${user.value.id}`, {
      method: 'DELETE',
    })
    historyEntries.value = []
  } catch (error) {
    console.error(error)
  }
}

const applyHistory = (entry: SearchHistoryEntry) => {
  searchForm.keyword = entry.keyword
  if (entry.locationFilter) {
    searchForm.location = entry.locationFilter
  }
  if (entry.categoryFilter) {
    const raw = entry.categoryFilter.split(',').map((item) => item.trim()).filter(Boolean)
    if (raw.length) {
      const unique = Array.from(new Set(raw))
      searchForm.categories.splice(0, searchForm.categories.length, ...unique)
    }
  }
  performSearch()
}

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.item(0) || null
  selectedFile.value = file
}

const uploadResource = async () => {
  if (!selectedFile.value) {
    resourceUploadMessage.value = '请选择需要上传的文件'
    return
  }
  resourceUploadLoading.value = true
  resourceUploadMessage.value = ''
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    if (uploadDescription.value.trim()) {
      formData.append('description', uploadDescription.value.trim())
    }
    if (user.value?.id) {
      formData.append('uploaderId', String(user.value.id))
    }

    const response = await fetch(`${API_BASE}/api/public/files`, {
      method: 'POST',
      body: formData,
    })
    const data = await response.json()
    if (response.ok && data.code === 200) {
      resourceUploadMessage.value = '文件上传成功'
      resources.value = [data.data as ResourceSummary, ...resources.value]
      selectedFile.value = null
      uploadDescription.value = ''
    } else {
      resourceUploadMessage.value = data.message || '文件上传失败'
    }
  } catch (error) {
    console.error(error)
    resourceUploadMessage.value = '无法上传文件，请稍后重试'
  } finally {
    resourceUploadLoading.value = false
  }
}

const formatDateTime = (value: string | null) => {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const formatFileSize = (size: number) => {
  if (!size) return '—'
  const units = ['B', 'KB', 'MB', 'GB']
  let value = size
  let unitIndex = 0
  while (value >= 1024 && unitIndex < units.length - 1) {
    value /= 1024
    unitIndex += 1
  }
  return `${value.toFixed(value >= 10 || value < 1 ? 0 : 1)} ${units[unitIndex]}`
}

const initUser = () => {
  const raw = localStorage.getItem('ryj-current-user')
  if (!raw) return
  try {
    const payload = JSON.parse(raw) as UserPayload
    user.value = payload
  } catch (error) {
    console.warn('无法解析用户信息', error)
  }
}

onMounted(async () => {
  initUser()
  await loadOverview()
  await loadResources()
  await loadHistory()
})
</script>

<template>
  <div class="home">
    <section class="hero" v-if="overview">
      <div class="hero__content">
        <p class="hero__badge" v-for="badge in overview.hero.badges" :key="badge">{{ badge }}</p>
        <h2>{{ overview.hero.title }}</h2>
        <p class="hero__subtitle">{{ overview.hero.subtitle }}</p>
        <p class="hero__description">{{ overview.hero.description }}</p>
        <div class="hero__quick-links">
          <a
            v-for="item in overview.quickLinks"
            :key="item.label"
            class="hero__quick-link"
            :href="item.target || '#'"
          >
            <span>{{ item.label }}</span>
            <small>{{ item.description }}</small>
          </a>
        </div>
      </div>
      <div class="hero__glass">
        <div class="hero__stat" v-if="statistics">
          <div>
            <p class="value">{{ statistics.totalJobs }}</p>
            <p class="label">开放职位</p>
          </div>
          <div>
            <p class="value">{{ statistics.activeEmployers }}</p>
            <p class="label">入驻企业</p>
          </div>
          <div>
            <p class="value">{{ statistics.registeredStudents }}</p>
            <p class="label">学生用户</p>
          </div>
          <div>
            <p class="value">{{ statistics.serviceTeachers }}</p>
            <p class="label">指导教师</p>
          </div>
        </div>
        <ul class="hero__focus" v-if="focusAreas.length">
          <li v-for="item in focusAreas" :key="item">{{ item }}</li>
        </ul>
      </div>
    </section>
    <section v-else-if="overviewLoading" class="hero hero--loading">
      <div class="hero__content">
        <div class="skeleton skeleton__badge" />
        <div class="skeleton skeleton__title" />
        <div class="skeleton skeleton__text" />
        <div class="skeleton skeleton__text" />
      </div>
      <div class="hero__glass">
        <div class="skeleton skeleton__stat" />
        <div class="skeleton skeleton__list" />
      </div>
    </section>
    <section v-else-if="overviewError" class="hero hero--fallback">
      <div class="hero__content">
        <h2>公共首页暂时不可用</h2>
        <p class="hero__description">{{ overviewError }}</p>
      </div>
    </section>

    <section class="modules" v-if="modules.length">
      <article class="module" v-for="module in modules" :key="module.name" :style="{ '--accent': module.accentColor }">
        <header>
          <span class="module__tag">公共模块</span>
          <h3>{{ module.name }}</h3>
          <p>{{ module.description }}</p>
        </header>
        <ul>
          <li v-for="feature in module.features" :key="feature">{{ feature }}</li>
        </ul>
      </article>
    </section>

    <section class="search">
      <div class="search__panel">
        <div class="search__header">
          <div>
            <h3>全局搜索</h3>
            <p>跨角色检索岗位、通知、企业与公共资料</p>
          </div>
          <button class="search__action" type="button" @click="performSearch" :disabled="searchLoading">
            {{ searchLoading ? '正在搜索...' : '开始搜索' }}
          </button>
        </div>

        <div class="search__fields">
          <label>
            <span>关键字</span>
            <input v-model.trim="searchForm.keyword" type="text" placeholder="输入职位、企业、通知主题等" />
          </label>
          <label>
            <span>面向角色</span>
            <select v-model="searchForm.role">
              <option value="ALL">全部角色</option>
              <option value="STUDENT">学生</option>
              <option value="TEACHER">教师</option>
              <option value="EMPLOYER">企业</option>
              <option value="ADMIN">管理员</option>
            </select>
          </label>
          <label>
            <span>区域偏好</span>
            <input v-model.trim="searchForm.location" type="text" placeholder="可选：输入城市或地区" />
          </label>
        </div>

        <div class="search__filters">
          <span class="search__filters-label">高级筛选</span>
          <div class="search__chips">
            <button
              v-for="item in availableCategories"
              :key="item.key"
              type="button"
              class="chip"
              :class="{ active: isCategoryActive(item.key) }"
              @click="toggleCategory(item.key)"
            >
              {{ item.label }}
            </button>
          </div>
        </div>
        <p v-if="searchError" class="search__error">{{ searchError }}</p>
      </div>

      <aside class="search__history" v-if="user">
        <header>
          <h4>最近搜索</h4>
          <button type="button" @click="clearHistory" :disabled="!historyEntries.length">清空</button>
        </header>
        <div v-if="historyLoading" class="search__history-empty">加载中...</div>
        <div v-else-if="!historyEntries.length" class="search__history-empty">暂无搜索记录</div>
        <ul v-else>
          <li v-for="entry in historyEntries" :key="entry.id">
            <button type="button" @click="applyHistory(entry)">
              <span>{{ entry.keyword }}</span>
              <small>{{ formatDateTime(entry.createdAt) }}</small>
            </button>
            <span class="history__remove" @click.stop="removeHistory(entry)">×</span>
          </li>
        </ul>
      </aside>
    </section>

    <section class="results" v-if="searchResponse">
      <header>
        <div>
          <h3>搜索结果</h3>
          <p>共找到 {{ searchResponse.total }} 条匹配内容</p>
        </div>
        <div class="results__breakdown">
          <span v-for="(count, key) in searchResponse.breakdown" :key="key">{{ availableCategories.find((item) => item.key === key)?.label || key }} · {{ count }}</span>
        </div>
      </header>

      <div class="results__grid" v-if="results.length">
        <article v-for="item in results" :key="`${item.type}-${item.title}-${item.link}`" class="result-card" :data-type="item.type">
          <span class="result-card__type">{{ availableCategories.find((cat) => cat.key === item.type)?.label || item.type }}</span>
          <h4>{{ item.title }}</h4>
          <p class="result-card__subtitle" v-if="item.subtitle">{{ item.subtitle }}</p>
          <p class="result-card__description" v-if="item.description">{{ item.description }}</p>
          <div class="result-card__meta">
            <span v-if="item.timestamp">{{ formatDateTime(item.timestamp) }}</span>
            <span v-if="item.metadata?.salaryRange">薪资：{{ item.metadata.salaryRange as string }}</span>
            <span v-if="item.metadata?.workType">类型：{{ item.metadata.workType as string }}</span>
          </div>
          <a v-if="item.link" class="result-card__link" :href="item.link">查看详情 →</a>
        </article>
      </div>

      <div v-else class="results__empty">
        <p>暂无匹配结果，可尝试调整筛选条件。</p>
      </div>

      <footer v-if="suggestions.length" class="results__suggestions">
        <h4>搜索建议</h4>
        <ul>
          <li v-for="suggestion in suggestions" :key="suggestion">{{ suggestion }}</li>
        </ul>
      </footer>
    </section>

    <section class="insights">
      <div class="panel">
        <header>
          <h3>即时通知</h3>
          <p>系统公告、面试邀请等关键信息同步呈现</p>
        </header>
        <ul v-if="notifications.length">
          <li v-for="item in notifications" :key="item.id">
            <div>
              <span class="tag">{{ item.category || 'SYSTEM' }}</span>
              <h4>{{ item.title }}</h4>
              <p>{{ item.content }}</p>
            </div>
            <time>{{ formatDateTime(item.createdAt) }}</time>
          </li>
        </ul>
        <div v-else class="empty">暂无通知</div>
      </div>

      <div class="panel">
        <header>
          <h3>热门职位</h3>
          <p>根据实时投递热度与发布时间推荐</p>
        </header>
        <ul v-if="trendingJobs.length">
          <li v-for="job in trendingJobs" :key="job.id">
            <div>
              <h4>{{ job.title }}</h4>
              <p>{{ job.location }} · {{ job.workType || '灵活' }}</p>
              <small>{{ job.salaryRange || '薪资面议' }}</small>
            </div>
            <time>{{ formatDateTime(job.publishedDate) }}</time>
          </li>
        </ul>
        <div v-else class="empty">暂无开放职位</div>
      </div>
    </section>

    <section class="resources">
      <div class="resources__header">
        <div>
          <h3>公共资料中心</h3>
          <p>面向所有角色开放的模板、政策与指引</p>
        </div>
        <div class="upload" v-if="user">
          <label class="upload__input">
            <input type="file" @change="handleFileSelect" />
            <span>{{ selectedFile ? selectedFile.name : '选择文件' }}</span>
          </label>
          <input v-model.trim="uploadDescription" type="text" placeholder="添加文件描述（可选）" />
          <button type="button" @click="uploadResource" :disabled="resourceUploadLoading">
            {{ resourceUploadLoading ? '上传中...' : '上传共享' }}
          </button>
          <p v-if="resourceUploadMessage" class="upload__message">{{ resourceUploadMessage }}</p>
        </div>
      </div>

      <div class="resources__grid" v-if="resources.length">
        <article v-for="item in resources" :key="item.id" class="resource-card">
          <header>
            <h4>{{ item.fileName }}</h4>
            <span>{{ formatFileSize(item.fileSize) }}</span>
          </header>
          <p>{{ item.description || '公共资料文件' }}</p>
          <footer>
            <time>{{ formatDateTime(item.createdAt) }}</time>
            <a :href="`${API_BASE}${item.downloadUrl}`" target="_blank">下载</a>
          </footer>
        </article>
      </div>
      <div v-else class="resources__empty">暂无公共资料，可上传共享文件。</div>
    </section>
  </div>
</template>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  gap: 3rem;
  color: #0f172a;
}

.hero {
  position: relative;
  display: grid;
  gap: 2.5rem;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 0.9fr);
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.95), rgba(37, 99, 235, 0.85));
  border-radius: 32px;
  padding: 3.5rem;
  overflow: hidden;
  color: white;
  box-shadow: 0 45px 90px rgba(15, 23, 42, 0.3);
}

.hero--loading {
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.8), rgba(71, 85, 105, 0.8));
}

.hero--fallback {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.9), rgba(248, 113, 113, 0.85));
}

.skeleton {
  position: relative;
  overflow: hidden;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.15);
}

.skeleton::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, transparent, rgba(255, 255, 255, 0.35), transparent);
  animation: shimmer 1.8s infinite;
}

.skeleton__badge {
  height: 34px;
  width: 160px;
}

.skeleton__title {
  height: 42px;
  width: 60%;
}

.skeleton__text {
  height: 18px;
  width: 70%;
}

.skeleton__stat {
  height: 120px;
  border-radius: 28px;
}

.skeleton__list {
  height: 80px;
  border-radius: 28px;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.hero::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 80% 20%, rgba(96, 165, 250, 0.35), transparent 50%);
  pointer-events: none;
}

.hero__content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.hero__badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(148, 163, 184, 0.2);
  color: rgba(255, 255, 255, 0.8);
  border-radius: 999px;
  padding: 0.35rem 1.1rem;
  font-size: 0.85rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  width: fit-content;
}

.hero h2 {
  font-size: 2.6rem;
  font-weight: 700;
  margin: 0;
  letter-spacing: -0.02em;
}

.hero__subtitle {
  font-size: 1.2rem;
  font-weight: 500;
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
}

.hero__description {
  margin: 0;
  color: rgba(226, 232, 240, 0.92);
  line-height: 1.7;
}

.hero__quick-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 1rem;
}

.hero__quick-link {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  background: rgba(15, 23, 42, 0.45);
  border-radius: 18px;
  padding: 1.1rem 1.25rem;
  text-decoration: none;
  color: inherit;
  transition: transform 0.2s ease, background 0.2s ease;
}

.hero__quick-link:hover {
  transform: translateY(-4px);
  background: rgba(37, 99, 235, 0.55);
}

.hero__quick-link small {
  color: rgba(226, 232, 240, 0.75);
  font-size: 0.85rem;
}

.hero__glass {
  position: relative;
  z-index: 1;
  background: rgba(15, 23, 42, 0.35);
  border-radius: 24px;
  padding: 2.25rem;
  display: flex;
  flex-direction: column;
  gap: 1.75rem;
  backdrop-filter: blur(14px);
}

.hero__stat {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1.5rem;
}

.hero__stat .value {
  font-size: 2.1rem;
  font-weight: 700;
  margin: 0;
}

.hero__stat .label {
  margin: 0;
  color: rgba(226, 232, 240, 0.8);
  letter-spacing: 0.04em;
  text-transform: uppercase;
  font-size: 0.75rem;
}

.hero__focus {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.hero__focus li {
  position: relative;
  padding-left: 1.5rem;
  color: rgba(226, 232, 240, 0.88);
}

.hero__focus li::before {
  content: '';
  position: absolute;
  top: 0.5rem;
  left: 0;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #facc15, #f97316);
}

.modules {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
}

.module {
  background: white;
  border-radius: 28px;
  padding: 1.8rem;
  box-shadow: 0 25px 60px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  border: 1px solid rgba(148, 163, 184, 0.2);
}

.module__tag {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.35rem 0.9rem;
  border-radius: 999px;
  background: color-mix(in srgb, var(--accent) 18%, white);
  color: color-mix(in srgb, var(--accent) 80%, black);
  font-size: 0.75rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  width: fit-content;
}

.module h3 {
  margin: 0;
  font-size: 1.3rem;
  color: #0f172a;
}

.module p {
  margin: 0;
  color: #475569;
}

.module ul {
  margin: 0;
  padding-left: 1.2rem;
  color: #1f2937;
  display: flex;
  flex-direction: column;
  gap: 0.45rem;
}

.search {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: minmax(0, 2.1fr) minmax(0, 1fr);
}

.search__panel {
  background: white;
  border-radius: 28px;
  padding: 2rem;
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(148, 163, 184, 0.2);
  display: flex;
  flex-direction: column;
  gap: 1.75rem;
}

.search__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.search__header h3 {
  margin: 0;
  font-size: 1.5rem;
}

.search__header p {
  margin: 0;
  color: #64748b;
}

.search__action {
  background: linear-gradient(135deg, #2563eb, #8b5cf6);
  color: white;
  border: none;
  border-radius: 16px;
  padding: 0.85rem 1.6rem;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 12px 28px rgba(37, 99, 235, 0.3);
  transition: transform 0.2s ease;
}

.search__action:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

.search__action:not(:disabled):hover {
  transform: translateY(-1px);
}

.search__fields {
  display: grid;
  gap: 1.2rem;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

.search__fields label {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  font-weight: 600;
  color: #334155;
}

.search__fields input,
.search__fields select {
  padding: 0.75rem 1rem;
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.4);
  background: #f8fafc;
  font-size: 0.95rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search__fields input:focus,
.search__fields select:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.15);
  background: white;
}

.search__filters {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.search__filters-label {
  font-size: 0.85rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #64748b;
}

.search__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.chip {
  padding: 0.55rem 1.1rem;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.4);
  background: white;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
}

.chip.active {
  border-color: transparent;
  background: linear-gradient(135deg, #2563eb, #8b5cf6);
  color: white;
  box-shadow: 0 12px 22px rgba(37, 99, 235, 0.25);
}

.search__error {
  margin: 0;
  color: #dc2626;
  font-size: 0.9rem;
}

.search__history {
  background: rgba(15, 23, 42, 0.7);
  color: white;
  border-radius: 28px;
  padding: 1.75rem;
  backdrop-filter: blur(12px);
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.28);
}

.search__history header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.search__history h4 {
  margin: 0;
  font-size: 1.1rem;
}

.search__history button {
  border: none;
  background: rgba(255, 255, 255, 0.15);
  color: white;
  border-radius: 999px;
  padding: 0.45rem 1rem;
  cursor: pointer;
  transition: background 0.2s ease;
}

.search__history button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.search__history button:not(:disabled):hover {
  background: rgba(255, 255, 255, 0.25);
}

.search__history ul {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.search__history li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 18px;
  padding: 0.75rem 1rem;
}

.search__history li button {
  all: unset;
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  cursor: pointer;
}

.search__history li span {
  font-weight: 600;
  color: white;
}

.search__history li small {
  color: rgba(226, 232, 240, 0.7);
  font-size: 0.75rem;
}

.history__remove {
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0.2rem 0.5rem;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.2);
}

.search__history-empty {
  color: rgba(226, 232, 240, 0.7);
  text-align: center;
  padding: 1rem 0;
}

.results {
  background: white;
  border-radius: 28px;
  padding: 2rem;
  box-shadow: 0 22px 55px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1.75rem;
}

.results header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.results header h3 {
  margin: 0;
  font-size: 1.4rem;
}

.results header p {
  margin: 0;
  color: #64748b;
}

.results__breakdown {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  color: #1d4ed8;
  font-weight: 600;
}

.results__grid {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
}

.result-card {
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 22px;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.9), white);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.05);
}

.result-card__type {
  align-self: flex-start;
  padding: 0.3rem 0.85rem;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.12);
  color: #1d4ed8;
  font-weight: 600;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.result-card h4 {
  margin: 0;
  font-size: 1.1rem;
  color: #0f172a;
}

.result-card__subtitle {
  margin: 0;
  color: #1d4ed8;
  font-weight: 600;
}

.result-card__description {
  margin: 0;
  color: #475569;
  line-height: 1.6;
  max-height: 4.8rem;
  overflow: hidden;
}

.result-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  color: #64748b;
  font-size: 0.85rem;
}

.result-card__link {
  align-self: flex-start;
  color: #1d4ed8;
  font-weight: 600;
  text-decoration: none;
}

.results__empty {
  text-align: center;
  color: #64748b;
}

.results__suggestions {
  border-top: 1px dashed rgba(148, 163, 184, 0.4);
  padding-top: 1rem;
  color: #475569;
}

.insights {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.panel {
  background: white;
  border-radius: 26px;
  padding: 1.8rem;
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(148, 163, 184, 0.2);
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.panel header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.panel header p {
  margin: 0;
  color: #64748b;
}

.panel ul {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 1.1rem;
}

.panel li {
  display: flex;
  justify-content: space-between;
  gap: 1.5rem;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
  padding-bottom: 1rem;
}

.panel li:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.panel .tag {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
  padding: 0.25rem 0.7rem;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.12);
  color: #1d4ed8;
  font-size: 0.75rem;
  letter-spacing: 0.08em;
}

.panel time {
  color: #94a3b8;
  font-size: 0.8rem;
  min-width: 110px;
  text-align: right;
}

.panel .empty {
  text-align: center;
  color: #94a3b8;
}

.resources {
  background: white;
  border-radius: 32px;
  padding: 2.25rem;
  box-shadow: 0 25px 60px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1.75rem;
  border: 1px solid rgba(148, 163, 184, 0.2);
}

.resources__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 2rem;
}

.resources__header h3 {
  margin: 0;
  font-size: 1.4rem;
}

.resources__header p {
  margin: 0;
  color: #64748b;
}

.upload {
  display: grid;
  gap: 0.75rem;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  align-items: center;
}

.upload__input {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.75rem 1.2rem;
  border-radius: 16px;
  background: rgba(37, 99, 235, 0.08);
  color: #1d4ed8;
  cursor: pointer;
  font-weight: 600;
}

.upload__input input {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
}

.upload input[type='text'] {
  padding: 0.75rem 1rem;
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.4);
  background: #f8fafc;
}

.upload button {
  padding: 0.75rem 1.4rem;
  border-radius: 16px;
  border: none;
  background: linear-gradient(135deg, #22d3ee, #3b82f6);
  color: white;
  font-weight: 600;
  cursor: pointer;
}

.upload__message {
  grid-column: 1 / -1;
  margin: 0;
  font-size: 0.85rem;
  color: #0f172a;
}

.resources__grid {
  display: grid;
  gap: 1.5rem;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

.resource-card {
  border-radius: 22px;
  padding: 1.5rem;
  background: linear-gradient(180deg, rgba(56, 189, 248, 0.12), rgba(255, 255, 255, 0.92));
  border: 1px solid rgba(56, 189, 248, 0.25);
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.resource-card header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.resource-card h4 {
  margin: 0;
  font-size: 1.05rem;
}

.resource-card p {
  margin: 0;
  color: #0f172a;
}

.resource-card footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  font-size: 0.85rem;
  color: #0369a1;
}

.resource-card a {
  color: #0e7490;
  font-weight: 600;
  text-decoration: none;
}

.resources__empty {
  text-align: center;
  color: #64748b;
}

@media (max-width: 1024px) {
  .hero {
    grid-template-columns: 1fr;
    padding: 2.5rem;
  }

  .search {
    grid-template-columns: 1fr;
  }

  .search__history {
    order: -1;
  }

  .resources__header {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .hero {
    padding: 2rem;
  }

  .modules {
    grid-template-columns: 1fr;
  }

  .search__fields {
    grid-template-columns: 1fr;
  }

  .results__grid {
    grid-template-columns: 1fr;
  }

  .resources__grid {
    grid-template-columns: 1fr;
  }
}
</style>
