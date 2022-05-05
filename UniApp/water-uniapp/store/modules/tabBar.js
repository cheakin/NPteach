// import tabBer from '../../util/tabBer'
import tabBar from '../../util/tabBar.js'


// tabBar文件为我们创建的tabBer对象数组

// 判断用户tabBer类别
// 0 冻结
// 1 普通用户
// 2 教师
// 3 管理员

// 逻辑判断处理
let type = uni.getStorageSync('user_type')  >= 2 ? 'tchList' : 'stuList'

// midBtn 为设置tabBer中间的凸起，false为不凸起
const state = {
	list: tabBar[type],
	midBtn: type === 'stuList' ? false : true
}


export default {
	namespaced: true,
	state
}