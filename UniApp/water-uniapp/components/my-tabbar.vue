<template>
	<u-tabbar value="value" @change="changeTab" @ :fixed="true" :placeholder="true" :safeAreaInsetBottom="true">
		<u-tabbar-item v-for="(item, index) in tabBarList" :text="item.text"
			:icon="value == index ? item.selectedIconPath: item.iconPath" @click="click">
		</u-tabbar-item>
	</u-tabbar>
</template>

<script>
	import {
		mapGetters
	} from 'vuex'
	export default {
		computed: {
			...mapGetters([
				'tabBarList',
			])
		},
		onShow() {
			console.log('onShow');
		},
		created() {
			console.log('created', this.value);
		},
		/* onLaunch() {
			console.log('onLaunch',this.value);
		},
		onShow() {
			console.log('onShow',this.value);
		},
		onLoad() {
			console.log('onLoad',this.value);
		}, */
		data() {
			return {
				value: 0
			}
		},
		methods: {
			changeTab(index) {
				// console.log('changeTab', index);
				// this.value = index
				/* console.log(this.value, this.tabBarList[index].text);
				console.log(this.value === this.tabBarList[index].text);
				this.value = this.tabBarList[index].text
				console.log(this.value, this.tabBarList[index].text);
				console.log(this.value === this.tabBarList[index].text);
				
				uni.redirectTo({
					url: this.tabBarList[index].pagePath
				}) */
			},
			click(index) {
				this.value = index
				console.log(this.tabBarList[index].pagePath);
				uni.navigateTo({
					url: this.tabBarList[index].pagePath,
					success: res => {},
					fail: err => {
						console.log('err', err.errMsg);
					}
				})
				console.log('click, index:', index, ', value:', this.value);

				// this.value = 
			}
		}
	}
</script>

<style>
</style>
