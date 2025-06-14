<template>
	<z-paging class="page" ref="paging" v-model="dataList" @query="queryList">
			<!-- z-paging默认铺满全屏，此时页面所有view都应放在z-paging标签内，否则会被盖住 -->
			<!-- 需要固定在页面顶部的view请通过slot="top"插入，包括自定义的导航栏 -->
			<z-swiper grabCursor autoplay pagination loop :modules="modules">
			  <z-swiper-item v-for="item in list" :key="item.id">
				<view class="swiper-item"><image mode="aspectFill" src="/static/banner.jpg"></image></view>
			  </z-swiper-item>
			</z-swiper>
			<view class="wrapper">
				<view class="item" v-for="(item,index) in dataList" :key="index">
					<view class="item-head">
						<view class="head-l">{{item.name}}</view>
						<view class="head-r">
							<i class="far fa-edit" @click="showEditName(item.id, item.name, 'success')"></i>
						</view>
					</view>
					<view class="item-content">
						<view class="content-item">
							<view class="content-item-l">声音编码：</view>
							<view class="content-item-r">{{item.ttsVoice}}</view>
						</view>
						<view class="content-item">
							<view class="content-item-l">归属模型：</view>
							<view class="content-item-r">{{item.ttsModelName}}</view>
						</view>
						<view class="content-item">
							<view class="content-item-l">创建时间：</view>
							<view class="content-item-r">{{formatDate(item.createDate)}}</view>
						</view>
					</view>
					<view class="item-foot">
						<view class="foot-item"><sy-audio audioTitle subheading backgroundColor="#bfcadb" activeColor="#4167ed" :src="item.voiceDemo"></sy-audio></view>
						<view class="foot-item" style="color: #F56C6C;" @click="showUnBindDevice(item.id)">删除</view>
					</view>
				</view>
			</view>
			
			<view class="fixed-btn" @click="gotoClone"><i class="fas fa-plus"></i></view>

			<!-- 提示信息弹窗 -->
			<uni-popup ref="message" type="message">
				<uni-popup-message :type="msgType" :message="messageText" :duration="3000"></uni-popup-message>
			</uni-popup>
			<!-- 提示窗示例 -->
			<uni-popup ref="alertDialog" type="dialog">
				<uni-popup-dialog :type="msgType" cancelText="取消" confirmText="确定" title="确定要删除吗？" content="该操作将删除设备，是否确定！" @confirm="unbindDevice"></uni-popup-dialog>
			</uni-popup>
			<!-- 提示窗示例 -->
			<uni-popup ref="nameDialog" type="dialog">
				<uni-popup-dialog :type="msgType" mode="input" before-close cancelText="取消" confirmText="确定" title="设置声音名称" placeholder="请输入声音名称" @close="$refs.nameDialog.close()" @confirm="setName"></uni-popup-dialog>
			</uni-popup>
	</z-paging>
</template>

<script setup>
import { ref } from 'vue'
import * as API from '@/utils/api'
import { formatDate } from '@/utils/index';

const list = ref(
    Array.from({
      length: 2
    }).map((item, index) => {
      return {
        text: `Slide ${index + 1}`,
        id: index + 1
      }
    })
  )
  
import { Autoplay,Pagination } from '@zebra-ui/swiper/modules'
const modules = ref([Autoplay,Pagination])
</script>

