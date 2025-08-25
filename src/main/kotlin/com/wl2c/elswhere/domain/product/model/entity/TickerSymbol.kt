package com.wl2c.elswhere.domain.product.model.entity

import com.wl2c.elswhere.domain.product.model.UnderlyingAssetType
import com.wl2c.elswhere.global.base.BaseEntity
import jakarta.persistence.*

@Entity
class TickerSymbol(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticker_symbol_id")
    val id: Long? = null,

    @Column(nullable = false)
    var tickerSymbol: String,

    @Column(nullable = false)
    var equityName: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var underlyingAssetType: UnderlyingAssetType,

    ): BaseEntity() {

    @OneToMany(mappedBy = "tickerSymbol", cascade = [CascadeType.ALL], orphanRemoval = true)
    val productTickerSymbols: MutableList<ProductTickerSymbol> = mutableListOf()

}