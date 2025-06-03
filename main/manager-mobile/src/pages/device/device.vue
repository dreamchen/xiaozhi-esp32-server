<template>
	<view class="wrapper">
		<view class="head">MAC - {{device.macAddress}}</view>
		<view class="content">
			<view class="content-title">助手昵称：</view>
			<view class="content-item"><input class="uni-input" v-model="device.assistantName" maxlength="10" placeholder="最大输入长度为10" /></view>	
			<i class="fas fa-chevron-right"></i>
		</view>
		<view class="content">
			<view class="content-title">角色模板：</view>
			<view class="content-item">
				<picker :value="agentIndex" range-key="agentName" :range="agentsTemplate" @change="bindAgentPickerChange">
						<view class="uni-input">{{agentsTemplate[agentIndex]?.agentName}}</view>
				</picker>
			</view>
			<i class="fas fa-chevron-down"></i>
		</view>
		<view class="content">
			<view class="content-title">角色音色：</view>
			<view class="content-item">
				<picker :value="voiceIndex" range-key="label" :range="voiceList" @change="bindVoicePickerChange">
						<view class="uni-input">{{voiceList[voiceIndex]?.label}}</view>
				</picker>
			</view>
			<i class="fas fa-chevron-down"></i>
		</view>
		<view class="content">
			<view class="content-title">记&nbsp;&nbsp;忆&nbsp;&nbsp;体：</view>
			<view class="content-item">
				<view class="content-item-tips">
					<label>当前记忆（每次对话后重新生成）</label><i class="far fa-trash-alt" @click="device.summaryMemory=''"></i>
				</view>
				<textarea v-model="device.summaryMemory" disabled />
			</view>	
		</view>
		<view class="foot"><button type="default" @click="gotoBack()">返回</button><button type="primary" @click="saveConfig">保存</button></view>
	</view>
</template>

<script setup>
import * as API from '@/utils/api'
</script>

<script>
	export default {
		data() {
			return {
				agentIndex: -1,
				agentsTemplate: [],
				voiceIndex: -1,
				voiceList: [],
				mac: '',
				device: {},
				form: {
					id: '',
					assistantName: '',
					ttsModelId: '',
					ttsVoiceType: '',
					ttsVoiceId: '',
					summaryMemory: '',
				}
			}
		},
		onLoad: function(option) {
			this.mac = option && option.mac || '';
			this.getDevice(this.mac);
		},
		watch: {
			'agentIndex': {
				handler(newVal, oldVal) {
					if (newVal>-1 && newVal !== oldVal) {
						let _agent = this.agentsTemplate[newVal];
						Object.assign(this.form, {
							id: _agent.id,
							ttsModelId: _agent.ttsModelId,
							ttsVoiceType: _agent.ttsVoiceType,
							ttsVoiceId: _agent.ttsVoiceId,
						});
						Object.assign(this.device, {
							ttsModelId: _agent.ttsModelId,
							ttsVoiceType: _agent.ttsVoiceType,
							ttsVoiceId: _agent.ttsVoiceId,
						})
						this.fetchVoiceOptions(_agent.ttsModelId);
					}
				},
				immediate: true
			},
			'voiceIndex': {
				handler(newVal, oldVal) {
					if (newVal>-1 && newVal !== oldVal) {
						let _voice = this.voiceList[newVal]
						Object.assign(this.form, {
							ttsVoiceType: _voice.type,
							ttsVoiceId: _voice.value,
						});
					}
				},
				immediate: true
			},
			voiceList: {
				handler() {
					this.voiceIndex = this.voiceList.findIndex(item => item.value === this.device.ttsVoiceId && item.type === this.device.ttsVoiceType);
					console.info('voiceIndex', this.voiceIndex)
				},
				immediate: true
			}
		},
		methods: {
			gotoBack: function(){
				uni.navigateBack({delta: 1});
			},
			bindAgentPickerChange: function(e) {
				this.agentIndex = e.detail.value
			},
			bindVoicePickerChange: function(e) {
				this.voiceIndex = e.detail.value
			},
			getDevice(mac) {
				API.getDeviceByMac(mac, ({data}) => {
					if(data.code===0){
						this.device = data.data;
						this.getAgentsTemplate();
					}else{
						uni.showToast({
							icon: 'none',
							position: 'bottom',
							title: data.msg
						});
					}
				})
			},
			getAgentsTemplate() {
				API.getAgentsTemplate(({data}) => {
					if(data.code===0){
						this.agentsTemplate = data.data || [];
						this.agentIndex = this.agentsTemplate.findIndex(item => item.id === this.device.agentId);
					}else{
						uni.showToast({
							icon: 'none',
							position: 'bottom',
							title: data.msg
						});
					}
				})
			},
			fetchVoiceOptions(modelId) {
				if (!modelId) {
					this.voiceList = [];
					return;
				}
				API.getModelVoices(modelId, ({ data }) => {
					if (data.code === 0) {
						this.voiceList = data.data.map(voice => ({
							value: voice.id,
							type: 0,
							label: voice.name + ' - 官方'
						}));
						console.info('voiceList', this.voiceList)
					} else {
						this.voiceList = [];
					}
				});
				//获取自定义音色
				API.getCloneList({ttsModelId: modelId, page:1, limit: 100}, ({data}) => {
					if (data.code === 0) {
						let _voiceList = data.data.list.map(voice => ({
							value: voice.id,
							type: 1,
							label: voice.name + ' - 复刻'
						}));
						this.voiceList = this.voiceList.concat(_voiceList);
						console.info(this.voiceList)
					}
				});
			},
			saveConfig() {
				Object.assign(this.form, {
					assistantName: this.device.assistantName,
					summaryMemory: this.device.summaryMemory
				});

				API.updateDeviceConfig(this.mac, this.form, ({ data }) => {
					if (data.code === 0) {
						uni.showToast({
							icon: 'none',
							position: 'bottom',
							title: '配置保存成功'
						});
					} else {
						uni.showToast({
							icon: 'none',
							position: 'bottom',
							title: data.msg || '配置保存失败',
						});
					}
				});
			}
		}
	}
</script>

<style scoped>
	.wrapper{
		padding-bottom: 40rpx;
	}
	.head{
		padding: 40rpx;
		text-align: center;
		border-bottom: 2rpx solid #ebeef5;
	}
	.content{
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx 20rpx;
		gap: 20rpx;
		border-bottom: 2rpx solid #ebeef5;
	}
	.content i {
		color: #606060;
		font-size: 22rpx;
	}
	.content-title{
		flex-shrink: 0;
		font-weight: 500;
	}
	.content-item{
		flex: 1;
		display: flex;
		flex-direction: column;
		text-align: right;
		align-items: flex-end;
	}
	.content-item uni-picker{
		width: 100%;
	}
	.content-item-tips{
		font-size: 28rpx;
		color: red;
		padding: 0 10rpx 10rpx 0;
	}
	.content-item textarea {
		width: 100%;
		height: 160rpx;
		background-color: whitesmoke;
		border-radius: 6rpx;
		padding: 10rpx;
		text-align: left;
	}
	.foot{
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 40rpx;
		gap: 40rpx;
	}
	.foot button{
		flex: 1;
	}
</style>
