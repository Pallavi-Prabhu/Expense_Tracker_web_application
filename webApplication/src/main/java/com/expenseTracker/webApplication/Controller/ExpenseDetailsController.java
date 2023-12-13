package com.expenseTracker.webApplication.Controller;

import com.expenseTracker.webApplication.Models.PeopleList;
import com.expenseTracker.webApplication.Models.UserGroupDetails;
import com.expenseTracker.webApplication.Services.ExpenseDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jdk.jfr.Frequency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class ExpenseDetailsController {
    @Autowired
    ExpenseDetailsService expenseDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(ExpenseDetailsController.class);

    @PostMapping("/selectedGroupID")
    public ResponseEntity<String> setSelectedUser(@RequestParam("param1") String selectedGroupId, @RequestParam("param2") String amount,
                                                  HttpServletRequest request) {
        try {

            request.getSession().setAttribute("selectedGroupId", selectedGroupId);
//            System.out.println("selecteUser"+ request.getSession().getAttribute("selectedUser"));
            request.getSession().removeAttribute("selectedUser");
//            System.out.println("After selecteUser"+ request.getSession().getAttribute("selectedUser"));
            request.getSession().setAttribute("amount", amount);
            request.getSession().setAttribute("creator", "0");
            logger.info("Expense Details Controller->getting selected user and his/her amount from home page");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Expense Details Controller->Error occurred in setSelectedUser: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/expenseDetailPage")
    public ModelAndView viewPage(HttpServletRequest request) {
        try {
            String name = (String) request.getSession().getAttribute("email");
            if (name == null) {
                logger.info("Inside ExpenseDetails Controller->name not found->redirect to login");
                return new ModelAndView("redirect:/login");
            } else {
                logger.info("Inside ExpenseDetails Contoller->name found->continue");
                return new ModelAndView("expenseDetails");
            }
        } catch (Exception e) {
            logger.error("Expense Details Controller->Error occurred in viewPage: {}", e.getMessage());
            return new ModelAndView("redirect:/login");
        }

    }

    @PostMapping("/selectedGroupIDGroup")
    public ResponseEntity<String> selectedGroupID(@RequestParam("paramId") String selectedGroupIdGroup, @RequestParam("paramAmnt") String amount,
                                                  @RequestParam("paramCreator") String creator, HttpServletRequest request) {
        try {
            request.getSession().setAttribute("selectedGroupId", selectedGroupIdGroup);
//            System.out.println("selectedGroupIdGroup: " + selectedGroupIdGroup);
            request.getSession().setAttribute("amount", amount);
//            System.out.println("amount: " + amount);
            request.getSession().setAttribute("creator", creator);
//            System.out.println("creator: " + creator);
            logger.info("Expense Details Controller->getting selected user Group from home page");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Expense Details Controller->Error occurred in setSelectedUserGroup: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/expenseDetails")
    @ResponseBody
    public ResponseEntity<List<UserGroupDetails>> getExpenseDetail(HttpServletRequest request) {
        try {
            Long groupId;
            Float amount;
            String selectedUser = (String) request.getSession().getAttribute("selectedUser");

            if (selectedUser != null) {
//                System.out.println("inside selecteduser");
//                System.out.println("selectedU"+selectedUser);
                groupId = expenseDetailsService.findId(selectedUser,request);
                amount= 0f;
                request.getSession().removeAttribute("selectedGroupId");
                request.getSession().removeAttribute("selectedUser");
//                System.out.println( "session"+request.getSession().getAttribute("selectedUser"));
//                System.out.println( request.getSession().getAttribute("selectedGroupId"));
            }
            else {
                String groupIdStr = (String) request.getSession().getAttribute("selectedGroupId");
                groupId = null;
                 amount = 0f;
                if (groupIdStr != null) {
//                    System.out.println("groupIdStr1: " + groupIdStr);
                    groupId = Long.parseLong(groupIdStr);
                    String amountStr = (String) request.getSession().getAttribute("amount");
                    Integer type = expenseDetailsService.findType(groupId);
                    amount = Float.parseFloat(amountStr);
//                    System.out.println("amount :" + amount);
                    if (amount != null && amount >= 0 && type == 0) amount = -amount;
//                    System.out.println("groupId :" + groupId);
                }
            }
            request.getSession().setAttribute("pending",amount);
            List<UserGroupDetails> userGroupDetails = expenseDetailsService.getExpenseDetails(groupId, amount, request);
//            System.out.println("userGroupDetails :" + userGroupDetails);

            logger.info("Expense Details Controller->Getting Expense Details");
            return ResponseEntity.ok(userGroupDetails);
        } catch (Exception e) {
            logger.error("Expense Details Controller->Error occurred in setSelectedUser Expense  Details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/deleteExpenseDetails")
    public ResponseEntity<String> deleteExpenseDetails(@RequestParam Map<String, String> allParams, HttpServletRequest request) {
        try {
            expenseDetailsService.rowStatusToDeleted(allParams);
            logger.info("Expense Details Controller->Inside Delete Expense Details Post Mapping");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Expense Details Controller->Error occurred in deleteExpenseDetails: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/deleteExpense")
    public ModelAndView deleteExpense(Model model, HttpServletRequest request) {
        try {
            logger.info("Expense Details Controller->Inside DeleteModify");
            return new ModelAndView("expenseDetails");
        } catch (Exception e) {
            logger.error("Expense Details Controller->Error occurred in deleteExpense: {}", e.getMessage());
            return new ModelAndView("expenseDetails");
        }

    }

    @PostMapping("/settleAll")
    public ResponseEntity<String> setSelectedUser(@RequestParam("selectedGroupId") String selectedGroupId, HttpServletRequest session) {
        try {
            Long groupId = Long.parseLong(selectedGroupId);
            String name = (String) session.getSession().getAttribute("email");
            expenseDetailsService.updateExpenseStatusToSettled(groupId, name, session);
            logger.info("Expense Details Controller->Settle all expenses");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Expense Details Controller->Error occurred in setSelectedUser settle all: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/sendGroupDetails")
    public ResponseEntity<PeopleList> sendGroupDetails(@RequestParam Map<String, String> allParams, HttpServletRequest request) {
        try {
            PeopleList data= expenseDetailsService.getGroupMembers(request);
            logger.info("Expense Details Controller->Inside sendGroupDetails Post Mapping");
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            logger.error("Expense Details Controller->Error occurred in sendGroupDetails: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/getNonGroupMembers")
    public ResponseEntity<Map<String, String>> getNonGroupMembers(@RequestParam("param3") String selectedGroupId, HttpServletRequest request) {
        try {
            Map<String, String> data = expenseDetailsService.getNonGroupMembers(selectedGroupId);
            logger.info("Expense Details Controller->Inside sendGroupDetails Post Mapping");
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            logger.error("Expense Details Controller->Error occurred in sendGroupDetails: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/addNewPeople")
    public ResponseEntity<String> addNewPeople(@RequestBody Map<String, Object> requestData) {
        try {
            logger.info("Expense Details Controller->Inside addNew People post Mapping");
            Long groupId = Long.parseLong(requestData.get("groupId").toString());
            List<String> selectedPeople = (List<String>) requestData.get("selectedPeople");
            expenseDetailsService.addNewPeople(groupId, selectedPeople);

            return ResponseEntity.ok("POST request successful");
        } catch (Exception e) {
            logger.error("Expense Details Controller->Error occurred in addNew People post Mapping: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/removeUser")
    public ResponseEntity<String> removeUser(@RequestParam("paramUsrId") String userIdParam,@RequestParam("paramGrpId") String groupIdParam) {
        try {
            logger.info("Expense Details Controller->Inside removeUser post Mapping");
            Long groupId = Long.parseLong(groupIdParam.toString());
            Long userId=Long.parseLong(userIdParam.toString());

            expenseDetailsService.removePerson(groupId, userId);

            return ResponseEntity.ok("POST request successful");
        } catch (Exception e) {
            logger.error("Expense Details Controller->Error occurred in removeUser post Mapping: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
