import ajax from "./ajax.js";


export const getBaseUrl = () => {
	return ajax.config.baseURL
}

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
// 发送短信验证码
export const sendSmsVerification = (data, callback, failCallback) => {
        ajax.post({
				url: '/user/smsVerification',
            	data: data
			}).then(res => callback(res))
}
// 注册账号
export const register = (registerForm, callback, failCallback) => {
	ajax.post({
				url: '/user/register',
				data: registerForm
			}).then(res => callback(res))
}
// 获取智能体模板
export const getAgentsTemplate = (callback) => {
	ajax.get({
			url: `/agent/templates`
		}).then(res => callback(res))
}
// 已绑设备
export const getAgentBindDevices = (callback) => {
	ajax.get({
			url: `/device/bind`
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
export const bindDevice = (deviceCode, callback) => {
	ajax.post({
			url: `/device/bind/${deviceCode}`
		}).then(res => callback(res))
}
// 已绑设备
export const getDeviceByMac = (macAddress, callback) => {
	ajax.get({
			url: `/device/${macAddress}`
		}).then(res => callback(res))
}
// 获取设备信息
export const setAlias = (id, alias, callback) => {
	ajax.put({
			url: `/device/setAlias/${id}?alias=${alias}`
		}).then(res => callback(res))
}
// 配置智能体
export const updateDeviceConfig = (deviceId, configData, callback) => {
	ajax.put({
			url: `/agent/updateMobile/${deviceId}`,
			data: configData,
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

// 获取设备会话列表
export const getAgentSessions = (macAddress, params, callback) => {
        ajax.get({
			url: `/device/${macAddress}/sessions`,
            data: params
		}).then(res => callback(res))
}
// 获取设备聊天记录
export const getAgentChatHistory = (macAddress, sessionId, callback) => {
        ajax.get({
			url: `/device/${macAddress}/chat-history/${sessionId}`
		}).then(res => callback(res))
}
// 获取音频下载ID
export const getAudioId = (audioId, callback) => {
        ajax.post({
			url: `/agent/audio/${audioId}`
		}).then(res => callback(res))
}

// 获取模型音色列表
export const getModelVoices = (modelId, callback) => {
    ajax.get({
			url: `/models/${modelId}/voices`
		}).then(res => callback(res))
}
// 分页查询音色信息
export const getCloneList = (params, callback) => {
        ajax.get({
			url: '/voiceClone',
            data: params,
		}).then(res => callback(res))
}
// 克隆音色
export const cloneVoice = (formData, callback) => {
        ajax.post({
			url: '/voiceClone/clone',
            data: formData,
		}).then(res => callback(res))
}
// 更新克隆音色
 export const updateCloneVoice = (formData, callback) => {
       ajax.post({
			url: '/voiceClone/update',
            data: formData,
		}).then(res => callback(res))
}
// 克隆音色删除
export const deleteCloneVoice = (ids, callback) => {
        ajax.post({
			url: '/voiceClone/delete',
            data: ids,
		}).then(res => callback(res))
}