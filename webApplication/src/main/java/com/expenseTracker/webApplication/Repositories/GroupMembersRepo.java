package com.expenseTracker.webApplication.Repositories;

import com.expenseTracker.webApplication.Entities.GroupMembers;
import com.expenseTracker.webApplication.Entities.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GroupMembersRepo extends JpaRepository<GroupMembers, Long> {


    @Modifying
    @Query(value = "UPDATE group_members set member_status='settled' where group_id=:groupId AND user_id=:userId", nativeQuery = true)
    @Transactional

    void updateStatus(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Query(value="SELECT * from group_members where group_id=:groupId AND user_id=:userId",nativeQuery = true)
    GroupMembers findByGroupIdUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Modifying
    @Query(value = "UPDATE group_members set member_status='removed' where group_id=:groupId AND user_id=:userId", nativeQuery = true)
    @Transactional

    void removeStatus(@Param("groupId") Long groupId,@Param("userId") Long userId);

    @Modifying
    @Query(value = "UPDATE group_members set member_status='deleted' where group_id=:groupId", nativeQuery = true)
    @Transactional
    void updateGroupMembers(Long groupId);
}
