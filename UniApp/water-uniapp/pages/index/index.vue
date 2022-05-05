<template>
	<view>
		{{value6}}
		<button>测试按钮</button>
		{{tabBarList}}
		<!-- <tabbar :menus="tabBarList"></tabbar> -->
		
	</view>
</template>

<script>
	import {
		mapGetters
	} from 'vuex'
	import tabbar from '../../components/tabbar.vue'
	export default {
		components: {
			tabbar
		},
		data() {
			return {
				title: 'Hello',
				value6: '123'
			}
		},
		computed: {
			...mapGetters([
				'tabBarList',
				'midBtn'
			])
		},
		onLoad() {

		},
		methods: {

		}
	}
</script>

<style>

</style>
