package xiaozhi.modules.timbre.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 声音复刻实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_voice_clone")
@Schema(description = "声音复刻信息")
public class VoiceCloneEntity {

    @Schema(description = "id")
    private String id;

    @Schema(description = "声音名称")
    private String name;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否删除")
    private Integer isDelete;

    @Schema(description = "对应 TTS 模型主键")
    private String ttsModelId;

    @Schema(description = "声音编码")
    private String ttsVoice;

    @Schema(description = "音频播放地址")
    private String voiceDemo;

    @Schema(description = "更新者")
    @TableField(fill = FieldFill.UPDATE)
    private Long updater;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateDate;

    @Schema(description = "创建者")
    @TableField(fill = FieldFill.INSERT)
    private Long creator;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

}