<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useTemplateStore } from '../stores/template'
import { useOrderStore } from '../stores/order'
import { useFileStore } from '../stores/file'
import { ElMessage, ElDialog, ElMessageBox } from 'element-plus'
import { Share as ShareIcon, Star as StarIcon, StarFilled as StarFilledIcon, Download, Money, ChatDotRound } from '@element-plus/icons-vue'
import QrCode from 'qrcode.vue'
import {
  getTemplateDetail,
  collectTemplate,
  uncollectTemplate,
  shareTemplate
} from '../api/template'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const templateStore = useTemplateStore()
const orderStore = useOrderStore()
const fileStore = useFileStore()

const template = ref(null)
const loading = ref(true)
const showShareDialog = ref(false)
const shareType = ref('wechat')
const showPurchaseDialog = ref(false)
const payMethod = ref(1) // 1: 支付宝, 2: 微信支付

// 获取模板详情
const fetchTemplateDetail = async () => {
  const templateId = parseInt(route.params.id)
  if (!templateId) {
    ElMessage.error('无效的模板ID')
    router.push('/templates')
    return
  }

  try {
    loading.value = true
    const res = await getTemplateDetail(templateId)
    template.value = {
      ...res.data,
      isCollected: templateStore.isCollected(templateId)
    }
  } catch (error) {
    console.error('获取模板详情失败:', error)
    ElMessage.error('获取模板详情失败')
    router.push('/templates')
  } finally {
    loading.value = false
  }
}

// 收藏/取消收藏
const handleCollect = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再操作')
    router.push('/login')
    return
  }

  try {
    if (template.value.isCollected) {
      await uncollectTemplate(template.value.id)
      templateStore.removeCollection(template.value.id)
      ElMessage.success('取消收藏成功')
    } else {
      await collectTemplate(template.value.id)
      templateStore.addCollection(template.value.id)
      ElMessage.success('收藏成功，可在"我的简历"中查看收藏的模板')
    }
    template.value.isCollected = !template.value.isCollected
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 分享模板
const handleShare = async () => {
  try {
    // 如果用户不是VIP，显示提示但不中断程序
    if (!userStore.isVip) {
      ElMessage.warning('该功能仅对会员开放，请升级VIP')
      return
    }

    ElMessage.info('正在生成分享链接...')
    const res = await shareTemplate(template.value.id)
    if (res.code === 200 && res.data) {
      template.value.shareUrl = res.data.shareUrl
      showShareDialog.value = true
    } else {
      throw new Error(res.message || '分享失败')
    }
  } catch (error) {
    console.error('分享模板失败:', error)
    ElMessage.error('分享功能暂时不可用，请稍后再试')
  }
}

// 生成分享链接
const getShareUrl = (type) => {
  if (!template.value?.shareUrl) {
    return 'https://example.com';  // 返回一个默认URL，防止错误
  }
  
  const url = encodeURIComponent(template.value.shareUrl);
  const title = encodeURIComponent(`${template.value.name} - 简历模板`);
  const description = encodeURIComponent('我发现了一个不错的简历模板，分享给你！');
  
  if (type === 'qq') {
    return `http://connect.qq.com/widget/shareqq/index.html?url=${url}&title=${title}&desc=${description}`;
  } else {
    // 微信分享直接返回原始链接，用于生成二维码
    return template.value.shareUrl;
  }
}

// 开始制作
const startCreate = () => {
  router.push(`/create?template=${template.value.id}`)
}

// 在线编辑文档
const handleOnlineEdit = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再操作')
    router.push('/login')
    return
  }
  
  if (template.value && template.value.id) {
    router.push(`/document/TEMPLATE/${template.value.id}?mode=edit&showVersions=true`)
  } else {
    ElMessage.error('模板信息加载失败，请刷新页面重试')
  }
}

