package com.expenseTracker.webApplication.Models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupModel {
    Long groupID;
    String groupName;
    Float positiveAmount;
    Float negativeAmount;
    Integer status;
}
