package com.expenseTracker.webApplication.Controller;

import com.expenseTracker.webApplication.Models.Constants;
import com.expenseTracker.webApplication.Services.SignupService;
import jakarta.servlet.http.HttpSession;
import org.hibernate.event.spi.PostCollectionRecreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.lang.constant.Constable;
import java.util.Map;

@Controller
public class SignupController {
    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    @Autowired
    SignupService signupService;

    @Autowired
    Constants constants;// = new Constants();

    @GetMapping("/signup")
    public String signupPAge(Model model, HttpSession session) {
        model.addAttribute("error", null);
        return "signup";
    }

    @PostMapping("/signup")
    public ModelAndView signupPage(@RequestParam Map<String, Object> map, Model model) {
        try {
            String email = map.get("email").toString();
            String firstName = map.get("fname").toString();
            String lastName = map.get("lname").toString();
            Long phNumber = Long.parseLong(map.get("pnumber").toString());
            String password = map.get("password").toString();
            signupService.validateEmail(email, firstName, lastName, phNumber, password);
            if (constants.signupStatus) {
                logger.info("SignUp Controller->Successfull Signup for email: {}", email);
                return new ModelAndView("redirect:/login");
            } else {
                logger.warn("SignUp Controller->Signup failed for email: {}", email);
                model.addAttribute("error", constants.ERROR_EMAIL_EXISTS);
                return new ModelAndView("signup");
            }
        } catch (Exception e) {
            logger.error("SignUp Controller->Error during signup: {}", e.getMessage());
            model.addAttribute("error", "An error occurred during signup.");
            return new ModelAndView("signup");
        }


    }
}
