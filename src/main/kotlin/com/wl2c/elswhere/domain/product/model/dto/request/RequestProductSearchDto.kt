package com.wl2c.elswhere.domain.product.model.dto.request

import com.wl2c.elswhere.domain.product.model.ProductType
import com.wl2c.elswhere.domain.product.model.UnderlyingAssetType
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDate

data class RequestProductSearchDto(

    @Schema(description = "상품 명", example = "oo투자증권 1234회")
    val productName: String? = null,

    @Schema(description = "기초자산 명", example = "['S&P500', 'Tesla']")
    val equityNames: List<String>? = null,

    @Schema(description = "기초자산 수", example = "3")
    val equityCount: Int? = null,

    @Schema(description = "발행 회사", example = "oo투자증권")
    val issuer: String? = null,

    @Schema(description = "최대 KI(낙인배리어)", example = "45, 낙인 값이 없을 시 null")
    val maxKnockIn: Int? = null,

    @Schema(description = "최소 수익률(연, %)", example = "10.2")
    val minYieldIfConditionsMet: BigDecimal? = null,

    @Schema(description = "1차 상환 배리어", example = "90")
    val initialRedemptionBarrier: Int? = null,

    @Schema(description = "만기 상환 배리어", example = "65")
    val maturityRedemptionBarrier: Int? = null,

    @Schema(description = "상품 가입 기간", example = "3")
    val subscriptionPeriod: Int? = null,

    @Schema(description = "상환일 간격", example = "6")
    val redemptionInterval: Int? = null,

    @Schema(description = "기초자산 유형", example = "INDEX")
    val equityType: UnderlyingAssetType? = null,

    @Schema(description = "상품 유형", example = "STEP_DOWN")
    val type: ProductType? = null,

    @Schema(description = "청약 시작일", example = "2024-06-14")
    val subscriptionStartDate: LocalDate? = null,

    @Schema(description = "청약 마감일", example = "2024-06-21")
    val subscriptionEndDate: LocalDate? = null
)