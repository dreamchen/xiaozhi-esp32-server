from ..base import MemoryProviderBase, logger
import time
import json
import os
import yaml
from config.config_loader import get_project_dir
from config.manage_api_client import save_memory_to_server, DeviceNotFoundException, DeviceBindException


TAG = __name__


class MemoryProvider(MemoryProviderBase):
    def __init__(self, config, read_config_from_api):
        super().__init__(config, read_config_from_api)
        self.short_momery = ""
        self.memory_path = get_project_dir() + "data/.server_memory.yaml"
        self.load_memory()

    def init_memory(self, role_id, llm):
        super().init_memory(role_id, llm)
        #self.load_memory()

    def load_memory(self):
        all_memory = {}
        if os.path.exists(self.memory_path):
            with open(self.memory_path, "r", encoding="utf-8") as f:
                all_memory = yaml.safe_load(f) or {}
        if self.role_id in all_memory:
            self.short_momery = all_memory[self.role_id]
        
        logger.bind(tag=TAG).debug(f"load_memory, short_momery:{self.short_momery}")

    def save_memory_to_file(self, msgs):
        msgStr = ""
        for msg in msgs:
            if msg.role == "user":
                msgStr += f"User: {msg.content}\n"
            elif msg.role == "assistant":
                msgStr += f"Assistant: {msg.content}\n"
        # 当前时间
        time_str = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
        msgStr += f"当前时间：{time_str}"

        all_memory = {}
        if os.path.exists(self.memory_path):
            with open(self.memory_path, "r", encoding="utf-8") as f:
                all_memory = yaml.safe_load(f) or {}
        all_memory[self.role_id] = msgStr
        with open(self.memory_path, "w", encoding="utf-8") as f:
            yaml.dump(all_memory, f, allow_unicode=True)
        logger.bind(tag=TAG).info("save_memory_to_file")

    async def save_memory(self, msgs):
        if self.llm is None:
            logger.bind(tag=TAG).error("LLM is not set for memory provider")
            return None

        
        msgJsonStr = json.dumps(
            [msg.to_dict() for msg in msgs],
            ensure_ascii=False,
            indent=2
        )
        logger.bind(tag=TAG).debug(f"save_memory, msgs:{msgJsonStr}")

        msgs = [msg for msg in msgs if msg.role!="system" and msg.role!="tool"]
        if len(msgs) < 2:
            return None

        if not self.read_config_from_api:
            """保存到本地"""
            self.save_memory_to_file(msgs)
        else:
            """保存到服务器"""
            try:
                begin_time = time.time()
                msgJson = json.dumps(
                    [msg.to_dict() for msg in msgs],
                    ensure_ascii=False,
                    indent=2
                )

                save_memory_to_server(
                    self.role_id,
                    self.role_id,
                    json.loads(msgJson))
                logger.bind(tag=TAG).debug(
                    f"{time.time() - begin_time} 秒，服务端保存聊天记录成功: deviceId:{self.role_id}, clientId:{self.role_id}, memoryData:{msgJson}"
                )
            except DeviceNotFoundException as e:
                logger.bind(tag=TAG).error(f"服务端保存聊天记录失败，设备不存在: {e}")
            except DeviceBindException as e:
                logger.bind(tag=TAG).error(f"服务端保存聊天记录失败，设备绑定异常: {e}")
            except Exception as e:
                logger.bind(tag=TAG).error(f"服务端保存聊天记录失败: {e}")
        
        logger.bind(tag=TAG).info(f"Save memory successful - Role: {self.role_id}")