package com.wl2c.elswhere.mock

import com.wl2c.elswhere.domain.product.model.MaturityEvaluationDateType
import com.wl2c.elswhere.domain.product.model.ProductState
import com.wl2c.elswhere.domain.product.model.ProductType
import com.wl2c.elswhere.domain.product.model.UnderlyingAssetType
import com.wl2c.elswhere.domain.product.model.entity.Product
import com.wl2c.elswhere.util.injectId
import java.math.BigDecimal
import java.time.LocalDate

object ProductMock {

    fun create(
        issuer: String = "AA증권",
        name: String = "1호",
        issueNumber: Int? = 12345,
        equities: String = "삼성전자 / S&P500 / KOSPI200",
        equityCount: Int = 3,
        knockIn: Int? = 45,
        issuedDate: LocalDate = LocalDate.now().minusDays(1),
        maturityDate: LocalDate = LocalDate.now().plusYears(3),
        maturityEvaluationDate: LocalDate = LocalDate.now().plusYears(3).minusDays(5),
        subscriptionStartDate: LocalDate = LocalDate.now().minusDays(14),
        subscriptionEndDate: LocalDate = LocalDate.now().minusDays(1),
        initialBasePriceEvaluationDate: LocalDate? = null,
        maturityEvaluationDateType: MaturityEvaluationDateType = MaturityEvaluationDateType.SINGLE,
        yieldIfConditionsMet: BigDecimal = BigDecimal("10.423"),
        maximumLossRate: BigDecimal = BigDecimal("100.00"),
        type: ProductType = ProductType.STEP_DOWN,
        underlyingAssetType: UnderlyingAssetType = UnderlyingAssetType.MIX,
        productFullInfo: String = "",
        productInfo: String? = "95-90-85-80-75-50",
        link: String = "",
        remarks: String = "",
        summaryInvestmentProspectusLink: String? = "",
        earlyRepaymentEvaluationDates: String? = "",
        volatilites: String? = "",
        productState: ProductState = ProductState.ACTIVE
    ): Product {
        val product = Product(
            issuer = issuer,
            name = name,
            issueNumber = issueNumber,
            equities = equities,
            equityCount = equityCount,
            knockIn = knockIn,
            issuedDate = issuedDate,
            maturityDate = maturityDate,
            maturityEvaluationDate = maturityEvaluationDate,
            maturityEvaluationDateType = maturityEvaluationDateType,
            yieldIfConditionsMet = yieldIfConditionsMet,
            maximumLossRate = maximumLossRate,
            subscriptionStartDate = subscriptionStartDate,
            subscriptionEndDate = subscriptionEndDate,
            type = type,
            underlyingAssetType = underlyingAssetType,
            productFullInfo = productFullInfo,
            productInfo = productInfo,
            link = link,
            remarks = remarks,
            summaryInvestmentProspectusLink = summaryInvestmentProspectusLink,
            earlyRepaymentEvaluationDates = earlyRepaymentEvaluationDates,
            volatilites = volatilites,
            initialBasePriceEvaluationDate = initialBasePriceEvaluationDate,
            productState = productState
        )

        return product
    }
}