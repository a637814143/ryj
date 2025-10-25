<template>
  <div class="job-intention">
    <el-page-header @back="router.back()" title="返回">
      <template #content>
        <span class="page-title">🎯 就业意向管理</span>
      </template>
    </el-page-header>

    <el-card shadow="hover" class="intention-card">
      <el-form :model="intentionForm" label-width="120px" class="intention-form">
        <el-divider content-position="left">
          <el-icon><Star /></el-icon>
          <span style="margin-left: 8px;">期望职位</span>
        </el-divider>

        <el-form-item label="期望职位">
          <el-select
            v-model="intentionForm.positions"
            multiple
            filterable
            allow-create
            placeholder="请选择或输入期望职位"
            style="width: 100%"
          >
            <el-option
              v-for="position in positionOptions"
              :key="position"
              :label="position"
              :value="position"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="职位类别">
          <el-checkbox-group v-model="intentionForm.categories">
            <el-checkbox label="技术开发">技术开发</el-checkbox>
            <el-checkbox label="产品设计">产品设计</el-checkbox>
            <el-checkbox label="运营管理">运营管理</el-checkbox>
            <el-checkbox label="市场营销">市场营销</el-checkbox>
            <el-checkbox label="人力资源">人力资源</el-checkbox>
            <el-checkbox label="财务会计">财务会计</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-divider content-position="left">
          <el-icon><Money /></el-icon>
          <span style="margin-left: 8px;">薪资待遇</span>
        </el-divider>

        <el-form-item label="期望薪资">
          <el-row :gutter="10">
            <el-col :span="11">
              <el-input-number
                v-model="intentionForm.salaryMin"
                :min="0"
                :max="100"
                :step="1"
                style="width: 100%"
              />
            </el-col>
            <el-col :span="2" style="text-align: center">
              <span>-</span>
            </el-col>
            <el-col :span="11">
              <el-input-number
                v-model="intentionForm.salaryMax"
                :min="0"
                :max="100"
                :step="1"
                style="width: 100%"
              />
            </el-col>
          </el-row>
          <div style="margin-top: 8px; color: #909399; font-size: 12px;">
            单位：K/月，如 8-12K 表示月薪 8000-12000 元
          </div>
        </el-form-item>

        <el-form-item label="薪资要求">
          <el-radio-group v-model="intentionForm.salaryNegotiable">
            <el-radio :label="false">按期望薪资</el-radio>
            <el-radio :label="true">面议</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-divider content-position="left">
          <el-icon><Location /></el-icon>
          <span style="margin-left: 8px;">工作地点</span>
        </el-divider>

        <el-form-item label="期望城市">
          <el-cascader
            v-model="intentionForm.cities"
            :options="cityOptions"
            :props="{ multiple: true, checkStrictly: true }"
            clearable
            placeholder="请选择期望工作城市"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="是否接受调剂">
          <el-switch
            v-model="intentionForm.acceptTransfer"
            active-text="接受"
            inactive-text="不接受"
          />
        </el-form-item>

        <el-divider content-position="left">
          <el-icon><Briefcase /></el-icon>
          <span style="margin-left: 8px;">工作类型</span>
        </el-divider>

        <el-form-item label="工作性质">
          <el-radio-group v-model="intentionForm.workType">
            <el-radio label="全职">全职</el-radio>
            <el-radio label="兼职">兼职</el-radio>
            <el-radio label="实习">实习</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="到岗时间">
          <el-select v-model="intentionForm.availableTime" placeholder="请选择到岗时间" style="width: 100%">
            <el-option label="随时到岗" value="随时到岗" />
            <el-option label="1周内" value="1周内" />
            <el-option label="2周内" value="2周内" />
            <el-option label="1个月内" value="1个月内" />
            <el-option label="3个月内" value="3个月内" />
          </el-select>
        </el-form-item>

        <el-form-item label="公司规模">
          <el-checkbox-group v-model="intentionForm.companySize">
            <el-checkbox label="20人以下">20人以下</el-checkbox>
            <el-checkbox label="20-99人">20-99人</el-checkbox>
            <el-checkbox label="100-499人">100-499人</el-checkbox>
            <el-checkbox label="500-999人">500-999人</el-checkbox>
            <el-checkbox label="1000人以上">1000人以上</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-divider content-position="left">
          <el-icon><Setting /></el-icon>
          <span style="margin-left: 8px;">其他要求</span>
        </el-divider>

        <el-form-item label="行业偏好">
          <el-select
            v-model="intentionForm.industries"
            multiple
            filterable
            placeholder="请选择期望行业"
            style="width: 100%"
          >
            <el-option label="互联网/IT" value="互联网/IT" />
            <el-option label="金融" value="金融" />
            <el-option label="教育培训" value="教育培训" />
            <el-option label="医疗健康" value="医疗健康" />
            <el-option label="制造业" value="制造业" />
            <el-option label="房地产" value="房地产" />
            <el-option label="零售/电商" value="零售/电商" />
            <el-option label="文化娱乐" value="文化娱乐" />
          </el-select>
        </el-form-item>

        <el-form-item label="福利要求">
          <el-checkbox-group v-model="intentionForm.benefits">
            <el-checkbox label="五险一金">五险一金</el-checkbox>
            <el-checkbox label="年终奖">年终奖</el-checkbox>
            <el-checkbox label="带薪年假">带薪年假</el-checkbox>
            <el-checkbox label="弹性工作">弹性工作</el-checkbox>
            <el-checkbox label="股票期权">股票期权</el-checkbox>
            <el-checkbox label="定期体检">定期体检</el-checkbox>
            <el-checkbox label="员工培训">员工培训</el-checkbox>
            <el-checkbox label="节日福利">节日福利</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="补充说明">
          <el-input
            v-model="intentionForm.remarks"
            type="textarea"
            :rows="4"
            placeholder="请输入其他补充说明"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" @click="saveIntention">保存就业意向</el-button>
          <el-button size="large" @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 推荐职位 -->
    <el-card shadow="hover" class="recommend-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">💡 根据您的意向推荐</span>
          <el-button text type="primary" @click="router.push('/student/recruitment')">
            查看更多 →
          </el-button>
        </div>
      </template>
      
      <el-row :gutter="16">
        <el-col
          v-for="job in recommendedJobs"
          :key="job.id"
          :span="8"
        >
          <el-card shadow="hover" class="job-card">
            <div class="job-header">
              <h3>{{ job.title }}</h3>
              <div class="job-salary">{{ job.salary }}</div>
            </div>
            <div class="job-company">
              <span>{{ job.company }}</span>
            </div>
            <div class="job-location">
              <el-icon><Location /></el-icon>
              <span>{{ job.location }}</span>
            </div>
            <div class="job-tags">
              <el-tag v-for="tag in job.tags" :key="tag" size="small">{{ tag }}</el-tag>
            </div>
            <el-button type="primary" size="small" style="width: 100%; margin-top: 12px;">
              立即申请
            </el-button>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Star,
  Money,
  Location,
  Briefcase,
  Setting
} from '@element-plus/icons-vue';

