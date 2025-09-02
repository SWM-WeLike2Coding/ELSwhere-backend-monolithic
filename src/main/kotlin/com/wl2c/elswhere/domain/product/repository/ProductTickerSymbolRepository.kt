package com.wl2c.elswhere.domain.product.repository

import com.wl2c.elswhere.domain.product.model.entity.ProductTickerSymbol
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductTickerSymbolRepository: JpaRepository<ProductTickerSymbol, Long> {

    @Query("select p from ProductTickerSymbol p " +
            "where p.product.id = :productId ")
    fun findAllByProductId(@Param("productId") productId: Long): List<ProductTickerSymbol>
}