package loginuser;

import helper.BaseTest;
import helper.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;

public class LoginUserTest extends BaseTest {
    private final UserApi userApi = new UserApi();

    @Before
    @Step("setUp")
    public void setUp() {
        userApi.registerUserAndSaveToken();
        userApi.checkResponseSC(SC_OK);
    }

    @After
    @Step("shutDown")
    public void shutDown() {
        // В документации не сказано, что после авторизации обязательно нужно выйти до удаления,
        // поэтому не добавила выход из учётной записи, а просто удаляю созданного пользователя.
        userApi.deleteUser();
    }

    @Test
    @DisplayName("Успешная авторизация пользователя с валидными данными")
    @Description("Отправляем запрос на авторизацию, передавая валидные данные.")
    public void loginAsExistingUser() {
        userApi.loginUser();

        userApi.checkResponseSC(SC_OK);
        userApi.checkSuccessStatus(true);
        // Возможно проверка не обязательна, но в документации нигде не сказано, что токены могут меняться после
        // регистрации.
        // AccessToken - сказано, что токен может быть либо просрочен после 20 минут, но он просто станет невалидным,
        // не должен меняться сам. Измениться он может только при обращении в ручку /api/auth/token с RefreshToken.
        // RefreshToken - в документации не сказано, что он может меняться.
        userApi.compareRegisterAndLoginFields();
    }
}
