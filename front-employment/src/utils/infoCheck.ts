/**
 * 用户信息完整性检查工具
 */

import studentService from '@/api/student.service'

/**
 * 检查学生信息是否完善
 * @returns Promise<boolean> true表示信息完善，false表示信息不完善
 */
export async function checkStudentInfoComplete(): Promise<boolean> {
  try {
    const response = await studentService.getStudentInfo()
    
    if (response.code === 200 && response.data) {
      const info = response.data
      
      // 必填字段检查
      const requiredFields = [
        'fullName',      // 姓名
        'gender',        // 性别
        'age',           // 年龄
        'nation',        // 民族
        'idNumber',      // 身份证号
        'registeredResidence', // 户籍地
        'education',     // 学历
        'graduationSchool',    // 毕业学校
        'major',         // 专业
        'employmentIntention'  // 就业意向
      ]
      
      // 检查所有必填字段是否都已填写
      for (const field of requiredFields) {
        const value = info[field as keyof typeof info]
        if (!value || value === '' || value === null || value === undefined) {
          console.log(`字段 ${field} 未填写`)
          return false
        }
      }
      
      return true
    } else if (response.code === 404 || !response.data) {
      // 学生信息不存在
      return false
    }
    
    return false
  } catch (error: any) {
    console.error('检查学生信息失败:', error)
    // 如果是404，说明信息不存在
    if (error?.response?.status === 404) {
      return false
    }
    // 其他错误，保守处理，认为信息已完善，避免阻塞用户
    return true
  }
}

/**
 * 标记信息已完善
 */
export function markInfoCompleted() {
  localStorage.setItem('infoCompleted', 'true')
  localStorage.removeItem('infoSkipped')
}

/**
 * 标记信息已跳过
 */
export function markInfoSkipped() {
  localStorage.setItem('infoSkipped', 'true')
}

/**
 * 清除信息完善状态（用于退出登录时）
 */
export function clearInfoStatus() {
  localStorage.removeItem('infoCompleted')
  localStorage.removeItem('infoSkipped')
}

/**
 * 获取信息完善状态
 * @returns 'completed' | 'skipped' | 'incomplete'
 */
export function getInfoStatus(): 'completed' | 'skipped' | 'incomplete' {
  const completed = localStorage.getItem('infoCompleted')
  const skipped = localStorage.getItem('infoSkipped')
  
  if (completed === 'true') {
    return 'completed'
  } else if (skipped === 'true') {
    return 'skipped'
  } else {
    return 'incomplete'
  }
}

