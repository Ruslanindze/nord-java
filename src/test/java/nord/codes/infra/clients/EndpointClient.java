package nord.codes.infra.clients;

import nord.codes.dto.EndpointRs;
import nord.codes.infra.Action;
import nord.codes.infra.ApiPaths;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


public class EndpointClient {
    private final RestTestClient restTestClient;

    public EndpointClient(RestTestClient restTestClient) {
        this.restTestClient = restTestClient;
    }

    public EntityExchangeResult<EndpointRs> sendAction(String token, Action action) {
        MultiValueMap<String, String> rqBody = createFormUrlEncodedValue(token, action);

        return restTestClient.post()
                .uri(ApiPaths.ENDPOINT)
                .body(createFormUrlEncodedValue(token, action))
                .exchange()
                .expectBody(EndpointRs.class)
                .returnResult();
    }

    private MultiValueMap<String, String> createFormUrlEncodedValue(String token, Action action) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        if (token != null) formData.add("token", token);
        if (action != null) formData.add("action", action.name());

        return formData;
    }
}
