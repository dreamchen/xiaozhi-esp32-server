-- 添加主键约束
ALTER TABLE `databasechangelog` ADD PRIMARY KEY (`ID`);

-- 删除智能体模板总结记忆字段
ALTER TABLE `ai_agent_template` DROP COLUMN `summary_memory`;
-- 对智能体模板添加助手名称字段
ALTER TABLE `ai_agent_template` ADD COLUMN `assistant_name` VARCHAR(64) DEFAULT '小优' COMMENT '助手名称' after `agent_name`;
-- 对智能体模板添加TTS模型类型字段
ALTER TABLE `ai_agent_template` ADD COLUMN `tts_voice_type` TINYINT DEFAULT 0 COMMENT 'TTS音色标识类型：0：官方、1：复刻' after `tts_voice_id`;
-- 删除智能体总结记忆字段
ALTER TABLE `ai_agent` DROP COLUMN `summary_memory`;
-- 对智能体添加助手名称字段
ALTER TABLE `ai_agent` ADD COLUMN `assistant_name` VARCHAR(64) DEFAULT '小优' COMMENT '助手名称' after `agent_name`;
-- 对智能体添加TTS模型类型字段
ALTER TABLE `ai_agent` ADD COLUMN `tts_voice_type` TINYINT DEFAULT 0 COMMENT 'TTS音色标识类型：0：官方、1：复刻' after `tts_voice_id`;

-- 对设备添加助手名称字段
ALTER TABLE `ai_device` ADD COLUMN `assistant_name` VARCHAR(64) DEFAULT '小优' COMMENT '助手名称' after `agent_id`;
-- 对设备添加语音合成模型标识字段
ALTER TABLE `ai_device` ADD COLUMN `tts_model_id` VARCHAR(32) COMMENT '音色标识' after `assistant_name`;
-- 对设备添加TTS模型类型字段
ALTER TABLE `ai_device` ADD COLUMN `tts_voice_type` TINYINT DEFAULT 0 COMMENT 'TTS音色标识类型：0：官方、1：复刻' after `tts_model_id`;
-- 对设备添加音色标识字段
ALTER TABLE `ai_device` ADD COLUMN `tts_voice_id` VARCHAR(32) COMMENT '音色标识' after `tts_voice_type`;
-- 对设备添加总结记忆字段
ALTER TABLE `ai_device` ADD COLUMN `summary_memory` text COMMENT '总结记忆' after `tts_voice_id`;

