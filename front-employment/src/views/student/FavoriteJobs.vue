<template>
  <div class="favorite-jobs">
    <el-page-header @back="router.back()" title="返回">
      <template #content>
        <span class="page-title">⭐ 我的收藏</span>
      </template>
    </el-page-header>

    <!-- 统计信息 -->
    <el-card shadow="hover" class="stats-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-statistic title="收藏职位" :value="stats.total">
            <template #prefix>
              <el-icon style="color: #409eff"><Star /></el-icon>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="8">
          <el-statistic title="本周新增" :value="stats.thisWeek">
            <template #prefix>
              <el-icon style="color: #67c23a"><TrendCharts /></el-icon>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="8">
          <el-statistic title="已申请" :value="stats.applied">
            <template #prefix>
              <el-icon style="color: #e6a23c"><Document /></el-icon>
            </template>
          </el-statistic>
        </el-col>
      </el-row>
    </el-card>

    <!-- 筛选和操作栏 -->
    <el-card shadow="hover" class="filter-card">
      <el-row :gutter="16" align="middle">
        <el-col :span="12">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索职位或公司"
            :prefix-icon="Search"
            clearable
          />
        </el-col>
        <el-col :span="6">
          <el-select v-model="filterCity" placeholder="工作地点" clearable style="width: 100%">
            <el-option label="全部城市" value="" />
            <el-option label="北京" value="北京" />
            <el-option label="上海" value="上海" />
            <el-option label="深圳" value="深圳" />
            <el-option label="杭州" value="杭州" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="danger" :icon="Delete" @click="batchDelete">批量删除</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 收藏列表 -->
    <el-card shadow="hover" class="jobs-card">
      <el-table
        :data="filteredJobs"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="职位信息" min-width="300">
          <template #default="{ row }">
            <div class="job-info">
              <div class="job-logo">
                <img :src="row.logo || '/default-company.png'" :alt="row.company" />
              </div>
              <div class="job-details">
                <h3 @click="viewJobDetail(row.id)">{{ row.title }}</h3>
                <p>{{ row.company }}</p>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="salary" label="薪资" width="150">
          <template #default="{ row }">
            <span class="salary">{{ row.salary }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="地点" width="120" />
        <el-table-column prop="experience" label="经验" width="120" />
        <el-table-column prop="education" label="学历" width="100" />
        <el-table-column label="标签" width="200">
          <template #default="{ row }">
            <el-tag v-for="tag in row.tags.slice(0, 2)" :key="tag" size="small" style="margin-right: 4px;">
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="favoriteTime" label="收藏时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :icon="View" @click="viewJobDetail(row.id)">查看</el-button>
            <el-button
              v-if="!row.applied"
              size="small"
              type="primary"
              @click="applyJob(row)"
            >
              申请
            </el-button>
            <el-tag v-else size="small" type="success">已申请</el-tag>
            <el-button
              size="small"
              type="danger"
              :icon="Delete"
              @click="removeFavorite(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next, jumper"
        @current-change="handlePageChange"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>

    <!-- 空状态 -->
    <el-empty v-if="favoriteJobs.length === 0" description="还没有收藏职位，快去发现心仪的工作吧！">
      <el-button type="primary" @click="router.push('/student/recruitment')">
        浏览职位
      </el-button>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Star,
  TrendCharts,
  Document,
  Search,
  Delete,
  View
} from '@element-plus/icons-vue';

const router = useRouter();

const stats = ref({
  total: 12,
  thisWeek: 3,
  applied: 5
});

const searchKeyword = ref('');
const filterCity = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(12);

const favoriteJobs = ref([
  {
    id: 1,
    title: 'Java开发工程师',
    company: '阿里巴巴',
    logo: '',
    salary: '15-25K',
    location: '杭州',
    experience: '1-3年',
    education: '本科',
    tags: ['Java', 'Spring Boot', 'MySQL'],
    favoriteTime: '2024-12-15 10:30',
    applied: false
  },
  {
    id: 2,
    title: '前端开发工程师',
    company: '腾讯科技',
    logo: '',
    salary: '12-20K',
    location: '深圳',
    experience: '应届生',
    education: '本科',
    tags: ['Vue', 'React', 'TypeScript'],
    favoriteTime: '2024-12-14 15:20',
    applied: true
  },
  {
    id: 3,
    title: 'Python开发',
    company: '字节跳动',
    logo: '',
    salary: '18-30K',
    location: '北京',
    experience: '3-5年',
    education: '本科',
    tags: ['Python', 'Django', 'Flask'],
    favoriteTime: '2024-12-13 09:45',
    applied: false
  }
]);

const selectedJobs = ref<any[]>([]);

const filteredJobs = computed(() => {
  let result = favoriteJobs.value;
  
  // 关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(job =>
      job.title.toLowerCase().includes(keyword) ||
      job.company.toLowerCase().includes(keyword)
    );
  }
  
  // 城市筛选
  if (filterCity.value) {
    result = result.filter(job => job.location === filterCity.value);
  }
  
  return result;
});

const handleSelectionChange = (selection: any[]) => {
  selectedJobs.value = selection;
};

const viewJobDetail = (jobId: number) => {
  router.push(`/student/job/${jobId}`);
};

const applyJob = (job: any) => {
  ElMessage.success(`正在申请：${job.title}`);
  job.applied = true;
  // TODO: 调用API申请职位
};

const removeFavorite = (job: any) => {
  ElMessageBox.confirm(`确定要取消收藏"${job.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = favoriteJobs.value.findIndex(item => item.id === job.id);
    if (index !== -1) {
      favoriteJobs.value.splice(index, 1);
      stats.value.total--;
      ElMessage.success('已取消收藏');
    }
  });
};

const batchDelete = () => {
  if (selectedJobs.value.length === 0) {
    ElMessage.warning('请先选择要删除的职位');
    return;
  }
  
  ElMessageBox.confirm(`确定要删除选中的 ${selectedJobs.value.length} 个职位吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    selectedJobs.value.forEach(job => {
      const index = favoriteJobs.value.findIndex(item => item.id === job.id);
      if (index !== -1) {
        favoriteJobs.value.splice(index, 1);
      }
    });
    stats.value.total -= selectedJobs.value.length;
    selectedJobs.value = [];
    ElMessage.success('批量删除成功');
  });
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
  // TODO: 加载对应页的数据
};
</script>

<style scoped>
.favorite-jobs {
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.stats-card,
.filter-card,
.jobs-card {
  margin-top: 20px;
}

.job-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.job-logo {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f7fa;
}

.job-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.job-details h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #409eff;
  cursor: pointer;
  transition: color 0.3s;
}

.job-details h3:hover {
  color: #66b1ff;
}

.job-details p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.salary {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}
</style>

