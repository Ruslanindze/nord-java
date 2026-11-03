package nord.codes.infra;

public class TestData {
    // Позитивные случаи
    public static final String VALID_TOKEN = "50637827474A440F95BF04409DA0D2E3";
    public static final String VALID_TOKEN_2 = "78A6FDD0DF054DE5B7E5B7947DB602B3";
    public static final String VALID_TOKEN_3 = "FE61CAABB5C34AC2877389D4D5552ACC";

    // Обработка приложением, во внешний сервис не идём
    public static final String ERR_TOKEN_400_1 = "IE61CAABB5C34AC2877389D4D5BAD400";
    public static final String ERR_TOKEN_400_2 = "E61CAABB5C34AC2877389D4D5BAD400";
    public static final String ERR_TOKEN_400_3 = "FE61CAABB5C34AC2877389D4D5552ACc";

    // Обработка приложением, ответ от внешнего сервиса (мок)
    public static final String BAD_TOKEN_400 = "FE61CAABB5C34AC2877389D4D5BAD400";
    public static final String BAD_TOKEN_400_2 = "FE61CAABB5C34AC2877389D4D5ERR400";
    public static final String BAD_TOKEN_401 = "FE61CAABB5C34AC2877389D4D5BAD401";
    public static final String BAD_TOKEN_403 = "FE61CAABB5C34AC2877389D4D5BAD403";
    public static final String BAD_TOKEN_415 = "FE61CAABB5C34AC2877389D4D5BAD415";

    public static final String BAD_TOKEN_500 = "FE61CAABB5C34AC2877389D4D5BAD500";
    public static final String BAD_TOKEN_503 = "FE61CAABB5C34AC2877389D4D5BAD503";
    public static final String BAD_TOKEN_504 = "FE61CAABB5C34AC2877389D4D5BAD504";
}
