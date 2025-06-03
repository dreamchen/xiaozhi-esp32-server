package xiaozhi.modules.agent.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import xiaozhi.common.constant.Constant;
import xiaozhi.common.page.PageData;
import xiaozhi.common.utils.ConvertUtils;
import xiaozhi.modules.agent.dao.AiAgentChatHistoryDao;
import xiaozhi.modules.agent.dto.AgentChatHistoryDTO;
import xiaozhi.modules.agent.dto.AgentChatSessionDTO;
import xiaozhi.modules.agent.entity.AgentChatHistoryEntity;
import xiaozhi.modules.agent.service.AgentChatHistoryService;

/**
 * 智能体聊天记录表处理service {@link AgentChatHistoryService} impl
 *
 * @author Goody
 * @version 1.0, 2025/4/30
 * @since 1.0.0
 */
@Service
public class AgentChatHistoryServiceImpl extends ServiceImpl<AiAgentChatHistoryDao, AgentChatHistoryEntity>
        implements AgentChatHistoryService {

    @Override
    public PageData<AgentChatSessionDTO> getSessionListByAgentId(Map<String, Object> params) {
        String agentId = (String) params.get("agentId");
        String macAddress = (String) params.get("macAddress");
        int page = Integer.parseInt(params.get(Constant.PAGE).toString());
        int limit = Integer.parseInt(params.get(Constant.LIMIT).toString());

        // 构建查询条件
        QueryWrapper<AgentChatHistoryEntity> wrapper = new QueryWrapper<>();
        wrapper.select("session_id", "MAX(created_at) as created_at", "COUNT(*) as chat_count");
        if (StringUtils.isNotBlank(agentId)) {
            wrapper.eq("agent_id", agentId);
        }
        if (StringUtils.isNotBlank(macAddress)) {
            wrapper.eq("mac_address", macAddress);
        }
        wrapper.groupBy("session_id");
        wrapper.orderByDesc("created_at");

        // 执行分页查询
        Page<Map<String, Object>> pageParam = new Page<>(page, limit);
        IPage<Map<String, Object>> result = this.baseMapper.selectMapsPage(pageParam, wrapper);

        List<AgentChatSessionDTO> records = result.getRecords().stream().map(map -> {
            AgentChatSessionDTO dto = new AgentChatSessionDTO();
            dto.setSessionId((String) map.get("session_id"));
            dto.setCreatedAt((LocalDateTime) map.get("created_at"));
            dto.setChatCount(((Number) map.get("chat_count")).intValue());
            return dto;
        }).collect(Collectors.toList());

        return new PageData<>(records, result.getTotal());
    }

    @Override
    public List<AgentChatHistoryDTO> getChatHistoryBySessionId(String agentId, String macAddress, String sessionId) {
        // 构建查询条件
        QueryWrapper<AgentChatHistoryEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(agentId)) {
            wrapper.eq("agent_id", agentId);
        }
        if (StringUtils.isNotBlank(macAddress)) {
            wrapper.eq("mac_address", macAddress);
        }
        wrapper.eq("session_id", sessionId);
        wrapper.orderByAsc("created_at");

        // 查询聊天记录
        List<AgentChatHistoryEntity> historyList = list(wrapper);

        // 转换为DTO
        return ConvertUtils.sourceToTarget(historyList, AgentChatHistoryDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByAgentId(String agentId, String macAddress, Boolean deleteAudio, Boolean deleteText) {
        if (deleteAudio) {
            baseMapper.deleteAudioByAgentId(agentId, macAddress);
        }
        if (deleteAudio && !deleteText) {
            baseMapper.deleteAudioIdByAgentId(agentId, macAddress);
        }
        if (deleteText) {
            baseMapper.deleteHistoryByAgentId(agentId, macAddress);
        }

    }
}
