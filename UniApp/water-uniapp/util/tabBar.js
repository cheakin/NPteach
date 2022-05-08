const userList = [
	// 首页
	{
		// 非凸起按钮未激活的图标，可以是uView内置图标名或自定义扩展图标库的图标
		// 或者png图标的【绝对路径】，建议尺寸为80px * 80px
		// 如果是中间凸起的按钮，只能使用图片，且建议为120px * 120px的png图片
		iconPath: "home",
		// 激活(选中)的图标，同上
		selectedIconPath: "home-fill",
		// 显示的提示文字
		text: '首页',
		// 红色角标显示的数字，如果需要移除角标，配置此参数为0即可
		count: 0,
		// 如果配置此值为true，那么角标将会以红点的形式显示
		isDot: true,
		// 如果使用自定义扩展的图标库字体，需配置此值为true
		// 自定义字体图标库教程：https://www.uviewui.com/guide/customIcon.html
		customIcon: false,
		// 如果是凸起按钮项，需配置此值为true
		midButton: false,
		// 点击某一个item时，跳转的路径，此路径必须是pagees.json中tabBar字段中定义的路径
		pagePath: '/pages/index/index', // 1.5.6新增，路径需要以"/"开头
	},
	// 订单
	{
		iconPath: "file-text",
		// 激活(选中)的图标，同上
		selectedIconPath: "file-text-fill",
		// 显示的提示文字
		text: '订单',
		// 红色角标显示的数字，如果需要移除角标，配置此参数为0即可
		count: 0,
		// 如果配置此值为true，那么角标将会以红点的形式显示
		isDot: true,
		// 如果使用自定义扩展的图标库字体，需配置此值为true
		// 自定义字体图标库教程：https://www.uviewui.com/guide/customIcon.html
		customIcon: false,
		// 如果是凸起按钮项，需配置此值为true
		midButton: false,
		// 点击某一个item时，跳转的路径，此路径必须是pagees.json中tabBar字段中定义的路径
		pagePath: '/pages/order/order', // 1.5.6新增，路径需要以"/"开头
	}
]

const staffList = [
	// 首页
	{
		// 未点击图标
		iconPath: "home",
		// 点击后图标
		selectedIconPath: "home-fill",
		// 显示文字
		text: '首页',
		// 是否显示红点
		isDot: true,
		// 是否使用自定义图标
		customIcon: false,
		// 页面路径
		pagePath: '/pages/index/index'
	},
	// 订单
	{
		iconPath: "home",
		// 激活(选中)的图标，同上
		selectedIconPath: "home-fill",
		// 显示的提示文字
		text: '订单',
		// 红色角标显示的数字，如果需要移除角标，配置此参数为0即可
		count: 0,
		// 如果配置此值为true，那么角标将会以红点的形式显示
		isDot: true,
		// 如果使用自定义扩展的图标库字体，需配置此值为true
		// 自定义字体图标库教程：https://www.uviewui.com/guide/customIcon.html
		customIcon: false,
		// 如果是凸起按钮项，需配置此值为true
		midButton: false,
		// 点击某一个item时，跳转的路径，此路径必须是pagees.json中tabBar字段中定义的路径
		pagePath: '/pages/order/order', // 1.5.6新增，路径需要以"/"开头
	}
]

const adminList = [
	
]

export default{
	userList,
	staffList
}
