package com.wl2c.elswhere.domain.product.model

enum class MaturityEvaluationDateType {
    /**
     * 한 개의 만기 평가일
     */
    SINGLE,

    /**
     * 여러 개의 만기 평가일
     */
    MULTIPLE,

    /**
     * 파악되지 않음
     */
    UNKNOWN
}