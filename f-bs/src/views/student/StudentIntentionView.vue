<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import {
  saveEmploymentIntention,
  getEmploymentIntention,
  type EmploymentIntentionRequest,
  type EmploymentIntentionResponse,
} from '../../api/employmentIntention'

const loading = ref(false)
const saving = ref(false)
const message = ref('')
const messageType = ref<'success' | 'error'>('success')
const studentId = ref<number | null>(null)

const intentionForm = reactive({
  expectedPosition: '',
  salaryRange: '',
  workType: 'FULL_TIME',
  cities: [] as string[],
  notes: '',
})

const workTypeOptions = [
  { value: 'FULL_TIME', label: '全职' },
  { value: 'PART_TIME', label: '兼职' },
  { value: 'INTERNSHIP', label: '实习' },
  { value: 'FLEXIBLE', label: '灵活就业' },
]

// 省市数据
const cityData: Record<string, string[]> = {
  '北京市': ['东城区', '西城区', '朝阳区', '丰台区', '石景山区', '海淀区', '门头沟区', '房山区', '通州区', '顺义区', '昌平区', '大兴区', '怀柔区', '平谷区', '密云区', '延庆区'],
  '上海市': ['黄浦区', '徐汇区', '长宁区', '静安区', '普陀区', '虹口区', '杨浦区', '闵行区', '宝山区', '嘉定区', '浦东新区', '金山区', '松江区', '青浦区', '奉贤区', '崇明区'],
  '天津市': ['和平区', '河东区', '河西区', '南开区', '河北区', '红桥区', '东丽区', '西青区', '津南区', '北辰区', '武清区', '宝坻区', '滨海新区', '宁河区', '静海区', '蓟州区'],
  '重庆市': ['渝中区', '万州区', '涪陵区', '大渡口区', '江北区', '沙坪坝区', '九龙坡区', '南岸区', '北碚区', '綦江区', '大足区', '渝北区', '巴南区', '黔江区', '长寿区', '江津区', '合川区', '永川区', '南川区', '璧山区', '铜梁区', '潼南区', '荣昌区', '开州区', '梁平区', '武隆区'],
  '广东省': ['广州市', '深圳市', '珠海市', '汕头市', '佛山市', '韶关市', '湛江市', '肇庆市', '江门市', '茂名市', '惠州市', '梅州市', '汕尾市', '河源市', '阳江市', '清远市', '东莞市', '中山市', '潮州市', '揭阳市', '云浮市'],
  '江苏省': ['南京市', '无锡市', '徐州市', '常州市', '苏州市', '南通市', '连云港市', '淮安市', '盐城市', '扬州市', '镇江市', '泰州市', '宿迁市'],
  '浙江省': ['杭州市', '宁波市', '温州市', '嘉兴市', '湖州市', '绍兴市', '金华市', '衢州市', '舟山市', '台州市', '丽水市'],
  '山东省': ['济南市', '青岛市', '淄博市', '枣庄市', '东营市', '烟台市', '潍坊市', '济宁市', '泰安市', '威海市', '日照市', '临沂市', '德州市', '聊城市', '滨州市', '菏泽市'],
  '河南省': ['郑州市', '开封市', '洛阳市', '平顶山市', '安阳市', '鹤壁市', '新乡市', '焦作市', '濮阳市', '许昌市', '漯河市', '三门峡市', '南阳市', '商丘市', '信阳市', '周口市', '驻马店市'],
  '河北省': ['石家庄市', '唐山市', '秦皇岛市', '邯郸市', '邢台市', '保定市', '张家口市', '承德市', '沧州市', '廊坊市', '衡水市'],
  '湖北省': ['武汉市', '黄石市', '十堰市', '宜昌市', '襄阳市', '鄂州市', '荆门市', '孝感市', '荆州市', '黄冈市', '咸宁市', '随州市'],
  '湖南省': ['长沙市', '株洲市', '湘潭市', '衡阳市', '邵阳市', '岳阳市', '常德市', '张家界市', '益阳市', '郴州市', '永州市', '怀化市', '娄底市'],
  '四川省': ['成都市', '自贡市', '攀枝花市', '泸州市', '德阳市', '绵阳市', '广元市', '遂宁市', '内江市', '乐山市', '南充市', '眉山市', '宜宾市', '广安市', '达州市', '雅安市', '巴中市', '资阳市'],
  '陕西省': ['西安市', '铜川市', '宝鸡市', '咸阳市', '渭南市', '延安市', '汉中市', '榆林市', '安康市', '商洛市'],
  '辽宁省': ['沈阳市', '大连市', '鞍山市', '抚顺市', '本溪市', '丹东市', '锦州市', '营口市', '阜新市', '辽阳市', '盘锦市', '铁岭市', '朝阳市', '葫芦岛市'],
  '吉林省': ['长春市', '吉林市', '四平市', '辽源市', '通化市', '白山市', '松原市', '白城市'],
  '黑龙江省': ['哈尔滨市', '齐齐哈尔市', '鸡西市', '鹤岗市', '双鸭山市', '大庆市', '伊春市', '佳木斯市', '七台河市', '牡丹江市', '黑河市', '绥化市'],
  '安徽省': ['合肥市', '芜湖市', '蚌埠市', '淮南市', '马鞍山市', '淮北市', '铜陵市', '安庆市', '黄山市', '滁州市', '阜阳市', '宿州市', '六安市', '亳州市', '池州市', '宣城市'],
  '福建省': ['福州市', '厦门市', '莆田市', '三明市', '泉州市', '漳州市', '南平市', '龙岩市', '宁德市'],
  '江西省': ['南昌市', '景德镇市', '萍乡市', '九江市', '新余市', '鹰潭市', '赣州市', '吉安市', '宜春市', '抚州市', '上饶市'],
  '山西省': ['太原市', '大同市', '阳泉市', '长治市', '晋城市', '朔州市', '晋中市', '运城市', '忻州市', '临汾市', '吕梁市'],
  '云南省': ['昆明市', '曲靖市', '玉溪市', '保山市', '昭通市', '丽江市', '普洱市', '临沧市'],
  '贵州省': ['贵阳市', '六盘水市', '遵义市', '安顺市', '毕节市', '铜仁市'],
  '广西壮族自治区': ['南宁市', '柳州市', '桂林市', '梧州市', '北海市', '防城港市', '钦州市', '贵港市', '玉林市', '百色市', '贺州市', '河池市', '来宾市', '崇左市'],
  '海南省': ['海口市', '三亚市', '三沙市', '儋州市'],
  '甘肃省': ['兰州市', '嘉峪关市', '金昌市', '白银市', '天水市', '武威市', '张掖市', '平凉市', '酒泉市', '庆阳市', '定西市', '陇南市'],
  '青海省': ['西宁市', '海东市'],
  '内蒙古自治区': ['呼和浩特市', '包头市', '乌海市', '赤峰市', '通辽市', '鄂尔多斯市', '呼伦贝尔市', '巴彦淖尔市', '乌兰察布市'],
  '宁夏回族自治区': ['银川市', '石嘴山市', '吴忠市', '固原市', '中卫市'],
  '新疆维吾尔自治区': ['乌鲁木齐市', '克拉玛依市', '吐鲁番市', '哈密市'],
  '西藏自治区': ['拉萨市', '日喀则市', '昌都市', '林芝市', '山南市', '那曲市'],
}

