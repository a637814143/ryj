<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getTeacherByUserId } from '@/api/teacher'

type TeacherRecord = {
  id: number
  userId: number
  department: string | null
  email: string | null
  phone: string | null
}

type GuidanceRecord = {
  id?: number
  teacherId: number
  studentId: number
  studentName?: string
  note: string
  createdAt?: string
}

type Student = {
  id: number
  name: string
  major: string
}

const teacherRecord = ref<TeacherRecord | null>(null)
const guidanceRecords = ref<GuidanceRecord[]>([])
const guidedStudents = ref<Student[]>([])
const loading = ref(true)
const submitting = ref(false)

// 表单数据
const showAddDialog = ref(false)
const formData = ref({
  studentId: null as number | null,
  note: ''
})

const message = ref<{ type: 'success' | 'error'; text: string } | null>(null)
let messageTimer: number | undefined

const showMessage = (type: 'success' | 'error', text: string) => {
  message.value = { type, text }
  if (messageTimer) {
    clearTimeout(messageTimer)
  }
  messageTimer = window.setTimeout(() => {
    message.value = null
  }, 3000)
}

const loadData = async () => {
  const stored = localStorage.getItem('userInfo')
  if (!stored) {
    showMessage('error', '请先登录')
    loading.value = false
    return
  }

  try {
    const userInfo = JSON.parse(stored)
    teacherRecord.value = await getTeacherByUserId(userInfo.id)

    // 加载指导记录
    const response = await fetch(
      `http://localhost:8080/api/teacher-guidance?teacherId=${teacherRecord.value.id}&size=50`
    )
    const result = await response.json()
    if (result.code === 200) {
      guidanceRecords.value = result.data.records || []
    }

    // 加载结对学生（从仪表板数据获取）
    const dashboardResponse = await fetch(
      `http://localhost:8080/api/teachers/${teacherRecord.value.id}/dashboard`
    )
    const dashboardResult = await dashboardResponse.json()
    if (dashboardResult.code === 200) {
      guidedStudents.value = dashboardResult.data.guidedStudents.map((s: any) => ({
        id: s.studentId,
        name: s.studentName,
        major: s.major || '未知专业'
      }))
    }
  } catch (error: any) {
    showMessage('error', error.message || '数据加载失败')
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  formData.value = {
    studentId: null,
    note: ''
  }
  showAddDialog.value = true
}

const closeAddDialog = () => {
  showAddDialog.value = false
}

const submitGuidance = async () => {
  if (!formData.value.studentId || !formData.value.note.trim()) {
    showMessage('error', '请填写完整的指导信息')
    return
  }

  submitting.value = true
  try {
    const response = await fetch('http://localhost:8080/api/teacher-guidance', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        teacherId: teacherRecord.value?.id,
        studentId: formData.value.studentId,
        note: formData.value.note.trim()
      })
    })

    const result = await response.json()
    if (result.code === 200) {
      showMessage('success', '指导记录添加成功')
      closeAddDialog()
      await loadData()
    } else {
      showMessage('error', result.message || '添加失败')
    }
  } catch (error: any) {
    showMessage('error', error.message || '添加失败')
  } finally {
    submitting.value = false
  }
}

const deleteGuidance = async (id: number) => {
  if (!confirm('确定要删除这条指导记录吗？')) {
    return
  }

  try {
    const response = await fetch(`http://localhost:8080/api/teacher-guidance/${id}`, {
      method: 'DELETE'
    })

    const result = await response.json()
    if (result.code === 200) {
      showMessage('success', '删除成功')
      await loadData()
    } else {
      showMessage('error', result.message || '删除失败')
    }
  } catch (error: any) {
    showMessage('error', error.message || '删除失败')
  }
}

