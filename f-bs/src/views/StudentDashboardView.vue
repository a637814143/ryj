<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  fetchStudentDashboard,
  submitStudentProfileRequest,
  createEducation,
  updateEducation,
  deleteEducation,
  createExperience,
  updateExperience,
  deleteExperience,
  createAward,
  updateAward,
  deleteAward,
  saveEmploymentIntention,
  createResume,
  updateResume,
  deleteResume,
  fetchResumeDetail,
  createJobApplication,
  type EmploymentIntentionWorkType,
  type ExperienceType,
  type JobPostingWorkType,
  type JobApplicationOverview,
  type JobApplicationStatus,
  type InterviewOverview,
  type InterviewStatus,
  type RecommendedJob,
  type ResumeExperienceInput,
  type ResumeOverview,
  type ResumeSkillInput,
  type StudentAward,
  type StudentDashboardResponse,
  type StudentEducation,
  type StudentExperience,
  type StudentModuleInfo,
} from '../api/student'

const props = defineProps<{ section?: string }>()
const route = useRoute()
const router = useRouter()

const dashboard = ref<StudentDashboardResponse | null>(null)
const loading = ref(false)
const loadError = ref('')
const studentId = ref<number | null>(null)
const studentIdInput = ref('')
const studentIdError = ref('')
let requestToken = 0

// 检查当前登录用户是否是学生角色
const isStudentUser = computed(() => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    try {
      const userInfo = JSON.parse(userInfoStr)
      return userInfo && userInfo.role === 'STUDENT'
    } catch (e) {
      return false
    }
  }
  return false
})

const modules = computed<StudentModuleInfo[]>(() => dashboard.value?.modules ?? [])
const header = computed(() => dashboard.value?.header)

const educationList = ref<StudentEducation[]>([])
const experienceList = ref<StudentExperience[]>([])
const awardList = ref<StudentAward[]>([])
const resumes = ref<ResumeOverview[]>([])
const jobApplications = ref<JobApplicationOverview[]>([])
const interviewList = ref<InterviewOverview[]>([])
const recommendedJobs = ref<RecommendedJob[]>([])

const profileForm = reactive({
  gender: '',
  age: null as number | null,
  major: '',
  biography: '',
  graduationYear: null as number | null,
})
const profileSaving = ref(false)
const profileMessage = ref('')
const profileError = ref('')

const educationForm = reactive({
  id: null as number | null,
  school: '',
  major: '',
  degree: '',
  startDate: '',
  endDate: '',
  description: '',
})
const editingEducationId = ref<number | null>(null)
const educationSaving = ref(false)
const educationMessage = ref('')
const educationError = ref('')

const experienceForm = reactive({
  id: null as number | null,
  title: '',
  organization: '',
  experienceType: 'INTERNSHIP' as ExperienceType,
  startDate: '',
  endDate: '',
  description: '',
})
const editingExperienceId = ref<number | null>(null)
const experienceSaving = ref(false)
const experienceMessage = ref('')
const experienceError = ref('')

const awardForm = reactive({
  id: null as number | null,
  name: '',
  level: '',
  awardDate: '',
  description: '',
})
const editingAwardId = ref<number | null>(null)
const awardSaving = ref(false)
const awardMessage = ref('')
const awardError = ref('')

const intentionForm = reactive({
  expectedPosition: '',
  salaryRange: '',
  workType: '' as EmploymentIntentionWorkType | '',
  notes: '',
  cities: [''] as string[],
})
const intentionSaving = ref(false)
const intentionMessage = ref('')
const intentionError = ref('')

const resumeForm = reactive({
  id: null as number | null,
  title: '',
  summary: '',
  portfolioUrl: '',
  experiences: [] as ResumeExperienceInput[],
  skills: [] as ResumeSkillInput[],
})
const resumeSaving = ref(false)
const resumeMessage = ref('')
const resumeError = ref('')
const resumeFormLoading = ref(false)

const selectedResumeId = ref<number | null>(null)
const applicationCoverLetter = ref('')
const applyingJobId = ref<number | null>(null)
const applicationMessage = ref('')
const applicationError = ref('')

const experienceTypeOptions: { value: ExperienceType; label: string }[] = [
  { value: 'INTERNSHIP', label: '实习' },
  { value: 'PROJECT', label: '项目' },
  { value: 'VOLUNTEER', label: '志愿' },
  { value: 'OTHER', label: '其他' },
]

const workTypeOptions: { value: EmploymentIntentionWorkType; label: string }[] = [
  { value: 'FULL_TIME', label: '全职' },
  { value: 'PART_TIME', label: '兼职' },
  { value: 'INTERNSHIP', label: '实习' },
  { value: 'FLEXIBLE', label: '灵活' },
]

const jobStatusLabels: Record<JobApplicationStatus, string> = {
  SUBMITTED: '已提交',
  REVIEWING: '审核中',
  INTERVIEW: '面试阶段',
  OFFERED: '已录用',
  REJECTED: '已拒绝',
}

const interviewStatusLabels: Record<InterviewStatus, string> = {
  SCHEDULED: '待进行',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
}

const workTypeLabels: Record<JobPostingWorkType | EmploymentIntentionWorkType, string> = {
  FULL_TIME: '全职',
  PART_TIME: '兼职',
  INTERNSHIP: '实习',
  FLEXIBLE: '灵活',
  REMOTE: '远程',
}

const experienceTypeLabels: Record<ExperienceType, string> = {
  INTERNSHIP: '实习',
  PROJECT: '项目',
  VOLUNTEER: '志愿',
  OTHER: '其他',
}

const activeSection = ref<string | undefined>(props.section)

watch(
  () => props.section,
  (section) => {
    activeSection.value = section
    if (section) {
      nextTick(() => scrollToSection(section, false))
    }
  },
  { immediate: true }
)

const parseStudentIdValue = (value: unknown): number | null => {
  if (typeof value === 'string') {
    const trimmed = value.trim()
    if (!trimmed) return null
    const parsed = Number(trimmed)
    if (Number.isFinite(parsed) && parsed > 0) {
      return Math.floor(parsed)
    }
  }
  return null
}

const syncStudentIdFromRoute = (value: unknown) => {
  const candidate = Array.isArray(value) ? value[0] : value
  const parsed = parseStudentIdValue(candidate)
  if (parsed !== null) {
    if (studentId.value !== parsed) {
      studentId.value = parsed
    }
    studentIdInput.value = String(parsed)
    studentIdError.value = ''
  } else if (candidate !== undefined) {
    studentId.value = null
    studentIdInput.value = ''
    studentIdError.value = 'URL 中的学生 ID 无效'
  }
}

watch(
  () => route.query.studentId,
  (value) => {
    syncStudentIdFromRoute(value)
  },
  { immediate: true }
)

