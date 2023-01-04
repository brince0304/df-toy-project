package com.dfoff.demo.Controller;

import com.dfoff.demo.Domain.UserAccount;
import com.dfoff.demo.Service.UserAccountService;
import com.dfoff.demo.Util.Bcrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserAccountController {
    private final UserAccountService userAccountService;
    private final Bcrypt bcrypt;

    @PostMapping("/api/user/login")
    public ResponseEntity<?> login(@RequestBody UserAccount.LoginDto dto) {
        try {
            log.info("login: {}", dto);
            UserAccount.UserAccountDto accountDto = userAccountService.loginByUserId(dto);
            return new ResponseEntity<>(accountDto.getUserId(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("잘못된 아이디나 비밀번호입니다.", HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/api/user")
    public ResponseEntity<?> createAccount(@RequestBody UserAccount.UserAccountSignUpRequest request){
        try {
            log.info("singUp: {}", request);
            if (request.getPassword().equals(request.getPasswordCheck())) {
                UserAccount.UserAccountDto dto = request.toDto();
                dto.setPassword(bcrypt.encode(request.getPassword()));
                if (userAccountService.createAccount(dto)) {
                    return new ResponseEntity<>(request.getUserId(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            log.error("회원가입 실패");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}