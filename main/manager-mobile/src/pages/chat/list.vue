<template>
	<z-paging class="page" ref="paging" v-model="dataList" @query="queryList">
		<view class="wrapper">
			<view class="item" v-for="(item,index) in dataList" :key="index">
				<view class="item-time" @click="gotoChat(item)">{{formatTime(item.createdAt)}}<view class="item-num">{{item.chatCount>99?'99+':item.chatCount}}</view></view>
				<i class="far fa-trash-alt" @click="showDelSession(item.sessionId)"></i>
			</view>
		</view>
		<!-- 提示信息弹窗 -->
		<uni-popup ref="message" type="message">
			<uni-popup-message :type="msgType" :message="messageText" :duration="3000"></uni-popup-message>
		</uni-popup>
		<!-- 提示窗示例 -->
		<uni-popup ref="alertDialog" type="dialog">
			<uni-popup-dialog :type="msgType" cancelText="取消" confirmText="确定" title="确定要删除吗？" content="该操作将删除聊天记录，是否确定！" @confirm="delSession"></uni-popup-dialog>
		</uni-popup>
	</z-paging>
</template>

<script setup>
import { ref } from 'vue'
import * as API from '@/utils/api'
import { formatTime } from '@/utils/index'
</script>

<script>
	export default {
		data() {
			return {
				// v-model绑定的这个变量不要在分页请求结束中自己赋值，直接使用即可
				dataList: [],
				mac: '',
				alias: '',
			}
		},		
		onLoad: function(option) {
			this.mac = option && option.mac || '';
			this.alias = option && option.alias || '';
		},
		methods: {
			dialogToggle(type) {
				this.msgType = type
				this.$refs.alertDialog.open()
			},
			messageToggle(type, text) {
				this.msgType = type?type:"success"
				this.messageText = text?text:"移动端功能开发中，敬请期待…"
				this.$refs.message.open()
			},
			// @query所绑定的方法不要自己调用！！需要刷新列表数据时，只需要调用this.$refs.paging.reload()即可
            queryList(pageNo, pageSize) {
				if(pageNo>1){
					this.$refs.paging.complete(true);
					return;
				}
				API.getAgentSessions(this.mac,{page: pageNo, limit: pageSize},({data}) => {
					if(data.code===0){
						this.$refs.paging.complete(data.data.list);
					}else{
						this.$refs.paging.complete(false);
					}
				})
            },
			gotoChat(item) {
				uni.navigateTo({
					url: '/pages/chat/chat?sessionId='+item.sessionId+'&mac='+this.mac+'&alias='+this.alias
				});
			},
			showDelSession(id) {
				this.sessionId = id;
				this.messageToggle('warn','初期阶段暂不开放')
			},
			delSession() {
				this.messageToggle('warn','初期阶段暂不开放')
			}
		}
	}
</script>

<style scoped>
	.wrapper{
		display: flex;
		flex-direction: column;
		box-shadow: 0 4rpx 24rpx 0 rgba(0, 0, 0, .1);
		background-color: #fff;
		overflow: hidden;
	}
	.item{
		position: relative;
		display: flex;
		justify-content: space-between;
		align-items: center;
		gap: 20rpx;
		padding: 40rpx;
		border-top: 2rpx solid #ebeef5;
	}
	.item i{
		color: red;
	}
	.item-time{
		flex: 1;
		font-weight: 500;
		display: flex;
		align-items: center;
		justify-content: flex-start;
		gap: 20rpx;
	}
	.item-num{
		font-size: 28rpx;
		background-color: rgb(153, 153, 153);
		border-radius: 20rpx;
		color: #fff;
		padding: 6rpx 10rpx;
	}
</style>
