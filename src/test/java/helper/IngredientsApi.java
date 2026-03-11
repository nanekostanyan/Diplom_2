package helper;

import io.qameta.allure.Step;
import pojo.Ingredient;
import pojo.IngredientsResponse;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

public class IngredientsApi extends BaseApi {
    private List<Ingredient> ingredients;

    private final String INGREDIENTS_HANDLE = "/api/ingredients/";

    @Step("Получение ингредиентов")
    public void getIngredients() {
        this.response = given()
                .header("Content-type", "application/json")
                .get(INGREDIENTS_HANDLE);
        response.then().statusCode(SC_OK);

        IngredientsResponse resp = response.body().as(IngredientsResponse.class);
        this.ingredients = resp.getData();
        assertNotNull(this.ingredients);
    }

    @Step("Получаем список ингредиентов")
    public List<Ingredient> getLoadedIngredients() {
        return ingredients;
    }

    @Step("Получаем количество ингредиентов")
    public int ingredientsCount() {
        assertNotNull(ingredients);
        return ingredients.size();
    }

    @Step("Получаем ингредиент по его индексу")
    public Ingredient getIngredient(int i) {
        assertNotNull(ingredients);
        assertTrue(i <= ingredientsCount());
        return ingredients.get(i);
    }

    @Step("Получаем хеш ингредиента по его индексу")
    public String getIngredientID(int i) {
        return getIngredient(i).getId();
    }
}
