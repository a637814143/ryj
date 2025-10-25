<template>
  <div class="resume-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <el-icon class="header-icon">📄</el-icon>
        <div>
          <h2>简历管理</h2>
          <p>创建和管理您的个人简历，提升求职成功率</p>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Plus" size="large" @click="createResume">
          创建新简历
        </el-button>
      </div>
    </div>

    <div class="resume-content">
      <!-- 简历列表 -->
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card shadow="hover" class="resume-list-card">
            <template #header>
              <div class="card-header">
                <span>我的简历</span>
              </div>
            </template>

            <div v-if="resumeList.length > 0" class="resume-list">
              <div
                v-for="resume in resumeList"
                :key="resume.id"
                class="resume-item"
                :class="{ active: currentResume?.id === resume.id }"
                @click="selectResume(resume)"
              >
                <div class="resume-info">
                  <div class="resume-header">
                    <h3>{{ resume.title }}</h3>
                    <el-tag v-if="resume.isDefault" type="success" size="small">默认</el-tag>
                  </div>
                  <div class="resume-meta">
                    <span>📅 更新时间：{{ resume.updateTime }}</span>
                    <span>👁️ 浏览次数：{{ resume.views }}</span>
                  </div>
                  <div class="resume-tags">
                    <el-tag v-for="tag in resume.tags" :key="tag" size="small">{{ tag }}</el-tag>
                  </div>
                </div>
                <div class="resume-actions">
                  <el-button size="small" :icon="View" @click.stop="previewResume(resume)">预览</el-button>
                  <el-button size="small" :icon="Edit" @click.stop="editResume(resume)">编辑</el-button>
                  <el-button size="small" :icon="Download" @click.stop="downloadResume(resume)">下载</el-button>
                  <el-dropdown @command="handleResumeCommand($event, resume)">
                    <el-button size="small" :icon="More" />
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="setDefault">设为默认</el-dropdown-item>
                        <el-dropdown-item command="duplicate">复制</el-dropdown-item>
                        <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
            </div>

            <el-empty v-else description="还没有简历，快去创建一份吧！">
              <el-button type="primary" @click="createResume">创建简历</el-button>
            </el-empty>
          </el-card>

          <!-- 简历编辑器 -->
          <el-card v-if="currentResume" shadow="hover" class="resume-editor-card">
            <template #header>
              <div class="card-header">
                <span>编辑简历</span>
                <div>
                  <el-button @click="cancelEdit">取消</el-button>
                  <el-button type="primary" @click="saveResume">保存</el-button>
                </div>
              </div>
            </template>

            <el-form :model="resumeForm" label-width="100px" class="resume-form">
              <el-form-item label="简历标题">
                <el-input v-model="resumeForm.title" placeholder="请输入简历标题" />
              </el-form-item>

              <el-divider content-position="left">基本信息</el-divider>
              
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="姓名">
                    <el-input v-model="resumeForm.name" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="性别">
                    <el-radio-group v-model="resumeForm.gender">
                      <el-radio label="男">男</el-radio>
                      <el-radio label="女">女</el-radio>
                    </el-radio-group>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="联系电话">
                    <el-input v-model="resumeForm.phone" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="电子邮箱">
                    <el-input v-model="resumeForm.email" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="求职意向">
                <el-input v-model="resumeForm.jobIntention" placeholder="如：Java开发工程师" />
              </el-form-item>

              <el-form-item label="个人优势">
                <el-input
                  v-model="resumeForm.advantage"
                  type="textarea"
                  :rows="4"
                  placeholder="请简要描述您的个人优势"
                />
              </el-form-item>

              <el-divider content-position="left">技能特长</el-divider>
              
              <el-form-item label="专业技能">
                <el-tag
                  v-for="skill in resumeForm.skills"
                  :key="skill"
                  closable
                  @close="removeSkill(skill)"
                  style="margin-right: 8px; margin-bottom: 8px;"
                >
                  {{ skill }}
                </el-tag>
                <el-input
                  v-if="skillInputVisible"
                  ref="skillInputRef"
                  v-model="skillInputValue"
                  size="small"
                  style="width: 120px;"
                  @keyup.enter="handleSkillInputConfirm"
                  @blur="handleSkillInputConfirm"
                />
                <el-button v-else size="small" @click="showSkillInput">+ 添加技能</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>

        <!-- 右侧功能区 -->
        <el-col :span="8">
          <!-- 上传简历 -->
          <el-card shadow="hover" class="upload-card">
            <template #header>
              <span>📤 上传简历</span>
            </template>
            <el-upload
              class="upload-demo"
              drag
              action="#"
              :auto-upload="false"
              :on-change="handleFileChange"
              accept=".pdf,.doc,.docx"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将文件拖到此处，或<em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  支持 PDF、Word 格式，文件大小不超过 5MB
                </div>
              </template>
            </el-upload>
          </el-card>

          <!-- 投递记录 -->
          <el-card shadow="hover" class="delivery-card">
            <template #header>
              <div class="card-header">
                <span>📨 最近投递</span>
                <el-button text type="primary" @click="router.push('/student/applications')">
                  查看全部 →
                </el-button>
              </div>
            </template>
            <el-timeline v-if="deliveryRecords.length > 0">
              <el-timeline-item
                v-for="record in deliveryRecords"
                :key="record.id"
                :timestamp="record.time"
                placement="top"
              >
                <div class="delivery-item">
                  <h4>{{ record.company }}</h4>
                  <p>{{ record.position }}</p>
                  <el-tag :type="getStatusType(record.status)" size="small">
                    {{ record.status }}
                  </el-tag>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无投递记录" :image-size="80" />
          </el-card>

          <!-- 简历模板 -->
          <el-card shadow="hover" class="template-card">
            <template #header>
              <span>📋 简历模板</span>
            </template>
            <div class="template-list">
              <div
                v-for="template in templates"
                :key="template.id"
                class="template-item"
                @click="useTemplate(template)"
              >
                <div class="template-preview">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="template-name">{{ template.name }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 简历预览对话框 -->
    <el-dialog v-model="previewDialogVisible" title="简历预览" width="800px" top="5vh">
      <div class="resume-preview">
        <div class="preview-header">
          <h2>{{ previewResume?.name }}</h2>
          <p>{{ previewResume?.jobIntention }}</p>
        </div>
        <el-divider />
        <div class="preview-content">
          <h3>📞 联系方式</h3>
          <p>电话：{{ previewResume?.phone }}</p>
          <p>邮箱：{{ previewResume?.email }}</p>
          
          <h3>💼 求职意向</h3>
          <p>{{ previewResume?.jobIntention }}</p>
          
          <h3>✨ 个人优势</h3>
          <p>{{ previewResume?.advantage }}</p>
          
          <h3>🎯 专业技能</h3>
          <el-tag
            v-for="skill in previewResume?.skills"
            :key="skill"
            style="margin-right: 8px; margin-bottom: 8px;"
          >
            {{ skill }}
          </el-tag>
        </div>
      </div>
      <template #footer>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
        <el-button type="primary" :icon="Download" @click="downloadCurrentResume">下载</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Plus,
  View,
  Edit,
  Download,
  More,
  UploadFilled,
  Document
} from '@element-plus/icons-vue';

