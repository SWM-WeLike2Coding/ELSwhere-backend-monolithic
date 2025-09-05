package com.wl2c.elswhere.domain.product.repository

import com.wl2c.elswhere.domain.product.model.entity.TickerSymbol
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TickerSymbolRepository: JpaRepository<TickerSymbol, Long> {
    @Query("select ts from TickerSymbol ts " +
            "inner join ProductTickerSymbol pts " +
            "on ts.id = pts.tickerSymbol.id " +
            "where pts.product.id = :productId and pts.product.productState = 'ACTIVE' ")
    fun findTickerSymbolList(@Param("productId") productId: Long): List<TickerSymbol>

    @Query("select ts from TickerSymbol ts " +
            "where ts.tickerSymbol <> 'NEED_TO_CHECK' ")
    override fun findAll(): List<TickerSymbol>
}