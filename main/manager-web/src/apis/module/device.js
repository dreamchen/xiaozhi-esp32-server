import { getServiceUrl } from '../api';
import RequestService from '../httpRequest';

export default {
    // 已绑设备
    getAgentBindDevices(agentId, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/device/bind/${agentId}`)
            .method('GET')
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .networkFail((err) => {
                console.error('获取设备列表失败:', err);
                RequestService.reAjaxFun(() => {
                    this.getAgentBindDevices(agentId, callback);
                });
            }).send();
    },
    // 解绑设备
    unbindDevice(device_id, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/device/unbind`)
            .method('POST')
            .data({ deviceId: device_id })
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .networkFail((err) => {
                console.error('解绑设备失败:', err);
                RequestService.reAjaxFun(() => {
                    this.unbindDevice(device_id, callback);
                });
            }).send();
    },
    // 绑定设备
    bindDevice(agentId, deviceCode, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/device/bind/${agentId}/${deviceCode}`)
            .method('POST')
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .networkFail((err) => {
                console.error('绑定设备失败:', err);
                RequestService.reAjaxFun(() => {
                    this.bindDevice(agentId, deviceCode, callback);
                });
            }).send();
    },
    // 设置备注
    setAlias(id, alias, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/device/setAlias/${id}?alias=${alias}`)
            .method('PUT')
            .success((res) => {
                RequestService.clearRequestTime()
                callback(res)
            })
            .networkFail((err) => {
                console.error('设置备注失败:', err)
                this.$message.error(err.msg || '设置备注失败')
                RequestService.reAjaxFun(() => {
                    this.setAlias(id, alias, callback)
                })
            }).send()
    },
    enableOtaUpgrade(id, status, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/device/enableOta/${id}/${status}`)
            .method('PUT')
            .success((res) => {
                RequestService.clearRequestTime()
                callback(res)
            })
            .networkFail((err) => {
                console.error('更新OTA状态失败:', err)
                this.$message.error(err.msg || '更新OTA状态失败')
                RequestService.reAjaxFun(() => {
                    this.enableOtaUpgrade(id, status, callback)
                })
            }).send()
    },
    // 手动添加设备
    manualAddDevice(params, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/device/manual-add`)
            .method('POST')
            .data(params)
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .networkFail((err) => {
                console.error('手动添加设备失败:', err);
                RequestService.reAjaxFun(() => {
                    this.manualAddDevice(params, callback);
                });
            }).send();
    },
    delMemSummary(id, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/device/delMemSummary/${id}`)
            .method('DELETE')
            .success((res) => {
                RequestService.clearRequestTime()
                callback(res)
            })
            .fail((err) => {
                console.error('删除记忆体失败:', err)
                this.$message.error(err.msg || '删除记忆体失败')
            }).send()
    },
}