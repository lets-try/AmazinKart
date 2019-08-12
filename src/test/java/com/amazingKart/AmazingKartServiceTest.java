package com.amazingKart;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.amazingKart.AmazingKartService.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
public class AmazingKartServiceTest {
    @Mock
    private Promotion promotion;

    @Mock
    private ExchangeRateConverter exchangeRateConverter;

    @Mock
    private ProductClient productClient;

    @Test(expected = InvalidPromotionException.class)
    public void shouldThrowInvalidAExceptionWhenObjectIsCreatedWithInvalidPromotion() {
          new AmazingKartService("randomPromo");
    }

    @Test
    public void promotionShouldBeOfTypePromotionAWhenObjectIsCreatedWithPromotionSetA() {
        AmazingKartService amazingKartService = new AmazingKartService(PROMOTION_SET_A);
        assertTrue(amazingKartService.getPromotion() instanceof PromotionA);
    }

    @Test
    public void promotionShouldBeOfTypePromotionAWhenObjectIsCreatedWithPromotionSetB() {
        AmazingKartService amazingKartService = new AmazingKartService(PROMOTION_SET_B);
        assertTrue(amazingKartService.getPromotion() instanceof PromotionB);
    }

    @Test
    public void shouldReturnEmptyIfProductClientReturnsEmptyList() {
        AmazingKartService amazingKartService = new AmazingKartService(promotion, productClient, exchangeRateConverter);
        when(productClient.retrieveProducts()).thenReturn(Collections.emptyList());

        List<DiscountedProduct> discountedProducts = amazingKartService.listProducts();

        verify(productClient, times(1)).retrieveProducts();
        verifyZeroInteractions(exchangeRateConverter);
        verifyZeroInteractions(promotion);
        assertTrue(discountedProducts.isEmpty());
    }

    @Test
    public void shouldCallExchangeRateConverterAndApplyPromotionsIfProductClientReturnsNonEmptyList() {
        AmazingKartService amazingKartService = new AmazingKartService(promotion, productClient, exchangeRateConverter);
        Product product1 = new Product();
        Product product2 = new Product();
        List<Product> products =  Arrays.asList(product1, product2);
        when(productClient.retrieveProducts()).thenReturn(products);
        when(exchangeRateConverter.convertToINR(products)).thenReturn(products);
        DiscountedProduct discountedProduct = new DiscountedProduct();
        when(promotion.apply(any())).thenReturn(discountedProduct);

        List<DiscountedProduct> discountedProducts = amazingKartService.listProducts();

        verify(productClient, times(1)).retrieveProducts();
        verify(exchangeRateConverter, times(1)).convertToINR(products);
        verify(promotion, times(1)).apply(product1);
        verify(promotion, times(1)).apply(product2);
        assertEquals(Arrays.asList(discountedProduct, discountedProduct), discountedProducts);
    }
}