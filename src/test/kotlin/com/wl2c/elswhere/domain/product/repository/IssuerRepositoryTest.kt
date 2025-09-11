package com.wl2c.elswhere.domain.product.repository

import com.wl2c.elswhere.domain.product.model.IssuerState
import com.wl2c.elswhere.domain.product.model.entity.Issuer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class IssuerRepositoryTest @Autowired constructor(
    private val issuerRepository: IssuerRepository
) {

    @Test
    @DisplayName("ACTIVE 상태인 모든 발행사 리스트를 잘 가져오는지 확인")
    fun findAllByIssuerState() {
        // given
        val issuer1 = Issuer("A증권", IssuerState.ACTIVE)
        val issuer2 = Issuer("B증권", IssuerState.INACTIVE)
        val issuer3 = Issuer("C증권", IssuerState.ACTIVE)
        issuerRepository.saveAll(listOf(issuer1, issuer2, issuer3))

        // when
        val result = issuerRepository.findAllByIssuerState()

        // then
        assertThat(result).isNotNull
        assertThat(result.size).isEqualTo(2)
        assertThat(result)
            .extracting("issuerName")
            .containsExactlyInAnyOrder("A증권", "C증권")
    }
}