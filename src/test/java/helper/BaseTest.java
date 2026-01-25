package helper;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.mapper.ObjectMapperType;
import org.junit.BeforeClass;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

public class BaseTest {
    @BeforeClass
    @Step("globalSetUp")
    public static void globalSetUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        RestAssured.config = RestAssured.config()
                .objectMapperConfig(
                        objectMapperConfig()
                                .defaultObjectMapperType(ObjectMapperType.GSON)
                );
    }
}
