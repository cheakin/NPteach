<template>
	<view>
		<!-- 轮播 -->
		<u-swiper height="400rpx" keyName="image" :list="swipers" bgColor="#ffffff" />

		<!-- 通知 -->
		<u-notice-bar v-if="text1" :text="text1" />

		<!-- 导航区域 -->
		
		<u-grid :border="false" @click="jump">
			<u-grid-item v-for="nav in tabBarList">
				<u-icon :customStyle="{paddingTop:20+'rpx'}" :name="nav.iconPath" :size="30" />
				<text class="grid-text">{{nav.text}}</text>
			</u-grid-item>
		</u-grid>
		<!-- <view class="navs">
			<view class="navs_item" v-for="nav in navs" :key="nav.name">
				<u-icon :name="nav.name" size="35" class="nav-icon" />
				<text>{{nav.title}}</text>
			</view>
		</view> -->

		<u-divider text="最新上架" textSize="35rpx " textColor="#b50e03" />
		<good-list :goods="goods" />
		
		<u-divider text="到底了" :hairline="true" />
	</view>
</template>

<script>
	import { mapGetters } from 'vuex'
	import goodList from '@/components/good-list.vue'
	export default {
		components: {
			"good-list": goodList
		},
		computed: {
			...mapGetters([
				'tabBarList',
			])
		},
		data() {
			return {
				swipers: [
					'https://cdn.uviewui.com/uview/swiper/swiper1.png',
					'https://cdn.uviewui.com/uview/swiper/swiper2.png',
					'https://cdn.uviewui.com/uview/swiper/swiper3.png',
				],
				text1: 'uView UI众多组件覆盖开发过程的各个需求，组件功能丰富，多端兼容。让您快速集成，开箱即用',
				/* navs: [{
						path: '/pages/good/good',
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
				], */
				goods: [
					{
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
		methods: {
			jump(index) {
				uni.navigateTo({
					url:this.tabBarList[index].pagePath,
					fail: err => {
						console.log(err);
					}
				})
			}
			
		},
		onReachBottom() {
			console.log('触底了');
			let goodList = this.goods;
			this.goods.push(...goodList)
		}
		
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

	
</style>
