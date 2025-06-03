<template>
	<z-paging class="page" ref="paging" v-model="dataList" @query="queryList">
		<template #top>
		  <view class="chat-header">
			<label>{{alias?alias+'('+mac+')':mac}}</label>
			<label>{{createdAt?formatTime(createdAt):''}}</label>
		  </view>
		</template>
		<view class="messages">
			<view v-for="(item,index) in dataList" :key="index" :class="['message', item.chatType === 1 ? 'sent' : 'received']">
			<view class="msg-title"><span>{{item.chatType===1?'User':'AI'}}</span><span>{{formatTime(item.createdAt)}}</span></view>
			<view class="msg-wrapp">
				<view class="msg-content">{{ item.content }}</view>
				<i v-if="item.audioId" :class="getAudioIconClass(item.audioId)" style="color: #1890ff" @click="playAudio(item.audioId)"></i>
			</view>
			<view v-if="item.chatType === 2" class="msg-tip">以上内容由AI生成</view>
			</view>
		  </view>
	</z-paging>
</template>
<script setup>
	import * as API from '@/utils/api'
	import { formatTime } from '@/utils/index';
</script>
<script>
	export default {
		data() {
			return {
				// v-model绑定的这个变量不要在分页请求结束中自己赋值，直接使用即可
				dataList: [],
				sessionId: '',
				createdAt: '',
				mac: '',
				alias: '',
				playingAudioId: null,
				innerAudioContext: null
			}
		},	
		onLoad: function(option) {
			this.sessionId = option && option.sessionId || '';
			this.createdAt = option && option.createdAt || '';
			this.mac = option && option.mac || '';
			this.alias = option && option.alias || '';
		},
		methods: {
			// @query所绑定的方法不要自己调用！！需要刷新列表数据时，只需要调用this.$refs.paging.reload()即可
			queryList(pageNo, pageSize) {
				if(pageNo>1){
					this.$refs.paging.complete(true);
					return;
				}
				API.getAgentChatHistory(this.mac, this.sessionId, ({data}) => {
					if(data.code===0){
						this.$refs.paging.complete(data.data);
					}else{
						this.$refs.paging.complete(false);
					}
				})
			},
			playAudio(audioId) {
				if (this.playingAudioId === audioId) {
					// 如果正在播放当前音频，则停止播放
					if (this.innerAudioContext) {
						this.innerAudioContext.pause();
						this.innerAudioContext.destroy()
						this.innerAudioContext = null
					}
					this.playingAudioId = null;
					return;
				}

				// 停止当前正在播放的音频
				if (this.innerAudioContext) {
					this.innerAudioContext.pause();
					this.innerAudioContext.destroy()
					this.innerAudioContext = null
				}

				// 先获取音频下载ID
				this.playingAudioId = audioId;
				API.getAudioId(audioId, (res) => {
					if (res.data && res.data.data) {
						this.innerAudioContext = uni.createInnerAudioContext();
						this.innerAudioContext.src = API.getBaseUrl() + `/agent/play/${res.data.data}`;
						this.innerAudioContext.onPlay(() => {
							console.log('开始播放');
						});
						this.innerAudioContext.onEnded(() => {
							console.log('播放结束');
							this.playingAudioId = null;
							this.innerAudioContext.destroy();
							this.innerAudioContext = null;
						});
						this.innerAudioContext.onError((res) => {
							console.log('播放错误');
							console.log(res.errMsg);
							this.playingAudioId = null;
							this.innerAudioContext.destroy();
							this.innerAudioContext = null;
							console.log(res.errCode);
						});
						this.innerAudioContext.play();
					}
				});
			},
			getAudioIconClass(audioId) {
				if (this.playingAudioId === audioId) {
					return 'fas fa-spinner fa-spin';
				}
				return 'far fa-play-circle';
			},
		}
	}
</script>

<style scoped>
	.chat-header {
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		align-items: center;
		font-size: 36rpx;
		font-weight: 600;
		padding: 30rpx 60rpx;
		border-bottom: 2rpx solid #ebeef5;
	}

	.messages {
		flex: 1;
		padding: 30rpx;
		overflow-y: auto;
	}

	.message {
		max-width: 70%;
		margin-bottom: 30rpx;
		padding: 20rpx 30rpx;
		border-radius: 36rpx;
		line-height: 1.4;
		text-align: left;
		gap: 40rpx;
	}

	.message.sent {
		background-color: #e6f7ff;
		color: white;
		margin-left: auto;
		border-bottom-right-radius: 4rpx;
	}

	.message.received {
		background-color: #f0f0f0;
		margin-right: auto;
		border-bottom-left-radius: 4rpx;
	}

	.msg-title {
		display: flex;
		justify-content: space-between;
		align-items: center;
		font-size: 24rpx;
		color: #666;
		margin-top: 10rpx;
	}
	.msg-wrapp {
		display: flex;
		justify-content: space-between;
		align-items: center;
		gap:  10rpx;
	}
	.msg-content {
		font-size: 28rpx;
		color: #000;
		margin-top: 10rpx;
	}
	.msg-tip {
		/* flex: 1; */
		text-align: right;
		font-size: 24rpx;
		color: #666;
		margin-top: 10rpx;
	}
</style>
