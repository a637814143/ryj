<template>
  <div class="complete-info-container">
    <div class="welcome-section">
      <div class="welcome-icon">
        <el-icon :size="80" color="#667eea"><UserFilled /></el-icon>
      </div>
      <h1>欢迎加入高校就业服务平台！</h1>
      <p class="welcome-text">为了给您提供更好的就业服务，请完善您的个人信息</p>
      <el-steps :active="currentStep" align-center class="steps">
        <el-step title="基本信息" icon="User" />
        <el-step title="教育背景" icon="School" />
        <el-step title="就业意向" icon="Suitcase" />
      </el-steps>
    </div>

    <div class="form-section">
      <el-card class="form-card" shadow="hover">
        <!-- 步骤1：基本信息 -->
        <div v-show="currentStep === 0" class="step-content">
          <h2 class="step-title">
            <el-icon><User /></el-icon>
            基本信息
          </h2>
          <el-form
            ref="basicFormRef"
            :model="studentInfo"
            :rules="basicRules"
            label-width="120px"
            class="info-form"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="真实姓名" prop="fullName">
                  <el-input 
                    v-model="studentInfo.fullName" 
                    placeholder="请输入真实姓名"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><User /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="studentInfo.gender">
                    <el-radio label="男">男</el-radio>
                    <el-radio label="女">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="年龄" prop="age">
                  <el-input 
                    v-model="studentInfo.age" 
                    placeholder="请输入年龄"
                    clearable
                  >
                    <template #suffix>岁</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="民族" prop="nation">
                  <el-input 
                    v-model="studentInfo.nation" 
                    placeholder="如：汉族"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="身份证号" prop="idNumber">
                  <el-input 
                    v-model="studentInfo.idNumber" 
                    placeholder="请输入18位身份证号"
                    maxlength="18"
                    clearable
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="户籍地" prop="registeredResidence">
                  <el-input 
                    v-model="studentInfo.registeredResidence" 
                    placeholder="如：浙江省杭州市"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="个人简介" prop="personalProfile">
              <el-input
                v-model="studentInfo.personalProfile"
                type="textarea"
                :rows="4"
                placeholder="请简要介绍自己的特长、优势、性格等（选填）"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-form>
        </div>

        <!-- 步骤2：教育背景 -->
        <div v-show="currentStep === 1" class="step-content">
          <h2 class="step-title">
            <el-icon><School /></el-icon>
            教育背景
          </h2>
          <el-form
            ref="educationFormRef"
            :model="studentInfo"
            :rules="educationRules"
            label-width="120px"
            class="info-form"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="学历" prop="education">
                  <el-select 
                    v-model="studentInfo.education" 
                    placeholder="请选择最高学历"
                    style="width: 100%"
                  >
                    <el-option label="高中" value="高中" />
                    <el-option label="中专" value="中专" />
                    <el-option label="大专" value="大专" />
                    <el-option label="本科" value="本科" />
                    <el-option label="硕士" value="硕士" />
                    <el-option label="博士" value="博士" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="毕业学校" prop="graduationSchool">
                  <el-input 
                    v-model="studentInfo.graduationSchool" 
                    placeholder="请输入毕业学校全称"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="专业" prop="major">
              <el-input 
                v-model="studentInfo.major" 
                placeholder="请输入专业名称"
                clearable
              >
                <template #prefix>
                  <el-icon><Reading /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="证书情况">
              <el-input
                v-model="studentInfo.certificate"
                type="textarea"
                :rows="3"
                placeholder="请列举您持有的证书，如：英语四级、计算机二级等（选填）"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="获奖情况">
              <el-input
                v-model="studentInfo.awards"
                type="textarea"
                :rows="3"
                placeholder="请列举您获得的奖项（选填）"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </el-form>
        </div>

        <!-- 步骤3：就业意向 -->
        <div v-show="currentStep === 2" class="step-content">
          <h2 class="step-title">
            <el-icon><Suitcase /></el-icon>
            就业意向
          </h2>
          <el-form
            ref="intentionFormRef"
            :model="studentInfo"
            :rules="intentionRules"
            label-width="120px"
            class="info-form"
          >
            <el-form-item label="就业意向" prop="employmentIntention">
              <el-input 
                v-model="studentInfo.employmentIntention" 
                placeholder="如：前端开发、市场营销等"
                clearable
              >
                <template #prefix>
                  <el-icon><Suitcase /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="社会实践">
              <el-input
                v-model="studentInfo.socialPractice"
                type="textarea"
                :rows="4"
                placeholder="请描述您的社会实践、实习或工作经历（选填）"
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>

            <el-alert
              title="提示"
              type="success"
              :closable="false"
              class="complete-tip"
            >
              完善信息后，系统将为您智能推荐合适的职位！
            </el-alert>
          </el-form>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <el-button 
            v-if="currentStep > 0" 
            @click="prevStep"
            :icon="ArrowLeft"
          >
            上一步
          </el-button>
          <el-button 
            v-if="currentStep < 2" 
            type="primary" 
            @click="nextStep"
            :icon="ArrowRight"
          >
            下一步
          </el-button>
          <el-button 
            v-if="currentStep === 2" 
            type="primary" 
            @click="submitInfo"
            :loading="submitting"
            :icon="Check"
          >
            完成
          </el-button>
          <el-button 
            v-if="currentStep === 2"
            text
            @click="skipComplete"
          >
            跳过
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import {
  UserFilled, User, School, Suitcase, Reading,
  ArrowLeft, ArrowRight, Check
} from '@element-plus/icons-vue'
import studentService, { type StudentInfo } from '@/api/student.service'

