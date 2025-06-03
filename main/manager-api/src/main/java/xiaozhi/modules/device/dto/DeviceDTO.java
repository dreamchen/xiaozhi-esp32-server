package xiaozhi.modules.device.dto;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Schema(description = "设备信息对象")
public class DeviceDTO {

    @Schema(description = "设备ID", example = "b4:3a:45:a8:8f:70")
    private String id;

    @Schema(description = "关联用户ID")
    private Long userId;

    @Schema(description = "MAC地址")
    private String macAddress;

    @Schema(description = "最后连接时间")
    private Date lastConnectedAt;

    @Schema(description = "自动更新开关(0关闭/1开启)")
    private Integer autoUpdate;

    @Schema(description = "设备硬件型号")
    private String board;

    @Schema(description = "设备别名")
    private String alias;

    @Schema(description = "智能体ID")
    private String agentId;

    @Schema(description = "智能体名称")
    private String agentName;

    @Schema(description = "助手名称")
    private String assistantName;

    @Schema(description = "语音合成模型标识")
    private String ttsModelId;

    @Schema(description = "音色标识")
    private String ttsVoiceId;

    @Schema(description = "音色名称")
    private String ttsVoiceName;

    @Schema(description = "音色类型：0：官方、1：复刻")
    private Integer ttsVoiceType;

    @Schema(description = "总结记忆", example = "构建可生长的动态记忆网络，在有限空间内保留关键信息的同时，智能维护信息演变轨迹\n" +
            "根据对话记录，总结user的重要信息，以便在未来的对话中提供更个性化的服务", required = false)
    private String summaryMemory;

    @Schema(description = "固件版本号")
    private String appVersion;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "更新者")
    private Long updater;

    @Schema(description = "更新时间")
    private Date updateDate;

    @Schema(description = "创建者")
    private Long creator;

    @Schema(description = "创建时间")
    private Date createDate;
}