package com.adi.trading.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adi.trading.domain.VerificationType;
import com.adi.trading.model.ForgotPasswordToken;
import com.adi.trading.model.User;
import com.adi.trading.model.VerificationCode;
import com.adi.trading.request.ForgotPasswordTokenRequest;
import com.adi.trading.request.ResetPasswordRequest;
import com.adi.trading.response.ApiResponse;
import com.adi.trading.response.AuthResponse;
import com.adi.trading.service.EmailService;
import com.adi.trading.service.ForgotPasswordService;
import com.adi.trading.service.UserService;
import com.adi.trading.service.VerificationCodeService;
import com.adi.trading.utils.OtpUtils;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Autowired
    private EmailService emailService;
    private String jwt;

    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception{
        User user=userService.findUserByJwt(jwt);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(@RequestHeader("Authorization") String jwt, @PathVariable VerificationType verrificationType) throws Exception{
        User user=userService.findUserByJwt(jwt);
        VerificationCode verificationCode=verificationCodeService.getVerificationCodeByUser(user.getId());
        if(verificationCode!=null){
            verificationCode=verificationCodeService.sendVerificationCode(user, verrificationType);
        }
        if(verrificationType.equals(VerificationType.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }

        return new ResponseEntity<>("verification otp sent successfully",HttpStatus.OK);
    }

    @PatchMapping("/api/users/enable-two-factor/verity-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(@RequestHeader("Authorization") String jwt,@PathVariable String otp) throws Exception{
        User user=userService.findUserByJwt(jwt);
        VerificationCode verificationCode=verificationCodeService.getVerificationCodeByUser(user.getId());
        String sendTo=verificationCode.getVerificationType().equals(VerificationType.EMAIL)?
        verificationCode.getEmail():verificationCode.getMobile();
        boolean isVerified=verificationCode.getOtp().equals(otp);
        if(isVerified){
            User updatedUser=userService.enableTwoFactorAuthentication(verificationCode.getVerificationType(), sendTo, user);
            verificationCodeService.deleteVerificationCode(verificationCode);
            return new ResponseEntity<User>(updatedUser,HttpStatus.OK);
        }
        throw new Exception("wrong otp...");
    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(@RequestBody ForgotPasswordTokenRequest req) throws Exception{

        User user=userService.findUserByEmail(req.getSendTo());
        String otp=OtpUtils.generateOtp();
        UUID uuid=UUID.randomUUID();
        String id=uuid.toString();

        ForgotPasswordToken token=forgotPasswordService.findByUser(user.getId());
        if(token==null){
            forgotPasswordService.createToken(user, id, otp, req.getVerificationType(),req.getSendTo());
        }

        if(req.getVerificationType().equals(VerificationType.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(), token.getOtp());
        }
        AuthResponse response=new AuthResponse();
        response.setSession(token.getId());
        response.setMessage("Password reset otp sent successfully");

        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PatchMapping("/auth/users/reset-password/verity-otp")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String id,@RequestBody ResetPasswordRequest req,@RequestHeader("Authorization") String jwt) throws Exception{
        ForgotPasswordToken forgotPasswordToken=forgotPasswordService.findById(id);
        boolean isVerified=forgotPasswordToken.getOtp().equals(req.getOtp());
        if(isVerified){
            userService.updatePasswrd(forgotPasswordToken.getUser(), req.getPassword());
            ApiResponse response=new ApiResponse();
            response.setMessage("password updated successfully");
            return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
        }
        throw new Exception("wrong otp");
    }


}
