<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  createResume,
  deleteResume,
  fetchResumeDetail,
  fetchStudentResumes,
  updateResume,
  type ResumeDetail,
  type ResumeExperienceDetail,
  type ResumeSummary,
} from '../../api/student'

type ResumeExperienceForm = {
  title: string
  organization: string
  startDate: string
  endDate: string
  description: string
}

type ResumeSkillForm = {
  skill: string
  proficiency: number
}

type TemplateOption = {
  id: 'aurora' | 'sunset' | 'sprout'
  name: string
  accent: string
  gradient: string
  paper: string
  muted: string
}

const router = useRouter()
const route = useRoute()

const studentIdInput = ref('')
const studentIdError = ref('')
const studentId = ref<number | null>(null)

const resumes = ref<ResumeSummary[]>([])
const listLoading = ref(false)
const listError = ref('')
const deletingId = ref<number | null>(null)

const resumeForm = reactive({
  id: null as number | null,
  title: '',
  summary: '',
  portfolioUrl: '',
  experiences: [] as ResumeExperienceForm[],
  skills: [] as ResumeSkillForm[],
})

const resumeFormLoading = ref(false)
const resumeSaving = ref(false)
const resumeMessage = ref('')
const resumeError = ref('')

const templateOptions: TemplateOption[] = [
  {
    id: 'aurora',
    name: '极光幻彩',
    accent: '#6A6FF5',
    gradient: 'linear-gradient(135deg, #6A6FF5 0%, #9B63F8 100%)',
    paper: '#F8F9FF',
    muted: '#E3E5FF',
  },
  {
    id: 'sunset',
    name: '暮色流光',
    accent: '#F97316',
    gradient: 'linear-gradient(135deg, #F97316 0%, #EC4899 100%)',
    paper: '#FFF7ED',
    muted: '#FFE8D1',
  },
  {
    id: 'sprout',
    name: '青藤初露',
    accent: '#10B981',
    gradient: 'linear-gradient(135deg, #22C55E 0%, #0EA5E9 100%)',
    paper: '#ECFDF5',
    muted: '#D1FAE5',
  },
]

const DEFAULT_TEMPLATE_ID: TemplateOption['id'] = 'aurora'

const templatePreference = reactive<Record<number, TemplateOption['id']>>({})
const previewTemplate = ref<TemplateOption['id']>(DEFAULT_TEMPLATE_ID)

const ensureTemplateId = (candidate?: TemplateOption['id']): TemplateOption['id'] => {
  if (!candidate) return DEFAULT_TEMPLATE_ID
  return templateOptions.some((item) => item.id === candidate) ? candidate : DEFAULT_TEMPLATE_ID
}

const currentTemplate = computed<TemplateOption>(() => {
  const matched = templateOptions.find((item) => item.id === previewTemplate.value)
  return matched ?? templateOptions[0]!
})

const previewExperiences = computed(() =>
  resumeForm.experiences
    .filter((item) => item.title.trim().length > 0)
    .map((item) => ({
      ...item,
      period: formatPeriod(item.startDate, item.endDate),
      description: item.description.trim() || '尚未填写经历亮点',
    }))
)

const previewSkills = computed(() =>
  resumeForm.skills.filter((item) => item.skill.trim().length > 0).map((item) => ({
    ...item,
    proficiency: Number.isFinite(item.proficiency) ? Math.min(Math.max(item.proficiency, 0), 100) : 0,
  }))
)

const previewSummary = computed(() => resumeForm.summary.trim() || '请在右侧填写简历摘要，突出核心能力与求职意向。')
const previewTitle = computed(() => resumeForm.title.trim() || '未命名简历')
const previewPortfolio = computed(() => resumeForm.portfolioUrl.trim())

