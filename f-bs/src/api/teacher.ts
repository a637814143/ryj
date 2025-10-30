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

export type TeacherRecord = {
  id: number
  userId: number
  department: string | null
  email: string | null
  phone: string | null
}

export type TeacherDashboardOverview = {
  totalGuidedStudents: number
  pendingApprovalCount: number
  activeInterviewCount: number
  collaborationCount: number
}

export type TeacherProfileInfo = {
  teacherId: number
  userId: number
  name: string
  department: string | null
  major?: string | null
  email: string | null
  phone: string | null
  biography: string | null
  focus: string | null
  avatarInitials: string | null
}

export type TeacherGuidedStudent = {
  studentId: number
  studentName: string
  major: string | null
  pendingRequestCount: number
  activeApplicationCount: number
  latestInterviewStatus: string | null
  latestGuidanceAt: string | null
  latestGuidanceNote: string | null
  employerNames: string[]
}

export type TeacherPendingApproval = {
  requestId: number
  studentId: number
  studentName: string
  major: string | null
  graduationYear: number | null
  submittedAt: string | null
  biography: string | null
}

export type TeacherEmployerCollaboration = {
  employerId: number
  companyName: string
  jobCount: number
  studentCount: number
  latestInteraction: string | null
  studentNames: string[]
}

export type TeacherGuidanceNote = {
  id: number
  studentId: number
  studentName: string
  note: string | null
  createdAt: string | null
}

export type TeacherStudentActivity = {
  applicationId: number
  studentId: number
  studentName: string
  jobId: number | null
  jobTitle: string | null
  employerName: string | null
  status: string | null
  appliedAt: string | null
}

export type TeacherDashboardResponse = {
  overview: TeacherDashboardOverview
  profile: TeacherProfileInfo
  guidedStudents: TeacherGuidedStudent[]
  pendingApprovals: TeacherPendingApproval[]
  employerCollaborations: TeacherEmployerCollaboration[]
  recentGuidanceNotes: TeacherGuidanceNote[]
  recentStudentActivities: TeacherStudentActivity[]
}

export async function getTeacherByUserId(userId: number): Promise<TeacherRecord> {
  return request<TeacherRecord>(`/api/teachers/by-user/${userId}`)
}

export async function getTeacherDashboard(teacherId: number): Promise<TeacherDashboardResponse> {
  return request<TeacherDashboardResponse>(`/api/teachers/${teacherId}/dashboard`)
}

type ReviewPayload = {
  reviewComment?: string
  reviewerId?: number
}

export async function approveProfileUpdate(teacherId: number, requestId: number, payload: ReviewPayload = {}) {
  return request<boolean>(`/api/teachers/${teacherId}/requests/${requestId}/approve`, {
    method: 'PUT',
    body: JSON.stringify(payload ?? {}),
  })
}

export async function rejectProfileUpdate(teacherId: number, requestId: number, payload: ReviewPayload) {
  return request<boolean>(`/api/teachers/${teacherId}/requests/${requestId}/reject`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

// 教师列表检索（按院系/专业筛选班主任候选）
export type TeacherSearchItem = {
  id: number
  userId: number
  name: string
  department: string | null
  major?: string | null
  email: string | null
}

export async function searchTeachers(params: { department?: string; major?: string; keyword?: string } = {}) {
  const searchParams = new URLSearchParams()
  if (params.department) searchParams.set('department', params.department)
  if (params.major) searchParams.set('major', params.major)
  if (params.keyword) searchParams.set('keyword', params.keyword)
  const qs = searchParams.toString()
  return request<TeacherSearchItem[]>(`/api/teachers${qs ? `?${qs}` : ''}`)
}

// 教师个人信息获取/更新
export type TeacherProfileUpsert = {
  name?: string
  department?: string | null
  major?: string | null
  email?: string | null
  phone?: string | null
  focus?: string | null
  biography?: string | null
}

export async function getTeacherProfile(teacherId: number) {
  return request<TeacherProfileInfo>(`/api/teachers/${teacherId}`)
}

export async function updateTeacherProfile(teacherId: number, payload: TeacherProfileUpsert) {
  return request<boolean>(`/api/teachers/${teacherId}` , {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}
