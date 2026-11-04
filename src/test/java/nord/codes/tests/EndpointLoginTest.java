package nord.codes.tests;

import lombok.extern.slf4j.Slf4j;
import nord.codes.dto.EndpointRs;
import nord.codes.infra.Action;
import nord.codes.infra.ApiPaths;
import nord.codes.infra.BaseTest;
import nord.codes.infra.TestData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import java.util.stream.Stream;

import static nord.codes.infra.TestTags.*;


@Slf4j
@Tag(REGRESS)
@DisplayName("Аутентификация")
public class EndpointLoginTest extends BaseTest implements ApiPaths {
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
    @DisplayName("Успешная авторизация")
    class PositiveLogin {

        @DisplayName("Токен")
        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = {
                TestData.VALID_TOKEN,
                TestData.VALID_TOKEN_2,
                TestData.VALID_TOKEN_3
        })
        void shouldReturnOkWhenLoggingInWithNewValidToken(String token) {
            // Given
            activeToken = token;

            // When
            EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(activeToken, Action.LOGIN);

            // Then
            endpointAssertions.assertSuccessAction(actualRs);
        }
    }

    @Nested
    @Tag(NEGATIVE)
    @DisplayName("Неуспешная авторизация")
    class NegativeLogin {

        @Test
        @DisplayName("Токен равен null")
        void shouldReturnClientErrorWhenLoggingInWithNullToken() {
            // Given
            String token = null;

            // When
            EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(token, Action.LOGIN);

            // Then
            String message = "token: не должно равняться null";
            endpointAssertions.assertErrorAction(actualRs, HttpStatus.BAD_REQUEST, message);
        }

        @Test
        @Tag(SMOKE)
        @DisplayName("Токен уже авторизован")
        void shouldReturnClientErrorWhenLoggingInWithDuplicateToken() {
            // Given
            activeToken = TestData.VALID_TOKEN;

            // When
            // 1-й запрос
            EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(activeToken, Action.LOGIN);
            endpointAssertions.assertSuccessAction(actualRs);
            // 2-й запрос (дубль)
            EntityExchangeResult<EndpointRs> actualRs2 = endpointClient.sendAction(activeToken, Action.LOGIN);

            // Then
            String message = "Token '%s' already exists".formatted(TestData.VALID_TOKEN);
            endpointAssertions.assertErrorAction(actualRs2, HttpStatus.CONFLICT, message);
        }

        @DisplayName("Невалидный токен")
        @ParameterizedTest(name = "{0}")
        @MethodSource
        void shouldReturnClientErrorWhenLoggingInWithInvalidToken(String token, String message) {
            // Given - token

            // When
            EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(token, Action.LOGIN);

            // Then
            endpointAssertions.assertErrorAction(actualRs, HttpStatus.BAD_REQUEST, message);
        }

        static Stream<Arguments> shouldReturnClientErrorWhenLoggingInWithInvalidToken() {
            return Stream.of(
                    Arguments.of(TestData.ERR_TOKEN_400_1, "token: должно соответствовать"),
                    Arguments.of(TestData.ERR_TOKEN_400_2, "token: должно соответствовать"),
                    Arguments.of(TestData.ERR_TOKEN_400_3, "token: должно соответствовать"),
                    Arguments.of(TestData.ERR_TOKEN_400_4, "token: должно соответствовать")
            );
        }

        @DisplayName("Ошибка сервера")
        @ParameterizedTest(name = "{0}")
        @MethodSource
        void shouldReturnServerErrorWhenLoggingInWithValidToken(String token, HttpStatus httpStatus) {
            // Given - token

            // When
            EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(token, Action.LOGIN);

            // Then
            endpointAssertions.assertErrorAction(actualRs, httpStatus, httpStatus.getReasonPhrase());
        }

        static Stream<Arguments> shouldReturnServerErrorWhenLoggingInWithValidToken() {
            return Stream.of(
                    Arguments.of(TestData.MOCK_ERR_TOKEN_500, HttpStatus.INTERNAL_SERVER_ERROR)
            );
        }
    }
}
