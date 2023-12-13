package com.expenseTracker.webApplication.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "history", schema = "pallavi")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "created_user_id")
    private Long createdUserId;

    @Column(name = "created_user_name")
    private String createdUserName;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "expense_name")
    private String expenseName;

    @Column(name = "action")
    private String action;

    @Column(name = "amount")
    private Float amount;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

}
