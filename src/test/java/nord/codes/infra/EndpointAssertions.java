package nord.codes.infra;

import io.qameta.allure.Allure;
import nord.codes.dto.EndpointRs;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import static nord.codes.infra.ApiPaths.ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;

public class EndpointAssertions {

    public void assertSuccessAction(EntityExchangeResult<EndpointRs> result) {
        Allure.step("Проверка успешного действия (result=OK)", () -> {
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
        });
    }

    public void assertErrorAction(EntityExchangeResult<EndpointRs> result, HttpStatus status, String message) {
        Allure.step("Проверка неуспешного действия (result=ERROR)", () -> {
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
        });
    }
}
