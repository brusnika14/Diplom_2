package ru.yandex.prakticum.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.yandex.prakticum.dto.CreateUserRequest;
import ru.yandex.prakticum.dto.UserLoginRequest;

import static io.restassured.RestAssured.given;
import static ru.yandex.prakticum.constants.Constants.*;

public class UserSteps {

    @Step("Создание нового пользователя")
    public static ValidatableResponse createUser(CreateUserRequest user) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(user)
                .when()
                .post(USER_CREATE)
                .then();
    }

    @Step("Логин пользователя")
    public static ValidatableResponse loginUser(UserLoginRequest credentials) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .body(credentials)
                .when()
                .post(USER_LOGIN)
                .then();
    }

    @Step("Удаление пользователя")
    public static ValidatableResponse deleteUser(String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .when()
                .delete(USER)
                .then();
    }

    @Step("Извлечение токена доступа")
    public static String extractAccessToken(ValidatableResponse response) {
        return response.extract().path("accessToken");
    }
}