<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useNotificationStore } from '../stores/notification'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Trophy, Star, Money, Timer } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const notificationStore = useNotificationStore()
const activeTabName = ref('monthly')
const showPaymentDialog = ref(false)
const selectedPlan = ref(null)
const paymentMethod = ref('wechat')

// 计算用户是否已登录
const isLoggedIn = computed(() => userStore.isLoggedIn)
const isVip = computed(() => userStore.isVip)
const userInfo = computed(() => userStore.userInfo)

// 会员套餐数据
const vipPlans = ref([
  {
    id: 'monthly',
    name: '月度会员',
    price: 19.9,
    originalPrice: 29.9,
    period: '30天',
    features: [
      '简历创建份数20份',
      '在线简历修改下载',
      '海量简历案例',
      'AI写简历20次',
      '智能文案辅助',
      '海量简历模板',
    ]
  },
  {
    id: 'lifetime',
    name: '终身会员',
    price: 99,
    originalPrice: 199,
    period: '永久',
    features: [
      '简历创建份数不限',
      '在线简历修改下载',
      '海量简历案例',
      '智能文案辅助',
      '海量简历模板',
      'AI写简历不限次数'
    ]
  }
])

// 处理会员购买
const handleBuyVip = (plan) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再操作')
    router.push('/login')
    return
  }
  
  // 直接显示支付对话框
  selectedPlan.value = plan
  showPaymentDialog.value = true
}

// 处理支付
const handlePayment = () => {
  if (!selectedPlan.value) return
  
  ElMessage.success(`正在处理${selectedPlan.value.name}的支付，支付方式：${paymentMethod.value === 'wechat' ? '微信支付' : '支付宝'}`)
  
  // 模拟支付过程，实际项目中这里应该调用支付API
  setTimeout(async () => {
    try {
      // 1. 判断是否是终身会员
    const isLifetime = selectedPlan.value.id === 'lifetime'
    
      // 2. 更新用户VIP状态（Mock数据更新）
      // 创建VIP状态数据
      const mockVipData = {
        isVip: true,
        level: isLifetime ? 2 : 1,
        startTime: new Date().toISOString().replace('T', ' ').split('.')[0],
        endTime: isLifetime ? 
          new Date(2099, 11, 31).toISOString().replace('T', ' ').split('.')[0] : 
          new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().replace('T', ' ').split('.')[0],
        remainingDays: isLifetime ? 9999 : 30
      }
      
      // 使用直接更新VIP状态的方法
      userStore.updateVipStatus(mockVipData)
      
      // 模拟更新用户角色
      await userStore.updateInfo({
        role: isLifetime ? 2 : 1
      })
      
      // 3. 立即重新加载用户信息，确保状态更新
      await userStore.loadUserInfo()
      
      // 4. 创建一条系统通知
      const notification = {
        id: Date.now(),
        title: `${selectedPlan.value.name}开通成功`,
        content: isLifetime ? 
          `恭喜您成功开通终身会员，可享受所有特权！\n开通时间：${new Date().toLocaleString()}\n有效期：永久` : 
          `恭喜您成功开通月度会员，可享受所有特权！\n开通时间：${new Date().toLocaleString()}\n有效期：30天`,
        type: 1, // 系统通知
        isRead: 0, // 未读
        createTime: new Date().toISOString().replace('T', ' ').split('.')[0]
      }
      
      // 添加通知到通知中心
      notificationStore.createNotification(notification)
      
      // 5. 显示成功消息
      showPaymentDialog.value = false
      
      ElMessage({
        message: isLifetime ? 
          '恭喜您成为终身会员，可享受所有特权！' : 
          '月度会员开通成功，有效期30天！',
        type: 'success',
        duration: 5000
      })
      
      // 6. 提示用户刷新页面以确保会员标识显示正常
      setTimeout(() => {
        ElMessageBox.confirm(
          '会员已成功开通，建议刷新页面以确保会员权益立即生效。是否立即刷新页面？',
          '提示',
          {
            confirmButtonText: '刷新页面',
            cancelButtonText: '稍后刷新',
            type: 'info'
          }
        ).then(() => {
          // 用户点击刷新，刷新整个页面
          window.location.reload()
        }).catch(() => {
          // 用户取消，再次尝试更新用户信息
          userStore.loadUserInfo()
        })
      }, 1000)
      
    } catch (error) {
      console.error('会员开通失败:', error)
      ElMessage.error('会员开通失败，请稍后重试')
    }
  }, 1500)
}
</script>