const selectedProvince = ref('')
const selectedCity = ref('')

const provinces = computed(() => Object.keys(cityData))

const cities = computed(() => {
  if (!selectedProvince.value) return []
  return cityData[selectedProvince.value] || []
})

onMounted(async () => {
  // 从 localStorage 获取用户信息
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    try {
      const userInfo = JSON.parse(userInfoStr)
      studentId.value = userInfo.id
      
      // 加载就业意向数据
      if (studentId.value) {
        loading.value = true
        try {
          const response = await getEmploymentIntention(studentId.value)
          if (response.code === 200 && response.data) {
            const data = response.data
            intentionForm.expectedPosition = data.expectedPosition || ''
            intentionForm.salaryRange = data.salaryRange || ''
            intentionForm.workType = data.workType as any || 'FULL_TIME'
            intentionForm.cities = data.cities || []
            intentionForm.notes = data.notes || ''
          }
        } catch (error) {
          console.error('加载就业意向失败:', error)
        } finally {
          loading.value = false
        }
      }
    } catch (e) {
      console.error('解析用户信息失败:', e)
      loading.value = false
    }
  } else {
    loading.value = false
  }
})

const addCity = () => {
  if (!selectedProvince.value) {
    message.value = '请先选择省份'
    messageType.value = 'error'
    return
  }

  if (!selectedCity.value) {
    message.value = '请选择城市'
    messageType.value = 'error'
    return
  }

  const cityName = `${selectedProvince.value}-${selectedCity.value}`
  if (!intentionForm.cities.includes(cityName)) {
    intentionForm.cities.push(cityName)
    selectedProvince.value = ''
    selectedCity.value = ''
    message.value = ''
  } else {
    message.value = '该城市已添加'
    messageType.value = 'error'
  }
}