const router = useRouter();

const resumeList = ref([
  {
    id: 1,
    title: '前端开发工程师简历',
    isDefault: true,
    updateTime: '2024-12-15 10:30',
    views: 128,
    tags: ['Vue', 'React', 'TypeScript']
  },
  {
    id: 2,
    title: '全栈开发工程师简历',
    isDefault: false,
    updateTime: '2024-12-10 15:20',
    views: 86,
    tags: ['Java', 'Spring Boot', 'Vue']
  }
]);

const currentResume = ref<any>(null);

const resumeForm = ref({
  title: '',
  name: '',
  gender: '男',
  phone: '',
  email: '',
  jobIntention: '',
  advantage: '',
  skills: [] as string[]
});

const skillInputVisible = ref(false);
const skillInputValue = ref('');
const skillInputRef = ref();

const deliveryRecords = ref([
  {
    id: 1,
    company: '阿里巴巴',
    position: '前端开发工程师',
    time: '2024-12-15 14:30',
    status: '待查看'
  },
  {
    id: 2,
    company: '腾讯科技',
    position: 'Vue开发工程师',
    time: '2024-12-14 10:20',
    status: '已查看'
  },
  {
    id: 3,
    company: '字节跳动',
    position: '全栈开发工程师',
    time: '2024-12-13 16:45',
    status: '面试邀请'
  }
]);

