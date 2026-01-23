package helper;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.BeforeClass;

public class BaseTest {
    @BeforeClass
    @Step("globalSetUp")
    public static void globalSetUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}
