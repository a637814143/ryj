<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

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

interface JobDetail {
  posting: {
    id: number
    employerId: number
    title: string
    description: string | null
    salaryRange: string | null
    location: string | null
    workType: string | null
    status: string | null
    publishedDate: string | null
    closingDate: string | null
  }
  requirements: Array<{ requirement: string }>
}

interface EmployerInfo {
  id: number
  userId: number
  companyName: string
  contactPerson: string | null
  contactEmail: string | null
  contactPhone: string | null
  description: string | null
  website: string | null
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
const router = useRouter()

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

// 职位详情弹窗相关
const showJobDetail = ref(false)
const jobDetailLoading = ref(false)
const jobDetail = ref<JobDetail | null>(null)
const employerInfo = ref<EmployerInfo | null>(null)

// 登录提示对话框
const showLoginPrompt = ref(false)
const loginPromptMessage = ref('')

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

const getFileIcon = (fileType: string | null) => {
  if (!fileType) return '📄'
  const type = fileType.toLowerCase()
  if (type.includes('pdf')) return '📕'
  if (type.includes('word') || type.includes('doc')) return '📘'
  if (type.includes('excel') || type.includes('sheet')) return '📊'
  if (type.includes('powerpoint') || type.includes('presentation')) return '📙'
  if (type.includes('image') || type.includes('png') || type.includes('jpg')) return '🖼️'
  if (type.includes('video')) return '🎬'
  if (type.includes('audio')) return '🎵'
  if (type.includes('zip') || type.includes('rar')) return '📦'
  if (type.includes('text')) return '📝'
  return '📄'
}

const scrollToModule = (moduleKey: string) => {
  // 定义模块键与元素ID的映射关系
  const moduleIdMap: Record<string, string> = {
    'job': 'job-module',
    'employer': 'employer-module',
    'notification': 'notification-module',
    'resource': 'resource-module',
  }

  const targetId = moduleIdMap[moduleKey]
  if (!targetId) return

  const element = document.getElementById(targetId)
  if (!element) return

  // 计算需要滚动的位置，考虑顶部搜索栏的高度
  const searchTopHeight = 80 // 顶部搜索区域的大致高度
  const elementPosition = element.getBoundingClientRect().top
  const offsetPosition = elementPosition + window.pageYOffset - searchTopHeight

  // 平滑滚动到目标位置
  window.scrollTo({
    top: offsetPosition,
    behavior: 'smooth'
  })
}

const formatWorkType = (workType: string | null) => {
  if (!workType) return '灵活'
  const workTypeMap: Record<string, string> = {
    'FULL_TIME': '全职',
    'PART_TIME': '兼职',
    'INTERNSHIP': '实习',
    'REMOTE': '远程',
  }
  return workTypeMap[workType] || workType
}

const formatNotificationCategory = (category: string | null) => {
  if (!category) return '系统'
  const categoryMap: Record<string, string> = {
    'SYSTEM': '系统',
    'GUIDANCE': '指导',
    'APPLICATION': '申请',
    'INTERVIEW': '面试',
  }
  return categoryMap[category] || category
}

const openJobDetail = async (jobId: number) => {
  showJobDetail.value = true
  jobDetailLoading.value = true
  jobDetail.value = null
  employerInfo.value = null
  
  try {
    // 获取职位详情
    const jobResponse = await fetch(`${API_BASE}/api/job-postings/${jobId}`)
    const jobData = await jobResponse.json()
    
    if (jobResponse.ok && jobData.code === 200) {
      jobDetail.value = jobData.data as JobDetail
      
      // 获取企业信息
      const employerId = jobData.data.posting.employerId
      if (employerId) {
        const employerResponse = await fetch(`${API_BASE}/api/employers/${employerId}`)
        const employerData = await employerResponse.json()
        
        if (employerResponse.ok && employerData.code === 200) {
          employerInfo.value = employerData.data as EmployerInfo
        }
      }
    }
  } catch (error) {
    console.error('获取职位详情失败', error)
  } finally {
    jobDetailLoading.value = false
  }
}

const closeJobDetail = () => {
  showJobDetail.value = false
  jobDetail.value = null
  employerInfo.value = null
}

const handleApplyJob = () => {
  if (!user.value) {
    loginPromptMessage.value = '请先登录后才能申请职位'
    showLoginPrompt.value = true
    return
  }
  
  // 检查用户角色，只有学生可以申请职位
  if (user.value.role !== 'STUDENT') {
    alert('只有学生用户可以申请职位')
    return
  }
  
  // 已登录的学生用户，跳转到学生职位申请页面
  if (jobDetail.value?.posting.id) {
    closeJobDetail()
    // 跳转到学生职位申请页面，并传递职位ID
    router.push({
      path: '/student/jobs',
      query: { jobId: jobDetail.value.posting.id }
    })
  }
}

const handleCollectJob = () => {
  if (!user.value) {
    loginPromptMessage.value = '请先登录后才能收藏职位'
    showLoginPrompt.value = true
    return
  }
  
  // TODO: 实现职位收藏逻辑
  alert('收藏成功！')
  closeJobDetail()
}

const closeLoginPrompt = () => {
  showLoginPrompt.value = false
  loginPromptMessage.value = ''
}

const goToLogin = () => {
  closeLoginPrompt()
  closeJobDetail()
  // 跳转到登录页面
  router.push('/login')
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
    <!-- 顶部搜索区域 -->
    <section class="search-top">
      <div class="search-top__container">
        <div class="search-top__input-wrapper">
          <input
            v-model.trim="searchForm.keyword"
            type="text"
            class="search-top__input"
            placeholder="搜索职位、企业、通知或资料..."
            @keyup.enter="performSearch"
          />
          <button
            class="search-top__button"
            type="button"
            @click="performSearch"
            :disabled="searchLoading"
          >
            <span class="search-icon">🔍</span>
            {{ searchLoading ? '搜索中...' : '搜索' }}
          </button>
        </div>
        
        <!-- 快速筛选标签 -->
        <div class="search-top__chips">
          <button
            v-for="item in availableCategories"
            :key="item.key"
            type="button"
            class="search-chip"
            :class="{ active: isCategoryActive(item.key) }"
            @click="scrollToModule(item.key)"
          >
            {{ item.label }}
          </button>
        </div>
        
        <!-- 高级选项（可折叠） -->
        <details class="search-top__advanced">
          <summary>高级搜索选项</summary>
          <div class="search-advanced__content">
            <label class="search-field">
              <span>面向角色</span>
              <select v-model="searchForm.role">
                <option value="ALL">全部角色</option>
                <option value="STUDENT">学生</option>
                <option value="TEACHER">教师</option>
                <option value="EMPLOYER">企业</option>
                <option value="ADMIN">管理员</option>
              </select>
            </label>
            <label class="search-field">
              <span>区域偏好</span>
              <input v-model.trim="searchForm.location" type="text" placeholder="输入城市或地区" />
            </label>
          </div>
        </details>
        
        <p v-if="searchError" class="search-top__error">{{ searchError }}</p>
      </div>
    </section>

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
      <div id="employer-module" class="hero__glass">
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
            <span v-if="item.metadata?.workType">类型：{{ formatWorkType(item.metadata.workType as string) }}</span>
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
      <div id="notification-module" class="panel">
        <header>
          <h3>即时通知</h3>
          <p>系统公告、面试邀请等关键信息同步呈现</p>
        </header>
        <ul v-if="notifications.length">
          <li v-for="item in notifications" :key="item.id">
            <div>
              <span class="tag">{{ formatNotificationCategory(item.category) }}</span>
              <h4>{{ item.title }}</h4>
              <p>{{ item.content }}</p>
            </div>
            <time>{{ formatDateTime(item.createdAt) }}</time>
          </li>
        </ul>
        <div v-else class="empty">暂无通知</div>
      </div>

      <div id="job-module" class="panel">
        <header>
          <h3>热门职位</h3>
          <p>根据实时投递热度与发布时间推荐</p>
        </header>
        <ul v-if="trendingJobs.length">
          <li v-for="job in trendingJobs" :key="job.id" @click="openJobDetail(job.id)" class="job-item-clickable">
            <div>
              <h4>{{ job.title }}</h4>
              <p>{{ job.location }} · {{ formatWorkType(job.workType) }}</p>
              <small>{{ job.salaryRange || '薪资面议' }}</small>
            </div>
            <time>{{ formatDateTime(job.publishedDate) }}</time>
          </li>
        </ul>
        <div v-else class="empty">暂无开放职位</div>
      </div>
    </section>

    <section id="resource-module" class="resources">
      <div class="resources__header">
        <div class="header-content">
          <div class="header-icon">📚</div>
          <div class="header-text">
            <h2>公共资料中心</h2>
            <p>面向所有角色开放的模板、政策与指引文档</p>
          </div>
        </div>
        
        <!-- 上传区域 -->
        <div class="upload-section" v-if="user">
          <div class="upload-card">
            <div class="upload-header">
              <span class="upload-icon">⬆️</span>
              <h3>上传资料</h3>
            </div>
            <div class="upload-form">
              <label class="file-select">
                <input type="file" @change="handleFileSelect" hidden />
                <div class="file-select-button">
                  <span class="file-icon">📎</span>
                  <span class="file-text">{{ selectedFile ? selectedFile.name : '点击选择文件' }}</span>
                </div>
              </label>
              <input 
                v-model.trim="uploadDescription" 
                type="text" 
                class="upload-description" 
                placeholder="文件描述（可选）" 
              />
              <button 
                type="button" 
                class="upload-button" 
                @click="uploadResource" 
                :disabled="resourceUploadLoading || !selectedFile"
              >
                <span class="button-icon">{{ resourceUploadLoading ? '⏳' : '✓' }}</span>
                {{ resourceUploadLoading ? '上传中...' : '上传共享' }}
              </button>
            </div>
            <p v-if="resourceUploadMessage" class="upload-message">{{ resourceUploadMessage }}</p>
          </div>
        </div>
      </div>

      <!-- 资源列表（横向滚动） -->
      <div v-if="resources.length" class="resources-scroll">
        <article v-for="item in resources" :key="item.id" class="resource-card">
          <div class="resource-card-icon">
            <span>{{ getFileIcon(item.fileType) }}</span>
          </div>
          <div class="resource-card-content">
            <h4 class="resource-card-title">{{ item.fileName }}</h4>
            <p class="resource-card-desc">{{ item.description || '暂无描述' }}</p>
            <div class="resource-card-meta">
              <span class="meta-badge">
                <span class="meta-icon">📦</span>
                {{ formatFileSize(item.fileSize) }}
              </span>
              <span class="meta-badge">
                <span class="meta-icon">🕒</span>
                {{ formatDateTime(item.createdAt) }}
              </span>
            </div>
          </div>
          <a :href="`${API_BASE}${item.downloadUrl}`" target="_blank" class="resource-download-btn">
            <span class="download-icon">⬇</span>
            <span>下载</span>
          </a>
        </article>
      </div>
      <div v-else class="resources-empty">
        <div class="empty-icon">📭</div>
        <p>暂无公共资料</p>
        <small v-if="user">点击上方上传按钮分享资料</small>
      </div>
    </section>

    <!-- 职位详情弹窗 -->
    <div v-if="showJobDetail" class="job-detail-modal" @click.self="closeJobDetail">
      <div class="job-detail-content">
        <button class="close-btn" @click="closeJobDetail">✕</button>
        
        <div v-if="jobDetailLoading" class="loading-container">
          <div class="loading-spinner">加载中...</div>
        </div>
        
        <div v-else-if="jobDetail" class="job-detail-body">
          <!-- 职位基本信息 -->
          <div class="job-detail-header">
            <h2>{{ jobDetail.posting.title }}</h2>
            <div class="job-meta">
              <span class="meta-item">
                <span class="icon">📍</span>
                {{ jobDetail.posting.location || '未指定' }}
              </span>
              <span class="meta-item">
                <span class="icon">💼</span>
                {{ formatWorkType(jobDetail.posting.workType) }}
              </span>
              <span class="meta-item">
                <span class="icon">💰</span>
                {{ jobDetail.posting.salaryRange || '薪资面议' }}
              </span>
              <span v-if="jobDetail.posting.closingDate" class="meta-item">
                <span class="icon">📅</span>
                截止：{{ jobDetail.posting.closingDate }}
              </span>
            </div>
          </div>

          <!-- 企业信息 -->
          <div v-if="employerInfo" class="employer-section">
            <h3>🏢 企业信息</h3>
            <div class="employer-card">
              <div class="employer-name">{{ employerInfo.companyName }}</div>
              <p v-if="employerInfo.description" class="employer-desc">{{ employerInfo.description }}</p>
              <div class="employer-contacts">
                <span v-if="employerInfo.contactPerson" class="contact-item">
                  <span class="icon">👤</span>
                  联系人：{{ employerInfo.contactPerson }}
                </span>
                <span v-if="employerInfo.contactEmail" class="contact-item">
                  <span class="icon">📧</span>
                  {{ employerInfo.contactEmail }}
                </span>
                <span v-if="employerInfo.contactPhone" class="contact-item">
                  <span class="icon">📱</span>
                  {{ employerInfo.contactPhone }}
                </span>
                <a v-if="employerInfo.website" :href="employerInfo.website" target="_blank" class="contact-item website-link">
                  <span class="icon">🌐</span>
                  {{ employerInfo.website }}
                </a>
              </div>
            </div>
          </div>

          <!-- 职位描述 -->
          <div v-if="jobDetail.posting.description" class="job-section">
            <h3>📝 职位描述</h3>
            <p class="job-description">{{ jobDetail.posting.description }}</p>
          </div>

          <!-- 任职要求 -->
          <div v-if="jobDetail.requirements && jobDetail.requirements.length" class="job-section">
            <h3>✅ 任职要求</h3>
            <ul class="requirements-list">
              <li v-for="(req, index) in jobDetail.requirements" :key="index">
                {{ req.requirement }}
              </li>
            </ul>
          </div>

          <!-- 申请按钮 -->
          <div class="job-actions">
            <button class="apply-btn" @click="handleApplyJob">
              <span class="icon">💼</span>
              立即申请
            </button>
            <button class="collect-btn" @click="handleCollectJob">
              <span class="icon">⭐</span>
              收藏职位
            </button>
          </div>
        </div>

        <div v-else class="error-container">
          <p>暂无职位详情</p>
        </div>
      </div>
    </div>

    <!-- 登录提示对话框 -->
    <div v-if="showLoginPrompt" class="login-prompt-modal" @click.self="closeLoginPrompt">
      <div class="login-prompt-content">
        <div class="prompt-icon">🔐</div>
        <h3 class="prompt-title">需要登录</h3>
        <p class="prompt-message">{{ loginPromptMessage }}</p>
        <div class="prompt-actions">
          <button class="btn-cancel" @click="closeLoginPrompt">
            <span class="icon">✕</span>
            取消
          </button>
          <button class="btn-login" @click="goToLogin">
            <span class="icon">👤</span>
            去登录
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  color: #1d1d1f;
  background: linear-gradient(to bottom, #f5f5f7 0%, #ffffff 100%);
  min-height: 100vh;
  padding: 0;
}

/* ===== 顶部搜索区域（超紧凑版）===== */
.search-top {
  background: #ffffff;
  padding: 0.75rem 2rem;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.04);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.search-top__container {
  max-width: 1600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.search-top__input-wrapper {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.search-top__input {
  flex: 1;
  padding: 0.625rem 1rem;
  font-size: 0.875rem;
  border: 1.5px solid rgba(0, 0, 0, 0.08);
  border-radius: 10px;
  background: #f5f5f7;
  color: #1d1d1f;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.search-top__input::placeholder {
  color: #86868b;
}

.search-top__input:focus {
  outline: none;
  border-color: #0071e3;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(0, 113, 227, 0.08);
}

.search-top__button {
  padding: 0.625rem 1.25rem;
  font-size: 0.875rem;
  font-weight: 600;
  background: linear-gradient(135deg, #0071e3 0%, #0077ed 100%);
  color: #ffffff;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 6px rgba(0, 113, 227, 0.18);
  display: flex;
  align-items: center;
  gap: 0.3rem;
  white-space: nowrap;
}

.search-top__button:hover:not(:disabled) {
  background: linear-gradient(135deg, #0077ed 0%, #0081f7 100%);
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(0, 113, 227, 0.28);
}

.search-top__button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}

.search-icon {
  font-size: 0.9375rem;
}

.search-top__chips {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  justify-content: center;
}

.search-chip {
  padding: 0.4375rem 0.875rem;
  border-radius: 100px;
  border: 1.5px solid rgba(0, 0, 0, 0.1);
  background: #f5f5f7;
  color: #1d1d1f;
  font-size: 0.8125rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-chip:hover {
  background: #e8e8ed;
  border-color: rgba(0, 0, 0, 0.15);
}

.search-chip.active {
  background: #0071e3;
  border-color: #0071e3;
  color: #ffffff;
  box-shadow: 0 2px 6px rgba(0, 113, 227, 0.18);
}

.search-top__advanced {
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  padding-top: 0.375rem;
  margin-top: 0.125rem;
}

.search-top__advanced summary {
  cursor: pointer;
  color: #0071e3;
  font-weight: 600;
  font-size: 0.75rem;
  padding: 0.25rem 0;
  list-style: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.3rem;
  user-select: none;
}

.search-top__advanced summary::-webkit-details-marker {
  display: none;
}

.search-top__advanced summary::after {
  content: '▼';
  font-size: 0.625rem;
  transition: transform 0.3s ease;
}

.search-top__advanced[open] summary::after {
  transform: rotate(180deg);
}

.search-advanced__content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 0.625rem;
  padding: 0.5rem 0 0.25rem;
}

.search-field {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}

.search-field span {
  font-size: 0.75rem;
  font-weight: 600;
  color: #1d1d1f;
}

.search-field input,
.search-field select {
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  border: 1.5px solid rgba(0, 0, 0, 0.1);
  background: #f5f5f7;
  font-size: 0.8125rem;
  color: #1d1d1f;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-field input:focus,
.search-field select:focus {
  outline: none;
  border-color: #0071e3;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(0, 113, 227, 0.08);
}

.search-top__error {
  margin: 0;
  color: #ff3b30;
  font-size: 0.8125rem;
  font-weight: 500;
  text-align: center;
}

.hero {
  position: relative;
  display: grid;
  gap: 2rem;
  grid-template-columns: minmax(0, 1.2fr) minmax(0, 0.8fr);
  background: linear-gradient(135deg, #0071e3 0%, #5e5ce6 100%);
  border-radius: 28px;
  padding: 3rem 2.5rem;
  overflow: hidden;
  color: white;
  box-shadow: 0 20px 60px rgba(0, 113, 227, 0.35), 0 8px 16px rgba(0, 0, 0, 0.15);
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  margin: 1.5rem auto 0;
  max-width: 1600px;
}

.hero:hover {
  transform: translateY(-4px);
  box-shadow: 0 28px 80px rgba(0, 113, 227, 0.4), 0 12px 24px rgba(0, 0, 0, 0.2);
}

.hero--loading {
  background: linear-gradient(135deg, #86868b 0%, #6e6e73 100%);
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.85;
  }
}

.hero--fallback {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #ff3b30 0%, #ff9500 100%);
  box-shadow: 0 20px 60px rgba(255, 59, 48, 0.3), 0 8px 16px rgba(0, 0, 0, 0.15);
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
  background: radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.15), transparent 60%);
  pointer-events: none;
}

.hero::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
}

.hero__content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.hero__badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(255, 255, 255, 0.18);
  color: rgba(255, 255, 255, 0.95);
  border-radius: 100px;
  padding: 0.5rem 1.25rem;
  font-size: 0.8125rem;
  font-weight: 600;
  letter-spacing: 0.02em;
  text-transform: uppercase;
  width: fit-content;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.25);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.hero h2 {
  font-size: 2.75rem;
  font-weight: 700;
  margin: 0;
  letter-spacing: -0.025em;
  line-height: 1.1;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.hero__subtitle {
  font-size: 1.125rem;
  font-weight: 500;
  margin: 0;
  color: rgba(255, 255, 255, 0.95);
  letter-spacing: -0.01em;
}

.hero__description {
  margin: 0;
  color: rgba(255, 255, 255, 0.88);
  line-height: 1.6;
  font-size: 0.9375rem;
}

.hero__quick-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.hero__quick-link {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  padding: 1rem 1.125rem;
  text-decoration: none;
  color: inherit;
  border: 1px solid rgba(255, 255, 255, 0.18);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.hero__quick-link:hover {
  transform: translateY(-3px);
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.hero__quick-link span {
  font-weight: 600;
  font-size: 0.9375rem;
}

.hero__quick-link small {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.8125rem;
  line-height: 1.4;
}

.hero__glass {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 24px;
  padding: 1.75rem 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.25);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.hero__stat {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1.25rem;
}

.hero__stat > div {
  text-align: center;
  padding: 0.75rem 0;
}

.hero__stat .value {
  font-size: 2rem;
  font-weight: 700;
  margin: 0 0 0.375rem 0;
  letter-spacing: -0.02em;
  background: linear-gradient(135deg, #ffffff, #e0e0ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero__stat .label {
  margin: 0;
  color: rgba(255, 255, 255, 0.85);
  letter-spacing: 0.02em;
  text-transform: uppercase;
  font-size: 0.6875rem;
  font-weight: 600;
}

.hero__focus {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 0.625rem;
}

.hero__focus li {
  position: relative;
  padding-left: 1.75rem;
  color: rgba(255, 255, 255, 0.92);
  font-size: 0.875rem;
  line-height: 1.5;
}

.hero__focus li::before {
  content: '';
  position: absolute;
  top: 0.5rem;
  left: 0;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffcc00, #ff6b35);
  box-shadow: 0 2px 8px rgba(255, 204, 0, 0.4);
}

.modules {
  display: grid;
  gap: 1.25rem;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  margin: 0 auto;
  max-width: 1600px;
  padding: 0 2rem;
}

.module {
  background: #ffffff;
  border-radius: 24px;
  padding: 1.75rem 1.5rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05), 0 1px 3px rgba(0, 0, 0, 0.03);
  display: flex;
  flex-direction: column;
  gap: 1rem;
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.module::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--accent, #0071e3), color-mix(in srgb, var(--accent, #0071e3) 60%, white));
  opacity: 0;
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.module:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08), 0 4px 12px rgba(0, 0, 0, 0.05);
  border-color: rgba(0, 113, 227, 0.15);
}

.module:hover::before {
  opacity: 1;
}

.module__tag {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.4rem 1rem;
  border-radius: 100px;
  background: color-mix(in srgb, var(--accent) 10%, white);
  color: color-mix(in srgb, var(--accent) 85%, black);
  font-size: 0.6875rem;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  width: fit-content;
  border: 1px solid color-mix(in srgb, var(--accent) 20%, transparent);
}

.module h3 {
  margin: 0;
  font-size: 1.375rem;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.015em;
}

.module p {
  margin: 0;
  color: #6e6e73;
  font-size: 0.9375rem;
  line-height: 1.5;
}

.module ul {
  margin: 0;
  padding-left: 1.5rem;
  color: #1d1d1f;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  font-size: 0.875rem;
  line-height: 1.5;
}

.module ul li {
  padding-left: 0.25rem;
}

.results {
  background: #ffffff;
  border-radius: 24px;
  padding: 2rem 1.75rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05), 0 1px 3px rgba(0, 0, 0, 0.03);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  border: 1px solid rgba(0, 0, 0, 0.04);
  margin: 0 auto;
  max-width: 1600px;
}

.results header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1.5rem;
}

.results header h3 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.02em;
}

.results header p {
  margin: 0;
  color: #6e6e73;
  font-size: 0.9375rem;
}

.results__breakdown {
  display: flex;
  flex-wrap: wrap;
  gap: 0.625rem;
  color: #0071e3;
  font-weight: 600;
  font-size: 0.875rem;
}

.results__grid {
  display: grid;
  gap: 1.25rem;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.result-card {
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 18px;
  padding: 1.5rem 1.375rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  background: #ffffff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.result-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.08);
  border-color: rgba(0, 113, 227, 0.2);
}

.result-card__type {
  align-self: flex-start;
  padding: 0.375rem 0.875rem;
  border-radius: 100px;
  background: rgba(0, 113, 227, 0.1);
  color: #0071e3;
  font-weight: 700;
  font-size: 0.6875rem;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.result-card h4 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.015em;
}

.result-card__subtitle {
  margin: 0;
  color: #0071e3;
  font-weight: 600;
  font-size: 0.9375rem;
}

.result-card__description {
  margin: 0;
  color: #6e6e73;
  line-height: 1.5;
  max-height: 4.5rem;
  overflow: hidden;
  font-size: 0.875rem;
}

.result-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  color: #86868b;
  font-size: 0.8125rem;
}

.result-card__link {
  align-self: flex-start;
  color: #0071e3;
  font-weight: 600;
  text-decoration: none;
  font-size: 0.9375rem;
  transition: color 0.2s ease;
}

.result-card__link:hover {
  color: #0077ed;
}

.results__empty {
  text-align: center;
  color: #86868b;
  padding: 2rem 0;
  font-size: 0.9375rem;
}

.results__suggestions {
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  padding-top: 1.25rem;
  color: #6e6e73;
}

.results__suggestions h4 {
  margin: 0 0 0.75rem 0;
  font-weight: 700;
  font-size: 1rem;
  color: #1d1d1f;
}

.results__suggestions ul {
  margin: 0;
  padding-left: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  font-size: 0.875rem;
}

.insights {
  display: grid;
  gap: 1.25rem;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  margin: 0 auto;
  max-width: 1600px;
  padding: 0 2rem;
}

.panel {
  background: #ffffff;
  border-radius: 24px;
  padding: 1.75rem 1.5rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05), 0 1px 3px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.panel:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08), 0 2px 6px rgba(0, 0, 0, 0.04);
  border-color: rgba(0, 113, 227, 0.1);
}

