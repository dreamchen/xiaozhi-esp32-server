from ..base import MemoryProviderBase, logger
import time
import json
import os
import yaml
from config.config_loader import get_project_dir


custom_term_memory_prompt = """
# 智能对话总结机器人
## 核心能力
通过对话历史、记忆等知识，总结user的重要信息，要求含义明确、简短，以便在未来的对话中提供更个性化的服务

## 记忆结构
输出格式为字符串，不需要解释、注释和说明，参考示例格式，如果会话中不包含不要出现示例内容，保存记忆时仅从对话提取信息，不要混入示例内容
```
用户询问小优是否会画画，小优提供教程。用户对蒙娜丽莎感兴趣，小优介绍达·芬奇及传说。用户探讨恐龙灭绝原因，小优解释小行星撞击理论。用户对冷笑话感兴趣，小优分享笑话。用户提到人鱼传说，小优探讨存在形式及保护方法。用户讨论海洋传说、百慕大三角及泰坦尼克号遗址，小优分享相关理论。用户回忆圆明园遗址，小优介绍十二生肖铜像历史。用户希望成为考古学家找回流失文物，小优鼓励关注博物馆和历史遗迹。用户对生肖文化感兴趣，小优解释龙、蛇、鸡、猪等生肖意义。用户提到美食“爬拉干妈”，小优推荐川菜菜系。用户讨论宝石形成过程和种类，包括钻石、红宝石、蓝宝石、翡翠、和田玉等。用户分享哈利·波特故事，小优参与讨论。用户询问猪油成分及美人鱼脂肪是否能制作油灯，小优解释脂肪组成并探讨美人鱼传说。用户提到设备功能演示问题，小优建议检查线路并讨论功能演示重要性。
```
"""


def extract_data(_code):
    start = _code.find("```")
    # 从start开始找到下一个```结束
    end = _code.find("```", start + 1)
    # print("start:", start, "end:", end)
    if start == -1 or end == -1:
        return ""
    strData = _code[start + 3 : end]
    return strData


TAG = __name__


class MemoryProvider(MemoryProviderBase):
    def __init__(self, config):
        super().__init__(config)
        self.short_momery = ""
        self.memory_path = get_project_dir() + "data/.custom_memory.yaml"
        self.load_memory()

    def init_memory(self, role_id, llm):
        super().init_memory(role_id, llm)
        self.load_memory()

    def load_memory(self):
        all_memory = {}
        if os.path.exists(self.memory_path):
            with open(self.memory_path, "r", encoding="utf-8") as f:
                all_memory = yaml.safe_load(f) or {}
        if self.role_id in all_memory:
            self.short_momery = all_memory[self.role_id]
        
        logger.bind(tag=TAG).info(f"load_memory, short_momery:{self.short_momery}")

    def save_memory_to_file(self):
        all_memory = {}
        if os.path.exists(self.memory_path):
            with open(self.memory_path, "r", encoding="utf-8") as f:
                all_memory = yaml.safe_load(f) or {}
        all_memory[self.role_id] = self.short_momery
        with open(self.memory_path, "w", encoding="utf-8") as f:
            yaml.dump(all_memory, f, allow_unicode=True)
        logger.bind(tag=TAG).info("save_memory_to_file")

    async def save_memory(self, msgs):
        logger.bind(tag=TAG).info(f"save_memory self:{self}, msgs:{msgs}")
        if self.llm is None:
            logger.bind(tag=TAG).error("LLM is not set for memory provider")
            return None

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

        logger.bind(tag=TAG).info(f"save_memory prompt:{custom_term_memory_prompt}, MSG:{msgStr}")
        result = self.llm.response_no_stream(custom_term_memory_prompt, msgStr)

        _str = extract_data(result)
        # try:
        #     _data = json.loads(_str)  # 检查json格式是否正确
        #     self.short_momery = _str
        # except Exception as e:
        #     print("Error:", e)
        self.short_momery = _str

        logger.bind(tag=TAG).info(f"save_memory short_momery:{_str},result:{result}")

        self.save_memory_to_file()
        logger.bind(tag=TAG).info(f"Save memory successful - Role: {self.role_id}")

        return self.short_momery

    async def query_memory(self, query: str) -> str:
        short_memory = self.short_momery
        result = self.llm.response_no_stream(query, short_memory)
        logger.bind(tag=TAG).info(f"query_memory query:{query}, short_memory:{short_memory}, result:{result}")
        return self.short_momery