const formatDateTime = (dateStr: string | undefined) => {
  if (!dateStr) return '—'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="guidance-view">
    <!-- 顶部导航 -->
    <nav class="teacher-nav">
      <div class="nav-container">
        <div class="nav-logo">
          <span class="logo-icon">🎓</span>
          <span class="logo-text">教师工作台</span>
        </div>
        <div class="nav-links">
          <router-link to="/teacher/overview" class="nav-link" active-class="active">
            <span class="link-icon">📊</span>
            <span>仪表板</span>
          </router-link>
          <router-link to="/teacher/guidance" class="nav-link" active-class="active">
            <span class="link-icon">📝</span>
            <span>指导记录</span>
          </router-link>
          <router-link to="/teacher/statistics" class="nav-link" active-class="active">
            <span class="link-icon">📈</span>
            <span>统计分析</span>
          </router-link>
          <router-link to="/teacher/profile" class="nav-link" active-class="active">
            <span class="link-icon">🧑‍🏫</span>
            <span>教师信息</span>
          </router-link>
        </div>
      </div>
    </nav>

    <div class="guidance-content">
      <div class="guidance-header">
        <h1>📝 指导记录管理</h1>
        <button class="btn-primary" @click="openAddDialog" :disabled="loading">
          <span class="btn-icon">+</span>
          <span>添加指导记录</span>
        </button>
      </div>

    <transition name="fade">
      <div v-if="message" class="message-banner" :class="message.type">
        {{ message.text }}
      </div>
    </transition>

    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else-if="guidanceRecords.length === 0" class="empty-state">
      <div class="empty-icon">📝</div>
      <p>暂无指导记录，点击上方按钮添加第一条记录</p>
    </div>

    <div v-else class="records-list">
      <div v-for="record in guidanceRecords" :key="record.id" class="record-card">
        <div class="record-header">
          <div class="record-student">
            <strong>{{ record.studentName || `学生ID: ${record.studentId}` }}</strong>
            <span class="record-time">{{ formatDateTime(record.createdAt) }}</span>
          </div>
          <button class="btn-delete" @click="deleteGuidance(record.id!)">删除</button>
        </div>
        <div class="record-content">
          {{ record.note }}
        </div>
      </div>
      </div>
    </div>

    <!-- 添加指导记录对话框 -->
    <transition name="modal">
      <div v-if="showAddDialog" class="modal-overlay" @click.self="closeAddDialog">
        <div class="modal-panel">
          <header>
            <h2>添加指导记录</h2>
          </header>
          <div class="form-group">
            <label>选择学生</label>
            <select v-model="formData.studentId" required>
              <option :value="null">请选择学生</option>
              <option v-for="student in guidedStudents" :key="student.id" :value="student.id">
                {{ student.name }} - {{ student.major }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>指导内容</label>
            <textarea
              v-model="formData.note"
              placeholder="记录本次指导的要点、建议和后续跟进计划..."
              rows="6"
              required
            ></textarea>
          </div>
          <footer>
            <button class="btn-ghost" @click="closeAddDialog">取消</button>
            <button class="btn-primary" @click="submitGuidance" :disabled="submitting">
              {{ submitting ? '提交中...' : '确认添加' }}
            </button>
          </footer>
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
/* 顶部导航栏 */
.teacher-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
}

.nav-logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-weight: 600;
  font-size: 1.1rem;
  color: #1e293b;
}

.logo-icon {
  font-size: 1.5rem;
}

.nav-links {
  display: flex;
  gap: 0.5rem;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1.25rem;
  border-radius: 10px;
  color: #64748b;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.nav-link:hover {
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.08);
}

.nav-link.active {
  color: #3b82f6;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12), rgba(99, 102, 241, 0.12));
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 50%;
  transform: translateX(-50%);
  width: 60%;
  height: 2px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  border-radius: 2px;
}

.link-icon {
  font-size: 1.1rem;
}

.guidance-view {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
  padding-top: 56px;
}

.guidance-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.guidance-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.guidance-header h1 {
  font-size: 2rem;
  color: #1e293b;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.875rem 1.75rem;
  background: linear-gradient(135deg, #2563eb, #7c3aed);
  color: white;
  border: none;
  border-radius: 14px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.35);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-icon {
  font-size: 1.25rem;
  font-weight: 400;
}

.message-banner {
  padding: 1rem 1.5rem;
  border-radius: 12px;
  margin-bottom: 1.5rem;
  font-weight: 600;
}

.message-banner.success {
  background: rgba(34, 197, 94, 0.15);
  color: #047857;
}

.message-banner.error {
  background: rgba(248, 113, 113, 0.18);
  color: #b91c1c;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
}

.spinner {
  width: 3rem;
  height: 3rem;
  border: 4px solid rgba(99, 102, 241, 0.25);
  border-top-color: #4f46e5;
  border-radius: 999px;
  margin: 0 auto 1rem;
  animation: spin 0.8s linear infinite;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.record-card {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
}

.record-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.record-student {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.record-student strong {
  font-size: 1.1rem;
  color: #1e293b;
}

.record-time {
  font-size: 0.9rem;
  color: #64748b;
}

.btn-delete {
  padding: 0.4rem 1rem;
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-delete:hover {
  background: rgba(239, 68, 68, 0.2);
}

.record-content {
  color: #334155;
  line-height: 1.6;
  white-space: pre-wrap;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: grid;
  place-items: center;
  z-index: 50;
}

.modal-panel {
  width: min(580px, 90vw);
  background: white;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.modal-panel header h2 {
  margin: 0 0 1.5rem;
  font-size: 1.5rem;
  color: #1e293b;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #334155;
}

.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #cbd5e1;
  border-radius: 12px;
  font-size: 1rem;
  font-family: inherit;
  transition: border-color 0.2s;
}

.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
}

.modal-panel footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.btn-ghost {
  padding: 0.75rem 1.5rem;
  background: rgba(226, 232, 240, 0.6);
  color: #1f2937;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-ghost:hover {
  background: rgba(226, 232, 240, 0.9);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>

