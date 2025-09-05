package com.wl2c.elswhere.domain.product.repository

import com.wl2c.elswhere.domain.product.model.entity.ProductEquityVolatility
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductEquityVolatilityRepository: JpaRepository<ProductEquityVolatility, Long> {

    @Query("select p from ProductEquityVolatility p " +
            "where p.id in :productTickerSymbolIdList ")
    fun findAllByProductTickerSymbol(@Param("productTickerSymbolIdList") productTickerSymbolIdList: List<Long>): List<ProductEquityVolatility>
}