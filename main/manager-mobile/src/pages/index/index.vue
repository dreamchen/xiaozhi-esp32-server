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
						<view class="head-l">MAC - {{item.macAddress}}</view>
						<view class="head-r">
							别名: {{item.alias||'-'}}
							<i class="far fa-edit" @click="showEditAlias(item.id, item.alias, 'success')"></i>
						</view>
					</view>
					<view class="item-content">
						<view class="content-item">
							<view class="content-item-l">助手昵称：</view>
							<view class="content-item-r">{{item.assistantName}}</view>
						</view>
						<view class="content-item">
							<view class="content-item-l">角色模板：</view>
							<view class="content-item-r">{{item.agentName}}</view>
						</view>
						<view class="content-item">
							<view class="content-item-l">角色音色：</view>
							<view class="content-item-r">{{item.ttsVoiceName}}</view>
						</view>
						<view class="content-item">
							<view class="content-item-l">最近对话：</view>
							<view class="content-item-r">{{formatDate(item.lastConnectedAt)}}</view>
						</view>
						<view class="content-item">
							<view class="content-item-inline">
								<view class="content-item-l">固件版本：</view>
								<view class="content-item-r">{{item.appVersion}}</view>
							</view>
							<view class="content-item-inline">
								<view class="content-item-l">OTA升级：</view>
								<view class="content-item-r"><switch :checked="item.autoUpdate?true:false" @change="changeOTA(index, $event.detail.value)" style="transform:scale(0.7)"/></view>
							</view>
						</view>
					</view>
					<view class="item-foot">
						<view class="foot-item" @click="gotoDevice(item.macAddress)">配置角色</view>
						<view class="foot-item" style="color: #E6A23C;" @click="gotoChatList(item.macAddress,item.alias)">历史对话</view>
						<view class="foot-item" style="color: #F56C6C;" @click="showUnBindDevice(item.id)">删除</view>
					</view>
				</view>
			</view>
			<view class="fixed-btn" @click="showBindDevice('success')"><i class="fas fa-plus"></i></view>
			<!-- 提示信息弹窗 -->
			<uni-popup ref="message" type="message">
				<uni-popup-message :type="msgType" :message="messageText" :duration="3000"></uni-popup-message>
			</uni-popup>
			<!-- 提示窗示例 -->
			<uni-popup ref="alertDialog" type="dialog">
				<uni-popup-dialog :type="msgType" cancelText="取消" confirmText="确定" title="确定要删除吗？" content="该操作将删除设备，是否确定！" @confirm="unbindDevice"></uni-popup-dialog>
			</uni-popup>
			<!-- 提示窗示例 -->
			<uni-popup ref="aliasDialog" type="dialog">
				<uni-popup-dialog :type="msgType" mode="input" before-close cancelText="取消" confirmText="确定" title="设置备注" placeholder="请输入备注" @close="$refs.aliasDialog.close()" @confirm="setAlias"></uni-popup-dialog>
			</uni-popup>
			<!-- 提示窗示例 -->
			<uni-popup ref="bindDialog" type="dialog">
				<uni-popup-dialog :type="msgType" mode="input" before-close cancelText="取消" confirmText="确定" title="添加设备" placeholder="请输入设备验证码" @close="$refs.bindDialog.close()" @confirm="bindDevice"></uni-popup-dialog>
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
  
import { Autoplay,Pagination } from '@/uni_modules/zebra-swiper/modules'
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
				API.getAgentBindDevices(({data}) => {
					if(data.code===0){
						this.$refs.paging.complete(data.data);
					}else{
						this.messageToggle("error", data.msg)
						this.$refs.paging.complete(false);
					}
				})
            },
			gotoDevice(mac) {
				uni.navigateTo({
					url: '/pages/device/device?mac='+mac
				});
			},
			gotoChatList(mac, alias) {
				alias = alias || ''
				uni.navigateTo({
					url: '/pages/chat/list?mac='+mac+'&alias='+alias
				});
			},
			showEditAlias(id, alias, type) {
				this.msgType = type;
				this.$refs.aliasDialog.open();
				this.device_id = id;
      			this.oldVal = alias || '';
			},
			setAlias(alias) {
				if(!alias){
					uni.showToast({
		                icon: 'none',
						position: 'bottom',
		                title: '备注不能为空'
					});
					return false;
				}
				
				if(this.oldVal === alias) {
					this.$refs.aliasDialog.close()
					return false;
				}

				API.setAlias(this.device_id, alias, ({ data }) => {
					if (data.code === 0) {
						this.$refs.aliasDialog.close()
						this.messageToggle(undefined, '设置成功')
						this.$refs.paging.refresh()
					} else {
						this.messageToggle("error", data.msg)
					}
				})
			},
			changeOTA(index, bool){
				this.dataList[index].autoUpdate = bool?1:0;
				API.enableOtaUpgrade(this.dataList[index].id, bool?1:0, ({data}) => {
					if(data.code===0){
						this.messageToggle(undefined,bool?'已设置自动升级' : '已关闭自动升级')
					}else{
						this.messageToggle("error", data.msg)
					}
				})
			},
			showBindDevice(type) {
				this.msgType = type;
				this.$refs.bindDialog.open();
			},
			bindDevice(code) {
				if(!code){
					uni.showToast({
		                icon: 'none',
						position: 'bottom',
		                title: '设备验证码不能为空'
					});
					return;
				}
				API.bindDevice(code, ({data}) => {
					this.$refs.bindDialog.close();
					if(data.code===0){
						this.messageToggle('success', '设备绑定成功！')
					}else{
						this.messageToggle('error', data.msg?'绑定设备失败：'+data.msg:'绑定设备失败')
					}
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
	.content-item-inline{
		display: flex;
		align-items: center;
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
