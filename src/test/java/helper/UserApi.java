package helper;

import io.qameta.allure.Step;
import pojo.*;
import registeruser.RegisterUserTest;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.text.IsEmptyString.emptyString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserApi extends Helper {
    private UserRegisterRequest lastRegisterRequest;
    private UserRegisterResponse lastRegisterResponse;
    private String registerAccessToken;

    private final String REGISTER_HANDLE = "/api/auth/register/";
    private final String LOGIN_USER_HANDLE = "/api/auth/login/";
    private final String DELETE_USER_HANDLE = "/api/auth/user/";

    private final int MIN_SIZE_OF_RANDOM_STRING = 5;
    private final int ADDITIONAL_RANGE_OF_RANGE_STRING = 10;
    private final String EMAIL_DOMAIN_NAME = "@yandex.ru";

    // Методы запросов

    @Step("Удаление пользователя после теста")
    public void deleteUser() {
        if (registerAccessToken == null) {
            return;
        }
        deleteUser(registerAccessToken);
    }

    @Step("Удаление пользователя")
    private void deleteUser(String token) {
        this.response = given()
                .header("Authorization", token)
                .delete(DELETE_USER_HANDLE);
        // Не проверяем бизнес логику, только убеждаемся что удаление действительно произведено и ранее созданный пользователь
        // не повлияет на последующие тесты
        checkResponseSC(SC_ACCEPTED);
    }

    @Step("Регистрируем пользователя и сохраняем токен. Запрос создаём внутри")
    public void registerUserAndSaveToken() {
        registerUserAndSaveToken(createRegisterRequest());
    }

    @Step("Регистрируем пользователя и сохраняем токен")
    public void registerUserAndSaveToken(UserRegisterRequest request) {
        registerUser(request);
        saveRegisterResponse();
    }

    @Step("Регистрируем пользователя. Запрос создаём внутри")
    public void registerUser() {
        registerUser(createRegisterRequest());
    }

    @Step("Регистрируем пользователя")
    public void registerUser(UserRegisterRequest request) {
        this.response = given()
                .header("Content-type", "application/json")
                .body(request)
                .post(REGISTER_HANDLE);
        this.lastRegisterRequest = request;
    }

    @Step("Логинимся под юзером. Создаём запрос из последнего запроса на регистрацию")
    public void loginUser() {
        LoginUserRequest request = createLoginUserRequestFromLastRegisterRequest();
        loginUser(request);
    }

    @Step("Логинимся под юзером")
    public void loginUser(LoginUserRequest request) {
        this.response = given()
                .header("Content-type", "application/json")
                .body(request)
                .post(LOGIN_USER_HANDLE);
    }

    // Методы проверок

    @Step("Проверяем что поле Message ответа не пустое")
    public void checkMessageNotEmpty() {
        assertNotNull(response);
        response.then().assertThat().body("message", not(emptyString()));
    }

    @Step("Проверяем что поле AccessToken ответа не пустое")
    public void checkAccessTokenNotEmpty() {
        assertNotNull(response);
        response.then().assertThat().body("accessToken", not(emptyString()));
    }

    @Step("Проверяем поле RefreshToken ответа не пустое")
    public void checkRefreshTokenNotEmpty() {
        assertNotNull(response);
        response.then().assertThat().body("refreshToken", not(emptyString()));
    }

    @Step("Сравниваем данные полученные при регистрации с данными полученными при авторизации")
    public void compareRegisterAndLoginFields() {
        assertNotNull(lastRegisterResponse);
        assertNotNull(response);

        LoginUserResponse loginUserResponse = response.body().as(LoginUserResponse.class);

        assertEquals("RefreshToken ответа при регистрации не совпадает с токеном из ответа на авторизацию", lastRegisterResponse.getRefreshToken(), loginUserResponse.getRefreshToken());
        assertEquals("AccessToken ответа при регистрации не совпадает с токеном из ответа на авторизацию", lastRegisterResponse.getAccessToken(), loginUserResponse.getAccessToken());
        User loginUser = lastRegisterResponse.getUser();
        User registerUser = loginUserResponse.getUser();
        assertEquals("Email ответа при регистрации не совпадает с Email из ответа на авторизацию", loginUser.getEmail(), registerUser.getEmail());
        assertEquals("Name из ответа при регистрации не совпадает с Name из ответа на авторизацию", loginUser.getName(), registerUser.getName());
    }

    // Вспомогательные методы

    @Step("Сохраняем ответ регистрации. Будет использован для логина, сверки полей и послетестового удаления пользователя")
    public void saveRegisterResponse() {
        assertNotNull(response);
        lastRegisterResponse = response.body().as(UserRegisterResponse.class);
        this.registerAccessToken = lastRegisterResponse.getAccessToken();
        assertNotNull(this.registerAccessToken);
    }

    @Step("Генерируем запрос для регистрации со случайными значениями")
    public UserRegisterRequest createRegisterRequest() {
        Random rand = new Random();
        String email = generateRandomString(rand.nextInt(ADDITIONAL_RANGE_OF_RANGE_STRING) + MIN_SIZE_OF_RANDOM_STRING) + EMAIL_DOMAIN_NAME;
        String name = generateRandomString(rand.nextInt(ADDITIONAL_RANGE_OF_RANGE_STRING) + MIN_SIZE_OF_RANDOM_STRING);
        String password = generateRandomString(rand.nextInt(ADDITIONAL_RANGE_OF_RANGE_STRING) + MIN_SIZE_OF_RANDOM_STRING);

        return new UserRegisterRequest(email, password, name);
    }

    @Step("Создаём новый запрос на авторизацию, используя данные из последнего запроса на регистрацию")
    public LoginUserRequest createLoginUserRequestFromLastRegisterRequest() {
        assertNotNull(lastRegisterRequest);
        return new LoginUserRequest(
                this.lastRegisterRequest.getEmail(),
                this.lastRegisterRequest.getPassword()
        );
    }

    @Step("Получаем последний отправленный запрос на регистрацию пользователя")
    public UserRegisterRequest getLastUserRegisterRequest() {
        return this.lastRegisterRequest;
    }

    @Step("Получаем AccessToken")
    public String getAccessToken() {
        assertNotNull(response);
        return response.body().as(UserRegisterResponse.class).getAccessToken();
    }
}
