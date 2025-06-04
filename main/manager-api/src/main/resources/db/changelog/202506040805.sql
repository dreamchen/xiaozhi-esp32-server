-- 添加主键约束
ALTER TABLE `databasechangelog` ADD PRIMARY KEY (`ID`);

-- 删除智能体模板总结记忆字段
ALTER TABLE `ai_agent_template` DROP COLUMN `summary_memory`;
-- 对智能体模板添加助手名称字段
ALTER TABLE `ai_agent_template` ADD COLUMN `assistant_name` VARCHAR(64) DEFAULT '小智' COMMENT '助手名称' after `agent_name`;
-- 对智能体模板添加TTS模型类型字段
ALTER TABLE `ai_agent_template` ADD COLUMN `tts_voice_type` TINYINT DEFAULT 0 COMMENT 'TTS音色标识类型：0：官方、1：复刻' after `tts_voice_id`;
-- 删除智能体总结记忆字段
ALTER TABLE `ai_agent` DROP COLUMN `summary_memory`;
-- 对智能体添加助手名称字段
ALTER TABLE `ai_agent` ADD COLUMN `assistant_name` VARCHAR(64) DEFAULT '小智' COMMENT '助手名称' after `agent_name`;
-- 对智能体添加TTS模型类型字段
ALTER TABLE `ai_agent` ADD COLUMN `tts_voice_type` TINYINT DEFAULT 0 COMMENT 'TTS音色标识类型：0：官方、1：复刻' after `tts_voice_id`;

-- 对设备添加助手名称字段
ALTER TABLE `ai_device` ADD COLUMN `assistant_name` VARCHAR(64) DEFAULT '小智' COMMENT '助手名称' after `agent_id`;
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


