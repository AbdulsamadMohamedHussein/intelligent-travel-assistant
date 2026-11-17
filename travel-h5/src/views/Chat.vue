<template>
  <div class="page-container chat-page">
    <div class="page-header">
      <van-nav-bar title="AI Chat Assistant" left-arrow left-text="go back" fixed @click-left="router.back()" />
    </div>

    <div class="chat-container" ref="chatContainer">
      <div v-if="messages.length === 0" class="chat-empty">
        <van-empty description="Start a conversation" />
        <div class="quick-questions">
          <div class="quick-title">Quick Questions</div>
          <van-tag class="quick-tag" v-for="question in quickQuestions" :key="question" @click="sendMessage(question)"
            size="large">
            {{ question }}
          </van-tag>
        </div>
      </div>
      <div v-else class="message-list">
        <ChatBubble v-for="message in messages" :key="message.id" :message="message" />
        <div v-if="isStreaming" class="streaming-indicator">
          <van-loading type="spinner" size="24"/>
          <span>Assistant is typing...</span>
        </div>
      </div>
    </div>

    <div class="chat-input-area">
      <van-cell-group inset>
        <van-field v-model="inputMessage" placeholder="Input your question" :disabled="isStreaming">
          <template #button>
            <van-button size="small" type="primary" @click="sendMessage()"
              :disabled="!inputMessage.trim()">Send</van-button>
          </template>
        </van-field>
      </van-cell-group>
    </div>
  </div>
</template>


<script setup>
import { ref, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchStream } from '../utils/api'
import ChatBubble from '../components/ChatBubble.vue'

const router = useRouter()
const route = useRoute()
// When opened from a trip detail page, the city is passed so the assistant knows the context
const cityContext = route.query.city || ''
const inputMessage = ref('')
const isStreaming = ref(false)
const messages = ref([]) // conversation between User and AI
const chatContainer = ref(null)

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

const quickQuestions = ref([
  'Best place to visit in Beijing',
  'Weather in Shanghai',
  'Food recommendation in Chengdu',
  "Guangzhou's tallest building",
  'How much for 3 days in Hangzhou'
])

const fetchAIResponse = (userMessage) => {
  isStreaming.value = true
  messages.value.push({
    id: Date.now() + 2,
    role: 'AI',
    content: '',
    timestamp: new Date().toISOString()
  })
  let fullResponse = ''
  const aiMessage = messages.value[messages.value.length - 1]

  fetchStream('chat', { message: userMessage, city: cityContext },
    (chunk) => {
      fullResponse += chunk
      aiMessage.content = fullResponse
      scrollToBottom()
    },
    () => {
      isStreaming.value = false
      scrollToBottom()
    },
    (error) => {
      aiMessage.content = `Sorry, something went wrong: ${error}`
      isStreaming.value = false
      scrollToBottom()
    })
}

const addUserMessage = (msg) => {
  messages.value.push({
    id: Date.now(),
    role: 'user',
    content: msg,
    timestamp: new Date().toISOString()
  })
}

// text is given when a quick question is tapped; otherwise the typed message is used
const sendMessage = (text) => {
  const msg = (typeof text === 'string' ? text : inputMessage.value).trim()
  if (!msg || isStreaming.value) return
  addUserMessage(msg)
  inputMessage.value = ''
  scrollToBottom()
  fetchAIResponse(msg)
}
</script>

<style scoped>
.page-header {
  height: 46px;
}

.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding-bottom: 0px !important;
}

.chat-container {
  height: calc(100vh - 46px - 110px);
  overflow-y: auto;
  padding: 16px;
  padding-bottom: 60px;
}

.chat-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.quick-questions {
  margin-top: 32px;
  text-align: center;
}

.quick-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 16px;
}

.quick-tag {
  margin: 8px;
  cursor: pointer;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.streaming-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  color: #999;
  font-size: 14px;
}

.chat-input-area {
  position: fixed;
  bottom: 50px;
  left: 0;
  right: 0;
  background: #fff;
  padding: 8px 16px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  max-width: 750px;
  margin: 0 auto;
}

.chat-input-area :deep(.van-field) {
  background: #f7f8fa;
  border-radius: 20px;
  padding: 8px 16px;
}
</style>
