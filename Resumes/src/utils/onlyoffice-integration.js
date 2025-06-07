/**
 * OnlyOffice前端集成工具
 */
class OnlyOfficeIntegration {
    /**
     * 构造函数
     * @param {string} apiUrl - 后端API地址
     * @param {string} documentServerUrl - OnlyOffice服务器地址
     */
    constructor(apiUrl, documentServerUrl) {
        this.apiUrl = apiUrl || '';
        this.documentServerUrl = documentServerUrl || 'http://192.168.64.129:80';
        this.editorInstance = null;
    }

    /**
     * 加载OnlyOffice API
     * @returns {Promise} - 加载完成的Promise
     */
    loadApi() {
        return new Promise((resolve, reject) => {
            if (window.DocsAPI) {
                resolve(window.DocsAPI);
                return;
            }

            const script = document.createElement('script');
            script.src = `${this.documentServerUrl}/web-apps/apps/api/documents/api.js`;
            script.onload = () => {
                if (window.DocsAPI) {
                    resolve(window.DocsAPI);
                } else {
                    reject(new Error('Failed to load OnlyOffice API'));
                }
            };
            script.onerror = () => {
                reject(new Error('Failed to load OnlyOffice API script'));
            };
            document.head.appendChild(script);
        });
    }

    /**
     * 构建API URL
     * @param {string} path - API路径
     * @returns {string} - 完整的API URL
     */
    buildApiUrl(path) {
        // 如果apiUrl为空，则使用相对路径以便利用Vite代理
        if (!this.apiUrl) {
            // 确保path以/开头
            if (!path.startsWith('/')) {
                path = '/' + path;
            }
            
            // 确保path包含api前缀
            if (!path.startsWith('/api/')) {
                path = path.startsWith('/') ? '/api' + path : '/api/' + path;
            }
            
            return path;
        }
        
        // 以下代码处理绝对URL的情况
        let requestUrl = this.apiUrl;
        
        // 确保API URL以/结尾
        if (requestUrl && !requestUrl.endsWith('/')) {
            requestUrl += '/';
        }
        
        // 移除API URL中可能的重复/api前缀
        if (requestUrl.endsWith('/api/')) {
            requestUrl = requestUrl.substring(0, requestUrl.length - 4);
        }
        
        // 确保path不以/开头
        if (path.startsWith('/')) {
            path = path.substring(1);
        }
        
        // 使用统一的文档服务API路径格式
        if (!path.includes('documents') && !path.includes('api/documents')) {
            path = `api/documents/${path}`;
        } else if (!path.includes('api/') && !path.startsWith('documents')) {
            path = `api/${path}`;
        }
        
        return `${requestUrl}${path}`;
    }

    /**
     * 获取请求头
     * @returns {Object} - 包含认证信息的请求头对象
     */
    getRequestHeaders() {
        // 从localStorage获取token和userId
        const token = localStorage.getItem('token');
        const userId = localStorage.getItem('userId');
        
        const headers = {
            'Content-Type': 'application/json'
        };
        
        // 添加认证token
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        
        // 添加userId
        if (userId) {
            headers['userId'] = userId;
        }
        
        return headers;
    }

    /**
     * 获取编辑器配置
     * @param {string} sourceType - 文档来源类型
     * @param {number} sourceId - 文档来源ID
     * @param {string} mode - 编辑模式 (view/edit/comment)
     * @param {number} userId - 用户ID
     * @param {string} userName - 用户名称
     * @returns {Promise} - 配置Promise
     */
    async getEditorConfig(sourceType, sourceId, mode, userId, userName) {
        try {
            // 根据网关配置，需要保留api前缀
            const url = this.buildApiUrl(`api/documents/${sourceType}/${sourceId}/config?mode=${mode}&userId=${userId}&userName=${encodeURIComponent(userName || '')}`);
            console.log('请求编辑器配置URL:', url);
            
            const response = await fetch(url, {
                headers: this.getRequestHeaders()
            });
            console.log('API响应状态:', response.status);
            
            if (!response.ok) {
                console.error(`API请求失败: ${response.status} ${response.statusText}`);
                // 尝试读取响应内容
                const errorText = await response.text();
                console.error('错误响应内容:', errorText);
                throw new Error(`API请求失败: ${response.status}`);
            }
            
            const result = await response.json();
            console.log('API响应数据:', result);
            
            if (result.code === 200 && result.data) {
                // 确保配置中包含docserviceApiUrl
                if (!result.data.docserviceApiUrl) {
                    result.data.docserviceApiUrl = `${this.documentServerUrl}/web-apps/apps/api/documents/api.js`;
                }
                return result.data;
            } else {
                throw new Error(result.message || '获取编辑器配置失败');
            }
        } catch (error) {
            console.error('获取编辑器配置异常:', error);
            throw error;
        }
    }

