package ru.yandex.prakticum.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.yandex.prakticum.dto.CreateOrderRequest;

import static io.restassured.RestAssured.given;
import static ru.yandex.prakticum.constants.Constants.*;

public class OrderSteps {

    @Step("Получение списка ингредиентов")
    public static ValidatableResponse getIngredients() {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .when()
                .get(INGREDIENTS)
                .then();
    }

    @Step("Создание заказа с авторизацией")
    public static ValidatableResponse createOrderWithAuth(CreateOrderRequest order, String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDERS)
                .then();
    }

    @Step("Создание заказа без авторизации")
    public static ValidatableResponse createOrderWithoutAuth(CreateOrderRequest order) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(order)
                .when()
                .post(ORDERS)
                .then();
    }
}