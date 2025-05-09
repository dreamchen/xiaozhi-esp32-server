<template>
    <div class="copyright">
        ©2025 小优-优享AI智控台 v{{ version }}&nbsp;&nbsp;<a href="https://beian.miit.gov.cn/" style="text-decoration: none;color: inherit;" target="_blank">京ICP备16066791号-1</a>
    </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
    name: 'VersionFooter',
    computed: {
        ...mapState({
            version: state => state.pubConfig.version
        })
    },
    mounted() {
        this.$store.dispatch('fetchPubConfig');
    }
}
</script>

<style scoped></style>