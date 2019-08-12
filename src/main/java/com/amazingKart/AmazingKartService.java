package com.amazingKart;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AmazingKartService {

    public static final String PROMOTION_SET_A = "promotionSetA";
    public static final String PROMOTION_SET_B = "promotionSetB";
    private Promotion promotion;
    private final List<String> validPromotions = Arrays.asList(PROMOTION_SET_A, PROMOTION_SET_B);
    private ProductClient productClient;
    private ExchangeRateConverter exchangeRateConverter;

    public AmazingKartService(String promotion) {
        if (!validPromotions.contains(promotion)) {
            throw new InvalidPromotionException();
        }

        this.promotion = PROMOTION_SET_A.equals(promotion) ? new PromotionA() : new PromotionB();
        this.productClient = new ProductClient();
        this.exchangeRateConverter = new ExchangeRateConverter();

    }

    public AmazingKartService(Promotion promotion, ProductClient productClient, ExchangeRateConverter exchangeRateConverter) {
        this.promotion = promotion;
        this.productClient = productClient;
        this.exchangeRateConverter = exchangeRateConverter;
    }

    public List<DiscountedProduct> listProducts() {
        List<Product> products = productClient.retrieveProducts();
        if(products.isEmpty())
            return Collections.EMPTY_LIST;
        List<Product> updatedProducts = exchangeRateConverter.convertToINR(products);
        return updatedProducts.stream()
                .map(product -> promotion.apply(product))
                .collect(Collectors.toList());
    }

    public Promotion getPromotion() {
        return promotion;
    }
}
