package com.wl2c.elswhere.domain.product.model.entity

import com.wl2c.elswhere.domain.product.model.MaturityEvaluationDateType
import com.wl2c.elswhere.domain.product.model.ProductState
import com.wl2c.elswhere.domain.product.model.ProductType
import com.wl2c.elswhere.domain.product.model.UnderlyingAssetType
import com.wl2c.elswhere.global.base.BaseEntity
import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.hibernate.annotations.ColumnDefault
import java.math.BigDecimal
import java.time.LocalDate

@Entity
class Product(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    val id: Long? = null,

    @Column(nullable = false)
    var issuer: String,

    @Column(nullable = false)
    var name: String,

    var issueNumber: Int?,

    @Column(nullable = false)
    var equities: String,

    var equityCount: Int,

    var knockIn: Int?,

    @Column(nullable = false)
    var issuedDate: LocalDate,

    @Column(nullable = false)
    var maturityEvaluationDate: LocalDate,

    @Column(nullable = false)
    @ColumnDefault("'UNKNOWN'")
    @Enumerated(EnumType.STRING)
    var maturityEvaluationDateType: MaturityEvaluationDateType,

    @Column(nullable = false)
    var maturityDate: LocalDate,

    @Column(nullable = false, precision = 8, scale = 5)
    var yieldIfConditionsMet: BigDecimal,

    @Column(nullable = false, precision = 8, scale = 5)
    var maximumLossRate: BigDecimal,

    @Column(nullable = false)
    var subscriptionStartDate: LocalDate,

    @Column(nullable = false)
    var subscriptionEndDate: LocalDate,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: ProductType,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var underlyingAssetType: UnderlyingAssetType,

    @Column(nullable = false)
    var productFullInfo: String,

    var productInfo: String?,

    @Column(nullable = false)
    var link: String,

    @Column(nullable = false)
    var remarks: String,

    var summaryInvestmentProspectusLink: String?,

    var earlyRepaymentEvaluationDates: String?,

    var volatilites: String?,

    var initialBasePriceEvaluationDate: LocalDate?,

    @Column(nullable = false)
    @ColumnDefault("'INACTIVE'")
    @Enumerated(EnumType.STRING)
    var productState: ProductState,

): BaseEntity() {

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    val productTickerSymbols: MutableList<ProductTickerSymbol> = mutableListOf()

    fun setInActiveProductState() {
        productState = ProductState.INACTIVE
    }

    fun setActiveProductState() {
        productState = ProductState.ACTIVE
    }

}