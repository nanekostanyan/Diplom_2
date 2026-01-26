package registeruser;

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
import pojo.UserRegisterRequest;

import static org.apache.http.HttpStatus.*;

@RunWith(Parameterized.class)
public class RegisterUserWoFieldsTest extends BaseTest {
    private final UserApi userApi = new UserApi();
    private UserRegisterRequest request;

    private final boolean removeEmail;
    private final boolean removePassword;
    private final boolean removeName;

    public RegisterUserWoFieldsTest(String name, boolean removeEmail, boolean removePassword, boolean removeName) {
        this.removeEmail = removeEmail;
        this.removePassword = removePassword;
        this.removeName = removeName;
    }

    @Parameterized.Parameters(name = "Безуспешная попытка регистрации пользователя без обязательных полей: {0}")
    @Step("Получаем параметры теста")
    public static Object[] getFields() {
        return new Object[][] {
            {
                "Отсутствует email", true, false, false
            },
            {
                "Отсутствует password", false, true, false
            },
            {
                "Отсутствует name", false, false, true
            },
        };
    }

    @Before
    @Step("setUp")
    public void setUp() {
        this.request = userApi.createRegisterRequest();
        if (this.removeEmail) {
            request.setEmail("");
        }
        if (this.removePassword) {
            request.setPassword("");
        }
        if (this.removeName) {
            request.setName("");
        }
    }

    @After
    @Step("shutDown")
    public void shutDown() {
        if (userApi.getResponseSC() != SC_OK && userApi.getResponseSC() != SC_CREATED) {
            return;
        }
        userApi.saveRegisterResponse();
        userApi.deleteUser();
    }

    @Test
    @Description("Параметризованный тест. Пробуем зарегистрировать нового пользователя, не передавая обязательные " +
            "поля Email, Name и Password.")
    public void registerUserWoField() {
        userApi.registerUser(request);

        userApi.checkResponseSC(SC_FORBIDDEN);
        userApi.checkSuccessStatus(false);
        userApi.isNotAllRequiredFieldsError();
    }
}
