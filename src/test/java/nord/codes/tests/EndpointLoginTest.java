package nord.codes.tests;

import lombok.extern.slf4j.Slf4j;
import nord.codes.dto.rs.EndpointRs;
import nord.codes.infra.Action;
import nord.codes.infra.ApiPaths;
import nord.codes.infra.BaseTest;
import nord.codes.infra.TestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import java.util.stream.Stream;

import static nord.codes.infra.TestTags.NEGATIVE;
import static nord.codes.infra.TestTags.POSITIVE;
import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
public class EndpointLoginTest extends BaseTest implements ApiPaths {
    private String activeToken;

    @Nested
    @DisplayName("Позитивные сценарии")
    @Tag(POSITIVE)
    class Positive {

        @AfterEach
        void logoutToken() {
            if (activeToken != null) {
                try {
                    restTestClient.post()
                            .uri(ENDPOINT)
                            .body(createFormUrlEncodedValue(activeToken, Action.LOGOUT))
                            .exchange();
                } catch (Exception e) {
                    log.warn("Не удалось выполнить LOGOUT для токена {}.", activeToken, e);
                }
            }
        }

        @ParameterizedTest(name = "Авторизация нового токена | {0}")
        @ValueSource(strings = {
                TestData.VALID_TOKEN,
                TestData.VALID_TOKEN_2,
                TestData.VALID_TOKEN_3
        })
        void shouldReturnOkWhenLoggingInWithNewValidToken(String token) {
            // Given
            activeToken = token;
            MultiValueMap<String, String> formData = createFormUrlEncodedValue(token, Action.LOGIN);

            // When and Then
            restTestClient.post()
                    .uri(ENDPOINT)
                    .body(formData)
                    .exchange()
                    // Then
                    .expectStatus().isOk()
                    .expectBody(EndpointRs.class)
                    .value(rs -> {
                        assertThat(rs)
                                .as("Response %s should not be null".formatted(ENDPOINT))
                                .isNotNull();
                        assertThat(rs.getResult())
                                .as("Result %s should be OK".formatted(ENDPOINT))
                                .isEqualTo("OK");
                        assertThat(rs.getMessage())
                                .as("Message %s should be null".formatted(ENDPOINT))
                                .isNull();
                    });

        }
    }

    @Nested
    @DisplayName("Негативные сценарии")
    @Tag(NEGATIVE)
    class Negative {

        @ParameterizedTest(name = "Ошибка клиента при авторизации | {0}")
        @MethodSource
        void shouldReturnClientErrorWhenLoggingInWithInvalidToken(String token, String message) {
            // Given
            activeToken = token;
            MultiValueMap<String, String> formData = createFormUrlEncodedValue(token, Action.LOGIN);

            // When and Then
            restTestClient.post()
                    .uri(ENDPOINT)
                    .body(formData)
                    .exchange()
                    // Then
                    .expectStatus().is4xxClientError()
                    .expectBody(EndpointRs.class)
                    .value(rs -> {
                        assertThat(rs)
                                .as("Response %s should not be null".formatted(ENDPOINT))
                                .isNotNull();
                        assertThat(rs.getResult())
                                .as("Result %s should be ERROR".formatted(ENDPOINT))
                                .isEqualTo("ERROR");
                        assertThat(rs.getMessage())
                                .as("Message %s should be %s".formatted(ENDPOINT, message))
                                .contains(message);
                    });
        }

        static Stream<Arguments> shouldReturnClientErrorWhenLoggingInWithInvalidToken() {
            return Stream.of(
                    Arguments.of(TestData.ERR_TOKEN_400_1, "token: должно соответствовать"),
                    Arguments.of(TestData.ERR_TOKEN_400_2, "token: должно соответствовать"),
                    Arguments.of(TestData.ERR_TOKEN_400_3, "token: должно соответствовать"),
                    Arguments.of("", "token: не должно равняться null")
            );
        }

        @ParameterizedTest(name = "Ошибка сервера при авторизации | {0}")
        @MethodSource
        void shouldReturnServerErrorWhenLoggingInWithValidToken(String token, HttpStatus httpStatus) {
            // Given
            activeToken = token;
            MultiValueMap<String, String> formData = createFormUrlEncodedValue(token, Action.LOGIN);

            // When and Then
            restTestClient.post()
                    .uri(ENDPOINT)
                    .body(formData)
                    .exchange()
                    // Then
                    .expectStatus().isEqualTo(httpStatus)
                    .expectBody(EndpointRs.class)
                    .value(rs -> {
                        assertThat(rs)
                                .as("Response %s should not be null".formatted(ENDPOINT))
                                .isNotNull();
                        assertThat(rs.getResult())
                                .as("Result %s should be ERROR".formatted(ENDPOINT))
                                .isEqualTo("ERROR");
                        assertThat(rs.getMessage())
                                .as("Message %s should be %s".formatted(ENDPOINT, httpStatus.getReasonPhrase()))
                                .contains(httpStatus.getReasonPhrase());
                    });

        }

        static Stream<Arguments> shouldReturnServerErrorWhenLoggingInWithValidToken() {
            return Stream.of(
                    Arguments.of(TestData.BAD_TOKEN_500, HttpStatus.INTERNAL_SERVER_ERROR)
            );
        }
    }
}
