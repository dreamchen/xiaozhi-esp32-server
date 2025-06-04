package xiaozhi.modules.timbre.util;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xiaozhi.common.user.UserDetail;
import xiaozhi.modules.security.user.SecurityUser;
import xiaozhi.modules.timbre.dao.VoiceCloneDao;
import xiaozhi.modules.timbre.entity.VoiceCloneEntity;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MiniMaxClone extends BaseVoiceClone {

    @Autowired
    private VoiceCloneDao voiceCloneDao;

    @Value("tts.miniMax.demoText")
    private String demoText;

    @Value("${tts.miniMax.apiKey}")
    private String apiKey;

    @Value("${tts.miniMax.groupId}")
    private String groupId;

    @Value("${tts.miniMax.ttsModelId}")
    private String ttsModelId;


    @Override
    public Map clone(MultipartFile file) {
        UserDetail user = SecurityUser.getUser();
        Map<String, String> map = upload(file);
        if (!"0".equals(map.get("code"))) {
            return map;
        }
        int num = 0;
        VoiceCloneEntity entity = voiceCloneDao.selectOne(new QueryWrapper<VoiceCloneEntity>().eq("creator", user.getId()).eq("tts_model_id", ttsModelId).orderByDesc("create_date").last("LIMIT 1"));
        if (ObjectUtils.isNotEmpty(entity)) {
            num = Integer.parseInt(entity.getTtsVoice().split("_")[3]);
        }
        String voiceId = ttsModelId + "_" + user.getUsername() + "_" + getVoiceIdNum(num + 1, 4);
        map = voice_clone(map.get("fileId"), voiceId);
        if ("0".equals(map.get("code"))) {
            map.put("voiceId", voiceId);
            map.put("ttsModelId", ttsModelId);
            String demoAudio = map.get("demoAudio");
            if (StringUtils.isNotBlank(demoAudio)) {
                //demo音频转存
                String realPath = saveDemoAudio(ttsModelId + "_" + voiceId, demoAudio);
                map.put("voiceDemo", realPath);
            }
        }
        return map;
    }

    @Override
    public boolean delete(String voiceId) {
        String bodyContent = "{\n" +
                "            \"voice_type\":\"voice_cloning\",\n" +
                "            \"voice_id\":\"" + voiceId + "\"\n" +
                "         }";

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)  // 设置连接超时为10秒
                    .readTimeout(60, TimeUnit.SECONDS)     // 设置读取超时为30秒
                    .writeTimeout(60, TimeUnit.SECONDS)    // 设置写入超时为30秒
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, bodyContent);
            Request request = new Request.Builder()
                    .url("https://api.minimax.chat/v1/delete_voice?GroupId=" + groupId)
                    .method("POST", body)
                    .addHeader("content-type", "application/json")
                    .addHeader("authorization", "Bearer " + apiKey)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject baseObject = new JSONObject(responseBody);
                JSONObject respObject = baseObject.getJSONObject("base_resp");
                if (respObject.getInt("status_code") == 0) {
                    JSONObject dataObject = baseObject.getJSONObject("data");
                    log.info("MiniMax生成Demo语音成功, voice_id:{}", dataObject.getStr("voice_id"));
                    return true;
                } else {
                    log.error("MiniMax删除失败, voiceId:{}, status_code:{}, status_msg:{}, trace-id:{}", voiceId, respObject.getInt("status_code"), respObject.getStr("status_msg"), response.header("trace-id"));
                }
            } else {
                log.error("MiniMax删除失败, voiceId:{}, responseBody:{}, trace-id:{}", voiceId, response.body().string(), response.header("trace-id"));
            }
        } catch (IOException e) {
            log.error("MiniMax删除失败, voiceId:{}", voiceId, e);
        }
        return false;
    }

    @Override
    public Map demo(String voiceId) {

        Map<String, String> map = new HashMap<>();

        String bodyContent = "{\n" +
                "            \"model\":\"speech-01-turbo\",\n" +
                "            \"text\":\"" + demoText + "\",\n" +
                "            \"stream\":false,\n" +
                "            \"voice_setting\":{\n" +
                "                \"voice_id\":\"" + voiceId + "\",\n" +
                "                \"emotion\":\"happy\"\n" +
                "            },\n" +
                "            \"output_format\": \"url\"\n" +
                "        }";

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)  // 设置连接超时为10秒
                    .readTimeout(60, TimeUnit.SECONDS)     // 设置读取超时为30秒
                    .writeTimeout(60, TimeUnit.SECONDS)    // 设置写入超时为30秒
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, bodyContent);
            Request request = new Request.Builder()
                    .url("https://api.minimax.chat/v1/t2a_v2?GroupId=" + groupId)
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject baseObject = new JSONObject(responseBody);
                JSONObject respObject = baseObject.getJSONObject("base_resp");
                if (respObject.getInt("status_code") == 0) {
                    JSONObject dataObject = baseObject.getJSONObject("data");
                    map.put("url", dataObject.getStr("audio"));
                    map.put("type", "url");
                    map.put("code", "0");
                    log.info("MiniMax生成Demo语音成功, url:{}", dataObject.getStr("audio"));
                } else {
                    map.put("code", respObject.getStr("status_code"));
                    map.put("msg", respObject.getStr("status_msg"));
                    log.error("MiniMax生成Demo语音失败, status_code:{}, status_msg:{}, trace-id:{}", respObject.getInt("status_code"), respObject.getStr("status_msg"), response.header("trace-id"));
                }
            } else {
                map.put("code", "1002");
                map.put("msg", "MiniMax语音生成失败，请求异常");
                log.error("MiniMax生成Demo语音失败, responseBody:{}, trace-id:{}", response.body().string(), response.header("trace-id"));
            }
        } catch (IOException e) {
            map.put("code", "1001");
            map.put("msg", "MiniMax语音生成失败，网络异常");
            log.error("MiniMax语音生成失败, voiceId:{}", voiceId, e);
        }
        return map;
    }

    @Override
    public Map list() {
        Map map = new HashMap<>();

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)  // 设置连接超时为10秒
                    .readTimeout(60, TimeUnit.SECONDS)     // 设置读取超时为30秒
                    .writeTimeout(60, TimeUnit.SECONDS)    // 设置写入超时为30秒
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"voice_type\":\"all\"}");
            Request request = new Request.Builder()
                    .url("https://api.minimax.chat/v1/get_voice")
                    .method("POST", body)
                    .addHeader("content-type", "application/json")
                    .addHeader("authorization", "Bearer " + apiKey)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject baseObject = new JSONObject(responseBody);
                JSONObject respObject = baseObject.getJSONObject("base_resp");
                if (respObject.getInt("status_code") == 0) {
                    map.put("system_voice", baseObject.getJSONArray("system_voice"));
                    map.put("voice_cloning", baseObject.getJSONArray("voice_cloning"));
                    map.put("voice_generation", baseObject.getJSONArray("voice_generation"));
                    map.put("music_generation", baseObject.getJSONArray("music_generation"));
                    map.put("code", "0");
                    log.info("MiniMax列表查询成功");
                } else {
                    map.put("code", respObject.getStr("status_code"));
                    map.put("msg", respObject.getStr("status_msg"));
                    log.error("MiniMax列表查询失败, status_code:{}, status_msg:{}, trace-id:{}", respObject.getInt("status_code"), respObject.getStr("status_msg"), response.header("trace-id"));
                }
            } else {
                map.put("code", "1002");
                map.put("msg", "MiniMax列表查询失败，请求异常");
                log.error("MiniMax列表查询失败, responseBody:{}, trace-id:{}", response.body().string(), response.header("trace-id"));
            }
        } catch (IOException e) {
            map.put("msg", "MiniMax列表查询失败，网络异常");
            log.error("MiniMax列表查询失败", e);
        }
        return map;
    }


    /**
     * Step1:上传文件
     *
     * @param file 文件
     * @return 文件id
     */
    private Map upload(MultipartFile file) {
        Map<String, String> map = new HashMap<>();

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)  // 设置连接超时为10秒
                    .readTimeout(60, TimeUnit.SECONDS)     // 设置读取超时为30秒
                    .writeTimeout(60, TimeUnit.SECONDS)    // 设置写入超时为30秒
                    .build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("purpose", "voice_clone")
                    .addFormDataPart("file", file.getOriginalFilename(),
                            RequestBody.create(MediaType.parse("application/octet-stream"),
                                    file.getBytes()))
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.minimax.chat/v1/files/upload?GroupId=" + groupId)
                    .method("POST", body)
                    .addHeader("authority", "api.minimax.chat")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject baseObject = new JSONObject(responseBody);
                JSONObject respObject = baseObject.getJSONObject("base_resp");
                if (respObject.getInt("status_code") == 0) {
                    JSONObject dataObject = baseObject.getJSONObject("file");
                    map.put("fileId", dataObject.getStr("file_id"));
                    map.put("code", "0");
                    log.info("MiniMax上传成功, file:{}", baseObject.getStr("file"));
                } else {
                    map.put("code", respObject.getStr("status_code"));
                    map.put("msg", respObject.getStr("status_msg"));
                    log.error("MiniMax上传失败, status_code:{}, status_msg:{}, trace-id:{}", respObject.getInt("status_code"), respObject.getStr("status_msg"), response.header("trace-id"));
                }
            } else {
                map.put("code", "1002");
                map.put("msg", "MiniMax上传失败，请求异常");
                log.error("MiniMax上传失败, responseBody:{}, trace-id:{}", response.body().string(), response.header("trace-id"));
            }
        } catch (IOException e) {
            map.put("code", "1001");
            map.put("msg", "MiniMax上传失败，网络异常");
            log.error("MiniMax上传失败, fileName:{}", "test", e);
        }
        return map;
    }


    /**
     * Step2:声音复刻
     *
     * @param fileId
     * @param voiceId
     * @return
     */
    private Map voice_clone(String fileId, String voiceId) {
        Map<String, String> map = new HashMap<>();

        try {
            String bodyContent = "{\n" +
                    "                \"file_id\": " + fileId + ",\n" +
                    "                \"voice_id\": \"" + voiceId + "\",\n" +
                    "                \"model\": \"speech-02-turbo\",\n" +
                    "                \"accuracy\": 0.8,\n" +
                    "                \"need_noise_reduction\": true,\n" +
                    "                \"text\": \"" + demoText + "\"\n" +
                    "            }";
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)  // 设置连接超时为10秒
                    .readTimeout(60, TimeUnit.SECONDS)     // 设置读取超时为30秒
                    .writeTimeout(60, TimeUnit.SECONDS)    // 设置写入超时为30秒
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, bodyContent);
            Request request = new Request.Builder()
                    .url("https://api.minimax.chat/v1/voice_clone?GroupId=" + groupId)
                    .method("POST", body)
                    .addHeader("authority", "api.minimax.chat")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("content-type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
//                String responseHeader = response.headers()
                JSONObject baseObject = new JSONObject(responseBody);
                JSONObject respObject = baseObject.getJSONObject("base_resp");
                if (respObject.getInt("status_code") == 0) {
                    map.put("code", "0");
                    map.put("demoAudio", baseObject.getStr("demo_audio"));
                    log.info("MiniMax复刻成功, demoAudio:{}", map.get("demoAudio"));
                } else {
                    map.put("code", respObject.getStr("status_code"));
                    map.put("msg", respObject.getStr("status_msg"));
                    log.error("MiniMax复刻失败, fileId:{}, voiceId:{}, status_code:{}, status_msg:{}, trace-id:{}", fileId, voiceId, respObject.getInt("status_code"), respObject.getStr("status_msg"), response.header("trace-id"));
                }
            } else {
                map.put("code", "1002");
                map.put("msg", "MiniMax复刻失败，请求异常");
                log.error("MiniMax复刻失败, fileId:{}, voiceId:{}, responseBody:{}, trace-id:{}", fileId, voiceId, response.body().string(), response.header("trace-id"));
            }
        } catch (IOException e) {
            map.put("code", "1001");
            map.put("msg", "MiniMax复刻失败，网络异常");
            log.error("MiniMax复刻失败, fileId:{}, voiceId:{}", fileId, voiceId, e);
        }
        return map;
    }


    /**
     * 生成指定长度的语音ID字符串
     * 如果传入的数字长度小于指定长度，会在数字前补零直到达到指定长度
     *
     * @param num    用于生成语音ID的数字
     * @param length 期望的语音ID字符串长度
     * @return 生成的语音ID字符串
     */
    private String getVoiceIdNum(int num, int length) {
        // 将传入的数字转换为字符串
        String voiceId = String.valueOf(num);
        // 如果当前字符串长度小于期望的长度，则在字符串前补零
        while (voiceId.length() < length) {
            voiceId = "0" + voiceId;
        }
        // 返回补零后的语音ID字符串
        return voiceId;
    }

    /**
     * 保存演示音频文件到指定目录
     *
     * @param fileName     演示音频的文件名，不含扩展名
     * @param demoAudioUrl 演示音频文件的URL或路径
     * @return 返回保存后的音频文件的路径
     * <p>
     * 该方法首先尝试从提供的演示音频URL中读取文件，然后将其保存到上传目录中，
     * 如果上传目录不存在，则会创建该目录保存时使用原始文件名加上".wav"扩展名
     * 如果在文件操作过程中发生任何错误，将抛出RuntimeException
     */
    private String saveDemoAudio(String fileName, String demoAudioUrl) {
        // 初始化文件路径为演示音频的URL
        String realPath = demoAudioUrl;

        try {
            // 设置存储路径
            String uploadDir = "upload/voice";
            Path uploadPath = Paths.get(uploadDir);

            // 如果目录不存在，创建目录
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 使用fileName参数作为文件名，固定使用.wav扩展名
            String realFileName = fileName + ".wav";
            // 解析最终的文件保存路径
            Path filePath = uploadPath.resolve(realFileName);
            // 使用 try-with-resources 确保资源释放
            // 创建文件输入流，用于读取演示音频文件
            try (FileInputStream file = new FileInputStream(demoAudioUrl)) {
                // 保存文件到指定路径
                Files.copy(file, filePath);
            }
            // 更新实际文件路径为保存后的路径
            realPath = filePath.toString();
        } catch (Exception e) {
            // 如果发生IO错误，抛出RuntimeException
            log.error("保存演示音频文件失败：" + e.getMessage(), e);
        }

        // 返回保存后的文件路径
        return realPath;
    }

}
