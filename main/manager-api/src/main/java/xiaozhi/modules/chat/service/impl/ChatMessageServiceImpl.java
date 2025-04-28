package xiaozhi.modules.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import xiaozhi.common.constant.Constant;
import xiaozhi.common.page.PageData;
import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.common.user.UserDetail;
import xiaozhi.modules.chat.dao.ChatMessageDao;
import xiaozhi.modules.chat.dto.ChatMessagePageDTO;
import xiaozhi.modules.chat.entity.ChatMessageEntity;
import xiaozhi.modules.chat.service.ChatMessageService;
import xiaozhi.modules.security.user.SecurityUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ChatMessageServiceImpl extends BaseServiceImpl<ChatMessageDao, ChatMessageEntity> implements ChatMessageService {

    private final ChatMessageDao chatMessageDao;

    @Override
    public List<ChatMessageEntity> getChatMessages(String userId, String chatId) {
        return this.chatMessageDao.selectList(new QueryWrapper<ChatMessageEntity>().eq("user_id", userId).eq("chat_id", chatId).orderByAsc("sort"));
    }

    @Override
    public int deleteByUserId(Long userId) {
        return this.chatMessageDao.delete(new QueryWrapper<ChatMessageEntity>().eq("user_id", userId));
    }

    @Override
    public int deleteByChatId(String chatId) {
        return this.chatMessageDao.delete(new QueryWrapper<ChatMessageEntity>().eq("chat_id", chatId));
    }

    @Override
    public PageData<ChatMessageEntity> page(ChatMessagePageDTO dto) {
        UserDetail user = SecurityUser.getUser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Constant.PAGE, dto.getPage());
        params.put(Constant.LIMIT, dto.getLimit());
        IPage<ChatMessageEntity> page = this.chatMessageDao.selectPage(
                getPage(params, "sort", true), new QueryWrapper<ChatMessageEntity>().eq("user_id", user.getId()));

        return getPageData(page, ChatMessageEntity.class);
    }
}
