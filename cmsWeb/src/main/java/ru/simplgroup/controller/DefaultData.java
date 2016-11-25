package ru.simplgroup.controller;

import ru.simplgroup.data.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Здесь храню первоначальные данные для текущей верстки
 */
public interface DefaultData {

    public static final String PROJECT_NAME = "mobifinance";

    //главная страница Подходил ли вам onntime
    static final LinkedHashMap<String, String> li1 = new LinkedHashMap<String, String>() {{
        put("header","Рейтинг");
        put("body","Несвоевременное погашение заёма негативно вливает на Ваш кредитный ретинг");
    }};
    static final LinkedHashMap<String, String> li2 = new LinkedHashMap<String, String>() {{
        put("header","Ограничения");
        put("body","Заём выдается только гражданам РФ, достигшим 18 лет.");
    }};
    static final LinkedHashMap<String, String> li3 = new LinkedHashMap<String, String>() {{
        put("header", "Условия");
        put("body", "Наши кредиты выдаються суммой до 30 000 рублей на срок не более 30 дней.");
    }};

    public static final List<LinkedHashMap<String, String>> yesOntime = new ArrayList<LinkedHashMap<String, String>>(){{
        add(li1);
        add(li2);
        add(li3);
    }};
    //как работает ontime
    static final LinkedHashMap<String, String> hw1 = new LinkedHashMap<String, String>() {{
        put("header", "Подача заявки");
        put("body", "Ontime - это первая микрофинансовая организация в России, которая полностью ведет свою деятельность в интернете. Чтобы получить у нас заем не нужно ни выходить из дома, ни сканировать документы, ни пускать к себе домой непонятных администраторов или курьеров.");
    }};
    static final LinkedHashMap<String, String> hw2 = new LinkedHashMap<String, String>() {{
        put("header", "Пролонгация");
    }};
    static final LinkedHashMap<String, String> hw3 = new LinkedHashMap<String, String>() {{
        put("header", "Бонусная система");
    }};
    static final LinkedHashMap<String, String> hw4 = new LinkedHashMap<String, String>() {{
        put("header", "Как вернуть заем");
    }};

    public static final List<LinkedHashMap<String, String>> howWork = new ArrayList<LinkedHashMap<String, String>>(){{
        add(hw1);
        add(hw2);
        add(hw3);
        add(hw4);
    }};


    List<QuestionDataW> QUESTION_DATAS = new ArrayList<>(Arrays.asList(
            new QuestionDataW("Какие у вас требования к заемщику?", "Вам нужно быть совершеннолетним гражданином РФ.", 1, true, false, 10),
            new QuestionDataW("В каких городах вы работаете?", "Мы выдаем микрозаймы на всей территории РФ, вам не нужно посещать наш офис, просто оставьте заявку, и мы свяжемся с вами в течение 15 минут.", 2, true, false, 10),
            new QuestionDataW("Дадут ли мне заем?", "Просто оформите заявку, и мы моментально примем решение.", 3, true, false, 20),
            new QuestionDataW("Как я могу получить свой микрозаем?", "Мы выдаем микрозаймы на карту любого банка РФ, на счета Яндекс Денег, на QIWI, а также переводим с помощью Юнистрим, золотой Короны и системы Contact.", 4, false, true, 30)));


    List<CategoryQuestionW> CATEGORY_QUESTIONS = new ArrayList<>(Arrays.asList(new CategoryQuestionW("ПОЛУЧЕНИЕ ДЕНЕГ", 10, false),
            new CategoryQuestionW("КТО МОЖЕТ ВЗЯТЬ ЗАЕМ", 20, true),
            new CategoryQuestionW("ОФОРМЛЕНИЕ ЗАЯВКИ", 30, true)));

    public static final FaqData FAQ_DATA = new FaqData("<h1>Вопросы и ответы</h1><p>Получить деньги предельно просто</p>", CATEGORY_QUESTIONS, QUESTION_DATAS);

    public static final List<NewsW> NEWS_LIST = new ArrayList<>(Arrays.asList(
            new NewsW("Ontime дарит бонусы в честь Дня друзей!", "6 июня, 2015", "19 марта 2015 года компания Ontime запустила акцию «Заем без процентов». По условиям акции, каждый 30-й клиент компании, оформивший заем на срок не менее 17-ти дней и на сумму от 5-ти тысяч рублей получает возможность вернуть", null, 1),
            new NewsW("Конкурс «Супер Тысяча» в социальных сетях", "6 июня, 2015", "12 июня – День России. К этому празднику Ontime снова запускает ставший традиционным и любимый многими конкурс, претерпевший изменения и получивший новое название - «Супер Тысяча».", null, 2),
            new NewsW("Акция «Займи с сюрпризом» от Ontime", "6 июня, 2015", "Ontime снова запускает акцию “Займи с сюрпризом». Получив займ с 01.06.2015 по 30.06.2015, каждый клиент компании автоматически участвует в розыгрыше 3-х смартфонов Samsung Galaxy S4!", null, 3)));

