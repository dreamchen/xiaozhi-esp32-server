package xiaozhi.modules.config.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "保存智能体聊天DTO")
public class SaveMemoryDTO {
    @NotBlank(message = "密钥不能为空")
    @Schema(description = "密钥")
    private String secret;

    @NotBlank(message = "设备MAC地址不能为空")
    @Schema(description = "设备MAC地址")
    private String macAddress;

    @Schema(description = "智能体ID")
    private String agentId;

    @NotBlank(message = "客户端ID不能为空")
    @Schema(description = "客户端ID")
    private String clientId;

    @NotNull(message = "聊天记录不能为空")
    @Schema(description = "聊天记录")
    private List<MemoryDTO> memoryData;

    @Data
    @Schema(description = "聊天记录DTO")
    public static class MemoryDTO {
        @Schema(description = "角色")
        private String role;
        @Schema(description = "内容")
        private String content;
        @Schema(description = "时间戳")
        private Date timestamp;
    }
}