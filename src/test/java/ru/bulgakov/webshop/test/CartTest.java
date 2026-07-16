package ru.bulgakov.webshop.test;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import ru.bulgakov.webshop.TestBase;
import ru.bulgakov.webshop.pages.CartPage;
import ru.bulgakov.webshop.pages.ProductPage;
import ru.bulgakov.webshop.pages.WsWelcomePage;
import ru.bulgakov.webshop.steps.AuthSteps;

import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static io.qameta.allure.SeverityLevel.CRITICAL;
import static java.util.Locale.US;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.bulgakov.webshop.config.Config.WEB_SHOP_URL;

public class CartTest extends TestBase {
    private final AuthSteps authSteps = new AuthSteps();
    private static final Faker faker = new Faker();

    @BeforeEach
    void beforeEach() {
        authSteps.registerNewUser();
    }

    @Test
    @Tags({@Tag("positive"), @Tag("ws-site")})
    @Severity(CRITICAL)
    @Owner("george_kiselev")
    @Link(name = "TASK-123", url = "https://task-123.com")
    @DisplayName("Добавление товаров в корзину")
    void addItemsToCartTest() {
        int processorIndex = 0; // 0 = slow, 1 = medium, 2 = fast

        ProductPage productPage = open(WEB_SHOP_URL, WsWelcomePage.class)
                .goToCatalogFromMenu(1, "Desktops")
                .goToProductCard(1);

        String expectedProductName = productPage.getProductName();
        String expectedProductPrice = productPage.getProductPrice();
        String quantity = String.valueOf(faker.number().numberBetween(1, 10));

        CartPage cartPage = productPage
                .selectProcessor(processorIndex)
                .setQuantity(quantity)
                .addToCart()
                .checkDisplayNotificationOfAdd()
                .checkChangeCartCounter(quantity)
                .goToCart();

        float processorPrice = getProcessorPrice(processorIndex);
        String expectedTotal = String.format(US, "%.2f",
                (Float.parseFloat(expectedProductPrice) + processorPrice) * Float.parseFloat(quantity));

        assertAll(
                () -> step("Проверить название товара", () ->
                        assertEquals(expectedProductName, cartPage.getProductName())),
                () -> step("Проверить итоговую цену товаров", () ->
                        assertEquals(expectedTotal, cartPage.getTotalPrice())),
                () -> step("Проверить количество товаров", () ->
                        assertEquals(quantity, cartPage.getProductQuantity()))
        );
    }

    private float getProcessorPrice(int processorIndex) {
        return switch (processorIndex) {
            case 0 -> 0f;
            case 1 -> 15f;
            case 2 -> 100f;
            default -> throw new IllegalArgumentException("Unknown processor index: " + processorIndex);
        };
    }
}