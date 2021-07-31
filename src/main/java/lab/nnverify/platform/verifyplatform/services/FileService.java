package lab.nnverify.platform.verifyplatform.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class FileService {
    public boolean saveFile(MultipartFile file, String path) {
        log.info("the save path is: " + path);
        File dest = new File(path);
        if (dest.exists()) {
            return true;
        }
        if (!dest.getParentFile().exists()) {
            if (!dest.getParentFile().mkdirs()) {
                log.error("mkdirs files, path: " + path);
                return false;
            }
        }
        try {
            file.transferTo(dest);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("文件" + path + "上传保存失败");
            return false;
        }
    }

    public String generateUUidFilename(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return "";
        }
        log.info("origin filename: " + filename);
        int idx = filename.lastIndexOf(".");
        String extension = filename.substring(idx);
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }
}
