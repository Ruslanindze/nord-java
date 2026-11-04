package nord.codes.infra.clients;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import nord.codes.dto.EndpointRs;
import nord.codes.infra.Action;
import nord.codes.infra.ApiPaths;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


public class EndpointClient {
    private final RestTestClient restTestClient;

    public EndpointClient(RestTestClient restTestClient) {
        this.restTestClient = restTestClient;
    }

    @Step("Отправка действия {action} с токеном: {token}")
    public EntityExchangeResult<EndpointRs> sendAction(String token, Action action) {
        return Allure.step("Отправка действия " + action + " с токеном: " + token, () -> {
            MultiValueMap<String, String> rqBody = createFormUrlEncodedValue(token, action);

            Allure.attachment("Request body", rqBody.toString());

            EntityExchangeResult<EndpointRs> result = restTestClient.post()
                    .uri(ApiPaths.ENDPOINT)
                    .body(rqBody)
                    .exchange()
                    .expectBody(EndpointRs.class)
                    .returnResult();

            // Прикрепляем ответ
            Allure.attachment("Response Status", String.valueOf(result.getStatus().value()));
            if (result.getResponseBody() != null) {
                Allure.attachment("Response Body",
                        MediaType.APPLICATION_JSON_VALUE, result.getResponseBody().toString());
            }

            return result;
        });
    }

    private MultiValueMap<String, String> createFormUrlEncodedValue(String token, Action action) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        if (token != null) formData.add("token", token);
        if (action != null) formData.add("action", action.name());

        return formData;
    }
}
