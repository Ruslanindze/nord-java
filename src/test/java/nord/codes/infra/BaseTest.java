package nord.codes.infra;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.http.HttpHeaders;


@SpringBootTest
public abstract class BaseTest {

    @Value("${app.url}")
    private String baseUrl;

    @Value("${app.x-api-key:default_dummy_key}")
    private String xApiKey;

    protected RestTestClient restTestClient;

    @BeforeEach
    protected void setUp() {
        restTestClient = RestTestClient.bindToServer()
                .baseUrl(baseUrl)
                .defaultHeader("X-Api-Key", xApiKey)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }
}
