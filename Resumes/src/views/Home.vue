<script setup>
import HomeBanner from '../components/HomeBanner.vue'
import OnlineTemplates from '../components/OnlineTemplates.vue'
import ExpertServices from '../components/ExpertServices.vue'
import UserTestimonials from '../components/UserTestimonials.vue'
</script>

<template>
  <div class="page-container">
    <main class="main-content">
      <HomeBanner />
      <OnlineTemplates />
      <ExpertServices />
      <UserTestimonials />
    </main>
  </div>
</template>

<style scoped>
.page-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  overflow-x: hidden;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 确保每个section都能完整显示 */
:deep(.templates-section),
:deep(.expert-section),
:deep(.testimonials-section) {
  width: 100%;
  padding: 60px 0;
  background-color: #fff;
}

:deep(.section-container) {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  width: 100%;
}

@media (max-width: 768px) {
  :deep(.section-container) {
    padding: 0 15px;
  }
  
  :deep(.templates-section),
  :deep(.expert-section),
  :deep(.testimonials-section) {
    padding: 40px 0;
  }
}
</style>
