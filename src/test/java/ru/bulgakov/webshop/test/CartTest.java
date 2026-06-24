package ru.bulgakov.webshop.test;

import com.codeborne.selenide.Configuration;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.bulgakov.webshop.pages.ProductPage;
import ru.bulgakov.webshop.pages.WsWelcomePage;
import ru.bulgakov.webshop.steps.AuthSteps;

import static com.codeborne.selenide.Selenide.*;
import static ru.bulgakov.webshop.config.Config.TIMEOUT_MS;
import static ru.bulgakov.webshop.config.Config.WEB_SHOP_URL;

public class CartTest {
    private final AuthSteps authSteps = new AuthSteps();
    private static final Faker faker = new Faker();

    @BeforeEach
    void beforeEach() {
        Configuration.timeout = TIMEOUT_MS;
        authSteps.registerNewUser();
    }

    @Test
    void addItemToCartTest() {
        String quantity = String.valueOf(faker.number().numberBetween(1,10));

        ProductPage productPage = open(WEB_SHOP_URL, WsWelcomePage.class)
                .goToCatalogFromMenu(1, "Desktops")
                .goToProductCard(1);

        String expectedProductName = productPage.getProductName();
        String expectedProductPrice = productPage.getProductPrice();

        productPage
                .selectProcessor(0)
                .setQuantity(quantity)
                .addToCart()
                .checkDisplayNotificationOfAdd()
                .checkChangeCartCounter(quantity)
                .goToCart()
                .checkProductQuantity(quantity)
                .checkProductName(expectedProductName)
                .checkProductPrice(expectedProductPrice, quantity);
    }
}
