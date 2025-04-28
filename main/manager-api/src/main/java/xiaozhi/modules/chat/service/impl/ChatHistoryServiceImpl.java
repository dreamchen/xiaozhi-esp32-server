package xiaozhi.modules.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xiaozhi.common.constant.Constant;
import xiaozhi.common.page.PageData;
import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.common.user.UserDetail;
import xiaozhi.modules.chat.dao.ChatHistoryDao;
import xiaozhi.modules.chat.dto.ChatHistoryPageDTO;
import xiaozhi.modules.chat.entity.ChatHistoryEntity;
import xiaozhi.modules.chat.service.ChatHistoryService;
import xiaozhi.modules.chat.service.ChatMessageService;
import xiaozhi.modules.security.user.SecurityUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ChatHistoryServiceImpl extends BaseServiceImpl<ChatHistoryDao, ChatHistoryEntity> implements ChatHistoryService {

    private final ChatHistoryDao chatHistoryDao;
    private final ChatMessageService chatMessageService;

    @Override
    public List<ChatHistoryEntity> getChatHistorys(String userId, String agentId) {
        return this.chatHistoryDao.selectList(new QueryWrapper<ChatHistoryEntity>().eq("user_id", userId).eq("agent_id", agentId).orderByDesc("create_date"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByChatId(String chatId) {
        chatMessageService.deleteByChatId(chatId);
        chatHistoryDao.deleteById(chatId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByUserId(Long userId) {
        List<String> ids = this.chatHistoryDao.selectList(new QueryWrapper<ChatHistoryEntity>().eq("user_id", userId)).stream().map(ChatHistoryEntity::getId).toList();
        this.chatHistoryDao.delete(new QueryWrapper<ChatHistoryEntity>().eq("user_id", userId));
        this.chatMessageService.deleteBatchIds(ids);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByAgentId(String agentId) {
        List<String> ids = this.chatHistoryDao.selectList(new QueryWrapper<ChatHistoryEntity>().eq("agent_id", agentId)).stream().map(ChatHistoryEntity::getId).toList();
        this.chatHistoryDao.delete(new QueryWrapper<ChatHistoryEntity>().eq("agent_id", agentId));
        this.chatMessageService.deleteBatchIds(ids);
        return true;
    }

    @Override
    public PageData<ChatHistoryEntity> page(ChatHistoryPageDTO dto) {
        UserDetail user = SecurityUser.getUser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Constant.PAGE, dto.getPage());
        params.put(Constant.LIMIT, dto.getLimit());
        IPage<ChatHistoryEntity> page = this.chatHistoryDao.selectPage(
                getPage(params, "create_date", false), new QueryWrapper<ChatHistoryEntity>().eq("user_id", user.getId()));

        return getPageData(page, ChatHistoryEntity.class);
    }
}
