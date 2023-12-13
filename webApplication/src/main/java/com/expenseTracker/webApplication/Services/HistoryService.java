package com.expenseTracker.webApplication.Services;

import com.expenseTracker.webApplication.Controller.SSEController;
import com.expenseTracker.webApplication.Entities.Expenses;
import com.expenseTracker.webApplication.Entities.History;
import com.expenseTracker.webApplication.Entities.User;
import com.expenseTracker.webApplication.Models.ExpenseModel;
import com.expenseTracker.webApplication.Models.HistoryModel;
import com.expenseTracker.webApplication.Repositories.HistoryRepo;
import com.expenseTracker.webApplication.Repositories.HomeRepo;
import com.expenseTracker.webApplication.Repositories.LoginRepo;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class HistoryService {
    @Autowired
    HistoryRepo historyRepo;
    @Autowired
    LoginRepo loginRepo;
    @Autowired
    private SSEController sseController;
    @Autowired
    HomeRepo homeRepo;

    @Autowired
    EmailService emailService;
    Long userId=null;

    public List<HistoryModel> getHistory(String pageNumberr,HttpServletRequest request)
    {
        User user = loginRepo.findByEmailId(request.getSession().getAttribute("email").toString());
        Long userId=user.getId();
        Integer pageSize=6;
        Long pageNumber=Long.parseLong(pageNumberr.toString());
        //System.out.println("pageNumber"+pageNumber);
        Long offset=(pageNumber-1L)*pageSize;
        //System.out.println("offset"+offset);
        List< History> history = historyRepo.findAllHistoryById(userId,offset,pageSize);
        List<HistoryModel> result= new ArrayList<>();
        for (History historyVal : history) {
//            System.out.println(historyVal);
            HistoryModel historyModel= new HistoryModel();
            String desc = description(historyVal);
            historyModel.setDescription(desc);
            historyModel.setDateTime(historyVal.getDateTime());
            result.add(historyModel);
        }

        return result;
    }

    public Long getRecordsCount(HttpServletRequest request)
    {
        User user = loginRepo.findByEmailId(request.getSession().getAttribute("email").toString());
        userId=user.getId();
        Long count= historyRepo.findAllHistoryById(userId);
        return count;

    }
    public String description(History historyVal)
    {
        StringBuilder stringBuilder = new StringBuilder();
        if(historyVal.getUserId()==userId) {
            if (historyVal.getAction().equals("deleted")) {
                stringBuilder.append(historyVal.getCreatedUserName() + " " + historyVal.getAction());
                if (historyVal.getExpenseName() != null)
                    stringBuilder.append(" expense " + historyVal.getExpenseName());

                if (historyVal.getGroupName() != null) {
                    stringBuilder.append(" Group " + historyVal.getGroupName());
                }
            } else {
                stringBuilder.append(historyVal.getCreatedUserName() + " " + historyVal.getAction());
                if (historyVal.getAction().equals("removed"))
                    stringBuilder.append(" you from ");

                else if (historyVal.getAction().equals("settled"))
                    stringBuilder.append(" you for ");
                else if (historyVal.getAction().equals("modified"))
                    stringBuilder.append(" with you for ");
                else
                    stringBuilder.append(" you to ");


                if (historyVal.getExpenseName() != null && historyVal.getAmount() != null) {
                    stringBuilder.append(" expense " + historyVal.getExpenseName() + " with Rs." +
                            historyVal.getAmount());
                }
                if (historyVal.getGroupName() != null) {
                    stringBuilder.append(" Group " + historyVal.getGroupName());
                }
            }
        }
        else if(historyVal.getCreatedUserId()==userId)
        {
            stringBuilder.append("You ");
            if (historyVal.getAction().equals("deleted")) {
                stringBuilder.append(historyVal.getAction() + " " + historyVal.getUserName());
                if (historyVal.getExpenseName() != null)
                    stringBuilder.append(" from expense " + historyVal.getExpenseName());

                if (historyVal.getGroupName() != null) {
                    stringBuilder.append(" from Group " + historyVal.getGroupName());
                }
            } else {
                stringBuilder.append( historyVal.getAction() +" "+historyVal.getUserName());
                if (historyVal.getAction().equals("removed"))
                    stringBuilder.append("  from ");
                else if (historyVal.getAction().equals("settled"))
                    stringBuilder.append(" for ");
                else if (historyVal.getAction().equals("modified"))
                    stringBuilder.append(" for ");
                else
                    stringBuilder.append(" to ");


                if (historyVal.getExpenseName() != null && historyVal.getAmount() != null) {
                    stringBuilder.append(" expense " + historyVal.getExpenseName() + " with Rs." +
                            historyVal.getAmount());
                }
                if (historyVal.getGroupName() != null) {
                    stringBuilder.append(" Group " + historyVal.getGroupName());
                }
            }

        }


        String desc = stringBuilder.toString();
        return desc;

    }

    public void addToHistory(String action, Float amount, Long creatorId, String creatorName,
                             LocalDateTime dateTime, String expenseName,String grpName, Long userId, String userName) throws MessagingException, IOException {
        History history = new History();
        history.setAction(action);
        history.setAmount(amount);
        history.setCreatedUserId(creatorId);
        history.setCreatedUserName(creatorName);
        history.setDateTime(dateTime);
        history.setExpenseName(expenseName);
        history.setGroupName(grpName);
        history.setUserId(userId);
        history.setUserName(userName);
        historyRepo.save(history);
        String historyEventMessage = notification(history);
        sseController.sendHistoryEvent(historyEventMessage,userId);
        User user = loginRepo.findByUserId(userId);
        String email = user.getEmail();
        //emailService.sendEmail(email, "Expense Tracker", historyEventMessage);
        emailService.sendNotificationEmail(email, "Expense Tracker",userName, historyEventMessage);
    }

    public String notification(History historyVal)
    {
        StringBuilder stringBuilder = new StringBuilder();

        if(historyVal.getAction().equals("deleted"))
        {
            stringBuilder.append(historyVal.getCreatedUserName() + " " + historyVal.getAction());
            if (historyVal.getExpenseName() != null)
                stringBuilder.append(" expense " + historyVal.getExpenseName());

            if (historyVal.getGroupName() != null) {
                stringBuilder.append(" Group " + historyVal.getGroupName());
            }
        }
        else {
            stringBuilder.append(historyVal.getCreatedUserName() + " " + historyVal.getAction());
            if (historyVal.getAction().equals("removed"))
                stringBuilder.append(" you from ");

            else if (historyVal.getAction().equals("settled"))
                stringBuilder.append(" you for ");
            else if (historyVal.getAction().equals("modified"))
                stringBuilder.append(" with you for ");
            else
                stringBuilder.append(" you to ");


            if (historyVal.getExpenseName() != null && historyVal.getAmount() != null) {
                stringBuilder.append(" expense " + historyVal.getExpenseName() + " with Rs." +
                        historyVal.getAmount());
            }
            if (historyVal.getGroupName() != null) {
                stringBuilder.append(" Group " + historyVal.getGroupName());
            }
        }


        String desc = stringBuilder.toString();
        return desc;

    }


    public List<ExpenseModel> getExpenseDetails(HttpServletRequest session)
    {
        String name = (String) session.getSession().getAttribute("email");
        User user = loginRepo.findByEmailId(name);
        List<Object[]> data= historyRepo.findExpenseDetails(user.getId());
        List<ExpenseModel> result = new ArrayList<>();
        for(Object[] res: data)
        {
            ExpenseModel expenseModel = new ExpenseModel();
            Integer grpType= homeRepo.findGroupType(Long.parseLong(res[1].toString()));
            if(grpType==1)
            {
                String userName= historyRepo.findUserName(Long.parseLong(res[1].toString()),user.getId());
                expenseModel.setName(userName);
            }
            else expenseModel.setName(res[0].toString());
            expenseModel.setPending(Float.parseFloat(res[2].toString()));
            expenseModel.setLent(Float.parseFloat(res[3].toString()));
            result.add(expenseModel);

        }

        return result;

    }
}