onMounted(() => {
  if (!studentId.value) {
    // 首先尝试从登录信息中获取学生ID
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      try {
        const userInfo = JSON.parse(userInfoStr)
        if (userInfo && userInfo.role === 'STUDENT' && userInfo.id) {
          studentId.value = userInfo.id
          studentIdInput.value = String(userInfo.id)
          localStorage.setItem('currentStudentId', String(userInfo.id))
          const query = { ...route.query } as Record<string, any>
          query.studentId = String(userInfo.id)
          const params = props.section ? { section: props.section } : {}
          router.replace({ name: 'student-dashboard', params, query }).catch(() => {})
          return
        }
      } catch (e) {
        console.error('Failed to parse userInfo:', e)
      }
    }
    
    // 如果不是学生登录，再尝试从缓存中获取（用于教师/管理员查看）
    const stored = localStorage.getItem('currentStudentId')
    const parsed = parseStudentIdValue(stored)
    if (parsed) {
      studentId.value = parsed
      studentIdInput.value = String(parsed)
      const query = { ...route.query } as Record<string, any>
      query.studentId = String(parsed)
      const params = props.section ? { section: props.section } : {}
      router.replace({ name: 'student-dashboard', params, query }).catch(() => {})
    }
  }
})

watch(
  studentId,
  (id) => {
    if (id) {
      localStorage.setItem('currentStudentId', String(id))
      fetchDashboard()
    } else {
      localStorage.removeItem('currentStudentId')
      dashboard.value = null
    }
  },
  { immediate: true }
)

const fetchDashboard = async () => {
  if (!studentId.value) {
    return
  }
  const token = ++requestToken
  loading.value = true
  loadError.value = ''
  try {
    const data = await fetchStudentDashboard(studentId.value)
    if (token !== requestToken) {
      return
    }
    dashboard.value = data
    educationList.value = data.educations ?? []
    experienceList.value = data.experiences ?? []
    awardList.value = data.awards ?? []
    resumes.value = data.resumes ?? []
    jobApplications.value = data.jobApplications ?? []
    interviewList.value = data.interviews ?? []
    recommendedJobs.value = data.recommendedJobs ?? []
    applyProfileData(data.profile)
    applyEmploymentIntention(data.employmentIntention)
    ensureSelectedResume(resumes.value)
  } catch (error) {
    loadError.value =
      error instanceof Error ? error.message : '加载学生模块数据失败，请稍后重试'
    dashboard.value = null
  } finally {
    if (token === requestToken) {
      loading.value = false
    }
  }
}

const applyProfileData = (profile: StudentDashboardResponse['profile']) => {
  profileForm.gender = profile?.gender ?? ''
  profileForm.age = profile?.age ?? null
  profileForm.major = profile?.major ?? ''
  profileForm.biography = profile?.biography ?? ''
  profileForm.graduationYear = profile?.graduationYear ?? null
}

const applyEmploymentIntention = (
  employmentIntention: StudentDashboardResponse['employmentIntention']
) => {
  const intention = employmentIntention?.intention
  intentionForm.expectedPosition = intention?.expectedPosition ?? ''
  intentionForm.salaryRange = intention?.salaryRange ?? ''
  intentionForm.workType = intention?.workType ?? ''
  intentionForm.notes = intention?.notes ?? ''
  const cities =
    employmentIntention?.cities?.map((item) => item.city).filter(Boolean) ?? []
  intentionForm.cities = cities.length ? [...cities] : ['']
}

const ensureSelectedResume = (items: ResumeOverview[]) => {
  if (!items.length) {
    selectedResumeId.value = null
    return
  }
  const first = items[0]!
  if (!selectedResumeId.value || !items.some((item) => item.id === selectedResumeId.value)) {
    selectedResumeId.value = first.id
  }
}

const applyStudentId = () => {
  const parsed = parseStudentIdValue(studentIdInput.value)
  if (!parsed) {
    studentIdError.value = '请输入有效的学生 ID（正整数）'
    return
  }
  studentIdError.value = ''
  if (studentId.value !== parsed) {
    studentId.value = parsed
  } else {
    fetchDashboard()
  }
  const query = { ...route.query } as Record<string, any>
  query.studentId = String(parsed)
  const params = props.section ? { section: props.section } : {}
  router.replace({ name: 'student-dashboard', params, query }).catch(() => {})
}

const scrollToSection = (key: string, updateRoute = true) => {
  const element = document.getElementById(key)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
  activeSection.value = key
  if (updateRoute) {
    const query = { ...route.query } as Record<string, any>
    if (studentId.value) {
      query.studentId = String(studentId.value)
    }
    router.replace({ name: 'student-dashboard', params: { section: key }, query }).catch(() => {})
  }
}

const formatDate = (value: string | null | undefined) => {
  if (!value) return ''
  if (value.length > 10) {
    return value.substring(0, 10)
  }
  return value
}

const formatDateTime = (value: string | null | undefined) => {
  if (!value) return ''
  return value.replace('T', ' ').substring(0, 16)
}

const formatDateRange = (start: string | null | undefined, end: string | null | undefined) => {
  const startLabel = formatDate(start) || '未填写'
  const endLabel = formatDate(end) || '至今'
  return `${startLabel} - ${endLabel}`
}

const formatExperienceType = (type: ExperienceType | null | undefined) =>
  type ? experienceTypeLabels[type] : '其他'

const formatWorkType = (
  type: JobPostingWorkType | EmploymentIntentionWorkType | null | undefined
) =>
  type ? workTypeLabels[type] : '未指定'

const formatJobStatus = (status: JobApplicationStatus) => jobStatusLabels[status] || status

const formatInterviewStatus = (status: InterviewStatus) =>
  interviewStatusLabels[status] || status

const resetEducationForm = () => {
  educationForm.id = null
  educationForm.school = ''
  educationForm.major = ''
  educationForm.degree = ''
  educationForm.startDate = ''
  educationForm.endDate = ''
  educationForm.description = ''
  editingEducationId.value = null
  educationError.value = ''
  educationMessage.value = ''
}

const startEditEducation = (education: StudentEducation) => {
  educationForm.id = education.id ?? null
  educationForm.school = education.school ?? ''
  educationForm.major = education.major ?? ''
  educationForm.degree = education.degree ?? ''
  educationForm.startDate = education.startDate ?? ''
  educationForm.endDate = education.endDate ?? ''
  educationForm.description = education.description ?? ''
  editingEducationId.value = education.id ?? null
  educationMessage.value = ''
  educationError.value = ''
}

