<template>
	<view>
		<u-grid :border="true" @click="click">
			<u-grid-item v-for="(baseListItem,baseListIndex) in baseList" :key="baseListIndex">
				<u-icon :customStyle="{paddingTop:25+'rpx'}" :name="baseListItem.name" :size="30"></u-icon>
				<text class="grid-text">{{baseListItem.title}}</text>
			</u-grid-item>
		</u-grid>
		<u-toast ref="uToast" />
	</view>
</template>

<script>
	export default {
		data() {
			return {
				baseList: [{
						name: 'account',
						title: '用户管理'
					},
					{
						name: 'account-fill',
						title: '员工管理'
					},
					{
						name: 'file-text',
						title: '所有订单'
					},
					{
						name: 'grid',
						title: '分类管理'
					},
					{
						name: 'bag',
						title: '商品管理'
					}
				]
			}
		},
		methods: {

		}
	}
</script>

<style>

</style>
