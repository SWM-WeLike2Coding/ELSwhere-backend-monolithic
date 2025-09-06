package com.wl2c.elswhere.mock

import com.wl2c.elswhere.domain.product.model.entity.EarlyRepaymentEvaluationDates
import com.wl2c.elswhere.domain.product.model.entity.Product
import java.time.LocalDate

object EarlyRepaymentEvaluationDatesMock {
    fun create(
        product: Product,
        earlyRepaymentEvaluationDate: LocalDate
    ): EarlyRepaymentEvaluationDates {
        return EarlyRepaymentEvaluationDates(
            product = product,
            earlyRepaymentEvaluationDate = earlyRepaymentEvaluationDate
        )
    }

    fun createList(
        product: Product,
        earlyRepaymentEvaluationDates: List<LocalDate>
    ): List<EarlyRepaymentEvaluationDates> {
        return earlyRepaymentEvaluationDates.map { date ->
            create(product, date)
        }
    }
}