const submitEducationForm = async () => {
  if (!studentId.value) {
    educationError.value = '请先选择学生 ID'
    return
  }
  educationSaving.value = true
  educationError.value = ''
  educationMessage.value = ''
  const payload = {
    studentId: studentId.value,
    school: educationForm.school || '',
    major: educationForm.major || '',
    degree: educationForm.degree || '',
    startDate: educationForm.startDate || null,
    endDate: educationForm.endDate || null,
    description: educationForm.description || '',
  }
  try {
    if (editingEducationId.value) {
      await updateEducation(editingEducationId.value, payload)
      educationMessage.value = '教育经历已更新'
    } else {
      await createEducation(payload)
      educationMessage.value = '教育经历已新增'
    }
    resetEducationForm()
    await fetchDashboard()
  } catch (error) {
    educationError.value =
      error instanceof Error ? error.message : '保存教育经历失败，请稍后再试'
  } finally {
    educationSaving.value = false
  }
}

const handleDeleteEducation = async (id: number) => {
  if (!window.confirm('确定删除该教育经历吗？')) return
  educationSaving.value = true
  educationError.value = ''
  educationMessage.value = ''
  try {
    await deleteEducation(id)
    educationMessage.value = '已删除教育经历'
    await fetchDashboard()
  } catch (error) {
    educationError.value = error instanceof Error ? error.message : '删除教育经历失败'
  } finally {
    educationSaving.value = false
  }
}

const resetExperienceForm = () => {
  experienceForm.id = null
  experienceForm.title = ''
  experienceForm.organization = ''
  experienceForm.experienceType = 'INTERNSHIP'
  experienceForm.startDate = ''
  experienceForm.endDate = ''
  experienceForm.description = ''
  editingExperienceId.value = null
  experienceMessage.value = ''
  experienceError.value = ''
}

const startEditExperience = (experience: StudentExperience) => {
  experienceForm.id = experience.id ?? null
  experienceForm.title = experience.title ?? ''
  experienceForm.organization = experience.organization ?? ''
  experienceForm.experienceType = experience.experienceType ?? 'INTERNSHIP'
  experienceForm.startDate = experience.startDate ?? ''
  experienceForm.endDate = experience.endDate ?? ''
  experienceForm.description = experience.description ?? ''
  editingExperienceId.value = experience.id ?? null
  experienceMessage.value = ''
  experienceError.value = ''
}

const submitExperienceForm = async () => {
  if (!studentId.value) {
    experienceError.value = '请先选择学生 ID'
    return
  }
  experienceSaving.value = true
  experienceError.value = ''
  experienceMessage.value = ''
  const payload = {
    studentId: studentId.value,
    title: experienceForm.title || '',
    organization: experienceForm.organization || '',
    experienceType: experienceForm.experienceType,
    startDate: experienceForm.startDate || null,
    endDate: experienceForm.endDate || null,
    description: experienceForm.description || '',
  }
  try {
    if (editingExperienceId.value) {
      await updateExperience(editingExperienceId.value, payload)
      experienceMessage.value = '实践经历已更新'
    } else {
      await createExperience(payload)
      experienceMessage.value = '实践经历已新增'
    }
    resetExperienceForm()
    await fetchDashboard()
  } catch (error) {
    experienceError.value =
      error instanceof Error ? error.message : '保存实践经历失败，请稍后再试'
  } finally {
    experienceSaving.value = false
  }
}

const handleDeleteExperience = async (id: number) => {
  if (!window.confirm('确定删除该实践经历吗？')) return
  experienceSaving.value = true
  experienceError.value = ''
  experienceMessage.value = ''
  try {
    await deleteExperience(id)
    experienceMessage.value = '已删除实践经历'
    await fetchDashboard()
  } catch (error) {
    experienceError.value = error instanceof Error ? error.message : '删除实践经历失败'
  } finally {
    experienceSaving.value = false
  }
}

const resetAwardForm = () => {
  awardForm.id = null
  awardForm.name = ''
  awardForm.level = ''
  awardForm.awardDate = ''
  awardForm.description = ''
  editingAwardId.value = null
  awardMessage.value = ''
  awardError.value = ''
}

const startEditAward = (award: StudentAward) => {
  awardForm.id = award.id ?? null
  awardForm.name = award.name ?? ''
  awardForm.level = award.level ?? ''
  awardForm.awardDate = award.awardDate ?? ''
  awardForm.description = award.description ?? ''
  editingAwardId.value = award.id ?? null
  awardMessage.value = ''
  awardError.value = ''
}

const submitAwardForm = async () => {
  if (!studentId.value) {
    awardError.value = '请先选择学生 ID'
    return
  }
  awardSaving.value = true
  awardError.value = ''
  awardMessage.value = ''
  const payload = {
    studentId: studentId.value,
    name: awardForm.name || '',
    level: awardForm.level || '',
    awardDate: awardForm.awardDate || null,
    description: awardForm.description || '',
  }
  try {
    if (editingAwardId.value) {
      await updateAward(editingAwardId.value, payload)
      awardMessage.value = '获奖信息已更新'
    } else {
      await createAward(payload)
      awardMessage.value = '获奖信息已新增'
    }
    resetAwardForm()
    await fetchDashboard()
  } catch (error) {
    awardError.value = error instanceof Error ? error.message : '保存获奖信息失败'
  } finally {
    awardSaving.value = false
  }
}

const handleDeleteAward = async (id: number) => {
  if (!window.confirm('确定删除该获奖记录吗？')) return
  awardSaving.value = true
  awardError.value = ''
  awardMessage.value = ''
  try {
    await deleteAward(id)
    awardMessage.value = '已删除获奖记录'
    await fetchDashboard()
  } catch (error) {
    awardError.value = error instanceof Error ? error.message : '删除获奖记录失败'
  } finally {
    awardSaving.value = false
  }
}

const addIntentionCity = () => {
  intentionForm.cities.push('')
}

const removeIntentionCity = (index: number) => {
  if (intentionForm.cities.length === 1) return
  intentionForm.cities.splice(index, 1)
}

const submitEmploymentIntention = async () => {
  if (!studentId.value) {
    intentionError.value = '请先选择学生 ID'
    return
  }
  intentionSaving.value = true
  intentionError.value = ''
  intentionMessage.value = ''
  const payload = {
    studentId: studentId.value,
    expectedPosition: intentionForm.expectedPosition || '',
    salaryRange: intentionForm.salaryRange || '',
    workType: intentionForm.workType,
    notes: intentionForm.notes || '',
    cities: intentionForm.cities.map((city) => city.trim()).filter(Boolean),
  }
  try {
    await saveEmploymentIntention(payload)
    intentionMessage.value = '就业意向已保存'
    await fetchDashboard()
  } catch (error) {
    intentionError.value = error instanceof Error ? error.message : '保存就业意向失败'
  } finally {
    intentionSaving.value = false
  }
}

const resetResumeForm = () => {
  resumeForm.id = null
  resumeForm.title = ''
  resumeForm.summary = ''
  resumeForm.portfolioUrl = ''
  resumeForm.experiences = []
  resumeForm.skills = []
  resumeMessage.value = ''
  resumeError.value = ''
}

