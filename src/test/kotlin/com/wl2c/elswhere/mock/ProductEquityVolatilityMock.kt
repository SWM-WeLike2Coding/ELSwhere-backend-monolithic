package com.wl2c.elswhere.mock

import com.wl2c.elswhere.domain.product.model.entity.ProductEquityVolatility
import com.wl2c.elswhere.domain.product.model.entity.ProductTickerSymbol
import java.math.BigDecimal

object ProductEquityVolatilityMock {

    fun create(
        productTickerSymbol: ProductTickerSymbol,
        volatility: BigDecimal = BigDecimal("0.25")
    ): ProductEquityVolatility {
        return ProductEquityVolatility(
            productTickerSymbol = productTickerSymbol,
            volatility = volatility
        )
    }
}