const router = useRouter()

// 当前步骤
const currentStep = ref(0)

// 提交状态
const submitting = ref(false)

// 表单引用
const basicFormRef = ref<FormInstance>()
const educationFormRef = ref<FormInstance>()
const intentionFormRef = ref<FormInstance>()

// 学生信息
const studentInfo = reactive<StudentInfo>({
  fullName: '',
  gender: '男',
  age: '',
  nation: '汉族',
  idNumber: '',
  registeredResidence: '',
  education: '',
  graduationSchool: '',
  major: '',
  employmentIntention: '',
  personalProfile: '',
  socialPractice: '',
  awards: '',
  certificate: '',
  examineState: '已通过'
})

// 基本信息验证规则
const basicRules = reactive<FormRules>({
  fullName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' },
    { pattern: /^([1-9][0-9]?|100)$/, message: '请输入有效的年龄（1-100）', trigger: 'blur' }
  ],
  nation: [
    { required: true, message: '请输入民族', trigger: 'blur' }
  ],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { 
      pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, 
      message: '请输入正确的身份证号', 
      trigger: 'blur' 
    }
  ],
  registeredResidence: [
    { required: true, message: '请输入户籍地', trigger: 'blur' }
  ]
})

// 教育背景验证规则
const educationRules = reactive<FormRules>({
  education: [
    { required: true, message: '请选择学历', trigger: 'change' }
  ],
  graduationSchool: [
    { required: true, message: '请输入毕业学校', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ]
})

// 就业意向验证规则
const intentionRules = reactive<FormRules>({
  employmentIntention: [
    { required: true, message: '请输入就业意向', trigger: 'blur' }
  ]
})

// 下一步
const nextStep = async () => {
  let formRef: FormInstance | undefined
  
  // 根据当前步骤选择对应的表单
  if (currentStep.value === 0) {
    formRef = basicFormRef.value
  } else if (currentStep.value === 1) {
    formRef = educationFormRef.value
  }
  
  if (!formRef) return
  
  try {
    await formRef.validate()
    currentStep.value++
  } catch (error) {
    ElMessage.warning('请填写完整信息')
  }
}

// 上一步
const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

// 提交信息
const submitInfo = async () => {
  if (!intentionFormRef.value) return
  
  try {
    await intentionFormRef.value.validate()
    submitting.value = true
    
    // 调用API保存学生信息
    const response = await studentService.saveStudentInfo(studentInfo)
    
    if (response.code === 200) {
      ElMessage.success('信息完善成功！')
      // 标记信息已完善
      localStorage.setItem('infoCompleted', 'true')
      // 跳转到学生dashboard
      setTimeout(() => {
        router.push('/student/dashboard')
      }, 1000)
    } else {
      ElMessage.error(response.message || '提交失败')
    }
  } catch (error: any) {
    console.error('提交失败:', error)
    if (error?.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
    } else {
      ElMessage.error(error?.response?.data?.message || '提交失败，请检查网络连接')
    }
  } finally {
    submitting.value = false
  }
}

// 跳过完善
const skipComplete = () => {
  ElMessage.warning('建议完善信息以获得更好的服务')
  // 标记为已跳过（稍后可以在个人中心完善）
  localStorage.setItem('infoSkipped', 'true')
  router.push('/student/dashboard')
}
</script>

<style scoped>
.complete-info-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
}

.welcome-section {
  text-align: center;
  color: white;
  margin-bottom: 40px;
  max-width: 800px;
}

.welcome-icon {
  margin-bottom: 24px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.welcome-section h1 {
  font-size: 32px;
  margin: 0 0 16px 0;
  font-weight: 600;
}

.welcome-text {
  font-size: 16px;
  margin-bottom: 32px;
  opacity: 0.95;
}

.steps {
  background: rgba(255, 255, 255, 0.1);
  padding: 24px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

:deep(.el-step__title) {
  color: rgba(255, 255, 255, 0.8) !important;
}

:deep(.el-step__title.is-process),
:deep(.el-step__title.is-finish) {
  color: white !important;
  font-weight: 600;
}

:deep(.el-step__icon) {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}

:deep(.el-step__icon.is-process),
:deep(.el-step__icon.is-finish) {
  background: white;
  border-color: white;
  color: #667eea;
}

:deep(.el-step__line) {
  background: rgba(255, 255, 255, 0.3);
}

.form-section {
  width: 100%;
  max-width: 900px;
}

.form-card {
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.step-content {
  min-height: 400px;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.step-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 32px 0;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f0f0;
}

.step-title .el-icon {
  font-size: 28px;
  color: #667eea;
}

.info-form {
  margin-bottom: 32px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input__prefix),
:deep(.el-input__suffix) {
  color: #909399;
}

.complete-tip {
  margin-top: 24px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.form-actions .el-button {
  min-width: 120px;
  height: 40px;
  font-size: 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .complete-info-container {
    padding: 20px 10px;
  }

  .welcome-section h1 {
    font-size: 24px;
  }

  .welcome-text {
    font-size: 14px;
  }

  .step-title {
    font-size: 20px;
  }

  .form-card {
    padding: 16px;
  }

  .steps {
    padding: 16px;
  }

  :deep(.el-step__title) {
    font-size: 12px;
  }
}
</style>

