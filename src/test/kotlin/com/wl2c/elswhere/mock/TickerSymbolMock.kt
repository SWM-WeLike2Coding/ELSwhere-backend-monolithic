package com.wl2c.elswhere.mock

import com.wl2c.elswhere.domain.product.model.UnderlyingAssetType
import com.wl2c.elswhere.domain.product.model.entity.TickerSymbol

object TickerSymbolMock {

    fun create(
        tickerSymbol: String = "005930.KS",
        equityName: String = "삼성전자",
        underlyingAssetType: UnderlyingAssetType = UnderlyingAssetType.STOCK
    ): TickerSymbol {
        return TickerSymbol(
            tickerSymbol = tickerSymbol,
            equityName = equityName,
            underlyingAssetType = underlyingAssetType
        )
    }
}