package com.expenseTracker.webApplication.Services;

import com.expenseTracker.webApplication.Entities.Groups;
import com.expenseTracker.webApplication.Entities.User;
import com.expenseTracker.webApplication.Models.GroupModel;
import com.expenseTracker.webApplication.Models.UserGroupModel;
import com.expenseTracker.webApplication.Repositories.GroupMembersRepo;
import com.expenseTracker.webApplication.Repositories.HomeRepo;
import com.expenseTracker.webApplication.Repositories.LoginRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class HomeService {
    @Autowired
    LoginRepo loginRepo;
    @Autowired
    HomeRepo homeRepo;
    @Autowired
    GroupMembersRepo groupMembersRepo;
    @Autowired
    HistoryService historyService;
    private static final Logger logger = LoggerFactory.getLogger(HomeService.class);


    public List<User> findUsers(String name) {
        try {
            List<User> userList = loginRepo.findAllUserByEmailNot(name);
            return userList != null ? userList : new ArrayList<>();
        } catch (Exception e) {
            logger.error("Home service->Error occurred in findUsers: {}", e.getMessage());
            throw new RuntimeException("Home service-> Error occurred while fetching user list");
        }
    }

    public List<Object> getUSerData(String email) {
        try {
            List<Object> userdata = new ArrayList<>();
            User user = loginRepo.findByEmailId(email);
            Float pending = homeRepo.getPendingMoneyForUserId(user.getId());
            Float lent = homeRepo.getLentMoneyForUserId(user.getId());
            userdata.add(email);
            userdata.add(pending != null ? pending : 0f);
            userdata.add(lent != null ? lent : 0f);
            userdata.add(user.getId());
            userdata.add(user.getFirstName());
            return userdata;
        } catch (Exception e) {
            logger.error("Home service->Error occurred in getUSerData: {}", e.getMessage());
            throw new RuntimeException("Home service->Error occurred while fetching user data");
        }
    }

    public List<UserGroupModel> findUserGroup(String pageNumberr,String email) {
        try {
            List<UserGroupModel> groupExpenses = new ArrayList<>();
            Integer groupType = 1;
            Integer pageSize=6;
            Long pageNumber=Long.parseLong(pageNumberr.toString());
            //System.out.println("pageNumber"+pageNumber);
            Long offset=(pageNumber-1L)*pageSize;
            //System.out.println("offset"+offset);
            List<Object[]> result = homeRepo.findByMembersUserEmailAndGroupType(email, groupType,offset,pageSize);

            for (Object[] res : result) {
                Long groupId = ((Number) res[0]).longValue();
                Long userID = ((Number) res[1]).longValue();
                Float pendingMoney = homeRepo.getPendingMoneyForGroupAndEmail(groupId, userID);
                if (pendingMoney == null) pendingMoney = 0f;
                Float lentMoney = homeRepo.getLentMoneyForGroupAndEmail(groupId, userID);
                if (lentMoney == null) lentMoney = 0f;
                UserGroupModel userGroupModel = new UserGroupModel();
                String name = homeRepo.findByGroupId(groupId, userID);
                userGroupModel.setGroupID(groupId);
                userGroupModel.setGroupName(name);
                userGroupModel.setTotalAmount(pendingMoney-lentMoney);
                groupExpenses.add(userGroupModel);
            }
            return groupExpenses;
        } catch (Exception e) {
            logger.error("Home service->Error occurred in findUserGroup: {}", e.getMessage());
            throw new RuntimeException("Home service->Error occurred while fetching user groups");
        }
    }

    public List<GroupModel> findGroups(String email)
    {
        try {
            List<GroupModel> groupExpenses = new ArrayList<>();
            Integer groupType = 0;
            List<Object[]> result = homeRepo.findByMembersUserEmailAndGroupTypeGroups(email, groupType);

            for (Object[] res : result) {
                Long groupId = ((Number) res[0]).longValue();
                Long userID = ((Number) res[1]).longValue();
                Float pendingMoney = homeRepo.getPendingMoneyForGroupAndEmail(groupId, userID);
                if (pendingMoney == null) pendingMoney = 0f;
                Float lentMoney = homeRepo.getLentMoneyForGroupAndEmail(groupId, userID);
                if (lentMoney == null) lentMoney = 0f;

                GroupModel groupModel = new GroupModel();
                Groups groups = homeRepo.findCreatorByGroupId(groupId, userID);
                if(groups!=null)
                  groupModel.setStatus(1);
                else groupModel.setStatus(0);

                groupModel.setGroupName(res[2].toString());
                groupModel.setGroupID(groupId);
                groupModel.setPositiveAmount(pendingMoney);
                groupModel.setNegativeAmount(lentMoney);

                groupExpenses.add(groupModel);
            }
            return groupExpenses;
        } catch (Exception e) {
            logger.error("Home service->Error occurred in findingGroup: {}", e.getMessage());
            throw new RuntimeException("Home service->Error occurred while fetching groups of users");
        }

    }

    public void deleteGroup(Long groupId) {
        try {
            logger.info("Home service->before database calls");
            homeRepo.updateGroupTable(groupId);
            Integer type = homeRepo.findGroupType(groupId);
            List<Object> data = homeRepo.findDetailsBy(groupId);
            if (!data.isEmpty()) {
                Object[] dataArray = (Object[]) data.get(0);
                Long detailId = Long.parseLong(dataArray[1].toString());
                String detailName = dataArray[0].toString();
                String detailValue = dataArray[2].toString();
                List<Object[]> result = homeRepo.findMemberDetails(groupId, detailId);
                for (Object[] row : result) {
                    if (type == 0) {
                        historyService.addToHistory("deleted", null, detailId, detailName, LocalDateTime.now(),
                                null, detailValue, Long.parseLong(row[1].toString()), row[0].toString());
                    } else historyService.addToHistory("deleted", null, detailId, detailName, LocalDateTime.now(),
                            null, "one-to-one", Long.parseLong(row[1].toString()), row[0].toString());
                }
            }

            groupMembersRepo.updateGroupMembers(groupId);
            homeRepo.updateExpenses(groupId);
            logger.info("Home service->after database calls");
        } catch (Exception e) {
            logger.error("Home service->Error occurred in deleteGroup: {}", e.getMessage());
            throw new RuntimeException("Home service->Error occurred while deleting group");
        }
    }
    public Long getRecordsCount(HttpServletRequest request)
    {
        String email =request.getSession().getAttribute("email").toString();

        Long count= homeRepo.findAllUserRecordCountById(email,1);
        return count;

    }

}




