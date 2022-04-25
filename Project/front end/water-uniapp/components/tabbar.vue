<template>
	<u-tabbar
		value="value6"
		@change="changeTab"
		:fixed="true"
		:placeholder="true"
		:safeAreaInsetBottom="true"
	 >
	<!-- <view >
		<u-tabbar-item text="{{item.text}}" icon="{{item.icon}}"></u-tabbar-item>
	</view> -->
	<u-tabbar-item v-for="item in menus" :text="item.name" :icon="item.icon">
	</u-tabbar-item>
	</u-tabbar>
</template>

<script>
	export default {
		data() {
			return {
				value6: '123',
				menus: [
					{text:'首页', icon:'home', selectIcon:'home-fill', path:''},
					{text:'历史订单', icon:'home', selectIcon: '', path:''}
				]
			}
		},
		methods:{
			changeTab(index) {
				console.log(index);
				console.log(this.menus)
			}
		}
	}
</script>

<style>
</style>