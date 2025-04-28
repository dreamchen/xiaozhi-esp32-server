package xiaozhi.modules.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_chat_history")
@Schema(description = "聊天历史")
public class ChatHistoryEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(description = "对话编号")
    private String id;

    @Schema(description = "用户编号")
    private Long userId;

    @Schema(description = "聊天角色")
    private String agentId;

    @Schema(description = "设备编号")
    private String deviceId;

    @Schema(description = "信息汇总")
    private Integer messageCount;

    @Schema(description = "创建者")
    @TableField(fill = FieldFill.INSERT)
    private Long creator;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "更新者")
    @TableField(fill = FieldFill.UPDATE)
    private Long updater;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;
}
