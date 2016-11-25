app = angular.module('app', ['layout']);

var api = new API()

app.controller('CreditRequestCtrl', function ($scope, $sce, $resource, $http, $timeout) {
    var creditRequestUrl = '/main/rest/overview/getLastCreditRequest';
    var bankListUrl = '/main/rest/overview/banks';
    var smsSendUrl = '/main/rest/overview/sms/send';
    var smsCheckUrl = '/main/rest/overview/save';
    var userUrl = '/main/rest/usersettings/getPeopleMain';
    var firstReqSystemsUrl = '/main/rest/overview/firstReqPaySystems';
    var firstReqUrl = '/main/rest/overview/isFirstRequest';

    var actualPaySys = '/main/rest/overview/activepaysys';
    $scope.receive = {
        type: null,
        form: null,
        error: false
    };
    $scope.sendingSms = false;
//	var hasVariant = 1;

    var timeoutPromise;
    var delayInMs = 100;
    $scope.$watch('receive', function (newVal, oldVal) {
        $timeout.cancel(timeoutPromise);
        timeoutPromise = $timeout(function () {
            if (newVal.type == null) {
                $scope.receive.error = true;
                $scope.receive.errorMessage = 'Выберите способ получения займа'
            } else if (newVal.form != null && newVal.form.$valid == false) {
                $scope.receive.error = true;
                $scope.receive.errorMessage = 'Форма заполнена неверно'
            } else {
                $scope.receive.error = false;
                $scope.receive.errorMessage = ''
            }
        }, delayInMs);
    }, true);

    $http.get(creditRequestUrl).then(function (resp) {
        var data = resp.data.data;
        $scope.lastCreditReq = data;
        $scope.lastCreditReq.agreement = $sce.trustAsHtml(data.creditRequest_agreement);


        // 0 - нету кредита, нету заявки, есть последняя заявка
        // 1 - есть заявка, не одобрена
        // 2 - заявка одобрена, кредит не получен
        // 3 - кредит получен
        if (!data.hasCredit && !data.hasCreditRequest && data.hasLastCreditRequest) {
            $scope.lastCreditReq.viewState = 0
        } else if (!data.hasCredit && data.hasCreditRequest && data.canSign != true) {
            $scope.lastCreditReq.viewState = 1
        } else if (!data.hasCredit && data.hasCreditRequest && data.canSign) {
            $scope.lastCreditReq.viewState = 2;
        } else if (data.hasCredit) {
            $scope.lastCreditReq.viewState = 3
        }

        console.log($scope.lastCreditReq.viewState)
    });

    $http.get(bankListUrl).then(function (resp) {
        $scope.banks = resp.data.data
    });
    $scope.isFirstRequest = false;
    $scope.contactIs = false;
    $scope.cardIs = false;
    $scope.schetIs = false;
    $scope.qiwiIs = false;
    $scope.yandexIs = false;
    $scope.koronaIs = false;
    $scope.unistrIs = false;
    $http.get(firstReqUrl).then(function (resp) {
        $scope.isFirstRequest = resp.data.data;
        if ($scope.isFirstRequest) {
            $http.get(firstReqSystemsUrl).then(function (resp) {
                $scope.actualPaySys = resp.data.data;
                for (var index = 0; index < $scope.actualPaySys.length; ++index) {
                    switch ($scope.actualPaySys[index]) {
                        case 5:
                            $scope.qiwiIs = true;
                            break;
                        case 3:
                            $scope.contactIs = true;
                            break;
                        case 2:
                            $scope.cardIs = true;
                            break;
                        case 1:
                            $scope.schetIs = true;
                            break;
                        case 4:
                            $scope.yandexIs = true;
                            break;
                        case 7:
                            $scope.koronaIs = true;
                            break;
                        case 8:
                            $scope.unistrIs = true;
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        else {
            $scope.isFirstRequest = true;
            $scope.contactIs = true;
            $scope.cardIs = true;
            $scope.schetIs = true;
            $scope.qiwiIs = true;
            $scope.yandexIs = true;
            $scope.koronaIs = true;
            $scope.unistrIs = true;
        }
    });

    $http.post(userUrl).then(function (resp) {
        if (resp.data.success == false) {
            console.log(data);
            return
        }

        var accounts = resp.data.data.accounts;

        $scope.accounts = {
            qiwi: [],
            yandex: [],
            banks: [],
            cards: []
        };
        accounts.map(function (elem) {
            if (elem.codeType == 1) {
                $scope.accounts.banks.push(elem);
            } else if (elem.codeType == 2) {
                $scope.accounts.cards.push(elem);
            } else if (elem.codeType == 4) {
                $scope.accounts.yandex.push(elem);
            } else if (elem.codeType == 5) {
                $scope.accounts.qiwi.push(elem);
            }
        })
    });


    $scope.refuse = function () {
        if (confirm('Вы действительно хотите отказаться от займа?')) {
            api.refuseCredit().done(function () {
                location.href = "refused.shtml"
            })
        }
    };

    $scope.receiveRadio = function (newType, form) {
        if ($scope.receive.type == null) {
            $scope.receive.type = newType;
            $scope.receive.form = form
        } else {
            if ($scope.receive.type == newType) {
                $scope.receive.type = null;
                $scope.receive.form = null
            } else {
                $scope.receive.type = newType;
                $scope.receive.form = form
            }
        }
    };

    $scope.sendSms = function () {
        $scope.sendingSms = true;

        if ($scope.receive.form.$valid == false) {
            alert('Вы не заполнили форму');
            $scope.sendingSms = false;
            return
        }


        var data = parseReceiveType($scope.receive);
        data.type = $scope.receive.type;

        $http.put(smsSendUrl, data).then(function (resp) {
            $scope.sendingSms = false;

            if (resp.data.success == false) {
                console.log(resp);
                return
            }

            $('#smsNumber').html('+' + resp.data.data);
            lkSendKodSms('Подтвердить получение займа');

            $('#retrySendSms').on('click', function () {
                $http.put(smsSendUrl, {})
            });

            var confirmButton = $('#confirmSmsCode');
            confirmButton.unbind('click');
            confirmButton.on('click', function () {
                var smsCode = $('#phoneCode').val();

                var data = {
                    smsCode: smsCode
                };

                $http.post(smsCheckUrl, data).then(function (resp) {
                    $('.send-sms_up #phoneCode').parent().removeClass('send-data-server');
                    if (resp.data.success == false) {
                        $('.send-sms_up #phoneCode').parent().nextAll('.errorMessage').first().text(resp.data.errorMessage).show();
                        return
                    }
                    $('.alert_box').triggerHandler('click');
                    location.reload();
                });
            });

            // тестовое поле кода
            var inputCode = $('.send-sms_up #phoneCode');
            inputCode.unbind('keyup');
            inputCode.on('keyup', function () {
                if (inputCode.val().length == inputCode.attr('maxlength')) {
                    $('.send-sms_up #phoneCode').blur().parent().addClass('send-data-server');
                    confirmButton.trigger('click');
                }
            });
        })
    };

    $scope.updateBank = function (account) {
        var isNew = false;
        if (account == "") {
            account = "{}";
            isNew = true;
        }

        account = JSON.parse(account);
        $scope.receive.bank = {
            isNew: isNew,
            id: account.id,
            name: account.name,
            bik: account.bik,
            corAccount: account.corAccount,
            accountnumber: account.accountnumber
        }
    };

    $scope.updateQiwi = function (qiwi) {
        var isNew = false;
        if (qiwi == "") {
            qiwi = "{}";
            isNew = true;
        }

        qiwi = JSON.parse(qiwi);
        $scope.receive.qiwi = {
            isNew: isNew,
            id: qiwi.id,
            accountnumber: qiwi.accountnumber
        }
    };

    $scope.updateYandex = function (yandex) {
        var isNew = false;

        if (yandex == "") {
            yandex = "{}";
            isNew = true;
        }

        yandex = JSON.parse(yandex);
        $scope.receive.yandex = {
            isNew: isNew,
            id: yandex.id,
            accountnumber: yandex.accountnumber
        };
    };

    $scope.updateCard = function (card) {
        var isNew = false;

        if (card == "" || card == undefined) {
            card = "{}";
            isNew = true;
        }

        card = JSON.parse(card);
        $scope.receive.card = {
            isNew: isNew,
            id: card.id,
            accountnumber: card.accountnumber
        };
    };

    $scope.upAddCard = function () {
        alert_top($('.add-card_up'))
    }

    function parseReceiveType(value) {
        var data = {};

        if (value.type == 'contactSys') {
            data = {}
        } else if (value.type == 'bank') {
            data = value.bank
        } else if (value.type == 'card') {
            data = value.card
        } else if (value.type == 'account') {
            data = value.bank
        } else if (value.type == 'qiwi') {
            data = value.qiwi
        } else if (value.type == 'yandex') {
            data = value.yandex
        }

        return data
    }

    $scope.$on('yandexAccountUpdated', function (event, data) {
        $scope.accounts.yandex = data;
        $scope.$apply();
    });
    $scope.$on('cardAccountUpdated', function (event, data) {
        $scope.accounts.cards = data;
        $scope.$apply();
    });
    $scope.$on('bankAccountUpdated', function (event, data) {
        $scope.accounts.banks = data;
        $scope.$apply();
    });
});

app.controller('AddYandexWallerCtrl', function ($scope, $http) {
    $scope.number = '';
    $scope.error = false;
    $scope.conflictError = false;

    $scope.addYandexWallet = function () {
        $http.post('/main/rest/account/yandex', {number: $scope.number}).then(function (response) {
            $scope.error = false;
            $scope.conflictError = false;
            var userUrl = '/main/rest/usersettings/getPeopleMain';
            $http.post(userUrl).then(function (resp) {
                var accounts = resp.data.data.accounts;
                var yandexAccounts = [];
                accounts.map(function (elem) {
                    if (elem.codeType == 4) {
                        yandexAccounts.push(elem);
                    }
                });
                $scope.$emit('yandexAccountUpdated', yandexAccounts);
            });
            $('#addYandexWalletDialog .close-reveal-modal').click();
        }, function (response) {
            $scope.error = false;
            $scope.conflictError = false;
            if (response.status == 400) {
                $scope.error = true
            } else if (response.status == 409) {
                $scope.conflictError = true
            } else if (response.status == 401) {
                window.location = '/main/login.shtml';
            } else {
                alert('Сервер временно недоступен, попробуйте позже');
            }
        });
    }
});

app.controller('AddCardCtrl', function ($scope, $http) {
    $scope.number = '';
    $scope.error = false;
    $scope.conflictError = false;
    $('.card').mask('9999-9999-9999-9999');

    $scope.addCard = function () {
        $http.post('/main/rest/account/card', {number: $scope.number}).then(function (response) {
            $scope.error = false;
            $scope.conflictError = false;
            var userUrl = '/main/rest/usersettings/getPeopleMain';
            $http.post(userUrl).then(function (resp) {
                var accounts = resp.data.data.accounts;
                var cards = [];
                accounts.map(function (elem) {
                    if (elem.codeType == 2) {
                        cards.push(elem);
                    }
                });

                up_box.trigger('click')
                $scope.$emit('cardAccountUpdated', cards);
                $scope.number = '';
            });

        }, function (response) {
            $scope.error = false;
            $scope.conflictError = false;
            if (response.status == 400) {
                $scope.error = true
            } else if (response.status == 409) {
                $scope.conflictError = true;
            } else if (response.status == 401) {
                window.location = '/';
            } else {
                alert('Сервер временно недоступен, попробуйте позже');
            }
        });
    }
});

app.controller('AddBankCtrl', function ($scope, $http) {
    $scope.number = '';
    $scope.error = {};
    $scope.conflictError = false;


    $scope.addBankAccount = function () {
        $http.post('/main/rest/account/bank', {
            bankBik: $scope.bankBik,
            bankName: $scope.bankName,
            bankCorAccount: $scope.bankCorAccount,
            bankKPP: $scope.bankKPP,
            account: $scope.account
        }).then(function (response) {
            $scope.error = false;
            $scope.conflictError = false;
            var userUrl = '/main/rest/usersettings/getPeopleMain';
            $http.post(userUrl).then(function (resp) {
                var accounts = resp.data.data.accounts;
                var bankAccounts = [];
                accounts.map(function (elem) {
                    if (elem.codeType == 1) {
                        bankAccounts.push(elem);
                    }
                });
                $scope.$emit('bankAccountUpdated', bankAccounts);
                $scope.bankBik = '';
                $scope.bankName = '';
                $scope.bankCorAccount = '';
                $scope.bankKPP = '';
                $scope.account = '';
            });
            $('#addBankDialog .close-reveal-modal').click();
        }, function (response) {
            $scope.error = {};
            $scope.conflictError = false;
            if (response.status == 400) {
                if (response.data.errors) {
                    for (var field in response.data.errors) {
                        $scope.error[field] = response.data.errors[field][0];
                    }
                }
            } else if (response.status == 409) {
                $scope.conflictError = true;
            } else if (response.status == 401) {
                window.location = '/main/login.shtml';
            } else {
                alert('Сервер временно недоступен, попробуйте позже');
            }
        });
    }
});
