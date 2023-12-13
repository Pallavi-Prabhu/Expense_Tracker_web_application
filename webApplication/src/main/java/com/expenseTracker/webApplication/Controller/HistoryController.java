package com.expenseTracker.webApplication.Controller;

import com.expenseTracker.webApplication.Entities.History;
import com.expenseTracker.webApplication.Models.ExpenseModel;
import com.expenseTracker.webApplication.Models.HistoryModel;
import com.expenseTracker.webApplication.Services.HistoryService;
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

import java.util.ArrayList;
import java.util.List;

@Controller
public class HistoryController {
    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);
    @Autowired
    HistoryService historyService;


    @GetMapping("/history")
    public ModelAndView expensePage(Model model, HttpServletRequest request) {
        try {
            String email = (String) request.getSession().getAttribute("email");

            if (email == null) {
                logger.info("Inside HistoryController->name not found->redirect to login");
                return new ModelAndView("redirect:/login");
            } else {
                logger.info("Inside HistoryController->name found->go to expense");
                return new ModelAndView("history");
            }
        } catch (Exception e) {
            logger.error("History Controller->Error occurred : {}", e.getMessage());
            return new ModelAndView("redirect:/login");
        }
    }

    @GetMapping("/dashboardd")
    public ModelAndView dashboardPage(Model model, HttpServletRequest request) {
        try {
            String email = (String) request.getSession().getAttribute("email");

            if (email == null) {
                logger.info("Inside HistoryController->name not found->redirect to login");
                return new ModelAndView("redirect:/login");
            } else {
                logger.info("Inside HistoryController->name found->go to expense");
                return new ModelAndView("dashboard");
            }
        } catch (Exception e) {
            logger.error("History Controller->Error occurred : {}", e.getMessage());
            return new ModelAndView("redirect:/login");
        }
    }

    @PostMapping("/historyDetails")
    @ResponseBody
    public ResponseEntity<List<HistoryModel>> getDashboardData(@RequestParam("postPageNo") String pageNumber, HttpServletRequest request) {
        try {
            List<HistoryModel> data = historyService.getHistory(pageNumber,request);
            logger.info("History Controller->Dashboard data");
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            logger.error("History Controller->Error occurred in historyDetails: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/expenseHistory")
    @ResponseBody
    public ResponseEntity<List<ExpenseModel>> getExpenseHistory(HttpServletRequest request) {
        try {
            List<ExpenseModel> data = historyService. getExpenseDetails(request);
            logger.info("History Controller->expenseHistory");
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            logger.error("History Controller->Error occurred in expenseHistory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getRecords")
    @ResponseBody
    public ResponseEntity<Long> getRecordsCount(HttpServletRequest request) {
        try {
            Long count  = historyService. getRecordsCount(request);
            logger.info("History Controller->getRecords");
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("History Controller->Error occurred in getRecords: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




}
