package com.luxhouse.main.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luxhouse.main.common.APIResponse;
import com.luxhouse.main.domain.Authorities;
import com.luxhouse.main.domain.Roles;
import com.luxhouse.main.domain.Users;
import com.luxhouse.main.model.LoginDTO;
import com.luxhouse.main.model.SignUpDTO;
import com.luxhouse.main.repository.RoleRepository;
import com.luxhouse.main.repository.UserRepository;
import com.luxhouse.main.service.AuthService;
import com.luxhouse.main.service.SessionService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthService authService; 
    @Autowired
    private SessionService  sessionService;

    @PostMapping("/signin")
    public APIResponse authenticateUser(@RequestBody LoginDTO loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        String password = loginDto.getPassword();
        String[] emails = loginDto.getUsernameOrEmail().split("@");

        Users users = userRepository.findByEmailOrUsername(loginDto.getUsernameOrEmail(), emails[0]);

        APIResponse response = new APIResponse();
        
        List<Authorities> list = authService.selectsByUserId(users.getId());
        
        boolean isAdmin = list.stream().anyMatch(e -> e.getRoles().getId() == 1);
        
        if(users == null) {
        	response.setData("Login error");

            response.setStatus(400);

            response.setError("Email ho???c m???t kh???u ch??a ch??nh x??c.");
        }

        if (!isAdmin) {
            Users userRp = users;

            userRp.setPassword("");

            response.setData(userRp);
            response.setStatus(200);
            
            sessionService.set("loginUser", userRp);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
        	
        	Users userRp = users;

            userRp.setPassword("");

            response.setData(userRp);
            response.setStatus(200);
            
            sessionService.set("loginUser", userRp);
            sessionService.set("loginAdmin", userRp);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }


        return response;

//        Map<String, String> map = new HashMap<>();
//        map.put("success", "User signed-in successfully!");
//        return map;
        // {"data":""}
    }

    @PostMapping("/signup")
    public Map<String, String> registerUser(@RequestBody SignUpDTO signUpDto) {

        // add check for username exists in a DB
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
//            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
            Map<String, String> map = new HashMap<>();
            map.put("error", "Username ???? t???n t???i!.");
            return map;
        }

        // add check for email exists in DB
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
//            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
            Map<String, String> map = new HashMap<>();
            map.put("error", "Email ???? t???n t???i!.");
            return map;
        }

        // add check for phone exists in DB
        if (userRepository.existsByPhone(signUpDto.getPhone())) {
//            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
            Map<String, String> map = new HashMap<>();
            map.put("error", "S??? ??i???n tho???i ???? t???n t???i!.");
            return map;
        }

        // create user object
        Users user = new Users();
        user.setFullname(signUpDto.getFullname());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPhone(signUpDto.getPhone());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setGender(true);

        Roles roles = roleRepository.findByName("Users").get();
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);

//        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
        Map<String, String> map = new HashMap<>();
        map.put("success", "User signed-in successfully!");
        return map;

    }
}
