<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useTemplateStore } from '../stores/template'
import { ElMessage, ElDialog, ElMessageBox } from 'element-plus'
import { Share as ShareIcon, Star as StarIcon, StarFilled as StarFilledIcon, Download, Money } from '@element-plus/icons-vue'
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

const template = ref(null)
const loading = ref(true)
const showShareDialog = ref(false)
const shareType = ref('wechat')

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

// 会员免费下载
const handleVipDownload = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再操作')
    router.push('/login')
    return
  }
  
  if (!userStore.isVip) {
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
  
  // 会员下载逻辑
  ElMessage.success('开始下载模板，请稍候...')
  
  // 模拟下载过程
  setTimeout(() => {
    ElMessage.success('下载成功！')
    // 这里可以触发实际的下载逻辑，如打开一个新窗口或者下载文件
    // window.open(`/api/templates/${template.value.id}/download`, '_blank')
  }, 1500)
}

// 购买模板
const handleBuyTemplate = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再操作')
    router.push('/login')
    return
  }
  
  ElMessageBox.confirm(
    `确认以 ¥${template.value.price} 的价格购买该模板吗？`,
    '确认购买',
    {
      confirmButtonText: '确认购买',
      cancelButtonText: '取消',
      type: 'info'
    }
  )
    .then(() => {
      // 创建订单并跳转到支付页面
      ElMessage.success('订单创建成功，正在跳转到支付页面...')
      
      // 这里可以调用创建订单的API，然后跳转到支付页面
      // 模拟一下创建订单的过程
      setTimeout(() => {
        // 实际项目中，这里应该是调用API创建订单，然后根据返回的订单信息跳转
        router.push(`/orders/pay?templateId=${template.value.id}&price=${template.value.price}`)
      }, 1000)
    })
    .catch(() => {
      // 取消购买
    })
}

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
            <el-button type="primary" size="large" @click="startCreate">
              开始制作
            </el-button>
            
            <el-button
              :type="template.isCollected ? 'success' : 'default'"
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
            
            <!-- 所有模板VIP会员可以免费下载 -->
            <el-button
              type="success"
              size="large"
              @click="handleVipDownload"
            >
              <el-icon><Download /></el-icon>
              会员免费下载
            </el-button>
            
            <!-- 非VIP会员可以购买 -->
            <el-button
              v-if="!userStore.isVip"
              type="danger"
              size="large"
              @click="handleBuyTemplate"
            >
              <el-icon><Money /></el-icon>
              立即购买 (¥{{template.price}})
            </el-button>
          </div>

          <div class="tips">
            <h3>使用提示</h3>
            <ul>
              <li>点击"开始制作"即可在线编辑简历</li>
              <li>所有内容均可自定义修改</li>
              <li>制作完成后可导出PDF</li>
              <li>支持在线保存，随时编辑</li>
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
  color: #666;
  line-height: 1.6;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-bottom: 40px;
}

.tips {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
}

.tips h3 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #333;
}

.tips ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips li {
  color: #666;
  margin-bottom: 10px;
  padding-left: 20px;
  position: relative;
}

.tips li::before {
  content: "•";
  color: #1890ff;
  position: absolute;
  left: 0;
}

@media (max-width: 992px) {
  .detail-container {
    grid-template-columns: 1fr;
  }

  .preview-section {
    position: static;
  }

  .preview-image {
    max-width: 500px;
    margin: 0 auto;
  }

  .info-section {
    padding: 0;
  }
}

@media (max-width: 576px) {
  .template-detail {
    padding: 20px;
  }

  .template-name {
    font-size: 24px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}

.share-dialog-content {
  padding: 20px 0;
}

.share-tabs {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.share-tab {
  padding: 8px 24px;
  cursor: pointer;
  position: relative;
  color: #666;
}

.share-tab.active {
  color: #409EFF;
}

.share-tab.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #409EFF;
}

.qrcode-container {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.share-tips {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-top: 15px;
}
</style> 