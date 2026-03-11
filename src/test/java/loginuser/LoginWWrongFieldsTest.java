package loginuser;

import helper.BaseTest;
import helper.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pojo.LoginUserRequest;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

@RunWith(Parameterized.class)
public class LoginWWrongFieldsTest extends BaseTest {
    private final UserApi userApi = new UserApi();
    private LoginUserRequest request;

    private final boolean ruinEmail;
    private final boolean ruinPassword;

    public LoginWWrongFieldsTest(String name, boolean ruinEmail, boolean ruinPassword) {
        this.ruinEmail = ruinEmail;
        this.ruinPassword = ruinPassword;
    }

    @Parameterized.Parameters(name = "Попытка авторизации с некорректными данными: {0}")
    @Step("Получаем параметры теста")
    public static Object[] getFields() {
        return new Object[][] {
            {
                "Неверный email", true, false
            },
            {
                "Неверный password", false, true
            },
            {
                "Неверные email и password", true, true
            },
        };
    }

    @Before
    @Step("setUp")
    public void setUp() {
        userApi.registerUserAndSaveToken();
        userApi.checkResponseSC(SC_OK);

        request = userApi.createLoginUserRequestFromLastRegisterRequest();
        if (ruinEmail) {
            request.setEmail("abraca"+request.getEmail());
        }
        if (ruinPassword) {
            request.setPassword("dabra"+request.getPassword());
        }
    }

    @After
    @Step("shutDown")
    public void shutDown() {
        userApi.deleteUser();
    }

    @Test
    @Description("Параметризованный тест. Пробуем авторизоваться, передавая некорректные Email и Password.")
    public void loginWWrongEmailAndPassword() {
        userApi.loginUser(request);

        userApi.checkResponseSC(SC_UNAUTHORIZED);
        userApi.checkSuccessStatus(false);
        userApi.isMessageHasEmailPasswordError();
    }
}
