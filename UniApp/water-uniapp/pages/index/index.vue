<template>
	<view>
		<home v-if="curPage == 'home'" />
		<order v-if="curPage == 'order'" />
		<setting v-if="curPage == 'setting'" />
		
		
		<u-tabbar value="curPage" @change="" @ :fixed="true" :placeholder="true" :safeAreaInsetBottom="true">
			<u-tabbar-item v-for="(item, index) in tabBarList" :text="item.text"
				:icon="curPage == item.key ? item.selectedIconPath: item.iconPath" @click="click">
			</u-tabbar-item>
		</u-tabbar>
	</view>
</template>

<script>
	import {
		mapGetters
	} from 'vuex'
	import home from '../basics/home/home.vue'
	import order from '../basics/order/order.vue'
	import setting from '../basics/setting/setting.vue'
	// import myTabbar from '../../components/my-tabbar.vue'
	export default {
		components: {
			home,order,setting
		},
		computed: {
			...mapGetters([
				'tabBarList',
			])
		},
		data() {
			return {
				curPage: 'home'
			}
		},
		onLoad() {

		},
		methods: {
			click(index) {
				this.curPage = this.tabBarList[index].key
			}
		}
	}
</script>

<style>

</style>
