package com.expenseTracker.webApplication.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroupDetails {
    Long groupId;
    String groupName;
    Integer status;
    String groupCreatorName;
    Integer groupType;
    Float pendingAmount;
    String expenseName;
    Float totalAmount;
    String payerName;
    Long payerId;
    Long userId;
    Integer creator;
    Map<Long, List<Object>> userData;

}
