<template>
  <div class="settings">
    <el-page-header @back="router.back()" title="返回">
      <template #content>
        <span class="page-title">⚙️ 个人设置</span>
      </template>
    </el-page-header>

    <el-tabs v-model="activeTab" class="settings-tabs">
      <!-- 账号设置 -->
      <el-tab-pane label="账号设置" name="account">
        <el-card shadow="hover">
          <el-form :model="accountForm" label-width="120px" class="settings-form">
            <el-form-item label="头像">
              <el-upload
                class="avatar-uploader"
                action="#"
                :show-file-list="false"
                :on-change="handleAvatarChange"
                :auto-upload="false"
              >
                <img v-if="accountForm.avatar" :src="accountForm.avatar" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>

            <el-form-item label="用户名">
              <el-input v-model="accountForm.username" disabled />
            </el-form-item>

            <el-form-item label="昵称">
              <el-input v-model="accountForm.nickname" placeholder="请输入昵称" />
            </el-form-item>

            <el-form-item label="邮箱">
              <el-input v-model="accountForm.email" placeholder="请输入邮箱">
                <template #append>
                  <el-button :type="accountForm.emailVerified ? 'success' : 'primary'">
                    {{ accountForm.emailVerified ? '已验证' : '验证' }}
                  </el-button>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="手机号">
              <el-input v-model="accountForm.phone" placeholder="请输入手机号">
                <template #append>
                  <el-button :type="accountForm.phoneVerified ? 'success' : 'primary'">
                    {{ accountForm.phoneVerified ? '已验证' : '验证' }}
                  </el-button>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveAccountSettings">保存</el-button>
              <el-button @click="resetAccountForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 安全设置 -->
      <el-tab-pane label="安全设置" name="security">
        <el-card shadow="hover">
          <el-form :model="securityForm" label-width="120px" class="settings-form">
            <el-form-item label="当前密码">
              <el-input
                v-model="securityForm.currentPassword"
                type="password"
                placeholder="请输入当前密码"
                show-password
              />
            </el-form-item>

            <el-form-item label="新密码">
              <el-input
                v-model="securityForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>

            <el-form-item label="确认密码">
              <el-input
                v-model="securityForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="changePassword">修改密码</el-button>
            </el-form-item>
          </el-form>

          <el-divider />

          <div class="security-options">
            <div class="security-item">
              <div class="security-info">
                <h4>登录保护</h4>
                <p>开启后，登录时需要验证手机号或邮箱</p>
              </div>
              <el-switch v-model="securitySettings.loginProtection" />
            </div>

            <div class="security-item">
              <div class="security-info">
                <h4>简历隐私保护</h4>
                <p>开启后，企业需要经过您的同意才能查看完整简历</p>
              </div>
              <el-switch v-model="securitySettings.resumePrivacy" />
            </div>

            <div class="security-item">
              <div class="security-info">
                <h4>在线状态</h4>
                <p>是否向企业展示您的在线状态</p>
              </div>
              <el-switch v-model="securitySettings.onlineStatus" />
            </div>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 通知设置 -->
      <el-tab-pane label="通知设置" name="notification">
        <el-card shadow="hover">
          <div class="notification-settings">
            <div class="notification-item">
              <div class="notification-info">
                <h4>📧 邮件通知</h4>
                <p>接收面试邀请、申请进度等邮件通知</p>
              </div>
              <el-switch v-model="notificationSettings.email" />
            </div>

            <el-divider />

            <div class="notification-item">
              <div class="notification-info">
                <h4>📱 短信通知</h4>
                <p>接收重要通知的短信提醒</p>
              </div>
              <el-switch v-model="notificationSettings.sms" />
            </div>

            <el-divider />

            <div class="notification-item">
              <div class="notification-info">
                <h4>🔔 系统通知</h4>
                <p>接收系统内的消息通知</p>
              </div>
              <el-switch v-model="notificationSettings.system" />
            </div>

            <el-divider />

            <h3>通知类型</h3>
            <el-checkbox-group v-model="notificationTypes">
              <el-checkbox label="interview">面试邀请</el-checkbox>
              <el-checkbox label="application">申请进度</el-checkbox>
              <el-checkbox label="company">企业消息</el-checkbox>
              <el-checkbox label="recommendation">职位推荐</el-checkbox>
              <el-checkbox label="system">系统公告</el-checkbox>
            </el-checkbox-group>

            <el-button type="primary" style="margin-top: 24px;" @click="saveNotificationSettings">
              保存设置
            </el-button>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 隐私设置 -->
      <el-tab-pane label="隐私设置" name="privacy">
        <el-card shadow="hover">
          <div class="privacy-settings">
            <div class="privacy-item">
              <div class="privacy-info">
                <h4>简历公开程度</h4>
                <p>设置简历对企业的可见性</p>
              </div>
              <el-radio-group v-model="privacySettings.resumeVisibility">
                <el-radio label="public">完全公开</el-radio>
                <el-radio label="protected">需要申请</el-radio>
                <el-radio label="private">完全隐藏</el-radio>
              </el-radio-group>
            </div>

            <el-divider />

            <div class="privacy-item">
              <div class="privacy-info">
                <h4>个人信息可见性</h4>
                <p>选择哪些信息对企业可见</p>
              </div>
              <el-checkbox-group v-model="privacySettings.visibleFields">
                <el-checkbox label="phone">手机号</el-checkbox>
                <el-checkbox label="email">邮箱</el-checkbox>
                <el-checkbox label="address">地址</el-checkbox>
                <el-checkbox label="education">教育经历</el-checkbox>
                <el-checkbox label="experience">工作经历</el-checkbox>
              </el-checkbox-group>
            </div>

            <el-divider />

            <div class="privacy-item">
              <div class="privacy-info">
                <h4>屏蔽企业</h4>
                <p>被屏蔽的企业无法查看您的简历</p>
              </div>
              <el-button @click="manageBlockedCompanies">管理屏蔽列表</el-button>
            </div>

            <el-button type="primary" style="margin-top: 24px;" @click="savePrivacySettings">
              保存设置
            </el-button>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 关于 -->
      <el-tab-pane label="关于" name="about">
        <el-card shadow="hover">
          <div class="about-content">
            <div class="about-logo">
              <img src="@/assets/logo.svg" alt="系统Logo" />
            </div>
            <h2>高校就业服务平台</h2>
            <p class="version">版本 v1.0.0</p>
            <el-divider />
            <el-descriptions :column="1" border>
              <el-descriptions-item label="系统名称">高校就业服务平台</el-descriptions-item>
              <el-descriptions-item label="版本号">v1.0.0</el-descriptions-item>
              <el-descriptions-item label="更新时间">2024-12-15</el-descriptions-item>
              <el-descriptions-item label="技术栈">Vue 3 + TypeScript + Element Plus</el-descriptions-item>
            </el-descriptions>
            <el-divider />
            <div class="about-links">
              <el-button type="primary" link>使用帮助</el-button>
              <el-button type="primary" link>隐私政策</el-button>
              <el-button type="primary" link>服务条款</el-button>
              <el-button type="primary" link>联系我们</el-button>
            </div>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';

