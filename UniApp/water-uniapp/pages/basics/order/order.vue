<template>
	<view>
		<view class="card" v-for="good in goods">
			<!-- <u-skeleton rows="3" :loading="loading" avatar :title="false">
				<u--text>loading为false时，将会展示此处插槽内容</u--text>
			</u-skeleton> -->
			<view class="good">
				<!-- <image :src="good.img_url"  class="good_img"/> -->
				<u--image :src="good.img_url" width="200rpx" height="200rpx" radius="5"></u--image>
				<view class="good_info">
					<view class="good_name">{{good.title}}</view>
					<view class="good_desc">
						<view class="good_spec">规格：{{good.spec}}</view>
						<!-- <view class="good_price">单价：{{good.sell_price}}</view> -->
					</view>
					<view class="good_desc">
						<view class="good_spec">数量：1</view>
						<view class="good_price">合计：{{good.sell_price}}</view>
					</view>
					<view class=""></view>
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
	.card {
		// border: red 1rpx solid;
		margin: 17rpx auto;
		padding: 15rpx 10rpx;
		width: 700rpx;
		border-radius: 0.4em;
		box-shadow: 0.3em 0.3em 0.7em #00000015;
		
		.good{
			display: flex;

			.good_img{
				width: 200rpx;
				height: 200rpx;
			}
			
			.good_info {
				// border: red 1rpx solid;
				padding: 0 10rpx;
				width:490rpx;
				
				.good_name{
					font-weight: bold;
					overflow:hidden;
					text-overflow:ellipsis;
					display:-webkit-box;
					-webkit-box-orient:vertical;
					-webkit-line-clamp:2;
					// color: #82848a;
					
				}
				
				.good_desc {
					display: flex;
					justify-content: space-between;
					margin: 5rpx auto;
					color: #82848a;
					font-size: 25rpx;
				}
			}
		}
	}
</style>
