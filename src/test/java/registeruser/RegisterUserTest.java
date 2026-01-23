package registeruser;

import helper.BaseTest;
import helper.UserApi;
import io.qameta.allure.Step;
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
    @Step("Регистрируем нового уникального пользователя")
    public void registerUniqueUser() {
        userApi.registerUserAndSaveToken();

        userApi.checkResponseSC(SC_OK); // Думаю, тут должно быть SC_CREATED (201)
        userApi.checkSuccessStatus(true);
        userApi.checkAccessTokenNotEmpty();
        userApi.checkRefreshTokenNotEmpty();
    }

    @Test
    @Step("Повторно регистрируем того же пользователя")
    public void registerSameUser() {
        userApi.registerUserAndSaveToken();

        userApi.checkResponseSC(SC_OK); // Думаю тут должно быть SC_CREATED (201)
        userApi.checkSuccessStatus(true);

        userApi.registerUser(userApi.getLastUserRegisterRequest());

        userApi.checkResponseSC(SC_FORBIDDEN);
        userApi.checkSuccessStatus(false);
    }
}
