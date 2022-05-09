<template>
	<view>
		this is order page
		<my-tabbar></my-tabbar>
	</view>
</template>

<script>
	export default {
		onLoad(options) {
			console.log(options);
			let curPage = getCurrentPages();
			let route = curPage[curPage.length - 1].route; //获取当前页面的路由
			let params = curPage[curPage.length - 1].options; //获取当前页面参数，如果有则返回参数的对象，没有参数返回空对象{}
			let query = '';
			let keys = Object.keys(params); //获取对象的key 返回对象key的数组
			if (keys.length > 0) {
				query = keys.reduce((pre, cur) => {
					return pre + cur + '=' + params[cur] + '&';
				}, '?').slice(0, -1);
			}
			console.log(route + query)
		}
	}
</script>

<style>

</style>
