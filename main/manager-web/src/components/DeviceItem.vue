<template>
  <div class="device-item">
    <div style="display: flex;justify-content: space-between;">
      <div style="font-weight: 700;font-size: 18px;text-align: left;color: #3d4566;">
        {{ device.agentName }}
      </div>
      <div>
        <img src="@/assets/home/delete.png" alt="" style="width: 18px;height: 18px;margin-right: 10px;"
          @click.stop="handleDelete" />
        <el-tooltip class="item" effect="dark" :content="device.systemPrompt" placement="top"
          popper-class="custom-tooltip">
          <img src="@/assets/home/info.png" alt="" style="width: 18px;height: 18px;" />
        </el-tooltip>
      </div>
    </div>
    <div class="device-name">
      设备型号：{{ device.ttsModelName }}
    </div>
    <div class="device-name">
      音色模型：{{ device.ttsVoiceName }}
    </div>
    <div class="version-info">
      最近对话：{{ formatDate(device.lastConnectedAt) }}
    </div>
    <div style="display: flex; justify-content: space-between; align-items: center; gap: 10px; padding-top: 10px;">
      <div class="settings-btn" @click="handleConfigure">
        配置角色
      </div>
      <!-- <div class="settings-btn" disabled>
        声纹识别
      </div> -->
      <div class="settings-btn" @click="handleChatHistory">
        聊天记录
      </div>
      <div class="settings-btn" @click="handleDeviceManage">
        设备管理({{ device.deviceCount }})
      </div>
    </div>
  </div>
</template>

<script>
import { formatDate } from "@/utils/format";

export default {
  name: 'DeviceItem',
  props: {
    device: { type: Object, required: true }
  },
  data() {
    return { switchValue: false }
  },
  methods: {
    handleDelete() {
      this.$emit('delete', this.device.agentId)
    },
    handleConfigure() {
      this.$router.push({ path: '/role-config', query: { agentId: this.device.agentId } });
    },
    handleChatHistory() {
      this.$router.push({ path: '/chat-history', query: { agentId: this.device.agentId,agentName: this.device.agentName } });
    },
    handleDeviceManage() {
      this.$router.push({ path: '/device-management', query: { agentId: this.device.agentId } });
    },
    formatDate
  }
}
</script>
<style scoped>
.device-item {
  border-radius: 20px;
  background: #fafcfe;
  padding: 20px;
  box-sizing: border-box;
}

.device-name {
  margin: 7px 0 10px;
  font-weight: 400;
  font-size: 11px;
  color: #3d4566;
  text-align: left;
}

.version-info {
  margin: 7px 0 10px;
  font-weight: 400;
  font-size: 11px;
  color: #979db1;
  text-align: left;
}

.settings-btn {
  font-weight: 500;
  font-size: 12px;
  color: #5778ff;
  background: #e6ebff;
  width: auto;
  padding: 0 12px;
  height: 21px;
  line-height: 21px;
  cursor: pointer;
  border-radius: 14px;
}
</style>

<style>
.custom-tooltip {
  max-width: 400px;
  word-break: break-word;
}
</style>