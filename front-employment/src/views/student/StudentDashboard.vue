<template>
  <div class="student-dashboard">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <el-icon class="header-icon">👋</el-icon>
        <div>
          <h2>你好，{{ userInfo?.username }}</h2>
          <p>今天是 {{ currentDate }}，继续加油！</p>
        </div>
      </div>
      <div class="header-right">
        <div class="stat-item">
          <div class="stat-value">{{ stats.applications }}</div>
          <div class="stat-label">投递简历</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.interviews }}</div>
          <div class="stat-label">面试邀请</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.favorites }}</div>
          <div class="stat-label">收藏职位</div>
        </div>
      </div>
    </div>

    <!-- 最新招聘信息 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">🔥 热门招聘</span>
          <el-button text type="primary" @click="router.push('/student/recruitment')">
            查看更多 →
          </el-button>
        </div>
      </template>
      <div class="recruitment-list">
        <div 
          v-for="job in latestJobs" 
          :key="job.id"
          class="job-item"
          @click="viewJobDetail(job)"
        >
          <div class="job-logo">
            <img :src="job.logo || '/default-company.png'" :alt="job.company" />
          </div>
          <div class="job-info">
            <h4>{{ job.title }}</h4>
            <div class="job-meta">
              <span class="company">{{ job.company }}</span>
              <span class="location">📍 {{ job.location }}</span>
              <span class="salary">💰 {{ job.salary }}</span>
            </div>
            <div class="job-tags">
              <el-tag v-for="tag in job.tags" :key="tag" size="small">{{ tag }}</el-tag>
            </div>
          </div>
          <div class="job-actions">
            <el-button type="primary" size="small">立即申请</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 面试日程 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">📅 面试日程</span>
          <el-button text type="primary" @click="router.push('/student/interview')">
            查看全部 →
          </el-button>
        </div>
      </template>
      <el-timeline v-if="interviews.length > 0">
        <el-timeline-item
          v-for="interview in interviews"
          :key="interview.id"
          :timestamp="interview.time"
          placement="top"
          :color="getInterviewStatusColor(interview.status)"
        >
          <el-card>
            <h4>{{ interview.company }} - {{ interview.position }}</h4>
            <p>{{ interview.location }}</p>
            <el-tag :type="getInterviewStatusType(interview.status)" size="small">
              {{ interview.status }}
            </el-tag>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无面试安排" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();

const userInfo = ref<any>(null);
const stats = ref({
  applications: 12,
  interviews: 3,
  favorites: 8
});

const currentDate = computed(() => {
  const date = new Date();
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    weekday: 'long'
  });
});

const latestJobs = ref([
  {
    id: 1,
    title: '前端开发工程师',
    company: '阿里巴巴',
    location: '杭州',
    salary: '15-25K',
    logo: '',
    tags: ['Vue', 'React', '本科及以上']
  },
  {
    id: 2,
    title: 'Java后端开发',
    company: '腾讯科技',
    location: '深圳',
    salary: '18-30K',
    logo: '',
    tags: ['Spring Boot', 'MySQL', '3年经验']
  },
  {
    id: 3,
    title: 'UI设计师',
    company: '字节跳动',
    location: '北京',
    salary: '12-20K',
    logo: '',
    tags: ['Figma', 'Sketch', '应届生']
  }
]);

const interviews = ref([
  {
    id: 1,
    company: '华为技术',
    position: '软件开发工程师',
    time: '2024-12-20 14:00',
    location: '线上面试',
    status: '待面试'
  },
  {
    id: 2,
    company: '百度',
    position: '算法工程师',
    time: '2024-12-22 10:00',
    location: '北京总部',
    status: '已确认'
  }
]);

const loadUserInfo = () => {
  const user = localStorage.getItem('user');
  if (user) {
    userInfo.value = JSON.parse(user);
  }
};

const viewJobDetail = (job: any) => {
  ElMessage.info(`查看职位：${job.title}`);
  // TODO: 跳转到职位详情页
};

const getInterviewStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    '待面试': '#409eff',
    '已确认': '#67c23a',
    '已完成': '#909399',
    '已取消': '#f56c6c'
  };
  return colorMap[status] || '#409eff';
};

const getInterviewStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    '待面试': 'warning',
    '已确认': 'success',
    '已完成': 'info',
    '已取消': 'danger'
  };
  return typeMap[status] || 'info';
};

onMounted(() => {
  loadUserInfo();
});
</script>

<style scoped>
.student-dashboard {
  max-width: 1400px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  margin-bottom: 24px;
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
  color: #fff;
}

.header-left p {
  margin: 4px 0 0 0;
  font-size: 14px;
  opacity: 0.9;
  color: #fff;
}

.header-right {
  display: flex;
  gap: 48px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #fff;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  color: #fff;
}

.section-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.recruitment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.job-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.job-item:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  background: #f8f9fa;
}

.job-logo {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.job-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.job-info {
  flex: 1;
}

.job-info h4 {
  margin: 0 0 8px 0;
  font-size: 17px;
  font-weight: 600;
  color: #2c3e50;
}

.job-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.job-meta span {
  color: #2c3e50;
}

.job-tags {
  display: flex;
  gap: 8px;
}

.job-actions {
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 24px;
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
  }
  
  .job-item {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>

