package com.expenseTracker.webApplication.Services;

import com.expenseTracker.webApplication.Controller.LoginController;
import com.expenseTracker.webApplication.Entities.User;
import com.expenseTracker.webApplication.Models.Constants;
import com.expenseTracker.webApplication.Models.PasswordHash;
import com.expenseTracker.webApplication.Repositories.LoginRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@Component
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);
    @Autowired
    LoginRepo loginRepo;
    @Autowired
    Constants constants;

    @Autowired
    PasswordHash passwordHash;

    public void validate(String email, String password, HttpServletRequest request) {
        try {
            String hashPass=passwordHash.passwordHashing(password);

            User user = loginRepo.findByEmailAndPassword(email, hashPass);

            if (user != null) {
                constants.loginStatus = true;
                request.getSession().setAttribute("email", email);

            } else
                constants.loginStatus = false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Login service->Error during validation of login {}", e.getMessage());
//            System.out.println(e.getMessage());
        }
    }
}