const formatDate = (value: string | null | undefined) => {
  if (!value) return '未记录'
  try {
    return new Intl.DateTimeFormat('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).format(new Date(value))
  } catch (error) {
    return value
  }
}

const formatPeriod = (start: string, end: string) => {
  if (!start && !end) return '时间待定'
  const normalize = (value: string) => (value ? value.replace(/-/g, '.') : '')
  if (start && end) return `${normalize(start)} - ${normalize(end)}`
  if (start) return `${normalize(start)} 起`
  return `截至 ${normalize(end)}`
}

const parseStudentId = (value: string) => {
  if (!value) return null
  const numeric = Number.parseInt(value.trim(), 10)
  return Number.isFinite(numeric) && numeric > 0 ? numeric : null
}

const createEmptyExperience = (): ResumeExperienceForm => ({
  title: '',
  organization: '',
  startDate: '',
  endDate: '',
  description: '',
})

const createEmptySkill = (): ResumeSkillForm => ({
  skill: '',
  proficiency: 60,
})

const replaceArray = <T>(target: T[], source: T[]) => {
  target.splice(0, target.length, ...source)
}

const fillFormFromDetail = (detail: ResumeDetail) => {
  resumeForm.id = detail.resume?.id ?? null
  resumeForm.title = detail.resume?.title ?? ''
  resumeForm.summary = detail.resume?.summary ?? ''
  resumeForm.portfolioUrl = detail.resume?.portfolioUrl ?? ''

  const experiences: ResumeExperienceForm[] = (detail.experiences ?? []).map((item: ResumeExperienceDetail) => ({
    title: item.title ?? '',
    organization: item.organization ?? '',
    startDate: item.startDate ?? '',
    endDate: item.endDate ?? '',
    description: item.description ?? '',
  }))
  const skills: ResumeSkillForm[] = (detail.skills ?? []).map((item) => ({
    skill: item.skill ?? '',
    proficiency: typeof item.proficiency === 'number' ? item.proficiency : 60,
  }))
  replaceArray(resumeForm.experiences, experiences.length ? experiences : [createEmptyExperience()])
  replaceArray(resumeForm.skills, skills.length ? skills : [createEmptySkill()])

  previewTemplate.value = ensureTemplateId(resumeForm.id ? templatePreference[resumeForm.id] : undefined)
}

const resetForm = () => {
  resumeForm.id = null
  resumeForm.title = ''
  resumeForm.summary = ''
  resumeForm.portfolioUrl = ''
  replaceArray(resumeForm.experiences, [createEmptyExperience()])
  replaceArray(resumeForm.skills, [createEmptySkill()])
  resumeMessage.value = ''
  resumeError.value = ''
  previewTemplate.value = DEFAULT_TEMPLATE_ID
}

const loadResumes = async (id: number, preferredId?: number | null) => {
  listLoading.value = true
  listError.value = ''
  try {
    const data = await fetchStudentResumes(id)
    resumes.value = data
    if (data.length === 0) {
      resetForm()
      return
    }
    const firstResume = data[0]
    const fallbackId = firstResume ? firstResume.id : null
    const targetId = preferredId ?? resumeForm.id ?? fallbackId
    if (targetId) {
      await editResume(targetId, { silent: true })
    }
  } catch (error) {
    listError.value = error instanceof Error ? error.message : '加载简历列表失败'
    resumes.value = []
  } finally {
    listLoading.value = false
  }
}

const handleSelectStudent = async () => {
  const parsed = parseStudentId(studentIdInput.value)
  if (!parsed) {
    studentIdError.value = '请输入有效的学生 ID'
    return
  }
  studentIdError.value = ''
  studentId.value = parsed
  localStorage.setItem('currentStudentId', String(parsed))
  await loadResumes(parsed)
  router.replace({
    name: 'student-resume',
    query: { studentId: String(parsed) },
  })
}

const showCreateModal = ref(false)

const createModal = reactive({
  title: '',
  summary: '',
  portfolioUrl: '',
})

const openCreateModal = () => {
  if (!studentId.value) {
    resumeError.value = '请先选择学生 ID 再创建简历'
    return
  }
  createModal.title = ''
  createModal.summary = ''
  createModal.portfolioUrl = ''
  showCreateModal.value = true
}

const closeCreateModal = () => {
  showCreateModal.value = false
}

const confirmCreateResume = () => {
  if (!createModal.title.trim()) {
    alert('请填写简历标题')
    return
  }
  
  resetForm()
  resumeForm.title = createModal.title.trim()
  resumeForm.summary = createModal.summary.trim()
  resumeForm.portfolioUrl = createModal.portfolioUrl.trim()
  
  showCreateModal.value = false
  resumeMessage.value = '已创建新的简历草稿，请继续填写经历和技能'
}

const createNewResume = () => {
  openCreateModal()
}

const addExperience = () => {
  resumeForm.experiences.push(createEmptyExperience())
}

const removeExperience = (index: number) => {
  resumeForm.experiences.splice(index, 1)
  if (!resumeForm.experiences.length) {
    resumeForm.experiences.push(createEmptyExperience())
  }
}

const addSkill = () => {
  resumeForm.skills.push(createEmptySkill())
}

const removeSkill = (index: number) => {
  resumeForm.skills.splice(index, 1)
  if (!resumeForm.skills.length) {
    resumeForm.skills.push(createEmptySkill())
  }
}

const editResume = async (id: number, options: { silent?: boolean } = {}) => {
  if (!options.silent) {
    resumeFormLoading.value = true
  }
  resumeError.value = ''
  try {
    const detail = await fetchResumeDetail(id)
    fillFormFromDetail(detail)
    resumeMessage.value = options.silent ? '' : '已载入简历内容，可继续编辑'
  } catch (error) {
    resumeError.value = error instanceof Error ? error.message : '加载简历详情失败'
  } finally {
    if (!options.silent) {
      resumeFormLoading.value = false
    }
  }
}

const removeResume = async (id: number) => {
  if (!studentId.value) return
  if (!window.confirm('确定删除该简历吗？删除后将无法恢复。')) return
  deletingId.value = id
  resumeError.value = ''
  resumeMessage.value = ''
  try {
    await deleteResume(id)
    if (resumeForm.id === id) {
      resetForm()
    }
    resumeMessage.value = '简历已删除'
    await loadResumes(studentId.value)
  } catch (error) {
    resumeError.value = error instanceof Error ? error.message : '删除简历失败'
  } finally {
    deletingId.value = null
  }
}

const submitResume = async () => {
  if (!studentId.value) {
    resumeError.value = '请先选择学生 ID'
    return
  }
  if (!resumeForm.title.trim()) {
    resumeError.value = '请填写简历标题'
    return
  }
  resumeSaving.value = true
  resumeError.value = ''
  resumeMessage.value = ''

  const payload = {
    studentId: studentId.value,
    title: resumeForm.title.trim(),
    summary: resumeForm.summary.trim(),
    portfolioUrl: resumeForm.portfolioUrl.trim(),
    experiences: resumeForm.experiences
      .map((exp) => ({
        title: exp.title.trim(),
        organization: exp.organization.trim(),
        startDate: exp.startDate || null,
        endDate: exp.endDate || null,
        description: exp.description.trim(),
      }))
      .filter((exp) => exp.title.length > 0),
    skills: resumeForm.skills
      .map((skill) => ({
        skill: skill.skill.trim(),
        proficiency: Number.isFinite(skill.proficiency) ? skill.proficiency : null,
      }))
      .filter((skill) => skill.skill.length > 0),
  }

  try {
    let detail: ResumeDetail
    if (resumeForm.id) {
      detail = await updateResume(resumeForm.id, payload)
      resumeMessage.value = '简历已更新'
    } else {
      detail = await createResume(payload)
      resumeMessage.value = '已创建新的简历'
    }
    fillFormFromDetail(detail)
    templatePreference[detail.resume.id] = previewTemplate.value
    await loadResumes(studentId.value, detail.resume.id)
  } catch (error) {
    resumeError.value = error instanceof Error ? error.message : '保存简历失败，请稍后再试'
  } finally {
    resumeSaving.value = false
  }
}

watch(
  () => route.query.studentId,
  async (value) => {
    const queryId = typeof value === 'string' ? parseStudentId(value) : null
    if (queryId && queryId !== studentId.value) {
      studentIdInput.value = String(queryId)
      studentId.value = queryId
      localStorage.setItem('currentStudentId', String(queryId))
      await loadResumes(queryId)
    }
    if (!queryId && value !== undefined) {
      studentId.value = null
      studentIdInput.value = ''
      resumes.value = []
      resetForm()
    }
  },
  { immediate: false }
)

watch(
  () => previewTemplate.value,
  (templateId) => {
    if (resumeForm.id) {
      templatePreference[resumeForm.id] = templateId
    }
  }
)

onMounted(async () => {
  const queryId = typeof route.query.studentId === 'string' ? parseStudentId(route.query.studentId) : null
  
  // 尝试从登录信息中获取学生ID
  let userStudentId: number | null = null
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    try {
      const userInfo = JSON.parse(userInfoStr)
      if (userInfo && userInfo.role === 'STUDENT' && userInfo.id) {
        userStudentId = userInfo.id
        localStorage.setItem('currentStudentId', String(userInfo.id))
      }
    } catch (e) {
      console.error('Failed to parse userInfo:', e)
    }
  }
  
  const cachedId = parseStudentId(localStorage.getItem('currentStudentId') || '')
  const initialId = queryId ?? userStudentId ?? cachedId
  if (initialId) {
    studentId.value = initialId
    studentIdInput.value = String(initialId)
    await loadResumes(initialId)
  } else {
    resetForm()
  }
})
</script>

