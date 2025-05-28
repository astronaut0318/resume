<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const goToExpert = () => {
  router.push('/expert')
}

// 模拟服务数据
const services = ref([
  {
    id: 1,
    title: '简历修改',
    description: '由拥有丰富HR经验的简历顾问一对一为您提供修改服务，从专业角度诊断简历弱点，定制修改方案，量身打造一份有竞争力、脱颖而出的简历，专业、高效、热情的服务态度，换取您满意的笑容。',
    price: 99
  },
  {
    id: 2,
    title: '英文简历修改',
    description: '做专业的事！擅长英语的招聘专家为你输出一份原汁原味的英文简历，帮你提高面试通过率200%，在求职的路上，幻主简历助你一臂之力。',
    price: 199
  },
  {
    id: 3,
    title: '面试辅导',
    description: '一对一语音沟通，面试上千的HR老鸟传亲授面试技巧，手把手带你了解行业情况、薪资待遇、面试套路、工作法则，帮你缩小与目标岗位的差距，针对性提出快速改进方案，助你在面试过程中知己知彼、百战百胜！。',
    price: 399
  },
  {
    id: 4,
    title: '职业规划',
    description: '职业规划师为你量身打造职场晋升之路，明确发展方向，你对职场存在的任何困惑，都能得到咨询师的专业解答，快速高效地帮你理清思路，认清自我，调整心态，了解自身优势，明确未来的职业发展方向。',
    price: 699
  }
])
</script>

<template>
  <section class="expert-section">
    <div class="section-container">
      <div class="section-header">
        <h2 class="section-title">简历行家服务</h2>
        <p class="section-subtitle">专业人士一对一指导，提升简历质量与面试成功率</p>
      </div>
      
      <div class="services-grid">
        <div v-for="service in services" :key="service.id" class="service-card">
          <div class="service-header">
            <h3 class="service-title">{{ service.title }}</h3>
          </div>
          <div class="service-content">
            <p class="service-desc">{{ service.description }}</p>
            <div class="service-price">
              <span class="price-prefix">￥</span>
              <span class="price-value">{{ service.price }}</span>
            </div>
            <el-button type="primary" class="service-btn" @click="goToExpert">了解详情</el-button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.expert-section {
  padding: 60px 0;
  background-color: #fff;
}

.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.section-subtitle {
  font-size: 16px;
  color: #666;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.service-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s, box-shadow 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.service-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.service-header {
  background-color: #1890ff;
  padding: 20px;
  text-align: center;
}

.service-title {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  margin: 0;
}

.service-content {
  padding: 20px;
  background-color: #fff;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.service-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 20px;
  flex-grow: 1;
}

.service-price {
  text-align: center;
  margin-bottom: 20px;
}

.price-prefix {
  color: #1890ff;
  font-size: 16px;
  font-weight: bold;
}

.price-value {
  color: #1890ff;
  font-size: 24px;
  font-weight: bold;
}

.service-btn {
  width: 100%;
}

@media (max-width: 992px) {
  .services-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .services-grid {
    grid-template-columns: 1fr;
  }
}
</style> 