<template>
	<view class="content">
		<u-checkbox-group 
		    v-model="checkboxValue1"
		    iconPlacement="right" 
		    placement="column">
			<u-checkbox activeColor="red" label="红123色">1234</u-checkbox>
			<u-checkbox activeColor="green" label="绿色"></u-checkbox>
		</u-checkbox-group>
		
		<view class="balance"><u-button type="info" text="结算"></u-button></view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				checkboxValue1: [],
				// 基本案列数据
				checkboxList1: [{
						name: '苹果',
						disabled: false
					},
					{
						name: '香蕉',
						disabled: false
					},
					{
						name: '橙子',
						disabled: true
					}
				],
			}
		},
		methods: {
			checkboxChange(n) {
				console.log('change', n);
			}
		}
	}
</script>

<style>
	.content{
		/* border: red 1rpx solid; */
		height: 100vh;
	}
	.balance {
		position: absolute;
		width: 100%;
		bottom: 0;
	}

</style>
