package com.wl2c.elswhere.domain.product.repository

import com.wl2c.elswhere.domain.product.model.ProductType
import com.wl2c.elswhere.domain.product.model.UnderlyingAssetType
import com.wl2c.elswhere.domain.product.model.dto.request.RequestProductSearchDto
import com.wl2c.elswhere.domain.product.model.entity.Product
import com.wl2c.elswhere.global.config.QuerydslConfig
import com.wl2c.elswhere.mock.EarlyRepaymentEvaluationDatesMock
import com.wl2c.elswhere.mock.ProductMock
import com.wl2c.elswhere.mock.ProductTickerSymbolMock
import com.wl2c.elswhere.mock.TickerSymbolMock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import java.time.LocalDate

@DataJpaTest
@Import(ProductSearchRepository::class, QuerydslConfig::class)
class ProductSearchRepositoryTest @Autowired constructor(
    private val earlyRepaymentEvaluationDatesRepository: EarlyRepaymentEvaluationDatesRepository,
    private val productTickerSymbolRepository: ProductTickerSymbolRepository,
    private val tickerSymbolRepository: TickerSymbolRepository,
    private val productRepository: ProductRepository,
    private val productSearchRepository: ProductSearchRepository
) {
    private lateinit var product1: Product
    private lateinit var product2: Product
    private lateinit var product3: Product

    private val today = LocalDate.now()
    private val unpaged = PageRequest.of(0, Int.MAX_VALUE)

    @BeforeEach
    fun setup() {
        earlyRepaymentEvaluationDatesRepository.deleteAll()
        productTickerSymbolRepository.deleteAll()
        tickerSymbolRepository.deleteAll()
        productRepository.deleteAll()

        val tickerSymbols = tickerSymbolRepository.saveAll(listOf(
            TickerSymbolMock.create("005930.KS", "삼성전자", UnderlyingAssetType.STOCK),
            TickerSymbolMock.create("^GSPC", "S&P500", UnderlyingAssetType.INDEX),
            TickerSymbolMock.create("^KS200", "KOSPI200", UnderlyingAssetType.INDEX),
            TickerSymbolMock.create("TSLA", "Tesla", UnderlyingAssetType.STOCK),
            TickerSymbolMock.create("^HSCE", "HSCEI", UnderlyingAssetType.INDEX),
            TickerSymbolMock.create("NVDA", "NVIDIA", UnderlyingAssetType.STOCK)
        ))

        product1 = productRepository.save(
            ProductMock.create(
            issuer = "AA증권",
            name = "AA증권 1호",
            equities = "삼성전자 / S&P500 / KOSPI200",
            subscriptionEndDate = today.minusDays(1),
            knockIn = 45,
            type = ProductType.STEP_DOWN,
            underlyingAssetType = UnderlyingAssetType.MIX,
            yieldIfConditionsMet = BigDecimal("10.423")
        ))

        product2 = productRepository.save(ProductMock.create(
            issuer = "BB증권",
            name = "BB증권 2호",
            equities = "S&P500 / HSCEI / KOSPI200",
            subscriptionEndDate = today,
            knockIn = 40,
            type = ProductType.STEP_DOWN,
            underlyingAssetType = UnderlyingAssetType.INDEX,
            yieldIfConditionsMet = BigDecimal("15.34")
        ))

        product3 = productRepository.save(ProductMock.create(
            issuer = "CC증권",
            name = "CC증권 3호",
            equities = "Tesla / 삼성전자 / NVIDIA",
            subscriptionEndDate = today,
            knockIn = 50,
            type = ProductType.LIZARD,
            underlyingAssetType = UnderlyingAssetType.STOCK,
            yieldIfConditionsMet = BigDecimal("11.234")
        ))

        productTickerSymbolRepository.saveAll(ProductTickerSymbolMock.createList(product1, listOf(tickerSymbols[0], tickerSymbols[1], tickerSymbols[2])))
        productTickerSymbolRepository.saveAll(ProductTickerSymbolMock.createList(product2, listOf(tickerSymbols[1], tickerSymbols[4], tickerSymbols[2])))
        productTickerSymbolRepository.saveAll(ProductTickerSymbolMock.createList(product3, listOf(tickerSymbols[3], tickerSymbols[0], tickerSymbols[5])))
    }

    @Test
    @DisplayName("상품 검색 - 원하는 이름을 포함하는 상품을 잘 가져오는지 확인1")
    fun searchProductName_1() {
        // given
        val searchDto = RequestProductSearchDto(productName = "AA")

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(1)
    }

    @Test
    @DisplayName("상품 검색 - 원하는 이름을 포함하는 상품을 잘 가져오는지 확인2")
    fun searchProductName_2() {
        // given
        val searchDto = RequestProductSearchDto(productName = "증권")

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(3)
    }

    @Test
    @DisplayName("상품 검색 - 원하는 조건의 발행회사 상품을 잘 가져오는지 확인")
    fun searchPublisher() {
        // given
        val searchDto = RequestProductSearchDto(issuer = "AA증권")

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(1)
    }

    @Test
    @DisplayName("상품 검색 - 원하는 조건의 기초자산 수를 가진 상품을 잘 가져오는지 확인")
    fun searchEquityCount() {
        // given
        val searchDto = RequestProductSearchDto(equityCount = 3)

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(3)
    }

    @Test
    @DisplayName("상품 검색 - 설정한 최대 낙인 이하의 상품을 잘 가져오는지 확인")
    fun searchMaxKnockIn() {
        // given
        val searchDto = RequestProductSearchDto(maxKnockIn = 50)

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(3)
    }

    @Test
    @DisplayName("상품 검색 - 최소 수익률 이상인 상품을 잘 가져오는지 확인")
    fun searchMinYield() {
        // given
        val searchDto = RequestProductSearchDto(minYieldIfConditionsMet = BigDecimal.valueOf(12))

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(1)
        assertThat(result.any { it.name == "BB증권 2호" }).isTrue()
    }

    @Test
    @DisplayName("상품 검색 - 설정한 1차 상환 배리어에 해당하는 상품을 잘 가져오는지 확인")
    fun searchInitialRedemptionBarrier() {
        // given
        val searchDto = RequestProductSearchDto(initialRedemptionBarrier = 95)

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(3)
    }

    @Test
    @DisplayName("상품 검색 - 설정한 상품 종류에 해당하는 상품을 잘 가져오는지 확인")
    fun searchProductType() {
        // given
        val searchDto = RequestProductSearchDto(type = ProductType.STEP_DOWN)

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(2)
    }

    @Test
    @DisplayName("상품 검색 - 설정한 청약 기간에 속하는 상품을 잘 가져오는지 확인")
    fun searchSubscriptionPeriod() {
        // given
        val searchDto = RequestProductSearchDto(
            subscriptionStartDate = today.minusDays(20),
            subscriptionEndDate = today
        )

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(3)
    }

    @Test
    @DisplayName("상품 검색 - 특정 기초자산을 포함하고 있는 상품을 잘 가져오는지 확인")
    fun searchEquityNamesIn() {
        // given
        val equityNamesList = listOf("삼성전자", "S&P500")
        val searchDto = RequestProductSearchDto(equityNames = equityNamesList)

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].equities).contains("삼성전자", "S&P500")
    }

    @Test
    @DisplayName("상품 검색 - 종목 형으로만 이루어진 상품을 잘 가져오는지 확인")
    fun searchEquityTypeEqSTOCK() {
        // given
        val searchDto = RequestProductSearchDto(equityType = UnderlyingAssetType.STOCK)

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].equities).contains("Tesla", "삼성전자", "NVIDIA")
    }

    @Test
    @DisplayName("상품 검색 - 주가 지수 형으로만 이루어진 상품을 잘 가져오는지 확인")
    fun searchEquityTypeEqINDEX() {
        // given
        val searchDto = RequestProductSearchDto(equityType = UnderlyingAssetType.INDEX)

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].equities).contains("S&P500", "HSCEI", "KOSPI200")
    }

    @Test
    @DisplayName("상품 검색 - 주가 지수과 종목형이 섞여있는 상품을 잘 가져오는지 확인")
    fun searchEquityTypeEqMIX() {
        // given
        val searchDto = RequestProductSearchDto(equityType = UnderlyingAssetType.MIX)

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].equities).contains("삼성전자", "S&P500", "KOSPI200")
    }

    @Test
    @DisplayName("상품 검색 - 설정한 조기상환일 간격에 맞는 상품을 잘 가져오는지 확인")
    fun searchRedemptionIntervalEq() {
        // given
        val earlyRepaymentDates = listOf(
            today.plusMonths(6),
            today.plusMonths(12),
            today.plusMonths(18),
            today.plusMonths(24),
            today.plusMonths(30)
        )
        earlyRepaymentEvaluationDatesRepository.saveAll(
            EarlyRepaymentEvaluationDatesMock.createList(product1, earlyRepaymentDates)
        )

        val searchDto = RequestProductSearchDto(redemptionInterval = 6)

        // when
        val result = productSearchRepository.search(searchDto, unpaged)

        // then
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].equities).contains("삼성전자", "S&P500", "KOSPI200")
    }
}