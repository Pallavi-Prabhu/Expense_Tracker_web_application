package com.expenseTracker.webApplication.Controller;

import com.expenseTracker.webApplication.Models.Constants;
import com.expenseTracker.webApplication.Services.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    LoginService loginService;
    @Autowired
    Constants constants;// = new Constants();

    @GetMapping("/")
    public String redirectToLoginPage() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        logger.info("Login Controller -> loginPage().");
        model.addAttribute("error", null);
        return "login";
    }

    @PostMapping("/login")
    public ModelAndView loginPage(@RequestParam Map<String, Object> map, Model model, HttpServletRequest request) {
        try {
            String email = map.get("email").toString();
            String password = map.get("password").toString();
            loginService.validate(email, password, request);
            if (constants.loginStatus) {
                logger.info("Login Controller->hello " + request.getSession().getAttribute("email"));
                return new ModelAndView("redirect:/home");
            } else {
                model.addAttribute("error", constants.ERROR_INVALID_CREDENTIALS);
                logger.error("Login Controller->Not valid credentials");
                return new ModelAndView("/login");
            }
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during login.");
            logger.error("Login Controller->Exception during login: " + e.getMessage());
            return new ModelAndView("/login");
        }
    }

}
