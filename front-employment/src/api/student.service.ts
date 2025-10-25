import apiClient from './axios.config';

// ==================== 类型定义 ====================

// 学生信息（对应后端Student实体）
export interface StudentInfo {
  studentId?: number;
  fullName: string;
  gender: string;
  age: string;
  nation: string;
  idNumber: string;
  registeredResidence: string;
  education: string;
  graduationSchool: string;
  major: string;
  employmentIntention: string;
  personalProfile: string;
  socialPractice: string;
  awards: string;
  certificate: string;
  examineState?: string;
  recommend?: number;
  userId?: number;
  createTime?: string;
  updateTime?: string;
}

// 教育经历
export interface Education {
  id?: number;
  school: string;
  major: string;
  degree: string;
  gpa: string;
  startDate: string;
  endDate: string;
  description: string;
}

// 社会实践
export interface Practice {
  id?: number;
  organization: string;
  position: string;
  startDate: string;
  endDate: string;
  description: string;
}

// 获奖情况
export interface Award {
  id?: number;
  awardName: string;
  awardLevel: string;
  awardDate: string;
  issuer: string;
}

// 简历
export interface Resume {
  id?: number;
  title: string;
  name: string;
  gender: string;
  phone: string;
  email: string;
  jobIntention: string;
  advantage: string;
  skills: string[];
  isDefault: boolean;
  updateTime?: string;
  views?: number;
  tags?: string[];
}

// 就业意向
export interface JobIntention {
  id?: number;
  positions: string[];
  categories: string[];
  salaryMin: number;
  salaryMax: number;
  salaryNegotiable: boolean;
  cities: any[];
  acceptTransfer: boolean;
  workType: string;
  availableTime: string;
  companySize: string[];
  industries: string[];
  benefits: string[];
  remarks: string;
}

// 招聘信息
export interface Job {
  id: number;
  title: string;
  company: string;
  location: string;
  salary: string;
  experience: string;
  education: string;
  tags: string[];
  description?: string;
  requirements?: string[];
  benefits?: string[];
  logo?: string;
  isFavorite?: boolean;
}

// 面试信息
export interface Interview {
  id: number;
  company: string;
  position: string;
  round: string;
  type: string;
  time: string;
  location: string;
  status: string;
  interviewer?: string;
  phone?: string;
  notes?: string;
  materials?: string[];
  result?: string;
  feedback?: string;
}

// 投递记录
export interface Application {
  id: number;
  company: string;
  position: string;
  location: string;
  salary: string;
  applyTime: string;
  status: string;
  resumeName: string;
  logo?: string;
  logs?: Array<{
    time: string;
    content: string;
  }>;
}

// 统一响应格式
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// ==================== API 服务 ====================

class StudentService {
  // ========== 学生信息管理（对应后端Student实体） ==========
  
  /**
   * 获取当前登录学生信息
   */
  async getStudentInfo(): Promise<ApiResponse<StudentInfo>> {
    // 从localStorage获取用户ID
    const userStr = localStorage.getItem('user')
    if (!userStr) {
      throw new Error('请先登录')
    }
    const user = JSON.parse(userStr)
    const userId = user.userInfo?.userId || user.userId
    
    const response = await apiClient.get(`/student/user/${userId}`)
    return response.data
  }

  /**
   * 根据ID获取学生信息
   */
  async getStudentById(id: number): Promise<ApiResponse<StudentInfo>> {
    const response = await apiClient.get(`/student/${id}`)
    return response.data
  }

  /**
   * 更新学生信息
   */
  async updateStudentInfo(data: StudentInfo): Promise<ApiResponse<StudentInfo>> {
    const response = await apiClient.put('/student/update', data)
    return response.data
  }

  /**
   * 保存学生信息（新增或更新）
   */
  async saveStudentInfo(data: StudentInfo): Promise<ApiResponse<StudentInfo>> {
    // 确保userId已设置
    if (!data.userId) {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        const user = JSON.parse(userStr)
        data.userId = user.userInfo?.userId || user.userId
      }
    }
    
