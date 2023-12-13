package com.expenseTracker.webApplication.Repositories;

import com.expenseTracker.webApplication.Entities.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepo extends JpaRepository<Expenses, Long> {

    @Query(value = "SELECT gm.group_id FROM group_members gm, group_table g WHERE gm.group_id= g.groups_id "+
                    " and gm.user_id IN (:id1, :id2) and g.group_type=1 "+
                    " GROUP BY gm.group_id HAVING COUNT(DISTINCT gm.user_id) = 2", nativeQuery = true)
    Long searchGroupExists(@Param("id1") Long id1, @Param("id2") Long id2);

    //to get the userGroupId for the given two users
    @Modifying
    @Query(value = "UPDATE group_table set group_status='active' where groups_id=:groupId", nativeQuery = true)
    @Transactional
    void updateGroupStatus(@Param("groupId") Long groupId);
    //if userGroup status is not active then update to active after adding expenses

    @Query(value="select tu.user_id, tu.first_name  from "+
            " tracker_user tu, group_members gm  where tu.user_id=gm.user_id " +
            " and gm.group_id =:groupId and gm.member_status!='removed'",nativeQuery = true)
    List<Object> findNameIdByGroupId(@Param("groupId") Long groupId);


    @Modifying
    @Query(value = "UPDATE group_members gm SET gm.member_status = " +
            "CASE " +
            "   WHEN gm.user_id IN (" +
            "       SELECT e.payee_user_id FROM expenses e " +
            "       WHERE e.group_id = gm.group_id AND e.expense_status = 'not settled'" +
            "   ) THEN 'unsettled' " +
            "   WHEN gm.user_id IN (" +
            "       SELECT e.payee_user_id FROM expenses e " +
            "       WHERE e.group_id = gm.group_id AND e.expense_status = 'settled'" +
            "   ) THEN 'settled' " +
            "   ELSE gm.member_status " +
            "END " +
            "WHERE gm.group_id = :groupId", nativeQuery = true)
    @Transactional
    void updateGroupMembersStatus(@Param("groupId") Long groupId);
    //update group member status to settled if in expense table it is settled else update to unsettled

    @Query(value = "SELECT * from expenses WHERE group_id = :groupId " +
            "AND expense_status = 'not settled' AND payee_user_id=:userId", nativeQuery = true)
    List<Expenses> findDetailsBy (@Param("groupId") Long groupId, @Param("userId") Long userId);
    @Modifying
    @Query(value = "UPDATE expenses " +
            "SET expense_status = 'settled' " +
            "WHERE group_id = :groupId " +
            "AND expense_status = 'not settled' AND payee_user_id=:userId", nativeQuery = true)
    @Transactional
    void updateExpenseStatusToSettledIfUnsettledGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);
    //update to settled when clicked on settle all

    @Modifying
    @Query(value = "UPDATE group_members " +
            "SET member_status = 'settled' " +
            "WHERE group_id = :groupId " +
            "AND member_status = 'unsettled' AND user_id=:userId", nativeQuery = true)
    @Transactional
    void updateMemberStatusToSettledIfUnsettledGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);
    //even update group Members table to settled if clicked on settle all

    @Modifying
    @Query(value = "UPDATE expenses " +
            "SET expense_status = 'settled' " +
            "WHERE group_id = :groupId " +
            "AND expense_status = 'not settled'", nativeQuery = true)
    @Transactional
    void updateExpenseStatusToSettledIfUnsettled(@Param("groupId") Long groupId);

    @Modifying
    @Query(value = "UPDATE group_members " +
            "SET member_status = 'settled' " +
            "WHERE group_id = :groupId " +
            "AND member_status = 'unsettled'", nativeQuery = true)
    @Transactional
    void updateMemberStatusToSettledIfUnsettled(@Param("groupId") Long groupId);

    @Query(value = "SELECT e.expense_name, e.payer_user_id, sum(e.amount), e.date_time FROM expenses e " +
            "WHERE e.group_id = :groupId AND (e.expense_status = 'not settled' OR e.expense_status = 'settled') " +
            "GROUP BY e.expense_name, DATE(e.date_time), HOUR(e.date_time) " +
            "ORDER BY DATE(e.date_time) desc, HOUR(e.date_time) desc, MINUTE(e.date_time) desc, e.expense_name ", nativeQuery = true)
    List<Object[]> findByGroupId(@Param("groupId") Long groupId);
    //get expenseName, payer id, and total sum for each expense

    @Query(value = "SELECT " +
            " CASE " +
            " WHEN MAX(CASE WHEN e.expense_status = 'not settled' THEN 1 ELSE 0 END) = 1 THEN 0 " +
            " ELSE 1 END AS result" +
            " FROM expenses e WHERE e.group_id =:groupId AND e.expense_status != 'deleted' " +
            " GROUP BY e.expense_name, DATE(e.date_time), HOUR(e.date_time) " +
            " ORDER BY DATE(e.date_time) desc, HOUR(e.date_time) desc,MINUTE(e.date_time) desc,e.expense_name ", nativeQuery = true)
    List<Integer> findStatus(@Param("groupId") Long groupId);
    //grouping by expense if even one is not settled then return 0 else return 1 which means settled


    @Query(value = "SELECT e.payee_user_id, e.amount,e.expense_id,e.expense_status FROM "+
            " expenses e WHERE e.group_id = :groupId AND e.expense_name = :expenseName " +
            " AND e.date_time >= :startDateTime " +
            " AND e.date_time <=:endDateTime", nativeQuery = true)
    List<Object[]> findNameAmount(@Param("groupId") Long groupId, @Param("expenseName") String expenseName,
                                  @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
    //find each individual with his amount for given expense name and groupId


    @Modifying
    @Query(value = "DELETE from expenses where expense_id=:expenseId",nativeQuery = true)
    @Transactional
    void deleteByExpenseID(@Param("expenseId") Long expenseId);

    @Modifying
    @Query(value = "UPDATE expenses set expense_status='deleted' where expense_id=:expenseId", nativeQuery = true)
    @Transactional
    void deleteRowStatus (@Param("expenseId") Long expenseId);

    @Query(value = "SELECT tu.first_name, tu.user_id, e.expense_name ,e.amount  FROM tracker_user tu, expenses e " +
            " WHERE tu.user_id =e.payer_user_id and e.expense_id =:expenseId and e.payee_user_id !=e.payer_user_id ",nativeQuery = true)
    List<Object> findPayerDetailsBy(@Param("expenseId") Long expenseId);

    @Query(value="SELECT tu.first_name, tu.user_id  FROM tracker_user tu, expenses e " +
            " WHERE tu.user_id =e.payee_user_id and e.expense_id =:expenseId and e.payee_user_id !=e.payer_user_id ",nativeQuery = true)
    List<Object> findPayeeDetailsBy(@Param("expenseId") Long expenseId);



//    @Query(value = "select * from tracker_user where first_name=:name", nativeQuery = true)
//    User findUserByUserId (@Param("name") String name);

    //    @Modifying
//    @Query(value = "UPDATE group_members set member_status='unsettled' where group_id=:groupId AND user_id IN " +
//            "(SELECT payee_user_id from expenses where group_id =:groupId and expense_status ='not settled') ", nativeQuery = true)
//    @Transactional
//    void updateGroupMembersStatus(@Param("groupId") Long groupId);// ,@Param("userId") Long userId);
}
