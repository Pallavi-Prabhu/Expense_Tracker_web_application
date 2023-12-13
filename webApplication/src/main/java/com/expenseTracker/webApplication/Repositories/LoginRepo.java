package com.expenseTracker.webApplication.Repositories;

import com.expenseTracker.webApplication.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoginRepo extends JpaRepository<User, Long> {

    @Query(value = "SELECT * from tracker_user where email_id!=:name", nativeQuery = true)
    List<User> findAllUserByEmailNot(@Param("name") String name);

    @Query(value = "SELECT * FROM tracker_user WHERE email_id = :email", nativeQuery = true)
    User findByEmailId(@Param("email") String email);

    User findByEmailAndPassword(String email, String password);

    @Query(value = "SELECT * FROM tracker_user WHERE user_id = :userId", nativeQuery = true)
    User findByUserId(Long userId);

//    @Query(value = "SELECT * from tracker_user where email_id=:name", nativeQuery = true)
//    User findAllUserByEmail(@Param("name") String name);

}
