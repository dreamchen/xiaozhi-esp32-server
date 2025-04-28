package xiaozhi.modules.timbre.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springframework.web.multipart.MultipartFile;
import xiaozhi.common.page.PageData;
import xiaozhi.common.service.BaseService;
import xiaozhi.modules.timbre.dto.VoiceClonePageDTO;
import xiaozhi.modules.timbre.entity.VoiceCloneEntity;
import xiaozhi.modules.timbre.vo.VoiceCloneVO;

import java.util.List;

public interface VoiceCloneService extends BaseService<VoiceCloneEntity> {
    /**
     * 获取声音复刻记录
     *
     * @param userId 用户ID
     * @param chatId 聊天历史ID
     * @return
     */
    List<VoiceCloneEntity> getVoiceClones(String userId, String chatId);

    /**
     * 分页获取全部声音复刻记录
     *
     * @param dto 分页查找参数
     * @return 声音复刻记录分页数据
     */
    PageData<VoiceCloneVO> page(VoiceClonePageDTO dto);

    /**
     * 声音复刻
     *
     * @param file 文件
     * @return 复刻结果
     */
    String clone(MultipartFile file);


    boolean update(VoiceCloneEntity entity);
}
