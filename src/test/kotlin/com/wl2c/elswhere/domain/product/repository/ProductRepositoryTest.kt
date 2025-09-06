package com.wl2c.elswhere.domain.product.repository

import com.wl2c.elswhere.domain.product.model.MaturityEvaluationDateType
import com.wl2c.elswhere.domain.product.model.ProductState
import com.wl2c.elswhere.domain.product.model.ProductType
import com.wl2c.elswhere.domain.product.model.UnderlyingAssetType
import com.wl2c.elswhere.domain.product.model.entity.Product
import com.wl2c.elswhere.mock.ProductMock
import com.wl2c.elswhere.mock.ProductTickerSymbolMock
import com.wl2c.elswhere.mock.TickerSymbolMock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate

@DataJpaTest
class ProductRepositoryTest @Autowired constructor(
    private val productRepository: ProductRepository,
    private val productTickerSymbolRepository: ProductTickerSymbolRepository,
    private val tickerSymbolRepository: TickerSymbolRepository
){
    private lateinit var product1: Product
    private lateinit var product2: Product

    @BeforeEach
    fun setup() {
        productTickerSymbolRepository.deleteAll()
        tickerSymbolRepository.deleteAll()
        productRepository.deleteAll()

        val tickerSymbols = listOf(
            TickerSymbolMock.create("005930.KS", "삼성전자", UnderlyingAssetType.STOCK),
            TickerSymbolMock.create("^GSPC", "S&P500", UnderlyingAssetType.INDEX),
            TickerSymbolMock.create("^KS200", "KOSPI200", UnderlyingAssetType.INDEX),
            TickerSymbolMock.create("TSLA", "Tesla", UnderlyingAssetType.STOCK),
            TickerSymbolMock.create("^HSCE", "HSCEI", UnderlyingAssetType.INDEX),
            TickerSymbolMock.create("NVDA", "NVIDIA", UnderlyingAssetType.STOCK)
        )

        product1 = ProductMock.create(
            issuer = "AA증권",
            name = "1호",
            equities = "삼성전자 / S&P500 / KOSPI200",
            equityCount = 3,
            issuedDate = LocalDate.now().minusDays(1),
            maturityDate = LocalDate.now().plusYears(3),
            maturityEvaluationDate = LocalDate.now().plusYears(3).minusDays(5),
            maturityEvaluationDateType = MaturityEvaluationDateType.SINGLE,
            yieldIfConditionsMet = BigDecimal("10.423"),
            subscriptionStartDate = LocalDate.now().minusDays(14),
            subscriptionEndDate = LocalDate.now().minusDays(1),
            productInfo = "95-90-85-80-75-50",
            knockIn = 45,
            type = ProductType.STEP_DOWN,
            underlyingAssetType = UnderlyingAssetType.MIX,
            productState = ProductState.ACTIVE
        )

        product2 = ProductMock.create(
            issuer = "BB증권",
            name = "2호",
            equities = "Tesla / HSCEI / NVIDIA",
            equityCount = 3,
            issuedDate = LocalDate.now().minusDays(1),
            maturityDate = LocalDate.now().plusYears(3),
            maturityEvaluationDate = LocalDate.now().plusYears(3).minusDays(3),
            maturityEvaluationDateType = MaturityEvaluationDateType.SINGLE,
            yieldIfConditionsMet = BigDecimal("15.34"),
            subscriptionStartDate = LocalDate.now().minusDays(14),
            subscriptionEndDate = LocalDate.now(),
            productInfo = "95-90-85-80-75-50",
            knockIn = 40,
            type = ProductType.STEP_DOWN,
            underlyingAssetType = UnderlyingAssetType.MIX,
            productState = ProductState.ACTIVE
        )

        tickerSymbolRepository.saveAll(tickerSymbols);
        productRepository.save(product1)
        productRepository.save(product2)

        val productTickerSymbol1 = ProductTickerSymbolMock.createList(product1, listOf(tickerSymbols[0], tickerSymbols[1], tickerSymbols[2]))
        val productTickerSymbol2 = ProductTickerSymbolMock.createList(product1, listOf(tickerSymbols[3], tickerSymbols[4], tickerSymbols[5]))
        productTickerSymbolRepository.saveAll(productTickerSymbol1)
        productTickerSymbolRepository.saveAll(productTickerSymbol2)
    }

    @Test
    @DisplayName("상품 id 리스트에 해당하는 상품 리스트를 잘 가져오는지 확인")
    fun listByIds() {
        // given & when
        val list = listOf(product1.id!!, product2.id!!)
        val productList = productRepository.listByIds(list)

        // then
        assertThat(productList.size).isEqualTo(2)
        assertThat(productList[0].issuer).isEqualTo("AA증권")
        assertThat(productList[1].issuer).isEqualTo("BB증권")
    }

    @Test
    @DisplayName("특정 상품 id로 상품을 조회하는지 확인")
    fun findOne() {
        // given & when
        val foundProduct = productRepository.findOne(product1.id!!)

        // then
        assertThat(foundProduct).isNotNull
        assertThat(foundProduct?.issuer).isEqualTo("AA증권")
    }

    @Test
    @DisplayName("청약 중인 상품인지를 잘 구분하는지 확인")
    fun findOnSale() {
        // given & when
        val result = productRepository.listByOnSale("knock-in", Pageable.unpaged())

        // then
        assertThat(result.totalElements).isEqualTo(1L)
        assertThat(result.content.any { it.name == "2호" }).isTrue
    }

    @Test
    @DisplayName("청약 종료인 상품인지를 잘 구분하는지 확인")
    fun findEndSale() {
        // given & when
        val result = productRepository.listByEndSale("knock-in", Pageable.unpaged())

        // then
        assertThat(result.totalElements).isEqualTo(1L)
        assertThat(result.content.any { it.name == "1호" }).isTrue
    }

    @Test
    @DisplayName("상품이 청약 중인지 확인")
    fun isItProductOnSale() {
        // given & when
        val onSaleProduct = productRepository.isItProductOnSale(product2.id!!)

        // then
        assertThat(onSaleProduct).isNotNull
        assertThat(onSaleProduct?.name).isEqualTo("2호")
    }

    @Test
    @DisplayName("STEP_DOWN 타입 청약 중인 상품을 잘 가져오는지 확인")
    fun listByOnSaleAndStepDown() {
        // given & when
        val stepDownList = productRepository.listByOnSaleAndStepDown()

        // then
        assertThat(stepDownList).isNotEmpty
        assertThat(stepDownList.all { it.type == ProductType.STEP_DOWN }).isTrue
    }

    @Test
    @DisplayName("비교 결과 상품을 잘 찾아오는지 확인")
    fun findComparisonResults() {
        // given
        val targetEquityTickerSymbols = listOf("005930.KS", "^GSPC", "^KS200")

        // when
        val comparisonResults = productRepository.findComparisonResults(
            targetId = 0L,
            targetEquityCount = 3,
            targetEquityTickerSymbols = targetEquityTickerSymbols
        )

        // then
        assertThat(comparisonResults).isNotNull
        assertThat(comparisonResults.all { it.id == product1.id }).isTrue
    }
}