<template>
  <div class="resume-workshop">
    <header class="page-header">
      <div class="page-header__intro">
        <h1>简历创作工坊</h1>
        <p>通过模块化的编辑体验和实时预览，打造属于你的个性化求职名片。</p>
      </div>
      <div class="student-selector">
        <label for="student-id-input">学生 ID</label>
        <div class="student-selector__control">
          <input
            id="student-id-input"
            v-model="studentIdInput"
            type="number"
            min="1"
            placeholder="输入学生 ID 载入数据"
            @keyup.enter="handleSelectStudent"
          />
          <button type="button" @click="handleSelectStudent">载入简历</button>
        </div>
        <p v-if="studentIdError" class="feedback feedback--error">{{ studentIdError }}</p>
      </div>
    </header>

    <div class="workspace">
      <aside class="resume-sidebar">
        <div class="sidebar__header">
          <h2>我的简历</h2>
          <button type="button" class="ghost" @click="createNewResume">+ 新建</button>
        </div>
        <p class="sidebar__hint">可维护多份简历，用于不同求职场景。</p>

        <div v-if="listLoading" class="sidebar__state">正在载入简历...</div>
        <div v-else-if="listError" class="sidebar__state sidebar__state--error">{{ listError }}</div>
        <div v-else-if="!resumes.length" class="sidebar__state sidebar__state--empty">
          <strong>暂无简历</strong>
          <span>点击上方按钮创建首份简历</span>
        </div>
        <ul v-else class="resume-list">
          <li
            v-for="resume in resumes"
            :key="resume.id"
            :class="['resume-item', { 'resume-item--active': resumeForm.id === resume.id }]"
          >
            <button type="button" class="resume-item__body" @click="editResume(resume.id)">
              <div class="resume-item__title">
                <span>{{ resume.title }}</span>
                <time>{{ formatDate(resume.updatedAt) }}</time>
              </div>
              <p class="resume-item__summary">{{ resume.summary || '暂无简介，点击编辑补充内容。' }}</p>
              <div class="resume-item__stats">
                <span>经历 {{ resume.experienceCount }}</span>
                <span>技能 {{ resume.skillCount }}</span>
              </div>
            </button>
            <div class="resume-item__actions">
              <button type="button" @click="editResume(resume.id)">编辑</button>
              <button
                type="button"
                class="danger"
                :disabled="deletingId === resume.id"
                @click="removeResume(resume.id)"
              >
                {{ deletingId === resume.id ? '删除中...' : '删除' }}
              </button>
            </div>
          </li>
        </ul>
      </aside>

      <main class="resume-editor">
        <section class="editor-card">
          <header class="editor-card__header">
            <div class="editor-card__header-left">
              <h2>{{ resumeForm.id ? '编辑简历' : '创建简历' }}</h2>
              <span v-if="resumeForm.id" class="editor-card__tag">#{{ resumeForm.id }}</span>
            </div>
            <button 
              type="button" 
              class="btn-primary-action" 
              :disabled="resumeSaving"
              @click="submitResume"
            >
              {{ resumeSaving ? '保存中...' : resumeForm.id ? '保存修改' : '创建简历' }}
            </button>
          </header>

          <div v-if="resumeFormLoading" class="editor-card__loading">正在载入简历详情...</div>
          <form v-else class="editor-form" @submit.prevent="submitResume">
            <div class="editor-form__grid">
              <label>
                简历标题
                <input v-model="resumeForm.title" type="text" placeholder="例如：2024 校招通用简历" />
              </label>
              <label>
                作品集链接
                <input v-model="resumeForm.portfolioUrl" type="url" placeholder="可选，填写个人网站或 GitHub" />
              </label>
            </div>

            <label class="editor-form__full">
              摘要亮点
              <textarea
                v-model="resumeForm.summary"
                rows="3"
                placeholder="概述核心技能、经验与求职目标"
              />
            </label>

            <section class="form-section">
              <header class="form-section__header">
                <h3>经历模块</h3>
                <button type="button" class="link" @click="addExperience">+ 添加经历</button>
              </header>
              <p class="form-section__hint">按时间顺序填写项目、实习或校园经历，突出贡献与成果。</p>
              <div v-for="(exp, index) in resumeForm.experiences" :key="index" class="experience-block">
                <div class="editor-form__grid">
                  <label>
                    标题
                    <input v-model="exp.title" type="text" placeholder="例如：前端工程师实习生" />
                  </label>
                  <label>
                    组织
                    <input v-model="exp.organization" type="text" placeholder="所在企业/组织" />
                  </label>
                  <label>
                    开始日期
                    <input v-model="exp.startDate" type="date" />
                  </label>
                  <label>
                    结束日期
                    <input v-model="exp.endDate" type="date" />
                  </label>
                </div>
                <label class="editor-form__full">
                  详细描述
                  <textarea v-model="exp.description" rows="2" placeholder="描述职责、技术栈与成果" />
                </label>
                <div class="experience-block__actions">
                  <button type="button" class="ghost" @click="removeExperience(index)">移除</button>
                </div>
              </div>
            </section>

            <section class="form-section">
              <header class="form-section__header">
                <h3>技能雷达</h3>
                <button type="button" class="link" @click="addSkill">+ 添加技能</button>
              </header>
              <p class="form-section__hint">通过拖动滑块设置熟练度，构建个性化技能标签。</p>
              <div v-for="(skill, index) in resumeForm.skills" :key="index" class="skill-meter">
                <input v-model="skill.skill" type="text" placeholder="技能名称，例如：Vue / Java / 数据分析" />
                <div class="skill-meter__control">
                  <input v-model.number="skill.proficiency" type="range" min="0" max="100" step="5" />
                  <span>{{ skill.proficiency }}%</span>
                </div>
                <button type="button" class="ghost" @click="removeSkill(index)">删除</button>
              </div>
            </section>

            <div v-if="resumeMessage || resumeError" class="editor-form__feedback">
              <span v-if="resumeMessage" class="feedback feedback--success">{{ resumeMessage }}</span>
              <span v-if="resumeError" class="feedback feedback--error">{{ resumeError }}</span>
            </div>
          </form>
        </section>

        <section class="preview-card">
          <header class="preview-card__header">
            <h2>实时预览</h2>
            <div class="template-switcher">
              <button
                v-for="option in templateOptions"
                :key="option.id"
                type="button"
                :class="['template-switcher__btn', { 'template-switcher__btn--active': option.id === previewTemplate }]"
                @click="previewTemplate = option.id"
              >
                {{ option.name }}
              </button>
            </div>
          </header>
          <div
            class="resume-preview"
            :style="{
              '--resume-accent': currentTemplate.accent,
              '--resume-gradient': currentTemplate.gradient,
              '--resume-paper': currentTemplate.paper,
              '--resume-muted': currentTemplate.muted,
            }"
          >
            <div class="resume-preview__banner" />
            <div class="resume-preview__body">
              <div class="resume-preview__header">
                <div class="resume-preview__avatar">{{ previewTitle.charAt(0) }}</div>
                <div>
                  <h3>{{ previewTitle }}</h3>
                  <p>{{ previewSummary }}</p>
                  <a v-if="previewPortfolio" :href="previewPortfolio" target="_blank" rel="noopener">作品集</a>
                </div>
              </div>

              <section class="resume-preview__section">
                <h4>经历精选</h4>
                <ul v-if="previewExperiences.length" class="timeline">
                  <li v-for="(exp, index) in previewExperiences" :key="index">
                    <div class="timeline__period">{{ exp.period }}</div>
                    <div class="timeline__content">
                      <strong>{{ exp.title }}</strong>
                      <span>{{ exp.organization || '组织待填写' }}</span>
                      <p>{{ exp.description }}</p>
                    </div>
                  </li>
                </ul>
                <p v-else class="resume-preview__placeholder">暂无经历，请在左侧补充具体内容。</p>
              </section>

              <section class="resume-preview__section">
                <h4>技能标签</h4>
                <div v-if="previewSkills.length" class="skill-tags">
                  <div v-for="(skill, index) in previewSkills" :key="index" class="skill-tags__item">
                    <span>{{ skill.skill }}</span>
                    <div class="skill-tags__meter">
                      <div class="skill-tags__fill" :style="{ width: `${skill.proficiency}%` }" />
                    </div>
                    <span class="skill-tags__value">{{ skill.proficiency }}%</span>
                  </div>
                </div>
                <p v-else class="resume-preview__placeholder">添加技能后，这里将以数据化方式展示优势。</p>
              </section>
            </div>
          </div>
        </section>
      </main>
    </div>

    <!-- 创建简历模态对话框 -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="closeCreateModal">
      <div class="modal-dialog">
        <header class="modal-header">
          <h2>创建新简历</h2>
          <button type="button" class="modal-close" @click="closeCreateModal">✕</button>
        </header>
        
        <div class="modal-body">
          <p class="modal-intro">请填写简历的基本信息，创建后可继续编辑经历和技能。</p>
          
          <form class="modal-form" @submit.prevent="confirmCreateResume">
            <label class="modal-field modal-field--required">
              <span class="modal-label">简历标题</span>
              <input 
                v-model="createModal.title" 
                type="text" 
                placeholder="例如：2024 校招通用简历"
                class="modal-input"
                autofocus
                required
              />
              <span class="modal-hint">为这份简历起一个便于识别的标题</span>
            </label>

            <label class="modal-field">
              <span class="modal-label">个人概述</span>
              <textarea 
                v-model="createModal.summary" 
                rows="4"
                placeholder="概述核心技能、经验与求职目标，例如：&#10;• 3年前端开发经验，熟练掌握 Vue/React&#10;• 具备良好的团队协作和沟通能力&#10;• 期望从事互联网行业产品研发工作"
                class="modal-textarea"
              />
              <span class="modal-hint">突出你的核心竞争力和求职意向</span>
            </label>

            <label class="modal-field">
              <span class="modal-label">作品集链接</span>
              <input 
                v-model="createModal.portfolioUrl" 
                type="url" 
                placeholder="可选，填写个人网站、GitHub 或在线作品集"
                class="modal-input"
              />
              <span class="modal-hint">展示你的项目成果和技术实力</span>
            </label>
          </form>
        </div>

        <footer class="modal-footer">
          <button type="button" class="modal-btn modal-btn--secondary" @click="closeCreateModal">
            取消
          </button>
          <button type="button" class="modal-btn modal-btn--primary" @click="confirmCreateResume">
            确认创建
          </button>
        </footer>
      </div>
    </div>
  </div>
