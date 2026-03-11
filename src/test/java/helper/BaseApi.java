package helper;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import net.datafaker.Faker;

import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

public class BaseApi {
    protected Response response;
    protected static final Faker faker = new Faker(Locale.ENGLISH);

    private final String EMAIL_DOMAIN_NAME = "@yandex.ru";

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
        return faker.regexify(String.format("[a-zA-Z0-9]{%d}", length));
    }

    @Step("Генерируем случайный Email заданной длины")
    protected String generateRandomEmail(int length) {
        return generateRandomString(length) + EMAIL_DOMAIN_NAME;
    }

    @Step("Получаем код ответа")
    public int getResponseSC() {
        assertNotNull(response);
        return response.statusCode();
    }
}
