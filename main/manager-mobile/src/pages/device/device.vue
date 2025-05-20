<template>
	<view class="wrapper">
		<view class="head">MAC:aabbccdd</view>
		<view class="content">
			<view class="content-title">设备别名：</view>
			<view class="content-item"><input class="uni-input" maxlength="10" placeholder="最大输入长度为10" /></view>
		</view>
		<view class="content">
			<view class="content-title">助手昵称：</view>
			<view class="content-item"><input class="uni-input" maxlength="10" placeholder="最大输入长度为10" /></view>	
		</view>
		<view class="content">
			<view class="content-title">角色模板：</view>
			<view class="content-item">
				<picker :value="index" :range="array">
						<view class="uni-input">{{array[index]}}</view>
				</picker>
			</view>
		</view>
		<view class="content">
			<view class="content-title">角色音色：</view>
			<view class="content-item">
				<picker :value="index" :range="array">
						<view class="uni-input">{{array[index]}}</view>
				</picker>
			</view>
		</view>
		<view class="content">
			<view class="content-title">记&nbsp;&nbsp;忆&nbsp;&nbsp;体：</view>
			<view class="content-item">
				<view class="content-item-tips">
					<label>当前记忆（每次对话后重新生成）</label><i class="far fa-trash-alt"></i>
				</view>
				<textarea />
			</view>	
		</view>
		<view class="foot"><button type="default">取消</button><button type="primary">保存</button></view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				index: 0,
				array: ['中国', '美国', '巴西', '日本'],
			}
		},
		methods: {
			
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
	.content-item-tips{
		font-size: 28rpx;
		color: #808080;
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