</template>

<style scoped>
:global(body) {
  background: #f5f7fb;
}

.resume-workshop {
  max-width: 1280px;
  margin: 0 auto;
  padding: 2.5rem 2rem 4rem;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 2rem;
}

.page-header__intro h1 {
  font-size: 2.25rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.page-header__intro p {
  color: #64748b;
  max-width: 520px;
}

.student-selector {
  min-width: 260px;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.student-selector label {
  font-weight: 600;
  color: #334155;
}

.student-selector__control {
  display: flex;
  gap: 0.75rem;
}

.student-selector input {
  flex: 1;
  padding: 0.65rem 0.75rem;
  border: 1.5px solid #cbd5f5;
  border-radius: 10px;
  font-size: 0.95rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.student-selector input:focus {
  outline: none;
  border-color: #6a6ff5;
  box-shadow: 0 0 0 3px rgba(106, 111, 245, 0.15);
}

.student-selector button {
  padding: 0.65rem 1.25rem;
  border-radius: 10px;
  border: none;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.student-selector button:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(99, 102, 241, 0.25);
}

.feedback {
  font-size: 0.85rem;
}

.feedback--error {
  color: #dc2626;
}

.feedback--success {
  color: #16a34a;
}

.workspace {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 2rem;
}

.resume-sidebar {
  background: #ffffff;
  border-radius: 24px;
  padding: 1.75rem;
  box-shadow: 0 20px 40px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.sidebar__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar__header h2 {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1e293b;
}

.sidebar__header .ghost {
  border: none;
  background: #f1f5f9;
  color: #475569;
  padding: 0.4rem 0.9rem;
  border-radius: 999px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease;
}

.sidebar__header .ghost:hover {
  background: #e2e8f0;
}

.sidebar__hint {
  font-size: 0.9rem;
  color: #64748b;
}

.sidebar__state {
  background: #f8fafc;
  border-radius: 16px;
  padding: 1.5rem 1.25rem;
  text-align: center;
  color: #475569;
  display: grid;
  gap: 0.4rem;
}

.sidebar__state--error {
  color: #dc2626;
  background: #fee2e2;
}

.sidebar__state--empty span {
  color: #94a3b8;
  font-size: 0.9rem;
}

.resume-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 0;
  margin: 0;
}

.resume-item {
  border: 1.5px solid transparent;
  border-radius: 18px;
  background: #f8fafc;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
  display: flex;
  flex-direction: column;
}

.resume-item--active {
  border-color: #6366f1;
  box-shadow: 0 12px 28px rgba(99, 102, 241, 0.18);
  transform: translateY(-2px);
}

.resume-item__body {
  border: none;
  background: transparent;
  text-align: left;
  padding: 1.2rem 1.2rem 0.5rem;
  display: grid;
  gap: 0.75rem;
  cursor: pointer;
}

.resume-item__title {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 0.75rem;
  font-weight: 600;
  color: #1e293b;
}

.resume-item__title time {
  font-size: 0.8rem;
  color: #94a3b8;
}

.resume-item__summary {
  font-size: 0.9rem;
  color: #64748b;
  min-height: 2.5rem;
}

.resume-item__stats {
  display: flex;
  gap: 0.75rem;
  font-size: 0.8rem;
  color: #6366f1;
  font-weight: 600;
}

.resume-item__actions {
  display: flex;
  justify-content: space-between;
  padding: 0 1.2rem 1rem;
  gap: 0.75rem;
}

.resume-item__actions button {
  flex: 1;
  border: none;
  border-radius: 10px;
  padding: 0.55rem 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease;
}

.resume-item__actions button:first-child {
  background: #e0e7ff;
  color: #4338ca;
}

.resume-item__actions button:first-child:hover {
  background: #c7d2fe;
}

.resume-item__actions .danger {
  background: #fee2e2;
  color: #b91c1c;
}

.resume-item__actions .danger:hover {
  background: #fecaca;
}

.resume-editor {
  display: grid;
  grid-template-columns: 1.15fr 1fr;
  gap: 2rem;
}

.editor-card,
.preview-card {
  background: #ffffff;
  border-radius: 28px;
  padding: 2rem;
  box-shadow: 0 25px 50px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.editor-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.editor-card__header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.editor-card__header h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
}

.editor-card__tag {
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  font-size: 0.75rem;
  background: #ede9fe;
  color: #6d28d9;
}

.btn-primary-action {
  padding: 0.7rem 1.5rem;
  border-radius: 999px;
  border: none;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  white-space: nowrap;
}

.btn-primary-action:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(99, 102, 241, 0.25);
}

.btn-primary-action:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.editor-card__loading {
  text-align: center;
  color: #475569;
}

.editor-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.editor-form__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem 1.25rem;
}

.editor-form label {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  font-weight: 600;
  color: #334155;
}

.editor-form input,
.editor-form textarea {
  border: 1.5px solid #cbd5f5;
  border-radius: 12px;
  padding: 0.65rem 0.75rem;
  font-size: 0.95rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.editor-form input:focus,
.editor-form textarea:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15);
}

