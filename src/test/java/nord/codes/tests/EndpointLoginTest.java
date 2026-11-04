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

import static nord.codes.infra.TestTags.NEGATIVE;
import static nord.codes.infra.TestTags.POSITIVE;
import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
public class EndpointLoginTest extends BaseTest implements ApiPaths {
    private String activeToken;

    @AfterEach
    void logoutToken() {
        if (activeToken != null) {
            endpointClient.sendAction(activeToken, Action.LOGOUT);
        }
    }

    @Nested
    @DisplayName("Позитивные сценарии")
    @Tag(POSITIVE)
    class Positive {

        @ParameterizedTest(name = "Авторизация нового токена | {0}")
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
            assertThat(actualRs.getStatus())
                    .as("Response status %s should be %s".formatted(ENDPOINT, HttpStatus.OK))
                    .isEqualTo(HttpStatus.OK);

            EndpointRs actualEndpointRs = actualRs.getResponseBody();
            assertThat(actualEndpointRs)
                    .as("Response %s should not be null".formatted(ENDPOINT))
                    .isNotNull();
            assertThat(actualEndpointRs.getResult())
                    .as("Result %s should be OK".formatted(ENDPOINT))
                    .isEqualTo(HttpStatus.OK.getReasonPhrase());
            assertThat(actualEndpointRs.getMessage())
                    .as("Message %s should be null".formatted(ENDPOINT))
                    .isNull();
        }
    }

    @Nested
    @DisplayName("Негативные сценарии")
    @Tag(NEGATIVE)
    class Negative {

        @DisplayName("Ошибка клиента при авторизации | null")
        @Test
        void shouldReturnClientErrorWhenLoggingInWithNullToken() {
            // Given
            String token = null;

            // When
            EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(token, Action.LOGIN);

            // Then
            assertThat(actualRs.getStatus())
                    .as("Response status %s should be %s".formatted(ENDPOINT, HttpStatus.BAD_REQUEST))
                    .isEqualTo(HttpStatus.BAD_REQUEST);

            EndpointRs actualEndpointRs = actualRs.getResponseBody();
            String message = "token: не должно равняться null";
            assertThat(actualEndpointRs)
                    .as("Response %s should not be null".formatted(ENDPOINT))
                    .isNotNull();
            assertThat(actualEndpointRs.getResult())
                    .as("Result %s should be ERROR".formatted(ENDPOINT))
                    .isEqualTo("ERROR");
            assertThat(actualEndpointRs.getMessage())
                    .as("Message %s should be %s".formatted(ENDPOINT, message))
                    .contains(message);
        }

        @DisplayName("Ошибка клиента при авторизации | дубликат")
        @Test
        void shouldReturnClientErrorWhenLoggingInWithDuplicateToken() {
            // Given
            activeToken = TestData.VALID_TOKEN;

            // When
            // 1-й запрос
            EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(activeToken, Action.LOGIN);
            assertThat(actualRs.getStatus())
                    .as("Response status %s should be %s".formatted(ENDPOINT, HttpStatus.OK))
                    .isEqualTo(HttpStatus.OK);
            // 2-й запрос (дубль)
            EntityExchangeResult<EndpointRs> actualRs2 = endpointClient.sendAction(activeToken, Action.LOGIN);

            // Then
            assertThat(actualRs2.getStatus())
                    .as("Response status %s should be %s".formatted(ENDPOINT, HttpStatus.CONFLICT))
                    .isEqualTo(HttpStatus.CONFLICT);

            EndpointRs actualEndpointRs2 = actualRs2.getResponseBody();
            String message = "Token '%s' already exists".formatted(TestData.VALID_TOKEN);
            // проверка дубля
            assertThat(actualEndpointRs2)
                    .as("Response %s should not be null".formatted(ENDPOINT))
                    .isNotNull();
            assertThat(actualEndpointRs2.getResult())
                    .as("Result %s should be ERROR".formatted(ENDPOINT))
                    .isEqualTo("ERROR");
            assertThat(actualEndpointRs2.getMessage())
                    .as("Message %s should be %s".formatted(ENDPOINT, message))
                    .contains(message);
        }

        @ParameterizedTest(name = "Ошибка клиента при авторизации | {0}")
        @MethodSource
        void shouldReturnClientErrorWhenLoggingInWithInvalidToken(String token, String message) {
            // Given - token

            // When
            EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(token, Action.LOGIN);

            // Then
            assertThat(actualRs.getStatus())
                    .as("Response status %s should be %s".formatted(ENDPOINT, HttpStatus.BAD_REQUEST))
                    .isEqualTo(HttpStatus.BAD_REQUEST);

            EndpointRs actualEndpointRs = actualRs.getResponseBody();
            assertThat(actualEndpointRs)
                    .as("Response %s should not be null".formatted(ENDPOINT))
                    .isNotNull();
            assertThat(actualEndpointRs.getResult())
                    .as("Result %s should be ERROR".formatted(ENDPOINT))
                    .isEqualTo("ERROR");
            assertThat(actualEndpointRs.getMessage())
                    .as("Message %s should be %s".formatted(ENDPOINT, message))
                    .contains(message);
        }

        static Stream<Arguments> shouldReturnClientErrorWhenLoggingInWithInvalidToken() {
            return Stream.of(
                    Arguments.of(TestData.ERR_TOKEN_400_1, "token: должно соответствовать"),
                    Arguments.of(TestData.ERR_TOKEN_400_2, "token: должно соответствовать"),
                    Arguments.of(TestData.ERR_TOKEN_400_3, "token: должно соответствовать")
            );
        }

        @ParameterizedTest(name = "Ошибка сервера при авторизации | {0}")
        @MethodSource
        void shouldReturnServerErrorWhenLoggingInWithValidToken(String token, HttpStatus httpStatus) {
            // Given - token

            // When
            EntityExchangeResult<EndpointRs> actualRs = endpointClient.sendAction(token, Action.LOGIN);

            // Then
            assertThat(actualRs.getStatus())
                    .as("Response status %s should be %s".formatted(ENDPOINT, httpStatus))
                    .isEqualTo(httpStatus);

            EndpointRs actualEndpointRs = actualRs.getResponseBody();
            assertThat(actualEndpointRs)
                    .as("Response %s should not be null".formatted(ENDPOINT))
                    .isNotNull();
            assertThat(actualEndpointRs.getResult())
                    .as("Result %s should be ERROR".formatted(ENDPOINT))
                    .isEqualTo("ERROR");
            assertThat(actualEndpointRs.getMessage())
                    .as("Message %s should be %s".formatted(ENDPOINT, httpStatus.getReasonPhrase()))
                    .contains(httpStatus.getReasonPhrase());
        }

        static Stream<Arguments> shouldReturnServerErrorWhenLoggingInWithValidToken() {
            return Stream.of(
                    Arguments.of(TestData.BAD_TOKEN_500, HttpStatus.INTERNAL_SERVER_ERROR)
            );
        }
    }
}
