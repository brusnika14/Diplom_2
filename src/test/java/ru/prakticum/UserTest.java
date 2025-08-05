package ru.prakticum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.prakticum.dto.CreateUserRequest;
import ru.yandex.prakticum.steps.UserSteps;

import static org.hamcrest.Matchers.is;

public class UserTest {

    @Before
    public void setup() {
        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
    }

    @Test
    @DisplayName("Создание нового пользователя")
    @Description("Проверяем, что курьера можно создать с валидными данными")
    public void shouldRegisterSuccessfully() {
        CreateUserRequest userRequest = new CreateUserRequest(
                RandomStringUtils.randomAlphabetic(10) + "@example.com",
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10)
        );

        UserSteps.createUser(userRequest)
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание двух одинаковых пользователей")
    @Description("Попытка создать двух пользователей с одинаковым набором данных")
    public void shouldNotRegisterDuplicateUser() {
        CreateUserRequest userRequest = new CreateUserRequest(
                RandomStringUtils.randomAlphabetic(10) + "@example.com",
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10)
        );

        // Первая регистрация
        UserSteps.createUser(userRequest)
                .statusCode(200);

        // Попытка повторной регистрации
        UserSteps.createUser(userRequest)
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без логина")
    @Description("Попытка создать пользователя с неверным email")
    public void shouldNotRegisterWithInvalidEmail() {
        CreateUserRequest invalidUserRequest = new CreateUserRequest(
                "invalid-email",
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10)
        );

        UserSteps.createUser(invalidUserRequest)
                .statusCode(403)
                .body("success", is(false));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Попытка создать пользователя без поля email")
    public void shouldNotRegisterWithEmptyEmail() {
        CreateUserRequest emptyEmailRequest = new CreateUserRequest(
                "",
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10)
        );

        UserSteps.createUser(emptyEmailRequest)
                .statusCode(403)
                .body("success", is(false));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Попытка создать пользователя без поля пароль")
    public void shouldNotRegisterWithEmptyPassword() {
        CreateUserRequest emptyPasswordRequest = new CreateUserRequest(
                RandomStringUtils.randomAlphabetic(10) + "@example.com",
                "",
                RandomStringUtils.randomAlphabetic(10)
        );

        UserSteps.createUser(emptyPasswordRequest)
                .statusCode(403)
                .body("success", is(false));
    }
}