const templates = ref([
  { id: 1, name: '经典模板' },
  { id: 2, name: '简约模板' },
  { id: 3, name: '创意模板' },
  { id: 4, name: '专业模板' }
]);

const previewDialogVisible = ref(false);
const previewResume = ref<any>(null);

const createResume = () => {
  currentResume.value = { id: Date.now(), isNew: true };
  resumeForm.value = {
    title: '新建简历',
    name: '',
    gender: '男',
    phone: '',
    email: '',
    jobIntention: '',
    advantage: '',
    skills: []
  };
  ElMessage.success('开始创建新简历');
};

const selectResume = (resume: any) => {
  currentResume.value = resume;
  // 加载简历数据到表单
  resumeForm.value = {
    title: resume.title,
    name: resume.name || '',
    gender: resume.gender || '男',
    phone: resume.phone || '',
    email: resume.email || '',
    jobIntention: resume.jobIntention || '',
    advantage: resume.advantage || '',
    skills: resume.skills || []
  };
};

const editResume = (resume: any) => {
  selectResume(resume);
  ElMessage.info('进入编辑模式');
};

const previewResumeFunc = (resume: any) => {
  previewResume.value = resume;
  previewDialogVisible.value = true;
};

const downloadResume = (resume: any) => {
  ElMessage.success(`正在下载简历：${resume.title}`);
  // TODO: 实现下载功能
};

const handleResumeCommand = (command: string, resume: any) => {
  switch (command) {
    case 'setDefault':
      ElMessage.success('已设为默认简历');
      break;
    case 'duplicate':
      ElMessage.success('简历已复制');
      break;
    case 'delete':
      ElMessageBox.confirm('确定要删除这份简历吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        ElMessage.success('删除成功');
      });
      break;
  }
};

const saveResume = () => {
  ElMessage.success('简历保存成功');
  currentResume.value = null;
};

const cancelEdit = () => {
  currentResume.value = null;
};

const showSkillInput = () => {
  skillInputVisible.value = true;
  nextTick(() => {
    skillInputRef.value?.focus();
  });
};

const handleSkillInputConfirm = () => {
  if (skillInputValue.value) {
    resumeForm.value.skills.push(skillInputValue.value);
  }
  skillInputVisible.value = false;
  skillInputValue.value = '';
};

const removeSkill = (skill: string) => {
  resumeForm.value.skills = resumeForm.value.skills.filter(s => s !== skill);
};

const handleFileChange = (file: any) => {
  ElMessage.success(`文件 ${file.name} 上传成功`);
};

const useTemplate = (template: any) => {
  ElMessage.success(`使用模板：${template.name}`);
};

const getStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    '待查看': 'info',
    '已查看': 'warning',
    '面试邀请': 'success',
    '已拒绝': 'danger'
  };
  return typeMap[status] || 'info';
};

const downloadCurrentResume = () => {
  ElMessage.success('正在下载简历');
  previewDialogVisible.value = false;
};
</script>

<style scoped>
.resume-management {
  width: 100%;
  min-height: 100%;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  color: #fff;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 36px;
  color: rgba(255, 255, 255, 0.9);
}

.header-left h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.header-left p {
  margin: 2px 0 0 0;
  font-size: 12px;
  opacity: 0.9;
  color: #fff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.resume-content {
  margin-top: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.resume-list-card,
.resume-editor-card {
  border-radius: 8px;
}

.resume-list-card :deep(.el-card__header) {
  padding: 12px 16px;
  background: #f8f9fa;
}

.resume-list-card :deep(.el-card__body) {
  padding: 16px;
}

.resume-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.resume-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.resume-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.12);
  transform: translateY(-1px);
}

.resume-item.active {
  border-color: #409eff;
  background: #ecf5ff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.resume-info {
  flex: 1;
}

.resume-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.resume-header h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.resume-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 6px;
  font-size: 12px;
  color: #909399;
}

.resume-tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.resume-tags :deep(.el-tag) {
  height: 20px;
  line-height: 18px;
  font-size: 11px;
  padding: 0 6px;
}

