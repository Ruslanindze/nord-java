package nord.codes.infra;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

@Slf4j
public abstract class BaseTest {

    protected String baseUrl;
    protected String xApiKey;

    protected RestTestClient restTestClient;

    @BeforeEach
    protected void setUp() {
        baseUrl = System.getProperty("app.url", "http://localhost:8080");
        xApiKey = Objects.requireNonNull(
                System.getProperty("api.key"),
                "API Key must be provided via -Dapi.key"
        );

        restTestClient = RestTestClient.bindToServer()
                .baseUrl(baseUrl)
                .defaultHeader("X-Api-Key", xApiKey)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    protected MultiValueMap<String, String> createFormUrlEncodedValue(String token, Action action) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("token", token);
        formData.add("action", action.name());

        return formData;
    }
}
