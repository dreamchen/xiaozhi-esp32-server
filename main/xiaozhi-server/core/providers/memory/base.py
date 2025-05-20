from abc import ABC, abstractmethod
from config.logger import setup_logging
from config.settings import load_config
from config.manage_api_client import save_mem_summary, DeviceNotFoundException, DeviceBindException
import time

mem_summary_prompt = """
# 智能对话总结机器人
## 核心能力
1.User代表用户，通过对话历史、记忆等知识，总结用户的重要信息(如：姓名、年龄、兴趣)
2.Assistant代表助手，以用户特征数据优先总结与助手的对话数据，不要单独对助手对话进行总结
3.要求含义明确、简短，每次对话后生成新的总结，相同内容以最新为准
4.用户重要信息始终保留，历史总结可基于关联性进行更新和补充，最多不超过1000字
5.生成内容要尽可能的简单，且能表达出用户信息，以便在未来的对话中提供更个性化的服务

## 记忆结构
输出格式为字符串，不需要解释、注释和说明，参考示例格式，保存记忆时仅从对话提取信息，不要混入示例内容
```用户说自己叫张三，今年36岁，喜欢画画。用户想去圆明园，询问圆明园历史，助手回复圆明园历史。```
"""

sdef extract_data(_code):
    start = _code.find("```")
    # 从start开始找到下一个```结束
    end = _code.find("```", start + 1)
    # print("start:", start, "end:", end)
    if start == -1 or end == -1:
        return ""
    strData = _code[start + 3 : end]
    return strData

TAG = __name__
logger = setup_logging()

class MemoryProviderBase(ABC):
    def __init__(self, config, read_config_from_api):
        self.config = config
        self.read_config_from_api = read_config_from_api
        self.role_id = None
        self.llm = None
        self.mem_summary = None

    @abstractmethod
    async def save_memory(self, msgs):
        """Save a new memory for specific role and return memory ID"""
        print("this is base func save_memory", msgs)

    async def query_memory(self, query: str) -> str:
        return self.mem_summary
        

    def init_memory(self, role_id, llm):
        self.role_id = role_id    
        self.llm = llm

    def load_mem_summary(self, mem_summary):
        self.mem_summary = mem_summary

    async def set_mem_summary(self, msgs):
        if self.llm is None:
            logger.bind(tag=TAG).error("LLM is not set for memory provider")
            return None

        msgs = [msg for msg in msgs if msg.role!="system"]
        if len(msgs) < 2:
            return None

        msgStr = ""
        for msg in msgs:
            if msg.role == "user":
                msgStr += f"User: {msg.content}\n"
            elif msg.role == "assistant":
                msgStr += f"Assistant: {msg.content}\n"
        if len(self.short_momery) > 0:
            msgStr += "历史记忆：\n"
            msgStr += self.short_momery
        
        # 当前时间
        time_str = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
        msgStr += f"当前时间：{time_str}"

        logger.bind(tag=TAG).debug(f"save_mem_summary prompt:{mem_summary_prompt}, MSG:{msgStr}")
        result = self.llm.response_no_stream(mem_summary_prompt, msgStr)
        self.mem_summary = extract_data(result)
        logger.bind(tag=TAG).info(f"mem_summary:{self.mem_summary}")

        """如果是从配置文件获取，则进行二次实例化"""
        if not self.read_config_from_api:
            return
        """从接口获取差异化的配置进行二次实例化，非全量重新实例化"""
        try:
            begin_time = time.time()
            save_mem_summary(
                self.role_id,
                self.role_id,
                self.mem_summary)
            logger.bind(tag=TAG).debug(
                f"{time.time() - begin_time} 秒，服务端保存聊天总结成功: deviceId:{self.role_id}, clientId:{self.role_id}, mem_summary:{self.mem_summary}"
            )
        except DeviceNotFoundException as e:
            logger.bind(tag=TAG).error(f"服务端保存聊天总结失败，设备不存在: {e}")
        except DeviceBindException as e:
            logger.bind(tag=TAG).error(f"服务端保存聊天总结失败，设备绑定异常: {e}")
        except Exception as e:
            logger.bind(tag=TAG).error(f"服务端保存聊天总结失败: {e}")
        
        logger.bind(tag=TAG).info(f"Save memory_summary successful - Role: {self.role_id}")
        