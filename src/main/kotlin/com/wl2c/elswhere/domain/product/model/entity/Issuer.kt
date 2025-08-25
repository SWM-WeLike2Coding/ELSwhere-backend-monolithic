package com.wl2c.elswhere.domain.product.model.entity

import com.wl2c.elswhere.domain.product.model.IssuerState
import com.wl2c.elswhere.global.base.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault

@Entity
class Issuer(

    @Column(nullable = false)
    val issuerName: String,

    @ColumnDefault("'INACTIVE'")
    @Enumerated(EnumType.STRING)
    var issuerState: IssuerState

): BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issuer_id")
    val id: Long? = null

}