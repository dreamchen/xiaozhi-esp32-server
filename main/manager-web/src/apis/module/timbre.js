import {getServiceUrl} from '../api';
import RequestService from '../httpRequest';

export default {
    // 获取音色
    getVoiceList(params, callback) {
        const queryParams = new URLSearchParams({
            ttsModelId: params.ttsModelId,
            page: params.page || 1,
            limit: params.limit || 10,
            name: params.name || ''
        }).toString();

        RequestService.sendRequest()
            .url(`${getServiceUrl()}/ttsVoice?${queryParams}`)
            .method('GET')
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res.data || []);
            })
            .fail((err) => {
                console.error('获取音色列表失败:', err);
                RequestService.reAjaxFun(() => {
                    this.getVoiceList(params, callback);
                });
            }).send();
    },
    // 音色保存
    saveVoice(params, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/ttsVoice`)
            .method('POST')
            .data(JSON.stringify({
                languages: params.languageType,
                name: params.voiceName,
                remark: params.remark,
                sort: params.sort,
                ttsModelId: params.ttsModelId,
                ttsVoice: params.voiceCode,
                voiceDemo: params.voiceDemo || ''
            }))
            .success((res) => {
                callback(res.data);
            })
            .fail((err) => {
                console.error('保存音色失败:', err);
                RequestService.reAjaxFun(() => {
                    this.saveVoice(params, callback);
                });
            }).send();
    },
    // 音色删除
    deleteVoice(ids, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/ttsVoice/delete`)
            .method('POST')
            .data(ids)
            .success((res) => {
                RequestService.clearRequestTime()
                callback(res);
            })
            .fail((err) => {
                console.error('删除音色失败:', err);
                RequestService.reAjaxFun(() => {
                    this.deleteVoice(ids, callback);
                });
            }).send();
    },
    // 音色修改
    updateVoice(params, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/ttsVoice/${params.id}`)
            .method('PUT')
            .data(JSON.stringify({
                languages: params.languageType,
                name: params.voiceName,
                remark: params.remark,
                ttsModelId: params.ttsModelId,
                ttsVoice: params.voiceCode,
                voiceDemo: params.voiceDemo || ''
            }))
            .success((res) => {
                callback(res.data);
            })
            .fail((err) => {
                console.error('修改音色失败:', err);
                RequestService.reAjaxFun(() => {
                    this.updateVoice(params, callback);
                });
            }).send();
    },

    // 分页查询OTA固件信息
    getCloneList(params, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/voiceClone`)
            .method('GET')
            .data(params)
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail((err) => {
                console.error('获取声音列表失败:', err);
            }).send();
    },
    // 克隆音色
    cloneVoice(formData, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/voiceClone/clone`)
            .method('POST')
            .data(formData)
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail((err) => {
                console.error('声音复刻失败:', err);
            }).send();
    },
    // 更新克隆音色
    updateCloneVoice(formData, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/voiceClone/update`)
            .method('POST')
            .data(formData)
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail((err) => {
                console.error('克隆声音更新失败:', err);
            }).send();
    },
    // 克隆音色删除
    deleteCloneVoice(ids, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/voiceClone/delete`)
            .method('POST')
            .data(ids)
            .success((res) => {
                RequestService.clearRequestTime()
                callback(res);
            })
            .fail((err) => {
                console.error('删除克隆音色失败:', err);
            }).send();
    },
}