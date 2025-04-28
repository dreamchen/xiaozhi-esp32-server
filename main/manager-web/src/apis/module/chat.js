import { getServiceUrl } from '../api';
import RequestService from '../httpRequest';

export default {
    // 获取所有聊天历史列表
    getChatHistoryList(params, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/chat/getChatHistorys`)
            .method('GET')
            .data(params)
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail((err) => {
                console.error('获取所有聊天历史列表失败:', err);
            }).send();
    },
    // 获取所有聊天信息记录
    getChatMessageList(params, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/chat/getChatMessages`)
            .method('GET')
            .data(params)
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail((err) => {
                console.error('获取所有聊天信息记录失败:', err);
            }).send();
    },
    // 聊天历史删除
    deleteChat(params, callback) {
        RequestService.sendRequest()
            .url(`${getServiceUrl()}/chat/delete`)
            .method('POST')
            .data(params)
            .success((res) => {
                RequestService.clearRequestTime();
                callback(res);
            })
            .fail((err) => {
                console.error('聊天历史删除失败:', err);
            }).send();
    },
}