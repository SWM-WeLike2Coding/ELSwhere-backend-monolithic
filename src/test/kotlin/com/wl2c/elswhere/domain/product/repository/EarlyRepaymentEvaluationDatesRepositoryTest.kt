package com.wl2c.elswhere.domain.product.repository

import com.wl2c.elswhere.domain.product.model.ProductState
import com.wl2c.elswhere.domain.product.model.entity.Product
import com.wl2c.elswhere.mock.EarlyRepaymentEvaluationDatesMock
import com.wl2c.elswhere.mock.ProductMock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDate

@DataJpaTest
class EarlyRepaymentEvaluationDatesRepositoryTest @Autowired constructor(
    private val productRepository: ProductRepository,
    private val earlyRepaymentEvaluationDatesRepository: EarlyRepaymentEvaluationDatesRepository
){
    private val today: LocalDate = LocalDate.now()
    private lateinit var product: Product

    @BeforeEach
    fun setup() {
        productRepository.deleteAll()
        earlyRepaymentEvaluationDatesRepository.deleteAll()

        product = ProductMock.create()
        productRepository.save(product)
    }

    @Test
    @DisplayName("특정 날짜 이전의 조기 상환 평가 회차를 기반으로 다음 차수를 잘 계산하는지 확인")
    fun verifyNextOrder() {
        // given
        val dates = listOf(
            today.minusMonths(6),
            today,
            today.plusMonths(6),
            today.plusMonths(12)
        )
        earlyRepaymentEvaluationDatesRepository.saveAll(EarlyRepaymentEvaluationDatesMock.createList(product, dates))
        val targetDate = today.plusDays(1)

        // when
        val nextOrder = earlyRepaymentEvaluationDatesRepository.findNextEarlyRepaymentEvaluationDateOrder(product.id!!, targetDate)

        // then
        assertThat(nextOrder).isNotNull()
        assertThat(nextOrder).isEqualTo(3)
    }

    @Test
    @DisplayName("현재 날짜 이후 가장 빠른 상환 평가일을 정확히 반환한다")
    fun getEarliestFutureEvaluationDate() {
        // given
        val dates = listOf(
            today.minusMonths(6),
            today.plusMonths(6),
            today.plusMonths(12)
        )
        earlyRepaymentEvaluationDatesRepository.saveAll(EarlyRepaymentEvaluationDatesMock.createList(product, dates))

        // when
        val nextDate = earlyRepaymentEvaluationDatesRepository.findNextEarlyRepaymentEvaluationDate(product.id!!)

        // then
        assertThat(nextDate).isNotNull()
        assertThat(nextDate).isEqualTo(today.plusMonths(6))
    }

    @Test
    @DisplayName("현재 날짜 이후 가장 빠른 상환 평가일이 존재하지 않는 경우 null을 반환한다")
    fun getNonExistentEarliestFutureEvaluationDate() {
        // when
        val nextDate = earlyRepaymentEvaluationDatesRepository.findNextEarlyRepaymentEvaluationDate(product.id!!)

        // then
        assertThat(nextDate).isNull()
    }

    @Test
    @DisplayName("특정 상품 id에 연결된 모든 상환 평가일 목록을 반환한다")
    fun getAllEvaluationDatesAboutProductId() {
        // given
        val dates = listOf(
            today.minusMonths(6),
            today,
            today.plusMonths(6)
        )
        earlyRepaymentEvaluationDatesRepository.saveAll(EarlyRepaymentEvaluationDatesMock.createList(product, dates))

        // when
        val allDates = earlyRepaymentEvaluationDatesRepository.findAllEarlyRepaymentEvaluationDate(product.id!!)

        // then
        assertThat(allDates).hasSize(3)
        assertThat(allDates).extracting("earlyRepaymentEvaluationDate").containsExactlyInAnyOrderElementsOf(dates)
    }

    @Test
    @DisplayName("상품이 비활성 상태이면 빈 리스트를 반환한다")
    fun `should return empty list if product is inactive`() {
        // given
        val inactiveProduct = productRepository.save(ProductMock.create(productState = ProductState.INACTIVE))
        val dates = listOf(today.plusMonths(6))
        earlyRepaymentEvaluationDatesRepository.saveAll(EarlyRepaymentEvaluationDatesMock.createList(inactiveProduct, dates))

        // when
        val allDates = earlyRepaymentEvaluationDatesRepository.findAllEarlyRepaymentEvaluationDate(inactiveProduct.id!!)

        // then
        assertThat(allDates).isNotNull
        assertThat(allDates).isEmpty()
    }
}