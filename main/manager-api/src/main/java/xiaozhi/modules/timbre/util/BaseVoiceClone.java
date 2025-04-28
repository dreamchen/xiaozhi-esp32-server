package xiaozhi.modules.timbre.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public abstract class BaseVoiceClone {

    public abstract Map clone(MultipartFile file);

    public abstract boolean delete(String voiceId);

    public abstract Map demo(String voiceId);

    public abstract Map list();
}