    /**
     * 初始化编辑器
     * @param {HTMLElement} container - 容器元素
     * @param {string} sourceType - 文档来源类型
     * @param {number} sourceId - 文档来源ID
     * @param {string} mode - 编辑模式 (view/edit/comment)
     * @param {number} userId - 用户ID
     * @param {string} userName - 用户名称
     * @returns {Promise} - 编辑器实例Promise
     */
    async initEditor(container, sourceType, sourceId, mode, userId, userName) {
        try {
            // 1. 加载API
            await this.loadApi();
            
            // 2. 获取配置
            const config = await this.getEditorConfig(sourceType, sourceId, mode, userId, userName);
            
            // 3. 创建编辑器
            this.editorInstance = new window.DocsAPI.DocEditor(container, config);
            
            return this.editorInstance;
        } catch (error) {
            console.error('初始化编辑器异常:', error);
            throw error;
        }
    }

    /**
     * 获取版本列表
     * @param {string} sourceType - 文档来源类型
     * @param {number} sourceId - 文档来源ID
     * @returns {Promise} - 版本列表Promise
     */
    async getVersionList(sourceType, sourceId) {
        try {
            const url = this.buildApiUrl(`api/documents/${sourceType}/${sourceId}/versions`);
            const response = await fetch(url, {
                headers: this.getRequestHeaders()
            });
            
            if (!response.ok) {
                console.error(`获取版本列表失败: ${response.status} ${response.statusText}`);
                const errorText = await response.text();
                console.error('错误响应内容:', errorText);
                throw new Error(`获取版本列表失败: ${response.status}`);
            }
            
            const result = await response.json();
            
            if (result.code === 200) {
                return result.data || [];
            } else {
                throw new Error(result.message || '获取版本列表失败');
            }
        } catch (error) {
            console.error('获取版本列表异常:', error);
            throw error;
        }
    }

    /**
     * 预览指定版本
     * @param {HTMLElement} container - 容器元素
     * @param {number} versionId - 版本ID
     * @param {number} userId - 用户ID
     * @param {string} userName - 用户名称
     * @returns {Promise} - 编辑器实例Promise
     */
    async previewVersion(container, versionId, userId, userName) {
        try {
            // 1. 加载API
            await this.loadApi();
            
            // 2. 获取配置
            const url = this.buildApiUrl(`api/documents/versions/${versionId}/preview?userId=${userId}&userName=${encodeURIComponent(userName || '')}`);
            const response = await fetch(url, {
                headers: this.getRequestHeaders()
            });
            
            if (!response.ok) {
                console.error(`预览版本失败: ${response.status} ${response.statusText}`);
                const errorText = await response.text();
                console.error('错误响应内容:', errorText);
                throw new Error(`预览版本失败: ${response.status}`);
            }
            
            const result = await response.json();
            
            if (result.code === 200 && result.data) {
                // 确保配置中包含docserviceApiUrl
                if (!result.data.docserviceApiUrl) {
                    result.data.docserviceApiUrl = `${this.documentServerUrl}/web-apps/apps/api/documents/api.js`;
                }
                
                // 3. 创建编辑器
                this.editorInstance = new window.DocsAPI.DocEditor(container, result.data);
                return this.editorInstance;
            } else {
                throw new Error(result.message || '预览版本失败');
            }
        } catch (error) {
            console.error('预览版本异常:', error);
            throw error;
        }
    }

    /**
     * 下载文档
     * @param {string} sourceType - 文档来源类型
     * @param {number} sourceId - 文档来源ID
     */
    downloadDocument(sourceType, sourceId) {
        const url = this.buildApiUrl(`api/documents/${sourceType}/${sourceId}/download`);
        const token = localStorage.getItem('token');
        const downloadUrl = token ? `${url}${url.includes('?') ? '&' : '?'}token=${token}` : url;
        window.open(downloadUrl, '_blank');
    }

    /**
     * 下载指定版本
     * @param {number} versionId - 版本ID
     */
    downloadVersion(versionId) {
        const url = this.buildApiUrl(`api/documents/versions/${versionId}/download`);
        const token = localStorage.getItem('token');
        const downloadUrl = token ? `${url}${url.includes('?') ? '&' : '?'}token=${token}` : url;
        window.open(downloadUrl, '_blank');
    }
}

export default OnlyOfficeIntegration; 