const router = useRouter();

const intentionForm = ref({
  positions: [] as string[],
  categories: [] as string[],
  salaryMin: 8,
  salaryMax: 15,
  salaryNegotiable: false,
  cities: [] as any[],
  acceptTransfer: false,
  workType: '全职',
  availableTime: '随时到岗',
  companySize: [] as string[],
  industries: [] as string[],
  benefits: [] as string[],
  remarks: ''
});

const positionOptions = [
  'Java开发工程师',
  '前端开发工程师',
  'Python开发工程师',
  '算法工程师',
  '产品经理',
  'UI设计师',
  '运营专员',
  '市场专员'
];

const cityOptions = [
  {
    value: '北京',
    label: '北京',
    children: [
      { value: '朝阳区', label: '朝阳区' },
      { value: '海淀区', label: '海淀区' },
      { value: '西城区', label: '西城区' }
    ]
  },
  {
    value: '上海',
    label: '上海',
    children: [
      { value: '浦东新区', label: '浦东新区' },
      { value: '徐汇区', label: '徐汇区' },
      { value: '黄浦区', label: '黄浦区' }
    ]
  },
  {
    value: '深圳',
    label: '深圳',
    children: [
      { value: '南山区', label: '南山区' },
      { value: '福田区', label: '福田区' },
      { value: '宝安区', label: '宝安区' }
    ]
  },
  {
    value: '杭州',
    label: '杭州',
    children: [
      { value: '西湖区', label: '西湖区' },
      { value: '滨江区', label: '滨江区' },
      { value: '余杭区', label: '余杭区' }
    ]
  }
];

const recommendedJobs = ref([
  {
    id: 1,
    title: 'Java开发工程师',
    company: '阿里巴巴',
    location: '杭州',
    salary: '15-25K',
    tags: ['Java', 'Spring Boot', '本科']
  },
  {
    id: 2,
    title: '前端开发工程师',
    company: '腾讯科技',
    location: '深圳',
    salary: '12-20K',
    tags: ['Vue', 'React', '2年经验']
  },
  {
    id: 3,
    title: 'Python开发',
    company: '字节跳动',
    location: '北京',
    salary: '18-30K',
    tags: ['Python', 'Django', '3年经验']
  }
]);

const saveIntention = () => {
  ElMessage.success('就业意向保存成功');
  // TODO: 调用API保存
};

const resetForm = () => {
  ElMessageBox.confirm('确定要重置表单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    intentionForm.value = {
      positions: [],
      categories: [],
      salaryMin: 8,
      salaryMax: 15,
      salaryNegotiable: false,
      cities: [],
      acceptTransfer: false,
      workType: '全职',
      availableTime: '随时到岗',
      companySize: [],
      industries: [],
      benefits: [],
      remarks: ''
    };
    ElMessage.info('已重置');
  });
};
</script>

<style scoped>
.job-intention {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.intention-card {
  margin-top: 24px;
}

.intention-form {
  max-width: 900px;
}

.recommend-card {
  margin-top: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
}

.job-card {
  height: 100%;
  transition: all 0.3s;
}

.job-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.job-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  flex: 1;
}

.job-salary {
  color: #f56c6c;
  font-size: 16px;
  font-weight: 600;
}

.job-company {
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.job-location {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #909399;
}

.job-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>

