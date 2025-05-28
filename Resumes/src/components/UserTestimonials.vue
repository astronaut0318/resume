<script setup>
import { ref } from 'vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

// 模拟用户评价数据
const testimonials = ref([
  {
    id: 1,
    phone: '13517*****',
    content: '有好多种不同简历，看好哪个可以先下载下来，简历里面的风格非常多，挑了一个自己喜欢的风格改一改就可以用了，希望这个星期能找到工作',
    name: '哆啦B梦'
  },
  {
    id: 2,
    phone: '13626*****',
    content: '奈何网速不给力，正在下载中，但里面的风格我看了下次都是能用的，因为最近在投简历模板，所以比较了多家之后选了这家，希望能为我面试的时候加分',
    name: '城市穿梭客'
  },
  {
    id: 3,
    phone: '13822*****',
    content: '简历在线制作工具，内容模板很多，特别实用，什么类型的都有，特别好，必须给个好评',
    name: '大油猫'
  },
  {
    id: 4,
    phone: '13917*****',
    content: '不错的简历工具，操作简单，内容样式丰富，模板随意选择，做的的简历特别漂亮，不会word的小白福音！',
    name: '诺诺的糖'
  },
  {
    id: 5,
    phone: '13867*****',
    content: '简历工具和模板的设计都不错，最近准备换工作，这家的模板很多很新，内容也很全面！赞赞赞',
    name: '刻意练习'
  },
  {
    id: 6,
    phone: '13421*****',
    content: '作为一名HR职场人，面试一位研究生的时候，特别漂亮的简历模板，确实很容易加分，good！赞一个！',
    name: '渐行渐远'
  }
])

// 控制当前显示的评价
const currentTestimonials = ref(testimonials.value.slice(0, 3))
const currentIndex = ref(0)

// 切换评价
const showPrevious = () => {
  currentIndex.value = (currentIndex.value - 1 + Math.ceil(testimonials.value.length / 3)) % Math.ceil(testimonials.value.length / 3)
  updateTestimonials()
}

const showNext = () => {
  currentIndex.value = (currentIndex.value + 1) % Math.ceil(testimonials.value.length / 3)
  updateTestimonials()
}

const updateTestimonials = () => {
  const startIndex = currentIndex.value * 3
  currentTestimonials.value = testimonials.value.slice(startIndex, startIndex + 3)
}
</script>

<template>
  <section class="testimonials-section">
    <div class="section-container">
      <div class="section-header">
        <h2 class="section-title">超多成功入职名企精英用户，高入职通过率选幻主简历</h2>
        <p class="section-subtitle">简历更专业，面试offer提前一步</p>
      </div>
      
      <div class="testimonials-carousel">
        <button class="carousel-nav carousel-prev" @click="showPrevious">
          <el-icon><arrow-left /></el-icon>
        </button>
        
        <div class="testimonials-container">
          <div v-for="testimonial in currentTestimonials" :key="testimonial.id" class="testimonial-card">
            <div class="testimonial-header">
              <div class="user-info">
                <div class="user-avatar">{{ testimonial.name.charAt(0) }}</div>
                <div class="user-details">
                  <div class="user-phone">{{ testimonial.phone }}</div>
                  <div class="user-name">{{ testimonial.name }}</div>
                </div>
              </div>
            </div>
            <div class="testimonial-content">
              <p>{{ testimonial.content }}</p>
            </div>
          </div>
        </div>
        
        <button class="carousel-nav carousel-next" @click="showNext">
          <el-icon><arrow-right /></el-icon>
        </button>
      </div>
      
      <div class="cta-section">
        <h3 class="cta-title">在这里，轻松创建一份出色的简历。</h3>
        <p class="cta-desc">好用的简历工具，不解释，职场优秀人才的共同选择。</p>
        <router-link to="/create">
          <el-button type="primary" size="large">在线制作</el-button>
        </router-link>
      </div>
    </div>
  </section>
</template>

<style scoped>
.testimonials-section {
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

.testimonials-carousel {
  display: flex;
  align-items: center;
  margin-bottom: 60px;
}

.carousel-nav {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #fff;
  border: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
  z-index: 1;
}

.carousel-nav:hover {
  background-color: #1890ff;
  color: #fff;
  border-color: #1890ff;
}

.carousel-prev {
  margin-right: 20px;
}

.carousel-next {
  margin-left: 20px;
}

.testimonials-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  flex-grow: 1;
}

.testimonial-card {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.testimonial-header {
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #1890ff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  margin-right: 10px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-phone {
  font-size: 14px;
  color: #666;
}

.user-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.testimonial-content {
  flex-grow: 1;
}

.testimonial-content p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.cta-section {
  text-align: center;
  padding: 40px;
  background-color: #f0f7ff;
  border-radius: 8px;
}

.cta-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.cta-desc {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

@media (max-width: 992px) {
  .testimonials-container {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .testimonial-card:last-child {
    display: none;
  }
}

@media (max-width: 768px) {
  .testimonials-container {
    grid-template-columns: 1fr;
  }
  
  .testimonial-card:nth-child(2) {
    display: none;
  }
}
</style> 