package ru.prakticum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.prakticum.dto.CreateUserRequest;
import ru.yandex.prakticum.dto.UserLoginRequest;
import ru.yandex.prakticum.steps.UserSteps;

import static org.hamcrest.Matchers.*;

public class UserLoginTest {

    private String email;
    private String password;
    private String accessToken;
    private CreateUserRequest userRequest;

    @Before
    public void setup() {
        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());

        // Подготовка тестовых данных
        email = RandomStringUtils.randomAlphabetic(10) + "@example.com";
        password = RandomStringUtils.randomAlphabetic(10);
        String username = RandomStringUtils.randomAlphabetic(10);

        userRequest = new CreateUserRequest(email, password, username);
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            UserSteps.deleteUser(accessToken)
                    .statusCode(202);
        }
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Проверка возможности логина под существующим пользователем")
    public void shouldLoginSuccessfullyWithValidCredentials() {
        // Регистрация пользователя
        UserSteps.createUser(userRequest)
                .statusCode(200);

        // Логин пользователя
        UserLoginRequest loginRequest = new UserLoginRequest(email, password);
        accessToken = UserSteps.loginUser(loginRequest)
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue())
                .extract().path("accessToken");
    }

    @Test
    @DisplayName("Логин с неверным email")
    @Description("Проверка не возможности логина с неверным email")
    public void shouldNotLoginWithInvalidCredentials() {
        UserLoginRequest invalidLoginRequest = new UserLoginRequest(
                RandomStringUtils.randomAlphabetic(10) + "@example.com",
                RandomStringUtils.randomAlphabetic(10)
        );

        UserSteps.loginUser(invalidLoginRequest)
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным password")
    @Description("Проверка не возможности логина с неверным password")
    public void shouldNotLoginWithWrongPassword() {
        // Регистрация пользователя
        UserSteps.createUser(userRequest)
                .statusCode(200);

        // Логин с правильными данными для получения токена
        UserLoginRequest correctLoginRequest = new UserLoginRequest(email, password);
        accessToken = UserSteps.loginUser(correctLoginRequest)
                .extract().path("accessToken");

        // Логин с неверным паролем
        UserLoginRequest wrongPasswordRequest = new UserLoginRequest(email, "wrong_password");
        UserSteps.loginUser(wrongPasswordRequest)
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }
}