.panel header h3 {
  margin: 0 0 0.375rem 0;
  font-size: 1.375rem;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.015em;
}

.panel header p {
  margin: 0;
  color: #6e6e73;
  font-size: 0.875rem;
  line-height: 1.5;
}

.panel ul {
  margin: 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.panel li {
  display: flex;
  justify-content: space-between;
  gap: 1.5rem;
  padding: 1rem;
  border-radius: 12px;
  background: #f5f5f7;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.panel li:hover {
  background: #e8e8ed;
  border-color: rgba(0, 113, 227, 0.15);
  transform: translateX(4px);
}

.panel li:last-child {
  margin-bottom: 0;
}

.panel li > div {
  flex: 1;
}

.panel .tag {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.3125rem 0.75rem;
  border-radius: 100px;
  background: rgba(0, 113, 227, 0.1);
  color: #0071e3;
  font-size: 0.6875rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

.panel li h4 {
  margin: 0 0 0.375rem 0;
  font-size: 0.9375rem;
  font-weight: 700;
  color: #1d1d1f;
}

.panel li p {
  margin: 0;
  color: #6e6e73;
  font-size: 0.875rem;
  line-height: 1.5;
}

.panel li small {
  display: block;
  margin-top: 0.375rem;
  color: #86868b;
  font-size: 0.8125rem;
}

.panel time {
  color: #86868b;
  font-size: 0.75rem;
  min-width: 100px;
  text-align: right;
  white-space: nowrap;
}

.panel .empty {
  text-align: center;
  color: #86868b;
  padding: 2rem 0;
  font-size: 0.9375rem;
}

/* ===== 公共资料中心（重新设计）===== */
.resources {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 24px;
  padding: 2.5rem 2rem;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.05);
  margin: 0 auto 2rem;
  max-width: 1600px;
}

.resources__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 2rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 1.25rem;
}

