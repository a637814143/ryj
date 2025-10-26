// 就业意向相关API

const API_BASE_URL = 'http://localhost:8080/api'

export interface EmploymentIntentionRequest {
  expectedPosition: string
  salaryRange?: string
  workType: 'FULL_TIME' | 'PART_TIME' | 'INTERNSHIP' | 'FLEXIBLE'
  cities?: string[]
  notes?: string
}

export interface EmploymentIntentionResponse {
  id: number
  studentId: number
  expectedPosition: string
  salaryRange: string | null
  workType: string
  cities: string[]
  notes: string | null
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

/**
 * 保存或更新就业意向
 */
export async function saveEmploymentIntention(
  studentId: number,
  data: EmploymentIntentionRequest
): Promise<ApiResponse<EmploymentIntentionResponse>> {
  const response = await fetch(`${API_BASE_URL}/employment-intention/${studentId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`)
  }

  return await response.json()
}

/**
 * 获取学生的就业意向
 */
export async function getEmploymentIntention(
  studentId: number
): Promise<ApiResponse<EmploymentIntentionResponse | null>> {
  const response = await fetch(`${API_BASE_URL}/employment-intention/${studentId}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`)
  }

  return await response.json()
}

/**
 * 删除就业意向
 */
export async function deleteEmploymentIntention(
  studentId: number
): Promise<ApiResponse<void>> {
  const response = await fetch(`${API_BASE_URL}/employment-intention/${studentId}`, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`)
  }

  return await response.json()
}

