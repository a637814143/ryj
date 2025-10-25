import type {
  DashboardSummary,
  Employer,
  Interview,
  JobApplication,
  JobPosting,
  Resume,
  Student,
  Teacher,
} from '@/types'

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080/api'

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(init?.headers ?? {}),
    },
    ...init,
  })

  if (!response.ok) {
    const text = await response.text()
    throw new Error(text || `请求失败: ${response.status}`)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return (await response.json()) as T
}

export const api = {
  getDashboardSummary(): Promise<DashboardSummary> {
    return request('/dashboard/summary')
  },
  getStudents(): Promise<Student[]> {
    return request('/students')
  },
  getStudent(id: number): Promise<Student> {
    return request(`/students/${id}`)
  },
  getStudentResumes(id: number): Promise<Resume[]> {
    return request(`/students/${id}/resumes`)
  },
  getStudentApplications(id: number): Promise<JobApplication[]> {
    return request(`/students/${id}/applications`)
  },
  createStudent(payload: Partial<Student>): Promise<Student> {
    return request('/students', {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  updateStudent(id: number, payload: Partial<Student>): Promise<Student> {
    return request(`/students/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },
  updateIntention(id: number, payload: Student['employmentIntention']): Promise<Student['employmentIntention']> {
    return request(`/students/${id}/intention`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  },
  createResume(studentId: number, payload: Partial<Resume>): Promise<Resume> {
    return request(`/students/${studentId}/resumes`, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  getEmployers(): Promise<Employer[]> {
    return request('/employers')
  },
  getEmployerJobs(id: number): Promise<JobPosting[]> {
    return request(`/employers/${id}/jobs`)
  },
  getJobs(params?: { keyword?: string; location?: string; workType?: string }): Promise<JobPosting[]> {
    const query = new URLSearchParams()
    if (params?.keyword) query.set('keyword', params.keyword)
    if (params?.location) query.set('location', params.location)
    if (params?.workType) query.set('workType', params.workType)
    const suffix = query.toString() ? `?${query.toString()}` : ''
    return request(`/jobs${suffix}`)
  },
  getJob(id: number): Promise<JobPosting> {
    return request(`/jobs/${id}`)
  },
  getJobApplications(id: number): Promise<JobApplication[]> {
    return request(`/jobs/${id}/applications`)
  },
  applyForJob(id: number, payload: { studentId: number; resumeId: number; coverLetter?: string }): Promise<JobApplication> {
    return request(`/jobs/${id}/apply`, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  scheduleInterview(jobId: number, payload: { applicationId: number; scheduledTime?: string; location?: string; meetingLink?: string }): Promise<Interview> {
    return request(`/jobs/${jobId}/interviews`, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  getJobInterviews(jobId: number): Promise<Interview[]> {
    return request(`/jobs/${jobId}/interviews`)
  },
  updateInterview(interviewId: number, payload: { status?: string; feedback?: string }): Promise<Interview> {
    return request(`/interviews/${interviewId}`, {
      method: 'PATCH',
      body: JSON.stringify(payload),
    })
  },
  updateApplicationStatus(applicationId: number, status: string): Promise<JobApplication> {
    return request(`/applications/${applicationId}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status }),
    })
  },
  getTeachers(): Promise<Teacher[]> {
    return request('/teachers')
  },
  addGuidance(teacherId: number, payload: { studentId: number; note: string }): Promise<Teacher> {
    return request(`/teachers/${teacherId}/guidance`, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
}
