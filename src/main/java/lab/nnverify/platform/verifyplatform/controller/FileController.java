package lab.nnverify.platform.verifyplatform.controller;

import lab.nnverify.platform.verifyplatform.models.ResponseEntity;
import lab.nnverify.platform.verifyplatform.services.FileService;
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
public class FileController {
    @Autowired
    FileService fileService;

    private final String wiNRAdvPath = WiNRConfig.basicPath + "adv_examples/";

    private final String uploadImageFilepathWiNR = WiNRConfig.uploadImageFilepathWiNR;

    @Value("${deepcert.file.upload.image}")
    private String uploadImageFilepathDeepcert;

    private final String uploadModelFilepathWiNR = WiNRConfig.uploadModelFilepathWiNR;

    @Value(("${deepcert.file.upload.model}"))
    private String uploadModelFilepathDeepcert;

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/winr/adv_image/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] wiNRAdvExample(@PathVariable String filename) throws Exception {
        File file = new File(wiNRAdvPath + filename);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        return bytes;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/winr/image/{filename}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    @ResponseBody
    public byte[] wiNRFetchImage(@PathVariable String filename) throws Exception {
        File file = new File(uploadImageFilepathWiNR + filename);
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
            String uuidFileName = fileService.generateUUidFilename(image);
            String destPath = uploadImageFilepathWiNR + uuidFileName;
            if (fileService.saveFile(image, destPath)) {
                imageNames.add(uuidFileName);
                successCount++;
            } else {
                log.error("image save failed, filename is: " + destPath);
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
            String uuidFileName = fileService.generateUUidFilename(image);
            String destPath = uploadImageFilepathDeepcert + uuidFileName;
            if (fileService.saveFile(image, destPath)) {
                imageNames.add(uuidFileName);
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
    @PostMapping("/winr/model")
    @ResponseBody
    public ResponseEntity wiNRModelUpload(@RequestParam("modelFile") MultipartFile model) {
        ResponseEntity response = new ResponseEntity();
        String uuidFileName = fileService.generateUUidFilename(model);
        String destPath = uploadModelFilepathWiNR + uuidFileName;
        if (fileService.saveFile(model, destPath)) {
            response.setStatus(200);
            response.getData().put("modelFilepath", uuidFileName);
        } else {
            response.setStatus(-510);
            response.setMsg("save model failed");
        }
        return response;
    }
}
