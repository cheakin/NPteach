<template>
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
				<u-tag text="+" size="mini" />
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name:"good-list",
		props:[
			'goods'
		],
		data() {
			return {
				
			};
		}
	}
</script>

<style lang="scss">
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
				width: 335rpx;
				height: 300rpx;
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
				// margin: 20rpx 0 5rpx 0;
				color: $u-error-dark;
				font-size: 45rpx;
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