package xiaozhi.modules.timbre;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import xiaozhi.modules.timbre.util.MiniMaxClone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
class MiniMaxCloneTest {

    @Autowired
    private MiniMaxClone miniMaxClone;

    @Test
    void testClone() {
        try {
            // 读取本地文件并转换为 MockMultipartFile
            Path path = Paths.get("/Users/chenerlei/Documents/wakeup_words.wav");
            byte[] content = Files.readAllBytes(path);

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "wakeup_words.wav",
                    "application/octet-stream",
                    content
            );
            Map res = miniMaxClone.clone(file);
            System.out.println(res);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void delete() {
        boolean bool = miniMaxClone.delete("TTS_MinimaxTTS_dreamchen_0001");
        System.out.println("delete bool:" + bool);
    }

    @Test
    void demo() {
        Map map = miniMaxClone.demo("custom-002");
        System.out.println(map);
    }

    @Test
    void list() {
        Map map = miniMaxClone.list();
        for(Object key:map.keySet()){
            System.out.println(key + ":" + map.get(key));
        }
    }
}