const addResumeExperience = () => {
  resumeForm.experiences.push({
    title: '',
    organization: '',
    startDate: null,
    endDate: null,
    description: '',
  })
}

const removeResumeExperience = (index: number) => {
  resumeForm.experiences.splice(index, 1)
}

const addResumeSkill = () => {
  resumeForm.skills.push({
    skill: '',
    proficiency: null,
  })
}

const removeResumeSkill = (index: number) => {
  resumeForm.skills.splice(index, 1)
}

const editResume = async (id: number) => {
  resumeFormLoading.value = true
  resumeError.value = ''
  resumeMessage.value = ''
  try {
    const detail = await fetchResumeDetail(id)
    resumeForm.id = detail.resume.id ?? null
    resumeForm.title = detail.resume.title ?? ''
    resumeForm.summary = detail.resume.summary ?? ''
    resumeForm.portfolioUrl = detail.resume.portfolioUrl ?? ''
    resumeForm.experiences = (detail.experiences ?? []).map((item) => ({
      title: item.title ?? '',
      organization: item.organization ?? '',
      startDate: item.startDate ?? null,
      endDate: item.endDate ?? null,
      description: item.description ?? '',
    }))
    resumeForm.skills = (detail.skills ?? []).map((item) => ({
      skill: item.skill ?? '',
      proficiency: item.proficiency ?? null,
    }))
  } catch (error) {
    resumeError.value = error instanceof Error ? error.message : '加载简历详情失败'
  } finally {
    resumeFormLoading.value = false
  }
}

const submitResumeForm = async () => {
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
    summary: resumeForm.summary || '',
    portfolioUrl: resumeForm.portfolioUrl || '',
    experiences: resumeForm.experiences
      .map((exp) => ({
        title: exp.title.trim(),
        organization: (exp.organization || '').trim(),
        startDate: exp.startDate || null,
        endDate: exp.endDate || null,
        description: exp.description || '',
      }))
      .filter((exp) => exp.title.length > 0),
    skills: resumeForm.skills
      .map((skill) => ({
        skill: skill.skill.trim(),
        proficiency: skill.proficiency ?? null,
      }))
      .filter((skill) => skill.skill.length > 0),
  }
  try {
    if (resumeForm.id) {
      await updateResume(resumeForm.id, payload)
      resumeMessage.value = '简历已更新'
    } else {
      await createResume(payload)
      resumeMessage.value = '简历已创建'
    }
    resetResumeForm()
    await fetchDashboard()
  } catch (error) {
    resumeError.value = error instanceof Error ? error.message : '保存简历失败，请稍后再试'
  } finally {
    resumeSaving.value = false
  }
}

const removeResume = async (id: number) => {
  if (!window.confirm('确定删除该简历吗？')) return
  resumeSaving.value = true
  resumeError.value = ''
  resumeMessage.value = ''
  try {
    await deleteResume(id)
    resumeMessage.value = '简历已删除'
    if (resumeForm.id === id) {
      resetResumeForm()
    }
    await fetchDashboard()
  } catch (error) {
    resumeError.value = error instanceof Error ? error.message : '删除简历失败'
  } finally {
    resumeSaving.value = false
  }
}

const handleProfileSubmit = async () => {
  if (!studentId.value) {
    profileError.value = '请先选择学生 ID'
    return
  }
  profileSaving.value = true
  profileError.value = ''
  profileMessage.value = ''
  try {
    await submitStudentProfileRequest({
      id: studentId.value,
      gender: profileForm.gender || null,
      age: profileForm.age ?? null,
      major: profileForm.major || null,
      biography: profileForm.biography || null,
      graduationYear: profileForm.graduationYear ?? null,
    })
    profileMessage.value = '档案更新申请已提交，待管理员审核后生效'
    await fetchDashboard()
  } catch (error) {
    profileError.value = error instanceof Error ? error.message : '保存个人信息失败'
  } finally {
    profileSaving.value = false
  }
}

const applyForJob = async (jobId: number) => {
  if (!studentId.value) {
    applicationError.value = '请先选择学生 ID'
    return
  }
  if (!selectedResumeId.value) {
    applicationError.value = '请先创建并选择一份简历'
    return
  }
  applyingJobId.value = jobId
  applicationError.value = ''
  applicationMessage.value = ''
  try {
    await createJobApplication({
      jobId,
      studentId: studentId.value,
      resumeId: selectedResumeId.value,
      coverLetter: applicationCoverLetter.value || '',
    })
    applicationMessage.value = '职位投递成功'
    applicationCoverLetter.value = ''
    await fetchDashboard()
  } catch (error) {
    applicationError.value = error instanceof Error ? error.message : '职位投递失败'
  } finally {
    applyingJobId.value = null
  }
}
</script>

