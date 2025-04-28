package xiaozhi.modules.config.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xiaozhi.common.constant.Constant;
import xiaozhi.common.exception.RenException;
import xiaozhi.common.utils.Result;
import xiaozhi.common.validator.ValidatorUtils;
import xiaozhi.modules.agent.service.AgentService;
import xiaozhi.modules.config.dto.AgentModelsDTO;
import xiaozhi.modules.config.dto.SaveMemSummaryDTO;
import xiaozhi.modules.config.dto.SaveMemoryDTO;
import xiaozhi.modules.config.service.ConfigService;
import xiaozhi.modules.device.entity.DeviceEntity;
import xiaozhi.modules.device.service.DeviceService;
import xiaozhi.modules.sys.dto.ConfigSecretDTO;
import xiaozhi.modules.sys.service.SysParamsService;

/**
 * xiaozhi-server 配置获取
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping("config")
@Tag(name = "参数管理")
@AllArgsConstructor
public class ConfigController {
    private final ConfigService configService;
    private final SysParamsService sysParamsService;

    @PostMapping("server-base")
    @Operation(summary = "获取配置")
    public Result<Object> getConfig(@RequestBody ConfigSecretDTO dto) {
        // 效验数据
        ValidatorUtils.validateEntity(dto);
        checkSecret(dto.getSecret());
        Object config = configService.getConfig(true);
        return new Result<Object>().ok(config);
    }

    @PostMapping("agent-models")
    @Operation(summary = "获取智能体模型")
    public Result<Object> getAgentModels(@RequestBody AgentModelsDTO dto) {
        // 效验数据
        ValidatorUtils.validateEntity(dto);
        checkSecret(dto.getSecret());
        Object models = configService.getAgentModels(dto.getMacAddress(), dto.getSelectedModule());
        return new Result<Object>().ok(models);
    }

    @PostMapping("save-mem-summary")
    @Operation(summary = "保存记忆体总结")
    public Result<Object> saveMemSummary(@RequestBody SaveMemSummaryDTO dto) {
        // 效验数据
        ValidatorUtils.validateEntity(dto);
        checkSecret(dto.getSecret());

        boolean bool = configService.saveMemSummary(dto.getMacAddress(), dto.getMemSummary());
        System.out.println("保存记忆体总结 bool:" + bool + ", memSummary: " + dto.getMemSummary());
        if (!bool) {
            return new Result<Object>().error("保存失败");
        }
        return new Result<Object>().ok("");
    }

    @PostMapping("save-memory-data")
    @Operation(summary = "保存聊天记录")
    public Result<Object> saveMemoryData(@RequestBody SaveMemoryDTO dto) {
        // 效验数据
        ValidatorUtils.validateEntity(dto);
        checkSecret(dto.getSecret());
        boolean bool = configService.saveMemory(dto.getMacAddress(), dto.getMemoryData());
        if (!bool) {
            return new Result<Object>().error("保存失败");
        }
        return new Result<Object>().ok("");
    }

    private void checkSecret(String secret) {
        String secretParam = sysParamsService.getValue(Constant.SERVER_SECRET, true);
        // 验证密钥
        if (StringUtils.isBlank(secret) || !secret.equals(secretParam)) {
            throw new RenException("密钥错误");
        }
    }
}
