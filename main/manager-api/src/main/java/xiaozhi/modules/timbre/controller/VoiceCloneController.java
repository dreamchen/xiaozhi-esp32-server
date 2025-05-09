package xiaozhi.modules.timbre.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xiaozhi.common.constant.Constant;
import xiaozhi.common.page.PageData;
import xiaozhi.common.user.UserDetail;
import xiaozhi.common.utils.Result;
import xiaozhi.common.validator.ValidatorUtils;
import xiaozhi.modules.security.user.SecurityUser;
import xiaozhi.modules.timbre.dto.VoiceClonePageDTO;
import xiaozhi.modules.timbre.entity.VoiceCloneEntity;
import xiaozhi.modules.timbre.service.VoiceCloneService;
import xiaozhi.modules.timbre.vo.VoiceCloneVO;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 声音复刻控制层
 */
@AllArgsConstructor
@RestController
@RequestMapping("/voiceClone")
@Tag(name = "音色管理")
public class VoiceCloneController {
    private final VoiceCloneService voiceCloneService;

    @GetMapping
    @Operation(summary = "分页查找")
    @RequiresPermissions("sys:role:normal")
    @Parameters({
            @Parameter(name = "ttsModelId", description = "对应 TTS 模型主键", required = false),
            @Parameter(name = "name", description = "音色名称"),
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
    })
    public Result<PageData<VoiceCloneVO>> page(
            @Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        VoiceClonePageDTO dto = new VoiceClonePageDTO();
        dto.setTtsModelId((String) params.get("ttsModelId"));
        dto.setName((String) params.get("name"));
        dto.setIsDelete(0);
        dto.setLimit((String) params.get(Constant.LIMIT));
        dto.setPage((String) params.get(Constant.PAGE));

        ValidatorUtils.validateEntity(dto);
        PageData<VoiceCloneVO> page = voiceCloneService.page(dto);
        return new Result<PageData<VoiceCloneVO>>().ok(page);
    }

    @PostMapping("/update")
    @Operation(summary = "音色修改")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> update(@RequestBody VoiceCloneEntity dto) {
        if (StringUtils.isBlank(dto.getId()) || StringUtils.isBlank(dto.getName())) {
            return new Result<Void>().error("参数不能为空");
        }
        boolean bool = voiceCloneService.update(dto);
        if (!bool) {
            return new Result<Void>().error("修改失败");
        }
        return new Result<>();
    }

    @PostMapping("/delete")
    @Operation(summary = "音色删除")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> delete(@RequestBody String[] ids) {
        if (ObjectUtils.isEmpty(ids)) {
            return new Result<Void>().error("参数不能为空");
        }
        UserDetail user = SecurityUser.getUser();
        List<VoiceCloneEntity> list = Arrays.stream(ids).map(id -> {
            VoiceCloneEntity entity = new VoiceCloneEntity();
            entity.setId(id);
            entity.setIsDelete(1);
            entity.setUpdater(user.getId());
            entity.setUpdateDate(new Date());
            return entity;
        }).toList();
        boolean bool = voiceCloneService.updateBatchById(list);
        if (!bool) {
            return new Result<Void>().error("删除失败");
        }
        return new Result<>();
    }

    @PostMapping("/clone")
    @Operation(summary = "声音复刻")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> clone(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
        if (ObjectUtils.isEmpty(file)) {
            return new Result<Void>().error("文件不能为空");
        }
        String res = voiceCloneService.clone(file);
        if ("success".equals(res)) {
            return new Result<>();
        }
        return new Result<Void>().error(res);
    }

}