.header-icon {
  font-size: 3rem;
  line-height: 1;
}

.header-text h2 {
  margin: 0 0 0.5rem 0;
  font-size: 1.75rem;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.02em;
}

.header-text p {
  margin: 0;
  color: #6e6e73;
  font-size: 0.9375rem;
}

/* 上传区域 */
.upload-section {
  flex: 1;
  min-width: 300px;
}

.upload-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 1.5rem;
  border: 2px dashed rgba(0, 113, 227, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.upload-card:hover {
  border-color: rgba(0, 113, 227, 0.4);
  background: #f8fafc;
}

.upload-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.upload-icon {
  font-size: 1.5rem;
}

.upload-header h3 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 700;
  color: #1d1d1f;
}

.upload-form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.file-select {
  cursor: pointer;
}

.file-select-button {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.875rem 1.125rem;
  background: #f5f5f7;
  border: 1.5px solid rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.file-select-button:hover {
  background: #e8e8ed;
  border-color: rgba(0, 0, 0, 0.12);
}

.file-icon {
  font-size: 1.25rem;
}

.file-text {
  flex: 1;
  font-size: 0.9375rem;
  font-weight: 600;
  color: #1d1d1f;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.upload-description {
  padding: 0.875rem 1.125rem;
  border-radius: 12px;
  border: 1.5px solid rgba(0, 0, 0, 0.1);
  background: #f5f5f7;
  font-size: 0.9375rem;
  color: #1d1d1f;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.upload-description:focus {
  outline: none;
  border-color: #0071e3;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(0, 113, 227, 0.1);
}

.upload-button {
  padding: 0.875rem 1.5rem;
  background: linear-gradient(135deg, #0071e3 0%, #0077ed 100%);
  color: #ffffff;
  border: none;
  border-radius: 12px;
  font-size: 0.9375rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 16px rgba(0, 113, 227, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.upload-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #0077ed 0%, #0081f7 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 113, 227, 0.35);
}

.upload-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}

.button-icon {
  font-size: 1.125rem;
}

.upload-message {
  margin: 0.75rem 0 0 0;
  color: #0071e3;
  font-size: 0.875rem;
  font-weight: 500;
  text-align: center;
}

/* 资源列表（横向滚动） */
.resources-scroll {
  display: flex;
  gap: 1.25rem;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 0.5rem 0.25rem 1rem;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
}

/* 自定义滚动条 */
.resources-scroll::-webkit-scrollbar {
  height: 8px;
}

.resources-scroll::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.04);
  border-radius: 100px;
}

