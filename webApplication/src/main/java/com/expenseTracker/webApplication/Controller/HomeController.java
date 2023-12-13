package com.expenseTracker.webApplication.Controller;

import com.expenseTracker.webApplication.Entities.User;
import com.expenseTracker.webApplication.Models.GroupModel;
import com.expenseTracker.webApplication.Models.UserGroupModel;

import com.expenseTracker.webApplication.Services.HomeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    HomeService homeService;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/home")
    public ModelAndView homePage(Model model,HttpServletRequest session) {
        try {
            if ((String) session.getSession().getAttribute("email") == null) {
                logger.info("Home Controller->Inside Home->name not found->redirect to login");
                return new ModelAndView("redirect:/login");
            } else {
                String name = (String) session.getSession().getAttribute("email");
                logger.info("Home Controller->Inside GetMapping->Home");
                model.addAttribute("email", name);
                return new ModelAndView("home");
            }
        } catch (Exception e) {
            logger.error("Home Controller->Error occurred in homePage: {}", e.getMessage());
            return new ModelAndView("redirect:/login");
        }
    }

    @GetMapping("/dashboard")
    @ResponseBody
    public ResponseEntity<List<Object>> getDashboardData(Model model,HttpServletRequest session) {
        try {
            List<Object> data = homeService.getUSerData(session.getSession().getAttribute("email").toString());
            logger.info("Home Controller->Dashboard data");
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            logger.error("Home Controller->Error occurred in getDashboardData: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user-lists")
    @ResponseBody
    public ResponseEntity<List<User>> getUsers(Model model, HttpServletRequest session) {
        try {
            List<User> userList = homeService.findUsers(session.getSession().getAttribute("email").toString());
            logger.info("Home Controller->Users List data for dropDown");
            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            logger.error("Home Controller->Error occurred in users-lists Data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }




    @PostMapping("/user-group-lists")
    @ResponseBody
    public ResponseEntity<List<UserGroupModel>> getUsersGroup(@RequestParam("postPageNo") String pageNumber,Model model, HttpServletRequest session) {
        try {
            String name = (String) session.getSession().getAttribute("email");
            List<UserGroupModel> userGroupList = homeService.findUserGroup(pageNumber,name);
            logger.info("Home Controller->Groups of individual user");
            return ResponseEntity.ok(userGroupList);
        } catch (Exception e) {
            logger.error("Home Controller->Error occurred in use-groupLists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
    @GetMapping("/getTotalUserRecords")
    @ResponseBody
    public ResponseEntity<Long> getRecordsCount(HttpServletRequest request) {
        try {
            Long count  = homeService. getRecordsCount(request);
            logger.info("Home Controller->getRecords");
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Home Controller->Error occurred in getRecords: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/group-lists")
    @ResponseBody
    public ResponseEntity<List<GroupModel>> getGroups(Model model, HttpServletRequest session) {
        try {
            String name = (String) session.getSession().getAttribute("email");
            List<GroupModel> userGroupList = homeService.findGroups(name);
            logger.info("Home Controller->Groups of user");
            return ResponseEntity.ok(userGroupList);
        } catch (Exception e) {
            logger.error("Home Controller->Error occurred in groupLists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    @PostMapping("/deleteUserGroup")
    public ModelAndView deleteGroup(@RequestParam("postData") String postData, Model model) {
        try {
            //System.out.println(postData);
            logger.info("before delete");
            homeService.deleteGroup(Long.parseLong(postData));
            logger.info("Home Controller->after delete");
            return new ModelAndView("redirect:/home");
        } catch (Exception e) {
            logger.error("Home Controller->Error occurred in deleting User group: {}", e.getMessage());
            return new ModelAndView("redirect:/login");
        }
    }


    @GetMapping("/logout")
    public ModelAndView logoutUser(HttpServletRequest session) {
        try {
            String email = (String) session.getSession().getAttribute("email");
            //System.out.println("email:"+email);
            logger.info("Home Controller->Logging out");
            HttpSession session1 = session.getSession(false);// this will give current session
            session1.invalidate();
            // System.out.println( "after invalidate"+  (String) session.getSession().getAttribute("email"));
            return new ModelAndView("redirect:/login");
        } catch (Exception e) {
            logger.error("Home Controller->Error occurred in logout: {}", e.getMessage());
            return new ModelAndView("redirect:/login"); // Redirect to login page on error
        }

    }


}
