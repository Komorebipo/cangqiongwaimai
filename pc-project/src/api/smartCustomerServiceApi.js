import request from '@/http'

/**
 * 发送消息并获取AI回复
 * @param {Object} data - 请求数据
 * @param {string} data.userId - 用户ID
 * @param {string} data.messageContent - 消息内容
 * @param {string} [data.conversationId] - 会话ID（首次为空）
 * @returns {Promise}
 */
export const sendMsgApi = (data) => {
    return request({
        url: '/msg/send',
        method: 'post',
        data
    })
}

/**
 * 查询历史对话记录
 * @param {Object} params - 查询参数
 * @param {string} params.userId - 用户ID
 * @param {string} [params.conversationId] - 会话ID
 * @returns {Promise}
 */
export const queryHistoryMsgApi = (params) => {
    return request({
        url: '/msg/history',
        method: 'get',
        params
    })
}

/**
 * 标记消息为已读
 * @param {Object} params - 参数
 * @param {string} params.userId - 用户ID
 * @param {string} params.conversationId - 会话ID
 * @returns {Promise}
 */
export const markMsgAsReadApi = (params) => {
    return request({
        url: '/msg/mark-read',
        method: 'post',
        params
    })
}