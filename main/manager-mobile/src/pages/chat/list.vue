<template>
	<z-paging class="page" ref="paging" v-model="dataList" @query="queryList">
		<view class="wrapper">
			<view class="item" v-for="(item,index) in dataList" :key="index" @click="gotoChat(item)">
				<view class="item-time">{{formatDate(item.createDate)}}</view>
				<view class="item-num">{{item.messageCount}}</view>
				<i class="far fa-trash-alt"></i>
			</view>
		</view>
	</z-paging>
</template>

<script setup>
import { ref } from 'vue'
import * as API from '@/utils/api'
import { formatDate } from '@/utils/index';
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
			// @query所绑定的方法不要自己调用！！需要刷新列表数据时，只需要调用this.$refs.paging.reload()即可
            queryList(pageNo, pageSize) {
				if(pageNo>1){
					this.$refs.paging.complete(true);
					return;
				}
				API.getChatHistoryList({agentId:"3f23e60d7d9246a18c9d68c91cd88f62"},(res) => {
					if(res.statusCode===200&&res.data.code===0){
						this.$refs.paging.complete(res.data.data);
					}else{
						this.$refs.paging.complete(false);
					}
				})
            },
			gotoChat(item) {
				uni.navigateTo({
					url: '/pages/chat/chat?chatId='+item.id+ '&datetime=' + item.createDate +'&mac='+this.mac+'&alias='+this.alias
				});
			},
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
		font-weight: 500;
	}
	.item-num{
		font-size: 28rpx;
		background-color: rgb(153, 153, 153);
		border-radius: 20rpx;
		color: #fff;
		padding: 6rpx 10rpx;
	}
</style>
