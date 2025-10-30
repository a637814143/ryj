<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getTeacherByUserId, getTeacherDashboard, approveProfileUpdate, rejectProfileUpdate, type TeacherPendingApproval } from '@/api/teacher'

type ReviewAction = 'APPROVE' | 'REJECT'

const teacherId = ref<number | null>(null)
const loading = ref(true)
const error = ref('')
const approvals = ref<TeacherPendingApproval[]>([])
const keyword = ref('')
const majorFilter = ref('')

const dialog = ref({ visible: false, action: 'APPROVE' as ReviewAction, requestId: null as number | null, comment: '' })
const submitting = ref(false)

const filtered = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  const mj = majorFilter.value.trim().toLowerCase()
  return approvals.value.filter(a => {
    const hitKw = !kw || a.studentName.toLowerCase().includes(kw)
    const hitMj = !mj || (a.major || '').toLowerCase().includes(mj)
    return hitKw && hitMj
  })
})

const loadData = async () => {
  loading.value = true
  error.value = ''
  try {
    const userInfoStr = localStorage.getItem('userInfo')
    if (!userInfoStr) throw new Error('请先登录')
    const userInfo = JSON.parse(userInfoStr)
    if (userInfo.role !== 'TEACHER') throw new Error('当前账号不是教师角色')
    const rec = await getTeacherByUserId(userInfo.id)
    teacherId.value = rec.id
    const dash = await getTeacherDashboard(rec.id)
    approvals.value = dash.pendingApprovals
  } catch (e: any) {
    error.value = e?.message || '加载失败'
  } finally {
    loading.value = false
  }
}

const openDialog = (action: ReviewAction, requestId: number) => {
  dialog.value = { visible: true, action, requestId, comment: '' }
}

const closeDialog = () => {
  dialog.value.visible = false
  dialog.value.comment = ''
}

