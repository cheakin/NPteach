<template>
	<view>
		<!-- 轮播 -->
		<u-swiper height="180" keyName="image" :list="swipers" bgColor="#ffffff" />

		<!-- 通知 -->
		<u-notice-bar v-if="text1" :text="text1" />

		<!-- 导航区域 -->
		<u-grid :border="false" @click="">
			<u-grid-item v-for="nav in navs">
				<u-icon :customStyle="{paddingTop:20+'rpx'}" :name="nav.icon" :size="30" />
				<text class="grid-text">{{nav.title}}</text>
			</u-grid-item>
		</u-grid>
		<!-- <view class="navs">
			<view class="navs_item" v-for="nav in navs" :key="nav.name">
				<u-icon :name="nav.name" size="35" class="nav-icon" />
				<text>{{nav.title}}</text>
			</view>
		</view> -->

		<u-divider text="最新上架" textSize="35rpx " textColor="#b50e03" />

		<view class="goods">
			<view class="good_item" v-for="good in goods" :key="good.id">
				<image :src="good.img_url" />
				<view class="good_name">{{good.title}}</view>
				<view class="good_desc">
					<u-tag text="酒水" size="mini" color="#909399" borderColor="#fff" plain shape="circle" />
					<view class="good_spec">{{good.spec}}</view>
					
				</view>
				<view class="good_desc">
					<view class="good_price">
						<text>￥{{good.sell_price}}</text>
						<text>￥{{good.market_price}}</text>
					</view>
					<u-tag text="+" />
				</view>
			</view>
		</view>
		
		<u-divider text="到底了...." :hairline="true" />
	</view>
</template>

<script>
	export default {
		data() {
			return {
				swipers: [
					'https://cdn.uviewui.com/uview/swiper/swiper1.png',
					'https://cdn.uviewui.com/uview/swiper/swiper2.png',
					'https://cdn.uviewui.com/uview/swiper/swiper3.png',
				],
				text1: 'uView UI众多组件覆盖开发过程的各个需求，组件功能丰富，多端兼容。让您快速集成，开箱即用',
				navs: [{
						icon: 'grid',
						title: '分类'
					},
					{
						icon: 'shopping-cart',
						title: '购物车'
					},
					{
						icon: 'file-text',
						title: '订单'
					}
				],
				goods: [{
					id: '1',
					img_url: 'https://picsum.photos/id/16/100/100',
					sell_price: 200,
					market_price: 300,
					title: '测试测试测试测试测试测试测试测试测试测试测试测试测试',
					spec: '200ml X 1'
				}, {
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
				}]
			}
		},
		methods: {}
	}
</script>

<style lang="scss">
	.navs {
		display: flex;
		justify-content: space-around;
		margin: 25rpx auto;

		.navs_item {
			// width: 33%;
			text-align: center;

			.nav-icon {
				text-align: center;
				// border: red 1px solid;
			}

			text {
				color: #7c1823;
				font: 20rpx;
			}
		}
	}

	.grid-text {
		color: #7c1823;
	}

	.goods {
		// border: red 1rpx solid;
		padding: 0 15rpx;
		display: flex;
		flex-wrap: wrap;
		justify-content: space-between;

		.good_item {
			background-color: #fff;
			width: 355rpx;
			margin: 3rpx 0;
			padding: 15rpx;
			box-sizing: border-box;

			image {
				width: 100%;
				// height: 250rpx;
				display: block;
				margin: auto;
			}

			.good_name {
				font-size: 36rpx;
				line-height: 50rpx;
				margin-top: 5rpx;
				font-weight: bold;
				white-space: nowrap;
				overflow: hidden;
				text-overflow: ellipsis;
				// padding: 15rpx 0 10rpx 0;
			}

			.good_desc {
				// border: red 1rpx solid;
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-top: -1rpx;
			}

			.good_spec {
				font-size: 26rpx;
				color: $u-info;
			}

			.good_price {
				// border: red 1rpx solid;
				// position: relative;
				// float: right;

				color: $u-error-dark;
				font-size: 45rpx;
				// margin: 20rpx 0 5rpx 0;

				text:nth-child(2) {
					color: #82848a;
					font-size: 18rpx;
					margin-left: 17rpx;
					text-decoration: line-through;
				}
			}
		}
	}
</style>
