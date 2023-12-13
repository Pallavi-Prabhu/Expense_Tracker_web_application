package com.expenseTracker.webApplication.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_table", schema = "pallavi")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Groups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "groups_id")
    private Long groupId;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "group_type")
    private Integer groupType;

    @Column(name = "group_status")
    private String groupStatus;

}
