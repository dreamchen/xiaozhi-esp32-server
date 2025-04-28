package xiaozhi.modules.chat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xiaozhi.common.dao.BaseDao;
import xiaozhi.modules.chat.entity.ChatHistoryEntity;

@Mapper
public interface ChatHistoryDao extends BaseDao<ChatHistoryEntity> {
}
