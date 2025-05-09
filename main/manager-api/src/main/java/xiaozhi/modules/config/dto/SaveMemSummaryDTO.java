package xiaozhi.modules.config.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "保存记忆总结DTO")
public class SaveMemSummaryDTO {
    @NotBlank(message = "密钥不能为空")
    @Schema(description = "密钥")
    private String secret;

    @NotBlank(message = "设备MAC地址不能为空")
    @Schema(description = "设备MAC地址")
    private String macAddress;

    @NotBlank(message = "客户端ID不能为空")
    @Schema(description = "客户端ID")
    private String clientId;

    @NotBlank(message = "记忆总结内容不能为空")
    @Schema(description = "记忆总结内容")
    private String memSummary;
}