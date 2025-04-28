<template>
  <div class="welcome">
    <HeaderBar />

    <div class="operation-bar">
      <h2 class="page-title">聊天记录</h2>
      <div class="action-group">
        <!-- <div class="search-group">
          <el-input placeholder="请输入模型名称查询" v-model="search" class="search-input" clearable
            @keyup.enter.native="handleSearch" style="width: 240px" />
          <el-button class="btn-search" @click="handleSearch">
            搜索
          </el-button>
        </div> -->
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-wrapper">
      <div class="content-panel">
        <!-- 左侧用户列表 -->
        <div v-loading="historyLoading" class="history-list">
          <div class="history-header">
            <span>{{ agentName }}</span>
          </div>
          <div 
            v-for="history in historys" 
            :key="history.id" 
            class="history-item"
            :class="{ active: activeHistoryId === history.id }"
            @click="selectMessage(history.id)"
          >
            <div class="history-info">
              <div class="historyname">{{ formatDate(history.createDate) }}</div>
              <div class="last-message">{{ history.deviceId }}</div>
            </div>
            <div class="history-menu">
              <div class="info-count">{{history.messageCount}}</div>
                <el-popconfirm title="确定要删除记录吗？" @confirm="deleteHistory(history.id)">
                  <i slot="reference" class="info-btn el-icon-delete"></i>
                </el-popconfirm>
            </div>
          </div>
        </div>
        
        <!-- 右侧聊天内容 -->
        <div v-loading="messageLoading" class="chat-content">
          <div class="chat-header">
            <span>对话详情 - {{ formatDate(activeHistory.createDate) }}</span><span>设备 - {{ activeHistory.deviceId }}</span>
          </div>
          <div class="messages">
            <div 
              v-for="message in messages" 
              :key="message.id"
              :class="['message', message.role === 'user' ? 'sent' : 'received']"
            >
            <div class="msg-title"><span>{{message.role==='user'?'User':'AI'}}</span><span>{{formatDate(message.createDate)}}</span></div>
            <div class="msg-content">{{ message.content }}</div>
              <div v-if="message.role === 'assistant'" class="msg-tip">以上内容由AI生成</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <el-footer>
      <version-footer />
    </el-footer>
  </div>
</template>

<script>
import Api from "@/apis/api";
import HeaderBar from "@/components/HeaderBar.vue";
import VersionFooter from "@/components/VersionFooter.vue";
import { formatDate } from "@/utils/format";

export default {
  components: { HeaderBar, VersionFooter },
  data() {
    return {
      agentId: this.$route.query.agentId,
      agentName: this.$route.query.agentName,
      historyLoading: false,
      messageLoading: false,
      historys: [],
      activeHistoryId: 0,
      messages: []
    }
  },
  computed: {
    activeHistory() {
      const history = this.historys.find(u => u.id === this.activeHistoryId);
      return history || {};
    },
  },
  mounted() {
    console.info("agentId:",this.agentId)
    if(this.agentId){
    this.fetchChatHistory();
    }
  },
  methods: {
    fetchChatHistory(){
      this.historyLoading = true;
      Api.chat.getChatHistoryList({agentId:this.agentId}, ({data}) => {
        this.historyLoading = false;
        if (data.code === 0) {

        this.historys = data.data;
      } else {
          this.$message.error(data.msg || '获取聊天历史列表失败');
        }
      });
    },
    selectMessage(historyId) {
      this.activeHistoryId = historyId;
      this.messageLoading = true;
      Api.chat.getChatMessageList({chatId: historyId}, ({data}) => {
        this.messageLoading = false;
        if (data.code === 0) {
        this.messages = data.data;
      } else {
          this.$message.error(data.msg || '获取聊天历史列表失败');
        }
      });
    },
    deleteHistory(historyId) {
      this.historyeLoading = true;
      Api.chat.deleteChat({chatId: historyId}, ({data}) => {
        this.historyeLoading = false;
        if (data.code === 0) {
        this.messages = [];
        this.fetchChatHistory();
      } else {
          this.$message.error(data.msg || '删除聊天历史失败');
        }
      });
    },
    formatDate
  }
};
</script>

<style scoped>
.el-switch {
  height: 23px;
}

::v-deep .el-table tr {
  background: transparent;
}

.welcome {
  min-width: 900px;
  min-height: 506px;
  height: 100vh;
  display: flex;
  position: relative;
  flex-direction: column;
  background-size: cover;
  background: linear-gradient(to bottom right, #dce8ff, #e4eeff, #e6cbfd) center;
  -webkit-background-size: cover;
  -o-background-size: cover;
}

.main-wrapper {
  margin: 10px 150px;
  border-radius: 15px;
  min-height: calc(100vh - 24vh);
  height: auto;
  max-height: 80vh;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  background: rgba(237, 242, 255, 0.5);
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
}

.page-title {
  font-size: 24px;
  margin: 0;
}

.content-panel {
  flex: 1;
  display: flex;
  overflow: hidden;
  height: 100%;
  border-radius: 15px;
  background: transparent;
  /* border: 1px solid #fff; */
}

.history-header {
  display: flex;
  justify-content: start;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  padding: 15px 30px;
  border-bottom: 1px solid #e6e6e6;
  margin-bottom: 10px;
}

.history-list {
  width: 300px;
  background-color: white;
  border-right: 1px solid #e6e6e6;
  overflow-y: auto;
  padding: 10px;
}

.history-item {
  display: flex;
  padding: 12px;
  cursor: pointer;
  border-radius: 4px;
  gap: 20px;
  /* margin-bottom: 8px; */
  transition: background-color 0.3s;
}

.history-item:hover {
  background-color: #f0f0f0;
}

.history-item.active {
  background-color: #e6f7ff;
}

.history-info {
  flex-shrink: 0;
  overflow: hidden;
}

.historyname {
  font-weight: bold;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;
}

.last-message {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left
}

.history-menu {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.info-count {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #999;
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
  color: #fff;
}

.info-btn {
  font-size: 18px;
  color: red;
}


.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 20px; /* 左右间隔 */
  background-color: white;
  border-left: 1px solid #e6e6e6;
  /* border-radius: 4px; */
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  padding: 15px 30px;
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
  text-align: left;
  gap: 20px;
}

.message.sent {
  background-color: #e6f7ff;
  color: white;
  margin-left: auto;
  border-bottom-right-radius: 4px;
}

.message.received {
  background-color: #f0f0f0;
  margin-right: auto;
  border-bottom-left-radius: 4px;
}

.msg-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}
.msg-content {
  font-size: 14px;
  color: #000;
  margin-top: 5px;
}
.msg-tip {
  flex: 1;
  text-align: right;
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

</style>
