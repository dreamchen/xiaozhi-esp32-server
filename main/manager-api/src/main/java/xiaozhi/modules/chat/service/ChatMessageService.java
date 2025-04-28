package xiaozhi.modules.chat.service;

import xiaozhi.common.page.PageData;
import xiaozhi.common.service.BaseService;
import xiaozhi.modules.chat.dto.ChatMessagePageDTO;
import xiaozhi.modules.chat.entity.ChatMessageEntity;

import java.util.List;

public interface ChatMessageService extends BaseService<ChatMessageEntity> {
    /**
     * 获取聊天信息记录
     *
     * @param userId 用户ID
     * @param chatId 聊天历史ID
     * @return
     */
    List<ChatMessageEntity> getChatMessages(String userId, String chatId);

    /**
     * 删除此用户的所有聊天信息记录
     *
     * @param userId 用户id
     * @return
     */
    int deleteByUserId(Long userId);

    /**
     * 删除指定聊天历史关联的所有聊天信息记录
     *
     * @param chatId 聊天历史ID
     * @return
     */
    int deleteByChatId(String chatId);

    /**
     * 分页获取全部聊天信息记录
     *
     * @param dto 分页查找参数
     * @return 聊天信息记录分页数据
     */
    PageData<ChatMessageEntity> page(ChatMessagePageDTO dto);
}
