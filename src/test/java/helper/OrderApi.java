package helper;

import io.qameta.allure.Step;
import org.junit.Assert;
import pojo.CreateOrderRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class OrderApi extends Helper {
    private final IngredientsApi ingredientsApi = new IngredientsApi();

    private final String CREATE_ORDER_HANDLE = "/api/orders/";

    @Step("Создаём заказ без токена авторизации. Запрос создаётся внутри, на основе переданных ингредиентов")
    public void createOrder(List<String> ingredientHashes) {
        createOrder(ingredientHashes, null);
    }

    @Step("Создаём заказ с токеном авторизации. Запрос создаётся внутри, на основе переданных ингредиентов")
    public void createOrder(List<String> ingredientHashes, String token) {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setIngredients(ingredientHashes);
        createOrder(request, token);
    }

    @Step("Запрос создания заказа")
    private void createOrder(CreateOrderRequest request, String token) {
        if (token == null) {
            this.response = given()
                    .header("Content-type", "application/json")
                    .body(request)
                    .post(CREATE_ORDER_HANDLE);
        } else {
            this.response = given()
                    .header("Content-type", "application/json")
                    .header("Authorization", token)
                    .body(request)
                    .post(CREATE_ORDER_HANDLE);
        }
    }

    // Вспомогательные методы

    @Step("Скачиваем список ингредиентов")
    public void downloadIngredients() {
        ingredientsApi.getIngredients();
    }

    @Step("Создаём тестовый список хешей ингредиентов")
    public List<String> createIngredientHashes(int countOfIngredients, boolean needCorruptHash) {
        Random rand = new Random();
        int numOfRuinedHash = -1;
        if (needCorruptHash) {
            numOfRuinedHash = rand.nextInt(countOfIngredients);
        }
        List<String> ingredientHashes = new ArrayList<>();
        for (int i = 0; i < countOfIngredients; i++) {
            int index = rand.nextInt(ingredientsApi.ingredientsCount());
            String hash = ingredientsApi.getIngredientID(index);
            if (numOfRuinedHash != -1 && i == numOfRuinedHash) {
                hash = corruptHash(hash);
            }

            ingredientHashes.add(hash); // Ингридиенты могут повторяться в заказе, это нормально
        }
        return ingredientHashes;
    }

    @Step("Портим случайный символ хеша")
    public String corruptHash(String hash) {
        Assert.assertNotNull(hash);

        Random rand = new Random();
        int index = rand.nextInt(hash.length());

        StringBuilder sb = new StringBuilder(hash);
        sb.setCharAt(index, sb.charAt(index) == 'a' ? 'b' : 'a');

        return sb.toString();
    }
}
