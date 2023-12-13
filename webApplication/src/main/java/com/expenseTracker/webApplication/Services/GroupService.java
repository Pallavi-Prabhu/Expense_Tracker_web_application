package com.expenseTracker.webApplication.Services;

import com.expenseTracker.webApplication.Controller.LoginController;
import com.expenseTracker.webApplication.Entities.GroupMembers;
import com.expenseTracker.webApplication.Entities.Groups;
import com.expenseTracker.webApplication.Entities.History;
import com.expenseTracker.webApplication.Entities.User;
import com.expenseTracker.webApplication.Repositories.GroupMembersRepo;
import com.expenseTracker.webApplication.Repositories.HistoryRepo;
import com.expenseTracker.webApplication.Repositories.HomeRepo;
import com.expenseTracker.webApplication.Repositories.LoginRepo;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class GroupService {
    @Autowired
    LoginRepo loginRepo;
    @Autowired
    HomeRepo homeRepo;
    @Autowired
    GroupMembersRepo groupMembersRepo;
    @Autowired
    HistoryService historyService;
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);

    public void addGroupDetails(String email, String groupName, List<String> selectedOptions) {
        try {
            logger.info("Group Serive->Inside add group Details");
            User user = loginRepo.findByEmailId(email);
            Long id = user.getId();
            Groups groups = new Groups();
            groups.setGroupName(groupName);
            groups.setGroupStatus("active");
            groups.setUserId(id);
            groups.setGroupType(0);
            homeRepo.save(groups);

            Long grpId = groups.getGroupId();
            List<String> selectedOption = selectedOptions;
            selectedOption.add(id.toString());
            for (String opt : selectedOption) {
                GroupMembers groupMembers = new GroupMembers();
                groupMembers.setUserId(Long.parseLong(opt.toString()));
                groupMembers.setMemberStatus("settled");
                groupMembers.setGroupId(grpId);
                groupMembersRepo.save(groupMembers);
                if(Long.parseLong(opt.toString())!= user.getId()) {
                    User uuser = loginRepo.findByUserId(Long.parseLong(opt.toString()));
                    historyService.addToHistory("added", null, user.getId(), user.getFirstName(),
                            LocalDateTime.now(), null,groupName, Long.parseLong(opt.toString()), uuser.getFirstName());

                }
            }
        } catch (Exception e) {
            logger.error("Group service->Error occurred in addGroupDetails: {}", e.getMessage());
            throw new RuntimeException("Group service->Error occurred while adding group details");
        }

    }
}
