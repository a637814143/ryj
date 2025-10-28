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

export type JobPostingWorkType = 'FULL_TIME' | 'PART_TIME' | 'INTERNSHIP' | 'REMOTE'
export type JobPostingStatus = 'OPEN' | 'CLOSED' | 'DRAFT'
export type JobApplicationStatus = 'SUBMITTED' | 'REVIEWING' | 'INTERVIEW' | 'OFFERED' | 'REJECTED'
export type InterviewStatus = 'SCHEDULED' | 'COMPLETED' | 'CANCELLED'
export type EmploymentIntentionWorkType = 'FULL_TIME' | 'PART_TIME' | 'INTERNSHIP' | 'FLEXIBLE'

export interface EmployerProfile {
  id: number
  userId: number
  companyName: string
  contactPerson: string | null
  contactEmail: string | null
  contactPhone: string | null
  description: string | null
  website: string | null
}

export interface EmployerCompanyProfile {
  id: number | null
  companyName: string | null
  contactPerson: string | null
  contactEmail: string | null
  contactPhone: string | null
  description: string | null
  website: string | null
}

export interface EmployerModuleInfo {
  key: string
  name: string
  description: string
  capabilities: string[]
}

export interface EmployerJobOverview {
  id: number
  title: string
  status: JobPostingStatus | null
  workType: JobPostingWorkType | null
  location: string | null
  salaryRange: string | null
  publishedDate: string | null
  closingDate: string | null
  applicationCount: number
}

export interface EmployerApplicationOverview {
  id: number
  jobId: number | null
  jobTitle: string | null
  studentId: number | null
  candidateName: string | null
  status: JobApplicationStatus
  appliedAt: string | null
  resumeId: number | null
}

export interface EmployerInterviewOverview {
  id: number
  jobId: number | null
  jobTitle: string | null
  applicationId: number | null
  candidateName: string | null
  status: InterviewStatus
  scheduledTime: string | null
  location: string | null
  meetingLink: string | null
  feedback: string | null
}

export interface EmployerTalentSummary {
  totalCandidates: number
  filteredCandidates: number
  pendingReviewCount: number
  interviewingCount: number
  offerCount: number
}

export interface EmployerTalentCandidate {
  studentId: number | null
  latestResumeId: number | null
  candidateName: string | null
  email: string | null
  phone: string | null
  major: string | null
  graduationYear: number | null
  expectedPosition: string | null
  expectedWorkType: EmploymentIntentionWorkType | null
  intentionCities: string[]
  latestStatus: JobApplicationStatus | null
  latestInterviewStatus: InterviewStatus | null
  lastAppliedAt: string | null
  latestJobTitle: string | null
  applicationCount: number
  interviewCount: number
}

export interface EmployerTalentResponse {
  summary: EmployerTalentSummary
  candidates: EmployerTalentCandidate[]
}

export interface EmployerTalentQuery {
  keyword?: string
  status?: JobApplicationStatus
  interviewStatus?: InterviewStatus
}

export interface EmployerDashboardResponse {
  header: {
    companyName: string
    totalJobCount: number
    openJobCount: number
    activeApplicationCount: number
    scheduledInterviewCount: number
    talentPoolSize: number
  }
  companyProfile: EmployerCompanyProfile | null
  modules: EmployerModuleInfo[]
  jobs: EmployerJobOverview[]
  applications: EmployerApplicationOverview[]
  interviews: EmployerInterviewOverview[]
}

export interface EmployerJobRecord {
  id: number
  employerId: number
  title: string
  description: string | null
  salaryRange: string | null
  location: string | null
  workType: JobPostingWorkType | null
  status: JobPostingStatus | null
  publishedDate: string | null
  closingDate: string | null
}

export interface EmployerJobDetail {
  job: EmployerJobRecord
  requirements: string[]
  applicationCount: number
}

