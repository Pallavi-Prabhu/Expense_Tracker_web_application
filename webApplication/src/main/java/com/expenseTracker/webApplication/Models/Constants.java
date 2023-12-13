package com.expenseTracker.webApplication.Models;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Component;

@Component
public class Constants {
    public final String ERROR_INVALID_CREDENTIALS = "Email-Id and Password not found";

    public Boolean loginStatus;

    public final String ERROR_EMAIL_EXISTS = "Email-Id already Exists";
    public Boolean signupStatus;


}
