package com.expenseTracker.webApplication.Controller;

import com.expenseTracker.webApplication.Models.UsersList;
import com.expenseTracker.webApplication.Services.ExpenseService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ExpenseController {

    @Autowired
    ExpenseService expenseService;
    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);

    String selectedUser = "";
    Boolean flag = true;
    Map<Long, String> users;

    @PostMapping("/setSelectedUser")
    public ResponseEntity<String> setSelectedUser(@RequestParam(name = "selectedUser") String selectedUser, HttpServletRequest request) {
        try {
            request.getSession().setAttribute("selectedUser", selectedUser);
            logger.info("Expense Controller->Inside PostMapping->setSelectedUser");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Expense Controller->Error occurred in setSelectedUser: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/expense")
    public ModelAndView expensePage(Model model, HttpServletRequest request) {
        try {
            selectedUser = (String) request.getSession().getAttribute("selectedUser");

            if (selectedUser == null) {
                logger.info("Inside ExpenseController->name not found->redirect to login");
                return new ModelAndView("redirect:/login");
            } else {
                flag = false;
                logger.info("Inside ExpenseController->name found->go to expense");
                return new ModelAndView("expense");
            }
        } catch (Exception e) {
            logger.error("Expense Controller->Error occurred in expensePage: {}", e.getMessage());
            return new ModelAndView("redirect:/login");
        }
    }

    @PostMapping("/modifyExpenseDetails")
    public ResponseEntity<String> modifyExpenseDetails(@RequestParam Map<String, String> allParams, HttpServletRequest request) {
        try {
            flag = true;
            logger.info("Expense Controller->inside modifyExpenseDetails");
            for (Map.Entry<String, String> param : allParams.entrySet()) {
                String paramName = param.getKey();
                String paramValue = param.getValue();
                request.getSession().setAttribute(paramName, paramValue);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Expense Controller->Error occurred in modifyExpenseDetails: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/expenseModify")
    public ModelAndView expenseModify(Model model, HttpServletRequest request) {
        try {
            if (flag == true) {
                String totAmountStr = (String) request.getSession().getAttribute("param2Amnt");
                Float totAmount = Float.parseFloat(totAmountStr);
                String expenseName = (String) request.getSession().getAttribute("param3Expense");
                String paidByName = (String) request.getSession().getAttribute("param4PaidBy");
                logger.info("Expense Controller->Inside ExpenseModify");
                model.addAttribute("expenseName", expenseName);
                model.addAttribute("amount", totAmount);
                model.addAttribute("paidByName", paidByName);
            }

            return new ModelAndView("expense");

        } catch (Exception e) {
            logger.error("Expense Controller->Error occurred in expensePage2: {}", e.getMessage());
            return new ModelAndView("redirect:/login");
        }

    }

    @PostMapping("/addNewExpense")
    public ResponseEntity<String> addNewExpense(@RequestParam Map<String, String> allParams, HttpServletRequest request) {
        try {
            logger.info("Inside ExpenseController->Inside addNewExpense");
            flag = true;
            for (Map.Entry<String, String> param : allParams.entrySet()) {
                String paramName = param.getKey();
                String paramValue = param.getValue();
                request.getSession().setAttribute(paramName, paramValue);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Expense Controller->Error occurred in addNewExpense: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/addNewExpenseGet")
    public ModelAndView addNewExpenseGetMapping(Model model, HttpServletRequest request) {
        try {

            if ((String) request.getSession().getAttribute("email") == null) {
                logger.info("Expense Controller->name not found->redirect to login");
                return new ModelAndView("redirect:/login");
            } else {

                logger.info("Expense Controller->Inside addNewExpenseGet");
                return new ModelAndView("expense");
            }


        } catch (Exception e) {
            logger.error("Expense Controller->Error occurred in addNewExpenseGet: {}", e.getMessage());
            return new ModelAndView("redirect:/login");
        }

    }


    @GetMapping("/expense-userList")
    @ResponseBody
    public ResponseEntity<UsersList> expensePageDisplayUsers(Model model, HttpServletRequest request) {
        try {
            UsersList usersList = new UsersList();
            Long id = expenseService.findUserId(request.getSession().getAttribute("email").toString());
            if (flag == true) {
                logger.info("Expense Controller->Inside Expense-userlist response body IF");
                String groupIdStr = (String) request.getSession().getAttribute("selectedGroupId");
//                System.out.println(request.getSession().getAttribute("selectedGroupId"));
//                System.out.println("hello");
//                System.out.println(groupIdStr);
                Long groupId = Long.parseLong(groupIdStr);
                List<Object> userData1 = expenseService.findNameId(groupId);
                users = new HashMap<>();

                for (Object data : userData1) {
                    if (data instanceof Object[]) {
                        Object[] userData = (Object[]) data;
                        users.put((Long) userData[0], (String) userData[1]);
                    }
                }
            } else {
                logger.info("Expense Controller->Inside Expense-userlist response body ELSE");
//                System.out.println("selectedUSer:" + selectedUser);
                String email = (String) request.getSession().getAttribute("email");
                List<Object> userData = expenseService.findUserName(email);
                List<Object> userData1 = expenseService.findUserName(selectedUser);
                users = new HashMap<>();
                users.put(Long.parseLong(userData.get(1).toString()), userData.get(0).toString());
                users.put(Long.parseLong(userData1.get(1).toString()), userData1.get(0).toString());
            }
            usersList.setUsersList(users);
            usersList.setUserId(id);

            logger.info("Expense Controller->expensePage DisplayUsers");
            return ResponseEntity.ok(usersList);

        } catch (Exception e) {
            logger.error("Expense Controller->Error occurred in expensePageDisplayUsers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/addExpense")
    public ModelAndView addExpense(@RequestParam Map<String, Object> map, HttpServletRequest request) {
        try {
            Map<String, Object> data = map;
//            System.out.println("data: " + data);
            expenseService.addExpenseService(data, users, request);
            return new ModelAndView("redirect:/home");
        } catch (Exception e) {
            logger.error("Expense Controller->Error occurred in addExpense: {}", e.getMessage());
            return new ModelAndView("redirect:/home");
        }
    }

}