<template>
  <div class="student-dashboard">
    <section class="page-header">
      <div>
        <h1>学生就业服务中心</h1>
        <p>集中管理个人资料、求职意向、简历与投递流程，实现一站式就业服务。</p>
      </div>
      <div class="student-id-selector">
        <label for="student-id-input">学生 ID</label>
        <div class="id-input-group">
          <input
            id="student-id-input"
            v-model="studentIdInput"
            type="number"
            min="1"
            placeholder="请输入学生 ID"
            :disabled="isStudentUser"
            @keyup.enter="applyStudentId"
          />
          <button type="button" @click="applyStudentId" :disabled="isStudentUser">载入数据</button>
        </div>
        <p v-if="studentIdError" class="feedback feedback--error">{{ studentIdError }}</p>
        <p v-if="isStudentUser" class="feedback">您已自动登录为学生角色</p>
      </div>
    </section>

    <div v-if="loading" class="state-block">正在加载学生数据，请稍候...</div>
    <div v-else-if="loadError" class="state-block state-block--error">{{ loadError }}</div>
    <div v-else>
      <div v-if="!studentId" class="state-block">请先输入学生 ID 以加载对应模块内容。</div>
      <div v-else-if="!dashboard" class="state-block">暂无学生数据，请完成基础信息填写。</div>
      <div v-else class="dashboard-body">
        <section id="overview" class="card">
          <header class="card__header">
            <h2>模块导航</h2>
            <p>快速跳转至学生可使用的功能模块。</p>
          </header>
          <div class="module-grid">
            <button
              v-for="module in modules"
              :key="module.key"
              type="button"
              class="module-card"
              :class="{ 'module-card--active': activeSection === module.key }"
              @click="scrollToSection(module.key)"
            >
              <div class="module-card__title">
                <h3>{{ module.name }}</h3>
                <span>{{ module.description }}</span>
              </div>
              <div class="module-card__tags">
                <span v-for="item in module.capabilities" :key="item">{{ item }}</span>
              </div>
            </button>
          </div>

          <div class="stats-grid">
            <div class="stat-card">
              <span>资料完成度</span>
              <strong>{{ header?.completionPercentage ?? 0 }}%</strong>
            </div>
            <div class="stat-card">
              <span>我的简历</span>
              <strong>{{ header?.resumeCount ?? 0 }}</strong>
            </div>
            <div class="stat-card">
              <span>投递岗位</span>
              <strong>{{ header?.jobApplicationCount ?? 0 }}</strong>
            </div>
            <div class="stat-card">
              <span>待面试</span>
              <strong>{{ header?.pendingInterviewCount ?? 0 }}</strong>
            </div>
            <div class="stat-card">
              <span>获奖记录</span>
              <strong>{{ header?.awardCount ?? 0 }}</strong>
            </div>
            <div class="stat-card">
              <span>就业意向</span>
              <strong>{{ header?.hasEmploymentIntention ? '已填写' : '未填写' }}</strong>
            </div>
          </div>
        </section>

        <section id="profile" class="card">
          <header class="card__header">
            <h2>个人档案</h2>
            <p>完善基础信息，提升简历推荐与匹配准确度。</p>
          </header>
          <form class="form" @submit.prevent="handleProfileSubmit">
            <div class="form__grid form__grid--two">
              <label>
                性别
                <select v-model="profileForm.gender">
                  <option value="">未填写</option>
                  <option value="男">男</option>
                  <option value="女">女</option>
                  <option value="其他">其他</option>
                </select>
              </label>
              <label>
                年龄
                <input v-model.number="profileForm.age" type="number" min="0" placeholder="请输入年龄" />
              </label>
              <label>
                专业
                <input v-model="profileForm.major" type="text" placeholder="请输入所学专业" />
              </label>
              <label>
                毕业年份
                <input
                  v-model.number="profileForm.graduationYear"
                  type="number"
                  min="1900"
                  max="2100"
                  placeholder="例如 2025"
                />
              </label>
            </div>
            <label class="form__full">
              个人简介
              <textarea v-model="profileForm.biography" rows="3" placeholder="介绍自己的优势与求职方向" />
            </label>
            <div class="form__actions">
              <button type="submit" :disabled="profileSaving">
                {{ profileSaving ? '保存中...' : '保存信息' }}
              </button>
              <span v-if="profileMessage" class="feedback feedback--success">{{ profileMessage }}</span>
              <span v-if="profileError" class="feedback feedback--error">{{ profileError }}</span>
            </div>
          </form>
        </section>

        <section id="education" class="card">
          <header class="card__header">
            <h2>教育经历</h2>
            <p>记录学习经历，展示专业背景与成绩。</p>
          </header>
          <div v-if="educationList.length" class="item-list">
            <article v-for="education in educationList" :key="education.id" class="item">
              <div>
                <h3>{{ education.school || '未填写学校' }}</h3>
                <p class="item__meta">
                  <span>{{ education.major || '专业未填写' }}</span>
                  <span>{{ education.degree || '学位未填写' }}</span>
                  <span>{{ formatDateRange(education.startDate, education.endDate) }}</span>
                </p>
                <p v-if="education.description" class="item__desc">{{ education.description }}</p>
              </div>
              <div class="item__actions">
                <button type="button" @click="startEditEducation(education)">编辑</button>
                <button type="button" class="danger" @click="handleDeleteEducation(education.id)">删除</button>
              </div>
            </article>
          </div>
          <p v-else class="empty">暂无教育经历，请添加。</p>

          <form class="form" @submit.prevent="submitEducationForm">
            <h3 class="form__title">{{ editingEducationId ? '编辑教育经历' : '新增教育经历' }}</h3>
            <div class="form__grid form__grid--two">
              <label>
                学校名称
                <input v-model="educationForm.school" type="text" placeholder="请输入学校" />
              </label>
              <label>
                专业方向
                <input v-model="educationForm.major" type="text" placeholder="请输入专业" />
              </label>
              <label>
                学位
                <input v-model="educationForm.degree" type="text" placeholder="如：本科 / 硕士" />
              </label>
              <label>
                入学日期
                <input v-model="educationForm.startDate" type="date" />
              </label>
              <label>
                毕业日期
                <input v-model="educationForm.endDate" type="date" />
              </label>
            </div>
            <label class="form__full">
              详细描述
              <textarea v-model="educationForm.description" rows="2" placeholder="主要课程、成绩或荣誉" />
            </label>
            <div class="form__actions">
              <button type="submit" :disabled="educationSaving">
                {{ editingEducationId ? (educationSaving ? '更新中...' : '保存修改') : educationSaving ? '保存中...' : '新增经历' }}
              </button>
              <button
                v-if="editingEducationId"
                type="button"
                class="ghost"
                @click="resetEducationForm"
              >
                取消编辑
              </button>
              <span v-if="educationMessage" class="feedback feedback--success">{{ educationMessage }}</span>
              <span v-if="educationError" class="feedback feedback--error">{{ educationError }}</span>
            </div>
          </form>
        </section>

        <section id="experience" class="card">
          <header class="card__header">
            <h2>实践经历</h2>
            <p>记录实习、项目或志愿经历，展示实践能力。</p>
          </header>
          <div v-if="experienceList.length" class="item-list">
            <article v-for="experience in experienceList" :key="experience.id" class="item">
              <div>
                <h3>{{ experience.title || '经历名称未填写' }}</h3>
                <p class="item__meta">
                  <span>{{ formatExperienceType(experience.experienceType) }}</span>
                  <span>{{ experience.organization || '组织未填写' }}</span>
                  <span>{{ formatDateRange(experience.startDate, experience.endDate) }}</span>
                </p>
                <p v-if="experience.description" class="item__desc">{{ experience.description }}</p>
              </div>
              <div class="item__actions">
                <button type="button" @click="startEditExperience(experience)">编辑</button>
                <button type="button" class="danger" @click="handleDeleteExperience(experience.id)">
                  删除
                </button>
              </div>
            </article>
          </div>
          <p v-else class="empty">暂无实践经历，请添加。</p>

          <form class="form" @submit.prevent="submitExperienceForm">
            <h3 class="form__title">{{ editingExperienceId ? '编辑实践经历' : '新增实践经历' }}</h3>
            <div class="form__grid form__grid--two">
              <label>
                经历名称
                <input v-model="experienceForm.title" type="text" placeholder="例如：暑期实习" />
              </label>
              <label>
                所属组织
                <input v-model="experienceForm.organization" type="text" placeholder="企业或团队名称" />
              </label>
              <label>
                经历类型
                <select v-model="experienceForm.experienceType">
                  <option v-for="item in experienceTypeOptions" :key="item.value" :value="item.value">
                    {{ item.label }}
                  </option>
                </select>
              </label>
              <label>
                开始日期
                <input v-model="experienceForm.startDate" type="date" />
              </label>
              <label>
                结束日期
                <input v-model="experienceForm.endDate" type="date" />
              </label>
            </div>
            <label class="form__full">
              经历描述
              <textarea v-model="experienceForm.description" rows="2" placeholder="主要职责或成果" />
            </label>
            <div class="form__actions">
              <button type="submit" :disabled="experienceSaving">
                {{ editingExperienceId ? (experienceSaving ? '更新中...' : '保存修改') : experienceSaving ? '保存中...' : '新增经历' }}
              </button>
              <button
                v-if="editingExperienceId"
                type="button"
                class="ghost"
                @click="resetExperienceForm"
              >
                取消编辑
              </button>
              <span v-if="experienceMessage" class="feedback feedback--success">{{ experienceMessage }}</span>
              <span v-if="experienceError" class="feedback feedback--error">{{ experienceError }}</span>
            </div>
          </form>
        </section>

        <section id="awards" class="card">
          <header class="card__header">
            <h2>获奖荣誉</h2>
            <p>记录各类竞赛、证书与荣誉，突出个人亮点。</p>
          </header>
          <div v-if="awardList.length" class="item-list">
            <article v-for="award in awardList" :key="award.id" class="item">
              <div>
                <h3>{{ award.name || '未命名的奖项' }}</h3>
                <p class="item__meta">
                  <span>{{ award.level || '级别未填写' }}</span>
                  <span>{{ formatDate(award.awardDate) || '日期未填写' }}</span>
                </p>
                <p v-if="award.description" class="item__desc">{{ award.description }}</p>
              </div>
              <div class="item__actions">
                <button type="button" @click="startEditAward(award)">编辑</button>
                <button type="button" class="danger" @click="handleDeleteAward(award.id)">删除</button>
              </div>
            </article>
          </div>
          <p v-else class="empty">暂无获奖记录，可添加竞赛获奖或荣誉称号。</p>

          <form class="form" @submit.prevent="submitAwardForm">
            <h3 class="form__title">{{ editingAwardId ? '编辑获奖信息' : '新增获奖信息' }}</h3>
            <div class="form__grid form__grid--two">
              <label>
                奖项名称
                <input v-model="awardForm.name" type="text" placeholder="请输入奖项名称" />
              </label>
              <label>
                奖项级别
                <input v-model="awardForm.level" type="text" placeholder="如：国家级 / 校级" />
              </label>
              <label>
                获奖日期
                <input v-model="awardForm.awardDate" type="date" />
              </label>
            </div>
            <label class="form__full">
              详细说明
              <textarea v-model="awardForm.description" rows="2" placeholder="补充获奖背景或成果" />
            </label>
            <div class="form__actions">
              <button type="submit" :disabled="awardSaving">
                {{ editingAwardId ? (awardSaving ? '更新中...' : '保存修改') : awardSaving ? '保存中...' : '新增获奖' }}
              </button>
              <button v-if="editingAwardId" type="button" class="ghost" @click="resetAwardForm">取消编辑</button>
              <span v-if="awardMessage" class="feedback feedback--success">{{ awardMessage }}</span>
              <span v-if="awardError" class="feedback feedback--error">{{ awardError }}</span>
            </div>
          </form>
        </section>

        <section id="intention" class="card">
          <header class="card__header">
            <h2>就业意向</h2>
            <p>设置期望岗位、地点与工作类型，系统将据此推荐岗位。</p>
          </header>
          <form class="form" @submit.prevent="submitEmploymentIntention">
            <div class="form__grid form__grid--two">
              <label>
                期望职位
                <input v-model="intentionForm.expectedPosition" type="text" placeholder="例如：前端工程师" />
              </label>
              <label>
                薪资期望
                <input v-model="intentionForm.salaryRange" type="text" placeholder="例如：8k-12k/月" />
              </label>
              <label>
                工作类型
                <select v-model="intentionForm.workType">
                  <option value="">未指定</option>
                  <option v-for="item in workTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
                </select>
              </label>
            </div>
            <label class="form__full">
              备注信息
              <textarea v-model="intentionForm.notes" rows="2" placeholder="补充其他就业要求或说明" />
            </label>
            <div class="city-list">
              <span class="city-list__label">目标城市</span>
              <div class="city-list__items">
                <div v-for="(city, index) in intentionForm.cities" :key="index" class="city-input">
                  <input v-model="intentionForm.cities[index]" type="text" placeholder="填写城市" />
                  <button type="button" class="ghost" @click="removeIntentionCity(index)" :disabled="intentionForm.cities.length === 1">
                    删除
                  </button>
                </div>
              </div>
              <button type="button" class="link" @click="addIntentionCity">+ 添加城市</button>
            </div>
            <div class="form__actions">
              <button type="submit" :disabled="intentionSaving">
                {{ intentionSaving ? '保存中...' : '保存意向' }}
              </button>
              <span v-if="intentionMessage" class="feedback feedback--success">{{ intentionMessage }}</span>
              <span v-if="intentionError" class="feedback feedback--error">{{ intentionError }}</span>
            </div>
          </form>
        </section>

        <section id="resume" class="card">
          <header class="card__header">
            <h2>简历管理</h2>
            <p>维护多份简历，针对不同岗位投递更合适的版本。</p>
          </header>
          <div v-if="resumes.length" class="resume-list">
            <article v-for="resume in resumes" :key="resume.id" class="resume-card">
              <div class="resume-card__header">
                <h3>{{ resume.title }}</h3>
                <div class="resume-card__meta">
                  <span>更新：{{ formatDateTime(resume.updatedAt) || '未记录' }}</span>
                  <span>经历：{{ resume.experienceCount }}</span>
                  <span>技能：{{ resume.skillCount }}</span>
                </div>
              </div>
              <p class="resume-card__summary">{{ resume.summary || '暂无简介' }}</p>
              <p v-if="resume.portfolioUrl" class="resume-card__link">作品集：{{ resume.portfolioUrl }}</p>
              <div class="resume-card__actions">
                <button type="button" @click="editResume(resume.id)">编辑</button>
                <button type="button" class="danger" @click="removeResume(resume.id)">删除</button>
              </div>
            </article>
          </div>
          <p v-else class="empty">尚未创建简历，请填写下方表单创建首份简历。</p>

          <form class="form" @submit.prevent="submitResumeForm">
            <h3 class="form__title">{{ resumeForm.id ? '编辑简历' : '新增简历' }}</h3>
            <div class="form__grid form__grid--two">
              <label>
                简历标题
                <input v-model="resumeForm.title" type="text" placeholder="例如：校园招聘通用版" />
              </label>
              <label>
                作品集链接
                <input v-model="resumeForm.portfolioUrl" type="url" placeholder="可选，填写个人网站或作品集" />
              </label>
            </div>
            <label class="form__full">
              简历摘要
              <textarea v-model="resumeForm.summary" rows="3" placeholder="概述技能与求职目标" />
            </label>

            <div class="form__subsection">
              <div class="form__subsection-header">
                <h4>简历经历</h4>
                <button type="button" class="link" @click="addResumeExperience">+ 添加经历</button>
              </div>
              <div v-if="resumeForm.experiences.length" class="subform-list">
                <div v-for="(experience, index) in resumeForm.experiences" :key="index" class="subform">
                  <div class="form__grid form__grid--two">
                    <label>
                      标题
                      <input v-model="experience.title" type="text" placeholder="经历标题" />
                    </label>
                    <label>
                      组织
                      <input v-model="experience.organization" type="text" placeholder="所属单位" />
                    </label>
                    <label>
                      开始日期
                      <input v-model="experience.startDate" type="date" />
                    </label>
                    <label>
                      结束日期
                      <input v-model="experience.endDate" type="date" />
                    </label>
                  </div>
                  <label class="form__full">
                    描述
                    <textarea v-model="experience.description" rows="2" placeholder="职责与成果" />
                  </label>
                  <button type="button" class="ghost" @click="removeResumeExperience(index)">移除经历</button>
                </div>
              </div>
              <p v-else class="empty">尚未添加经历，可选择“添加经历”。</p>
            </div>

            <div class="form__subsection">
              <div class="form__subsection-header">
                <h4>技能标签</h4>
                <button type="button" class="link" @click="addResumeSkill">+ 添加技能</button>
              </div>
              <div v-if="resumeForm.skills.length" class="subform-list">
                <div v-for="(skill, index) in resumeForm.skills" :key="index" class="subform subform--inline">
                  <input v-model="skill.skill" type="text" placeholder="技能名称" />
                  <input
                    v-model.number="skill.proficiency"
                    type="number"
                    min="0"
                    max="100"
                    placeholder="掌握度（0-100）"
                  />
                  <button type="button" class="ghost" @click="removeResumeSkill(index)">删除</button>
                </div>
              </div>
              <p v-else class="empty">可添加技能名称与掌握度，便于岗位筛选。</p>
            </div>

            <div class="form__actions">
              <button type="submit" :disabled="resumeSaving || resumeFormLoading">
                {{ resumeForm.id ? (resumeSaving ? '更新中...' : '保存修改') : resumeSaving ? '保存中...' : '创建简历' }}
              </button>
              <button v-if="resumeForm.id" type="button" class="ghost" @click="resetResumeForm">取消编辑</button>
              <span v-if="resumeMessage" class="feedback feedback--success">{{ resumeMessage }}</span>
              <span v-if="resumeError" class="feedback feedback--error">{{ resumeError }}</span>
            </div>
          </form>
        </section>

        <section id="applications" class="card">
          <header class="card__header">
            <h2>投递记录</h2>
            <p>查看岗位投递状态，及时跟进求职进度。</p>
          </header>
          <div v-if="jobApplications.length" class="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>岗位</th>
                  <th>状态</th>
                  <th>投递日期</th>
                  <th>使用简历</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="application in jobApplications" :key="application.id">
                  <td>
                    <strong>{{ application.jobTitle || '岗位已下架或未知' }}</strong>
                    <p class="muted">{{ application.jobLocation || '地点未填写' }}</p>
                  </td>
                  <td>{{ formatJobStatus(application.status) }}</td>
                  <td>{{ formatDateTime(application.appliedAt) || '—' }}</td>
                  <td>
                    <span v-if="application.resumeId">简历 #{{ application.resumeId }}</span>
                    <span v-else>—</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <p v-else class="empty">尚未投递任何岗位，可在下方推荐岗位中快速投递。</p>
        </section>

        <section id="interviews" class="card">
          <header class="card__header">
            <h2>面试安排</h2>
            <p>跟踪面试时间、地点与反馈，避免错过重要面试。</p>
          </header>
          <div v-if="interviewList.length" class="item-list">
            <article v-for="interview in interviewList" :key="interview.id" class="item">
              <div>
                <h3>{{ interview.jobTitle || '面试岗位未注明' }}</h3>
                <p class="item__meta">
                  <span>{{ formatInterviewStatus(interview.status) }}</span>
                  <span>{{ formatDateTime(interview.scheduledTime) || '时间待定' }}</span>
                </p>
                <p class="item__desc">
                  {{ interview.location || interview.meetingLink || '面试地点/链接待通知' }}
                </p>
              </div>
            </article>
          </div>
          <p v-else class="empty">暂无面试安排，一旦企业发出邀请将会在此显示。</p>
        </section>

        <section id="jobs" class="card">
          <header class="card__header">
            <h2>推荐职位</h2>
            <p>结合就业意向智能推荐岗位，可直接选择简历进行投递。</p>
          </header>
          <div v-if="recommendedJobs.length" class="job-grid">
            <article v-for="job in recommendedJobs" :key="job.id" class="job-card">
              <div class="job-card__header">
                <h3>{{ job.title }}</h3>
                <span class="job-card__tag" :class="{ 'job-card__tag--match': job.matchesIntention }">
                  {{ job.matchesIntention ? '符合意向' : '推荐岗位' }}
                </span>
              </div>
              <ul class="job-card__meta">
                <li>地点：{{ job.location || '未注明' }}</li>
                <li>薪资：{{ job.salaryRange || '面议' }}</li>
                <li>类型：{{ formatWorkType(job.workType) }}</li>
              </ul>
              <button
                type="button"
                class="job-card__action"
                :disabled="applyingJobId === job.id"
                @click="applyForJob(job.id)"
              >
                {{ applyingJobId === job.id ? '投递中...' : '投递该岗位' }}
              </button>
            </article>
          </div>
          <p v-else class="empty">暂无推荐岗位，可先完善就业意向以获得更精准的推荐。</p>

          <div class="application-box">
            <h3>选择简历进行投递</h3>
            <div class="form__grid form__grid--two">
              <label>
                使用简历
                <select v-model.number="selectedResumeId">
                  <option value="" disabled>请选择简历</option>
                  <option v-for="resume in resumes" :key="resume.id" :value="resume.id">
                    #{{ resume.id }} - {{ resume.title }}
                  </option>
                </select>
              </label>
              <label>
                附加说明
                <textarea
                  v-model="applicationCoverLetter"
                  rows="2"
                  placeholder="可选：补充自我介绍或求职动机"
                />
              </label>
            </div>
            <p v-if="applicationMessage" class="feedback feedback--success">{{ applicationMessage }}</p>
            <p v-if="applicationError" class="feedback feedback--error">{{ applicationError }}</p>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<style scoped>
