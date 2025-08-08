package ru.prakticum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.prakticum.dto.CreateUserRequest;
import ru.yandex.prakticum.dto.UserLoginRequest;
import ru.yandex.prakticum.steps.UserSteps;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class UserTest {

    private static final String REQUIRED_FIELDS_ERROR = "Email, password and name are required fields";
    private static final String DUPLICATE_USER_ERROR = "User already exists";

    private List<CreateUserRequest> usersToCleanup = new ArrayList<>();

    @Before
    public void setup() {
        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
    }

    @After
    public void cleanup() {
        for (CreateUserRequest user : usersToCleanup) {
            try {
                ValidatableResponse loginResponse = UserSteps.loginUser(new UserLoginRequest(
                        user.getEmail(),
                        user.getPassword()
                ));

                if (loginResponse.extract().statusCode() == 200) {
                    String accessToken = UserSteps.extractAccessToken(loginResponse);
                    UserSteps.deleteUser(accessToken).statusCode(202);
                }
            } catch (Exception e) {
                System.out.println("Failed to cleanup user: " + user.getEmail());
            }
        }
        usersToCleanup.clear();
    }

    @Test
    @DisplayName("Создание нового пользователя")
    @Description("Проверяем, что пользователя можно создать с валидными данными")
    public void shouldRegisterSuccessfully() {
        CreateUserRequest userRequest = new CreateUserRequest(
                RandomStringUtils.randomAlphabetic(10) + "@example.com",
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10)
        );

        ValidatableResponse response = UserSteps.createUser(userRequest)
                .statusCode(200)
                .body("success", is(true));

        usersToCleanup.add(userRequest);
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

        usersToCleanup.add(userRequest);

        // Попытка повторной регистрации
        UserSteps.createUser(userRequest)
                .statusCode(403)
                .body("success", is(false))
                .body("message", is(DUPLICATE_USER_ERROR));
    }

    @Test
    @DisplayName("Создание пользователя с неверным email")
    @Description("Попытка создать пользователя с неверным email")
    public void shouldNotRegisterWithInvalidEmail() {
        CreateUserRequest invalidUserRequest = new CreateUserRequest(
                "invalid-email",
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10)
        );

        UserSteps.createUser(invalidUserRequest)
                .statusCode(403)
                .body("success", is(false))
                .body("message", anyOf(
                        is(DUPLICATE_USER_ERROR),
                        is(REQUIRED_FIELDS_ERROR),
                        containsString("email")));
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
                .body("success", is(false))
                .body("message", is(REQUIRED_FIELDS_ERROR));
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
                .body("success", is(false))
                .body("message", is(REQUIRED_FIELDS_ERROR));
    }
}