package lab.nnverify.platform.verifyplatform.controller;

import lab.nnverify.platform.verifyplatform.models.AuthenticationRequest;
import lab.nnverify.platform.verifyplatform.models.AuthenticationResponse;
import lab.nnverify.platform.verifyplatform.models.ResponseBody;
import lab.nnverify.platform.verifyplatform.services.MyUserDetailsService;
import lab.nnverify.platform.verifyplatform.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @RequestMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @PostMapping(value = "/authenticate")
    public ResponseBody createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        ResponseBody response = new ResponseBody();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            log.error("authentication failed! username: " + authenticationRequest.getUsername());
            response.setStatus(403);
            response.getData().put("message", "wrong username or password");
            return response;
        }


        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        log.info("authenticate success");
        response.setStatus(200);
        response.getData().put("token", jwt);
        return response;
    }
}