.student-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 24px;
  background: #f7f9fc;
  min-height: 100vh;
  box-sizing: border-box;
}

.page-header {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 16px;
  background: linear-gradient(135deg, #2563eb, #60a5fa);
  color: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 12px 24px rgba(37, 99, 235, 0.2);
}

.page-header h1 {
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 700;
}

.page-header p {
  margin: 0;
  opacity: 0.9;
}

.student-id-selector {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 240px;
}

.student-id-selector label {
  font-weight: 600;
  letter-spacing: 0.02em;
}

.id-input-group {
  display: flex;
  gap: 8px;
}

.id-input-group input {
  flex: 1;
  padding: 8px 12px;
  border-radius: 8px;
  border: none;
}

.id-input-group button {
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease;
}

.id-input-group button:hover {
  background: rgba(255, 255, 255, 0.35);
}

.state-block {
  padding: 16px;
  border-radius: 12px;
  background: #fff;
  text-align: center;
  color: #475569;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
}

.state-block--error {
  color: #b91c1c;
  background: #fee2e2;
}

.dashboard-body {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.08);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card__header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card__header h2 {
  margin: 0;
  font-size: 20px;
  color: #1e293b;
}

.card__header p {
  margin: 0;
  color: #64748b;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

.module-card {
  background: #f8fafc;
  border: 1px solid transparent;
  border-radius: 12px;
  padding: 16px;
  text-align: left;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 8px;
  transition: transform 0.15s ease, box-shadow 0.2s ease, border-color 0.15s ease;
}

.module-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 20px rgba(37, 99, 235, 0.15);
}

.module-card--active {
  border-color: rgba(37, 99, 235, 0.6);
  box-shadow: 0 12px 20px rgba(37, 99, 235, 0.12);
}

.module-card__title h3 {
  margin: 0;
  font-size: 18px;
  color: #0f172a;
}

.module-card__title span {
  color: #64748b;
  font-size: 13px;
}

.module-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.module-card__tags span {
  display: inline-flex;
  align-items: center;
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.1);
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 500;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 16px;
}

