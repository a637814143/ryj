<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { api } from '@/services/api'
import type { Interview, JobApplication, JobPosting } from '@/types'

const filters = reactive({
  keyword: '',
  location: '',
  workType: '',
})

const jobs = ref<JobPosting[]>([])
const selectedJobId = ref<number | null>(null)
const applications = ref<JobApplication[]>([])
const interviews = ref<Interview[]>([])
const loadingJobs = ref(false)
const detailLoading = ref(false)
const error = ref<string | null>(null)
const success = ref<string | null>(null)

const applyForm = reactive({
  studentId: '',
  resumeId: '',
  coverLetter: '',
})

const interviewForm = reactive({
  applicationId: '',
  scheduledTime: '',
  location: '',
  meetingLink: '',
})

const selectedJob = computed(() => jobs.value.find((job) => job.id === selectedJobId.value) ?? null)

const loadJobs = async () => {
  try {
    loadingJobs.value = true
    jobs.value = await api.getJobs({
      keyword: filters.keyword || undefined,
      location: filters.location || undefined,
      workType: filters.workType || undefined,
    })
    const firstJob = jobs.value[0]
    if (!selectedJobId.value && firstJob) {
      selectedJobId.value = firstJob.id
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  } finally {
    loadingJobs.value = false
  }
}

const loadJobDetails = async (id: number) => {
  try {
    detailLoading.value = true
    const [freshJob, jobApplications, jobInterviews] = await Promise.all([
      api.getJob(id),
      api.getJobApplications(id),
      api.getJobInterviews(id),
    ])
    const index = jobs.value.findIndex((item) => item.id === id)
    if (index >= 0) {
      jobs.value[index] = freshJob
    }
    applications.value = jobApplications
    interviews.value = jobInterviews
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  } finally {
    detailLoading.value = false
  }
}

watch(selectedJobId, (value) => {
  if (value) {
    loadJobDetails(value)
  } else {
    applications.value = []
    interviews.value = []
  }
})

onMounted(loadJobs)

const applyForJob = async () => {
  if (!selectedJobId.value) return
  try {
    const payload = {
      studentId: Number(applyForm.studentId),
      resumeId: Number(applyForm.resumeId),
      coverLetter: applyForm.coverLetter || undefined,
    }
    await api.applyForJob(selectedJobId.value, payload)
    success.value = '已提交申请，请及时跟进'
    applyForm.studentId = ''
    applyForm.resumeId = ''
    applyForm.coverLetter = ''
    await loadJobDetails(selectedJobId.value)
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  }
}

const scheduleInterview = async () => {
  if (!selectedJobId.value) return
  try {
    const payload = {
      applicationId: Number(interviewForm.applicationId),
      scheduledTime: interviewForm.scheduledTime || undefined,
      location: interviewForm.location || undefined,
      meetingLink: interviewForm.meetingLink || undefined,
    }
    await api.scheduleInterview(selectedJobId.value, payload)
    success.value = '面试已安排'
    Object.assign(interviewForm, { applicationId: '', scheduledTime: '', location: '', meetingLink: '' })
    await loadJobDetails(selectedJobId.value)
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  }
}
</script>

<template>
  <div class="jobs">
    <section class="jobs__filters">
      <h2>岗位筛选</h2>
      <form @submit.prevent="loadJobs">
        <label>
          关键字
          <input v-model="filters.keyword" placeholder="岗位名称或描述" />
        </label>
        <label>
          地点
          <input v-model="filters.location" placeholder="上海/北京等" />
        </label>
        <label>
          工作类型
          <input v-model="filters.workType" placeholder="全职/实习等" />
        </label>
        <button type="submit" :disabled="loadingJobs">{{ loadingJobs ? '查询中…' : '查询' }}</button>
      </form>
    </section>

    <section class="jobs__list">
      <header>
        <h2>岗位列表</h2>
        <span class="total">共 {{ jobs.length }} 个岗位</span>
      </header>
      <ul>
        <li
          v-for="job in jobs"
          :key="job.id"
          :class="{ active: job.id === selectedJobId }"
          @click="selectedJobId = job.id"
        >
          <h3>{{ job.title }}</h3>
          <p>{{ job.location }} · {{ job.salaryRange || '薪资面议' }}</p>
          <p class="status">状态：{{ job.status }}</p>
        </li>
      </ul>
    </section>

    <section class="jobs__detail" v-if="selectedJob">
      <header>
        <h2>{{ selectedJob.title }}</h2>
        <p class="sub">工作地点：{{ selectedJob.location }} · 发布时间：{{ selectedJob.publishedDate }}</p>
      </header>

      <div v-if="detailLoading" class="loading">正在加载岗位详情…</div>
      <div v-else class="detail-grid">
        <article class="card">
          <h3>岗位描述</h3>
          <p>{{ selectedJob.description || '暂无描述' }}</p>
          <p v-if="selectedJob.requirements?.length"><strong>任职要求：</strong>{{ selectedJob.requirements.join('；') }}</p>
        </article>

        <article class="card">
          <h3>投递情况</h3>
          <table v-if="applications.length" class="table">
            <thead>
              <tr>
                <th>申请编号</th>
                <th>学生ID</th>
                <th>简历ID</th>
                <th>状态</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in applications" :key="item.id">
                <td>{{ item.id }}</td>
                <td>{{ item.studentId }}</td>
                <td>{{ item.resumeId }}</td>
                <td>{{ item.status }}</td>
              </tr>
            </tbody>
          </table>
          <p v-else>暂无投递</p>
        </article>

        <article class="card">
          <h3>面试安排</h3>
          <table v-if="interviews.length" class="table">
            <thead>
              <tr>
                <th>面试编号</th>
                <th>申请编号</th>
                <th>时间</th>
                <th>状态</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in interviews" :key="item.id">
                <td>{{ item.id }}</td>
                <td>{{ item.applicationId }}</td>
                <td>{{ new Date(item.scheduledTime).toLocaleString() }}</td>
                <td>{{ item.status }}</td>
              </tr>
            </tbody>
          </table>
          <p v-else>尚未安排面试</p>
        </article>

        <article class="card">
          <h3>新增申请</h3>
          <form @submit.prevent="applyForJob" class="form-grid">
            <label>
              学生ID
              <input v-model="applyForm.studentId" required />
            </label>
            <label>
              简历ID
              <input v-model="applyForm.resumeId" required />
            </label>
            <label class="full">
              附言
              <textarea v-model="applyForm.coverLetter" rows="3"></textarea>
            </label>
            <button type="submit">提交申请</button>
          </form>
        </article>

        <article class="card">
          <h3>安排面试</h3>
          <form @submit.prevent="scheduleInterview" class="form-grid">
            <label>
              申请编号
              <input v-model="interviewForm.applicationId" required />
            </label>
            <label>
              面试时间
              <input v-model="interviewForm.scheduledTime" type="datetime-local" />
            </label>
            <label>
              面试地点
              <input v-model="interviewForm.location" />
            </label>
            <label>
              会议链接
              <input v-model="interviewForm.meetingLink" />
            </label>
            <button type="submit">保存安排</button>
          </form>
        </article>
      </div>
    </section>

    <aside class="jobs__feedback">
      <p v-if="success" class="success">{{ success }}</p>
      <p v-if="error" class="error">{{ error }}</p>
    </aside>
  </div>
</template>

<style scoped>
.jobs {
  display: grid;
  grid-template-columns: 260px 320px 1fr;
  gap: 20px;
  padding: 24px;
  align-items: start;
}

.jobs__filters,
.jobs__list,
.jobs__detail,
.jobs__feedback {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  box-shadow: inset 0 0 0 1px #e2e8f0;
}

.jobs__filters form,
.jobs__detail form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.jobs__filters input,
.jobs__detail input,
.jobs__detail textarea {
  width: 100%;
  border: 1px solid #cbd5f5;
  border-radius: 8px;
  padding: 8px 10px;
  font-size: 14px;
}

.jobs__filters button,
.jobs__detail button {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px;
  cursor: pointer;
}

.jobs__list ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.jobs__list li {
  background: #fff;
  border-radius: 10px;
  padding: 12px;
  border: 1px solid transparent;
  cursor: pointer;
}

.jobs__list li.active {
  border-color: #2563eb;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1);
}

.status {
  color: #475569;
  font-size: 13px;
}

.jobs__detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: transparent;
  box-shadow: none;
  padding: 0;
}

.jobs__detail header {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  box-shadow: inset 0 0 0 1px #e2e8f0;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 16px;
}

.card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th,
.table td {
  border-bottom: 1px solid #e2e8f0;
  padding: 8px;
  text-align: left;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.form-grid .full {
  grid-column: 1 / -1;
}

.loading {
  padding: 12px;
  border-radius: 10px;
  background: #e0f2fe;
  color: #0369a1;
}

.success {
  color: #047857;
}

.error {
  color: #b91c1c;
}

@media (max-width: 1280px) {
  .jobs {
    grid-template-columns: 1fr;
  }

  .jobs__list,
  .jobs__filters {
    order: -1;
  }
}
</style>
