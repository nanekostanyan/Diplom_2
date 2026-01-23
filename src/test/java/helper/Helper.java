package helper;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Random;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

public class Helper {
    protected Response response;

    @Step("Проверяем код ответа")
    public void checkResponseSC(int sc) {
        assertNotNull(response);
        response.then().statusCode(sc);
    }

    @Step("Проверяем поле Status ответа")
    public void checkSuccessStatus(boolean flag) {
        assertNotNull(response);
        response.then().assertThat().body("success", equalTo(flag));
    }

    @Step("Генерируем случайную строку заданной длины")
    protected String generateRandomString(int length) {
        String characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characterSet.length());
            sb.append(characterSet.charAt(index));
        }
        return sb.toString();
    }

    @Step("Получаем код ответа")
    public int getResponseSC() {
        assertNotNull(response);
        return response.statusCode();
    }
}
