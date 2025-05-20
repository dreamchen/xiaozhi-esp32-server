import ajax from "./ajax.js";


// 获取公共配置
export const getPubConfig = () => {
	return ajax({
	  url: '/user/pub-config',
	})
}

// 登录
export const login = (loginForm, callback) => {
	ajax.post({
		url: '/user/login',
		data: loginForm
	}).then(res => callback(res))
}
// 用户信息获取
export const getUserInfo = (callback) => {
	ajax.get({
			url: '/user/info'
		}).then(res => callback(res))
}

// 获取验证码
export const getCaptcha = (uuid, callback) => {
        ajax.get({
				url: `/user/captcha?uuid=${uuid}`,
				responseType: 'arraybuffer',
				header: {
					'Content-Type': 'image/gif',
					'Pragma': 'No-cache',
					'Cache-Control': 'no-cache'
				},
			}).then(res => callback(res))
}


// 已绑设备
export const getAgentBindDevices = (agentId, callback) => {
	ajax.get({
			url: `/device/bind/${agentId}`
		}).then(res => callback(res))
}

// 解绑设备
export const unbindDevice = (device_id, callback) => {
	ajax.post({
			url: '/device/unbind',
			data: { deviceId: device_id }
		}).then(res => callback(res))
}
// 绑定设备
export const bindDevice = (agentId, deviceCode, callback) => {
	ajax.post({
			url: `/device/bind/${agentId}/${deviceCode}`
		}).then(res => callback(res))
}
//切换OTA升级状态
export const enableOtaUpgrade = (id, status, callback) => {
	ajax.put({
			url: `/device/enableOta/${id}/${status}`
		}).then(res => callback(res))
}
//删除记忆体
export const delMemSummary = (id, callback) => {
	ajax.delete({
			url: `/device/delMemSummary/${id}`
		}).then(res => callback(res))
}
	
// 获取所有聊天历史列表
export const getChatHistoryList = (params, callback) => {
	ajax.get({
			url: '/chat/getChatHistorys',
			data: params,
		}).then(res => callback(res))
}
// 获取所有聊天信息记录
export const getChatMessageList = (params, callback) => {
	ajax.get({
			url: '/chat/getChatMessages',
			data: params
		}).then(res => callback(res))
}
// 聊天历史删除
export const deleteChat = (params, callback) => {
	ajax.post({
			url: '/chat/delete',
			data: params
		}).then(res => callback(res))
}