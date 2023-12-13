package com.expenseTracker.webApplication.Repositories;


import com.expenseTracker.webApplication.Entities.History;
import com.expenseTracker.webApplication.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HistoryRepo extends JpaRepository<History, Long> {
    @Query(value = "SELECT * FROM history WHERE user_id = :userId or created_user_id=:userId " +
            "ORDER BY date_time DESC LIMIT :offset, :pageSize", nativeQuery = true)
    List<History> findAllHistoryById(@Param("userId") Long userId,@Param("offset") Long offset, @Param("pageSize") Integer pageSize);


    @Query(value = "SELECT count(*) FROM history WHERE user_id = :userId or created_user_id=:userId ",nativeQuery = true)
    Long findAllHistoryById(@Param("userId") Long userId);
    @Query(value= "SELECT g.group_name , g.groups_id, "+
            " SUM(CASE WHEN e.payer_user_id  =:userId THEN e.amount ELSE 0 END) AS paidMoney, "+
            " SUM(CASE WHEN e.payee_user_id  =:userId THEN e.amount ELSE 0 END)AS lentMoney "+
            " FROM expenses e, group_table g "+
            " WHERE e.group_id = g.groups_id AND e.expense_status  = 'not settled' "+
            " AND (:userId IN (e.payer_user_id , e.payee_user_id)) GROUP BY g.group_name ",nativeQuery = true)
    List<Object[]> findExpenseDetails(@Param("userId") Long userId);

    @Query(value="SELECT tu.first_name " +
            " FROM tracker_user tu " +
            " WHERE tu.user_id IN (SELECT gm.user_id from group_members gm "+
            " where gm.group_id =:groupId and gm.user_id !=:userId) ",nativeQuery = true)
    String findUserName(@Param("groupId") Long groupId, @Param("userId") Long userId);
}


