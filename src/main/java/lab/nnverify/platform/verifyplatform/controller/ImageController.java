package lab.nnverify.platform.verifyplatform.controller;

import lab.nnverify.platform.verifyplatform.verifykit.winr.WiNRConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
public class ImageController {
    private final String wiNRBasePath = WiNRConfig.basicPath + "adv_examples/";
    @Value("${winr.file.upload.url}")
    private String uploadFilepathWiNR;

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/winr/adv_image/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] wiNRAdvExample(@PathVariable String filename) throws Exception {

        File file = new File(wiNRBasePath + filename);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        return bytes;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/winr/images")
    @ResponseBody
    public List<String> wiNRImageUpload(@RequestParam("images") MultipartFile[] images) {
        ArrayList<String> imageNames = new ArrayList<>();
        for (MultipartFile image : images) {
            String filename = image.getOriginalFilename();
            log.info(filename);
            int idx = filename != null ? filename.lastIndexOf(".") : 0;
            String extension = filename.substring(idx);
            String uuidFileName = UUID.randomUUID().toString().replace("-", "") + extension;
            String destPath = uploadFilepathWiNR + "/" + uuidFileName;
            log.info("destPath: " + destPath);
            File dest = new File(destPath);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                image.transferTo(dest);
                imageNames.add(destPath);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("文件" + destPath + "上传保存失败");
            }
        }
        return imageNames;
    }
}
