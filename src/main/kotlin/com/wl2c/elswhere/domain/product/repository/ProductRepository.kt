package com.wl2c.elswhere.domain.product.repository

import com.wl2c.elswhere.domain.product.model.entity.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long> {

    @Query("select p from Product p " +
            "where p.productState = 'ACTIVE' and p.id in :productIdList ")
    fun listByIds(@Param("productIdList") productIdList: List<Long>): List<Product>

    @Query("select p from Product p " +
            "where p.productState = 'ACTIVE' and p.subscriptionEndDate >= CURRENT_DATE " +
            "order by case when :sortType = 'knock-in' and p.knockIn is null then 1 else 0 end ")
    fun listByOnSale(@Param("sortType") sortType: String, pageable: Pageable): Page<Product>

    @Query("select p from Product p " +
            "where p.productState = 'ACTIVE' and p.subscriptionEndDate < CURRENT_DATE " +
            "order by case when :sortType = 'knock-in' and p.knockIn is null then 1 else 0 end ")
    fun listByEndSale(@Param("sortType") sortType: String, pageable: Pageable): Page<Product>

    @Query("select p from Product p " +
            "where p.productState = 'ACTIVE' and p.subscriptionEndDate >= CURRENT_DATE " +
            "and p.type = 'STEP_DOWN' ")
    fun listByOnSaleAndStepDown(): List<Product>

    @Query("select p from Product p where p.productState = 'ACTIVE' and function('date', p.createdAt) = CURRENT_DATE ")
    fun listByCreatedAtToday(): List<Product>

    @Query("select p from Product p " +
            "where p.productState = 'ACTIVE' " +
            "and p.id = :id " +
            "and p.subscriptionEndDate >= CURRENT_DATE")
    fun isItProductOnSale(@Param("id") id: Long): Product?

    @Query("select p from Product p where p.productState = 'ACTIVE' and p.id = :id ")
    fun findOne(@Param("id") id: Long): Product?

    @Query("select p from Product p " +
            "join p.productTickerSymbols subpts on subpts.product.id = p.id " +
            "join subpts.tickerSymbol ts on ts.id = subpts.tickerSymbol.id " +
            "where p.productState = 'ACTIVE' " +
            "and p.subscriptionEndDate >= CURRENT_DATE " +
            "and p.id <> :targetId " +
            "and p.equityCount = :targetEquityCount " +
            "and ts.tickerSymbol IN :targetEquityTickerSymbols " +
            "group by p.id " +
            "having count(subpts.id) = :targetEquityCount ")
    fun findComparisonResults(@Param("targetId") targetId: Long,
                              @Param("targetEquityCount") targetEquityCount: Int,
                              @Param("targetEquityTickerSymbols") targetEquityTickerSymbols: List<String>): List<Product>
}