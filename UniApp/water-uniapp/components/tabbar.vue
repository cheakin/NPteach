<template>
	<u-tabbar value="value6" @change="changeTab" :fixed="true" :placeholder="true" :safeAreaInsetBottom="true">
		{{menus}}
		<!-- <view >
		<u-tabbar-item text="{{item.text}}" icon="{{item.icon}}"></u-tabbar-item>
	</view> -->
		<u-tabbar-item v-for="item in menus" :text="item.text" :icon="item.iconPath">
		</u-tabbar-item>
	</u-tabbar>
</template>

<script>
	export default {
		props: ['menus'],
		data() {
			return {
				value6: '123'
			}
		},
		methods: {
			changeTab(index) {
				console.log(index);
				console.log(this.menus)
			}
		}
	}
</script>

<style>
</style>