export interface EmployerProfileRequestPayload {
  companyName: string
  contactPerson?: string | null
  contactEmail?: string | null
  contactPhone?: string | null
  description?: string | null
  website?: string | null
}

export interface EmployerJobRequestPayload {
  title: string
  description?: string | null
  salaryRange?: string | null
  location?: string | null
  workType?: JobPostingWorkType | null
  status?: JobPostingStatus | null
  closingDate?: string | null
  requirements?: string[] | null
}

export interface EmployerApplicationStatusPayload {
  status: JobApplicationStatus
}

export interface EmployerInterviewRequestPayload {
  jobId: number
  applicationId: number
  scheduledTime: string
  location?: string | null
  meetingLink?: string | null
  status?: InterviewStatus | null
  feedback?: string | null
}

export async function fetchEmployerDashboard(userId: number): Promise<EmployerDashboardResponse> {
  return request(`/api/employer-module/overview?userId=${userId}`)
}

export async function fetchEmployerProfile(userId: number): Promise<EmployerProfile | null> {
  return request(`/api/employer-module/profile?userId=${userId}`)
}

export async function saveEmployerProfile(userId: number, payload: EmployerProfileRequestPayload): Promise<EmployerProfile> {
  return request(`/api/employer-module/profile?userId=${userId}`, {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function fetchEmployerJobs(userId: number): Promise<EmployerJobDetail[]> {
  return request(`/api/employer-module/jobs?userId=${userId}`)
}

export async function createEmployerJob(userId: number, payload: EmployerJobRequestPayload): Promise<EmployerJobDetail> {
  return request(`/api/employer-module/jobs?userId=${userId}`, {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function updateEmployerJob(
  userId: number,
  jobId: number,
  payload: EmployerJobRequestPayload,
): Promise<EmployerJobDetail> {
  return request(`/api/employer-module/jobs/${jobId}?userId=${userId}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export async function deleteEmployerJob(userId: number, jobId: number): Promise<boolean> {
  return request(`/api/employer-module/jobs/${jobId}?userId=${userId}`, {
    method: 'DELETE',
  })
}

export async function fetchEmployerApplications(
  userId: number,
  status?: JobApplicationStatus,
): Promise<EmployerApplicationOverview[]> {
  const query = status ? `&status=${status}` : ''
  return request(`/api/employer-module/applications?userId=${userId}${query}`)
}

export async function updateEmployerApplicationStatus(
  userId: number,
  applicationId: number,
  payload: EmployerApplicationStatusPayload,
): Promise<boolean> {
  return request(`/api/employer-module/applications/${applicationId}/status?userId=${userId}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export async function fetchEmployerInterviews(userId: number): Promise<EmployerInterviewOverview[]> {
  return request(`/api/employer-module/interviews?userId=${userId}`)
}

export async function createEmployerInterview(
  userId: number,
  payload: EmployerInterviewRequestPayload,
): Promise<EmployerInterviewOverview> {
  return request(`/api/employer-module/interviews?userId=${userId}`, {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function updateEmployerInterview(
  userId: number,
  interviewId: number,
  payload: EmployerInterviewRequestPayload,
): Promise<EmployerInterviewOverview> {
  return request(`/api/employer-module/interviews/${interviewId}?userId=${userId}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export async function deleteEmployerInterview(userId: number, interviewId: number): Promise<boolean> {
  return request(`/api/employer-module/interviews/${interviewId}?userId=${userId}`, {
    method: 'DELETE',
  })
}

export async function fetchEmployerTalent(
  userId: number,
  query: EmployerTalentQuery = {},
): Promise<EmployerTalentResponse> {
  const params = new URLSearchParams({ userId: String(userId) })
  if (query.keyword) {
    params.append('keyword', query.keyword)
  }
  if (query.status) {
    params.append('status', query.status)
  }
  if (query.interviewStatus) {
    params.append('interviewStatus', query.interviewStatus)
  }
  return request(`/api/employer-module/talent?${params.toString()}`)
}