const removeCity = (index: number) => {
  intentionForm.cities.splice(index, 1)
}

const handleProvinceChange = () => {
  selectedCity.value = ''
}

const saveIntention = async () => {
  if (!intentionForm.expectedPosition) {
    message.value = '请填写期望职位'
    messageType.value = 'error'
    return
  }

  if (!studentId.value) {
    message.value = '无法获取学生信息，请重新登录'
    messageType.value = 'error'
    return
  }

  saving.value = true
  message.value = ''

  try {
    const requestData: EmploymentIntentionRequest = {
      expectedPosition: intentionForm.expectedPosition,
      salaryRange: intentionForm.salaryRange || undefined,
      workType: intentionForm.workType as any,
      cities: intentionForm.cities.length > 0 ? intentionForm.cities : undefined,
      notes: intentionForm.notes || undefined,
    }

    const response = await saveEmploymentIntention(studentId.value, requestData)
    
    if (response.code === 200) {
      message.value = '保存成功'
      messageType.value = 'success'
      
      // 3秒后清除提示
      setTimeout(() => {
        message.value = ''
      }, 3000)
    } else {
      message.value = response.message || '保存失败'
      messageType.value = 'error'
    }
  } catch (error) {
    console.error('保存就业意向失败:', error)
    message.value = '保存失败，请稍后重试'
    messageType.value = 'error'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="student-intention">
    <div class="page-header">
      <h1>就业意向</h1>
      <p class="subtitle">设置您的就业期望，帮助企业更好地了解您</p>
    </div>

    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      
      <form v-else @submit.prevent="saveIntention" class="intention-form">
        <div class="form-group">
          <label class="required">期望职位</label>
          <input
            v-model="intentionForm.expectedPosition"
            type="text"
            placeholder="如：Java开发工程师、前端开发、产品经理等"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label>期望薪资</label>
          <input
            v-model="intentionForm.salaryRange"
            type="text"
            placeholder="如：8k-12k、面议等"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label class="required">工作类型</label>
          <div class="radio-group">
            <label
              v-for="option in workTypeOptions"
              :key="option.value"
              class="radio-label"
            >
              <input
                v-model="intentionForm.workType"
                type="radio"
                :value="option.value"
              />
              <span>{{ option.label }}</span>
            </label>
          </div>
        </div>

        <div class="form-group">
          <label>意向城市</label>
          <div class="city-selector-group">
            <select
              v-model="selectedProvince"
              @change="handleProvinceChange"
              class="form-select"
            >
              <option value="">请选择省份</option>
              <option v-for="province in provinces" :key="province" :value="province">
                {{ province }}
              </option>
            </select>

            <select
              v-model="selectedCity"
              class="form-select"
              :disabled="!selectedProvince"
            >
              <option value="">请选择城市/地区</option>
              <option v-for="city in cities" :key="city" :value="city">
                {{ city }}
              </option>
            </select>

            <button type="button" class="btn-add" @click="addCity">
              ➕ 添加
            </button>
          </div>
          <div v-if="intentionForm.cities.length > 0" class="city-tags">
            <span
              v-for="(city, index) in intentionForm.cities"
              :key="index"
              class="city-tag"
            >
              {{ city }}
              <button type="button" @click="removeCity(index)" class="remove-btn">×</button>
            </span>
          </div>
        </div>

        <div class="form-group">
          <label>其他备注</label>
          <textarea
            v-model="intentionForm.notes"
            rows="4"
            placeholder="您可以在这里补充其他就业相关的期望或说明"
            class="form-textarea"
          ></textarea>
        </div>

        <div v-if="message" :class="['message', messageType]">
          {{ message }}
        </div>

        <button type="submit" class="btn-submit" :disabled="saving">
          {{ saving ? '保存中...' : '保存就业意向' }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.student-intention {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: #64748b;
  font-size: 1rem;
}

.content {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #64748b;
  font-size: 1.1rem;
}

.intention-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 600;
  color: #334155;
  font-size: 0.95rem;
}

.form-group label.required::after {
  content: ' *';
  color: #dc2626;
}

.form-input {
  padding: 0.75rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  background: #f8fafc;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.form-textarea {
  padding: 0.75rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.95rem;
  font-family: inherit;
  resize: vertical;
  transition: all 0.3s ease;
  background: #f8fafc;
}

.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  padding: 0.5rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.radio-label:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.radio-label input[type="radio"] {
  cursor: pointer;
}

.radio-label input[type="radio"]:checked + span {
  color: #667eea;
  font-weight: 600;
}

.city-selector-group {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 0.75rem;
  align-items: start;
}

.form-select {
  padding: 0.75rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.95rem;
  background: #f8fafc;
  color: #1e293b;
  cursor: pointer;
  transition: all 0.3s ease;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23475569' d='M6 9L1 4h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 1rem center;
  padding-right: 2.5rem;
}

.form-select:focus {
  outline: none;
  border-color: #667eea;
  background-color: #ffffff;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.form-select:disabled {
  background-color: #f1f5f9;
  color: #94a3b8;
  cursor: not-allowed;
  opacity: 0.6;
}

.btn-add {
  padding: 0.75rem 1.25rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  box-shadow: 0 4px 10px rgba(102, 126, 234, 0.3);
}

.btn-add:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 15px rgba(102, 126, 234, 0.4);
}

.btn-add:active {
  transform: translateY(0);
}

.city-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.city-tag {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.75rem;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(139, 92, 246, 0.1));
  color: #667eea;
  border-radius: 8px;
  font-weight: 500;
  font-size: 0.9rem;
}

.remove-btn {
  background: none;
  border: none;
  color: #667eea;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.remove-btn:hover {
  background: rgba(102, 126, 234, 0.2);
}

.message {
  padding: 0.75rem 1rem;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.875rem;
}

.message.success {
  background: #d1fae5;
  color: #065f46;
  border: 1px solid #6ee7b7;
}

.message.error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fca5a5;
}

.btn-submit {
  padding: 0.875rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .student-intention {
    padding: 1rem;
  }

  .page-header h1 {
    font-size: 1.5rem;
  }

  .content {
    padding: 1.5rem;
  }

  .city-selector-group {
    grid-template-columns: 1fr;
  }

  .radio-group {
    flex-direction: column;
  }
}
</style>

