<template>
  <div class="personal-info-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <el-icon class="header-icon"><User /></el-icon>
        <div>
          <h2>个人信息管理</h2>
          <p>完善您的个人信息，提升就业竞争力</p>
        </div>
      </div>
      <div class="header-right">
        <el-tag 
          :type="examineStateType" 
          size="large"
          effect="dark"
        >
          {{ studentInfo.examineState || '未提交' }}
        </el-tag>
        <el-button 
          v-if="!isEditing" 
          type="primary" 
          :icon="Edit"
          @click="handleEdit"
        >
          编辑信息
        </el-button>
        <template v-else>
          <el-button @click="handleCancel">取消</el-button>
          <el-button 
            type="primary" 
            :icon="Check"
            :loading="saving"
            @click="handleSave"
          >
            保存并提交审核
          </el-button>
        </template>
      </div>
    </div>

    <!-- 审核状态提示 -->
    <el-alert
      v-if="studentInfo.examineState === '待审核'"
      title="审核中"
      type="warning"
      description="您的信息正在审核中，请耐心等待管理员审核。"
      :closable="false"
      show-icon
      class="status-alert"
    />
    
    <el-alert
      v-else-if="studentInfo.examineState === '未通过'"
      title="审核未通过"
      type="error"
      description="您的信息审核未通过，请修改后重新提交。"
      :closable="false"
      show-icon
      class="status-alert"
    />

    <!-- 表单内容 -->
    <el-form
      ref="formRef"
      :model="studentInfo"
      :rules="formRules"
      label-width="120px"
      :disabled="!isEditing"
      class="info-form"
    >
      <!-- 基本信息 -->
      <el-card class="form-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><User /></el-icon>
            <span>基本信息</span>
            <el-tag size="small" type="danger">必填</el-tag>
          </div>
        </template>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="姓名" prop="fullName">
              <el-input 
                v-model="studentInfo.fullName" 
                placeholder="请输入真实姓名"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="studentInfo.gender">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="年龄" prop="age">
              <el-input 
                v-model="studentInfo.age" 
                placeholder="请输入年龄"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="民族" prop="nation">
              <el-input 
                v-model="studentInfo.nation" 
                placeholder="请输入民族"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="16">
            <el-form-item label="身份证号" prop="idNumber">
              <el-input 
                v-model="studentInfo.idNumber" 
                placeholder="请输入18位身份证号"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="户籍地" prop="registeredResidence">
              <el-input 
                v-model="studentInfo.registeredResidence" 
                placeholder="请输入户籍所在地（如：浙江省杭州市西湖区）"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="个人简介">
              <el-input 
                v-model="studentInfo.personalProfile"
                type="textarea"
                :rows="4"
                placeholder="简要介绍您的特长、优势、性格特点等"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 教育背景 -->
      <el-card class="form-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><School /></el-icon>
            <span>教育背景</span>
            <el-tag size="small" type="danger">必填</el-tag>
          </div>
        </template>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="学历" prop="education">
              <el-select 
                v-model="studentInfo.education" 
                placeholder="请选择学历"
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

          <el-col :span="8">
            <el-form-item label="毕业学校" prop="graduationSchool">
              <el-input 
                v-model="studentInfo.graduationSchool" 
                placeholder="请输入毕业院校"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="专业" prop="major">
              <el-input 
                v-model="studentInfo.major" 
                placeholder="请输入所学专业"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 就业意向 -->
      <el-card class="form-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><Briefcase /></el-icon>
            <span>就业意向</span>
            <el-tag size="small" type="danger">必填</el-tag>
          </div>
        </template>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="期望职位" prop="employmentIntention">
              <el-input 
                v-model="studentInfo.employmentIntention" 
                placeholder="请输入期望从事的职位（如：前端开发工程师、市场专员等）"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 证书与获奖 -->
      <el-card class="form-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><Trophy /></el-icon>
            <span>证书与获奖</span>
            <el-tag size="small" type="info">选填</el-tag>
          </div>
        </template>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="证书">
              <el-input 
                v-model="studentInfo.certificate"
                type="textarea"
                :rows="3"
                placeholder="请输入您获得的证书（如：英语四六级、计算机等级证书、职业资格证等）"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="获奖情况">
              <el-input 
                v-model="studentInfo.awards"
                type="textarea"
                :rows="3"
                placeholder="请输入您的获奖情况（如：奖学金、竞赛获奖、荣誉称号等）"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 社会实践 -->
      <el-card class="form-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><Suitcase /></el-icon>
            <span>社会实践</span>
            <el-tag size="small" type="info">选填</el-tag>
          </div>
        </template>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="实践经历">
              <el-input 
                v-model="studentInfo.socialPractice"
                type="textarea"
                :rows="5"
                placeholder="请描述您的社会实践、实习或工作经历，包括时间、单位、岗位、主要工作内容等"
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { 
  User, Edit, Check, School, Trophy, Suitcase, Briefcase
} from '@element-plus/icons-vue'
import studentService, { type StudentInfo } from '@/api/student.service'

// 状态管理
const loading = ref(false)
const saving = ref(false)
const isEditing = ref(false)

// 表单引用
const formRef = ref<FormInstance>()

// 学生信息
const studentInfo = reactive<StudentInfo>({
  studentId: undefined,
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
  examineState: '未提交',
  userId: undefined
})

const storedUser = localStorage.getItem('user')
if (storedUser) {
  const user = JSON.parse(storedUser)
  studentInfo.userId = user.userInfo?.userId || user.userId
}

