package xiaozhi.modules.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xiaozhi.common.user.UserDetail;
import xiaozhi.common.utils.Result;
import xiaozhi.modules.chat.entity.ChatHistoryEntity;
import xiaozhi.modules.chat.entity.ChatMessageEntity;
import xiaozhi.modules.chat.service.ChatHistoryService;
import xiaozhi.modules.chat.service.ChatMessageService;
import xiaozhi.modules.security.user.SecurityUser;

import java.util.List;

/**
 * 声音复刻控制层
 */
@AllArgsConstructor
@RestController
@RequestMapping("/chat")
@Tag(name = "音色管理")
public class ChatController {

    private final ChatHistoryService chatHistoryService;
    private final ChatMessageService chatMessageService;


    @GetMapping("/getChatHistorys")
    @Operation(summary = "获取所有聊天历史")
    @RequiresPermissions("sys:role:normal")
    public Result<List<ChatHistoryEntity>> getChatHistorys(@RequestParam("agentId") String agentId) {
        if (StringUtils.isBlank(agentId)) {
            return new Result().error("agentId不能为空");
        }

        UserDetail user = SecurityUser.getUser();
        List<ChatHistoryEntity> list = chatHistoryService.getChatHistorys(String.valueOf(user.getId()), agentId);
        return new Result<List<ChatHistoryEntity>>().ok(list);
    }

    @GetMapping("/getChatMessages")
    @Operation(summary = "获取所有聊天信息记录")
    @RequiresPermissions("sys:role:normal")
    public Result<List<ChatMessageEntity>> getChatMessages(@RequestParam("chatId") String chatId) {
        if (StringUtils.isBlank(chatId)) {
            return new Result().error("chatId不能为空");
        }

        UserDetail user = SecurityUser.getUser();
        List<ChatMessageEntity> list = chatMessageService.getChatMessages(String.valueOf(user.getId()), chatId);
        return new Result<List<ChatMessageEntity>>().ok(list);
    }


    @PostMapping("/delete")
    @Operation(summary = "聊天历史删除")
    @RequiresPermissions("sys:role:superAdmin")
    public Result<Void> delete(@RequestParam("chatId") String chatId) {
        if (StringUtils.isBlank(chatId)) {
            return new Result().error("chatId不能为空");
        }
        boolean bool = chatHistoryService.deleteByChatId(chatId);
        if (!bool)
            return new Result().error("删除失败");
        return new Result<>();
    }

}