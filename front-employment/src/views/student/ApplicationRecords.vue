<template>
  <div class="application-records">
    <el-page-header @back="router.back()" title="返回">
      <template #content>
        <span class="page-title">📨 投递记录</span>
      </template>
    </el-page-header>

    <!-- 统计概览 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="总投递" :value="stats.total">
            <template #prefix>
              <el-icon style="color: #409eff">
                <Document />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="待查看" :value="stats.pending">
            <template #prefix>
              <el-icon style="color: #e6a23c">
                <Clock />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="面试邀请" :value="stats.interview">
            <template #prefix>
              <el-icon style="color: #67c23a">
                <Calendar />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="已拒绝" :value="stats.rejected">
            <template #prefix>
              <el-icon style="color: #f56c6c">
                <Close />
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <!-- 投递记录列表 -->
    <el-card shadow="hover" class="records-card">
      <template #header>
        <div class="card-header">
          <el-radio-group v-model="filterStatus" @change="handleFilterChange">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="pending">待查看</el-radio-button>
            <el-radio-button label="viewed">已查看</el-radio-button>
            <el-radio-button label="interview">面试邀请</el-radio-button>
            <el-radio-button label="rejected">已拒绝</el-radio-button>
          </el-radio-group>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索公司或职位"
            :prefix-icon="Search"
            style="width: 250px"
            clearable
          />
        </div>
      </template>

      <el-timeline>
        <el-timeline-item
          v-for="record in filteredRecords"
          :key="record.id"
          :timestamp="record.applyTime"
          placement="top"
          :color="getTimelineColor(record.status)"
        >
          <el-card shadow="hover" class="record-item">
            <div class="record-content">
              <div class="record-main">
                <div class="record-logo">
                  <img :src="record.logo || '/default-company.png'" :alt="record.company" />
                </div>
                <div class="record-info">
                  <h3>{{ record.position }}</h3>
                  <p class="company-name">{{ record.company }}</p>
                  <div class="record-meta">
                    <span>📍 {{ record.location }}</span>
                    <span>💰 {{ record.salary }}</span>
                    <span>📄 使用简历：{{ record.resumeName }}</span>
                  </div>
                </div>
              </div>
              <div class="record-status">
                <el-tag :type="getStatusType(record.status)" size="large">
                  {{ record.status }}
                </el-tag>
                <div class="record-actions">
                  <el-button size="small" :icon="View" @click="viewDetail(record)">
                    查看详情
                  </el-button>
                  <el-button
                    v-if="record.status === '面试邀请'"
                    size="small"
                    type="primary"
                    @click="goToInterview(record)"
                  >
                    查看面试
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 进度条 -->
            <el-divider />
            <div class="record-progress">
              <el-steps :active="getProgressStep(record.status)" align-center>
                <el-step title="简历投递" :icon="Document" />
                <el-step title="简历查看" :icon="View" />
                <el-step title="面试邀请" :icon="Calendar" />
                <el-step title="最终结果" :icon="record.finalResult === '通过' ? SuccessFilled : CircleClose" />
              </el-steps>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <el-empty v-if="filteredRecords.length === 0" description="暂无投递记录" />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="投递详情" width="700px">
      <div v-if="currentRecord" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="职位名称">{{ currentRecord.position }}</el-descriptions-item>
          <el-descriptions-item label="公司名称">{{ currentRecord.company }}</el-descriptions-item>
          <el-descriptions-item label="工作地点">{{ currentRecord.location }}</el-descriptions-item>
          <el-descriptions-item label="薪资范围">{{ currentRecord.salary }}</el-descriptions-item>
          <el-descriptions-item label="投递时间" :span="2">{{ currentRecord.applyTime }}</el-descriptions-item>
          <el-descriptions-item label="使用简历" :span="2">{{ currentRecord.resumeName }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusType(currentRecord.status)">{{ currentRecord.status }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <div class="timeline-detail">
          <h4>处理进度</h4>
          <el-timeline>
            <el-timeline-item
              v-for="(log, index) in currentRecord.logs"
              :key="index"
              :timestamp="log.time"
            >
              {{ log.content }}
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  Document,
  Clock,
  Calendar,
  Close,
  Search,
  View,
  SuccessFilled,
  CircleClose
} from '@element-plus/icons-vue';

