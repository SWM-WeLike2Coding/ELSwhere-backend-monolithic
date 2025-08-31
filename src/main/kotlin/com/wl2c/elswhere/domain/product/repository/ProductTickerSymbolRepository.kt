package com.wl2c.elswhere.domain.product.repository

import com.wl2c.elswhere.domain.product.model.entity.ProductTickerSymbol
import org.springframework.data.jpa.repository.JpaRepository

interface ProductTickerSymbolRepository: JpaRepository<ProductTickerSymbol, Long> {
}