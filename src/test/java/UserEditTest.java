import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserEditTest {
    private User user;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.generateRandomUser();
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void editAuthorizedUserTest() {
        accessToken = userClient.create(user).extract().path("accessToken");
        user.setName("changedName");
        user.setEmail("changed@ema.il");
        ValidatableResponse editResponse = userClient.edit(user, accessToken);
        editResponse
                .statusCode(200)
                .assertThat()
                .body("success", equalTo(true))
                .and()
                .body("user.name", equalTo("changedName"))
                .and()
                .body("user.email", equalTo("changed@ema.il"));
    }
    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void editNonAuthorizedUserTest() {
        accessToken = userClient.create(user).extract().path("accessToken");
        user.setName("changedName");
        user.setEmail("changed@ema.il");
        ValidatableResponse editResponse = userClient.edit(user, "NO");
        editResponse
                .statusCode(401)
                .assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message",equalTo("You should be authorised"));
    }
}