// 会员免费下载或者免费模板下载
const handleVipDownload = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再操作')
    router.push('/login')
    return
  }
  
  // 如果既不是会员也不是免费模板，则提示升级VIP
  if (!userStore.isVip && !template.value.isFree) {
    ElMessageBox.confirm(
      '该功能仅限VIP会员使用，是否立即开通VIP?',
      '提示',
      {
        confirmButtonText: '立即开通',
        cancelButtonText: '稍后再说',
        type: 'warning'
      }
    )
      .then(() => {
        // 跳转到VIP开通页面
        router.push('/vip')
      })
      .catch(() => {
        // 取消操作
      })
    return
  }
  
  // 会员下载或免费模板下载逻辑
  ElMessageBox.confirm('请选择下载格式', '下载模板', {
    cancelButtonText: 'Word格式',
    distinguishCancelAndClose: true,
    closeOnClickModal: false
  }).then(() => {
    // 导出PDF
    ElMessage.info('正在生成PDF，请稍候...')
    
    // 调用导出API
    fileStore.exportResumeToPdf(template.value.id)
      .then(() => {
        ElMessage.success('PDF下载成功！')
      })
      .catch((error) => {
        console.error('导出PDF失败:', error)
        // 处理可能的服务器错误
        if (error.response && error.response.status === 500) {
          ElMessage.warning('服务器暂时无法处理您的请求，请稍后再试')
        } else {
          ElMessage.error('PDF下载失败，请重试')
        }
        
        // 如果是演示环境，模拟下载成功
        if (import.meta.env.MODE === 'development') {
          ElMessage.success('在演示环境中模拟PDF下载成功')
        }
      })
  }).catch(action => {
    if (action === 'cancel') {
      // 导出Word
      ElMessage.info('正在生成Word文档，请稍候...')
      
      // 调用导出API
      fileStore.exportResumeToWord(template.value.id)
        .then(() => {
          ElMessage.success('Word文档下载成功！')
        })
        .catch((error) => {
          console.error('导出Word失败:', error)
          // 处理可能的服务器错误
          if (error.response && error.response.status === 500) {
            ElMessage.warning('服务器暂时无法处理您的请求，请稍后再试')
          } else {
            ElMessage.error('Word下载失败，请重试')
          }
          
          // 如果是演示环境，模拟下载成功
          if (import.meta.env.MODE === 'development') {
            ElMessage.success('在演示环境中模拟Word下载成功')
          }
        })
    }
  })
}

// 处理购买按钮点击
const handlePurchase = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再操作')
    router.push('/login')
    return
  }
  
  if (userStore.isVip) {
    ElMessage.info('您是VIP会员，可以直接免费下载此模板')
    return
  }
  
  // 显示购买对话框
  showPurchaseDialog.value = true
}

// 跳转到VIP开通页面
const goToVip = () => {
  showPurchaseDialog.value = false
  router.push('/vip')
}

// 确认购买
const confirmPurchase = async () => {
  try {
    if (!template.value || !template.value.id) {
      ElMessage.error('模板数据不完整，请刷新页面重试')
      return
    }
    
    // 验证模板ID是否有效
    if (isNaN(Number(template.value.id))) {
      ElMessage.error('无效的模板ID，请刷新页面重试')
      return
    }
    
    // 当前模板信息检查
    console.log('尝试购买模板：', {
      id: template.value.id,
      name: template.value.name,
      price: template.value.price
    })
    
    // 创建订单
    const orderData = {
      templateId: Number(template.value.id),
      payType: payMethod.value
    }
    
    const result = await orderStore.createNewOrder(orderData)
    if (result) {
      showPurchaseDialog.value = false
      ElMessageBox.confirm(
        '订单已创建成功，是否前往我的订单页面完成支付？',
        '订单创建成功',
        {
          confirmButtonText: '去支付',
          cancelButtonText: '稍后支付',
          type: 'success'
        }
      )
        .then(() => {
          router.push('/orders')
        })
        .catch(() => {
          // 用户选择稍后支付
        })
    }
  } catch (error) {
    console.error('创建订单失败:', error)
    // 根据错误类型显示更有用的信息
    if (error.response && error.response.data && error.response.data.message) {
      ElMessage.error(`创建订单失败: ${error.response.data.message}`)
    } else {
      ElMessage.error('创建订单失败，请稍后重试')
    }
  }
}

