package com.expenseTracker.webApplication.Services;

import com.expenseTracker.webApplication.Entities.*;
import com.expenseTracker.webApplication.Repositories.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class ExpenseService {
    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);
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
    Boolean flag = true;

    public List<Object> findUserName(String name) {
        try {
            User user = loginRepo.findByEmailId(name);
            List<Object> userData = new ArrayList<>();
            userData.add(user.getFirstName());
            userData.add(user.getId());
            return userData;
        } catch (Exception e) {
            logger.error("Expense Service->Error while finding user by email: {}", name, e);
            throw e;
        }


    }

    public List<Object> findNameId(Long groupID) {
        List<Object> data = expenseRepo.findNameIdByGroupId(groupID);
        return data;
    }

    public void addExpenseService(Map<String, Object> map, Map<Long, String> users, HttpServletRequest request) throws MessagingException, IOException {
        try {
//            System.out.println("map" + map);
//            System.out.println("users" + users);
            logger.info("Expense Service->Inside add expense service");
            List<Object> expenseData = new ArrayList<>();
            //System.out.println(expenseData);
            //System.out.println("userss" + users);
            String expenseName = map.get("expenseName").toString();
            //System.out.println("expenseName " + expenseName);
            String payer = map.get("payer").toString();
            //System.out.println("payer :" + payer);
            String groupIdStr = (String) request.getSession().getAttribute("selectedGroupId");
            Long groupId = null;
            if (groupIdStr != null && !groupIdStr.isEmpty() && !groupIdStr.equals("undefined")) {
                groupId = Long.parseLong(groupIdStr);
                logger.info("Expense Service->Inside add expense service IF1");
                //System.out.println("groupId" + groupId);
            }

            if (users.size() == 2) {
                logger.info("Expense Service->Inside add expense service IF2");
                Map<Long, String> list = users;
//
                Iterator<Long> keyIterator = list.keySet().iterator();

                if (keyIterator.hasNext()) {
                    Long key1 = keyIterator.next();

                    if (keyIterator.hasNext()) {
                        Long key2 = keyIterator.next();
//                        System.out.println(key1);
//                        System.out.println(key2);
                        groupId = expenseRepo.searchGroupExists(key1, key2);
                    }


                }
            }
            //System.out.println(groupId);
            expenseData.add(expenseName);
            expenseData.add(payer);
            for (Long key : users.keySet()) {
                String userValue = users.get(key);
                String onVal = "On" + key.toString();
                if (map.containsKey(onVal)) {
                    Object value = map.get(onVal);
                    if (value != null) {
                        String checkBoxVal = value.toString();
                       // System.out.println("check" + checkBoxVal);
                        if (checkBoxVal.equals("on")) {
                            expenseData.add(key);
                            String keyVal = key.toString();
                            Object value1 = map.get(keyVal);
                            if (value1 != null) {
                                String moneyStr = value1.toString();
                                Float money = Float.parseFloat(moneyStr);
                                expenseData.add(money);
                                logger.info("Expense Service->From expense Page extracting values if");
                            }
                        }
                    } else {
                        expenseData.add(payer);
                        expenseData.add(0);
                        logger.info("Expense Service->if only one person transaction");
                    }
                }
            }
            int paramNumber = 5;
            while (true) {
                String paramName = "param" + paramNumber;
                String paramValue = (String) request.getSession().getAttribute(paramName);
//                System.out.println("paramv"+paramValue);
                if (paramValue != null) {
                    request.getSession().removeAttribute(paramName);
                    //System.out.println(paramName + ": " + paramValue);
                    logger.info("Expense Service->getting params of expense Id's");
                    flag = false;
//                    System.out.println(Long.parseLong(paramValue));
                    deleteRow(Long.parseLong(paramValue), groupId);
                } else {
                    break;
                }
                paramNumber++;
            }

            addValues(expenseData, groupId, request);
        } catch (Exception e) {
            logger.error("Expense Service->Error in addExpenseService: {}", e);
            throw e;
        }
    }


    public void addValues(List<Object> data, Long groupId, HttpServletRequest request) throws MessagingException, IOException {
        try {
            logger.info("Expense Service->Adding values: {}", data);
            //System.out.println("grp:" + groupId);
            Groups groupss = homeRepo.findTypeByGroupId(groupId);
            //System.out.println("grps:" + groupss);
            if (groupss != null && groupss.getGroupType() == 0) {
                for (int i = 2; i < data.size(); i = i + 2) {
                    if (Float.parseFloat(data.get(i + 1).toString()) != 0) {
                        logger.info("Expense Service-if add values");
                        Expenses expenses = new Expenses();
                        expenses.setGroupId(groupId);
                        expenses.setExpenseName(data.get(0).toString());
                        Long payerID = Long.parseLong(data.get(1).toString());
                        Long payeeID = Long.parseLong(data.get(i).toString());
                        if (payeeID == payerID)
                            expenses.setExpenseStatus("settled");
                        else
                            expenses.setExpenseStatus("not settled");
                        expenses.setPayerUserId(payerID);
                        expenses.setPayeeUserId(payeeID);
                        expenses.setAmount(Float.parseFloat(data.get(i + 1).toString()));
                        expenses.setDateTime(LocalDateTime.now());
                        expenseRepo.save(expenses);
                        if (payeeID != payerID) {
                            User uuser = loginRepo.findByUserId(payeeID);
                            User userr = loginRepo.findByUserId(payerID);
                            String grpName = homeRepo.findGroupName(groupId);
                            String action;
                            if (flag) action = "added";
                            else {
                                action = "modified";
                                flag=true;
                            }
                            logger.info("Expense Service-if payeeID != payerID");
                            historyService.addToHistory(action, Float.parseFloat(data.get(i + 1).toString()), payerID, userr.getFirstName(),
                                    LocalDateTime.now(), data.get(0).toString(), grpName, payeeID, uuser.getFirstName());
                        }
                        expenseRepo.updateGroupMembersStatus(groupId);

                    }
                }
            } else {
                // Long groupId = expenseRepo.searchGroupExists(Long.parseLong(data.get(2).toString()), Long.parseLong(data.get(4).toString()));
                //System.out.println("groupIdd" + groupId);
                if (groupId != null) {
                    expenseRepo.updateGroupStatus(groupId);
                    //flag=false;
                    logger.info("Expense Service-else of add values if");
                    addUsers(groupId, data);
                    request.getSession().setAttribute("groupId", groupId);

                } else {
                    logger.info("Expense Service-else of add values else");
                    Groups groups = new Groups();
                    groups.setGroupType(1);
                    groups.setGroupStatus("active");
                    groups.setUserId(Long.parseLong(data.get(4).toString()));
                    groups.setGroupName(data.get(2).toString());
                    homeRepo.save(groups);
                    Long groupIDd = homeRepo.getGroupId(Long.parseLong(data.get(2).toString()), Long.parseLong(data.get(4).toString()));
                    //System.out.println(groupID);
                    GroupMembers groupMembers = new GroupMembers();
                    groupMembers.setGroupId(groupIDd);
                    groupMembers.setMemberStatus("settled");
                    groupMembers.setUserId(Long.parseLong(data.get(2).toString()));
                    groupMembersRepo.save(groupMembers);
                    GroupMembers groupMembers1 = new GroupMembers();
                    groupMembers1.setGroupId(groupIDd);
                    groupMembers1.setMemberStatus("settled");
                    groupMembers1.setUserId(Long.parseLong(data.get(4).toString()));
                    groupMembersRepo.save(groupMembers1);
                    addUsers(groupIDd, data);
                    request.getSession().setAttribute("groupId", groupIDd);

                }
            }

        } catch (Exception e) {
            logger.error("Expense Service->Error while adding values: {}", data, e);
            throw e;
        }
    }

    public Long findUserId(String email) {
        User user = loginRepo.findByEmailId(email);
        Long userId = user.getId();
        return userId;
    }

    public void deleteRow(Long expenseId, Long groupId) {
        logger.info("Expense Service->delete Row nd update status");
        expenseRepo.deleteByExpenseID(expenseId);
        expenseRepo.updateGroupMembersStatus(groupId);
    }

    public void addUsers(Long groupId, List<Object> data) throws MessagingException, IOException {
        try {
            logger.info("Expense Service-> Adding users for groupId: {}", groupId);
            Long payerID = 0l;
            Long payeeID = 0l;
            if (Float.parseFloat(data.get(3).toString()) != 0) {
                logger.info("Expense Service-> inside if add users");
                Expenses expenses = new Expenses();
                expenses.setExpenseName(data.get(0).toString());
                expenses.setGroupId(groupId);
                payerID = Long.parseLong(data.get(1).toString());
                payeeID = Long.parseLong(data.get(2).toString());
                expenses.setPayerUserId(payerID);
                expenses.setPayeeUserId(payeeID);
                if (payeeID == payerID)
                    expenses.setExpenseStatus("settled");
                else
                    expenses.setExpenseStatus("not settled");
                expenses.setAmount(Float.parseFloat(data.get(3).toString()));
                expenses.setDateTime(LocalDateTime.now());
                expenseRepo.save(expenses);
                if (payeeID != payerID) {

                    User uuser = loginRepo.findByUserId(payeeID);
                    User userr = loginRepo.findByUserId(payerID);
                    //String grpName = homeRepo.findGroupName(groupId);
                    String action;
//                    System.out.println(flag);
                    if (flag) action = "added";
                    else {
                        action = "modified";
                        flag=true;
                    }
                    historyService.addToHistory(action, Float.parseFloat(data.get(3).toString()), payerID, userr.getFirstName(),
                            LocalDateTime.now(), data.get(0).toString(), null, payeeID, uuser.getFirstName());
                }
                expenseRepo.updateGroupMembersStatus(groupId);//, payeeID);
            }
            if (Float.parseFloat(data.get(5).toString()) != 0) {
                Expenses expenses1 = new Expenses();
                expenses1.setExpenseName(data.get(0).toString());
                expenses1.setGroupId(groupId);
                payerID = Long.parseLong(data.get(1).toString());
                payeeID = Long.parseLong(data.get(4).toString());
                expenses1.setPayerUserId(payerID);
                expenses1.setPayeeUserId(payeeID);
                if (payeeID == payerID)
                    expenses1.setExpenseStatus("settled");
                else
                    expenses1.setExpenseStatus("not settled");
                expenses1.setAmount(Float.parseFloat(data.get(5).toString()));
                expenses1.setDateTime(LocalDateTime.now());
                expenseRepo.save(expenses1);
                if (payeeID != payerID) {
                    User uuser = loginRepo.findByUserId(payeeID);
                    User userr = loginRepo.findByUserId(payerID);
                    String action;
//                    System.out.println(flag);
                    if (flag) action = "added";
                    else {
                        action = "modified";
                        flag=true;
                    }
                    logger.info("Expense Service-> inside if payeeID != payerID  add users");
                    historyService.addToHistory(action, Float.parseFloat(data.get(3).toString()), payerID, userr.getFirstName(),
                            LocalDateTime.now(), data.get(0).toString(), null, payeeID, uuser.getFirstName());

                }
                expenseRepo.updateGroupMembersStatus(groupId);//, payeeID);
            }
        } catch (Exception e) {
            logger.error("Expense Service->Error while adding users for groupId: {}", groupId, e);
            throw e;
        }
    }
}
