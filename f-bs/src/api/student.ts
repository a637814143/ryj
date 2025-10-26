const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080'

type ApiResponse<T> = {
  code: number
  message: string
  data: T
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(options.headers ?? {}),
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  })

  const json: ApiResponse<T> = await response.json()
  if (!response.ok || json.code !== 200) {
    throw new Error(json?.message || '请求失败')
  }
  return json.data
}

export type StudentModuleInfo = {
  key: string
  name: string
  description: string
  capabilities: string[]
}

export type StudentDashboardHeader = {
  completionPercentage: number
  resumeCount: number
  jobApplicationCount: number
  pendingInterviewCount: number
  awardCount: number
  hasEmploymentIntention: boolean
}

export type StudentProfile = {
  id: number
  gender: string | null
  age: number | null
  major: string | null
  biography: string | null
  graduationYear: number | null
}

export type ProfileRequestStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

export type StudentProfileUpdateRequest = {
  id: number
  studentId: number
  gender: string | null
  age: number | null
  major: string | null
  biography: string | null
  graduationYear: number | null
  status: ProfileRequestStatus
  createdAt: string | null
  reviewedAt: string | null
  reviewerId: number | null
  reviewComment: string | null
}

export type StudentProfileDetail = {
  profile: StudentProfile | null
  pendingRequest: StudentProfileUpdateRequest | null
  history: StudentProfileUpdateRequest[]
}

export type StudentEducation = {
  id: number
  studentId: number
  school: string | null
  major: string | null
  degree: string | null
  startDate: string | null
  endDate: string | null
  description: string | null
}

export type ExperienceType = 'INTERNSHIP' | 'PROJECT' | 'VOLUNTEER' | 'OTHER'

export type StudentExperience = {
  id: number
  studentId: number
  title: string
  organization: string | null
  startDate: string | null
  endDate: string | null
  description: string | null
  experienceType: ExperienceType
}

export type StudentAward = {
  id: number
  studentId: number
  name: string
  awardDate: string | null
  level: string | null
  description: string | null
}

export type EmploymentIntentionWorkType = 'FULL_TIME' | 'PART_TIME' | 'INTERNSHIP' | 'FLEXIBLE'

export type EmploymentIntention = {
  id: number
  studentId: number
  expectedPosition: string | null
  salaryRange: string | null
  workType: EmploymentIntentionWorkType | null
  notes: string | null
}

export type EmploymentIntentionSection = {
  intention: EmploymentIntention | null
  cities: { city: string }[]
}

export type ResumeOverview = {
  id: number
  title: string
  summary: string | null
  portfolioUrl: string | null
  experienceCount: number
  skillCount: number
  updatedAt: string | null
}

export type JobApplicationStatus = 'SUBMITTED' | 'REVIEWING' | 'INTERVIEW' | 'OFFERED' | 'REJECTED'

export type JobApplicationOverview = {
  id: number
  status: JobApplicationStatus
  appliedAt: string | null
  jobId: number | null
  resumeId: number | null
  jobTitle: string | null
  jobLocation: string | null
}

export type InterviewStatus = 'SCHEDULED' | 'COMPLETED' | 'CANCELLED'

export type InterviewOverview = {
  id: number
  status: InterviewStatus
  scheduledTime: string | null
  location: string | null
  meetingLink: string | null
  jobId: number | null
  jobTitle: string | null
}

export type JobPostingWorkType = 'FULL_TIME' | 'PART_TIME' | 'INTERNSHIP' | 'FLEXIBLE'

export type RecommendedJob = {
  id: number
  title: string
  location: string | null
  salaryRange: string | null
  workType: JobPostingWorkType | null
  matchesIntention: boolean
}