const router = useRouter();

const stats = ref({
  total: 28,
  pending: 8,
  interview: 5,
  rejected: 3
});

const filterStatus = ref('all');
const searchKeyword = ref('');

const records = ref([
  {
    id: 1,
    company: '阿里巴巴',
    position: 'Java开发工程师',
    location: '杭州',
    salary: '15-25K',
    applyTime: '2024-12-15 10:30',
    status: '面试邀请',
    resumeName: '前端开发工程师简历',
    logo: '',
    logs: [
      { time: '2024-12-15 10:30', content: '投递简历' },
      { time: '2024-12-15 14:20', content: 'HR已查看简历' },
      { time: '2024-12-16 09:15', content: '收到面试邀请' }
    ]
  },
  {
    id: 2,
    company: '腾讯科技',
    position: '前端开发工程师',
    location: '深圳',
    salary: '12-20K',
    applyTime: '2024-12-14 15:20',
    status: '已查看',
    resumeName: '前端开发工程师简历',
    logo: '',
    logs: [
      { time: '2024-12-14 15:20', content: '投递简历' },
      { time: '2024-12-14 18:30', content: 'HR已查看简历' }
    ]
  },
  {
    id: 3,
    company: '字节跳动',
    position: 'Python开发',
    location: '北京',
    salary: '18-30K',
    applyTime: '2024-12-13 09:45',
    status: '待查看',
    resumeName: '全栈开发工程师简历',
    logo: '',
    logs: [
      { time: '2024-12-13 09:45', content: '投递简历' }
    ]
  }
]);

const filteredRecords = computed(() => {
  let result = records.value;
  
  // 状态筛选
  if (filterStatus.value !== 'all') {
    const statusMap: Record<string, string[]> = {
      pending: ['待查看'],
      viewed: ['已查看'],
      interview: ['面试邀请'],
      rejected: ['已拒绝']
    };
    result = result.filter(item => statusMap[filterStatus.value]?.includes(item.status));
  }
  
  // 关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(item =>
      item.company.toLowerCase().includes(keyword) ||
      item.position.toLowerCase().includes(keyword)
    );
  }
  
  return result;
});

const detailDialogVisible = ref(false);
const currentRecord = ref<any>(null);

const handleFilterChange = () => {
  ElMessage.info(`筛选：${filterStatus.value}`);
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

const getTimelineColor = (status: string) => {
  const colorMap: Record<string, string> = {
    '待查看': '#909399',
    '已查看': '#e6a23c',
    '面试邀请': '#67c23a',
    '已拒绝': '#f56c6c'
  };
  return colorMap[status] || '#409eff';
};

const getProgressStep = (status: string) => {
  const stepMap: Record<string, number> = {
    '待查看': 1,
    '已查看': 2,
    '面试邀请': 3,
    '已拒绝': 4,
    '已通过': 4
  };
  return stepMap[status] || 0;
};

const viewDetail = (record: any) => {
  currentRecord.value = record;
  detailDialogVisible.value = true;
};

const goToInterview = (record: any) => {
  router.push('/student/interview');
};
</script>

<style scoped>
.application-records {
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.stats-row {
  margin-top: 24px;
}

.stat-card {
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.records-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-item {
  transition: all 0.3s;
}

.record-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.record-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.record-main {
  display: flex;
  gap: 16px;
  flex: 1;
}

.record-logo {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f7fa;
}

.record-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.record-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
}

.company-name {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #606266;
}

.record-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #909399;
}

.record-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.record-actions {
  display: flex;
  gap: 8px;
}

.record-progress {
  margin-top: 16px;
}

.timeline-detail h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #303133;
}
</style>

