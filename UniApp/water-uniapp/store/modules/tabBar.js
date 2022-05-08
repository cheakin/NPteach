// tabBar文件为我们创建的tabBer对象数组
import tabBar from '../../util/tabBar.js'

// 判断用户tabBer类别[0:普通用户,1:职工,2:管理员]
let userType = uni.getStorageSync('user_type')
let type = 'userList'
if (userType === 2) {
	type = 'adminList'
} else if (userType === 1) {
	type = 'staffList'
} else {
	type = 'userList'
}

// midBtn 为设置tabBer中间的凸起，false为不凸起
const state = {
	list: tabBar[type]
}


export default {
	namespaced: true,
	state
}