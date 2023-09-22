import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

public class OrderCreateTest {
    private ClientOrders clientOrders;
    private Orders orders;
    private User user;
    private UserClient userClient;
    private String accessToken;

    List<String> ingredients = new ArrayList<>();

    @Before
    public void setUp() {
        user = User.generateRandomUser();
        userClient = new UserClient();
        clientOrders = new ClientOrders();
        ingredients.add(clientOrders.getIngredients().extract().path("data[0]._id"));
        ingredients.add(clientOrders.getIngredients().extract().path("data[1]._id"));
        orders = new Orders(ingredients);
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWAuthTest() {
        accessToken = userClient.create(user).extract().path("accessToken");
        ValidatableResponse orderResponse = clientOrders.createWAuth(accessToken, orders);
        orderResponse
                .statusCode(200)
                .assertThat()
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWOAuthTest() {
        ValidatableResponse orderResponse = clientOrders.createWOAuth(orders);
        orderResponse
                .statusCode(200)
                .assertThat()
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWOIngredientsTest() {
        ingredients.clear();
        Orders orderWrong = new Orders(ingredients);
        ValidatableResponse orderResponse = clientOrders.createWOAuth(orderWrong);
        orderResponse
                .statusCode(400)
                .assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message",equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    public void createOrderWIngredientsTest() {
        accessToken = userClient.create(user).extract().path("accessToken");
        ValidatableResponse orderResponse = clientOrders.createWAuth(accessToken, orders);
        orderResponse
                .statusCode(200)
                .assertThat()
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента")
    public void createOrderWWrongIngredientsTest() {
        ingredients.add("wrong_hash");
        Orders orderWrong = new Orders(ingredients);
        ValidatableResponse orderResponse = clientOrders.createWOAuth(orderWrong);
        orderResponse
                .statusCode(500);
    }
    @Test
    @DisplayName("Получить список заказов с авторизацией")
    public void getDataOrderWithAuthTest() {
        accessToken = userClient.create(user).extract().path("accessToken");
        ValidatableResponse orderResponse = clientOrders.getOrdersDataUser(accessToken);
        orderResponse
                .statusCode(200)
                .assertThat()
                .body("success", equalTo(true));
    }
    @Test
    @DisplayName("Получить список заказов без авторизации")
    public void getDataOrderWOAuthTest() {
        accessToken = "";
        ValidatableResponse orderResponse = clientOrders.getOrdersDataUser(accessToken);
        orderResponse
                .statusCode(401)
                .assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message",equalTo("You should be authorised"));
    }
}
