package com.expenseTracker.webApplication.Controller;

import com.expenseTracker.webApplication.Services.GroupService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class GroupController {
    @Autowired
    GroupService groupService;

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @PostMapping("/addGroup")
    @ResponseBody
    public void addGroup(@RequestBody Map<String, Object> requestMap, HttpServletRequest session) {
        try {
            String groupName = (String) requestMap.get("groupName");
            List<String> selectedOptions = (List<String>) requestMap.get("selectedOptions");
            String email = (String) session.getSession().getAttribute("email");
            logger.info("Group Controller->Add group Post mapping");
            groupService.addGroupDetails(email, groupName, selectedOptions);
        } catch (Exception e) {

            logger.error("Group Controller->An error occurred while processing the /addGroup request:", e);
        }
    }


}
