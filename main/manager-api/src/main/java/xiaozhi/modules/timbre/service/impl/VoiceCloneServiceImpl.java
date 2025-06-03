package xiaozhi.modules.timbre.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xiaozhi.common.constant.Constant;
import xiaozhi.common.page.PageData;
import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.common.user.UserDetail;
import xiaozhi.modules.model.service.ModelConfigService;
import xiaozhi.modules.security.user.SecurityUser;
import xiaozhi.modules.timbre.dao.VoiceCloneDao;
import xiaozhi.modules.timbre.dto.VoiceClonePageDTO;
import xiaozhi.modules.timbre.entity.VoiceCloneEntity;
import xiaozhi.modules.timbre.service.VoiceCloneService;
import xiaozhi.modules.timbre.util.BaseVoiceClone;
import xiaozhi.modules.timbre.vo.VoiceCloneVO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class VoiceCloneServiceImpl extends BaseServiceImpl<VoiceCloneDao, VoiceCloneEntity> implements VoiceCloneService {

    private final VoiceCloneDao voiceCloneDao;
    private final BaseVoiceClone voiceClone;
    private final ModelConfigService modelConfigService;

    @Override
    public List<VoiceCloneEntity> getVoiceClones(String userId, String chatId) {
        return this.voiceCloneDao.selectList(new QueryWrapper<VoiceCloneEntity>().eq("creator", userId).eq("chat_id", chatId).orderByDesc("create_date").eq("is_delete", 0));
    }

    @Override
    public PageData<VoiceCloneVO> page(VoiceClonePageDTO dto) {
        UserDetail user = SecurityUser.getUser();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Constant.PAGE, dto.getPage());
        params.put(Constant.LIMIT, dto.getLimit());
        QueryWrapper<VoiceCloneEntity> qw = new QueryWrapper<VoiceCloneEntity>().eq("creator", user.getId());
        if (StringUtils.isNotBlank(dto.getTtsModelId())) {
            qw.eq("tts_model_id", dto.getTtsModelId());
        }
        if (ObjectUtils.isNotEmpty(dto.getIsDelete())) {
            qw.eq("is_delete", dto.getIsDelete());
        }
        if (StringUtils.isNotBlank(dto.getName())) {
            qw.like("name", dto.getName());
        }

        IPage<VoiceCloneEntity> page = this.voiceCloneDao.selectPage(
                getPage(params, "create_date", false), qw);
        page.getRecords().forEach(item -> {
            item.setVoiceDemo(item.getVoiceDemo());
        });
        PageData<VoiceCloneVO> pageData = getPageData(page, VoiceCloneVO.class);
        pageData.getList().stream().forEach(item -> {
            // 获取 TTS 模型名称
            item.setTtsModelName(modelConfigService.getModelNameById(item.getTtsModelId()));
        });
        return pageData;
    }

    @Override
    public String clone(MultipartFile file) {
        UserDetail userDetail = SecurityUser.getUser();
        Map<String, String> map = voiceClone.clone(file);
        System.out.println(map);
        if (!"0".equals(map.get("code"))) {
            return map.get("msg").toString();
        }
        VoiceCloneEntity entity = new VoiceCloneEntity();
        entity.setTtsModelId(map.get("ttsModelId"));
        entity.setTtsVoice(map.get("data"));
        entity.setName(map.get("name"));
        entity.setVoiceDemo(map.get("demoAudio"));
        entity.setCreator(userDetail.getId());
        entity.setCreateDate(new Date());
        voiceCloneDao.insert(entity);
        return "success";
    }

    @Override
    public boolean update(VoiceCloneEntity entity) {
        int count = this.voiceCloneDao.update(entity, new QueryWrapper<VoiceCloneEntity>().eq("id", entity.getId()));
        return count > 0;
    }

    @Override
    public String getVoiceCloneNameById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        VoiceCloneEntity entity = this.voiceCloneDao.selectById(id);
        if (entity != null) {
            return entity.getName();
        }
        return null;
    }
}
