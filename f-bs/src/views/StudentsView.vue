<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { api } from '@/services/api'
import type { EmploymentIntention, JobApplication, Resume, Student } from '@/types'

const students = ref<Student[]>([])
const selectedId = ref<number | null>(null)
const resumes = ref<Resume[]>([])
const applications = ref<JobApplication[]>([])
const loadingStudents = ref(false)
const detailLoading = ref(false)
const error = ref<string | null>(null)
const successMessage = ref<string | null>(null)

const studentForm = reactive<Partial<Student>>({
  name: '',
  gender: '',
  age: undefined,
  major: '',
  email: '',
  phone: '',
})

const intentionForm = reactive<EmploymentIntention>({
  expectedPosition: '',
  salaryRange: '',
  preferredCities: [],
  workType: '',
  notes: '',
})

const preferredCitiesInput = ref('')

const resumeForm = reactive<Partial<Resume>>({
  title: '',
  summary: '',
  portfolioUrl: '',
})

const resumeSkillsInput = ref('')
const resumeExperiencesInput = ref('')

const selectedStudent = computed(() => students.value.find((item) => item.id === selectedId.value) ?? null)

const loadStudents = async () => {
  try {
    loadingStudents.value = true
    students.value = await api.getStudents()
    const firstStudent = students.value[0]
    if (!selectedId.value && firstStudent) {
      selectedId.value = firstStudent.id
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  } finally {
    loadingStudents.value = false
  }
}

const loadStudentDetails = async (id: number) => {
  try {
    detailLoading.value = true
    const [freshStudent, studentResumes, studentApplications] = await Promise.all([
      api.getStudent(id),
      api.getStudentResumes(id),
      api.getStudentApplications(id),
    ])
    const index = students.value.findIndex((item) => item.id === id)
    if (index >= 0) {
      students.value[index] = freshStudent
    }
    resumes.value = studentResumes
    applications.value = studentApplications
    Object.assign(intentionForm, freshStudent.employmentIntention ?? {
      expectedPosition: '',
      salaryRange: '',
      preferredCities: [],
      workType: '',
      notes: '',
    })
    preferredCitiesInput.value = (intentionForm.preferredCities ?? []).join(',')
    resumeSkillsInput.value = ''
    resumeExperiencesInput.value = ''
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  } finally {
    detailLoading.value = false
  }
}

watch(selectedId, (value) => {
  if (value) {
    loadStudentDetails(value)
  }
})

onMounted(loadStudents)

const createStudent = async () => {
  try {
    const created = await api.createStudent({ ...studentForm })
    students.value.push(created)
    selectedId.value = created.id
    successMessage.value = `已新增学生 ${created.name}`
    Object.assign(studentForm, { name: '', gender: '', age: undefined, major: '', email: '', phone: '' })
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  }
}

const updateIntention = async () => {
  if (!selectedId.value) return
  try {
    const payload = {
      ...intentionForm,
      preferredCities: preferredCitiesInput.value
        .split(',')
        .map((city) => city.trim())
        .filter((city) => city.length > 0),
    }
    intentionForm.preferredCities = payload.preferredCities
    await api.updateIntention(selectedId.value, payload)
    successMessage.value = '就业意向已更新'
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  }
}

const createResume = async () => {
  if (!selectedId.value) return
  try {
    const payload = {
      ...resumeForm,
      skills: resumeSkillsInput.value
        .split(',')
        .map((skill) => skill.trim())
        .filter((skill) => skill.length > 0),
      experiences: resumeExperiencesInput.value
        .split(';')
        .map((exp) => exp.trim())
        .filter((exp) => exp.length > 0),
    }
    const created = await api.createResume(selectedId.value, payload)
    resumes.value.push(created)
    successMessage.value = '简历已创建'
    Object.assign(resumeForm, { title: '', summary: '', portfolioUrl: '' })
    resumeSkillsInput.value = ''
    resumeExperiencesInput.value = ''
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  }
}

const splitByLine = (value?: string[]) => (value ?? []).join('、')

const displayPreferredCities = computed(() => {
  return (selectedStudent.value?.employmentIntention?.preferredCities ?? []).join('、') || '未填写'
})
</script>

<template>
  <div class="students">
    <section class="students__list">
      <header>
        <h2>学生列表</h2>
        <button type="button" class="refresh" :disabled="loadingStudents" @click="loadStudents">
          {{ loadingStudents ? '加载中…' : '刷新' }}
        </button>
      </header>
      <ul>
        <li
          v-for="student in students"
          :key="student.id"
          :class="{ active: student.id === selectedId }"
          @click="selectedId = student.id"
        >
          <h3>{{ student.name }}</h3>
          <p>{{ student.major }} · {{ student.email }}</p>
        </li>
      </ul>
    </section>

    <section class="students__detail" v-if="selectedStudent">
      <header>
        <h2>{{ selectedStudent.name }}的档案</h2>
      </header>

      <div v-if="detailLoading" class="loading">正在加载详情…</div>
      <div v-else>
        <article class="card">
          <h3>基础信息</h3>
          <dl>
            <div>
              <dt>性别</dt>
              <dd>{{ selectedStudent.gender || '未填写' }}</dd>
            </div>
            <div>
              <dt>年龄</dt>
              <dd>{{ selectedStudent.age ?? '未填写' }}</dd>
            </div>
            <div>
              <dt>专业</dt>
              <dd>{{ selectedStudent.major || '未填写' }}</dd>
            </div>
            <div>
              <dt>邮箱</dt>
              <dd>{{ selectedStudent.email || '未填写' }}</dd>
            </div>
            <div>
              <dt>电话</dt>
              <dd>{{ selectedStudent.phone || '未填写' }}</dd>
            </div>
            <div>
              <dt>个人简介</dt>
              <dd>{{ selectedStudent.biography || '未填写' }}</dd>
            </div>
            <div>
              <dt>教育经历</dt>
              <dd>{{ splitByLine(selectedStudent.educationHistory) || '未填写' }}</dd>
            </div>
            <div>
              <dt>实践经历</dt>
              <dd>{{ splitByLine(selectedStudent.practiceExperience) || '未填写' }}</dd>
            </div>
            <div>
              <dt>获奖情况</dt>
              <dd>{{ splitByLine(selectedStudent.awards) || '未填写' }}</dd>
            </div>
          </dl>
        </article>

        <article class="card">
          <h3>就业意向</h3>
          <ul class="intention">
            <li><strong>目标岗位：</strong>{{ selectedStudent.employmentIntention?.expectedPosition || '未填写' }}</li>
            <li><strong>期望薪资：</strong>{{ selectedStudent.employmentIntention?.salaryRange || '未填写' }}</li>
            <li><strong>期望城市：</strong>{{ displayPreferredCities }}</li>
            <li><strong>工作类型：</strong>{{ selectedStudent.employmentIntention?.workType || '未填写' }}</li>
            <li><strong>备注：</strong>{{ selectedStudent.employmentIntention?.notes || '未填写' }}</li>
          </ul>
        </article>

        <article class="card">
          <h3>简历管理</h3>
          <ul class="resume-list">
            <li v-for="resume in resumes" :key="resume.id">
              <header>
                <strong>{{ resume.title }}</strong>
                <span>{{ resume.portfolioUrl || '暂无作品链接' }}</span>
              </header>
              <p v-if="resume.summary">{{ resume.summary }}</p>
              <p v-if="resume.skills?.length"><strong>技能：</strong>{{ resume.skills.join('、') }}</p>
              <p v-if="resume.experiences?.length"><strong>经历：</strong>{{ resume.experiences.join('；') }}</p>
            </li>
          </ul>
        </article>

        <article class="card">
          <h3>投递记录</h3>
          <table v-if="applications.length" class="table">
            <thead>
              <tr>
                <th>申请编号</th>
                <th>岗位</th>
                <th>状态</th>
                <th>投递时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="application in applications" :key="application.id">
                <td>{{ application.id }}</td>
                <td>{{ application.jobId }}</td>
                <td>{{ application.status }}</td>
                <td>{{ new Date(application.appliedAt).toLocaleString() }}</td>
              </tr>
            </tbody>
          </table>
          <p v-else>暂无投递记录</p>
        </article>
      </div>
    </section>

    <aside class="students__actions">
      <section class="card">
        <h3>新增学生</h3>
        <form @submit.prevent="createStudent">
          <label>
            姓名
            <input v-model="studentForm.name" required />
          </label>
          <label>
            性别
            <input v-model="studentForm.gender" placeholder="男/女" />
          </label>
          <label>
            年龄
            <input v-model.number="studentForm.age" type="number" min="15" max="60" />
          </label>
          <label>
            专业
            <input v-model="studentForm.major" />
          </label>
          <label>
            邮箱
            <input v-model="studentForm.email" type="email" />
          </label>
          <label>
            电话
            <input v-model="studentForm.phone" />
          </label>
          <button type="submit">创建</button>
        </form>
      </section>

      <section class="card" v-if="selectedId">
        <h3>更新就业意向</h3>
        <form @submit.prevent="updateIntention">
          <label>
            目标岗位
            <input v-model="intentionForm.expectedPosition" />
          </label>
          <label>
            期望薪资
            <input v-model="intentionForm.salaryRange" placeholder="如 15k-20k" />
          </label>
          <label>
            期望城市（以逗号分隔）
            <input v-model="preferredCitiesInput" placeholder="如 上海,杭州" />
          </label>
          <label>
            工作类型
            <input v-model="intentionForm.workType" placeholder="全职/实习等" />
          </label>
          <label>
            补充说明
            <textarea v-model="intentionForm.notes" rows="3"></textarea>
          </label>
          <button type="submit">保存意向</button>
        </form>
      </section>

      <section class="card" v-if="selectedId">
        <h3>新增简历</h3>
        <form @submit.prevent="createResume">
          <label>
            标题
            <input v-model="resumeForm.title" required />
          </label>
          <label>
            简介
            <textarea v-model="resumeForm.summary" rows="3"></textarea>
          </label>
          <label>
            技能（逗号分隔）
            <input v-model="resumeSkillsInput" placeholder="如 Python,SQL" />
          </label>
          <label>
            经历（分号分隔）
            <input v-model="resumeExperiencesInput" placeholder="经历1；经历2" />
          </label>
          <label>
            作品链接
            <input v-model="resumeForm.portfolioUrl" type="url" />
          </label>
          <button type="submit">创建简历</button>
        </form>
      </section>

      <p v-if="successMessage" class="success">{{ successMessage }}</p>
      <p v-if="error" class="error">{{ error }}</p>
    </aside>
  </div>
</template>

<style scoped>
.students {
  display: grid;
  grid-template-columns: 260px 1fr 320px;
  gap: 20px;
  padding: 24px;
}

.students__list {
  border-radius: 12px;
  background: #f8fafc;
  box-shadow: inset 0 0 0 1px #e2e8f0;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.students__list header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.refresh {
  background: transparent;
  border: 1px solid #3b82f6;
  color: #2563eb;
  padding: 4px 12px;
  border-radius: 6px;
  cursor: pointer;
}

.students__list ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.students__list li {
  border-radius: 10px;
  padding: 10px 12px;
  cursor: pointer;
  background: #fff;
  border: 1px solid transparent;
}

.students__list li.active {
  border-color: #2563eb;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1);
}

.students__detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.loading {
  padding: 12px;
  background: #e0f2fe;
  border-radius: 8px;
  color: #0c4a6e;
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

.card h3 {
  margin: 0;
}

.card form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card input,
.card textarea {
  width: 100%;
  border: 1px solid #cbd5f5;
  border-radius: 8px;
  padding: 8px 10px;
  font-size: 14px;
}

.card button {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px;
  cursor: pointer;
}

.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.table th,
.table td {
  padding: 8px;
  border-bottom: 1px solid #e2e8f0;
  text-align: left;
}

.resume-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 0;
  margin: 0;
  list-style: none;
}

.resume-list li {
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 12px;
  background: #f8fafc;
}

.resume-list header {
  display: flex;
  justify-content: space-between;
  color: #475569;
}

.success {
  color: #047857;
}

.error {
  color: #b91c1c;
}

@media (max-width: 1200px) {
  .students {
    grid-template-columns: 1fr;
  }

  .students__actions {
    order: -1;
  }
}
</style>
