<script>
	import * as API from '@/utils/api'
	
	export default {
		globalData: {
			appInfo: {
				allowUserRegister: false,
				version: '0.0.1'
			},
			userInfo:{},
			config: {},
			$i18n: {},
			$t: {}
		},
		
		onLaunch: function() {
			console.log('App Launch')
			this.isLogin();
		},
		onShow: function() {
			console.log('App Show')
			API.getPubConfig().then(res=>{
				if(res.statusCode===200){
					if(res.data.code===0){
						this.globalData.appInfo = res.data.data
					}
				}
			});
		},
		onHide: function() {
			console.log('App Hide')
		},
		
		methods: {
			isLogin: function(){
				//判断缓存中是否登录过，直接登录
				try {
					let value = uni.getStorageSync('userInfo');
					if (value) {
						//有登录信息
						console.log("已登录用户：",value);
						// _this.$store.dispatch("setUserData",value); //存入状态
						this.globalData.userInfo = value
					}
				} catch (e) {
					// error
				}
			},
		}
	}
</script>

<style lang="scss">
	@use '@/uni_modules/zebra-swiper/index.scss';
	@use "@/uni_modules/zebra-swiper/modules/pagination/pagination.scss";
</style>