.editor-form textarea {
  resize: vertical;
  min-height: 90px;
}

.editor-form__full {
  grid-column: 1 / -1;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  background: #f8fafc;
  padding: 1.25rem;
  border-radius: 18px;
}

.form-section__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-section__header h3 {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1e293b;
}

.form-section__header .link {
  border: none;
  background: transparent;
  color: #6366f1;
  font-weight: 600;
  cursor: pointer;
}

.form-section__hint {
  font-size: 0.85rem;
  color: #64748b;
}

.experience-block {
  background: #ffffff;
  border-radius: 16px;
  padding: 1.1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  box-shadow: inset 0 0 0 1px #e2e8f0;
}

.experience-block__actions {
  display: flex;
  justify-content: flex-end;
}

.experience-block__actions .ghost {
  border: none;
  background: #f1f5f9;
  padding: 0.4rem 0.9rem;
  border-radius: 8px;
  cursor: pointer;
}

.skill-meter {
  display: grid;
  gap: 0.75rem;
  padding: 1rem;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: inset 0 0 0 1px #e2e8f0;
}

.skill-meter__control {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.skill-meter__control input[type='range'] {
  flex: 1;
  accent-color: #6366f1;
}

.skill-meter__control span {
  width: 3rem;
  text-align: right;
  font-weight: 600;
  color: #4338ca;
}

.skill-meter .ghost {
  justify-self: flex-end;
  border: none;
  background: #f1f5f9;
  padding: 0.4rem 0.9rem;
  border-radius: 8px;
  cursor: pointer;
}

.editor-form__feedback {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  border-radius: 12px;
  background: #f8fafc;
}

.editor-form__actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.editor-form__actions button {
  padding: 0.75rem 1.8rem;
  border-radius: 999px;
  border: none;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.editor-form__actions button:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(99, 102, 241, 0.25);
}

.preview-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-card__header h2 {
  font-size: 1.4rem;
  font-weight: 700;
  color: #1e293b;
}

.template-switcher {
  display: flex;
  gap: 0.5rem;
}

.template-switcher__btn {
  padding: 0.4rem 0.9rem;
  border-radius: 999px;
  border: 1.5px solid #cbd5f5;
  background: transparent;
  color: #475569;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.2s ease;
}

.template-switcher__btn--active {
  border-color: #6366f1;
  background: #eef2ff;
  color: #4338ca;
}

.resume-preview {
  background: var(--resume-paper);
  border-radius: 26px;
  overflow: hidden;
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.25);
  display: flex;
  flex-direction: column;
}

