<template>
	<z-paging class="page" ref="paging" v-model="dataList" @query="queryList">
		<template #top>
		  <view class="chat-header">
			<label>{{alias?alias+'('+mac+')':mac}}</label>
			<label>{{datetime?formatDate(parseInt(datetime)):''}}</label>
		  </view>
		  </template>
		  <view class="messages">
			<view v-for="(item,index) in dataList" :key="index" :class="['message', item.role === 'user' ? 'sent' : 'received']"
			>
			<view class="msg-title"><span>{{item.role==='user'?'User':'AI'}}</span><span>{{formatDate(item.createDate)}}</span></view>
			<view class="msg-content">{{ item.content }}</view>
			  <view v-if="item.role === 'assistant'" class="msg-tip">以上内容由AI生成</view>
			</view>
		  </view>
	</z-paging>
</template>
<script setup>
	import * as API from '@/utils/api'
	import { formatDate } from '@/utils/index';
</script>
<script>
	export default {
		data() {
			return {
				// v-model绑定的这个变量不要在分页请求结束中自己赋值，直接使用即可
				dataList: [],
				chatId: '',
				datetime: '',
				mac: '',
				alias: '',
			}
		},	
		onLoad: function(option) {
			console.info("option:",option)
			this.chatId = option && option.chatId || '';
			this.datetime = option && option.datetime || '';
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
				API.getChatMessageList({chatId: this.chatId},(res) => {
					if(res.statusCode===200&&res.data.code===0){
						this.$refs.paging.complete(res.data.data);
					}else{
						this.$refs.paging.complete(false);
					}
				})
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
	.msg-content {
	  font-size: 28rpx;
	  color: #000;
	  margin-top: 10rpx;
	}
	.msg-tip {
	  flex: 1;
	  text-align: right;
	  font-size: 24rpx;
	  color: #666;
	  margin-top: 10rpx;
	}
</style>
