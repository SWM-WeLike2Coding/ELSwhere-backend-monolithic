package com.wl2c.elswhere.domain.product.repository

import com.wl2c.elswhere.domain.product.model.entity.Issuer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface IssuerRepository : JpaRepository<Issuer, Long> {

    @Query("select i from Issuer i " +
            "where i.issuerState = 'ACTIVE' ")
    fun findAllByIssuerState(): List<Issuer>
}