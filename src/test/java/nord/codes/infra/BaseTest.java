package nord.codes.infra;

import lombok.extern.slf4j.Slf4j;
import nord.codes.infra.clients.EndpointClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
public abstract class BaseTest {
    private long startTime;
    private String testName;

    protected String baseUrl;
    protected String xApiKey;

    protected RestTestClient restTestClient;
    protected EndpointClient endpointClient;
    protected EndpointAssertions endpointAssertions;

    @BeforeEach
    protected void setUp(TestInfo testInfo) {
        startTime = System.currentTimeMillis();
        String className = testInfo.getTestClass()
                .map(Class::getSimpleName).orElse("");
        String methodName = testInfo.getTestMethod()
                .map(Method::getName).orElse("UnknownMethod");
        testName = className + "." + methodName;

        log.info("\n" + "=".repeat(100));
        log.info("Запуск теста: '{}'", testName);
        log.info("Описание: '{}'", testInfo.getDisplayName());

        // ----------------------------------------------------------------------
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

        endpointClient = new EndpointClient(restTestClient);

        endpointAssertions = new EndpointAssertions();
    }

    @AfterEach
    protected void tearDown(TestInfo testInfo) {
        long duration = System.currentTimeMillis() - startTime;

        log.info("Тест '{}' выполнен за {} мс", testName, duration);
        log.info("\n" + "=".repeat(100));
    }
}
