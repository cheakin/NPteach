<template>
	<view style="background-color: #ccc;">
		<u-search placeholder="搜索历史订单" inputAlign="center" v-model="keyword" :showAction="false" />
		<view class="order">
			<!-- <u-skeleton rows="3" :loading="loading" avatar :title="false">
				<u--text>loading为false时，将会展示此处插槽内容</u--text>
			</u-skeleton> -->
			<view class="order_desc">
				<view class="order_time">下单时间：2022-05-09 23:59:59</view>
				<view class="order_price">￥1234</view>
			</view>
			<view class="good" v-for="good in goods">
				<!-- <image :src="good.img_url"  class="good_img"/> -->
				<u--image :src="good.img_url" width="200rpx" height="200rpx" radius="5"></u--image>
				<view class="good_info">
					<view class="good_name">{{good.title}}</view>
					<view class="good_desc">
						<view class="good_spec">规格：{{good.spec}}</view>
						<view class="good_spec">数量：1</view>
					</view>
					<view class="good_price">合计：{{good.sell_price}}</view>
					<!-- <view class="good_time">下单时间：2022-05-09 23:59:59</view> -->
				</view>
			</view>
		</view>
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				loading: true,
				keyword: '',
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
		methods: {

		}
	}
</script>

<style lang="scss">
	.order {
		// border: red 1rpx solid;
		
		padding: 3rpx 5rpx;
		width: 720rpx;
		margin: 20rpx auto;
		// padding: 15rpx 0 10rpx;
		background-color: #fff;
		bgood-radius: 0.4em;
		box-shadow: 0.3em 0.3em 0.7em #00000015;
		
		.order_desc{
			border-bottom: 1rpx #c8c9cc solid;
			padding: 12rpx 7rpx;
			display: flex;
			justify-content: space-between;
			// margin: 15rpx 10rpx 10rpx;
			// border-bottom: #dcdfe6;
			color: #909399;
			.order_time {
				text-align: center;
				font-size: 26rpx;
			}
			.order_price{
				font-size: 35rpx;
				font-weight: bold;
				color: #dd6161;
			}
		}
		
		.good{
			// border: red 1rpx solid;
			display: flex;
			margin: 15rpx 15rpx;
			// padding: 15rpx 10rpx;
			width: 700rpx;
			// bgood-radius: 0.4em;
			// box-shadow: 0.3em 0.3em 0.7em #00000015;

			.good_img{
				width: 200rpx;
				height: 200rpx;
			}
			
			.good_info {
				position: relative;
				// bgood: red 1rpx solid;
				padding: 0 10rpx;
				width:490rpx;
				
				.good_name{
					font-weight: bold;
					font-size: 28rpx;
					color: #303133;
					overflow:hidden;
					text-overflow:ellipsis;
					display:-webkit-box;
					-webkit-box-orient:vertical;
					-webkit-line-clamp:2;
					// color: #82848a;
					
				}
				
				.good_desc {
					position: relative;
					margin: 5rpx auto;
					color: #606266;
					font-size: 25rpx;
				}
				
				.good_spec {
				}
				
				.good_price {
					color: #606266;
					font-size: 25rpx;
					position: absolute;
					bottom: 17rpx;
					right: 0;
					// top: 15rpx;
				}
				
				// .good_time {
				// 	position: absolute;
				// 	bottom: 0;
				// 	padding: 8rpx 0;
				// 	font-size: 25rpx;
				// 	color: #909399;
				// }
			}
		}
	}
</style>
