package com.wl2c.elswhere.mock

import com.wl2c.elswhere.domain.product.model.entity.Product
import com.wl2c.elswhere.domain.product.model.entity.ProductTickerSymbol
import com.wl2c.elswhere.domain.product.model.entity.TickerSymbol

object ProductTickerSymbolMock {

    fun create(
        product: Product,
        tickerSymbol: TickerSymbol,
    ): ProductTickerSymbol {
        val productTickerSymbol = ProductTickerSymbol(
            product = product,
            tickerSymbol = tickerSymbol
        )

        return productTickerSymbol
    }

    fun createList(
        product: Product,
        tickerSymbols: List<TickerSymbol>
    ): List<ProductTickerSymbol> {
        return tickerSymbols.map { ticker ->
            create(product, ticker)
        }
    }
}