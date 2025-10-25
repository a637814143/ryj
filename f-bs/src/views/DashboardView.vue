<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { api } from '@/services/api'
import type { DashboardSummary } from '@/types'

const summary = ref<DashboardSummary | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

const loadData = async () => {
  try {
    loading.value = true
    summary.value = await api.getDashboardSummary()
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <div class="dashboard">
    <header class="dashboard__header">
      <div>
        <h1>大学生就业管理驾驶舱</h1>
        <p class="subtitle">掌握学生、企业与岗位的整体态势，辅助就业服务决策</p>
      </div>
      <button type="button" class="refresh" :disabled="loading" @click="loadData">
        {{ loading ? '刷新中…' : '刷新数据' }}
      </button>
    </header>

    <div v-if="error" class="error">{{ error }}</div>

    <section v-if="summary" class="metrics">
      <article class="metric">
        <h2>在库学生</h2>
        <p class="metric__value">{{ summary.students }}</p>
        <p class="metric__desc">已建档的毕业生数量</p>
      </article>
      <article class="metric">
        <h2>合作企业</h2>
        <p class="metric__value">{{ summary.employers }}</p>
        <p class="metric__desc">已入驻并发布岗位的企业数量</p>
      </article>
      <article class="metric">
        <h2>开放岗位</h2>
        <p class="metric__value">{{ summary.openJobs }}</p>
        <p class="metric__desc">当前正在招聘的岗位数</p>
      </article>
      <article class="metric">
        <h2>待处理申请</h2>
        <p class="metric__value">{{ summary.pendingApplications }}</p>
        <p class="metric__desc">学生投递后等待跟进的申请数量</p>
      </article>
      <article class="metric">
        <h2>已排面试</h2>
        <p class="metric__value">{{ summary.scheduledInterviews }}</p>
        <p class="metric__desc">即将开展的面试安排</p>
      </article>
      <article class="metric">
        <h2>指导教师</h2>
        <p class="metric__value">{{ summary.teachers }}</p>
        <p class="metric__desc">参与就业指导的教师数量</p>
      </article>
    </section>

    <section class="tips">
      <h2>快速提示</h2>
      <ul>
        <li>在“学生中心”可以完善简历、就业意向以及查看投递情况。</li>
        <li>“岗位广场”支持关键字与地域筛选，快速匹配目标岗位。</li>
        <li>通过“企业管理”掌握企业联系人及发布的岗位，及时跟进。</li>
        <li>教师可在“指导服务”中为学生添加辅导记录。</li>
      </ul>
    </section>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 24px;
}

.dashboard__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.subtitle {
  margin-top: 4px;
  color: #5f6b7a;
}

.refresh {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 10px 16px;
  cursor: pointer;
  font-size: 14px;
}

.refresh:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error {
  padding: 12px 16px;
  border-radius: 6px;
  background: #fee2e2;
  color: #b91c1c;
}

.metrics {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.metric {
  border-radius: 12px;
  background: #f8fafc;
  padding: 18px;
  box-shadow: inset 0 0 0 1px #e2e8f0;
}

.metric__value {
  font-size: 32px;
  font-weight: 700;
  color: #0f172a;
  margin: 12px 0 4px;
}

.metric__desc {
  color: #64748b;
  font-size: 13px;
}

.tips {
  border-radius: 12px;
  background: #fff7ed;
  padding: 20px;
  border: 1px solid #fed7aa;
}

.tips ul {
  padding-left: 20px;
  margin: 10px 0 0;
  color: #92400e;
}
</style>