const submit = async () => {
  if (!teacherId.value || !dialog.value.requestId) return
  submitting.value = true
  try {
    if (dialog.value.action === 'APPROVE') {
      const payload = dialog.value.comment.trim() ? { reviewComment: dialog.value.comment.trim() } : {}
      await approveProfileUpdate(teacherId.value, dialog.value.requestId, payload)
    } else {
      await rejectProfileUpdate(teacherId.value, dialog.value.requestId, { reviewComment: dialog.value.comment.trim() })
    }
    closeDialog()
    await loadData()
  } catch (e) {
    // 简单提示
    alert(e instanceof Error ? e.message : '提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)

const formatDateTime = (value: string | null) => {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const hh = String(date.getHours()).padStart(2, '0')
  const mm = String(date.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${d} ${hh}:${mm}`
}
</script>

<template>
  <div class="approvals-page">
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
          <router-link to="/teacher/approvals" class="nav-link" active-class="active">
            <span class="link-icon">✅</span>
            <span>档案审核</span>
          </router-link>
        </div>
      </div>
    </nav>

    <div class="container">
      <header class="page-header">
        <h1>档案审核</h1>
        <div class="filters">
          <input v-model="keyword" class="input" placeholder="按学生姓名过滤" />
          <input v-model="majorFilter" class="input" placeholder="按专业过滤" />
        </div>
      </header>

      <div v-if="loading" class="loading-state">
        <div class="spinner" />
        加载中...
      </div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <div v-else>
        <div v-if="filtered.length === 0" class="empty-state">暂无待审核申请</div>
        <div v-else class="list">
          <article v-for="req in filtered" :key="req.requestId" class="item">
            <div class="head">
              <div class="title">
                <strong>{{ req.studentName }}</strong>
                <span class="meta">
                  <span>{{ req.major || '专业未填写' }}</span>
                  <span v-if="req.graduationYear">预计 {{ req.graduationYear }} 毕业</span>
                  <span>提交于 {{ formatDateTime(req.submittedAt) }}</span>
                </span>
              </div>
              <div class="actions">
                <button class="btn approve" @click="openDialog('APPROVE', req.requestId)">通过</button>
                <button class="btn reject" @click="openDialog('REJECT', req.requestId)">退回</button>
              </div>
            </div>
            <p class="brief">{{ (req.biography && req.biography.length > 180) ? (req.biography.slice(0,180) + '…') : (req.biography || '—') }}</p>
          </article>
        </div>
      </div>
    </div>

    <div v-if="dialog.visible" class="modal" @click.self="closeDialog">
      <div class="panel">
        <h3>{{ dialog.action === 'APPROVE' ? '通过档案申请' : '退回档案申请' }}</h3>
        <p class="hint">
          {{ dialog.action === 'APPROVE' ? '可填写对学生的指导建议（可选）' : '请填写退回原因（必填）' }}
        </p>
        <textarea v-model="dialog.comment" :placeholder="dialog.action === 'APPROVE' ? '可选建议' : '必填退回原因'" />
        <div class="modal-actions">
          <button class="btn" @click="closeDialog">取消</button>
          <button class="btn primary" :disabled="submitting || (dialog.action==='REJECT' && !dialog.comment.trim())" @click="submit">
            {{ submitting ? '提交中...' : '确认' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.teacher-nav { position: fixed; top:0; left:0; right:0; z-index:100; background: rgba(255,255,255,.85); backdrop-filter: blur(20px) saturate(180%); border-bottom:1px solid rgba(0,0,0,.08); }
.nav-container { max-width: 1400px; margin:0 auto; padding:0 2rem; display:flex; align-items:center; justify-content:space-between; height:56px; }
.nav-logo { display:flex; align-items:center; gap:.75rem; font-weight:600; font-size:1.1rem; color:#1e293b; }
.logo-icon { font-size:1.5rem; }
.nav-links { display:flex; gap:.5rem; }
.nav-link { display:flex; align-items:center; gap:.5rem; padding:.6rem 1.25rem; border-radius:10px; color:#64748b; text-decoration:none; font-weight:500; transition:all .2s cubic-bezier(.4,0,.2,1); position:relative; }
.nav-link:hover { color:#3b82f6; background:rgba(59,130,246,.08); }
.nav-link.active { color:#3b82f6; background:linear-gradient(135deg, rgba(59,130,246,.12), rgba(99,102,241,.12)); }

.approvals-page { min-height:100vh; background: linear-gradient(180deg,#f8fafc 0%, #f1f5f9 100%); padding-top:56px; }
.container { max-width: 1200px; margin:0 auto; padding: 2rem; }
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:1.5rem; }
.page-header h1 { margin:0; font-size:1.6rem; }
.filters { display:flex; gap:.75rem; }
.input { padding:.6rem .9rem; border:1px solid #cbd5e1; border-radius:10px; }

.loading-state { text-align:center; padding:3rem; }
.error { color:#b91c1c; }
.empty-state { text-align:center; padding:2.5rem 1.5rem; background: rgba(255,255,255,.9); border-radius:16px; }
.list { display:flex; flex-direction:column; gap:1rem; }
.item { background:#fff; border-radius:16px; padding:1.25rem; box-shadow:0 12px 24px rgba(15,23,42,.08); }
.head { display:flex; justify-content:space-between; gap:1rem; align-items:flex-start; }
.title { display:flex; flex-direction:column; gap:.35rem; }
.meta { display:flex; flex-wrap:wrap; gap:.75rem; color:#64748b; font-size:.9rem; }
.actions { display:flex; gap:.5rem; }
.btn { border:none; border-radius:12px; padding:.5rem .9rem; font-weight:600; cursor:pointer; }
.btn.approve { background: linear-gradient(135deg,#34d399,#059669); color:#fff; }
.btn.reject { background: linear-gradient(135deg,#f97316,#ef4444); color:#fff; }
.brief { margin:.75rem 0 0; color:#1f2937; }

.modal { position:fixed; inset:0; display:grid; place-items:center; background:rgba(15,23,42,.35); backdrop-filter: blur(6px); z-index:50; }
.panel { width:min(520px,92vw); background:#fff; border-radius:20px; padding:1.5rem; box-shadow:0 30px 80px rgba(15,23,42,.2); display:flex; flex-direction:column; gap:1rem; }
.panel h3 { margin:0; font-size:1.25rem; }
.hint { color:#64748b; margin:0; }
textarea { min-height:120px; border:1px solid #cbd5e1; border-radius:12px; padding:.75rem 1rem; resize:vertical; }
.modal-actions { display:flex; justify-content:flex-end; gap:.75rem; }
.btn.primary { background: linear-gradient(135deg,#2563eb,#7c3aed); color:#fff; }
</style>