// 监听用户VIP状态变化
watch(() => userStore.isVip, (isVip) => {
  // 当用户VIP状态变化时，可以在这里添加额外处理逻辑
})

onMounted(() => {
  fetchTemplateDetail()
})
</script>

<template>
  <div class="template-detail" v-loading="loading">
    <template v-if="template">
      <div class="detail-container">
        <!-- 预览区域 -->
        <div class="preview-section">
          <div class="preview-image">
            <img :src="template.thumbnail" :alt="template.name">
          </div>
        </div>

        <!-- 信息区域 -->
        <div class="info-section">
          <h1 class="template-name">{{ template.name }}</h1>
          
          <div class="template-meta">
            <span class="downloads">下载量：{{ template.downloads }}</span>
            <div class="price-tag" v-if="!template.isFree">
              <span class="price">￥{{ template.price }}</span>
            </div>
          </div>

          <div class="description">
            <h3>模板说明</h3>
            <p>{{ template.description }}</p>
          </div>

          <div class="action-buttons">
            <el-button
              type="primary"
              size="large"
              @click="startCreate"
            >
              开始制作
            </el-button>
            
            <el-button
              type="primary"
              plain
              size="large"
              @click="handleOnlineEdit"
            >
              在线编辑
            </el-button>

            <el-button
              :type="template.isCollected ? 'danger' : 'default'"
              size="large"
              @click="handleCollect"
            >
              <el-icon>
                <component :is="template.isCollected ? StarFilledIcon : StarIcon" />
              </el-icon>
              {{ template.isCollected ? '已收藏' : '收藏' }}
            </el-button>

            <el-button
              type="info"
              size="large"
              @click="handleShare"
            >
              <el-icon><ShareIcon /></el-icon>
              分享
            </el-button>
            
            <!-- VIP会员可以免费下载 -->
            <el-button
              type="success"
              size="large"
              @click="handleVipDownload"
              v-if="userStore.isVip || template.isFree"
            >
              <el-icon><Download /></el-icon>
              {{ userStore.isVip ? '会员免费下载' : '免费下载' }}
            </el-button>
            
            <!-- 非VIP用户，付费模板显示购买按钮 -->
            <el-button 
              v-if="!template.isFree && !userStore.isVip" 
              type="danger" 
              size="large"
              @click="handlePurchase" 
              :loading="orderStore.loading"
            >
              <el-icon><Download /></el-icon>
              立即购买
            </el-button>
          </div>

          <div class="tips">
            <h3>使用提示</h3>
            <ul>
              <li>点击"开始制作"即可在线编辑简历</li>
              <li>所有内容均可自定义修改</li>
              <li>制作完成后可导出PDF</li>
              <li>支持在线保存，随时编辑</li>
              <li v-if="!userStore.isVip && !template.isFree">非会员用户需要购买此模板才能下载</li>
              <li v-if="userStore.isVip">VIP会员可以免费下载所有模板</li>
            </ul>
          </div>
        </div>
      </div>

      <!-- 分享对话框 -->
      <el-dialog
        v-model="showShareDialog"
        title="分享模板"
        width="360px"
        destroy-on-close
        center
      >
        <div class="share-dialog-content">
          <div class="share-tabs">
            <div
              class="share-tab"
              :class="{ active: shareType === 'wechat' }"
              @click="shareType = 'wechat'"
            >
              微信
            </div>
            <div
              class="share-tab"
              :class="{ active: shareType === 'qq' }"
              @click="shareType = 'qq'"
            >
              QQ
            </div>
          </div>
          
          <div class="qrcode-container">
            <qr-code
              :value="getShareUrl(shareType)"
              :size="200"
              level="H"
              render-as="svg"
            />
          </div>
          
          <div class="share-tips">
            {{ shareType === 'wechat' ? '请使用微信扫描二维码分享' : '请使用QQ扫描二维码分享' }}
          </div>
        </div>
      </el-dialog>
      
      <!-- 购买对话框 -->
      <el-dialog
        v-model="showPurchaseDialog"
        title="购买模板"
        width="400px"
        destroy-on-close
        center
      >
        <div class="purchase-dialog-content">
          <h3>{{ template.name }}</h3>
          <div class="price-info">
            <span class="price-label">价格：</span>
            <span class="price-value">￥{{ template.price }}</span>
          </div>
          
          <div class="payment-options">
            <h4>选择支付方式</h4>
            <div class="payment-methods">
              <div class="payment-method" :class="{ active: payMethod === 1 }" @click="payMethod = 1">
                <span class="payment-icon">
                  <el-icon class="alipay-icon"><Money /></el-icon>
                </span>
                <span>支付宝</span>
              </div>
              <div class="payment-method" :class="{ active: payMethod === 2 }" @click="payMethod = 2">
                <span class="payment-icon">
                  <el-icon class="wechat-icon"><ChatDotRound /></el-icon>
                </span>
                <span>微信支付</span>
              </div>
            </div>
          </div>
          
          <div class="vip-promotion" v-if="!userStore.isVip">
            <p>成为VIP会员，即可免费下载所有模板！</p>
            <el-button type="warning" @click="goToVip">升级VIP</el-button>
          </div>
          
          <div class="dialog-footer">
            <el-button @click="showPurchaseDialog = false">取消</el-button>
            <el-button type="primary" @click="confirmPurchase" :loading="orderStore.loading">
              确认购买
            </el-button>
          </div>
        </div>
      </el-dialog>
    </template>
  </div>
