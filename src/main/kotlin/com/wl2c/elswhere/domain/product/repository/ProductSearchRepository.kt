package com.wl2c.elswhere.domain.product.repository

import com.querydsl.core.Tuple
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberTemplate
import com.querydsl.core.types.dsl.StringTemplate
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wl2c.elswhere.domain.product.model.ProductState
import com.wl2c.elswhere.domain.product.model.ProductType
import com.wl2c.elswhere.domain.product.model.UnderlyingAssetType
import com.wl2c.elswhere.domain.product.model.dto.request.RequestProductSearchDto
import com.wl2c.elswhere.domain.product.model.entity.Product
import com.wl2c.elswhere.domain.product.model.entity.QEarlyRepaymentEvaluationDates.earlyRepaymentEvaluationDates
import com.wl2c.elswhere.domain.product.model.entity.QProduct.product
import com.wl2c.elswhere.domain.product.model.entity.QProductTickerSymbol.productTickerSymbol
import com.wl2c.elswhere.domain.product.model.entity.QTickerSymbol.tickerSymbol1
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils.hasText
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period

@Repository
class ProductSearchRepository(
    private val queryFactory: JPAQueryFactory
) {

    fun searchByIssueNumber(issueNumber: Int?): List<Product> =
        queryFactory.selectFrom(product)
            .where(
                issueNumberEq(issueNumber),
                product.productState.eq(ProductState.ACTIVE)
            )
            .orderBy(product.id.desc())
            .fetch()

    fun search(requestDto: RequestProductSearchDto, pageable: Pageable): List<Product> =
        queryFactory.selectFrom(product)
            .where(
                productNameContain(requestDto.productName),
                equityNamesIn(requestDto.equityNames),
                equityCountEq(requestDto.equityCount),
                issuerEq(requestDto.issuer),
                knockInLoe(requestDto.maxKnockIn),
                yieldIfConditionsMetGoe(requestDto.minYieldIfConditionsMet),
                initialRedemptionBarrierEq(requestDto.initialRedemptionBarrier),
                maturityRedemptionBarrierEq(requestDto.maturityRedemptionBarrier),
                subscriptionPeriodEq(requestDto.subscriptionPeriod),
                redemptionIntervalEq(requestDto.redemptionInterval),
                equityTypeEq(requestDto.equityType),
                typeEq(requestDto.type),
                periodBetween(requestDto.subscriptionStartDate, requestDto.subscriptionEndDate),
                product.productState.eq(ProductState.ACTIVE)
            )
            .orderBy(product.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

    // 상품 명 (일부 가능)
    private fun productNameContain(name: String?): BooleanExpression? =
        name?.let { product.name.contains(it) }

    // 기초자산 명
    private fun equityNamesIn(equityNames: List<String>?): BooleanExpression? {
        if (equityNames != null) {
            val tickerSymbolList = equityNames.mapNotNull { equityName ->
                queryFactory
                    .select(tickerSymbol1.tickerSymbol)
                    .from(tickerSymbol1)
                    .where(tickerSymbol1.equityName.eq(equityName))
                    .fetchOne()
            }

            val result = queryFactory
                .select(productTickerSymbol.product.id)
                .from(productTickerSymbol)
                .where(productTickerSymbol.tickerSymbol.tickerSymbol.`in`(tickerSymbolList))
                .groupBy(productTickerSymbol.product.id)
                .having(productTickerSymbol.tickerSymbol.tickerSymbol.count().eq(tickerSymbolList.size.toLong()))
                .fetch()

            return product.id.`in`(result)
        }
        return null
    }

    // 기초자산 개수
    private fun equityCountEq(equityCount: Int?): BooleanExpression? =
        equityCount?.let { product.equityCount.eq(it) }

    // 발행회사
    private fun issuerEq(issuer: String?): BooleanExpression? =
        if (hasText(issuer)) product.issuer.eq(issuer) else null

    // 최대 KI
    private fun knockInLoe(maxKnockIn: Int?): BooleanExpression? =
        maxKnockIn?.let { product.knockIn.loe(it) }

    // 최소 수익률
    private fun yieldIfConditionsMetGoe(minYieldIfConditionsMet: BigDecimal?): BooleanExpression? =
        minYieldIfConditionsMet?.let { product.yieldIfConditionsMet.goe(it) }

    // 1차 상환 배리어
    private fun initialRedemptionBarrierEq(initialRedemptionBarrier: Int?): BooleanExpression? {
        if (initialRedemptionBarrier != null) {
            val firstNumberString: StringTemplate = Expressions.stringTemplate(
                "REGEXP_SUBSTR({0}, '^[0-9]+')",
                product.productInfo
            )
            val firstNumber: NumberTemplate<Int> = Expressions.numberTemplate(
                Int::class.java,
                "CAST({0} AS DOUBLE)",
                firstNumberString
            )
            return firstNumber.eq(initialRedemptionBarrier)
        }
        return null
    }

    // 만기 상환 배리어
    private fun maturityRedemptionBarrierEq(maturityRedemptionBarrier: Int?): BooleanExpression? {
        if (maturityRedemptionBarrier != null) {
            val firstNumberString: StringTemplate = Expressions.stringTemplate(
                "SUBSTRING_INDEX(SUBSTRING_INDEX({0}, '-', -1), '(', 1)",
                product.productInfo
            )
            val firstNumber: NumberTemplate<Int> = Expressions.numberTemplate(
                Int::class.java,
                "CAST({0} AS DOUBLE)",
                firstNumberString
            )
            return firstNumber.eq(maturityRedemptionBarrier)
        }
        return null
    }

    // 상품 가입 기간
    private fun subscriptionPeriodEq(subscriptionPeriod: Int?): BooleanExpression? {
        if (subscriptionPeriod != null) {
            val monthsDiff: NumberTemplate<Int> = Expressions.numberTemplate(
                Int::class.java,
                "PERIOD_DIFF(DATE_FORMAT({1}, '%Y%m'), DATE_FORMAT({0}, '%Y%m'))",
                product.issuedDate,
                product.maturityDate
            )
            val monthsAsDouble: NumberTemplate<Double> = Expressions.numberTemplate(
                Double::class.java,
                "CAST({0} AS DOUBLE)",
                monthsDiff
            )
            val yearsDiff: NumberTemplate<Double> = Expressions.numberTemplate(
                Double::class.java,
                "{0} / 12",
                monthsAsDouble
            )
            val roundedYears: NumberTemplate<Double> = Expressions.numberTemplate(
                Double::class.java,
                "CEIL({0})",
                yearsDiff
            )
            return roundedYears.eq(subscriptionPeriod.toDouble())
        }
        return null
    }

    // 상환일 간격
    private fun redemptionIntervalEq(redemptionInterval: Int?): BooleanExpression? {
        if (redemptionInterval != null) {
            val results: List<Tuple> = queryFactory
                .select(earlyRepaymentEvaluationDates.product.id, earlyRepaymentEvaluationDates.earlyRepaymentEvaluationDate)
                .from(earlyRepaymentEvaluationDates)
                .groupBy(earlyRepaymentEvaluationDates.product.id, earlyRepaymentEvaluationDates.earlyRepaymentEvaluationDate)
                .orderBy(earlyRepaymentEvaluationDates.product.id.asc(), earlyRepaymentEvaluationDates.earlyRepaymentEvaluationDate.asc())
                .fetch()

            val productDatesMap = mutableMapOf<Long, MutableList<LocalDate>>()
            val matchingProductIds = mutableListOf<Long>()

            for (tuple in results) {
                val productId = tuple.get(0, Long::class.java)
                val date = tuple.get(1, LocalDate::class.java)

                if (productId != null && date != null) {
                    if (productDatesMap.containsKey(productId) && productDatesMap[productId]!!.size == 2) {
                        continue
                    }
                    productDatesMap.computeIfAbsent(productId) { mutableListOf() }.add(date)
                }
            }

            for ((productId, dates) in productDatesMap) {
                if (dates.size == 2) {
                    val firstDate = dates[0]
                    val secondDate = dates[1]
                    val period = Period.between(firstDate, secondDate)
                    var monthsDifference = period.years * 12 + period.months
                    if (period.days >= 20) {
                        monthsDifference += 1
                    }
                    if (monthsDifference == redemptionInterval) {
                        matchingProductIds.add(productId)
                    }
                }
            }
            return product.id.`in`(matchingProductIds)
        }
        return null
    }

    // 기초자산 유형
    private fun equityTypeEq(type: UnderlyingAssetType?): BooleanExpression? =
        type?.let { product.underlyingAssetType.eq(it) }

    // 상품 유형
    private fun typeEq(type: ProductType?): BooleanExpression? =
        type?.let { product.type.eq(it) }

    // 청약 시작일 & 청약 마감일
    private fun periodBetween(subscriptionStartDate: LocalDate?, subscriptionEndDate: LocalDate?): BooleanExpression? =
        when {
            subscriptionStartDate != null && subscriptionEndDate != null ->
                product.subscriptionStartDate.goe(subscriptionStartDate)
                    .and(product.subscriptionEndDate.loe(subscriptionEndDate))
            subscriptionStartDate != null ->
                product.subscriptionStartDate.goe(subscriptionStartDate)
            subscriptionEndDate != null ->
                product.subscriptionEndDate.loe(subscriptionEndDate)
            else -> null
        }

    // 회차 번호
    private fun issueNumberEq(issueNumber: Int?): BooleanExpression? =
        issueNumber?.let { product.issueNumber.eq(it) }
}