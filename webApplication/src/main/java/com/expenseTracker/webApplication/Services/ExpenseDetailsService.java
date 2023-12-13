package com.expenseTracker.webApplication.Services;

import com.expenseTracker.webApplication.Controller.LoginController;
import com.expenseTracker.webApplication.Entities.Expenses;
import com.expenseTracker.webApplication.Entities.GroupMembers;
import com.expenseTracker.webApplication.Entities.User;
import com.expenseTracker.webApplication.Models.PeopleList;
import com.expenseTracker.webApplication.Models.UserGroupDetails;
import com.expenseTracker.webApplication.Models.UserGroupModel;
import com.expenseTracker.webApplication.Repositories.ExpenseRepo;
import com.expenseTracker.webApplication.Repositories.GroupMembersRepo;
import com.expenseTracker.webApplication.Repositories.HomeRepo;
import com.expenseTracker.webApplication.Repositories.LoginRepo;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.id.IntegralDataTypeHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class ExpenseDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(ExpenseDetailsService.class);
    @Autowired
    ExpenseRepo expenseRepo;
    @Autowired
    LoginRepo loginRepo;
    @Autowired
    HomeRepo homeRepo;

    @Autowired
    GroupMembersRepo groupMembersRepo;
    @Autowired
    HistoryService historyService;

    public List<UserGroupDetails> getExpenseDetails(Long groupId, Float amount, HttpServletRequest request) {
        try {
            logger.info("Expense Details Service-> Inside Get expense details");
            List<UserGroupDetails> userDetails = new ArrayList<>();
            List<Object[]> expenseNameAmount = expenseRepo.findByGroupId(groupId);
            Integer groupType = homeRepo.findGroupType(groupId);
            String groupName = homeRepo.findGroupName(groupId);
            String groupCreatorName = homeRepo.findGroupCreatorName(groupId);

            //System.out.println(expenseNameAmount.size());
            List<Integer> status = expenseRepo.findStatus(groupId);
            //System.out.println(status);
            Integer creatorId=null;
            String creatorString = (String) request.getSession().getAttribute("creator");
            if(creatorString!=null) {
                creatorId = Integer.parseInt(creatorString);
//                System.out.println("creator Id" + creatorId);
            }

            int i = 0;
            User userrs = loginRepo.findByEmailId(request.getSession().getAttribute("email").toString());

            for (Object[] res : expenseNameAmount) {
                User user = loginRepo.findByUserId(Long.parseLong(res[1].toString()));
                UserGroupDetails userGroupDetails = new UserGroupDetails();
                userGroupDetails.setGroupId(groupId);
                userGroupDetails.setStatus(status.get(i));
                userGroupDetails.setGroupType(groupType);
                userGroupDetails.setPendingAmount(amount);
                userGroupDetails.setCreator(creatorId);
                userGroupDetails.setGroupCreatorName(groupCreatorName);
                userGroupDetails.setGroupName(groupName);
                userGroupDetails.setExpenseName(res[0].toString());
                userGroupDetails.setTotalAmount(Float.parseFloat(res[2].toString()));
                userGroupDetails.setPayerName(user.getFirstName());
                userGroupDetails.setPayerId(Long.parseLong(user.getId().toString()));
                userGroupDetails.setUserId(userrs.getId());
                Map<Long, List<Object>> userDataMap = new HashMap<>();
                Timestamp timestamp = (Timestamp) res[3];
                LocalDateTime dateTime = timestamp.toLocalDateTime();
                LocalDateTime startDateTime = dateTime.minusMinutes(2).withSecond(0).withNano(0);
                LocalDateTime endDateTime = startDateTime.plusMinutes(4);
//                System.out.println("end :"+endDateTime);
                List<Object[]> nameAmount = expenseRepo.findNameAmount(groupId, res[0].toString(), startDateTime, endDateTime);
                for (Object[] data : nameAmount) {
                    User userr = loginRepo.findByUserId(Long.parseLong(data[0].toString()));
                    List<Object> userData = new ArrayList<>();
                    userData.add(userr.getFirstName());
                    userData.add(Float.parseFloat(data[1].toString()));
                    userData.add(data[3].toString());
                    userData.add(userr.getId());
                    userDataMap.put(Long.parseLong(data[2].toString()), userData);
                    userGroupDetails.setUserData(userDataMap);
                }
                userDetails.add(userGroupDetails);
                i++;
            }
            if(expenseNameAmount==null)
            {
                UserGroupDetails userGroupDetails = new UserGroupDetails();
                userGroupDetails.setGroupId(groupId);
                userGroupDetails.setGroupType(groupType);
                userGroupDetails.setCreator(creatorId);
                userGroupDetails.setUserId(userrs.getId());
                userDetails.add(userGroupDetails);

            }
            return userDetails;
        } catch (Exception e) {
            logger.error("Expense Details Service->Error occurred in getExpenseDetails: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    public PeopleList getGroupMembers(HttpServletRequest request)
    {
        PeopleList peopleList = new PeopleList();
        String groupIdStr = (String) request.getSession().getAttribute("selectedGroupId");
        Long groupId = null;
        Float amount = 0f;
        if (groupIdStr != null) {
            //System.out.println("groupIdStr: " + groupIdStr);
            groupId = Long.parseLong(groupIdStr);
        }
        Integer type = homeRepo.findGroupType(groupId);
        if(type==1)
        {
            return peopleList;
        }
        else {
            // Long groupId = Long.parseLong(data.get("param1").toString());
            User user = loginRepo.findByEmailId(request.getSession().getAttribute("email").toString());
            Long userId = user.getId();
            peopleList.setGroupId(groupId);
            String groupName = homeRepo.findGroupName(groupId);
            String groupCreatorName = homeRepo.findGroupCreatorName(groupId);
            peopleList.setGroupName(groupName);
            peopleList.setGroupCreatorName(groupCreatorName);
            String creatorString = (String) request.getSession().getAttribute("creator");
            Integer creatorId = Integer.parseInt(creatorString);
//            System.out.println("creator Id"+creatorId);
            peopleList.setCreator(creatorId);
            List<Object[]> result = homeRepo.findGroupMemberNames(groupId, userId);
//            System.out.println("resuuu"+result);
            Map<String, Object[]> resultMap = new HashMap<>();
            for (Object[] objArray : result) {
                String key = (String) objArray[0];
                Object[] value = {objArray[1], objArray[2]};
                resultMap.put(key, value);
            }
            peopleList.setData(resultMap);
            return peopleList;
        }

    }
    public Map<String,String> getNonGroupMembers(String selectedGroupId)
    {
        Long groupId = Long.parseLong(selectedGroupId.toString());
        List< Object[]> result = homeRepo.findNonGroupMemberNames(groupId);
        Map<String,String> resultMap = new HashMap<>();
        for (Object[] objArray : result) {
            if (objArray.length >= 2 && objArray[0] instanceof String && objArray[1] instanceof String) {
                resultMap.put((String) objArray[0], (String) objArray[1]);
            }
        }
        return resultMap;
    }
    public Long findId(String selectedUser,HttpServletRequest request)
    {
        User user = loginRepo.findByEmailId(selectedUser);
        User user1 = loginRepo.findByEmailId(request.getSession().getAttribute("email").toString());
        Long id = expenseRepo.searchGroupExists(user.getId(), user1.getId());
        return id;
    }



    public void rowStatusToDeleted(Map<String, String> data) {
        try {
            logger.info("Expense Details Service->Inside row status delete");
            Long groupId = Long.parseLong(data.get("param").toString());
//            System.out.println("data :" + data);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!key.equals("param")) {
                    deleteRowStatus(Long.parseLong(value.toString()), groupId);
                }
            }
        }
        catch (Exception e) {
            logger.error("Expense Details Service->Error occurred in rowStatus to be Deleted: {}", e.getMessage());
        }
    }

    public void deleteRowStatus(Long expenseId, Long groupId) throws MessagingException, IOException {
        logger.info("Expense Details Service->Inside Delete Row status");
        List<Object> data = expenseRepo.findPayerDetailsBy(expenseId);
        if (!data.isEmpty()) {
            List<Object> data2 = expenseRepo.findPayeeDetailsBy(expenseId);
            String grpName = homeRepo.findGroupName(groupId);
            Integer grpType= homeRepo.findGroupType(groupId);
            if(grpType==1) grpName=null;
                Object[] dataArray = (Object[]) data.get(0);
                Long creatorId = Long.parseLong(dataArray[1].toString());
                String creatorName = dataArray[0].toString();
                String expenseName = dataArray[2].toString();
                Float amount = Float.parseFloat(dataArray[3].toString());
                Object[] dataArray2 = (Object[]) data2.get(0);
                Long userId = Long.parseLong(dataArray2[1].toString());
                String userName = dataArray2[0].toString();
        historyService.addToHistory("deleted", amount, creatorId, creatorName, LocalDateTime.now(),
                expenseName, grpName, userId, userName);
        }
        expenseRepo.deleteRowStatus(expenseId);
        expenseRepo.updateGroupMembersStatus(groupId);

    }

    public void updateExpenseStatusToSettled(Long groupId, String name,HttpServletRequest request) {
        try {
            User user = loginRepo.findByEmailId(name);
            Long userId = user.getId();
            Integer grpType=null;
            logger.info("Expense Details Service->settle function");
//            System.out.println("userId:"+userId);
            List<Expenses> result= expenseRepo.findDetailsBy(groupId,userId);
            for(Expenses exp: result) {
                User uuser = loginRepo.findByUserId(exp.getPayeeUserId());
                User userr = loginRepo.findByUserId(exp.getPayerUserId());
                String grpName = homeRepo.findGroupName(groupId);
                grpType= homeRepo.findGroupType(groupId);
                if(grpType==1)
                {
                    grpName=null;
                    Float amount=null;
                    String amountStr=request.getSession().getAttribute("pending").toString();
                    if(amountStr!=null)
                    amount =-(Float.parseFloat(amountStr));
                    historyService.addToHistory("settled ", amount,exp.getPayeeUserId() ,uuser.getFirstName() ,
                            LocalDateTime.now(), "all", grpName , exp.getPayerUserId(), userr.getFirstName());
                    break;
                }
                else
                historyService.addToHistory("settled", exp.getAmount(),exp.getPayeeUserId() ,uuser.getFirstName() ,
                        LocalDateTime.now(), exp.getExpenseName(), grpName , exp.getPayerUserId(), userr.getFirstName());
            }
            if(grpType==0) {
                expenseRepo.updateExpenseStatusToSettledIfUnsettledGroup(groupId, userId);
                expenseRepo.updateMemberStatusToSettledIfUnsettledGroup(groupId, userId);
            }
            else if(grpType==1)
            {
                expenseRepo.updateExpenseStatusToSettledIfUnsettled(groupId);
                expenseRepo.updateMemberStatusToSettledIfUnsettled(groupId);
            }
        } catch (Exception e) {
            logger.error("Expense Details Service->Error occurred in updateExpenseStatusToSettled: {}", e.getMessage());
        }
    }

    public Integer findType(Long groupId)
    {
        Integer type= homeRepo.findGroupType(groupId);
        return type;
    }

    public void addNewPeople(Long groupId, List<String> selectedPeople) throws MessagingException, IOException {
        logger.info("Expense Details Service->Inside add new people");
        for (String opt : selectedPeople) {
            User user=loginRepo.findByEmailId(opt);
            Long userId=user.getId();
            GroupMembers groupMember = groupMembersRepo.findByGroupIdUserId(groupId,userId);
            if(groupMember==null) {
                GroupMembers groupMembers = new GroupMembers();
                groupMembers.setUserId(userId);
                groupMembers.setMemberStatus("settled");
                groupMembers.setGroupId(groupId);
                groupMembersRepo.save(groupMembers);
            }
            else
                groupMembersRepo.updateStatus(groupId,userId);

            List<Object> data = homeRepo.findDetailsBy(groupId);

            if (!data.isEmpty()) {
                Object[] dataArray = (Object[]) data.get(0); // Assuming the data is returned as an array of objects
                Long detailId = Long.parseLong(dataArray[1].toString());
                String detailName = dataArray[0].toString();
                String detailValue = dataArray[2].toString();

                historyService.addToHistory("added", null, detailId, detailName, LocalDateTime.now(), null, detailValue, userId, user.getFirstName());
            }

        }
    }

    public void removePerson(Long groupId, Long userId) throws MessagingException, IOException {
        groupMembersRepo.removeStatus(groupId,userId);
        List<Object> data = homeRepo.findDetailsBy(groupId);
        User user = loginRepo.findByUserId(userId);
        if (!data.isEmpty()) {
            Object[] dataArray = (Object[]) data.get(0); // Assuming the data is returned as an array of objects
            Long detailId = Long.parseLong(dataArray[1].toString());
            String detailName = dataArray[0].toString();
            String detailValue = dataArray[2].toString();

            historyService.addToHistory("removed", null, detailId, detailName, LocalDateTime.now(), null, detailValue, userId, user.getFirstName());
        }


    }
}
