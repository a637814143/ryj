<template>
  <div class="job-detail">
    <el-page-header @back="router.back()" title="返回">
      <template #content>
        <span class="page-title">职位详情</span>
      </template>
    </el-page-header>

    <div class="detail-content">
      <el-row :gutter="20">
        <!-- 左侧职位详情 -->
        <el-col :span="16">
          <el-card shadow="hover" class="job-info-card">
            <div class="job-header">
              <div class="job-main-info">
                <h1>{{ jobDetail.title }}</h1>
                <div class="job-salary">{{ jobDetail.salary }}</div>
              </div>
              <div class="job-actions">
                <el-button
                  :icon="jobDetail.isFavorite ? StarFilled : Star"
                  size="large"
                  @click="toggleFavorite"
                >
                  {{ jobDetail.isFavorite ? '已收藏' : '收藏' }}
                </el-button>
                <el-button type="primary" size="large" :icon="Position" @click="applyJob">
                  立即申请
                </el-button>
              </div>
            </div>

            <el-divider />

            <div class="job-meta-info">
              <div class="meta-item">
                <el-icon><Location /></el-icon>
                <span>工作地点：{{ jobDetail.location }}</span>
              </div>
              <div class="meta-item">
                <el-icon><Briefcase /></el-icon>
                <span>工作经验：{{ jobDetail.experience }}</span>
              </div>
              <div class="meta-item">
                <el-icon><Reading /></el-icon>
                <span>学历要求：{{ jobDetail.education }}</span>
              </div>
              <div class="meta-item">
                <el-icon><User /></el-icon>
                <span>招聘人数：{{ jobDetail.recruitNumber }}人</span>
              </div>
              <div class="meta-item">
                <el-icon><Clock /></el-icon>
                <span>发布时间：{{ jobDetail.publishTime }}</span>
              </div>
            </div>

            <el-divider />

            <div class="job-tags">
              <el-tag
                v-for="tag in jobDetail.tags"
                :key="tag"
                type="primary"
                effect="plain"
                size="large"
              >
                {{ tag }}
              </el-tag>
            </div>

            <el-divider content-position="left">
              <h3>职位描述</h3>
            </el-divider>
            <div class="job-description">
              <p>{{ jobDetail.description }}</p>
            </div>

            <el-divider content-position="left">
              <h3>职位要求</h3>
            </el-divider>
            <div class="job-requirements">
              <ul>
                <li v-for="(req, index) in jobDetail.requirements" :key="index">
                  {{ req }}
                </li>
              </ul>
            </div>

            <el-divider content-position="left">
              <h3>福利待遇</h3>
            </el-divider>
            <div class="job-benefits">
              <el-tag
                v-for="benefit in jobDetail.benefits"
                :key="benefit"
                type="success"
                effect="light"
                size="large"
              >
                {{ benefit }}
              </el-tag>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧公司信息 -->
        <el-col :span="8">
          <el-card shadow="hover" class="company-card">
            <template #header>
              <span>公司信息</span>
            </template>
            <div class="company-info">
              <div class="company-logo">
                <img :src="companyInfo.logo || '/default-company.png'" :alt="companyInfo.name" />
              </div>
              <h2>{{ companyInfo.name }}</h2>
              <div class="company-tags">
                <el-tag size="small">{{ companyInfo.industry }}</el-tag>
                <el-tag size="small">{{ companyInfo.scale }}</el-tag>
                <el-tag size="small">{{ companyInfo.financing }}</el-tag>
              </div>
            </div>

            <el-divider />

            <el-descriptions :column="1" border>
              <el-descriptions-item label="公司全称">
                {{ companyInfo.fullName }}
              </el-descriptions-item>
              <el-descriptions-item label="所属行业">
                {{ companyInfo.industry }}
              </el-descriptions-item>
              <el-descriptions-item label="公司规模">
                {{ companyInfo.scale }}
              </el-descriptions-item>
              <el-descriptions-item label="融资阶段">
                {{ companyInfo.financing }}
              </el-descriptions-item>
              <el-descriptions-item label="公司地址">
                {{ companyInfo.address }}
              </el-descriptions-item>
            </el-descriptions>

            <el-divider />

            <div class="company-description">
              <h4>公司介绍</h4>
              <p>{{ companyInfo.description }}</p>
            </div>

            <el-button type="primary" style="width: 100%; margin-top: 16px;" @click="viewCompanyJobs">
              查看该公司所有职位
            </el-button>
          </el-card>

          <!-- 相似职位推荐 -->
          <el-card shadow="hover" class="similar-jobs-card">
            <template #header>
              <span>相似职位推荐</span>
            </template>
            <div class="similar-jobs-list">
              <div
                v-for="job in similarJobs"
                :key="job.id"
                class="similar-job-item"
                @click="viewJob(job.id)"
              >
                <h4>{{ job.title }}</h4>
                <p class="company">{{ job.company }}</p>
                <div class="job-info">
                  <span class="salary">{{ job.salary }}</span>
                  <span class="location">{{ job.location }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 申请职位对话框 -->
    <el-dialog v-model="applyDialogVisible" title="申请职位" width="600px">
      <el-form :model="applyForm" label-width="100px">
        <el-form-item label="选择简历">
          <el-select v-model="applyForm.resumeId" placeholder="请选择要投递的简历" style="width: 100%">
            <el-option
              v-for="resume in resumeList"
              :key="resume.id"
              :label="resume.title"
              :value="resume.id"
            >
              <span>{{ resume.title }}</span>
              <el-tag v-if="resume.isDefault" size="small" style="margin-left: 8px;">默认</el-tag>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="求职信">
          <el-input
            v-model="applyForm.coverLetter"
            type="textarea"
            :rows="6"
            placeholder="请输入求职信（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmApply">确认申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  Star,
  StarFilled,
  Position,
  Location,
  Briefcase,
  Reading,
  User,
  Clock
} from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();

