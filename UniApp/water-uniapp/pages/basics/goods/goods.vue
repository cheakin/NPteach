<template>
	<view>
		<u-sticky bgColor="#fff">
			<u-tabs :list="list1" lineWidth="30" lineColor="#f56c6c" 
			:activeStyle="{
				color: '#303133',
				fontWeight: 'bold',
				transform: 'scale(1.05)'
			}" 
			:inactiveStyle="{
				color: '#606266',
				transform: 'scale(1)'
			}" 
			itemStyle="padding-left: 15px; padding-right: 15px; height: 34px;" />
		</u-sticky>
		<view style="margin-top: 200;">
			<good-list :goods="goods" />
		</view>
		
	</view>
</template>

<script>
	import goodList from '@/components/good-list.vue'
	export default {
		components: {
			"good-list": goodList
		},
		data() {
			return {
				pageParam: {
					pageIndex: 1,
					pageSize: 10
				},
				list1: [{
					name: '关注',
				}, {
					name: '推荐',
				}, {
					name: '电影'
				}, {
					name: '科技'
				}, {
					name: '音乐'
				}, {
					name: '美食'
				}, {
					name: '文化'
				}, {
					name: '财经'
				}, {
					name: '手工'
				}],
				goods: [{
						id: '1',
						img_url: 'https://picsum.photos/id/16/100/100',
						sell_price: 200,
						market_price: 300,
						title: '测试测试测试测试测试测试测试测试测试测试测试测试测试',
						spec: '200ml X 1'
					},
					{
						id: '2',
						img_url: 'https://picsum.photos/id/16/200/200',
						sell_price: 200,
						market_price: 300,
						title: '测试'
					}, {
						id: '3',
						img_url: 'https://picsum.photos/id/16/200/200',
						sell_price: 200,
						market_price: 300,
						title: '测试'
					}, {
						id: '4',
						img_url: 'https://picsum.photos/id/16/200/200',
						sell_price: 200,
						market_price: 300,
						title: '测试'
					}
				]
			}
		},
		onLoad() {

		},
		methods: {
			click(item) {
				console.log('item', item);
			}
		}
	}
</script>

<style>

</style>
