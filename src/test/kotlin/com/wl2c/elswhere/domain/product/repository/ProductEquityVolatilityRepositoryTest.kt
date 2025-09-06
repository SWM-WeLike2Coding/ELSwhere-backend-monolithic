package com.wl2c.elswhere.domain.product.repository

import com.wl2c.elswhere.domain.product.model.entity.Product
import com.wl2c.elswhere.domain.product.model.entity.ProductEquityVolatility
import com.wl2c.elswhere.domain.product.model.entity.ProductTickerSymbol
import com.wl2c.elswhere.mock.ProductEquityVolatilityMock
import com.wl2c.elswhere.mock.ProductMock
import com.wl2c.elswhere.mock.ProductTickerSymbolMock
import com.wl2c.elswhere.mock.TickerSymbolMock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.math.BigDecimal

@DataJpaTest
class ProductEquityVolatilityRepositoryTest @Autowired constructor(
    private val productEquityVolatilityRepository: ProductEquityVolatilityRepository,
    private val productTickerSymbolRepository: ProductTickerSymbolRepository,
    private val productRepository: ProductRepository,
    private val tickerSymbolRepository: TickerSymbolRepository
) {

    private lateinit var product: Product
    private lateinit var pts1: ProductTickerSymbol
    private lateinit var pts2: ProductTickerSymbol
    private lateinit var volatility1: ProductEquityVolatility
    private lateinit var volatility2: ProductEquityVolatility

    @BeforeEach
    fun setup() {
        productEquityVolatilityRepository.deleteAll()
        productTickerSymbolRepository.deleteAll()
        tickerSymbolRepository.deleteAll()
        productRepository.deleteAll()

        product = productRepository.save(ProductMock.create())
        val ticker1 = tickerSymbolRepository.save(TickerSymbolMock.create("AAPL", "Apple Inc"))
        val ticker2 = tickerSymbolRepository.save(TickerSymbolMock.create("MSFT", "Microsoft Corp"))

        pts1 = productTickerSymbolRepository.save(ProductTickerSymbolMock.create(product, ticker1))
        pts2 = productTickerSymbolRepository.save(ProductTickerSymbolMock.create(product, ticker2))

        volatility1 = productEquityVolatilityRepository.save(ProductEquityVolatilityMock.create(pts1, BigDecimal("0.25")))
        volatility2 = productEquityVolatilityRepository.save(ProductEquityVolatilityMock.create(pts2, BigDecimal("0.30")))
    }

    @Test
    @DisplayName("ProductTickerSymbol id 목록에 해당하는 변동성 정보를 정확히 조회한다")
    fun getAllMatchingVolatilities() {
        // given
        val unrelatedTicker = tickerSymbolRepository.save(TickerSymbolMock.create("TSLA", "Tesla Inc"))
        val unrelatedPts = productTickerSymbolRepository.save(ProductTickerSymbolMock.create(product, unrelatedTicker))
        productEquityVolatilityRepository.save(ProductEquityVolatilityMock.create(unrelatedPts, BigDecimal("0.45")))
        val idsToFind = listOf(pts1.id!!, pts2.id!!)

        // when
        val foundVolatilities = productEquityVolatilityRepository.findAllByProductTickerSymbol(idsToFind)

        // then
        assertThat(foundVolatilities).hasSize(2)
        assertThat(foundVolatilities).extracting("id").containsExactlyInAnyOrder(pts1.id, pts2.id)
        assertThat(foundVolatilities).extracting("volatility").containsExactlyInAnyOrder(volatility1.volatility, volatility2.volatility)
    }
}