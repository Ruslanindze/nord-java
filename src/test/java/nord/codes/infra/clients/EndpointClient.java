package nord.codes.infra.clients;

import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import nord.codes.dto.EndpointRs;
import nord.codes.infra.Action;
import nord.codes.infra.ApiPaths;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@Slf4j
public class EndpointClient {
    private final RestTestClient restTestClient;

    public EndpointClient(RestTestClient restTestClient) {
        this.restTestClient = restTestClient;
    }

    public EntityExchangeResult<EndpointRs> sendAction(String token, Action action) {
        return Allure.step("Отправка действия " + action + " с токеном: " + token, () -> {
            log.info("Отправка действия {} с токеном: {}", action, token);

            MultiValueMap<String, String> rqBody = createFormUrlEncodedValue(token, action);

            Allure.attachment("Request body", rqBody.toString());
            log.info("Сформировано тело запроса: {}", rqBody);

            EntityExchangeResult<EndpointRs> result = restTestClient.post()
                    .uri(ApiPaths.ENDPOINT)
                    .body(rqBody)
                    .exchange()
                    .expectBody(EndpointRs.class)
                    .returnResult();

            // Прикрепляем ответ
            Allure.attachment("Response Status", String.valueOf(result.getStatus().value()));
            log.info("Статус ответа: {}", result.getStatus());

            if (result.getResponseBody() != null) {
                Allure.attachment("Response Body", result.getResponseBody().toString());
                log.info("Тело ответа: {}", result.getResponseBody());
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