export type StudentDashboardResponse = {
  header: StudentDashboardHeader
  modules: StudentModuleInfo[]
  profile: StudentProfile | null
  educations: StudentEducation[]
  experiences: StudentExperience[]
  awards: StudentAward[]
  employmentIntention: EmploymentIntentionSection | null
  resumes: ResumeOverview[]
  jobApplications: JobApplicationOverview[]
  interviews: InterviewOverview[]
  recommendedJobs: RecommendedJob[]
}

export async function fetchStudentDashboard(studentId: number) {
  return request<StudentDashboardResponse>(`/api/student-dashboard/${studentId}`)
}

export async function fetchStudentProfileDetail(studentId: number) {
  return request<StudentProfileDetail>(`/api/students/profiles/${studentId}`)
}

export async function submitStudentProfileRequest(payload: Partial<StudentProfile> & { id: number }) {
  return request<StudentProfileUpdateRequest>('/api/students/profiles/requests', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function updateStudentProfileRequest(requestId: number, payload: Partial<StudentProfile> & { id: number }) {
  return request<boolean>(`/api/students/profiles/requests/${requestId}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export async function deleteStudentProfileRequest(requestId: number, studentId: number) {
  const params = new URLSearchParams({ studentId: String(studentId) })
  return request<boolean>(`/api/students/profiles/requests/${requestId}?${params.toString()}`, {
    method: 'DELETE',
  })
}

export async function createEducation(payload: Omit<StudentEducation, 'id'>) {
  return request<StudentEducation>('/api/students/educations', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function updateEducation(id: number, payload: Partial<StudentEducation>) {
  return request<boolean>(`/api/students/educations/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export async function deleteEducation(id: number) {
  return request<boolean>(`/api/students/educations/${id}`, {
    method: 'DELETE',
  })
}

export async function createExperience(payload: Omit<StudentExperience, 'id'>) {
  return request<StudentExperience>('/api/students/experiences', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function updateExperience(id: number, payload: Partial<StudentExperience>) {
  return request<boolean>(`/api/students/experiences/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export async function deleteExperience(id: number) {
  return request<boolean>(`/api/students/experiences/${id}`, {
    method: 'DELETE',
  })
}

export async function createAward(payload: Omit<StudentAward, 'id'>) {
  return request<StudentAward>('/api/students/awards', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function updateAward(id: number, payload: Partial<StudentAward>) {
  return request<boolean>(`/api/students/awards/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export async function deleteAward(id: number) {
  return request<boolean>(`/api/students/awards/${id}`, {
    method: 'DELETE',
  })
}

export async function saveEmploymentIntention(payload: {
  studentId: number
  expectedPosition: string
  salaryRange: string
  workType: EmploymentIntentionWorkType | ''
  notes: string
  cities: string[]
}) {
  const body = {
    ...payload,
    workType: payload.workType || null,
  }
  return request('/api/employment-intentions', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

export type ResumeExperienceInput = {
  title: string
  organization: string
  startDate: string | null
  endDate: string | null
  description: string
}

export type ResumeSkillInput = {
  skill: string
  proficiency: number | null
}

export async function createResume(payload: {
  studentId: number
  title: string
  summary: string
  portfolioUrl: string
  experiences: ResumeExperienceInput[]
  skills: ResumeSkillInput[]
}) {
  return request('/api/resumes', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function updateResume(id: number, payload: {
  studentId: number
  title: string
  summary: string
  portfolioUrl: string
  experiences: ResumeExperienceInput[]
  skills: ResumeSkillInput[]
}) {
  return request(`/api/resumes/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export async function deleteResume(id: number) {
  return request<boolean>(`/api/resumes/${id}`, {
    method: 'DELETE',
  })
}

export async function fetchResumeDetail(id: number) {
  return request<{ resume: ResumeOverview; experiences: ResumeExperienceInput[]; skills: ResumeSkillInput[] }>(
    `/api/resumes/${id}`
  )
}

export async function createJobApplication(payload: {
  jobId: number
  studentId: number
  resumeId: number
  coverLetter: string
}) {
  return request('/api/job-applications', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}
