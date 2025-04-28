package xiaozhi.modules.timbre.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import xiaozhi.modules.timbre.entity.VoiceCloneEntity;

/**
 * 查询声音复刻的VO
 */
@Data
public class VoiceCloneVO extends VoiceCloneEntity {

    @Schema(description = "对应 TTS 模型名称")
    private String ttsModelName;
}
