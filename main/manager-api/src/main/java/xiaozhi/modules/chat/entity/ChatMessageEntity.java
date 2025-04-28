package xiaozhi.modules.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_chat_message")
@Schema(description = "聊天信息记录")
public class ChatMessageEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(description = "对话记录唯一标识")
    private String id;

    @Schema(description = "用户唯一标识")
    private Long userId;

    @Schema(description = "对话历史ID")
    private String chatId;

    @Schema(description = "角色（用户/助理)")
    private String role;

    @Schema(description = "对话内容")
    private String content;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "提示令牌数")
    private Integer promptTokens;

    @Schema(description = "完成令牌数")
    private Integer completionTokens;

    @Schema(description = "总令牌数")
    private Integer totalTokens;

    @Schema(description = "提示耗时（毫秒）")
    private Integer promptMs;

    @Schema(description = "完成耗时（毫秒")
    private Integer completionMs;

    @Schema(description = "总耗时（毫秒")
    private Integer totalMs;

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
