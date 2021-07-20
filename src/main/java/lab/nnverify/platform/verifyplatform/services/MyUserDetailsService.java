package lab.nnverify.platform.verifyplatform.services;

import lab.nnverify.platform.verifyplatform.mapper.UserMapper;
import lab.nnverify.platform.verifyplatform.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserModel user = userMapper.fetchUserByName(s);
        return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    public UserModel fetchUserByUsername(String s) {
        return userMapper.fetchUserByName(s);
    }
}
