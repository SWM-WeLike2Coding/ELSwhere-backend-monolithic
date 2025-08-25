package com.wl2c.elswhere.domain.product.model.entity

import com.wl2c.elswhere.global.base.BaseEntity
import jakarta.persistence.*

@Entity
class ProductTickerSymbol(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker_symbol_id")
    var tickerSymbol: TickerSymbol

): BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_ticker_symbol_id")
    val id: Long? = null

    @Column(nullable = false)
    var equityName: String = tickerSymbol.equityName

    @OneToOne(mappedBy = "productTickerSymbol", cascade = [CascadeType.ALL], orphanRemoval = true)
    @PrimaryKeyJoinColumn
    val productEquityVolatility: ProductEquityVolatility? = null

}