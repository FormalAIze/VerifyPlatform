package lab.nnverify.platform.verifyplatform.controller;

import lab.nnverify.platform.verifyplatform.models.AuthenticationRequest;
import lab.nnverify.platform.verifyplatform.models.ResponseEntity;
import lab.nnverify.platform.verifyplatform.models.Verification;
import lab.nnverify.platform.verifyplatform.services.VerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
public class UserController {
    @Autowired
    VerificationService verificationService;

    @GetMapping("/user/info")
    public ResponseEntity getUserInfo(@RequestHeader("Authorization") String token) {
        log.info("token: " + token);
        ResponseEntity response = new ResponseEntity();
        response.setStatus(200);
        response.getData().put("name", "Super admin");
        response.getData().put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return response;
    }

    @PostMapping("/user/login")
    public ResponseEntity userLogin(@RequestBody AuthenticationRequest request) {
        ResponseEntity response = new ResponseEntity();
        response.getData().put("token", "hello");
        response.setStatus(200);
        return response;
    }

    @PostMapping("/user/logout")
    public ResponseEntity userLogout() {
        ResponseEntity response = new ResponseEntity();
        response.setStatus(200);
        return response;
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity userVerificationHistory(@PathVariable Integer userId) {
        ResponseEntity response = new ResponseEntity();
        List<Verification> history = verificationService.findVerificationHistoryByUserId(userId);
        if (!history.isEmpty()) {
            response.setStatus(200);
        } else {
            response.setStatus(500);
        }
        response.getData().put("history", history);
        return response;
    }

//    测试用
//    @GetMapping("/user/{userId}/add/verify")
//    public ResponseEntity addVerification(@PathVariable Integer userId) {
//        Verification verification = new Verification("123", 1, "WiNR", "0.1", "mnist", "mnist", "2", "running");
//        verificationService.saveVerificationParams(verification);
//        return new ResponseEntity();
//    }
}