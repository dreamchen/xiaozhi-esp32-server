package xiaozhi.modules.chat.service;

import xiaozhi.common.page.PageData;
import xiaozhi.common.service.BaseService;
import xiaozhi.modules.chat.dto.ChatHistoryPageDTO;
import xiaozhi.modules.chat.entity.ChatHistoryEntity;

import java.util.List;

public interface ChatHistoryService extends BaseService<ChatHistoryEntity> {

    /**
     * 获取聊天历史列表
     *
     * @param userId
     * @param agentId
     * @return
     */
    List<ChatHistoryEntity> getChatHistorys(String userId, String agentId);

    /**
     * 删除对应ID聊天历史列表
     *
     * @param chatId 聊天历史ID
     * @return true/false
     */
    boolean deleteByChatId(String chatId);

    /**
     * 删除此用户的所有聊天历史列表
     *
     * @param userId 用户id
     * @return true/false
     */
    boolean deleteByUserId(Long userId);

    /**
     * 删除指定智能体关联的所有聊天历史列表
     *
     * @param agentId 智能体id
     * @return true/false
     */
    boolean deleteByAgentId(String agentId);

    /**
     * 分页获取全部聊天历史列表
     *
     * @param dto 分页查找参数
     * @return 聊天历史列表分页数据
     */
    PageData<ChatHistoryEntity> page(ChatHistoryPageDTO dto);
}
