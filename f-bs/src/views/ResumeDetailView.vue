<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchResumeDetail, type ResumeDetail } from '../api/student'

const route = useRoute()
const router = useRouter()

const resumeDetail = ref<ResumeDetail | null>(null)
const loading = ref(false)
const error = ref('')

const resumeId = computed(() => {
  const id = route.query.id
  if (typeof id === 'string') {
    const parsed = Number.parseInt(id, 10)
    return Number.isNaN(parsed) ? null : parsed
  }
  return null
})

const formatDate = (dateStr: string | null) => {
  if (!dateStr) return '未填写'
  const date = new Date(dateStr)
  if (Number.isNaN(date.getTime())) return dateStr
  return date.toLocaleDateString('zh-CN')
}

const loadResumeDetail = async (id: number) => {
  loading.value = true
  error.value = ''
  try {
    resumeDetail.value = await fetchResumeDetail(id)
  } catch (err) {
    console.error(err)
    error.value = (err as Error).message || '加载简历失败'
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

onMounted(async () => {
  if (resumeId.value) {
    await loadResumeDetail(resumeId.value)
  } else {
    error.value = '无效的简历ID'
  }
})
</script>

<template>
  <div class="resume-detail">
    <header class="page-header">
      <div class="title-block">
        <h1>简历详情</h1>
        <p class="subtitle">查看候选人简历信息</p>
      </div>
      <button class="back-btn" @click="goBack">返回</button>
    </header>

    <div v-if="loading" class="loading-box">正在加载简历...</div>

    <div v-else-if="error" class="error-box">{{ error }}</div>

    <div v-else-if="resumeDetail" class="resume-content">
      <!-- 简历基本信息 -->
      <section class="resume-section">
        <div class="section-header">
          <h2>{{ resumeDetail.resume.title }}</h2>
          <div class="meta-info">
            <span>创建时间：{{ formatDate(resumeDetail.resume.createdAt) }}</span>
            <span>更新时间：{{ formatDate(resumeDetail.resume.updatedAt) }}</span>
          </div>
        </div>
        
        <div v-if="resumeDetail.resume.summary" class="summary-box">
          <h3>个人简介</h3>
          <p>{{ resumeDetail.resume.summary }}</p>
        </div>

        <div v-if="resumeDetail.resume.portfolioUrl" class="portfolio-box">
          <h3>作品集链接</h3>
          <a :href="resumeDetail.resume.portfolioUrl" target="_blank" rel="noopener noreferrer">
            {{ resumeDetail.resume.portfolioUrl }}
          </a>
        </div>
      </section>

      <!-- 工作经历 -->
      <section v-if="resumeDetail.experiences.length > 0" class="resume-section">
        <h3 class="section-title">工作经历</h3>
        <div class="experience-list">
          <article
            v-for="exp in resumeDetail.experiences"
            :key="exp.id"
            class="experience-card"
          >
            <div class="experience-header">
              <h4>{{ exp.title }}</h4>
              <span class="organization">{{ exp.organization }}</span>
            </div>
            <div class="experience-period">
              {{ formatDate(exp.startDate) }} - {{ formatDate(exp.endDate) }}
            </div>
            <p v-if="exp.description" class="experience-description">
              {{ exp.description }}
            </p>
          </article>
        </div>
      </section>

      <!-- 技能 -->
      <section v-if="resumeDetail.skills.length > 0" class="resume-section">
        <h3 class="section-title">技能特长</h3>
        <div class="skills-grid">
          <div
            v-for="(skill, index) in resumeDetail.skills"
            :key="index"
            class="skill-card"
          >
            <div class="skill-name">{{ skill.skill }}</div>
            <div v-if="skill.proficiency !== null" class="skill-proficiency">
              <div class="proficiency-bar">
                <div
                  class="proficiency-fill"
                  :style="{ width: `${skill.proficiency}%` }"
                ></div>
              </div>
              <span class="proficiency-value">{{ skill.proficiency }}%</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 空状态提示 -->
      <div v-if="resumeDetail.experiences.length === 0 && resumeDetail.skills.length === 0" class="empty-state">
        <p>该简历暂无详细内容</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.resume-detail {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem 1.5rem 4rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1.5rem;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 2px solid #e5e7eb;
}

.title-block h1 {
  margin: 0;
  font-size: 2rem;
  font-weight: 700;
  color: #111827;
}

.subtitle {
  margin: 0.5rem 0 0;
  color: #6b7280;
  font-size: 0.95rem;
}

.back-btn {
  padding: 0.5rem 1.25rem;
  border: 1px solid #d1d5db;
  border-radius: 0.5rem;
  background: white;
  color: #374151;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.back-btn:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

.loading-box {
  padding: 3rem;
  text-align: center;
  color: #6b7280;
  font-size: 1.1rem;
}

.error-box {
  padding: 1.5rem;
  border: 1px solid #fca5a5;
  border-radius: 0.5rem;
  background: #fee2e2;
  color: #991b1b;
  text-align: center;
}

.resume-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.resume-section {
  padding: 1.75rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.75rem;
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.section-header h2 {
  margin: 0 0 1rem;
  font-size: 1.75rem;
  font-weight: 700;
  color: #111827;
}

.meta-info {
  display: flex;
  gap: 1.5rem;
  margin-top: 0.5rem;
  font-size: 0.85rem;
  color: #6b7280;
}

.summary-box,
.portfolio-box {
  margin-top: 1.5rem;
}

.summary-box h3,
.portfolio-box h3,
.section-title {
  margin: 0 0 0.75rem;
  font-size: 1.1rem;
  font-weight: 600;
  color: #374151;
}

.summary-box p {
  margin: 0;
  line-height: 1.6;
  color: #4b5563;
  white-space: pre-wrap;
}

.portfolio-box a {
  color: #2563eb;
  text-decoration: none;
  word-break: break-all;
}

.portfolio-box a:hover {
  text-decoration: underline;
}

.experience-list {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.experience-card {
  padding: 1.25rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  background: #f9fafb;
}

.experience-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

.experience-header h4 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #111827;
}

.organization {
  font-size: 0.9rem;
  color: #6b7280;
}

.experience-period {
  font-size: 0.85rem;
  color: #6b7280;
  margin-bottom: 0.75rem;
}

.experience-description {
  margin: 0;
  line-height: 1.6;
  color: #4b5563;
  white-space: pre-wrap;
}

.skills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
}

.skill-card {
  padding: 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  background: #f9fafb;
}

.skill-name {
  font-weight: 600;
  color: #111827;
  margin-bottom: 0.5rem;
}

.skill-proficiency {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.proficiency-bar {
  flex: 1;
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.proficiency-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #2563eb);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.proficiency-value {
  font-size: 0.85rem;
  color: #6b7280;
  min-width: 40px;
  text-align: right;
}

.empty-state {
  padding: 3rem;
  text-align: center;
  color: #9ca3af;
  font-size: 1rem;
}

@media (max-width: 768px) {
  .resume-detail {
    padding: 1.5rem 1rem;
  }

  .page-header {
    flex-direction: column;
    align-items: stretch;
  }

  .title-block h1 {
    font-size: 1.5rem;
  }

  .back-btn {
    align-self: flex-start;
  }

  .experience-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .skills-grid {
    grid-template-columns: 1fr;
  }

  .meta-info {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>

