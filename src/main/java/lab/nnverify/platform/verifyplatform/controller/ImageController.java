package lab.nnverify.platform.verifyplatform.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;

@ResponseBody
@Controller
public class ImageController {
    private final String wiNRBasePath = "/home/GuoXingWu/WiNR_GXW/adv_examples/";

    @GetMapping(value = "/winr/adv_image/{filename}",produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] wiNRAdvExample(@PathVariable String filename) throws Exception {

        File file = new File(wiNRBasePath + filename);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        return bytes;
    }
}
