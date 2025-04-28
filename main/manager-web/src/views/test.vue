<template>
    <div class="chat-container">
      <!-- 左侧用户列表 -->
      <div class="user-list">
        <div 
          v-for="user in users" 
          :key="user.id" 
          class="user-item"
          :class="{ active: activeUser === user.id }"
          @click="selectUser(user.id)"
        >
          <img :src="user.avatar" class="avatar">
          <div class="user-info">
            <div class="username">{{ user.name }}</div>
            <div class="last-message">{{ user.lastMessage }}</div>
          </div>
        </div>
      </div>
      
      <!-- 右侧聊天内容 -->
      <div class="chat-content">
        <div class="chat-header">
          <h3>{{ activeUserName }}</h3>
        </div>
        <div class="messages">
          <div 
            v-for="message in messages" 
            :key="message.id"
            :class="['message', message.sender === 'me' ? 'sent' : 'received']"
          >
            {{ message.content }}
          </div>
        </div>
        <div class="message-input">
          <el-input 
            v-model="newMessage" 
            placeholder="输入消息..."
            @keyup.enter="sendMessage"
          ></el-input>
          <el-button type="primary" @click="sendMessage">发送</el-button>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        users: [
          { id: 1, name: '张三', avatar: 'https://example.com/avatar1.jpg', lastMessage: '你好！' },
          { id: 2, name: '李四', avatar: 'https://example.com/avatar2.jpg', lastMessage: '在吗？' },
          // 更多用户...
        ],
        activeUser: 1,
        messages: [
          { id: 1, sender: 'other', content: '你好！' },
          { id: 2, sender: 'me', content: '你好，有什么可以帮你的吗？' },
          // 更多消息...
        ],
        newMessage: ''
      }
    },
    computed: {
      activeUserName() {
        const user = this.users.find(u => u.id === this.activeUser);
        return user ? user.name : '';
      }
    },
    methods: {
      selectUser(userId) {
        this.activeUser = userId;
        // 这里可以加载该用户的聊天记录
      },
      sendMessage() {
        if (!this.newMessage.trim()) return;
        
        this.messages.push({
          id: Date.now(),
          sender: 'me',
          content: this.newMessage
        });
        
        this.newMessage = '';
        // 滚动到底部
        this.$nextTick(() => {
          const container = document.querySelector('.messages');
          container.scrollTop = container.scrollHeight;
        });
      }
    }
  }
  </script>
  
  <style scoped>
  .chat-container {
    display: flex;
    height: 100vh;
    background-color: #f5f5f5;
  }
  
  .user-list {
    width: 300px;
    background-color: white;
    border-right: 1px solid #e6e6e6;
    overflow-y: auto;
    padding: 10px;
  }
  
  .user-item {
    display: flex;
    padding: 12px;
    cursor: pointer;
    border-radius: 4px;
    margin-bottom: 8px;
    transition: background-color 0.3s;
  }
  
  .user-item:hover {
    background-color: #f0f0f0;
  }
  
  .user-item.active {
    background-color: #e6f7ff;
  }
  
  .avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin-right: 12px;
  }
  
  .user-info {
    flex: 1;
    overflow: hidden;
  }
  
  .username {
    font-weight: bold;
    margin-bottom: 4px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .last-message {
    font-size: 12px;
    color: #999;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .chat-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-left: 20px; /* 左右间隔 */
    background-color: white;
    border-radius: 4px;
    overflow: hidden;
  }
  
  .chat-header {
    padding: 15px;
    border-bottom: 1px solid #e6e6e6;
  }
  
  .messages {
    flex: 1;
    padding: 15px;
    overflow-y: auto;
  }
  
  .message {
    max-width: 70%;
    margin-bottom: 15px;
    padding: 10px 15px;
    border-radius: 18px;
    line-height: 1.4;
  }
  
  .message.sent {
    background-color: #1890ff;
    color: white;
    margin-left: auto;
    border-bottom-right-radius: 4px;
  }
  
  .message.received {
    background-color: #f0f0f0;
    margin-right: auto;
    border-bottom-left-radius: 4px;
  }
  
  .message-input {
    display: flex;
    padding: 15px;
    border-top: 1px solid #e6e6e6;
  }
  
  .message-input .el-input {
    flex: 1;
    margin-right: 10px;
  }
  </style>