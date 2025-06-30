package xiaozhi.modules.device.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xiaozhi.common.constant.Constant;
import xiaozhi.common.exception.ErrorCode;
import xiaozhi.common.page.PageData;
import xiaozhi.common.redis.RedisKeys;
import xiaozhi.common.redis.RedisUtils;
import xiaozhi.common.user.UserDetail;
import xiaozhi.common.utils.Result;
import xiaozhi.modules.agent.dto.AgentChatHistoryDTO;
import xiaozhi.modules.agent.dto.AgentChatSessionDTO;
import xiaozhi.modules.agent.service.AgentChatHistoryService;
import xiaozhi.modules.device.dto.DeviceDTO;
import xiaozhi.modules.device.dto.DeviceRegisterDTO;
import xiaozhi.modules.device.dto.DeviceUnBindDTO;
import xiaozhi.modules.device.dto.DeviceUpdateDTO;
import xiaozhi.modules.device.dto.DeviceManualAddDTO;
import xiaozhi.modules.device.entity.DeviceEntity;
import xiaozhi.modules.device.service.DeviceService;
import xiaozhi.modules.security.user.SecurityUser;

import java.util.*;

@Tag(name = "设备管理")
@AllArgsConstructor
@RestController
@RequestMapping("/device")
public class DeviceController {
    private final DeviceService deviceService;
    private final AgentChatHistoryService agentChatHistoryService;
    private final RedisUtils redisUtils;

    @PostMapping("/bind/{agentId}/{deviceCode}")
    @Operation(summary = "绑定设备")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> bindDevice(@PathVariable String agentId, @PathVariable String deviceCode) {
        deviceService.deviceActivation(agentId, deviceCode);
        return new Result<>();
    }

    @PostMapping("/bind/{deviceCode}")
    @Operation(summary = "绑定设备")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> bindDevice(@PathVariable String deviceCode) {
        deviceService.deviceActivation(null, deviceCode);
        return new Result<>();
    }

    @PostMapping("/register")
    @Operation(summary = "注册设备")
    public Result<String> registerDevice(@RequestBody DeviceRegisterDTO deviceRegisterDTO) {
        String macAddress = deviceRegisterDTO.getMacAddress();
        if (StringUtils.isBlank(macAddress)) {
            return new Result<String>().error(ErrorCode.NOT_NULL, "mac地址不能为空");
        }
        // 生成六位验证码
        String code = String.valueOf(Math.random()).substring(2, 8);
        String key = RedisKeys.getDeviceCaptchaKey(code);
        String existsMac = null;
        do {
            existsMac = (String) redisUtils.get(key);
        } while (StringUtils.isNotBlank(existsMac));

        redisUtils.set(key, macAddress);
        return new Result<String>().ok(code);
    }

    @GetMapping("/bind/{agentId}")
    @Operation(summary = "获取已绑定设备")
    @RequiresPermissions("sys:role:normal")
    public Result<List<DeviceDTO>> getUserDevices(@PathVariable String agentId) {
        UserDetail user = SecurityUser.getUser();
        List<DeviceDTO> devices = new ArrayList<>();
        if (user.getSuperAdmin() == 1) {
            devices = deviceService.getUserDevices(null, agentId);
        } else {
            devices = deviceService.getUserDevices(user.getId(), agentId);
        }
        return new Result<List<DeviceDTO>>().ok(devices);
    }

    @GetMapping("/{macAddress}")
    @Operation(summary = "获取设备信息")
    @RequiresPermissions("sys:role:normal")
    public Result<DeviceEntity> getDeviceByMac(@PathVariable String macAddress) {
        if (StringUtils.isBlank(macAddress)){
            return new Result().error("参数不能为空");
        }
        DeviceEntity entity  = deviceService.getDeviceByMacAddress(macAddress);
        return new Result<DeviceEntity>().ok(entity);
    }

