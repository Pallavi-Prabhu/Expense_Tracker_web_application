package com.expenseTracker.webApplication.Repositories;

import com.expenseTracker.webApplication.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignupRepo extends JpaRepository<User, Long> {
    User findByEmail(String email);

}
