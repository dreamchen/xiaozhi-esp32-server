from config.logger import setup_logging
import json
import asyncio
import time
from core.utils.util import (
    get_string_no_punctuation_or_emoji,
)

TAG = __name__
logger = setup_logging()


async def sendAudioMessage(conn, audios, text, text_index=0):
    # 发送句子开始消息
    if text_index == conn.tts_first_text_index:
        logger.bind(tag=TAG).info(f"发送第一段语音: {text}")
    await send_tts_message(conn, "sentence_start", text)

    # 播放音频
    await sendAudio(conn, audios)

    await send_tts_message(conn, "sentence_end", text)

    # 发送结束消息（如果是最后一个文本）
    if conn.llm_finish_task and text_index == conn.tts_last_text_index:
        await send_tts_message(conn, "stop", None)
        if conn.close_after_chat:
            await conn.close()


# 播放音频
async def sendAudio(conn, audios):
    print(f"播放音频: {len(audios)}")
    # 流控参数优化
    frame_duration = 60  # 帧时长（毫秒），匹配 Opus 编码
    start_time = time.perf_counter()
    play_position = 0

    # 预缓冲：发送前 3 帧
    pre_buffer = min(3, len(audios))
    for i in range(pre_buffer):
        await conn.websocket.send(audios[i])

    # 正常播放剩余帧
    for opus_packet in audios[pre_buffer:]:
        if conn.client_abort:
            return

        # 计算预期发送时间
        expected_time = start_time + (play_position / 1000)
        current_time = time.perf_counter()
        delay = expected_time - current_time
        if delay > 0:
            await asyncio.sleep(delay)

        await conn.websocket.send(opus_packet)

        play_position += frame_duration


async def send_tts_message(conn, state, text=None):
    """发送 TTS 状态消息"""
    message = {"type": "tts", "state": state, "session_id": conn.session_id}
    logger.bind(tag=TAG).info(f"send_tts_message state:{state}， text:{text}")
    if text is not None:
        message["text"] = text
        if state == "sentence_start":
            await send_llm_message(conn, text)

    # TTS播放结束
    if state == "stop":
        # 播放提示音
        tts_notify = conn.config.get("enable_stop_tts_notify", False)
        if tts_notify:
            stop_tts_notify_voice = conn.config.get(
                "stop_tts_notify_voice", "config/assets/tts_notify.mp3"
            )
            audios, duration = conn.tts.audio_to_opus_data(stop_tts_notify_voice)
            await sendAudio(conn, audios)
        # 清除服务端讲话状态
        conn.clearSpeakStatus()

    # 发送消息到客户端
    await conn.websocket.send(json.dumps(message))


async def send_stt_message(conn, text):
    """发送 STT 状态消息"""
    await send_llm_message(conn, text)
    stt_text = get_string_no_punctuation_or_emoji(text)
    await conn.websocket.send(
        json.dumps({"type": "stt", "text": stt_text, "session_id": conn.session_id})
    )
    await send_tts_message(conn, "start")


async def send_llm_message(conn, text):
    """发送 LLM 状态消息"""
    term_emoji_prompt = """
    # 会话表情表管理器
    ## 示例
    ```
    😶-neutral
    🙂-happy
    😆-laughing
    😂-funny
    😔-sad
    😠-angry
    😭-crying
    😍-loving
    😳-embarrassed
    😲-surprised
    😱-shocked
    🤔-thinking
    😉-winking
    😎-cool
    😌-relaxed
    🤤-delicious
    😘-kissy
    😏-confident
    😴-sleepy
    😜-silly
    🙄-confused
    ```
    ## 核心能力
    通过当前对话内容，总结会话表情，以便实现在对话中提供丰富的表情服务，对应的返回格式为emoji-emotion,只能返回一个表情，比如：
    ```
    😶-neutral
    ```
    """

    stt_emoji_str = conn.llm.response_no_stream(term_emoji_prompt, text)
    stt_emoji = ['😶','neutral']
    if stt_emoji_str:
        stt_emoji = stt_emoji_str.split('-')
    logger.bind(tag=TAG).info(f"send_llm_message stt_emoji:{stt_emoji}")
    await conn.websocket.send(
        json.dumps(
            {
                "type": "llm",
                "text": stt_emoji[0],
                "emotion": stt_emoji[1],
                "session_id": conn.session_id,
            }
        )
    )