export interface EmploymentIntention {
  expectedPosition?: string
  salaryRange?: string
  preferredCities?: string[]
  workType?: string
  notes?: string
}

export interface Resume {
  id: number
  studentId: number
  title: string
  summary?: string
  skills?: string[]
  experiences?: string[]
  attachments?: string[]
  portfolioUrl?: string
}

export type ApplicationStatus = 'SUBMITTED' | 'REVIEWING' | 'INTERVIEW' | 'OFFERED' | 'REJECTED'
export type InterviewStatus = 'SCHEDULED' | 'COMPLETED' | 'CANCELLED'
export type JobStatus = 'OPEN' | 'CLOSED' | 'DRAFT'

export interface JobApplication {
  id: number
  jobId: number
  studentId: number
  resumeId: number
  status: ApplicationStatus
  coverLetter?: string
  appliedAt: string
}

export interface Interview {
  id: number
  jobId: number
  applicationId: number
  scheduledTime: string
  location?: string
  meetingLink?: string
  status: InterviewStatus
  feedback?: string
}

export interface Student {
  id: number
  name: string
  gender?: string
  age?: number
  major?: string
  email?: string
  phone?: string
  biography?: string
  educationHistory?: string[]
  practiceExperience?: string[]
  awards?: string[]
  employmentIntention?: EmploymentIntention
}

export interface Employer {
  id: number
  name: string
  contactPerson?: string
  contactEmail?: string
  contactPhone?: string
  description?: string
  jobIds?: number[]
}

export interface JobPosting {
  id: number
  employerId: number
  title: string
  description?: string
  salaryRange?: string
  location?: string
  workType?: string
  requirements?: string[]
  status: JobStatus
  publishedDate: string
}

export interface Teacher {
  id: number
  name: string
  department?: string
  email?: string
  phone?: string
  guidanceNotes: Record<string, string>
}

export interface DashboardSummary {
  students: number
  employers: number
  teachers: number
  openJobs: number
  pendingApplications: number
  scheduledInterviews: number
}