<template>
  <div class="vip-detail-container">
    <div class="vip-header">
      <h1>会员特权</h1>
      <p>助力您轻松斩获心仪offer!</p>
    </div>
    
    <div class="vip-statistics">
      <div class="stat-item">
        <div class="stat-number">10,000<span>+</span></div>
        <div class="stat-desc">优质简历模板</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">200,000<span>+</span></div>
        <div class="stat-desc">用户好评</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">98<span>%</span></div>
        <div class="stat-desc">简历通过率</div>
      </div>
    </div>
    
    <div class="vip-plans-container">
      <div class="plan-tabs">
        <div 
          class="plan-tab" 
          :class="{ active: activeTabName === 'monthly' }"
          @click="activeTabName = 'monthly'"
        >
          月度会员
        </div>
        <div 
          class="plan-tab" 
          :class="{ active: activeTabName === 'lifetime' }"
          @click="activeTabName = 'lifetime'"
        >
          终身会员
        </div>
      </div>
      
      <div class="plans-wrapper">
        <div v-for="plan in vipPlans" :key="plan.id" class="plan-card" :class="{ active: activeTabName === plan.id }">
          <div class="plan-header">
            <h2 class="plan-name">
              <el-icon><component :is="plan.id === 'monthly' ? Timer : Trophy" /></el-icon>
              {{ plan.name }}
            </h2>
            <div class="plan-price">
              <div class="current-price">¥{{ plan.price }}</div>
              <div class="original-price">原价：¥{{ plan.originalPrice }}</div>
            </div>
            <div class="plan-period">有效期：{{ plan.period }}</div>
          </div>
          
          <div class="plan-features">
            <h3>会员功能</h3>
            <ul>
              <li v-for="(feature, index) in plan.features" :key="index">
                <el-icon><Check /></el-icon>
                <span>{{ feature }}</span>
              </li>
            </ul>
          </div>
          
          <div class="plan-action">
            <el-button 
              type="primary" 
              size="large" 
              @click="handleBuyVip(plan)"
              :disabled="isVip && ((userInfo?.role === 1 && plan.id === 'monthly') || userInfo?.role === 2)"
            >
              <template v-if="isVip">
                <span v-if="userInfo?.role === 2">已是终身会员</span>
                <span v-else-if="userInfo?.role === 1 && plan.id === 'monthly'">续费会员</span>
                <span v-else-if="userInfo?.role === 1 && plan.id === 'lifetime'">升级终身会员</span>
              </template>
              <span v-else>立即开通</span>
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="vip-faq">
      <h2>常见问题</h2>
      <el-collapse>
        <el-collapse-item title="会员时间如何计算？" name="1">
          <div>
            会员有效期自付费成功之日起的自然时间段来计算，如购买月度会员，即付费日期后的30天内均可享受会员服务。
          </div>
        </el-collapse-item>
        <el-collapse-item title="会员到期后有什么影响？" name="2">
          <div>
            会员到期前3天会通过系统提醒（需保持登录状态），若不续费或升级，会员到期后，将恢复普通用户身份，相关权益也将随之变更，已创建的简历数据不会受到影响。
          </div>
        </el-collapse-item>
        <el-collapse-item title="如何升级为终身会员？" name="3">
          <div>
            若您已购买月度会员，可以在会员期内随时选择升级为终身会员，系统将为您提供优惠的升级价格。
          </div>
        </el-collapse-item>
        <el-collapse-item title="是否支持退款？" name="4">
          <div>
            非特殊情况下，会员服务开通后不支持退款。如有特殊情况，请联系客服处理。
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    
    
    <!-- 支付对话框 -->
    <el-dialog
      v-model="showPaymentDialog"
      title="选择支付方式"
      width="500px"
      destroy-on-close
    >
      <div v-if="selectedPlan" class="payment-info">
        <div class="payment-plan-info">
          <h3>{{ selectedPlan.name }}</h3>
          <div class="payment-price">¥{{ selectedPlan.price }}</div>
        </div>
        
        <div class="payment-methods">
          <div class="payment-title">支付方式</div>
          <div class="payment-options">
            <div 
              class="payment-option" 
              :class="{ active: paymentMethod === 'wechat' }"
              @click="paymentMethod = 'wechat'"
            >
              <img src="https://img.icons8.com/color/48/000000/wechat--v1.png" alt="微信支付" class="payment-icon" />
              <span>微信支付</span>
            </div>
            <div 
              class="payment-option" 
              :class="{ active: paymentMethod === 'alipay' }"
              @click="paymentMethod = 'alipay'"
            >
              <img src="https://img.icons8.com/color/48/000000/alipay.png" alt="支付宝" class="payment-icon" />
              <span>支付宝</span>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPaymentDialog = false">取消</el-button>
          <el-button type="primary" @click="handlePayment">确认支付</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.vip-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.vip-header {
  text-align: center;
  margin-bottom: 40px;
}

