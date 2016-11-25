/**
 * Класс для работы с rest api, содержит все требуемые пути и методы для обращения к ним
 * @constructor
 */
var API = function () {
    /**
     * Список доступных url
     */
    var urls = {
        lastCreditRequest: {
            url: '/main/rest/overview/getLastCreditRequest',
            type: 'get'
        },
        refuse: {
            url: '/main/rest/overview/refuse',
            type: 'post'
        },
        bankList: {
            url: '/main/rest/overview/banks',
            type: 'get'
        },
        actualPaySys: {
            url: '/main/rest/overview/activepaysys',
            type: 'get'
        },
        peopleMain: {
            url: '/main/rest/usersettings/getPeopleMain',
            type: 'post'
        },
        identityAnswer: {
            url: '/main/rest/overview/answers',
            type: 'post'
        },
        overviewSendSms: {
            url: '/main/rest/overview/sms/send',
            type: 'put'
        },
        overviewSave: {
            url: '/main/rest/overview/save',
            type: 'post'
        },
        balanceHistory: {
            url: '/main/rest/listsums/getData',
            type: 'get'
        },
        balanceInfo: {
            url: '/main/rest/listsums/mySum',
            type: 'get'
        },
        userName: {
            url: '/main/rest/usersettings/getUserName',
            type: 'post'
        },
        logout: {
            url: '/main/rest/auth/logout',
            type: 'post'
        },
        ai: {
            url: '/main/rest/ai/getData',
            type: 'get'
        },
        socialConfig: {
            url: '/main/rest/usersettings/socialConfig',
            type: 'post'
        },
        peopleContact: {
            url: '/main/rest/usersettings/getPeopleContact',
            type: 'post'
        },
        archiveSocContact: {
            url: '/main/rest/usersettings/makeArchive',
            type: 'post'
        },
        friends: {
            url: '/main/rest/friends/',
            type: 'get'
        },
        savePeopleContact: {
            url: '/main/rest/usersettings/savePeopleContact',
            type: 'post'
        },
        getAddresses: {
            url: '/main/rest/usersettings/getAddresses',
            type: 'get'
        },
        saveAddresses: {
            url: '/main/rest/usersettings/saveAddress',
            type: 'post'
        },
        removeDocument: {
            url: '/main/rest/usersettings/removeDocMedia',
            type: 'post'
        },
        uploadFile: {
            url: '/main/rest/firstrequest/upload',
            type: 'post',
            contentType: false,
            processData: false
        },
        validationDicts: {
            url: '/main/rest/firstrequest/validationDicts',
            type: 'get'
        },
        userSendSms: {
            url: '/main/rest/usersettings/sendSmsCode',
            type: 'post'
        },
        saveAdditionalData: {
            url: '/main/rest/usersettings/saveAdditionalData',
            type: 'post'
        },
        getFullPassportData: {
            url: '/main/rest/usersettings/fullPassportData',
            type: 'get'
        },
        saveFullPassportData: {
            url: '/main/rest/usersettings/fullPassportData',
            type: 'post'
        },
        getMainData: {
            url: '/main/rest/usersettings/mainData',
            type: 'get'
        },
        saveMainData: {
            url: '/main/rest/usersettings/mainData',
            type: 'post'
        },
        getEmploymentData: {
            url: '/main/rest/usersettings/employmentData',
            type: 'get'
        },
        saveEmploymentData: {
            url: '/main/rest/usersettings/employmentData',
            type: 'post'
        },
        getPasswordData: {
            url: '/main/rest/usersettings/passwordData',
            type: 'get'
        },
        savePasswordData: {
            url: '/main/rest/usersettings/passwordData',
            type: 'post'
        },
        isFirstRequest: {
            url: '/main/rest/overview/isFirstRequest',
            type: 'get'
        },
        firstReqPaySystems: {
            url: '/main/rest/overview/firstReqPaySystems',
            type: 'get'
        },
        universalActivePaySys: {
            url: '/main/rest/overview/universalActivePaySystem',
            type: 'get'
        },
        getProfileData: {
            url: '/main/rest/usersettings/profileData',
            type: 'get'
        },
        saveProfileData: {
            url: '/main/rest/usersettings/profileData',
            type: 'post'
        },
        getPersonalData: {
            url: '/main/rest/usersettings/formPersonalData',
            type: 'get'
        },
        savePersonalData: {
            url: '/main/rest/usersettings/formPersonalData',
            type: 'post'
        },
        getAddressData: {
            url: '/main/rest/usersettings/addressData',
            type: 'get'
        },
        saveAddressData: {
            url: '/main/rest/usersettings/addressData',
            type: 'post'
        },
    };


    function API() {
        // constructor
    }


    /**
     * Внутренний метод для обычного json запроса
     * @param resource ресурс из списка
     * @param data данные для передачи
     * @private
     */
    var _send = function (resource, data) {
        var sendData;
        if (resource.type == 'get') {
            sendData = data || undefined;
        } else if (resource.type == 'post') {
            sendData = data || {};
        } else if (resource.type == 'put') {
            sendData = data || {};
        } else if (resource.type == 'delete') {
            sendData = data || undefined;
        } else {
            sendData = data || undefined;
        }

        var config = {
            data: JSON.stringify(sendData),
            contentType: "application/json; charset=utf-8",
            traditional: true,
            dataType: "json"
        };
        $.extend(config, resource);

        return _request(config);
    };

    /**
     * Внутренний метод для загрузки файла
     * @param resource ресурс из списка
     * @param data данные для загрузки на сервер
     * @private
     */
    var _upload = function (resource, data) {
        var config = {
            data: data,
            cache: false,
            contentType: false,
            processData: false
        };
        $.extend(config, resource);

        return _request(config);
    };

    /**
     * Внутренний метод для вызова ajax
     * @param config конфиг для вызова
     * @returns возвращает promises
     * @private
     */
    var _request = function (config) {
        var dfd = $.Deferred();

        $.ajax(config).done(function (response) {
            if (response.success == false) {
                console.log(config);
                console.error(response);
                dfd.reject(response);
                return;
            }

            dfd.resolve(response.data);
        }).fail(function (response) {
            console.error(response);
            dfd.reject(response)
        });

        return dfd.promise();
    };


    // получает данные для главной страницы, текущий кредит, прошлую заявку, вопросы если есть
    this.getLastCreditRequest = function () {
        return _send(urls.lastCreditRequest);
    };

    // отказ от займа на главной странице
    this.refuseCredit = function () {
        return _send(urls.refuse);
    };

    // список банков
    this.bankList = function () {
        return _send(urls.bankList);
    };

    // доступные средства получения займа, на главной странице
    this.saveIdentityAnswers = function (ids) {
        return _send(urls.identityAnswer, ids);
    };

    // получить данные пользователя
    this.getUserInfo = function () {
        return _send(urls.peopleMain);
    };

    // сохраняет ответы на вопросы идентификации
    this.sendSmsOverview = function (data) {
        return _send(urls.overviewSendSms, data);
    };

    // отправляет смс с главной странице о подтверждении займа
    this.saveCredit = function (data) {
        return _send(urls.overviewSave, data);
    };

    /**
     * возвращает доступные платежные системы
     * @deprecated используйте {@link API#getUniversalActivePaySys}
     */
    this.activePaySys = function () {
        return _send(urls.actualPaySys);
    };

    // запрашивает историю накоплений для копилки
    this.balanceHistory = function () {
        return _send(urls.balanceHistory);
    };

    // запрашивает сумму накоплений, для главной страницы
    this.balanceInfo = function () {
        return _send(urls.balanceInfo);
    };

    // имя пользователя
    this.userName = function () {
        return _send(urls.userName);
    };

    // выход из лк
    this.logout = function () {
        return _send(urls.logout);
    };

    // данные о всяких разрешениях
    this.aiData = function () {
        return _send(urls.ai);
    };

    // данные для соц сетей (id, callback url, etc)
    this.socialConfig = function () {
        return _send(urls.socialConfig);
    };

    // контактые данные человека (друзья, соц сети, пр.)
    this.peopleContact = function () {
        return _send(urls.peopleContact);
    };

    // архивирует соц контакт пользователя
    this.makeArchiveSocContact = function (data) {
        return _send(urls.archiveSocContact, data);
    };

    // возвращает список друзей не для бонусов
    this.getFriends = function () {
        return _send(urls.friends);
    };

    // удаляет документ (права, паспорт, тд)
    this.removeDocument = function (data) {
        return _send(urls.removeDocument, data);
    };

    // загружает файл
    this.uploadFile = function (data) {
        return _upload(urls.uploadFile, data);
    };

    // получает информацию и максимальном размере файла, допустимых расширениях и т.д.
    this.getValidationDicts = function () {
        return _send(urls.validationDicts);
    }

    /**
     * сохраняет контактные данные человека
     * @param data данные
     * @deprecated
     */
    this.savePeopleContact = function (data) {
        return _send(urls.savePeopleContact, data);
    };

    // отправляет смс для изменения данных пользователя
    this.userSendSms = function (data) {
        data = data || {};
        return _send(urls.userSendSms, data);
    };

    // сохраняет доп данные (сканы файлов)
    this.saveAdditionalData = function (data) {
        return _send(urls.saveAdditionalData, data);
    };

    // возвращает адреса пользователя (домашний, рабочий...)
    this.getAddresses = function () {
        return _send(urls.getAddresses);
    };

    // сохраняет кучу данных
    this.saveAddresses = function (data) {
        return _send(urls.saveAddresses, data);
    };

    // возвращает паспортные данные (паспорт, прописку, адрес проживания, семейное положение)
    this.getFullPassportData = function (data) {
        return _send(urls.getFullPassportData, data);
    };

    // сохраняет паспортные данные (паспорт, прописку, адрес проживания, семейное положение)
    this.saveFullPassportData = function (data) {
        return _send(urls.saveFullPassportData, data);
    };

    // возвращает основные данные
    this.getMainData = function () {
        return _send(urls.getMainData);
    };

    // сохраняет основные данные
    this.saveMainData = function (data) {
        return _send(urls.saveMainData, data);
    };

    // возвращает данные о занятости
    this.getEmploymentData = function () {
        return _send(urls.getEmploymentData);
    };

    // сохраняет данные о занятости
    this.saveEmploymentData = function (data) {
        return _send(urls.saveEmploymentData, data);
    };

    /**
     * получает данные для страницы изменения пароля
     */
    this.getPasswordData = function () {
        return _send(urls.getPasswordData);
    };

    /**
     * сохраняет пароль
     * @param data данные для изменения пароля (старый, два раза новый)
     */
    this.savePasswordData = function (data) {
        return _send(urls.savePasswordData, data);
    }

    /**
     * первый запрос у клиента или нет
     */
    this.isFirstRequestData = function () {
        return _send(urls.isFirstRequest);
    }

    /**
     * достает платежные системы для первой заявки
     * @deprecated используйте {@link API#getUniversalActivePaySys}
     */
    this.getFirstReqPaySystemsData = function () {
        return _send(urls.firstReqPaySystems);
    }

    /**
     * возвращает список доступных платежных систем
     * универсальный метод, берет в расчет первый ли это кредит или нет
     */
    this.getUniversalActivePaySys = function () {
        return _send(urls.universalActivePaySys);
    }

    /**
     * возвращает данные профиля
     */
    this.getProfileData = function () {
        return _send(urls.getProfileData);
    }

    /**
     * сохраняет данные профиля
     * @param data данные для сохранения
     */
    this.saveProfileData = function (data) {
        return _send(urls.saveProfileData, data);
    }

    /**
     * возвращает основные\анкетные данные
     */
    this.getPersonalData = function () {
        return _send(urls.getPersonalData);
    }

    /**
     * сохраняет основные\анкетыне данные
     */
    this.savePersonalData = function (data) {
        return _send(urls.savePersonalData, data);
    }

    /**
     * возвращает данные адресов человека
     */
    this.getAddressData = function () {
        return _send(urls.getAddressData);
    }

    /**
     * сохраняет данные адресов
     * @param data данные
     */
    this.saveAddressData = function (data) {
        return _send(urls.saveAddressData, data);
    }
};
