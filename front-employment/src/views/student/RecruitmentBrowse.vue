<template>
  <div class="recruitment-browse">
    <el-page-header @back="router.back()" title="返回">
      <template #content>
        <span class="page-title">🔍 招聘信息浏览</span>
      </template>
    </el-page-header>

    <!-- 搜索筛选区 -->
    <el-card shadow="hover" class="search-card">
      <el-form :model="searchForm" :inline="true">
        <el-form-item>
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索职位、公司"
            :prefix-icon="Search"
            style="width: 300px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.city" placeholder="工作地点" clearable style="width: 150px">
            <el-option label="北京" value="北京" />
            <el-option label="上海" value="上海" />
            <el-option label="深圳" value="深圳" />
            <el-option label="杭州" value="杭州" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.salary" placeholder="薪资范围" clearable style="width: 150px">
            <el-option label="5K以下" value="0-5" />
            <el-option label="5-10K" value="5-10" />
            <el-option label="10-15K" value="10-15" />
            <el-option label="15-25K" value="15-25" />
            <el-option label="25K以上" value="25-100" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 职位列表 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="18">
        <el-card shadow="hover">
          <div class="job-list">
            <div
              v-for="job in jobList"
              :key="job.id"
              class="job-item"
              @click="viewJobDetail(job)"
            >
              <div class="job-main">
                <div class="job-logo">
                  <img :src="job.logo || '/default-company.png'" :alt="job.company" />
                </div>
                <div class="job-info">
                  <h3>{{ job.title }}</h3>
                  <div class="job-meta">
                    <span class="company">{{ job.company }}</span>
                    <span class="location">📍 {{ job.location }}</span>
                    <span class="experience">💼 {{ job.experience }}</span>
                    <span class="education">🎓 {{ job.education }}</span>
                  </div>
                  <div class="job-tags">
                    <el-tag v-for="tag in job.tags" :key="tag" size="small">{{ tag }}</el-tag>
                  </div>
                </div>
                <div class="job-right">
                  <div class="job-salary">{{ job.salary }}</div>
                  <div class="job-actions">
                    <el-button
                      :icon="job.isFavorite ? StarFilled : Star"
                      circle
                      @click.stop="toggleFavorite(job)"
                    />
                    <el-button type="primary" @click.stop="applyJob(job)">申请职位</el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            layout="total, prev, pager, next, jumper"
            @current-change="handlePageChange"
            style="margin-top: 20px; justify-content: center"
          />
        </el-card>
      </el-col>

      <!-- 右侧推荐 -->
      <el-col :span="6">
        <el-card shadow="hover" class="hot-companies">
          <template #header>
            <span>🔥 热门企业</span>
          </template>
          <div class="company-list">
            <div v-for="company in hotCompanies" :key="company.id" class="company-item">
              <img :src="company.logo || '/default-company.png'" :alt="company.name" />
              <div class="company-info">
                <div class="company-name">{{ company.name }}</div>
                <div class="company-jobs">{{ company.jobs }}个在招职位</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Search, Refresh, Star, StarFilled } from '@element-plus/icons-vue';

const router = useRouter();

const searchForm = ref({
  keyword: '',
  city: '',
  salary: ''
});

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(50);

const jobList = ref([
  {
    id: 1,
    title: 'Java开发工程师',
    company: '阿里巴巴',
    location: '杭州',
    salary: '15-25K',
    experience: '1-3年',
    education: '本科',
    tags: ['Java', 'Spring Boot', 'MySQL'],
    isFavorite: false,
    logo: ''
  },
  {
    id: 2,
    title: '前端开发工程师',
    company: '腾讯科技',
    location: '深圳',
    salary: '12-20K',
    experience: '应届生',
    education: '本科',
    tags: ['Vue', 'React', 'TypeScript'],
    isFavorite: true,
    logo: ''
  }
]);

const hotCompanies = ref([
  { id: 1, name: '阿里巴巴', jobs: 128, logo: '' },
  { id: 2, name: '腾讯科技', jobs: 96, logo: '' },
  { id: 3, name: '字节跳动', jobs: 156, logo: '' }
]);

const handleSearch = () => {
  ElMessage.success('搜索中...');
};

const resetSearch = () => {
  searchForm.value = { keyword: '', city: '', salary: '' };
};

const viewJobDetail = (job: any) => {
  ElMessage.info(`查看职位：${job.title}`);
};

const toggleFavorite = (job: any) => {
  job.isFavorite = !job.isFavorite;
  ElMessage.success(job.isFavorite ? '已收藏' : '已取消收藏');
};

const applyJob = (job: any) => {
  ElMessage.success(`正在申请：${job.title}`);
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
};
</script>

<style scoped>
.recruitment-browse {
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.search-card {
  margin-top: 24px;
}

.job-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.job-item {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.job-item:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

.job-main {
  display: flex;
  gap: 16px;
}

.job-logo {
  width: 60px;
  height: 60px;
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

.job-info {
  flex: 1;
}

.job-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
}

.job-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.job-tags {
  display: flex;
  gap: 8px;
}

.job-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
}

.job-salary {
  font-size: 20px;
  font-weight: 600;
  color: #f56c6c;
}

.job-actions {
  display: flex;
  gap: 8px;
}

.company-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.company-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.company-item:hover {
  background: #f5f7fa;
}

.company-item img {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: cover;
}

.company-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.company-jobs {
  font-size: 12px;
  color: #909399;
}
</style>

