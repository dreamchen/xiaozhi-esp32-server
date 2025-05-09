-- 对智能体模板添加助手名称字段
ALTER TABLE `ai_agent_template` ADD COLUMN `assistant_name` VARCHAR(64) DEFAULT '小智' COMMENT '助手名称' after `agent_name`;
-- 对智能体模板添加TTS模型类型字段
ALTER TABLE `ai_agent_template` ADD COLUMN `tts_voice_type` TINYINT DEFAULT 0 COMMENT 'TTS音色标识类型：0：官方、1：复刻' after `tts_voice_id`;

-- 对智能体添加助手名称字段
ALTER TABLE `ai_agent` ADD COLUMN `assistant_name` VARCHAR(64) DEFAULT '小智' COMMENT '助手名称' after `agent_name`;
-- 对智能体添加TTS模型类型字段
ALTER TABLE `ai_agent` ADD COLUMN `tts_voice_type` TINYINT DEFAULT 0 COMMENT 'TTS音色标识类型：0：官方、1：复刻' after `tts_voice_id`;

-- 对聊天记录详情添加排序字段
ALTER TABLE `ai_chat_message` ADD COLUMN `sort` INT COMMENT '排序' after `content`;

-- 对设备添加记忆总结字段
ALTER TABLE `ai_device` ADD COLUMN `mem_summary` TEXT COMMENT '记忆总结' after `alias`;


-- 添加服务端长期记忆配置
INSERT INTO `ai_model_config` VALUES ('Memory_mem_server', 'Memory', 'mem_server', '服务端长期记忆', 0, 1, '{\"type\": \"mem_server\"}', NULL, NULL, 4, NULL, NULL, NULL, NULL);



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


