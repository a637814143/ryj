<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { api } from '@/services/api'
import type { Employer, JobPosting } from '@/types'

const employers = ref<Employer[]>([])
const selectedId = ref<number | null>(null)
const jobs = ref<JobPosting[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

const loadEmployers = async () => {
  try {
    loading.value = true
    employers.value = await api.getEmployers()
    const firstEmployer = employers.value[0]
    if (!selectedId.value && firstEmployer) {
      selectedId.value = firstEmployer.id
      await loadJobs(firstEmployer.id)
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  } finally {
    loading.value = false
  }
}

const loadJobs = async (employerId: number) => {
  try {
    jobs.value = await api.getEmployerJobs(employerId)
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  }
}

const selectEmployer = async (employerId: number) => {
  selectedId.value = employerId
  await loadJobs(employerId)
}

onMounted(loadEmployers)
</script>

<template>
  <div class="employers">
    <section class="employers__list">
      <header>
        <h2>合作企业</h2>
        <button type="button" :disabled="loading" @click="loadEmployers">{{ loading ? '加载中…' : '刷新' }}</button>
      </header>
      <ul>
        <li
          v-for="employer in employers"
          :key="employer.id"
          :class="{ active: employer.id === selectedId }"
          @click="selectEmployer(employer.id)"
        >
          <h3>{{ employer.name }}</h3>
          <p>{{ employer.contactPerson }} · {{ employer.contactEmail }}</p>
        </li>
      </ul>
    </section>

    <section class="employers__detail" v-if="selectedId">
      <article v-if="jobs.length" class="card">
        <h3>发布中的岗位</h3>
        <ul>
          <li v-for="job in jobs" :key="job.id">
            <div class="title">{{ job.title }} <span>{{ job.status }}</span></div>
            <p>{{ job.location }} · {{ job.salaryRange || '薪资面议' }}</p>
            <p class="desc">{{ job.description }}</p>
          </li>
        </ul>
      </article>
      <p v-else class="empty">该企业暂未发布岗位</p>
    </section>

    <aside class="employers__info">
      <p v-if="error" class="error">{{ error }}</p>
      <p class="hint">提示：可结合岗位广场模块查看岗位详情并进行投递管理。</p>
    </aside>
  </div>
</template>

<style scoped>
.employers {
  display: grid;
  grid-template-columns: 320px 1fr 240px;
  gap: 20px;
  padding: 24px;
}

.employers__list,
.employers__detail,
.employers__info {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  box-shadow: inset 0 0 0 1px #e2e8f0;
}

.employers__list header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.employers__list button {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 8px 12px;
  cursor: pointer;
}

.employers__list ul {
  list-style: none;
  margin: 16px 0 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.employers__list li {
  background: #fff;
  border-radius: 10px;
  padding: 12px;
  border: 1px solid transparent;
  cursor: pointer;
}

.employers__list li.active {
  border-color: #2563eb;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1);
}

.card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  padding: 16px;
}

.card ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card li {
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 12px;
}

.card li:last-child {
  border-bottom: none;
}

.card .title {
  display: flex;
  justify-content: space-between;
  font-weight: 600;
}

.card .title span {
  font-size: 12px;
  color: #475569;
}

.card .desc {
  color: #475569;
  font-size: 14px;
}

.empty {
  color: #475569;
}

.error {
  color: #b91c1c;
}

.hint {
  color: #475569;
}

@media (max-width: 1200px) {
  .employers {
    grid-template-columns: 1fr;
  }
}
</style>