.resources-scroll::-webkit-scrollbar-thumb {
  background: rgba(0, 113, 227, 0.3);
  border-radius: 100px;
  transition: background 0.2s ease;
}

.resources-scroll::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 113, 227, 0.5);
}

.resource-card {
  flex-shrink: 0;
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  padding: 1.75rem 1.5rem;
  background: #ffffff;
  border-radius: 18px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.resource-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
  border-color: rgba(0, 113, 227, 0.2);
}

.resource-card-icon {
  width: 4.5rem;
  height: 4.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f5f7 0%, #e8e8ed 100%);
  border-radius: 16px;
  font-size: 2.5rem;
  align-self: center;
}

.resource-card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.resource-card-title {
  margin: 0;
  font-size: 1.0625rem;
  font-weight: 700;
  color: #1d1d1f;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.resource-card-desc {
  margin: 0;
  font-size: 0.875rem;
  color: #6e6e73;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.resource-card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.625rem;
  margin-top: auto;
}

.meta-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.375rem 0.75rem;
  background: #f5f5f7;
  border-radius: 100px;
  font-size: 0.75rem;
  color: #6e6e73;
  font-weight: 600;
}

.meta-icon {
  font-size: 0.9375rem;
}

.resource-download-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.875rem 1.5rem;
  background: linear-gradient(135deg, #0071e3 0%, #0077ed 100%);
  color: #ffffff;
  text-decoration: none;
  border-radius: 12px;
  font-size: 0.9375rem;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 16px rgba(0, 113, 227, 0.25);
}