<script>
	export default {
		data() {
			return {
				// v-model绑定的这个变量不要在分页请求结束中自己赋值，直接使用即可
                dataList: [],
				title: 'Hello 123',
				msgType: 'success',
				unbindId: '',
				oldVal: '',
				src: 'https://wpadmin.yuanxiaowei.com/uploads/mp3/20240402/4ea56772a7a4f602975e9e2ac098021e.mp3'
			}
		},
		onLoad() {

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
				API.getCloneList({pageNum: pageNo, pageSize: pageSize}, ({data}) => {
					if(data.code===0){
						this.$refs.paging.complete(data.data.list);
					}else{
						this.messageToggle("error", data.msg)
						this.$refs.paging.complete(false);
					}
				})
            },
			showEditName(id, name, type) {
				this.msgType = type;
				this.$refs.nameDialog.open();
				this.voice_id = id;
      			this.oldVal = name || '';
			},
			setName(name) {
				if(!name){
					uni.showToast({
		                icon: 'none',
						position: 'bottom',
		                title: '备注不能为空'
					});
					return false;
				}
				
				if(this.oldVal === name) {
					this.$refs.nameDialog.close()
					return false;
				}

				API.updateCloneVoice({id:this.voice_id, name: name}, ({ data }) => {
					if (data.code === 0) {
						this.$refs.nameDialog.close()
						this.messageToggle(undefined, '设置成功')
						this.$refs.paging.refresh()
					} else {
						this.messageToggle("error", data.msg)
					}
				})
			},
			gotoClone() {
				uni.navigateTo({
					url: '/pages/voice/clone'
				})
			},
			showUnBindDevice(id) {
				this.unbindId = id;
				this.messageToggle('warn','初期阶段暂不开放')
			},
			unbindDevice() {
				this.messageToggle('warn','初期阶段暂不开放')
			}
		}
	}
</script>

<style scoped>
	.wrapper{
		padding: 20rpx;
		display: flex;
		flex-direction: column;
		gap: 40rpx;
	}
	.item {
		border-radius: 8rpx;
		border: 2rpx solid #ebeef5;
		background-color: #fff;
		overflow: hidden;
		color: #303133;
		box-shadow: 0 4rpx 24rpx 0 rgba(0, 0, 0, .1);
		font-size: 28rpx;
	}
	.item-head{
		display: flex;
		justify-content: space-between;
		align-items: center;
		border-bottom: 2rpx solid #ebeef5;
		padding: 18rpx 20rpx;
	}
	.head-l{
		font-weight: 500;
		font-size: 32rpx;
	}
	.head-r{
		font-size: 28rpx;
		color: #808080;
	}
	.head-r I{
		padding-left: 10rpx;
		color: #F56C6C;
		cursor: pointer;
	}
	.item-content{
		display: flex;
		flex-direction: column;
		align-items: start;
		justify-content: space-between;
		padding: 30rpx;
		gap: 10rpx;
	}
	.content-item{
		width: 100%;
		display: flex;
		justify-content: space-between;
		align-items: center;
		gap: 1rpx;
	}
	.content-item-l{
		font-weight: 500;
	}
	.content-item-r{
		flex: 1;
	}
	.item-foot{
		display: flex;
		align-items: center;
		justify-content: space-between;
		border-top: 2rpx solid #ebeef5;
	}
	
	.foot-item{
		flex: 1;
		text-align: center;
		padding: 14rpx;
		color: #67C23A;
		cursor: pointer;
		border-left: 2rpx solid #ebeef5;
	}
	.foot-item:first-child{
		flex: 2;
		padding: 0 8rpx;
		border: none;
	}
	.fixed-btn {
		position: fixed;
		width: 80rpx;
		height: 80rpx;
		border-radius: 40rpx;
		right: 60rpx;
		bottom: 200rpx;
		background-color: coral;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.fixed-btn i {
		color: #fff;
	}
</style>
<style scoped lang="scss">
	.swiper-item {
		width: 100%;
		height: 200px;
		display: flex;
		align-items: center;
		justify-content: center;
		user-select: none;
		background-color: skyblue;
	}
	.swiper-item image {
		width: 100%;
		height: 100%;
	}
</style>
<style scoped lang="scss">
	::v-deep
	//sy-audio bar #bfcadb  icon #4362b3 text #5f7ba7
	.audio_center {
		background-color: #eef0fd;
		box-shadow: none;
		border-radius: none;
		padding: 10rpx 18rpx;
		gap: 20rpx;

		.single_title_info{
			margin-right: 0;
		}

		&_cover {
			width: auto;
			background-color: transparent;
			padding: 16rpx 0 10rpx 24rpx;
			margin-right: 0;

			.iconfont {
				background-color: transparent;
				color: #4362b3;
				font-size: 32rpx;
			}
		}
		&_right { 
			.single {
				flex: 1;
				padding: 0;
				flex-direction: row;
				align-items: center;
				gap: 6rpx;
				.tips {
					flex-shrink: 0;
					min-height: 10rpx;
					color: #5f7ba7;	
				}
				uni-slider {
					flex: 1;
					min-height: 10rpx;
				}
			}
		}
	}
</style>