package com.wl2c.elswhere.domain.product.model.entity

import com.wl2c.elswhere.global.base.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
class ProductEquityVolatility(

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_equity_volatility_id")
    var productTickerSymbol: ProductTickerSymbol,

    @Column(nullable = false, scale = 16)
    var volatility: BigDecimal

): BaseEntity() {

    @Id
    @Column(name = "product_equity_volatility_id")
    val id: Long? = null

}