.resume-preview__banner {
  height: 120px;
  background: var(--resume-gradient);
}

.resume-preview__body {
  padding: 1.8rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.resume-preview__header {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 1rem 1.5rem;
  align-items: center;
}

.resume-preview__avatar {
  width: 64px;
  height: 64px;
  border-radius: 20px;
  background: var(--resume-accent);
  color: #fff;
  display: grid;
  place-items: center;
  font-size: 1.75rem;
  font-weight: 700;
}

.resume-preview__header h3 {
  font-size: 1.4rem;
  font-weight: 700;
  color: #1e293b;
}

.resume-preview__header p {
  color: #475569;
  margin-top: 0.25rem;
}

.resume-preview__header a {
  margin-top: 0.5rem;
  display: inline-block;
  color: var(--resume-accent);
  text-decoration: none;
  font-weight: 600;
}

.resume-preview__section {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.resume-preview__section h4 {
  font-size: 1rem;
  font-weight: 700;
  color: #1e293b;
}

.resume-preview__placeholder {
  color: #94a3b8;
  font-size: 0.9rem;
}

.timeline {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.timeline li {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 1rem;
}

.timeline__period {
  color: #475569;
  font-weight: 600;
  font-size: 0.85rem;
}

.timeline__content {
  background: #fff;
  border-radius: 16px;
  padding: 1rem;
  box-shadow: 0 10px 20px rgba(15, 23, 42, 0.06);
  border-left: 4px solid var(--resume-accent);
}

.timeline__content span {
  display: block;
  color: #64748b;
  font-size: 0.85rem;
  margin: 0.25rem 0 0.5rem;
}

.timeline__content p {
  color: #475569;
  font-size: 0.9rem;
}

.skill-tags {
  display: grid;
  gap: 0.75rem;
}

.skill-tags__item {
  display: grid;
  grid-template-columns: 1.2fr 1fr auto;
  gap: 0.75rem;
  align-items: center;
  background: #fff;
  padding: 0.75rem 1rem;
  border-radius: 14px;
  box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.25);
}

.skill-tags__meter {
  width: 100%;
  height: 8px;
  border-radius: 999px;
  background: var(--resume-muted);
  overflow: hidden;
  position: relative;
}

.skill-tags__fill {
  position: absolute;
  inset: 0;
  background: var(--resume-accent);
  border-radius: 999px;
}

.skill-tags__value {
  font-weight: 700;
  color: var(--resume-accent);
}

@media (max-width: 1100px) {
  .workspace {
    grid-template-columns: 1fr;
  }

  .resume-sidebar {
    order: 2;
  }

  .resume-editor {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .resume-workshop {
    padding: 1.5rem 1.25rem 3rem;
  }

  .page-header {
    flex-direction: column;
  }

  .student-selector__control {
    flex-direction: column;
  }

  .editor-card__header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .btn-primary-action {
    width: 100%;
    text-align: center;
  }

  .editor-form__grid {
    grid-template-columns: 1fr;
  }

  .timeline li {
    grid-template-columns: 1fr;
  }

  .skill-tags__item {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }
}

/* 模态对话框样式 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(4px);
  z-index: 1000;
  display: grid;
  place-items: center;
  padding: 2rem;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-dialog {
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 25px 50px rgba(15, 23, 42, 0.25);
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  padding: 1.75rem 2rem;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

.modal-header h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.modal-close {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: none;
  background: #f1f5f9;
  color: #64748b;
  font-size: 1.25rem;
  cursor: pointer;
  display: grid;
  place-items: center;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background: #e2e8f0;
  color: #1e293b;
  transform: rotate(90deg);
}

.modal-body {
  padding: 2rem;
  overflow-y: auto;
  flex: 1;
}

.modal-intro {
  color: #64748b;
  font-size: 0.95rem;
  margin: 0 0 1.75rem;
  padding: 1rem 1.25rem;
  background: #f0f9ff;
  border-left: 4px solid #0ea5e9;
  border-radius: 12px;
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.modal-field {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.modal-field--required .modal-label::after {
  content: '*';
  color: #ef4444;
  margin-left: 4px;
}

.modal-label {
  font-weight: 600;
  color: #334155;
  font-size: 0.95rem;
}

.modal-input,
.modal-textarea {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1.5px solid #cbd5f5;
  border-radius: 12px;
  font-size: 0.95rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  font-family: inherit;
}

.modal-input:focus,
.modal-textarea:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
}

.modal-textarea {
  resize: vertical;
  min-height: 100px;
  line-height: 1.6;
}

.modal-hint {
  font-size: 0.85rem;
  color: #94a3b8;
  margin-top: -0.25rem;
}

.modal-footer {
  padding: 1.5rem 2rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  background: #f8fafc;
}

.modal-btn {
  padding: 0.7rem 1.75rem;
  border-radius: 999px;
  border: none;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.modal-btn--secondary {
  background: #e2e8f0;
  color: #475569;
}

.modal-btn--secondary:hover {
  background: #cbd5e1;
  transform: translateY(-1px);
}

.modal-btn--primary {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.25);
}

.modal-btn--primary:hover {
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.35);
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .modal-overlay {
    padding: 1rem;
  }

  .modal-dialog {
    border-radius: 20px;
  }

  .modal-header {
    padding: 1.25rem 1.5rem;
  }

  .modal-body {
    padding: 1.5rem;
  }

  .modal-footer {
    padding: 1.25rem 1.5rem;
    flex-direction: column;
  }

  .modal-btn {
    width: 100%;
  }
}
</style>
