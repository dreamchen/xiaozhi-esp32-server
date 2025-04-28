package xiaozhi.modules.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import xiaozhi.modules.chat.entity.ChatHistoryEntity;

/**
 * 查询聊天历史列表的DTO
 */
@Data
@Schema(description = "查询聊天历史列表的DTO")
public class ChatHistoryPageDTO extends ChatHistoryEntity {

    @Schema(description = "页数")
    @Min(value = 0, message = "{page.number}")
    private String page;

    @Schema(description = "显示列数")
    @Min(value = 0, message = "{limit.number}")
    private String limit;
}
