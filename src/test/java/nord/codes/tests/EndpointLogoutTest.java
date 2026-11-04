package nord.codes.tests;

import lombok.extern.slf4j.Slf4j;
import nord.codes.dto.EndpointRs;
import nord.codes.infra.Action;
import nord.codes.infra.ApiPaths;
import nord.codes.infra.BaseTest;
import nord.codes.infra.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import static nord.codes.infra.TestTags.*;

@Slf4j
@DisplayName("Завершение сессии пользователя")
public class EndpointLogoutTest extends BaseTest implements ApiPaths {

    @Nested
    @Tag(POSITIVE)
    @DisplayName("Успешное завершение сессии")
    class PositiveLogout {

        @DisplayName("Токен")
        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {
                TestData.VALID_TOKEN,
                TestData.VALID_TOKEN_2,
                TestData.VALID_TOKEN_3
        })
        void shouldReturnOkWhenLogoutWithAuthToken(String token) {
            // Precondition => Action пока не работает
            String message = "Token '%s' not found".formatted(token);
            EntityExchangeResult<EndpointRs> actionResult = endpointClient.sendAction(token, Action.ACTION);
            endpointAssertions.assertErrorAction(actionResult, HttpStatus.FORBIDDEN, message);

            // Given => Action стал работать
            EntityExchangeResult<EndpointRs> loginRs = endpointClient.sendAction(token, Action.LOGIN);
            endpointAssertions.assertSuccessAction(loginRs);
            EntityExchangeResult<EndpointRs> actionResult2 = endpointClient.sendAction(token, Action.ACTION);
            endpointAssertions.assertSuccessAction(actionResult2);

            // When
            EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(token, Action.LOGOUT);

            // Then => Action перестал работать
            endpointAssertions.assertSuccessAction(actualRs);
            // Повторный выход - клиент не найден
            EntityExchangeResult<EndpointRs> actionResult3 = endpointClient.sendAction(token, Action.ACTION);
            endpointAssertions.assertErrorAction(actionResult3, HttpStatus.FORBIDDEN, message);
        }

        @Nested
        @Tag(NEGATIVE)
        @DisplayName("Неудачное завершение сессии")
        class NegativeLogout {

            @DisplayName("Токен")
            @ParameterizedTest(name = "{0}")
            @ValueSource(strings = {
                    TestData.VALID_TOKEN,
                    TestData.VALID_TOKEN_2,
                    TestData.VALID_TOKEN_3
            })
            void shouldReturnClientErrorWhenSessionIsClosed(String token) {
                // Given - token

                // When
                EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(token, Action.LOGOUT);

                // Then
                String message = "Token '%s' not found".formatted(token);
                endpointAssertions.assertErrorAction(actualRs, HttpStatus.FORBIDDEN, message);
            }

            @Test
            @Tag(SMOKE)
            @DisplayName("Завершение сессии с не авторизованным токеном")
            void shouldReturnClientErrorWhenSessionIsAlreadyClosed() {
                // Given
                String token = TestData.VALID_TOKEN;
                EntityExchangeResult<EndpointRs> loginRs = endpointClient.sendAction(token, Action.LOGIN);
                endpointAssertions.assertSuccessAction(loginRs);

                // When
                // 1-й запрос
                EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(token, Action.LOGOUT);
                endpointAssertions.assertSuccessAction(actualRs);
                // 2-й запрос (дубль)
                EntityExchangeResult<EndpointRs> actualRs2 = endpointClient.sendAction(token, Action.LOGOUT);

                // Then
                String message = "Token '%s' not found".formatted(token);
                endpointAssertions.assertErrorAction(actualRs2, HttpStatus.FORBIDDEN, message);
            }
        }
    }
}