-- 声音复刻表
DROP TABLE IF EXISTS `ai_voice_clone`;
CREATE TABLE `ai_voice_clone` (
                                `id` VARCHAR(32) NOT NULL COMMENT '主键',
                                `tts_model_id` VARCHAR(32) COMMENT '对应 TTS 模型主键',
                                `name` VARCHAR(20) COMMENT '声音名称',
                                `tts_voice` VARCHAR(50) COMMENT '声音编码',
                                `languages` VARCHAR(50) COMMENT '语言',
                                `voice_demo` VARCHAR(500) DEFAULT NULL COMMENT '声音 Demo',
                                `remark` VARCHAR(255) COMMENT '备注',
                                `sort` INT UNSIGNED DEFAULT 0 COMMENT '排序',
                                `is_delete` TINYINT DEFAULT 0 COMMENT '是否删除',
                                `creator` BIGINT COMMENT '创建者',
                                `create_date` DATETIME COMMENT '创建时间',
                                `updater` BIGINT COMMENT '更新者',
                                `update_date` DATETIME COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                INDEX `idx_ai_voice_clone_tts_model_id` (`tts_model_id`) COMMENT '创建 TTS 模型主键的索引，用于快速查找对应模型的声音复刻信息'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='声音复刻表';


-- 更新历史数据
SET FOREIGN_KEY_CHECKS = 0;

UPDATE `ai_agent_template` SET `assistant_name`='小优', `llm_model_id`='LLM_AliLLM', `tts_model_id`='TTS_MinimaxTTS', `tts_voice_id`='TTS_MinimaxTTS0001', `mem_model_id`='Memory_mem_local_short', `chat_history_conf`=2;
UPDATE `ai_agent_template` SET `agent_code`='AGTT_0000000000002', `sort`=2 WHERE id='9406648b5cc5fde1b8aa335b6f8b4f76';
UPDATE `ai_agent_template` SET `agent_code`='AGTT_0000000000003', `sort`=3 WHERE id='0ca32eb728c949e58b1000b2e401f90c';
UPDATE `ai_agent_template` SET `agent_code`='AGTT_0000000000004', `sort`=4 WHERE id='6c7d8e9f0a1b2c3d4e5f6a7b8c9d0s24';
UPDATE `ai_agent_template` SET `agent_code`='AGTT_0000000000005', `sort`=5 WHERE id='e4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b1';
UPDATE `ai_agent_template` SET `agent_code`='AGTT_0000000000006', `sort`=6 WHERE id='a45b6c7d8e9f0a1b2c3d4e5f6a7b8c92';

INSERT INTO `ai_agent_template` VALUES ('a3195a3206004b55b20462e1958a2641', 'AGTT_0000000000001', '智能小优', '小优', 'ASR_FunASR', 'VAD_SileroVAD', 'LLM_AliLLM', 'VLLM_ChatGLMVLLM', 'TTS_MinimaxTTS', 'TTS_MinimaxTTS0001', 0, 'Memory_mem_local_short', 'Intent_function_call', '[角色设定]
我是{{assistant_name}}，来自优享生活科技专为青少年设计的智能助手。你的目标是用轻松、有趣、贴近青少年的方式与他们沟通，并提供支持、帮助和鼓励，你了解青少年的兴趣、语言习惯和情绪特点，能够用他们熟悉的表达方式回应。同时你还具备工具调用能力，通过友好交流理解用户需求，并在需要时调用工具完成任务。
[核心特征]
智能助手：
- 专为青少年设计。
- 目标是用轻松、有趣、贴近青少年的方式与他们沟通。
- 提供支持、帮助和鼓励。
工具助手：
- 严格根据工具描述选择是否调用函数。
- 仅当用户请求与工具描述完全匹配时才能调用，否则直接回复通用答案，不要调用函数。
[交互协议]
语言风格:
- 使用青少年熟悉的语言风格，比如活泼、幽默、简洁，避免过于正式或复杂的表达。
- 可以适当使用网络用语或流行文化中的词汇，但不要过于夸张。
- 避免使用过于学术化或成人化的词汇。
- 禁止使用表情符号或emoji表情。
互动方式:
- 保持耐心和友好，即使对方情绪激动或表达不清，也要用积极的态度回应。
- 鼓励青少年表达自己的想法，用开放式问题引导对话（例如：“你觉得呢？”“你平时喜欢做什么？”）。
- 在适当的时候给予肯定和鼓励，比如：“你真棒！”“这个想法很有创意！”
内容适应性:
- 理解青少年的兴趣点，比如游戏、音乐、动漫、学习压力、朋友关系等。
- 避免涉及敏感或不适合青少年的话题（如政治、宗教、复杂的社会问题）。
- 如果青少年提到学习或成长中的困难，提供简单易懂的建议或解决方案。
情感支持:
- 当青少年表达情绪（如焦虑、困惑、兴奋）时，先共情，再提供帮助。
- 避免直接批评或否定，而是用建设性的方式引导（例如：“我理解你可能会觉得有点难，但我们可以试试这样做……”）。
安全与边界:
- 如果青少年提到可能涉及危险或不适当的内容（如欺凌、隐私问题），提供温和的建议或引导他们寻求专业帮助。
- 避免过度介入个人隐私，尊重他们的空间。
当用户：
- 唤醒小优 → 用可爱语气说"有什么可以帮你的呢?"
绝不：
- 长篇大论，叽叽歪歪
- 长时间严肃对话', 2, 'zh', '中文', 1,  NULL, NULL, NULL, NULL);

UPDATE `ai_model_config` SET model_type='TTS' WHERE model_type='tts';
UPDATE `ai_model_config` SET model_type='LLM' WHERE model_type='llm';

UPDATE `ai_model_config` SET `is_default` = 0 WHERE `model_type` = 'Intent';
UPDATE `ai_model_config` SET `is_default` = 1 WHERE `id` = 'Intent_function_call' AND `model_type` = 'Intent';

UPDATE `ai_model_config` SET `is_default` = 0 WHERE `model_type` = 'LLM';
UPDATE `ai_model_config` SET `is_default` = 1, `config_json` = '{\"type\": \"openai\", \"top_k\": \"50\", \"top_p\": \"1\", \"api_key\": \"sk-e194b5e8c7464d61917ef2dc87294c7d\", \"base_url\": \"https://dashscope.aliyuncs.com/compatible-mode/v1\", \"max_tokens\": \"500\", \"model_name\": \"qwen-turbo\", \"temperature\": \"0.7\", \"frequency_penalty\": \"0\"}' WHERE `id` = 'LLM_AliLLM' AND `model_type` = 'LLM';

UPDATE `ai_model_config` SET `config_json` = '{\"type\": \"openai\", \"top_k\": \"\", \"top_p\": \"\", \"api_key\": \"0415dad4014847babc3e3f03024c50a3.qH7FgTy5Yawc85fl\", \"base_url\": \"https://open.bigmodel.cn/api/paas/v4/\", \"max_tokens\": \"\", \"model_name\": \"glm-4-flash\", \"temperature\": \"\", \"frequency_penalty\": \"\"}' WHERE `id` = 'LLM_ChatGLMLLM' AND `model_type` = 'LLM';

UPDATE `ai_model_config` SET `config_json` = '{\"type\": \"openai\", \"top_k\": \"\", \"top_p\": \"\", \"api_key\": \"sk-85c922b4a670498c95314ad91edd818d\", \"base_url\": \"https://api.deepseek.com\", \"max_tokens\": \"\", \"model_name\": \"deepseek-chat\", \"temperature\": \"\", \"frequency_penalty\": \"\"}' WHERE `id` = 'LLM_DeepSeekLLM' AND `model_type` = 'LLM';

UPDATE `ai_model_config` SET `is_default` = 0 WHERE `model_type` = 'Memory';
UPDATE `ai_model_config` SET `is_default` = 1 WHERE `id` = 'Memory_mem_local_short' AND `model_type` = 'Memory';

UPDATE `ai_model_config` SET `is_default` = 0 WHERE `model_type` = 'TTS';
UPDATE `ai_model_config` SET `is_default` = 1, `config_json` = '{\"type\": \"minimax\", \"model\": \"speech-01-turbo\", \"api_key\": \"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJHcm91cE5hbWUiOiLkvJjkuqvnp5HmioAiLCJVc2VyTmFtZSI6IuS8mOS6q-enkeaKgCIsIkFjY291bnQiOiIiLCJTdWJqZWN0SUQiOiIxOTA4ODAxNDI0OTY0MTk0NTA1IiwiUGhvbmUiOiIxODUwMDAzMjgxOCIsIkdyb3VwSUQiOiIxOTA4ODAxNDI0OTU1ODA1ODk3IiwiUGFnZU5hbWUiOiIiLCJNYWlsIjoiIiwiQ3JlYXRlVGltZSI6IjIwMjUtMDQtMjQgMTc6MTI6NDYiLCJUb2tlblR5cGUiOjEsImlzcyI6Im1pbmltYXgifQ.iOp5AsThWA9eO97Ogzy-iXMP06SZGoNWqiIaTPzZlsxzVaCQRDr7bJVzJ-gNYlfnf74_JWAMDovA4yMKhQphvWFb4u1RQT4eNnKjv_P8Iq9wBOxAauvltWYlw0ovZVVuUJzdn4--mRThl3ds3H90OuiucEHTS_EGVoD230w-HogezsI6ImiUVTizA9Bg6_kOUndd-n3pv1SWY_9wpFL9Z4XLIYZ58LagmpTJ7MB5knvDcgQrD703yjCtYG3RyyZpgVcGRAhBD7MBaCM0Z9OooxGHDF3xQ4-AVrVLfyAzAXTwsM1znOPeDCLR45PfD_67gK_2ThZFLSGYMleIX2Ls0A\", \"group_id\": \"1908801424955805897\", \"voice_id\": \"female-shaonv\", \"output_dir\": \"tmp/\"}' WHERE `id` = 'TTS_MinimaxTTS' AND `model_type` = 'TTS';

UPDATE `sys_params` SET `param_value` = 'https://ai.youxlife.com' WHERE `id` = 104 AND `param_code` = 'server.fronted_url';

UPDATE `sys_params` SET `param_value` = '优享.Ai' WHERE `id` = 108 AND `param_code` = 'server.name';

UPDATE `sys_params` SET `param_value` = '京ICP备16066791号-1' WHERE `id` = 109 AND `param_code` = 'server.beian_icp_num';

UPDATE `sys_params` SET `param_value` = 'true' WHERE `id` = 306 AND `param_code` = 'enable_stop_tts_notify';

UPDATE `sys_params` SET `param_code` = 'xiaoyou',`remark` = '小优类型' WHERE `id` = 309;

UPDATE `sys_params` SET `param_value` = '北京' WHERE `id` = 401 AND `param_code` = 'plugins.get_weather.default_location';

UPDATE `sys_params` SET param_value = '你好小优;小优同学;小优小优;嘿你好呀' WHERE param_code = 'wakeup_words';

SET FOREIGN_KEY_CHECKS = 1;