</template>

<style scoped>
.template-detail {
  padding: 40px 20px;
}

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
}

.preview-section {
  position: sticky;
  top: 20px;
}

.preview-image {
  width: 100%;
  padding-top: 141.4%; /* A4 比例 */
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.preview-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-section {
  padding: 20px;
}

.template-name {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

.template-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.downloads {
  color: #666;
  font-size: 16px;
}

.price-tag {
  font-size: 24px;
  font-weight: 500;
}

.free {
  color: #52c41a;
}

.price {
  color: #f5222d;
}

.description {
  margin-bottom: 30px;
}

.description h3 {
  font-size: 18px;
  margin-bottom: 10px;
  color: #333;
}

.description p {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 30px;
}

.tips {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
}

.tips h3 {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
}

.tips ul {
  padding-left: 20px;
}

.tips li {
  font-size: 14px;
  margin-bottom: 5px;
  color: #666;
}

.share-dialog-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.share-tabs {
  display: flex;
  width: 100%;
  margin-bottom: 20px;
}

.share-tab {
  flex: 1;
  text-align: center;
  padding: 10px;
  cursor: pointer;
  border-bottom: 2px solid #eee;
}

.share-tab.active {
  border-bottom: 2px solid #409eff;
  color: #409eff;
  font-weight: 500;
}

.qrcode-container {
  margin-bottom: 20px;
}

.share-tips {
  font-size: 14px;
  color: #666;
  text-align: center;
}

/* 新增的购买对话框样式 */
.purchase-dialog-content {
  padding: 10px;
}

.purchase-dialog-content h3 {
  font-size: 18px;
  margin-bottom: 15px;
  text-align: center;
}

.price-info {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.price-label {
  font-size: 16px;
  color: #666;
}

.price-value {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
  margin-left: 5px;
}

.payment-options {
  margin-bottom: 20px;
}

.payment-options h4 {
  font-size: 16px;
  margin-bottom: 10px;
}

.payment-methods {
  display: flex;
  justify-content: space-around;
  gap: 20px;
}

.payment-method {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
  border: 2px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  width: 120px;
}

.payment-method.active {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.payment-method .payment-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  margin-bottom: 8px;
}

.alipay-icon {
  color: #00a0e9;
  font-size: 24px;
}

.wechat-icon {
  color: #09bb07;
  font-size: 24px;
}

.vip-promotion {
  background-color: #fff9e6;
  border: 1px solid #ffeeba;
  padding: 15px;
  border-radius: 5px;
  text-align: center;
  margin-bottom: 20px;
}

.vip-promotion p {
  margin-bottom: 10px;
  color: #e6a23c;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  gap: 10px;
}

@media (max-width: 768px) {
  .detail-container {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .payment-methods {
    flex-direction: column;
    align-items: center;
  }
}
</style> 