    const response = await apiClient.post('/student/save', data)
    return response.data
  }

  /**
   * 删除学生信息
   */
  async deleteStudentInfo(studentId: number): Promise<ApiResponse<boolean>> {
    const response = await apiClient.delete(`/student/${studentId}`)
    return response.data
  }

  // ========== 个人信息管理（兼容旧接口） ==========
  
  /**
   * 获取个人信息
   */
  async getPersonalInfo(): Promise<ApiResponse<StudentInfo>> {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      throw new Error('请先登录');
    }
    const user = JSON.parse(userStr);
    const userId = user.userInfo?.userId || user.userId;

    const response = await apiClient.get('/student/personal-info', {
      params: { userId }
    });
    return response.data;
  }

  /**
   * 更新个人信息
   */
  async updatePersonalInfo(data: StudentInfo): Promise<ApiResponse<StudentInfo>> {
    if (!data.userId) {
      const userStr = localStorage.getItem('user');
      if (userStr) {
        const user = JSON.parse(userStr);
        data.userId = user.userInfo?.userId || user.userId;
      }
    }

    const response = await apiClient.post('/student/personal-info', data);
    return response.data;
  }

  // ========== 教育经历 ==========
  
  /**
   * 获取教育经历列表
   */
  async getEducationList(): Promise<ApiResponse<Education[]>> {
    const response = await apiClient.get('/student/education');
    return response.data;
  }

  /**
   * 添加教育经历
   */
  async addEducation(data: Education): Promise<ApiResponse<Education>> {
    const response = await apiClient.post('/student/education', data);
    return response.data;
  }

  /**
   * 更新教育经历
   */
  async updateEducation(id: number, data: Education): Promise<ApiResponse<Education>> {
    const response = await apiClient.put(`/student/education/${id}`, data);
    return response.data;
  }

  /**
   * 删除教育经历
   */
  async deleteEducation(id: number): Promise<ApiResponse<void>> {
    const response = await apiClient.delete(`/student/education/${id}`);
    return response.data;
  }

  // ========== 社会实践 ==========
  
  /**
   * 获取社会实践列表
   */
  async getPracticeList(): Promise<ApiResponse<Practice[]>> {
    const response = await apiClient.get('/student/practice');
    return response.data;
  }

  /**
   * 添加社会实践
   */
  async addPractice(data: Practice): Promise<ApiResponse<Practice>> {
    const response = await apiClient.post('/student/practice', data);
    return response.data;
  }

  /**
   * 更新社会实践
   */
  async updatePractice(id: number, data: Practice): Promise<ApiResponse<Practice>> {
    const response = await apiClient.put(`/student/practice/${id}`, data);
    return response.data;
  }

  /**
   * 删除社会实践
   */
  async deletePractice(id: number): Promise<ApiResponse<void>> {
    const response = await apiClient.delete(`/student/practice/${id}`);
    return response.data;
  }

  // ========== 获奖情况 ==========
  
  /**
   * 获取获奖列表
   */
  async getAwardsList(): Promise<ApiResponse<Award[]>> {
    const response = await apiClient.get('/student/awards');
    return response.data;
  }

  /**
   * 添加获奖记录
   */
  async addAward(data: Award): Promise<ApiResponse<Award>> {
    const response = await apiClient.post('/student/awards', data);
    return response.data;
  }

  /**
   * 更新获奖记录
   */
  async updateAward(id: number, data: Award): Promise<ApiResponse<Award>> {
    const response = await apiClient.put(`/student/awards/${id}`, data);
    return response.data;
  }

  /**
   * 删除获奖记录
   */
  async deleteAward(id: number): Promise<ApiResponse<void>> {
    const response = await apiClient.delete(`/student/awards/${id}`);
    return response.data;
  }

  // ========== 简历管理 ==========
  
  /**
   * 获取简历列表
   */
  async getResumeList(): Promise<ApiResponse<Resume[]>> {
    const response = await apiClient.get('/student/resume');
    return response.data;
  }

  /**
   * 获取简历详情
   */
  async getResumeDetail(id: number): Promise<ApiResponse<Resume>> {
    const response = await apiClient.get(`/student/resume/${id}`);
    return response.data;
  }

  /**
   * 创建简历
   */
  async createResume(data: Resume): Promise<ApiResponse<Resume>> {
    const response = await apiClient.post('/student/resume', data);
    return response.data;
  }

  /**
   * 更新简历
   */
  async updateResume(id: number, data: Resume): Promise<ApiResponse<Resume>> {
    const response = await apiClient.put(`/student/resume/${id}`, data);
    return response.data;
  }

  /**
   * 删除简历
   */
  async deleteResume(id: number): Promise<ApiResponse<void>> {
    const response = await apiClient.delete(`/student/resume/${id}`);
    return response.data;
  }

  /**
   * 设置默认简历
   */
  async setDefaultResume(id: number): Promise<ApiResponse<void>> {
    const response = await apiClient.put(`/student/resume/${id}/default`);
    return response.data;
  }

  /**
   * 上传简历文件
   */
  async uploadResume(file: File): Promise<ApiResponse<string>> {
    const formData = new FormData();
    formData.append('file', file);
    const response = await apiClient.post('/student/resume/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    return response.data;
  }

  // ========== 就业意向 ==========
  
  /**
   * 获取就业意向
   */
  async getJobIntention(): Promise<ApiResponse<JobIntention>> {
    const response = await apiClient.get('/student/job-intention');
    return response.data;
  }

  /**
   * 更新就业意向
   */
  async updateJobIntention(data: JobIntention): Promise<ApiResponse<JobIntention>> {
    const response = await apiClient.post('/student/job-intention', data);
    return response.data;
  }

  // ========== 招聘信息 ==========
  
  /**
   * 搜索招聘信息
   */
  async searchJobs(params: {
    keyword?: string;
    city?: string;
    salary?: string;
    page?: number;
    pageSize?: number;
  }): Promise<ApiResponse<{
    list: Job[];
    total: number;
  }>> {
    const response = await apiClient.get('/student/jobs', { params });
    return response.data;
  }

  /**
   * 获取职位详情
   */
  async getJobDetail(id: number): Promise<ApiResponse<Job>> {
    const response = await apiClient.get(`/student/jobs/${id}`);
    return response.data;
  }

  /**
   * 收藏职位
   */
  async favoriteJob(jobId: number): Promise<ApiResponse<void>> {
    const response = await apiClient.post(`/student/jobs/${jobId}/favorite`);
    return response.data;
  }

  /**
   * 取消收藏
   */
  async unfavoriteJob(jobId: number): Promise<ApiResponse<void>> {
    const response = await apiClient.delete(`/student/jobs/${jobId}/favorite`);
    return response.data;
  }

  /**
   * 申请职位
   */
  async applyJob(jobId: number, resumeId: number): Promise<ApiResponse<void>> {
    const response = await apiClient.post(`/student/jobs/${jobId}/apply`, { resumeId });
    return response.data;
  }

  // ========== 面试管理 ==========
  
  /**
   * 获取面试列表
   */
  async getInterviewList(status?: string): Promise<ApiResponse<Interview[]>> {
    const response = await apiClient.get('/student/interviews', {
      params: { status }
    });
    return response.data;
  }

  /**
   * 获取面试详情
   */
  async getInterviewDetail(id: number): Promise<ApiResponse<Interview>> {
    const response = await apiClient.get(`/student/interviews/${id}`);
    return response.data;
  }

  /**
   * 确认面试
   */
  async confirmInterview(id: number): Promise<ApiResponse<void>> {
    const response = await apiClient.put(`/student/interviews/${id}/confirm`);
    return response.data;
  }

  /**
   * 取消面试
   */
  async cancelInterview(id: number): Promise<ApiResponse<void>> {
    const response = await apiClient.put(`/student/interviews/${id}/cancel`);
    return response.data;
  }

  // ========== 投递记录 ==========
  
  /**
   * 获取投递记录列表
   */
  async getApplicationList(status?: string): Promise<ApiResponse<Application[]>> {
    const response = await apiClient.get('/student/applications', {
      params: { status }
    });
    return response.data;
  }

  /**
   * 获取投递详情
   */
  async getApplicationDetail(id: number): Promise<ApiResponse<Application>> {
    const response = await apiClient.get(`/student/applications/${id}`);
    return response.data;
  }

  // ========== 统计数据 ==========
  
  /**
   * 获取学生工作台统计数据
   */
  async getDashboardStats(): Promise<ApiResponse<{
    applications: number;
    interviews: number;
    favorites: number;
  }>> {
    const response = await apiClient.get('/student/dashboard/stats');
    return response.data;
  }

  /**
   * 获取热门企业
   */
  async getHotCompanies(): Promise<ApiResponse<Array<{
    id: number;
    name: string;
    jobs: number;
    logo: string;
  }>>> {
    const response = await apiClient.get('/student/hot-companies');
    return response.data;
  }

  /**
   * 获取推荐职位
   */
  async getRecommendedJobs(): Promise<ApiResponse<Job[]>> {
    const response = await apiClient.get('/student/recommended-jobs');
    return response.data;
  }
}

// 导出单例
const studentService = new StudentService();
export default studentService;