.vip-header h1 {
  font-size: 36px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.vip-header p {
  font-size: 18px;
  color: #666;
}

.vip-statistics {
  display: flex;
  justify-content: space-around;
  margin-bottom: 60px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  color: #1890ff;
  margin-bottom: 8px;
}

.stat-number span {
  font-size: 24px;
}

.stat-desc {
  font-size: 16px;
  color: #666;
}

.vip-plans-container {
  margin-bottom: 60px;
}

.plan-tabs {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.plan-tab {
  padding: 12px 30px;
  margin: 0 10px;
  font-size: 18px;
  font-weight: 500;
  color: #666;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s;
}

.plan-tab:hover {
  color: #1890ff;
}

.plan-tab.active {
  color: #1890ff;
  border-bottom-color: #1890ff;
}

.plans-wrapper {
  display: flex;
  justify-content: center;
  gap: 30px;
}

.plan-card {
  flex: 1;
  max-width: 400px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 30px;
  transition: all 0.3s;
  display: none;
}

.plan-card.active {
  display: block;
}

.plan-header {
  text-align: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.plan-name {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.plan-price {
  margin-bottom: 8px;
}

.current-price {
  font-size: 36px;
  font-weight: 700;
  color: #f56c6c;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
  margin-top: 4px;
}

.plan-period {
  font-size: 14px;
  color: #666;
}

.plan-features h3 {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 16px;
  color: #333;
  text-align: center;
}

.plan-features ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.plan-features li {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  color: #666;
}

.plan-features li .el-icon {
  color: #52c41a;
  margin-right: 8px;
  flex-shrink: 0;
}

.plan-action {
  margin-top: 30px;
  text-align: center;
}

.plan-action .el-button {
  width: 80%;
}

.vip-faq {
  margin-bottom: 60px;
}

.vip-faq h2 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #333;
  text-align: center;
}

.customer-service {
  text-align: center;
  padding: 24px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.customer-service h3 {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #333;
}

.customer-service p {
  font-size: 14px;
  color: #666;
}

/* 支付对话框样式 */
.payment-info {
  padding: 20px 0;
}

.payment-plan-info {
  text-align: center;
  margin-bottom: 30px;
}

.payment-plan-info h3 {
  font-size: 20px;
  font-weight: 500;
  margin-bottom: 10px;
  color: #333;
}

.payment-price {
  font-size: 28px;
  font-weight: 700;
  color: #f56c6c;
}

.payment-methods {
  margin-top: 24px;
}

.payment-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 16px;
  color: #333;
}

.payment-options {
  display: flex;
  gap: 20px;
}

.payment-option {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-option:hover {
  border-color: #1890ff;
}

.payment-option.active {
  border-color: #1890ff;
  background-color: #ecf5ff;
}

.payment-icon {
  width: 40px;
  height: 40px;
  margin-bottom: 8px;
}

@media (max-width: 768px) {
  .vip-statistics {
    flex-direction: column;
    gap: 24px;
  }
  
  .plans-wrapper {
    flex-direction: column;
    align-items: center;
  }
  
  .plan-card {
    max-width: 100%;
    width: 100%;
  }
  
  .plan-tab {
    padding: 12px 20px;
    font-size: 16px;
  }
  
  .payment-options {
    flex-direction: column;
  }
}
</style> 