const jobDetail = ref({
  id: 1,
  title: 'Java开发工程师',
  company: '阿里巴巴',
  salary: '15-25K',
  location: '杭州',
  experience: '1-3年',
  education: '本科',
  recruitNumber: 5,
  publishTime: '2024-12-15',
  tags: ['Java', 'Spring Boot', 'MySQL', 'Redis', '微服务'],
  description: '负责公司核心业务系统的开发和维护，参与系统架构设计，优化系统性能。我们寻找热爱技术、有责任心的工程师加入我们的团队。',
  requirements: [
    '本科及以上学历，计算机相关专业',
    '1-3年Java开发经验',
    '熟练掌握Java基础知识，熟悉JVM原理',
    '熟悉Spring、Spring Boot、MyBatis等主流框架',
    '熟悉MySQL、Redis等数据库',
    '了解分布式系统、微服务架构',
    '良好的编码习惯和团队协作能力'
  ],
  benefits: ['五险一金', '年终奖', '股票期权', '弹性工作', '带薪年假', '定期体检', '员工培训', '下午茶'],
  isFavorite: false
});

const companyInfo = ref({
  name: '阿里巴巴',
  fullName: '阿里巴巴（中国）有限公司',
  logo: '',
  industry: '互联网/电商',
  scale: '10000人以上',
  financing: '已上市',
  address: '浙江省杭州市余杭区文一西路969号',
  description: '阿里巴巴集团是以曾担任英语教师的马云为首的18人于1999年在浙江省杭州市创立的公司。阿里巴巴集团经营多项业务，另外也从关联公司的业务和服务中取得经营商业生态系统上的支援。'
});

const similarJobs = ref([
  {
    id: 2,
    title: 'Java高级开发工程师',
    company: '阿里巴巴',
    salary: '20-35K',
    location: '杭州'
  },
  {
    id: 3,
    title: 'Java开发工程师',
    company: '蚂蚁集团',
    salary: '18-30K',
    location: '杭州'
  },
  {
    id: 4,
    title: '后端开发工程师',
    company: '网易',
    salary: '15-25K',
    location: '杭州'
  }
]);

const applyDialogVisible = ref(false);
const applyForm = ref({
  resumeId: null,
  coverLetter: ''
});

const resumeList = ref([
  { id: 1, title: 'Java开发工程师简历', isDefault: true },
  { id: 2, title: '全栈开发工程师简历', isDefault: false }
]);

const toggleFavorite = () => {
  jobDetail.value.isFavorite = !jobDetail.value.isFavorite;
  ElMessage.success(jobDetail.value.isFavorite ? '收藏成功' : '取消收藏');
};

const applyJob = () => {
  applyDialogVisible.value = true;
};

const confirmApply = () => {
  if (!applyForm.value.resumeId) {
    ElMessage.warning('请选择要投递的简历');
    return;
  }
  ElMessage.success('申请成功！');
  applyDialogVisible.value = false;
  // TODO: 调用API提交申请
};

const viewCompanyJobs = () => {
  router.push({
    path: '/student/recruitment',
    query: { company: companyInfo.value.name }
  });
};

const viewJob = (jobId: number) => {
  router.push(`/student/job/${jobId}`);
};

onMounted(() => {
  // 从路由参数获取职位ID
  const jobId = route.params.id;
  // TODO: 根据jobId加载职位详情
});
</script>

<style scoped>
.job-detail {
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.detail-content {
  margin-top: 24px;
}

.job-info-card {
  margin-bottom: 20px;
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.job-main-info h1 {
  margin: 0 0 12px 0;
  font-size: 28px;
  color: #303133;
}

.job-salary {
  font-size: 24px;
  font-weight: 600;
  color: #f56c6c;
}

.job-actions {
  display: flex;
  gap: 12px;
}

.job-meta-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.job-tags {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.job-description,
.job-requirements {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}

.job-requirements ul {
  margin: 0;
  padding-left: 20px;
}

.job-requirements li {
  margin: 8px 0;
}

.job-benefits {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.company-card {
  margin-bottom: 20px;
}

.company-info {
  text-align: center;
}

.company-logo {
  width: 100px;
  height: 100px;
  margin: 0 auto 16px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
}

.company-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.company-info h2 {
  margin: 0 0 12px 0;
  font-size: 20px;
  color: #303133;
}

.company-tags {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.company-description h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #303133;
}

.company-description p {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.similar-jobs-card {
  margin-top: 20px;
}

.similar-jobs-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.similar-job-item {
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.similar-job-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.similar-job-item h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #303133;
}

.similar-job-item .company {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #909399;
}

.similar-job-item .job-info {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.similar-job-item .salary {
  color: #f56c6c;
  font-weight: 600;
}

.similar-job-item .location {
  color: #909399;
}
</style>

