<template>
  <div class="orders-container">
    <h2 class="page-title">我的订单</h2>
    
    <!-- 订单筛选 -->
    <div class="filter-container">
      <el-radio-group v-model="activeStatus" @change="handleStatusChange">
        <el-radio-button :label="null">全部</el-radio-button>
        <el-radio-button :label="0">待支付</el-radio-button>
        <el-radio-button :label="1">已支付</el-radio-button>
        <el-radio-button :label="2">已取消</el-radio-button>
      </el-radio-group>
    </div>
    
    <!-- 订单列表 -->
    <div class="order-list">
      <el-empty v-if="!orderStore.hasOrders && !orderStore.loading" description="暂无订单"></el-empty>
      <el-skeleton v-else-if="orderStore.loading" :rows="3" animated />
      <el-card v-else v-for="order in orderStore.orderList" :key="order.orderNo" class="order-item">
        <div class="order-header">
          <div class="order-id">订单号: {{ order.orderNo }}</div>
          <div class="order-status">
            <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
          </div>
        </div>
        
        <div class="order-content">
          <div class="order-info">
            <div class="template-name">{{ order.templateName }}</div>
            <div class="order-time">下单时间: {{ order.createTime }}</div>
            <div v-if="order.payTime" class="pay-time">支付时间: {{ order.payTime }}</div>
          </div>
          <div class="order-price">¥{{ order.amount }}</div>
        </div>
        
        <div class="order-footer">
          <el-button 
            v-if="order.status === 0" 
            type="primary" 
            @click="handlePay(order)"
            :disabled="orderStore.loading">
            立即支付
          </el-button>
          <el-button 
            v-if="order.status === 0 && !isVipOrder(order)" 
            @click="handleCancel(order)"
            :disabled="orderStore.loading">
            取消订单
          </el-button>
          <el-button 
            v-if="order.status === 1 && !isVipOrder(order)" 
            type="success" 
            @click="handleUseTemplate(order)">
            使用模板
          </el-button>
        </div>
      </el-card>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-container" v-if="orderStore.total > 0">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="orderStore.total"
        :current-page="orderStore.currentPage"
        :page-size="orderStore.pageSize"
        @current-change="handlePageChange"
      />
    </div>
    
    <!-- 支付对话框 -->
    <el-dialog v-model="payDialogVisible" title="订单支付" width="400px">
      <div class="pay-dialog-content">
        <p class="pay-amount">支付金额: <span>¥{{ currentPayOrder?.amount }}</span></p>
        <div class="pay-qrcode">
          <div class="mock-qrcode">
            <svg width="200" height="200" viewBox="0 0 200 200">
              <rect x="0" y="0" width="200" height="200" fill="#f0f0f0" />
              <rect x="20" y="20" width="160" height="160" fill="#fff" />
              <rect x="40" y="40" width="120" height="120" fill="#333" />
              <rect x="60" y="60" width="80" height="80" fill="#fff" />
              <rect x="80" y="80" width="40" height="40" fill="#333" />
            </svg>
          </div>
        </div>
        <p class="pay-tip">请使用微信或支付宝扫码支付</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="payDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="simulatePayment">模拟支付成功</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useOrderStore } from '../../stores/order'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { payOrder } from '../../api/order'

const router = useRouter()
const orderStore = useOrderStore()

// 状态管理
const activeStatus = ref(null)
const payDialogVisible = ref(false)
const currentPayOrder = ref(null)
const loading = ref(false)

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付',
    2: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'info'
  }
  return typeMap[status] || ''
}

// 判断是否为会员订单
const isVipOrder = (order) => {
  return order.templateName.includes('会员')
}

// 处理状态变更
const handleStatusChange = (status) => {
  orderStore.setStatus(status)
}

// 处理分页变更
const handlePageChange = (page) => {
  orderStore.setPage(page)
}

// 处理支付
const handlePay = (order) => {
  currentPayOrder.value = order
  payDialogVisible.value = true
}

// 模拟支付成功
const simulatePayment = async () => {
  try {
    if (!currentPayOrder.value || !currentPayOrder.value.orderNo) {
      ElMessage.error('订单数据不完整，请重试')
      return
    }

    loading.value = true
    
    // 显示加载状态
    ElMessage.info('正在处理支付，请稍候...')
    
    const orderNo = currentPayOrder.value.orderNo
    
    // 调用直接支付API
    const res = await payOrder(orderNo)
    
    if (res.code === 200) {
      // 关闭支付对话框
      payDialogVisible.value = false
      ElMessage.success('支付成功')
      
      // 刷新订单列表
      await orderStore.fetchOrderList()
    } else {
      throw new Error(res.message || '支付失败')
    }
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付处理失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理取消订单
const handleCancel = (order) => {
  ElMessageBox.confirm(
    '确定要取消该订单吗？',
    '取消订单',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    const success = await orderStore.cancelCurrentOrder(order.orderNo)
    if (success) {
      ElMessage.success('订单已取消')
    }
  }).catch(() => {})
}

// 使用模板
const handleUseTemplate = (order) => {
  router.push(`/create?templateId=${order.templateId}`)
}

onMounted(() => {
  // 获取订单列表
  orderStore.fetchOrderList()
})
</script>

<style scoped>
.orders-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
}

.filter-container {
  margin-bottom: 20px;
}

.order-list {
  margin-bottom: 20px;
}

.order-item {
  margin-bottom: 15px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.template-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.order-time, .pay-time {
  color: #999;
  font-size: 14px;
}

.order-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.pay-dialog-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.pay-amount {
  font-size: 16px;
  margin-bottom: 15px;
}

.pay-amount span {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.pay-qrcode {
  margin-bottom: 15px;
}

.pay-tip {
  color: #999;
  font-size: 14px;
}

.mock-qrcode {
  width: 200px;
  height: 200px;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style> 