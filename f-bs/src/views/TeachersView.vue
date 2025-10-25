<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { api } from '@/services/api'
import type { Teacher } from '@/types'

const teachers = ref<Teacher[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const success = ref<string | null>(null)

const noteForm = reactive({
  teacherId: '',
  studentId: '',
  note: '',
})

const loadTeachers = async () => {
  try {
    loading.value = true
    teachers.value = await api.getTeachers()
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  } finally {
    loading.value = false
  }
}

const addGuidance = async () => {
  if (!noteForm.teacherId) return
  try {
    await api.addGuidance(Number(noteForm.teacherId), {
      studentId: Number(noteForm.studentId),
      note: noteForm.note,
    })
    success.value = '指导记录已保存'
    noteForm.studentId = ''
    noteForm.note = ''
    await loadTeachers()
  } catch (err) {
    error.value = err instanceof Error ? err.message : String(err)
  }
}

onMounted(loadTeachers)
</script>

<template>
  <div class="teachers">
    <section class="teachers__list">
      <header>
        <h2>指导教师</h2>
        <button type="button" :disabled="loading" @click="loadTeachers">{{ loading ? '加载中…' : '刷新' }}</button>
      </header>
      <ul>
        <li v-for="teacher in teachers" :key="teacher.id">
          <header>
            <h3>{{ teacher.name }}</h3>
            <span>{{ teacher.department }}</span>
          </header>
          <p>邮箱：{{ teacher.email || '未填写' }}</p>
          <p>电话：{{ teacher.phone || '未填写' }}</p>
          <div class="notes" v-if="Object.keys(teacher.guidanceNotes || {}).length">
            <h4>指导记录</h4>
            <ul>
              <li v-for="(note, studentId) in teacher.guidanceNotes" :key="studentId">
                <strong>学生 {{ studentId }}：</strong>{{ note }}
              </li>
            </ul>
          </div>
        </li>
      </ul>
    </section>

    <section class="teachers__form">
      <h2>添加指导记录</h2>
      <form @submit.prevent="addGuidance">
        <label>
          教师
          <select v-model="noteForm.teacherId" required>
            <option value="" disabled>请选择教师</option>
            <option v-for="teacher in teachers" :key="teacher.id" :value="teacher.id">{{ teacher.name }}</option>
          </select>
        </label>
        <label>
          学生ID
          <input v-model="noteForm.studentId" required />
        </label>
        <label>
          指导内容
          <textarea v-model="noteForm.note" rows="4" required></textarea>
        </label>
        <button type="submit">保存</button>
      </form>
      <p v-if="success" class="success">{{ success }}</p>
      <p v-if="error" class="error">{{ error }}</p>
    </section>
  </div>
</template>

<style scoped>
.teachers {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 20px;
  padding: 24px;
}

.teachers__list,
.teachers__form {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  box-shadow: inset 0 0 0 1px #e2e8f0;
}

.teachers__list header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.teachers__list ul {
  list-style: none;
  padding: 0;
  margin: 16px 0 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.teachers__list li {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e2e8f0;
}

.teachers__list li header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notes ul {
  list-style: disc;
  padding-left: 20px;
  margin: 8px 0 0;
}

.teachers__form form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.teachers__form input,
.teachers__form select,
.teachers__form textarea {
  width: 100%;
  border: 1px solid #cbd5f5;
  border-radius: 8px;
  padding: 8px 10px;
  font-size: 14px;
}

.teachers__form button {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px;
  cursor: pointer;
}

.success {
  color: #047857;
}

.error {
  color: #b91c1c;
}

@media (max-width: 1100px) {
  .teachers {
    grid-template-columns: 1fr;
  }
}
</style>
