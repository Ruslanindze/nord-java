package nord.codes.tests;

import lombok.extern.slf4j.Slf4j;
import nord.codes.dto.EndpointRs;
import nord.codes.infra.Action;
import nord.codes.infra.ApiPaths;
import nord.codes.infra.BaseTest;
import nord.codes.infra.TestData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import static nord.codes.infra.TestTags.*;

@Slf4j
@Tag(REGRESS)
@DisplayName("Действие")
public class EndpointActionTest extends BaseTest implements ApiPaths {
    private String activeToken;

    @AfterEach
    void logoutToken() {
        if (activeToken != null) {
            EntityExchangeResult<EndpointRs> rs = endpointClient.sendAction(activeToken, Action.LOGOUT);
            endpointAssertions.assertSuccessAction(rs);
        }
    }

    @Nested
    @Tag(POSITIVE)
    @Tag(SMOKE)
    @DisplayName("Успешное действие")
    class PositiveAction {

        @DisplayName("Токен")
        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {
                TestData.VALID_TOKEN,
                TestData.VALID_TOKEN_2,
                TestData.VALID_TOKEN_3
        })
        void shouldReturnOkWhenActionWithAuthToken(String token) {
            // Given
            activeToken = token;
            EntityExchangeResult<EndpointRs> loginRs = endpointClient.sendAction(activeToken, Action.LOGIN);
            endpointAssertions.assertSuccessAction(loginRs);

            // When
            EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(activeToken, Action.ACTION);

            // Then
            endpointAssertions.assertSuccessAction(actualRs);
        }

        @Test
        @DisplayName("Многократное выполнение")
        void shouldReturnOkWhenMultipleActionWithAuthToken() {
            // Given
            int count = 3;
            activeToken = TestData.VALID_TOKEN;
            EntityExchangeResult<EndpointRs> loginRs = endpointClient.sendAction(activeToken, Action.LOGIN);
            endpointAssertions.assertSuccessAction(loginRs);

            // When and Then
            for (int i = 0; i < count; i++) {
                // When
                EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(activeToken, Action.ACTION);
                // Then
                endpointAssertions.assertSuccessAction(actualRs);
            }
        }
    }

    @Nested
    @Tag(NEGATIVE)
    @DisplayName("Действие без авторизации")
    class NegativeAction {

        @DisplayName("Токен")
        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {
                TestData.VALID_TOKEN,
                TestData.VALID_TOKEN_2,
                TestData.VALID_TOKEN_3
        })
        void shouldReturnClientErrorWhenActionWithNoAuthToken(String token) {
            // Given - token

            // When
            EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(token, Action.ACTION);

            // Then
            String message = "Token '%s' not found".formatted(token);
            endpointAssertions.assertErrorAction(actualRs, HttpStatus.FORBIDDEN, message);
        }
    }
}
