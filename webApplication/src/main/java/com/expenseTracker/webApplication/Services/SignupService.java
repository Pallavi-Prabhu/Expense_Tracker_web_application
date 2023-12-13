package com.expenseTracker.webApplication.Services;

import com.expenseTracker.webApplication.Controller.LoginController;
import com.expenseTracker.webApplication.Entities.User;
import com.expenseTracker.webApplication.Models.Constants;
import com.expenseTracker.webApplication.Models.PasswordHash;
import com.expenseTracker.webApplication.Repositories.SignupRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@Component
public class SignupService {
    private static final Logger logger = LoggerFactory.getLogger(SignupService.class);
    @Autowired
    SignupRepo signupRepo;

    @Autowired
    Constants constants;

    @Autowired
    PasswordHash passwordHash;

    public void validateEmail(String email, String firstName, String lastName, Long phNumber, String password) {
        try {
            User user = signupRepo.findByEmail(email);
            if (user != null) {
                constants.signupStatus = false;
            } else {
                constants.signupStatus = true;
                String hashPass=passwordHash.passwordHashing(password);
                saveUser(email, firstName, lastName, phNumber, hashPass);
            }
        } catch (Exception e) {
            logger.error("signUp service->Error in email validation");
            throw new RuntimeException("Error during email validation: " + e.getMessage(), e);
        }
    }


    private void saveUser(String email, String firstName, String lastName, Long phNumber, String password) {
        try {
            User user = new User();
            user.setEmail(email);
            user.setContact(phNumber);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(password);
            signupRepo.save(user);
        } catch (Exception e) {
            logger.info("signup service->Error in saving User data to db");
            throw new RuntimeException("Error during user saving: " + e.getMessage(), e);
        }
    }
}
