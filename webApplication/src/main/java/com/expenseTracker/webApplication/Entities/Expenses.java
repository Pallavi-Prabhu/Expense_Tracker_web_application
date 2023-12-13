package com.expenseTracker.webApplication.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "expenses", schema = "pallavi")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expenses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long expenseId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "expense_name")
    private String expenseName;

    @Column(name = "payer_user_id")
    private Long payerUserId;

    @Column(name = "payee_user_id")
    private Long payeeUserId;

    @Column(name = "expense_status")
    private String expenseStatus;

    @Column(name = "amount")
    private Float amount;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

}

