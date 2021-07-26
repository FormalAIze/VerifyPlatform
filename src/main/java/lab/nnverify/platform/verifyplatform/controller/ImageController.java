package lab.nnverify.platform.verifyplatform.controller;

import lab.nnverify.platform.verifyplatform.models.ResponseEntity;
import lab.nnverify.platform.verifyplatform.services.ImageService;
import lab.nnverify.platform.verifyplatform.verifykit.winr.WiNRConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

@Slf4j
@Controller
public class ImageController {
    @Autowired
    ImageService imageService;

    private final String wiNRBasePath = WiNRConfig.basicPath + "adv_examples/";

    @Value("${winr.file.upload.url}")
    private String uploadFilepathWiNR;

    @Value("${deepcert.file.upload.url}")
    private String uploadFilepathDeepcert;

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
    public ResponseEntity wiNRImageUpload(@RequestParam("images") MultipartFile[] images) {
        ArrayList<String> imageNames = new ArrayList<>();
        ResponseEntity response = new ResponseEntity();
        int successCount = 0;
        int imageCount = images.length;
        if (imageCount == 0) {
            response.setStatus(-410);
            response.setMsg("no image uploaded");
            return response;
        }
        for (MultipartFile image : images) {
            String uuidFileName = imageService.generateUUidFilename(image);
            String destPath = uploadFilepathDeepcert + "/" + uuidFileName;
            if (imageService.saveImage(image, destPath)) {
                imageNames.add(destPath);
                successCount++;
            } else {
                log.error("iamge save failed, filename is: " + destPath);
            }
        }
        response.getData().put("imageNames", imageNames);
        response.setStatus(successCount == 0 ? -510 : 200);
        response.getData().put("successSave", successCount);
        response.getData().put("imageUpload", imageCount);
        return response;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/deepcert/images")
    @ResponseBody
    public ResponseEntity deepCertImageUpload(@RequestParam("images") MultipartFile[] images) {
        ArrayList<String> imageNames = new ArrayList<>();
        ResponseEntity response = new ResponseEntity();
        int successCount = 0;
        int imageCount = images.length;
        if (imageCount == 0) {
            response.setStatus(-410);
            response.setMsg("no image uploaded");
            return response;
        }
        for (MultipartFile image : images) {
            String uuidFileName = imageService.generateUUidFilename(image);
            String destPath = uploadFilepathWiNR + "/" + uuidFileName;
            if (imageService.saveImage(image, destPath)) {
                imageNames.add(destPath);
                successCount++;
            } else {
                log.error("iamge save failed, filename is: " + destPath);
            }
        }
        response.getData().put("imageNames", imageNames);
        response.setStatus(successCount == 0 ? -510 : 200);
        response.getData().put("successSave", successCount);
        response.getData().put("imageUpload", imageCount);
        return response;
    }
}