    public static final String POLITICS_HEADER = "<h1>Соглашения и правила</h1><p>Номер в государственном реестре микрофинансовых организаций 651403029004957 от 09.04.2014 года</p>";

    public static final PoliticsPointDataW POLITICS_1 = new PoliticsPointDataW(1, "Общие условия договора потребительского займа", "");
    public static final PoliticsPointDataW POLITICS_2 = new PoliticsPointDataW(2, "Правила предоставления займов", "");
    public static final PoliticsPointDataW POLITICS_3 = new PoliticsPointDataW(3, "Информация об условиях предоставления, использования и возврата потребительского займа", "");
    public static final PoliticsPointDataW POLITICS_4 = new PoliticsPointDataW(4, "Правила обработки персональных данных и иной информации", "");
    public static final PoliticsPointDataW POLITICS_5 = new PoliticsPointDataW(5, "Соглашение об использовании аналога собственноручной подписи", "");
    public static final PoliticsPointDataW POLITICS_6 = new PoliticsPointDataW(6, "Свидетельство МФО", "");
    public static final PoliticsPointDataW POLITICS_7 = new PoliticsPointDataW(7, "Форма индивидуальных условий", "");
    public static final PoliticsPointDataW POLITICS_8 = new PoliticsPointDataW(8, "Раскрытие информации", "");
    public static final PoliticsPointDataW POLITICS_9 = new PoliticsPointDataW(9, "Информация о процессе оплаты для сайтов предприятий электронной коммерции", "");
    public static final PoliticsPointDataW POLITICS_10 = new PoliticsPointDataW(10, "Оплата с помощью банковской карты", "<p>Мы предлагаем Вам срочные микрозаймы через интернет. Воспользовавшись турбозаймом, Вы сможете оценить существенные преимущества нашего сервиса в сравнении с быстрым микрокредитом наличными. Вам не нужно тратить лишнее время и беспокоиться о том, чтобы собрать необходимые документы. Вам просто нужно дождаться предварительного одобрения заявки, и указанная в заявке сумма турбозайма будет срочно переведена на Вашу банковскую карту.</p>\n" +
            "\t\t\t\t\t<p>Если в указанную дату на счете Вашей банковской карты будет недостаточно средств для полного погашения займа, микрозайм будет погашен частично. <br>Проверьте состояние счета Вашей банковской карты в планируемую дату списания задолженности, и если операция не была совершена успешно, погасите займ в личном кабинете.</p>\n" +
            "\t\t\t\t\t<p>Услуга доступна всем клиентам компании с момента заключения договора микрозайма. <br>Если Вы погасили займ через QIWI, с помощью интернет банка «Альфа-Клик» или Деньги@mail.ru до 16 дня после окончания срока действия договора, пришлите в форме Обратной связи копию чека об оплате или приложите скриншот экрана монитора с пометкой об успешно завершенной операции,</p>\n" +
            "\t\t\t\t");


    public static final List<PoliticsPointDataW> politics = new ArrayList<PoliticsPointDataW>(){{
        add(POLITICS_1);
        add(POLITICS_2);
        add(POLITICS_3);
        add(POLITICS_4);
        add(POLITICS_5);
        add(POLITICS_6);
        add(POLITICS_7);
        add(POLITICS_8);
        add(POLITICS_9);
        add(POLITICS_10);
   }};
    public static final String CONTACTS_ADDRESS = "г. Северодвинск, ул. Карла Маркса, д. 46, офис 400";
    public static final String CONTACTS_PHONE = "8 800 843 43 43";
    public static final String CONTACTS_EMAIL = "support@lovizaim.ru";


    public static final String ABOUT_ME_COMPANY = "«Mobifinans» - современный высокотехнологичный сервис онлайн-займов, запущенный компанией в 2015 г. Он является перспективным каналом взаимодействия с клиентом. Весь процесс оформления займа - от регистрации на сайте до момента погашения - клиент проходит, оставаясь онлайн. Отсутствие необходимости посещения офиса или пункта выдачи средств открывает широкие возможности для жителей всей России, вне зависимости от региона проживания и типа населенного пункта, стать клиентами компании.";

    public static final String ABOUT_ME_SIMPLY = "Процесс получения займа предельно прост и занимает минимальное количество времени";

    public static final String ABOUT_ME_QUICKLY = "Mobifinans перечислит вам деньги в кратчайшие сроки с момента подачи онлайн заявки на займ";

    public static final String ABOUT_ME_RESPONSIBILITY = "Mobifinans – это ваш надежный друг, который всегдаф готов оказать поддержку всем, кто в ней нуждается";

}
