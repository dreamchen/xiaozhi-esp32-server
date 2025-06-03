<template>
    <view class="wrapper">
       <img src="../../static/logo.png" alt="">
       <view>TEL: <label @click="makePhone('18600806164')">18600806164</label></view>
    </view>
</template>
<script>
export default {
  data() {
    return {
    }
  },
  methods: {
    makePhone(num) {
      uni.makePhoneCall({
        phoneNumber: num
      })
    }
  }
}
</script>

<style scoped>
.wrapper {
    padding: 200rpx 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 40rpx;
}
</style>
