package nord.codes.tests;

import nord.codes.infra.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

import static nord.codes.infra.TestTags.POSITIVE;

public class EndpointLoginTest extends BaseTest {
    @Nested
    @DisplayName("Позитивные сценарии")
    @Tag(POSITIVE)
    class Positive {
        void shouldReturnOkWhenLoggingInWithNewValidToken() {

        }

    }

}

