package com.expenseTracker.webApplication.Repositories;

import com.expenseTracker.webApplication.Entities.Groups;
import com.expenseTracker.webApplication.Entities.User;
import com.expenseTracker.webApplication.Models.UserGroupModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HomeRepo extends JpaRepository<Groups, Long> {


    @Query(value = "SELECT g.groups_id, u.user_id, g.group_name " +
            "FROM  tracker_user u , group_members gm, group_table g " +
            "WHERE gm.group_id = g.groups_id " +
            "AND gm.user_id = u.user_id  AND gm.member_status!='removed'" +
            "AND g.group_type = :groupTypeParam " +
            "AND u.email_id = :emailParam AND g.group_status ='active' LIMIT :offset, :pageSize" ,nativeQuery = true)
    List<Object[]> findByMembersUserEmailAndGroupType(@Param("emailParam") String email, @Param("groupTypeParam") Integer groupType, @Param("offset") Long offset, @Param("pageSize") Integer pageSize);

    @Query(value = "SELECT g.groupId, u.id, g.groupName " +
            "FROM  User u , GroupMembers gm, Groups g " +
            "WHERE gm.groupId = g.groupId " +
            "AND gm.userId = u.id  AND gm.memberStatus!='removed'" +
            "AND g.groupType = :groupTypeParam " +
            "AND u.email = :emailParam AND g.groupStatus ='active'")
    List<Object[]> findByMembersUserEmailAndGroupTypeGroups(@Param("emailParam") String email, @Param("groupTypeParam") Integer groupType);
    @Query(value = "SELECT count(*) " +
            " FROM  User u , GroupMembers gm, Groups g " +
            " WHERE gm.groupId = g.groupId " +
            " AND gm.userId = u.id  AND gm.memberStatus!='removed'" +
            " AND g.groupType = :groupTypeParam " +
            " AND u.email = :emailParam AND g.groupStatus ='active' ")
    Long findAllUserRecordCountById(@Param("emailParam") String email, @Param("groupTypeParam") Integer groupType);
    //find groupId, firstName for given email to display in dashboard for individual users
    @Query("SELECT u.firstName FROM User u " +
            "WHERE u.id = (SELECT m.userId FROM GroupMembers m " +
            " WHERE m.groupId = :groupIdParam " +
            "AND m.userId != :userIDParam)")
    String findByGroupId(@Param("groupIdParam") Long groupId, @Param("userIDParam") Long userID);
    //find the userName to display on dashboard for individual users

    @Query("SELECT SUM(e.amount) FROM Expenses e " +
            "WHERE e.groupId = :groupId " +
            "AND e.payerUserId = :userID " +
            "AND e.payeeUserId != :userID " +
            "AND e.expenseStatus = 'not settled'")
    Float getPendingMoneyForGroupAndEmail(Long groupId, Long userID);
    //get sum of all pending money for specific groupId and userID


    @Query("SELECT SUM(e.amount) FROM Expenses e " +
            "WHERE e.groupId = :groupId " +
            "AND e.payeeUserId = :userID " +
            "AND e.payerUserId != :userID " +
            "AND e.expenseStatus = 'not settled'")
    Float getLentMoneyForGroupAndEmail(Long groupId, Long userID);
    //get sum of all lent money for specific groupId and userID


    @Query("SELECT SUM(e.amount) FROM Expenses e " +
            "WHERE e.payerUserId = :userID " +
            "AND e.payeeUserId != :userID " +
            "AND e.expenseStatus = 'not settled'")
    Float getPendingMoneyForUserId(Long userID);
    //get sum of all pending money for specific userID


    @Query("SELECT SUM(e.amount) FROM Expenses e " +
            "WHERE e.payeeUserId = :userID " +
            "AND e.payerUserId != :userID " +
            "AND e.expenseStatus = 'not settled'")
    Float getLentMoneyForUserId(Long userID);

    //get sum of all lent money for specific userID
    @Query(value = "SELECT g.groups_id from group_table g " +
            " WHERE group_name=:groupName AND user_id=:userIDParam OR " +
            " group_name=:userIDParam AND user_id=:groupName", nativeQuery = true)
    Long getGroupId(@Param("groupName") Long groupName, @Param("userIDParam") Long userID);

    //to get groupId for given groupName and userID
    @Modifying
    @Query(value = "UPDATE group_table set group_status='deleted' where groups_id=:groupId", nativeQuery = true)
    @Transactional
    void updateGroupTable(Long groupId);

    //update group_table status to deleted


    @Query(value="SELECT * from group_table where groups_id=:groupId AND user_id=:userId",nativeQuery = true)
    Groups findCreatorByGroupId(@Param("groupId") Long groupId, @Param("userId") Long userId);


    //update group_members status to deleted
    @Modifying
    @Query(value = "UPDATE expenses set expense_status='deleted' where group_id=:groupId", nativeQuery = true)
    @Transactional
    void updateExpenses(Long groupId);
    //update expenses status to deleted

    @Query(value="SELECT * from group_table where groups_id=:groupId",nativeQuery = true)
    Groups findTypeByGroupId(Long groupId);

    @Query(value="SELECT group_type from group_table where groups_id=:groupId",nativeQuery = true)
    Integer findGroupType(Long groupId);

    @Query("SELECT SUM(e.amount) FROM Expenses e " +
            "WHERE e.payerUserId = :userID " +
            "AND e.payeeUserId != :userID " +
            "AND e.expenseStatus = 'not settled' and "+
            " e.dateTime >= :startDateTime and "+
            " e.dateTime < :endDateTime")
    Float getPendingMoneyForUserIdWeekly(@Param("userID") Long userID, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
    //get sum of all pending money for specific userID


    @Query("SELECT SUM(e.amount) FROM Expenses e " +
            "WHERE e.payeeUserId = :userID " +
            "AND e.payerUserId != :userID " +
            "AND e.expenseStatus = 'not settled' and "+
            " e.dateTime >= :startDateTime and "+
            " e.dateTime < :endDateTime")
    Float getLentMoneyForUserIdWeekly(@Param("userID") Long userID, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query(value="SELECT tu.first_name from tracker_user tu , group_table gt "+
            " where gt.user_id =tu.user_id and gt.groups_id =:groupId",nativeQuery = true)
    String findGroupCreatorName(@Param("groupId") Long groupId);
//    @Query(nativeQuery = true, value = "SELECT " +
//            "g.group_name, " +
//            "SUM(e.amount) AS total_amount, " +
//            "GROUP_CONCAT(e.amount) AS individual_amounts, " +
//            "GROUP_CONCAT(e.expense_name) AS expense_names " +
//            "FROM group_table g " +
//            "JOIN expenses e ON g.groups_id = e.group_id " +
//            "JOIN group_members gm ON g.groups_id = gm.group_id " +
//            "JOIN tracker_user u ON gm.user_id = u.user_id " +
//            "WHERE u.email_id = :email " +
//            "GROUP BY g.group_name")

    //List<Object[]> getAllGroupExpenses(String email);
    @Query(value = "SELECT  tu.email_id,tu.first_name, tu.user_id FROM tracker_user tu, group_members gm " +
            "WHERE gm.user_id = tu.user_id AND gm.group_id = :groupId AND gm.user_id != :userId AND gm.member_status!='removed'",
            nativeQuery = true)
    List<Object[]> findGroupMemberNames(@Param("groupId") Long groupId, @Param("userId") Long userId);


    @Query(value = "SELECT  tu.first_name , tu.email_id  from tracker_user tu where "+
            " user_id not in (select user_id from group_members " +
            "where group_id=:groupId and member_status!='removed')", nativeQuery = true)
    List<Object[]>  findNonGroupMemberNames(@Param("groupId") Long groupId);

    @Query(value="SELECT group_name from group_table where groups_id=:groupId", nativeQuery = true)
    String findGroupName(@Param("groupId") Long groupId);




    @Query(value="SELECT tu.first_name , tu.user_id , gt.group_name from tracker_user tu ,group_table gt "+
            " where tu.user_id =gt.user_id and gt.groups_id =:groupId",nativeQuery = true)
    List<Object> findDetailsBy(@Param("groupId") Long groupId);

    @Query(value="SELECT tu.first_name, tu.user_id FROM tracker_user tu, group_members gm "+
    " WHERE gm.user_id = tu.user_id AND gm.group_id =:groupId AND gm.user_id !=:userId AND gm.member_status !='removed'",nativeQuery = true)

    List<Object[]> findMemberDetails(@Param("groupId") Long groupId, @Param("userId") Long userId);





}



