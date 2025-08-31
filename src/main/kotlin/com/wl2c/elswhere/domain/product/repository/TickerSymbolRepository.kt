package com.wl2c.elswhere.domain.product.repository

import com.wl2c.elswhere.domain.product.model.entity.TickerSymbol
import org.springframework.data.jpa.repository.JpaRepository

interface TickerSymbolRepository: JpaRepository<TickerSymbol, Long> {
}