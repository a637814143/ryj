<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getTeacherByUserId, getTeacherProfile, updateTeacherProfile, type TeacherProfileInfo } from '@/api/teacher'

const teacherId = ref<number | null>(null)
const loading = ref(true)
const saving = ref(false)
const error = ref('')
const message = ref('')

const form = reactive({
  name: '',
  department: '',
  major: '',
  email: '',
  phone: '',
  focus: '',
  biography: '',
})

const loadProfile = async () => {
  loading.value = true
  error.value = ''
  try {
    const userInfoStr = localStorage.getItem('userInfo')
    if (!userInfoStr) throw new Error('请先登录')
    const userInfo = JSON.parse(userInfoStr)
    if (userInfo.role !== 'TEACHER') throw new Error('当前账号不是教师角色')
    const rec = await getTeacherByUserId(userInfo.id)
    if (!rec || rec.id == null) {
      throw new Error('未找到当前教师档案，请联系管理员为该账号绑定教师信息')
    }
    teacherId.value = rec.id
    const detail: TeacherProfileInfo = await getTeacherProfile(rec.id)
    form.name = detail.name || ''
    form.department = detail.department || ''
    form.major = (detail as any).major || ''
    form.email = detail.email || ''
    form.phone = detail.phone || ''
    form.focus = detail.focus || ''
    form.biography = detail.biography || ''
  } catch (e: any) {
    error.value = e?.message || '加载失败'
  } finally {
    loading.value = false
  }
}

const save = async () => {
  if (!teacherId.value) return
  saving.value = true
  message.value = ''
  error.value = ''
  try {
    await updateTeacherProfile(teacherId.value, {
      name: form.name.trim() || undefined,
      department: form.department.trim() || null,
      major: form.major.trim() || null,
      email: form.email.trim() || null,
      phone: form.phone.trim() || null,
      focus: form.focus.trim() || null,
      biography: form.biography.trim() || null,
    })
    message.value = '保存成功'
  } catch (e: any) {
    error.value = e?.message || '保存失败'
  } finally {
    saving.value = false
  }
}

onMounted(loadProfile)
</script>

<template>
  <div class="teacher-profile">
    <nav class="teacher-nav">
      <div class="nav-container">
        <div class="nav-logo">
          <span class="logo-icon">🎓</span>
          <span class="logo-text">教师工作台</span>
        </div>
        <div class="nav-links">
          <router-link to="/teacher/overview" class="nav-link" active-class="active"><span class="link-icon">📊</span><span>仪表板</span></router-link>
          <router-link to="/teacher/guidance" class="nav-link" active-class="active"><span class="link-icon">📝</span><span>指导记录</span></router-link>
          <router-link to="/teacher/statistics" class="nav-link" active-class="active"><span class="link-icon">📈</span><span>统计分析</span></router-link>
          <router-link to="/teacher/profile" class="nav-link" active-class="active"><span class="link-icon">🧑‍🏫</span><span>教师信息</span></router-link>
        </div>
      </div>
    </nav>

    <div class="container">
      <header class="page-header">
        <h1>教师信息</h1>
        <p>完善您的基本资料、学院与专业信息，便于学生按专业匹配班主任。</p>
      </header>

      <div v-if="loading" class="loading-state"><div class="spinner" />加载中...</div>
      <div v-else class="card">
        <form class="form" @submit.prevent="save">
          <div class="grid">
            <label class="field"><span>姓名</span><input v-model="form.name" class="input" placeholder="如：张晓琳" /></label>
            <label class="field"><span>学院/部门</span><input v-model="form.department" class="input" placeholder="如：计算机学院" /></label>
            <label class="field"><span>专业</span><input v-model="form.major" class="input" placeholder="如：软件工程" /></label>
            <label class="field"><span>邮箱</span><input v-model="form.email" class="input" type="email" placeholder="name@school.edu.cn" /></label>
            <label class="field"><span>电话</span><input v-model="form.phone" class="input" placeholder="如：138****8888" /></label>
            <label class="field"><span>研究方向</span><input v-model="form.focus" class="input" placeholder="如：分布式系统、AI教育" /></label>
          </div>
          <label class="field">
            <span>个人简介</span>
            <textarea v-model="form.biography" class="textarea" rows="5" placeholder="教学理念、科研方向、对学生的建议..." />
          </label>

          <div class="actions">
            <button type="submit" class="btn primary" :disabled="saving">{{ saving ? '保存中...' : '保存信息' }}</button>
          </div>

          <p v-if="message" class="ok">{{ message }}</p>
          <p v-if="error" class="err">{{ error }}</p>
        </form>
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

.teacher-profile { min-height:100vh; background: linear-gradient(180deg,#f8fafc 0%, #f1f5f9 100%); padding-top:56px; }
.container { max-width: 1100px; margin:0 auto; padding:2rem; }
.page-header h1 { margin:0 0 .25rem; }
.card { background:#fff; border-radius:20px; padding:1.75rem; box-shadow:0 20px 50px rgba(15,23,42,.12); }
.form { display:flex; flex-direction:column; gap:1.25rem; }
.grid { display:grid; grid-template-columns: repeat(auto-fit,minmax(240px,1fr)); gap:1rem; }
.field { display:flex; flex-direction:column; gap:.5rem; }
.input { padding:.7rem 1rem; border:1px solid #cbd5e1; border-radius:12px; }
.textarea { padding:.8rem 1rem; border:1px solid #cbd5e1; border-radius:12px; resize:vertical; }
.actions { display:flex; justify-content:flex-end; }
.btn { border:none; border-radius:14px; padding:.7rem 1.4rem; font-weight:700; cursor:pointer; }
.btn.primary { color:#fff; background: linear-gradient(135deg,#2563eb,#7c3aed); box-shadow:0 16px 32px rgba(79,70,229,.25); }
.loading-state { text-align:center; padding:3rem; }
.spinner { width:3rem; height:3rem; border:4px solid rgba(99,102,241,.25); border-top-color:#4f46e5; border-radius:999px; margin:0 auto 1rem; animation: spin .85s linear infinite; }
.ok { color:#059669; }
.err { color:#b91c1c; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>


