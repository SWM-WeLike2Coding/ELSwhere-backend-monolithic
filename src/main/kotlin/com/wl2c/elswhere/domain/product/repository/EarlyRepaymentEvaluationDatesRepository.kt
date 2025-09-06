package com.wl2c.elswhere.domain.product.repository

import com.wl2c.elswhere.domain.product.model.entity.EarlyRepaymentEvaluationDates
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface EarlyRepaymentEvaluationDatesRepository : JpaRepository<EarlyRepaymentEvaluationDates, Long> {

    @Query("select count(e) + 1 from EarlyRepaymentEvaluationDates e " +
            "where e.product.id = :productId " +
            "and e.earlyRepaymentEvaluationDate < :targetDate ")
    fun findNextEarlyRepaymentEvaluationDateOrder(@Param("productId") productId: Long,
                                                  @Param("targetDate") targetDate: LocalDate): Int

    @Query("select e.earlyRepaymentEvaluationDate from EarlyRepaymentEvaluationDates e " +
            "where e.product.productState = 'ACTIVE' " +
            "and e.product.id = :productId " +
            "and e.earlyRepaymentEvaluationDate > CURRENT_DATE " +
            "order by e.earlyRepaymentEvaluationDate asc " +
            "LIMIT 1 ")
    fun findNextEarlyRepaymentEvaluationDate(@Param("productId") productId: Long): LocalDate?

    @Query("select e from EarlyRepaymentEvaluationDates e " +
            "where e.product.productState = 'ACTIVE' " +
            "and e.product.id = :productId ")
    fun findAllEarlyRepaymentEvaluationDate(@Param("productId") productId: Long): List<EarlyRepaymentEvaluationDates>

}