.resume-actions {
  display: flex;
  gap: 4px;
}

.resume-actions :deep(.el-button) {
  padding: 5px 10px;
  font-size: 12px;
}

.resume-editor-card {
  margin-top: 12px;
  border-radius: 8px;
}

.resume-editor-card :deep(.el-card__header) {
  padding: 12px 16px;
  background: #f8f9fa;
}

.resume-editor-card :deep(.el-card__body) {
  padding: 16px;
}

.resume-form {
  max-width: 100%;
}

.resume-form :deep(.el-form-item) {
  margin-bottom: 14px;
}

.resume-form :deep(.el-form-item__label) {
  font-size: 12px;
  font-weight: 500;
  color: #2c3e50;
  line-height: 32px;
}

.resume-form :deep(.el-input__inner) {
  height: 32px;
  font-size: 12px;
}

.resume-form :deep(.el-textarea__inner) {
  font-size: 12px;
  line-height: 1.5;
  padding: 6px 10px;
}

.resume-form :deep(.el-divider__text) {
  font-size: 13px;
  font-weight: 600;
  color: #2c3e50;
}

.resume-form :deep(.el-radio__label) {
  font-size: 12px;
}

.resume-form :deep(.el-tag) {
  height: 22px;
  line-height: 20px;
  font-size: 11px;
  padding: 0 8px;
}

.upload-card,
.delivery-card,
.template-card {
  margin-bottom: 12px;
  border-radius: 8px;
}

.upload-card :deep(.el-card__header),
.delivery-card :deep(.el-card__header),
.template-card :deep(.el-card__header) {
  padding: 10px 14px;
  background: #f8f9fa;
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

.upload-card :deep(.el-card__body),
.delivery-card :deep(.el-card__body),
.template-card :deep(.el-card__body) {
  padding: 14px;
}

.upload-card :deep(.el-upload-dragger) {
  padding: 16px 12px;
}

.upload-card :deep(.el-icon--upload) {
  font-size: 32px;
  margin-bottom: 6px;
}

.upload-card :deep(.el-upload__text) {
  font-size: 12px;
}

.upload-card :deep(.el-upload__tip) {
  font-size: 11px;
  margin-top: 6px;
}

.delivery-card :deep(.el-timeline) {
  padding-left: 8px;
}

.delivery-card :deep(.el-timeline-item__wrapper) {
  padding-left: 20px;
}

.delivery-card :deep(.el-timeline-item__timestamp) {
  font-size: 11px;
}

.delivery-item h4 {
  margin: 0 0 3px 0;
  font-size: 13px;
  font-weight: 600;
  color: #2c3e50;
}

.delivery-item p {
  margin: 0 0 6px 0;
  font-size: 11px;
  color: #606266;
}

.delivery-item :deep(.el-tag) {
  height: 20px;
  line-height: 18px;
  font-size: 11px;
  padding: 0 6px;
}

.template-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.template-item {
  padding: 10px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.template-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.12);
  transform: translateY(-1px);
}

.template-preview {
  font-size: 24px;
  margin-bottom: 4px;
  color: #409eff;
}

.template-name {
  font-size: 12px;
  color: #606266;
  font-weight: 500;
}

.resume-preview {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.preview-header {
  text-align: center;
  margin-bottom: 14px;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 6px;
  color: #fff;
}

.preview-header h2 {
  margin: 0 0 6px 0;
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.preview-header p {
  margin: 0;
  font-size: 13px;
  opacity: 0.9;
  color: #fff;
}

.preview-content {
  background: #fff;
  padding: 14px;
  border-radius: 6px;
}

.preview-content h3 {
  margin: 12px 0 8px 0;
  font-size: 13px;
  font-weight: 600;
  color: #2c3e50;
}

.preview-content h3:first-child {
  margin-top: 0;
}

.preview-content p {
  margin: 4px 0;
  font-size: 12px;
  color: #606266;
  line-height: 1.5;
}

.preview-content :deep(.el-tag) {
  height: 20px;
  line-height: 18px;
  font-size: 11px;
  padding: 0 6px;
  margin-right: 4px;
  margin-bottom: 4px;
}
</style>

