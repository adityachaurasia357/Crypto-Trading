package com.adi.trading.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adi.trading.config.JwtProvider;
import com.adi.trading.model.TwoFactorOtp;
import com.adi.trading.model.User;
import com.adi.trading.repository.UserRepository;
import com.adi.trading.response.AuthResponse;
import com.adi.trading.service.CustomUserDetailsService;
import com.adi.trading.service.EmailService;
import com.adi.trading.service.TwoFactorOtpService;
import com.adi.trading.utils.OtpUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception{

        User isEmailExist=userRepository.findByEmail(user.getEmail());
        if(isEmailExist!=null){
            throw new Exception("email already in use with another account...");
        }
 
        User newUser= new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setFullName(user.getFullName()); 

        User savedUser=userRepository.save(newUser);

        Authentication auth=new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt=JwtProvider.generateToken(auth);

        AuthResponse res=new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("register success");


        return new ResponseEntity<>(res,HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception{

        String username=user.getEmail();
        String password=user.getPassword();

        Authentication auth=authenticate(username,password);

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt=JwtProvider.generateToken(auth);

        User authUser=userRepository.findByEmail(username);

        if(user.getTwoFactorAuth().isEnabled()){
            AuthResponse res=new AuthResponse();
            res.setMessage("Two factor auth is enabled");
            res.setTwoFactorAuthEnabled(true);
            String otp=OtpUtils.generateOtp();
            TwoFactorOtp oldTwoFactorOtp=twoFactorOtpService.findByUser(authUser.getId());
            if(oldTwoFactorOtp!=null){
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOtp);
            }
            TwoFactorOtp newTwoFactorOtp=twoFactorOtpService.createTwoFactorOtp(authUser, otp, jwt);
            emailService.sendVerificationOtpEmail(username, otp);
            res.setSession(newTwoFactorOtp.getId());
            return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
        }

        AuthResponse res=new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("login success");


        return new ResponseEntity<>(res,HttpStatus.CREATED);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails=customUserDetailsService.loadUserByUsername(username);
        if(userDetails==null){
            throw new BadCredentialsException("invalid username..."); 
        }
        if(!password.equals(userDetails.getPassword())){
            throw new BadCredentialsException("invalid password...");
        }
        return new UsernamePasswordAuthenticationToken(password, userDetails);
    }


    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySigninOtp(@PathVariable String otp, @RequestParam String id) throws Exception{
        TwoFactorOtp twoFactorOtp=twoFactorOtpService.findById(id);
        if(twoFactorOtpService.verifyTwoFactorOtp(twoFactorOtp, otp)){
            AuthResponse res=new AuthResponse();
            res.setMessage("Two factor authentication verified");
            res.setTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOtp.getJwt());
            return new ResponseEntity<>(res,HttpStatus.OK);
        }
        throw new Exception("invalid otp...");
    }
}