.resource-download-btn:hover {
  background: linear-gradient(135deg, #0077ed 0%, #0081f7 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 113, 227, 0.35);
}

.download-icon {
  font-size: 1.125rem;
}

/* 空状态 */
.resources-empty {
  text-align: center;
  padding: 3rem 2rem;
  color: #86868b;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.resources-empty p {
  margin: 0 0 0.5rem 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #6e6e73;
}

.resources-empty small {
  font-size: 0.875rem;
  color: #86868b;
}

@media (max-width: 1024px) {
  /* 顶部搜索 */
  .search-top {
    padding: 0.75rem 1.5rem;
  }

  .search-advanced__content {
    grid-template-columns: 1fr;
  }

  /* Hero */
  .hero {
    grid-template-columns: 1fr;
    padding: 2.5rem 2rem;
    margin: 1.5rem 1.5rem 0;
  }

  .hero h2 {
    font-size: 2.25rem;
  }

  /* 其他区域 */
  .modules,
  .insights {
    padding: 0 1.5rem;
  }

  .resources__header {
    flex-direction: column;
  }

  .upload {
    width: 100%;
    min-width: unset;
  }
}

@media (max-width: 768px) {
  .home {
    gap: 1.5rem;
  }

  /* 顶部搜索 */
  .search-top {
    padding: 0.75rem 1rem;
  }

  .search-top__container {
    gap: 0.625rem;
  }

  .search-top__input-wrapper {
    flex-direction: column;
    gap: 0.5rem;
  }

  .search-top__button {
    width: 100%;
    justify-content: center;
    padding: 0.75rem;
  }

  .search-top__chips {
    justify-content: flex-start;
    gap: 0.5rem;
  }

  .search-chip {
    font-size: 0.8125rem;
    padding: 0.4375rem 0.875rem;
  }

  /* Hero */
  .hero {
    padding: 2rem 1.5rem;
    border-radius: 20px;
    margin: 1rem 1rem 0;
  }

  .hero h2 {
    font-size: 2rem;
  }

  .hero__subtitle {
    font-size: 1rem;
  }

  /* 其他区域 */
  .modules,
  .insights {
    padding: 0 1rem;
  }

  .modules {
    grid-template-columns: 1fr;
  }

  .results,
  .panel,
  .resources {
    border-radius: 20px;
    padding: 1.5rem 1.25rem;
  }

  .results header h3,
  .panel header h3 {
    font-size: 1.25rem;
  }

  .header-text h2 {
    font-size: 1.5rem;
  }

  .resource-card {
    width: 280px;
  }

  .results__grid {
    grid-template-columns: 1fr;
  }

  .insights {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  /* 顶部搜索 */
  .search-top {
    padding: 0.625rem 0.75rem;
  }

  .search-top__input {
    font-size: 0.875rem;
    padding: 0.625rem 1rem;
  }

  .search-top__button {
    font-size: 0.875rem;
    padding: 0.625rem;
  }

  .search-chip {
    font-size: 0.75rem;
    padding: 0.375rem 0.75rem;
  }

  .search-top__advanced summary {
    font-size: 0.75rem;
  }

  /* Hero */
  .hero {
    padding: 1.5rem 1.25rem;
  }

  .hero h2 {
    font-size: 1.75rem;
  }

  .hero__quick-links {
    grid-template-columns: 1fr;
  }

  /* 上传 */
  .upload-section {
    min-width: 100%;
  }

  .header-icon {
    font-size: 2.5rem;
  }

  .header-text h2 {
    font-size: 1.375rem;
  }

  .resource-card {
    width: 260px;
  }

  .resource-card-icon {
    width: 4rem;
    height: 4rem;
    font-size: 2.25rem;
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .header-icon {
    font-size: 2.5rem;
  }
}

/* 职位项可点击样式 */
.job-item-clickable {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.job-item-clickable:hover {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

/* 职位详情弹窗 */
.job-detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 2rem;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.job-detail-content {
  background: #ffffff;
  border-radius: 24px;
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes slideUp {
  from {
    transform: translateY(40px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 滚动条样式 */
.job-detail-content::-webkit-scrollbar {
  width: 8px;
}

.job-detail-content::-webkit-scrollbar-track {
  background: #f5f5f7;
  border-radius: 10px;
}

.job-detail-content::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #0071e3 0%, #0077ed 100%);
  border-radius: 10px;
}

.close-btn {
  position: sticky;
  top: 1.5rem;
  right: 1.5rem;
  float: right;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f5f5f7;
  border: none;
  font-size: 1.5rem;
  color: #1d1d1f;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  z-index: 10;
}

.close-btn:hover {
  background: #e8e8ed;
  transform: rotate(90deg);
}

.loading-container,
.error-container {
  padding: 4rem 2rem;
  text-align: center;
  color: #6e6e73;
}

.loading-spinner {
  font-size: 1.125rem;
  font-weight: 500;
}

.job-detail-body {
  padding: 2rem 2.5rem 2.5rem;
}

.job-detail-header {
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 2px solid #f5f5f7;
}

.job-detail-header h2 {
  margin: 0 0 1.25rem 0;
  font-size: 2rem;
  font-weight: 700;
  color: #1d1d1f;
  line-height: 1.2;
}

.job-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
}

.job-meta .meta-item {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9375rem;
  color: #6e6e73;
  font-weight: 500;
}

.job-meta .meta-item .icon {
  font-size: 1.125rem;
}

.employer-section,
.job-section {
  margin-bottom: 2rem;
}

.employer-section h3,
.job-section h3 {
  margin: 0 0 1rem 0;
  font-size: 1.25rem;
  font-weight: 700;
  color: #1d1d1f;
}

.employer-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 16px;
  padding: 1.75rem;
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.employer-name {
  font-size: 1.375rem;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 0.75rem;
}

.employer-desc {
  color: #6e6e73;
  line-height: 1.6;
  margin: 0 0 1rem 0;
  font-size: 0.9375rem;
}

.employer-contacts {
  display: flex;
  flex-direction: column;
  gap: 0.625rem;
}

.contact-item {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #6e6e73;
}

.contact-item .icon {
  font-size: 1rem;
}

.website-link {
  color: #0071e3;
  text-decoration: none;
  transition: color 0.2s;
}

.website-link:hover {
  color: #0077ed;
  text-decoration: underline;
}

.job-description {
  color: #1d1d1f;
  line-height: 1.7;
  font-size: 1rem;
  margin: 0;
  white-space: pre-wrap;
}

.requirements-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.requirements-list li {
  position: relative;
  padding-left: 1.75rem;
  color: #1d1d1f;
  line-height: 1.6;
  font-size: 0.9375rem;
}

.requirements-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  top: 0;
  width: 1.25rem;
  height: 1.25rem;
  background: linear-gradient(135deg, #0071e3 0%, #0077ed 100%);
  color: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 700;
}

.job-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2.5rem;
  padding-top: 2rem;
  border-top: 2px solid #f5f5f7;
}

.apply-btn,
.collect-btn {
  flex: 1;
  padding: 1rem 2rem;
  border-radius: 14px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.625rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.apply-btn {
  background: linear-gradient(135deg, #0071e3 0%, #0077ed 100%);
  color: #ffffff;
  box-shadow: 0 4px 16px rgba(0, 113, 227, 0.3);
}

.apply-btn:hover {
  background: linear-gradient(135deg, #0077ed 0%, #007aff 100%);
  box-shadow: 0 6px 24px rgba(0, 113, 227, 0.4);
  transform: translateY(-2px);
}

.collect-btn {
  background: #f5f5f7;
  color: #1d1d1f;
  border: 1.5px solid rgba(0, 0, 0, 0.1);
}

.collect-btn:hover {
  background: #e8e8ed;
  border-color: rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.apply-btn .icon,
.collect-btn .icon {
  font-size: 1.125rem;
}

@media (max-width: 768px) {
  .job-detail-modal {
    padding: 1rem;
  }

  .job-detail-content {
    border-radius: 20px;
    max-height: 95vh;
  }

  .job-detail-body {
    padding: 1.5rem 1.25rem 2rem;
  }

  .job-detail-header h2 {
    font-size: 1.5rem;
  }

  .job-meta {
    gap: 1rem;
  }

  .job-meta .meta-item {
    font-size: 0.875rem;
  }

  .employer-card {
    padding: 1.25rem;
  }

  .employer-name {
    font-size: 1.125rem;
  }

  .job-actions {
    flex-direction: column;
  }

  .apply-btn,
  .collect-btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .close-btn {
    top: 1rem;
    right: 1rem;
    width: 36px;
    height: 36px;
    font-size: 1.25rem;
  }

  .job-detail-header h2 {
    font-size: 1.25rem;
  }

  .job-meta .meta-item {
    font-size: 0.8125rem;
  }
}

/* 登录提示对话框 */
.login-prompt-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  animation: fadeIn 0.2s ease;
}

.login-prompt-content {
  background: #ffffff;
  border-radius: 20px;
  padding: 2.5rem 2rem;
  max-width: 420px;
  width: 90%;
  text-align: center;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  animation: scaleUp 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes scaleUp {
  from {
    transform: scale(0.9);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.prompt-icon {
  font-size: 4rem;
  margin-bottom: 1.25rem;
  line-height: 1;
}

.prompt-title {
  margin: 0 0 1rem 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #1d1d1f;
}

.prompt-message {
  margin: 0 0 2rem 0;
  font-size: 1rem;
  color: #6e6e73;
  line-height: 1.5;
}

.prompt-actions {
  display: flex;
  gap: 1rem;
}

.btn-cancel,
.btn-login {
  flex: 1;
  padding: 0.875rem 1.5rem;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-cancel {
  background: #f5f5f7;
  color: #1d1d1f;
  border: 1.5px solid rgba(0, 0, 0, 0.1);
}

.btn-cancel:hover {
  background: #e8e8ed;
  border-color: rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
}

.btn-login {
  background: linear-gradient(135deg, #0071e3 0%, #0077ed 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(0, 113, 227, 0.3);
}

.btn-login:hover {
  background: linear-gradient(135deg, #0077ed 0%, #007aff 100%);
  box-shadow: 0 6px 16px rgba(0, 113, 227, 0.4);
  transform: translateY(-1px);
}

.btn-cancel .icon,
.btn-login .icon {
  font-size: 1rem;
}

@media (max-width: 480px) {
  .login-prompt-content {
    padding: 2rem 1.5rem;
  }

  .prompt-icon {
    font-size: 3rem;
  }

  .prompt-title {
    font-size: 1.25rem;
  }

  .prompt-message {
    font-size: 0.9375rem;
  }

  .prompt-actions {
    flex-direction: column;
  }

  .btn-cancel,
  .btn-login {
    width: 100%;
  }
}
</style>
