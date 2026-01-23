package createorder;

import helper.BaseTest;
import helper.OrderApi;
import helper.UserApi;
import io.qameta.allure.Step;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.swing.*;
import java.util.List;

import static org.apache.http.HttpStatus.*;

@RunWith(Parameterized.class)
public class CreateOrderWParamsTest extends BaseTest {
    private final UserApi userApi = new UserApi();
    private final OrderApi orderApi = new OrderApi();

    private final boolean isAuthorize;
    private final int countOfIngredients;
    private final boolean needCorruptHash;
    private final int expectedStatusCode;

    public CreateOrderWParamsTest(String name, boolean isAuthorize, int countOfIngredients, boolean needCorruptHash, int expectedStatusCode) {
        this.isAuthorize = isAuthorize;
        this.countOfIngredients = countOfIngredients;
        this.needCorruptHash = needCorruptHash;
        this.expectedStatusCode = expectedStatusCode;
    }

    @Before
    @Step("setUp")
    public void setUp() {
        userApi.registerUserAndSaveToken();
        userApi.checkResponseSC(SC_OK);
        orderApi.downloadIngredients();
    }

    @After
    @Step("shutDown")
    public void shutDown() {
        userApi.deleteUser();
    }

    @Parameterized.Parameters(name = "{0}")
    @Step("Получаем параметры теста")
    public static Object[] getFields() {
        return new Object[][] {
                {
                        "Заказ авторизованным пользователем, 0 ингредиентов", true, 0, false, SC_BAD_REQUEST
                },
                {
                        "Заказ авторизованным пользователем, 1 ингредиент", true, 1, false, SC_OK
                },
                {
                        "Заказ авторизованным пользователем, 3 ингредиента", true, 3, false, SC_OK
                },
                {
                        "Заказ авторизованным пользователем, 10 ингредиентов", true, 10, false, SC_OK
                },
                {
                        "Заказ неавторизованным пользователем, 0 ингредиентов", false, 0, false, SC_UNAUTHORIZED
                },
                {
                        "Заказ неавторизованным пользователем, 1 ингредиент", false, 1, false, SC_UNAUTHORIZED
                },
                {
                        "Заказ неавторизованным пользователем, 3 ингредиента", false, 3, false, SC_UNAUTHORIZED
                },
                {
                        "Заказ неавторизованным пользователем, 10 ингредиентов", false, 10, false, SC_UNAUTHORIZED
                },
                {
                        "Заказ авторизованным пользователем, 1 ингредиент, ошибки в хеше", true, 1, true, SC_INTERNAL_SERVER_ERROR
                },
                {
                        "Заказ авторизованным пользователем, 3 ингредиента, ошибки в хеше", true, 3, true, SC_INTERNAL_SERVER_ERROR
                },
                {
                        "Заказ авторизованным пользователем, 10 ингредиентов, ошибки в хеше", true, 10, true, SC_INTERNAL_SERVER_ERROR
                },
                {
                        "Заказ неавторизованным пользователем, 1 ингредиент, ошибки в хеше", false, 1, true, SC_UNAUTHORIZED
                },
                {
                        "Заказ неавторизованным пользователем, 10 ингредиентов, ошибки в хеше", false, 10, true, SC_UNAUTHORIZED
                },
        };
    }

    @Test
    @Step("Оформляем заказ")
    public void CreateOrderWParams() {
        List<String> ingredientHashes = orderApi.createIngredientHashes(countOfIngredients, needCorruptHash);

        if (isAuthorize) {
            orderApi.createOrder(ingredientHashes, userApi.getAccessToken());
        } else {
            orderApi.createOrder(ingredientHashes);
        }

        orderApi.checkResponseSC(expectedStatusCode);
    }
}