const router = useRouter();

const activeTab = ref('account');

// 账号设置
const accountForm = ref({
  username: 'student001',
  nickname: '求职者',
  email: 'student@example.com',
  phone: '138****8888',
  avatar: '',
  emailVerified: true,
  phoneVerified: true
});

// 安全设置
const securityForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const securitySettings = ref({
  loginProtection: true,
  resumePrivacy: false,
  onlineStatus: true
});

// 通知设置
const notificationSettings = ref({
  email: true,
  sms: true,
  system: true
});

const notificationTypes = ref(['interview', 'application', 'company', 'recommendation']);

// 隐私设置
const privacySettings = ref({
  resumeVisibility: 'protected',
  visibleFields: ['email', 'education', 'experience']
});

const handleAvatarChange = (file: any) => {
  const reader = new FileReader();
  reader.onload = (e: any) => {
    accountForm.value.avatar = e.target.result;
  };
  reader.readAsDataURL(file.raw);
};

const saveAccountSettings = () => {
  ElMessage.success('账号设置保存成功');
  // TODO: 调用API保存
};

const resetAccountForm = () => {
  ElMessage.info('已重置');
};

const changePassword = () => {
  if (!securityForm.value.currentPassword) {
    ElMessage.warning('请输入当前密码');
    return;
  }
  if (!securityForm.value.newPassword) {
    ElMessage.warning('请输入新密码');
    return;
  }
  if (securityForm.value.newPassword !== securityForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致');
    return;
  }
  ElMessage.success('密码修改成功');
  securityForm.value = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
  // TODO: 调用API修改密码
};

const saveNotificationSettings = () => {
  ElMessage.success('通知设置保存成功');
  // TODO: 调用API保存
};

const savePrivacySettings = () => {
  ElMessage.success('隐私设置保存成功');
  // TODO: 调用API保存
};

const manageBlockedCompanies = () => {
  ElMessage.info('屏蔽企业管理功能开发中...');
};
</script>

<style scoped>
.settings {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.settings-tabs {
  margin-top: 24px;
}

.settings-form {
  max-width: 600px;
}

.avatar-uploader {
  width: 120px;
  height: 120px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-uploader-icon {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: #8c939d;
}

.security-options,
.notification-settings,
.privacy-settings {
  max-width: 800px;
}

.security-item,
.notification-item,
.privacy-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
}

.security-info h4,
.notification-info h4,
.privacy-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.security-info p,
.notification-info p,
.privacy-info p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.about-content {
  text-align: center;
  max-width: 600px;
  margin: 0 auto;
}

.about-logo {
  width: 120px;
  height: 120px;
  margin: 0 auto 24px;
}

.about-logo img {
  width: 100%;
  height: 100%;
}

.about-content h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.version {
  margin: 0 0 24px 0;
  font-size: 14px;
  color: #909399;
}

.about-links {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}
</style>

