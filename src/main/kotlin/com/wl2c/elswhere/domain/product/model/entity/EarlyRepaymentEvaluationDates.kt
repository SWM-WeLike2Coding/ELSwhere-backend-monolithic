package com.wl2c.elswhere.domain.product.model.entity

import com.wl2c.elswhere.global.base.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class EarlyRepaymentEvaluationDates(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: Product,

    @Column(nullable = false)
    var earlyRepaymentEvaluationDate: LocalDate

): BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "early_repayment_evaluation_dates_id")
    val id: Long? = null

}