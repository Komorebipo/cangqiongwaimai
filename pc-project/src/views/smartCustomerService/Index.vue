<template>
    <div class="chat-container">
        <div class="chat-header">
            <h3>智能客服</h3>
            <span class="online-status">
                <span class="dot"></span>
                在线
            </span>
        </div>

        <div class="chat-messages" ref="messagesContainer">
            <div
                v-for="msg in messageList"
                :key="msg.id"
                :class="['message-item', { 'self': msg.isSelf }]"
            >
                <div class="avatar">
                    <span>{{ msg.isSelf ? '我' : 'AI' }}</span>
                </div>
                <div class="message-content">
                    <p>{{ msg.messageContent }}</p>
                    <span class="time">{{ formatTime(msg.sendTime) }}</span>
                </div>
            </div>
        </div>

        <div class="chat-input">
            <textarea
                v-model="inputMessage"
                placeholder="请输入您的问题..."
                @keydown.enter.exact="handleSend"
                rows="2"
            ></textarea>
            <button @click="handleSend" class="send-btn">发送</button>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { sendMsgApi, queryHistoryMsgApi } from '@/api/smartCustomerServiceApi'
import { ElMessage } from 'element-plus'

const messageList = ref([])
const inputMessage = ref('')
const messagesContainer = ref(null)
const conversationId = ref('')

const userId = 'admin'

const formatTime = (dateStr) => {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    return `${hours}:${minutes}`
}

const scrollToBottom = () => {
    nextTick(() => {
        if (messagesContainer.value) {
            messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
    })
}

const handleSend = async () => {
    const content = inputMessage.value.trim()
    if (!content) {
        ElMessage.warning('请输入消息内容')
        return
    }

    inputMessage.value = ''

    const userMsg = {
        id: Date.now(),
        userId,
        messageContent: content,
        messageType: 1,
        sendTime: new Date(),
        isRead: 1,
        conversationId: conversationId.value,
        isSelf: true
    }
    messageList.value.push(userMsg)
    scrollToBottom()

    try {
        const res = await sendMsgApi({
            userId,
            messageContent: content,
            conversationId: conversationId.value || undefined
        })

        if (res.code === 200) {
            const aiMsg = res.data
            aiMsg.isSelf = false
            messageList.value.push(aiMsg)
            conversationId.value = aiMsg.conversationId
            scrollToBottom()
        } else {
            ElMessage.error(res.msg || '发送失败')
        }
    } catch (error) {
        ElMessage.error('发送失败，请稍后重试')
        const errorMsg = {
            id: Date.now() + 1,
            userId,
            messageContent: '抱歉，智能客服暂时无法回复您的问题，请稍后再试！',
            messageType: 2,
            sendTime: new Date(),
            isRead: 0,
            conversationId: conversationId.value,
            isSelf: false
        }
        messageList.value.push(errorMsg)
        scrollToBottom()
    }
}

const loadHistory = async () => {
    try {
        const res = await queryHistoryMsgApi({ userId })
        if (res.code === 200 && res.data.length > 0) {
            messageList.value = res.data.map(msg => ({
                ...msg,
                isSelf: msg.messageType === 1
            }))
            if (messageList.value.length > 0) {
                conversationId.value = messageList.value[0].conversationId
            }
            scrollToBottom()
        }
    } catch (error) {
        console.log('加载历史记录失败')
    }
}

onMounted(() => {
    loadHistory()
})
</script>

<style scoped>
.chat-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    max-height: 600px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    overflow: hidden;
    background: #fff;
}

.chat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-radius: 8px 8px 0 0;
}

.chat-header h3 {
    margin: 0;
    font-size: 16px;
}

.online-status {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
}

.dot {
    width: 8px;
    height: 8px;
    background: #52c41a;
    border-radius: 50%;
}

.chat-messages {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    background: #fafafa;
}

.message-item {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
}

.message-item.self {
    flex-direction: row-reverse;
}

.avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    color: #fff;
    flex-shrink: 0;
}

.message-item:not(.self) .avatar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.message-item.self .avatar {
    background: #1890ff;
}

.message-content {
    max-width: 70%;
    padding: 12px 16px;
    border-radius: 4px;
    background: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.message-item.self .message-content {
    background: #1890ff;
    color: #fff;
}

.message-content p {
    margin: 0 0 8px 0;
    line-height: 1.6;
    font-size: 14px;
    white-space: pre-wrap;
    word-break: break-word;
}

.message-content .time {
    font-size: 12px;
    color: #999;
}

.message-item.self .message-content .time {
    color: rgba(255, 255, 255, 0.6);
}

.chat-input {
    display: flex;
    gap: 12px;
    padding: 16px 20px;
    border-top: 1px solid #e4e7ed;
    background: #fff;
}

.chat-input textarea {
    flex: 1;
    padding: 10px 16px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    resize: none;
    font-size: 14px;
    line-height: 1.5;
    outline: none;
    transition: border-color 0.3s;
}

.chat-input textarea:focus {
    border-color: #1890ff;
}

.send-btn {
    padding: 0 24px;
    height: 40px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border: none;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
    transition: opacity 0.3s;
}

.send-btn:hover {
    opacity: 0.9;
}

.send-btn:active {
    transform: scale(0.98);
}
</style>