// 备份原始数据
let originalInfo: StudentInfo = {} as StudentInfo

// 表单验证规则
const formRules = reactive<FormRules>({
  fullName: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
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
  ],
  education: [
    { required: true, message: '请选择学历', trigger: 'change' }
  ],
  graduationSchool: [
    { required: true, message: '请输入毕业学校', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  employmentIntention: [
    { required: true, message: '请输入就业意向', trigger: 'blur' }
  ]
})

// 审核状态类型
const examineStateType = computed(() => {
  const stateMap: Record<string, any> = {
    '已通过': 'success',
    '待审核': 'warning',
    '未通过': 'danger',
    '未提交': 'info'
  }
  return stateMap[studentInfo.examineState || '未提交'] || 'info'
})

// 加载学生信息
const loadStudentInfo = async () => {
  loading.value = true
  try {
    const response = await studentService.getPersonalInfo()
    if (response.code === 200) {
      if (response.data) {
        Object.assign(studentInfo, response.data)
        originalInfo = JSON.parse(JSON.stringify(studentInfo))
        isEditing.value = false
      } else {
        const userStr = localStorage.getItem('user')
        if (userStr) {
          const user = JSON.parse(userStr)
          studentInfo.userId = user.userInfo?.userId || user.userId
        }
        originalInfo = JSON.parse(JSON.stringify(studentInfo))
        ElMessage.info('暂无个人信息，请点击"编辑信息"按钮添加')
        isEditing.value = true
      }
    } else {
      ElMessage.error(response.message || '加载学生信息失败')
    }
  } catch (error: any) {
    console.error('加载学生信息失败:', error)
    ElMessage.error(error?.response?.data?.message || '加载学生信息失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 进入编辑模式
const handleEdit = () => {
  // 如果正在审核中，提示用户
  if (studentInfo.examineState === '待审核') {
    ElMessageBox.confirm(
      '您的信息正在审核中，修改后需要重新提交审核，是否继续？',
      '提示',
      {
        confirmButtonText: '继续编辑',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      originalInfo = JSON.parse(JSON.stringify(studentInfo))
      isEditing.value = true
    }).catch(() => {
      // 用户取消
    })
  } else {
    originalInfo = JSON.parse(JSON.stringify(studentInfo))
    isEditing.value = true
  }
}

// 取消编辑
const handleCancel = () => {
  Object.assign(studentInfo, originalInfo)
  isEditing.value = false
  formRef.value?.clearValidate()
}

// 保存并提交审核
const handleSave = async () => {
  if (!formRef.value) return
  
  try {
    // 验证表单
    await formRef.value.validate()
    
    // 二次确认
    await ElMessageBox.confirm(
      '保存后将提交给管理员审核，确认提交吗？',
      '确认提交',
      {
        confirmButtonText: '确认提交',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    saving.value = true
    
    // 设置审核状态为待审核
    studentInfo.examineState = '待审核'
    
    // 调用保存接口
    const response = await studentService.updatePersonalInfo(studentInfo)
    
    if (response.code === 200) {
      ElMessage.success('保存成功！已提交审核，请等待管理员审核')
      isEditing.value = false
      
      // 更新本地数据
      if (response.data) {
        Object.assign(studentInfo, response.data)
        originalInfo = JSON.parse(JSON.stringify(response.data))
      }
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error: any) {
    if (error === 'cancel') {
      // 用户取消
      return
    }
    if (error instanceof Error) {
      ElMessage.warning('请填写完整的必填信息')
    } else {
      console.error('保存失败:', error)
      ElMessage.error(error?.response?.data?.message || '保存失败，请稍后重试')
    }
  } finally {
    saving.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadStudentInfo()
})
</script>

<style scoped>
.personal-info-container {
  width: 100%;
  min-height: 100%;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  color: #fff;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  font-size: 48px;
  color: rgba(255, 255, 255, 0.9);
}

.header-left h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.header-left p {
  margin: 4px 0 0 0;
  font-size: 14px;
  opacity: 0.9;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 状态提示 */
.status-alert {
  margin-bottom: 20px;
  border-radius: 8px;
}

/* 表单样式 */
.info-form {
  margin-bottom: 20px;
}

.form-card {
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.form-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

:deep(.form-card .el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-bottom: none;
  padding: 16px 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.card-header .el-icon {
  font-size: 20px;
}

:deep(.form-card .el-card__body) {
  padding: 24px;
}

/* 表单项样式 */
:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
}

:deep(.el-input__inner) {
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  border-radius: 8px;
}

:deep(.el-textarea__inner) {
  font-size: 14px;
  border-radius: 8px;
  line-height: 1.6;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-radio-group) {
  display: flex;
  gap: 24px;
}

:deep(.el-radio) {
  margin-right: 0;
}

:deep(.el-radio__label) {
  font-size: 14px;
}

/* 禁用状态样式 */
:deep(.el-form-item.is-disabled .el-input__inner),
:deep(.el-form-item.is-disabled .el-textarea__inner) {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
  color: #606266;
  cursor: not-allowed;
}

:deep(.el-form-item.is-disabled .el-radio__input.is-disabled .el-radio__inner) {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
  cursor: not-allowed;
}

:deep(.el-form-item.is-disabled .el-radio__label) {
  color: #c0c4cc;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .personal-info-container {
    padding: 12px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-right {
    width: 100%;
    justify-content: space-between;
  }

  .header-icon {
    font-size: 36px;
  }

  .header-left h2 {
    font-size: 20px;
  }

  :deep(.el-col) {
    max-width: 100%;
  }
}
</style>

