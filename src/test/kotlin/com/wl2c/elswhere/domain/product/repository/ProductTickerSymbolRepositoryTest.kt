package com.wl2c.elswhere.domain.product.repository

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

@DataJpaTest
class ProductTickerSymbolRepositoryTest @Autowired constructor(
    private val productRepository: ProductRepository,
    private val productTickerSymbolRepository: ProductTickerSymbolRepository,
    private val tickerSymbolRepository: TickerSymbolRepository
) {
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
        )
        product1 = ProductMock.create()
        product2 = ProductMock.create(name = "2호")

        tickerSymbolRepository.saveAll(tickerSymbols)
        productRepository.save(product1)
        productRepository.save(product2)
        productTickerSymbolRepository.saveAll(ProductTickerSymbolMock.createList(product1, listOf(tickerSymbols[0], tickerSymbols[1], tickerSymbols[2])))
    }

    @Test
    @DisplayName("상품 id에 해당하는 상품 티커 심볼 리스트를 잘 가져오는지 확인")
    fun getListByProductId() {
        // given & when
        val productTickerSymbolList = productTickerSymbolRepository.findAllByProductId(product1.id!!)

        // then
        assertThat(productTickerSymbolList.size).isEqualTo(3)
        assertThat(productTickerSymbolList).allSatisfy {
            assertThat(it.product.id).isEqualTo(product1.id)
        }
        assertThat(productTickerSymbolList)
            .extracting("tickerSymbol.equityName")
            .containsExactlyInAnyOrder("삼성전자", "S&P500", "KOSPI200")
    }

    @Test
    @DisplayName("존재하지 않는 상품 id로 조회하면 빈 리스트를 반환한다")
    fun getEmptyListByNonExistentProductId() {
        // given & when
        val nonExistentProductId = 9999L
        val productTickerSymbolList = productTickerSymbolRepository.findAllByProductId(nonExistentProductId)

        // then
        assertThat(productTickerSymbolList).isNotNull
        assertThat(productTickerSymbolList).isEmpty()

    }
}