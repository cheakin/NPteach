<template>
	<view style="background-color: #f4f4f5;">
		<view class="head">
			<u-search placeholder="搜索历史订单" inputAlign="center" v-model="keyword" :showAction="false" />
			<u-icon name="shopping-cart" color="#2979ff" size="30" style="padding: 0 8 rpx;"></u-icon>
		</view>
		
		<u-sticky offsetTop="0" customNavHeight="0" bgColor="#fff" style="overflow-x: hidden;">
			<u-tabs :list="list1" lineWidth="30" lineColor="#f56c6c"
			:activeStyle="{color: '#303133',fontWeight: 'bold',transform: 'scale(1.05)'}" 
			:inactiveStyle="{color: '#606266',transform: 'scale(1)'}" 
			itemStyle="padding-right: 15px; height: 34px;" style="overflow-y: hidden;"/>
		</u-sticky>
		
		<view >
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
				keyword: '',
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
						title: '测试',
						spec: '200ml X 6'
					}, {
						id: '3',
						img_url: 'https://picsum.photos/id/16/200/200',
						sell_price: 200,
						market_price: 300,
						title: '测试',
						spec: '500ml X 1'
					}, {
						id: '4',
						img_url: 'https://picsum.photos/id/16/200/200',
						sell_price: 200,
						market_price: 300,
						title: '测试',
						spec: '100ml X 1'
					}, {
						id: '5',
						img_url: 'https://picsum.photos/id/16/200/200',
						sell_price: 200,
						market_price: 300,
						title: '测试',
						spec: '100ml X 1'
					}, {
						id: '6',
						img_url: 'https://picsum.photos/id/16/200/200',
						sell_price: 200,
						market_price: 300,
						title: '测试',
						spec: '100ml X 1'
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
		},
		onReachBottom() {
			console.log('触底了');
			let goodList = this.goods;
			this.goods.push(...goodList)
		}
	}
</script>

<style>
.head {
	display: flex;
}
</style>
