package com.expenseTracker.webApplication.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroupModel {
    private Long groupID;
    private String groupName;
    private Float totalAmount;

}