    @GetMapping("/bind")
    @Operation(summary = "获取已绑定设备")
    @RequiresPermissions("sys:role:normal")
    public Result<List<DeviceDTO>> getUserDevices() {
        UserDetail user = SecurityUser.getUser();
        List<DeviceDTO> devices = deviceService.getUserDevices(user.getId(),null);
        return new Result<List<DeviceDTO>>().ok(devices);
    }

    @PostMapping("/unbind")
    @Operation(summary = "解绑设备")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> unbindDevice(@RequestBody DeviceUnBindDTO unDeviveBind) {
        UserDetail user = SecurityUser.getUser();
        deviceService.unbindDevice(user.getId(), unDeviveBind.getDeviceId());
        return new Result<Void>();
    }

    @PutMapping("/enableOta/{id}/{status}")
    @Operation(summary = "启用/关闭OTA自动升级")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> enableOtaUpgrade(@PathVariable String id, @PathVariable Integer status) {
        DeviceEntity entity = deviceService.selectById(id);
        if (entity == null) {
            return new Result<Void>().error("设备不存在");
        }
        entity.setAutoUpdate(status);
        deviceService.updateById(entity);
        return new Result<Void>();
    }

    @PutMapping("/setAlias/{id}")
    @Operation(summary = "设置别名")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> setAlias(@PathVariable String id, @RequestParam String alias) {
        if(StringUtils.isBlank(alias)){
            return new Result<Void>().error("别名不能为空");
        }
        DeviceEntity entity = deviceService.selectById(id);
        if (entity == null) {
            return new Result<Void>().error("设备不存在");
        }
        UserDetail user = SecurityUser.getUser();
        entity.setAlias(alias);
        entity.setUpdater(user.getId());
        entity.setUpdateDate(new Date());
        deviceService.updateById(entity);
        return new Result<Void>();
    }

    @DeleteMapping("/delMemSummary/{id}")
    @Operation(summary = "删除记忆总结")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> delMemSummary(@PathVariable String id) {
        DeviceEntity entity = deviceService.selectById(id);
        if (entity == null) {
            return new Result<Void>().error("设备不存在");
        }
        entity.setSummaryMemory("");
        entity.setUpdater(SecurityUser.getUser().getId());
        entity.setUpdateDate(new Date());
        deviceService.updateById(entity);
        return new Result<Void>();
    }

    @PostMapping("/manual-add")
    @Operation(summary = "手动添加设备")
    @RequiresPermissions("sys:role:normal")
    public Result<Void> manualAddDevice(@RequestBody @Valid DeviceManualAddDTO dto) {
        UserDetail user = SecurityUser.getUser();
        deviceService.manualAddDevice(user.getId(), dto);
        return new Result<>();
    }

    @GetMapping("/{macAddress}/sessions")
    @Operation(summary = "获取智能体会话列表")
    @RequiresPermissions("sys:role:normal")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true),
            @Parameter(name = Constant.LIMIT, description = "每页显示记录数", required = true),
    })
    public Result<PageData<AgentChatSessionDTO>> getAgentSessions(
            @PathVariable("macAddress") String macAddress,
            @Parameter(hidden = true) @RequestParam Map<String, Object> params) {
        params.put("macAddress", macAddress);
        PageData<AgentChatSessionDTO> page = agentChatHistoryService.getSessionListByAgentId(params);
        return new Result<PageData<AgentChatSessionDTO>>().ok(page);
    }

    @GetMapping("/{macAddress}/chat-history/{sessionId}")
    @Operation(summary = "获取智能体聊天记录")
    @RequiresPermissions("sys:role:normal")
    public Result<List<AgentChatHistoryDTO>> getAgentChatHistory(
            @PathVariable("macAddress") String macAddress,
            @PathVariable("sessionId") String sessionId) {
        // 获取当前用户
        UserDetail user = SecurityUser.getUser();

        // 查询聊天记录
        List<AgentChatHistoryDTO> result = agentChatHistoryService.getChatHistoryBySessionId(null, macAddress,  sessionId);
        return new Result<List<AgentChatHistoryDTO>>().ok(result);
    }
}