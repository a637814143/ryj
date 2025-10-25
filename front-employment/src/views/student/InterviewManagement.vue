<template>
  <div class="interview-management">
    <el-page-header @back="router.back()" title="返回">
      <template #content>
        <span class="page-title">📅 面试管理</span>
      </template>
    </el-page-header>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              📋
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">全部面试</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              ⏰
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pending }}</div>
              <div class="stat-label">待面试</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              ✅
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.completed }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
              🎉
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.passed }}</div>
              <div class="stat-label">通过面试</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 面试列表 -->
    <el-card shadow="hover" class="interview-card">
      <template #header>
        <div class="card-header">
          <el-radio-group v-model="filterStatus" @change="handleFilterChange">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="pending">待面试</el-radio-button>
            <el-radio-button label="confirmed">已确认</el-radio-button>
            <el-radio-button label="completed">已完成</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="filteredInterviews" stripe>
        <el-table-column prop="company" label="公司名称" width="180" />
        <el-table-column prop="position" label="职位" width="180" />
        <el-table-column prop="round" label="面试轮次" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.round }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="面试类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === '线上' ? 'success' : 'primary'" size="small">
              {{ row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="面试时间" width="180" />
        <el-table-column prop="location" label="面试地点" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :icon="View" @click="viewDetail(row)">详情</el-button>
            <el-button
              v-if="row.status === '待确认'"
              size="small"
              type="primary"
              @click="confirmInterview(row)"
            >
              确认
            </el-button>
            <el-button
              v-if="row.status === '已完成'"
              size="small"
              type="success"
              @click="viewResult(row)"
            >
              结果
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 面试详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="面试详情" width="700px">
      <div v-if="currentInterview" class="interview-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="公司名称">{{ currentInterview.company }}</el-descriptions-item>
          <el-descriptions-item label="职位">{{ currentInterview.position }}</el-descriptions-item>
          <el-descriptions-item label="面试轮次">{{ currentInterview.round }}</el-descriptions-item>
          <el-descriptions-item label="面试类型">{{ currentInterview.type }}</el-descriptions-item>
          <el-descriptions-item label="面试时间" :span="2">{{ currentInterview.time }}</el-descriptions-item>
          <el-descriptions-item label="面试地点" :span="2">{{ currentInterview.location }}</el-descriptions-item>
          <el-descriptions-item label="面试官">{{ currentInterview.interviewer }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentInterview.phone }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentInterview.status)">{{ currentInterview.status }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <div class="interview-notes">
          <h4>面试须知</h4>
          <p>{{ currentInterview.notes }}</p>
        </div>

        <div v-if="currentInterview.materials" class="interview-materials">
          <h4>需要准备的材料</h4>
          <ul>
            <li v-for="(material, index) in currentInterview.materials" :key="index">
              {{ material }}
            </li>
          </ul>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button v-if="currentInterview?.status === '待确认'" type="primary" @click="confirmInterview(currentInterview)">
          确认参加
        </el-button>
      </template>
    </el-dialog>

    <!-- 面试结果对话框 -->
    <el-dialog v-model="resultDialogVisible" title="面试结果" width="600px">
      <div v-if="currentInterview" class="interview-result">
        <el-result
          :icon="currentInterview.result === '通过' ? 'success' : 'error'"
          :title="currentInterview.result === '通过' ? '恭喜您通过面试！' : '很遗憾，未通过面试'"
        >
          <template #sub-title>
            <p>{{ currentInterview.feedback }}</p>
          </template>
          <template #extra>
            <el-button v-if="currentInterview.result === '通过'" type="primary">
              查看Offer详情
            </el-button>
            <el-button v-else @click="resultDialogVisible = false">
              继续努力
            </el-button>
          </template>
        </el-result>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { View } from '@element-plus/icons-vue';

const router = useRouter();

const stats = ref({
  total: 15,
  pending: 3,
  completed: 10,
  passed: 6
});

const filterStatus = ref('all');

const interviews = ref([
  {
    id: 1,
    company: '阿里巴巴',
    position: 'Java开发工程师',
    round: '一面',
    type: '线上',
    time: '2024-12-20 14:00',
    location: '腾讯会议',
    status: '待确认',
    interviewer: '张经理',
    phone: '138****8888',
    notes: '请提前10分钟进入会议室，准备好简历和作品集。',
    materials: ['个人简历', '身份证复印件', '学历证明']
  },
  {
    id: 2,
    company: '腾讯科技',
    position: '前端开发工程师',
    round: '二面',
    type: '现场',
    time: '2024-12-22 10:00',
    location: '深圳市南山区腾讯大厦',
    status: '已确认',
    interviewer: '李总监',
    phone: '139****9999',
    notes: '请携带身份证原件，提前15分钟到达。',
    materials: ['个人简历', '身份证原件', '作品集']
  },
  {
    id: 3,
    company: '字节跳动',
    position: 'Python开发',
    round: '终面',
    type: '线上',
    time: '2024-12-18 15:00',
    location: '飞书会议',
    status: '已完成',
    result: '通过',
    feedback: '技术能力扎实，沟通表达清晰，期待您的加入！',
    interviewer: '王总',
    phone: '137****7777',
    notes: '',
    materials: []
  }
]);

const filteredInterviews = computed(() => {
  if (filterStatus.value === 'all') {
    return interviews.value;
  }
  const statusMap: Record<string, string[]> = {
    pending: ['待确认'],
    confirmed: ['已确认'],
    completed: ['已完成']
  };
  return interviews.value.filter(item => statusMap[filterStatus.value]?.includes(item.status));
});

const detailDialogVisible = ref(false);
const resultDialogVisible = ref(false);
const currentInterview = ref<any>(null);

const handleFilterChange = () => {
  ElMessage.info(`筛选：${filterStatus.value}`);
};

const getStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    '待确认': 'warning',
    '已确认': 'primary',
    '已完成': 'success',
    '已取消': 'info'
  };
  return typeMap[status] || 'info';
};

const viewDetail = (interview: any) => {
  currentInterview.value = interview;
  detailDialogVisible.value = true;
};

const confirmInterview = (interview: any) => {
  interview.status = '已确认';
  ElMessage.success('已确认参加面试');
  detailDialogVisible.value = false;
};

const viewResult = (interview: any) => {
  currentInterview.value = interview;
  resultDialogVisible.value = true;
};
</script>

<style scoped>
.interview-management {
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
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.interview-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.interview-detail {
  padding: 20px 0;
}

.interview-notes,
.interview-materials {
  margin-top: 20px;
}

.interview-notes h4,
.interview-materials h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #303133;
}

.interview-notes p {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.interview-materials ul {
  margin: 0;
  padding-left: 20px;
}

.interview-materials li {
  margin: 8px 0;
  font-size: 14px;
  color: #606266;
}

.interview-result {
  padding: 20px 0;
}
</style>

