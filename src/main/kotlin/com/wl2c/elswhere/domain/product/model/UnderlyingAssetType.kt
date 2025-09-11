package com.wl2c.elswhere.domain.product.model

/**
 * 기초자산 유형
 */
enum class UnderlyingAssetType {
    /**
     * 주가 지수
     */
    INDEX,

    /**
     * 종목
     */
    STOCK,

    /**
     * 혼합
     */
    MIX
}