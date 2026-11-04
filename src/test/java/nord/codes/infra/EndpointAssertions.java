package nord.codes.infra;

import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import nord.codes.dto.EndpointRs;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import static nord.codes.infra.ApiPaths.ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class EndpointAssertions {

    public void assertSuccessAction(EntityExchangeResult<EndpointRs> result) {
        Allure.step("Проверка успешного действия (result=OK)", () -> {
            log.debug("Проверка успешного действия для ответа {}", result);

            assertThat(result.getStatus())
                    .as("Response status %s should be %s".formatted(ENDPOINT, HttpStatus.OK))
                    .isEqualTo(HttpStatus.OK);

            EndpointRs endpointRs = result.getResponseBody();
            assertThat(endpointRs)
                    .as("Response %s should not be null".formatted(ENDPOINT))
                    .isNotNull();
            assertThat(endpointRs.getResult())
                    .as("Result %s should be OK".formatted(ENDPOINT))
                    .isEqualTo(HttpStatus.OK.getReasonPhrase());
            assertThat(endpointRs.getMessage())
                    .as("Message %s should be null".formatted(ENDPOINT))
                    .isNull();

            log.info("Проверка успешного действия прошла успешно\n");
        });
    }

    public void assertErrorAction(EntityExchangeResult<EndpointRs> result, HttpStatus status, String message) {
        Allure.step("Проверка неуспешного действия (result=ERROR)", () -> {
            log.debug("Проверка неуспешного действия для ответа {}", result);

            assertThat(result.getStatus())
                    .as("Response status %s should be %s".formatted(ENDPOINT, status))
                    .isEqualTo(status);

            EndpointRs endpointRs = result.getResponseBody();
            assertThat(endpointRs)
                    .as("Response %s should not be null".formatted(ENDPOINT))
                    .isNotNull();
            assertThat(endpointRs.getResult())
                    .as("Result %s should be ERROR".formatted(ENDPOINT))
                    .isEqualTo("ERROR");
            assertThat(endpointRs.getMessage())
                    .as("Message %s should be %s".formatted(ENDPOINT, message))
                    .contains(message);

            log.info("Проверка неуспешного действия прошла успешно\n");
        });
    }
}
