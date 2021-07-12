package lab.nnverify.platform.verifyplatform.controller;

import lab.nnverify.platform.verifyplatform.models.AuthenticationRequest;
import lab.nnverify.platform.verifyplatform.models.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@CrossOrigin
@RestController
public class UserController {

    @GetMapping("/user/info")
    public ResponseBody getUserInfo(@RequestParam String token) {
        log.info("token: " + token);
        ResponseBody response = new ResponseBody();
        response.setStatus(200);
        response.getData().put("name", "Super admin");
        response.getData().put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return response;
    }

    @PostMapping("/user/login")
    public ResponseBody userLogin(@RequestBody AuthenticationRequest request) {
        ResponseBody response = new ResponseBody();
        response.getData().put("token", "hello");
        response.setStatus(200);
        return response;
    }

    @PostMapping("/user/logout")
    public ResponseBody userLogout() {
        ResponseBody response = new ResponseBody();
        response.setStatus(200);
        return response;
    }
}