.stat-card {
  background: #1d4ed8;
  color: #fff;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.15);
}

.stat-card span {
  font-size: 13px;
  opacity: 0.9;
}

.stat-card strong {
  font-size: 22px;
  font-weight: 700;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form__grid {
  display: grid;
  gap: 12px;
}

.form__grid--two {
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

.form__full {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form__subsection {
  border: 1px dashed #cbd5f5;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form__subsection-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form__subsection-header h4 {
  margin: 0;
  font-size: 16px;
  color: #1e293b;
}

.subform-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.subform {
  display: flex;
  flex-direction: column;
  gap: 12px;
  border-radius: 12px;
  background: #f8fafc;
  padding: 12px;
}

.subform--inline {
  flex-direction: row;
  align-items: center;
  gap: 12px;
}

.subform--inline input {
  flex: 1;
}

.form label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-weight: 600;
  color: #334155;
}

.form input,
.form select,
.form textarea {
  border: 1px solid #dbe2ef;
  border-radius: 10px;
  padding: 8px 12px;
  font-size: 14px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form input:focus,
.form select:focus,
.form textarea:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
  outline: none;
}

.form__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.form__actions button {
  padding: 10px 18px;
  border-radius: 999px;
  border: none;
  background: #2563eb;
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease;
}

.form__actions button:hover {
  background: #1d4ed8;
}

.form__actions button:disabled {
  background: #94a3b8;
  cursor: not-allowed;
}

button.ghost {
  background: rgba(148, 163, 184, 0.15);
  color: #475569;
}

button.ghost:hover {
  background: rgba(148, 163, 184, 0.25);
}

button.danger {
  background: rgba(220, 38, 38, 0.12);
  color: #b91c1c;
}

button.danger:hover {
  background: rgba(220, 38, 38, 0.2);
}

button.link {
  background: transparent;
  color: #2563eb;
  border: none;
  padding: 0;
  font-weight: 600;
  cursor: pointer;
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
}

.item h3 {
  margin: 0;
  font-size: 17px;
  color: #0f172a;
}

.item__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 6px 0;
  color: #64748b;
  font-size: 13px;
}

.item__desc {
  margin: 0;
  color: #475569;
}

.item__actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.feedback {
  font-size: 14px;
}

.feedback--success {
  color: #047857;
}

.feedback--error {
  color: #dc2626;
}

.empty {
  margin: 0;
  color: #94a3b8;
  font-style: italic;
}

.city-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.city-list__label {
  font-weight: 600;
  color: #334155;
}

.city-list__items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.city-input {
  display: flex;
  gap: 8px;
}

.city-input input {
  flex: 1;
}

.resume-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.resume-card {
  padding: 16px;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: #f8fafc;
}

.resume-card__header {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.resume-card__header h3 {
  margin: 0;
  color: #0f172a;
}

.resume-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 12px;
  color: #64748b;
}

.resume-card__summary {
  margin: 0;
  color: #475569;
  min-height: 42px;
}

.resume-card__link {
  margin: 0;
  font-size: 13px;
  color: #2563eb;
  word-break: break-all;
}

.resume-card__actions {
  display: flex;
  gap: 8px;
}

.table-wrapper {
  overflow-x: auto;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

table {
  width: 100%;
  border-collapse: collapse;
  min-width: 600px;
}

thead {
  background: #f1f5f9;
}

thead th {
  text-align: left;
  padding: 12px;
  color: #475569;
  font-size: 14px;
}

tbody td {
  padding: 12px;
  border-top: 1px solid #e2e8f0;
  vertical-align: top;
}

.muted {
  margin: 4px 0 0;
  color: #94a3b8;
  font-size: 13px;
}

.job-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

.job-card {
  border: 1px solid #dbeafe;
  border-radius: 16px;
  padding: 16px;
  background: #eff6ff;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.job-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.job-card__header h3 {
  margin: 0;
  font-size: 17px;
  color: #1d4ed8;
}

.job-card__tag {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  background: rgba(37, 99, 235, 0.15);
  color: #1d4ed8;
  font-weight: 600;
}

.job-card__tag--match {
  background: rgba(16, 185, 129, 0.15);
  color: #047857;
}

.job-card__meta {
  list-style: none;
  margin: 0;
  padding: 0;
  color: #475569;
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 14px;
}

.job-card__action {
  align-self: flex-start;
  padding: 8px 16px;
  border-radius: 999px;
  border: none;
  background: #2563eb;
  color: #fff;
  font-weight: 600;
  cursor: pointer;
}

.job-card__action:disabled {
  background: #94a3b8;
  cursor: not-allowed;
}

.application-box {
  margin-top: 16px;
  padding: 16px;
  border-radius: 12px;
  border: 1px dashed #bfdbfe;
  background: #f8fbff;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.application-box h3 {
  margin: 0;
  color: #1d4ed8;
}

@media (max-width: 768px) {
  .page-header {
    padding: 20px;
  }

  .card {
    padding: 20px;
  }

  .module-grid {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  }
}
</style>
