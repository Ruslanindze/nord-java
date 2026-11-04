package nord.codes.tests;

import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import nord.codes.dto.EndpointRs;
import nord.codes.infra.Action;
import nord.codes.infra.ApiPaths;
import nord.codes.infra.BaseTest;
import nord.codes.infra.TestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@DisplayName("E2E сценарий")
public class EndpointE2ETest extends BaseTest implements ApiPaths {
    private String activeToken;

    @AfterEach
    void logoutToken() {
        if (activeToken != null) {
            EntityExchangeResult<EndpointRs> rs = endpointClient.sendAction(activeToken, Action.LOGOUT);
        }
    }

    @Test
    @DisplayName("Пользовательский путь")
    void e2eTest() {
        // Дан токен
        activeToken = TestData.VALID_TOKEN_3;
        String messageNotFound = "Token '%s' not found".formatted(activeToken);
        String messageAlreadyExist = "Token '%s' already exists".formatted(activeToken);
        String messageInvalidAction = "action: invalid action '%s'. Allowed: LOGIN, LOGOUT, ACTION"
                .formatted(Action.OTHER_ACTION.name());

        // Логируем четыре раза (н.: завис UI)
        // 1-й раз логируем
        Allure.step("Многократная авторизация (защита от двойного клика)", () -> {
            EntityExchangeResult<EndpointRs> rsLogin = endpointClient.sendAction(activeToken, Action.LOGIN);
            endpointAssertions.assertSuccessAction(rsLogin);
            // 2-3-4-й раз логируем
            for (int i = 0; i < 3; i++) {
                // When
                EntityExchangeResult<EndpointRs> rsLocLogin = endpointClient.sendAction(activeToken, Action.LOGIN);
                // Then
                endpointAssertions.assertErrorAction(rsLocLogin, HttpStatus.CONFLICT, messageAlreadyExist);
            }
        });

        Allure.step("Выполнение 3 действий подряд", () -> {
            // Делаем 3 действий - все успешные (пользователь до сих пор авторизован)
            for (int i = 0; i < 3; i++) {
                EntityExchangeResult<EndpointRs> rsAction = endpointClient.sendAction(activeToken, Action.ACTION);
                endpointAssertions.assertSuccessAction(rsAction);
            }
        });

        Allure.step("Попытка выполнить невалидной действие", () -> {
            // Например в UI остался старый функционал, который делает другой action
            EntityExchangeResult<EndpointRs> rsInvalidAction = endpointClient.sendAction(activeToken, Action.OTHER_ACTION);
            endpointAssertions.assertErrorAction(rsInvalidAction, HttpStatus.BAD_REQUEST, messageInvalidAction);
        });

        // Пользователь пробует сделать два запроса action - все безуспешны
        Allure.step("Завершение сессии и проверка запрета действий", () -> {
            // Допустим, что-то случилось и пользователь перестал быть авторизован
            AtomicReference<EntityExchangeResult<EndpointRs>> rsLogout = new AtomicReference<>(endpointClient.sendAction(activeToken, Action.LOGOUT));
            endpointAssertions.assertSuccessAction(rsLogout.get());

            for (int i = 0; i < 2; i++) {
                EntityExchangeResult<EndpointRs> rsAction = endpointClient.sendAction(activeToken, Action.ACTION);
                endpointAssertions.assertErrorAction(rsAction, HttpStatus.FORBIDDEN, messageNotFound);
            }

            // Финальный LOGOUT - ошибка, т.к. уже вышел из сессии
            rsLogout.set(endpointClient.sendAction(activeToken, Action.LOGOUT));
            endpointAssertions.assertErrorAction(rsLogout.get(), HttpStatus.FORBIDDEN, messageNotFound);
        });

        activeToken = null;
    }
}
