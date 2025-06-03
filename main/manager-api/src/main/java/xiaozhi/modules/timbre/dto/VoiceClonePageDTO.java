package xiaozhi.modules.timbre.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import xiaozhi.modules.timbre.entity.VoiceCloneEntity;

/**
 * 查询声音复刻的DTO
 */
@Data
@Schema(description = "查询声音复刻的DTO")
public class VoiceClonePageDTO extends VoiceCloneEntity {

    @Schema(description = "页数")
    @Min(value = 0, message = "{page.number}")
    private String page;

    @Schema(description = "显示列数")
    @Min(value = 0, message = "{limit.number}")
    private String limit;
}
