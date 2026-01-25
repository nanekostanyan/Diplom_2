package registeruser;

import helper.BaseTest;
import helper.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.MatcherAssert.*;
import org.junit.After;
import org.junit.Test;
import pojo.*;

import static org.apache.http.HttpStatus.*;

public class RegisterUserTest extends BaseTest {
    private final UserApi userApi = new UserApi();

    @After
    @Step("shutDown")
    public void shutDown() {
        userApi.deleteUser();
        // В документации отсутствует ручка удаления заказа, так что чистить после себя заказы не нужно.
    }

    @Test
    @DisplayName("Регистрируем нового уникального пользователя")
    @Description("Отправляем запрос на регистрацию нового пользователя, с уникальными сгенерированными данными.")
    public void registerUniqueUser() {
        userApi.registerUserAndSaveToken();

        userApi.checkResponseSC(SC_OK); // Думаю, тут должно быть SC_CREATED (201)
        userApi.checkSuccessStatus(true);
        userApi.checkAccessTokenNotEmpty();
        userApi.checkRefreshTokenNotEmpty();
    }

    @Test
    @DisplayName("Повторно регистрируем того же пользователя")
    @Description("Создаём нового пользователя, после чего повторяем запрос на создание пользователя с теми же данными. " +
            "Ожидаем получить ошибку.")
    public void registerSameUser() {
        userApi.registerUserAndSaveToken();

        userApi.checkResponseSC(SC_OK); // Думаю тут должно быть SC_CREATED (201)
        userApi.checkSuccessStatus(true);

        userApi.registerUser(userApi.getLastUserRegisterRequest());

        userApi.checkResponseSC(SC_FORBIDDEN);
        userApi.checkSuccessStatus(false);
        userApi.checkMessageNotEmpty();
    }
}
