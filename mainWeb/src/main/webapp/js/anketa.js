var callback_url = ""; // адрес реста для коллбеков из соцсетей
var vk = "";
var ok = "";
var fb = "";
var ml = "";


(function (window) {
    'use strict';

    var accountTypesMap;

    var fileStorage = {};

    window.test = function () {
        console.log('step1', collectStep1Data());
        console.log('step2', collectStep2Data());
        console.log('step3', collectStep3Data());
        console.log('step4', collectStep4Data());
        console.log('step5', collectStep5Data());
    };


    window.pageInit = function () {
        $.ajax({
            url: '/main/rest/firstrequest/validate/location',
            method: 'POST',
            dataType: 'json',
            contentType: 'application/json'
        }).success(function (res) {
            if (res.success) {
                pageInitialization()
            } else {
                alert_top(up_window.filter('.location_denied_up'));
                up_box.off();
            }
        });
    };


    function pageInitialization() {
        $('.address').address();

        var infoPromise = $.getJSON('/main/rest/firstrequest/info');

        infoPromise.then(function (res) {
            fillSelect($('#marriage'), res.marriageTypes);
            fillSelect($('.liveRealty'), res.realtyTypes);
            fillSelect($('#education'), res.educationTypes);
            fillSelect($('#employ'), res.employTypes);
            fillSelect($('#extSalary'), res.extSalaryTypes);
            fillSelect($('#profession'), res.professions);
            fillSelect($('#professionTypes'), res.professionTypes);
            fillSelect($('#creditPurposes'), res.creditPurposes);
            fillSelect($('#creditTypes'), res.creditTypes, 'Укажите вид займа');
            fillSelect($('#currencyTypes'), res.currencyTypes, 'Укажите валюту займа');
            fillSelect($('#banks'), res.banksList, 'Укажите название банка');
            fillSelect($('#сreditOverdueTypes'), res.сreditOverdueTypes, 'Укажите максимальный просроченный статус');

            accountTypesMap = res.accountTypesMap;
        });

        $(document).on('anketa.continue', function () {
            infoPromise.then(function (res) {
                res.prevData && fillData(res.prevData);
            });
        });

        $.getJSON('/main/rest/firstrequest/hasPrevData').then(function (res) {
            if (res && res.success) {
                alert_top(up_window.filter('.continue_up'));
            }
        });

        $('.continue_up .btn').not('.cont').on('click', function () {
            $('.alert_box').trigger('click');
        });
        $('.continue_up .btn.cont').on('click', function () {
			$(document).trigger('anketa.continue');
            $('.alert_box').trigger('click');
        });

        $.getJSON("/main/rest/firstrequest/getSumsData", {
            initialSum: getQueryParam('initialSum'),
            initialDays: getQueryParam('initialDays')
        }).success(function (res) {
            var data = res.data;
            slidersInit(data);
        });

        $('.step-2 #kod input').mask('999-999');
        $('#snils').mask('999-999-999 99');

        $('[type="tel"], .step-2 #kod input, #snils, .date.input, .date-not-dd.input, .cod-passport').each(function () {
            var $el = $(this);
            $(document).on('initialData.finish', function () {
                $el.trigger('input');
            });
        });

        $('.btn.next').on('click', function ($evt) {
            // сохранение адресов в локальное хранилище
            var localAddress = [];
            if (typeof(Storage) !== "undefined") {
                var level = 0;
                $('.address').first().find(':selected').each(function (e) {
                    if ($(this).val() != null && $(this).val().length > 0) {
                        localAddress[level++] = $(this).val();
                    }
                });
                if (localAddress.length > 0) {
                    localStorage.setItem("localAddress", localAddress);
                }
            }
            //

            $evt.stopPropagation();
            $evt.preventDefault();

            var $btn = $(this),
                data = collectData();

            if ($btn.hasClass('finish')) {
                var invalidStep;
                if (invalidStep = firstInvalidStep()) {
                    switchStepForm(parseStepNum(invalidStep));
                    scrollToError(invalidStep);
                    return;
                }

                $.extend(true, data, collectStep1Data());
                $.extend(true, data, collectStep2Data());
                $.extend(true, data, collectStep3Data());
                $.extend(true, data, collectStep4Data());
                $.extend(true, data, collectStep5Data());
                data.sumData = collectSumData();

                var sr = function saveRequest() {
                    preloader.show();
                    $.ajax({
                        url: '/main/rest/firstrequest/saveRequest',
                        method: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(data),
                        contentType: 'application/json',
                        success: function (ans) {
                            preloader.hide();
                            if (ans.success) {

                                //       alert_top(up_window.filter('.anketa-finish_up'));
                                //       up_box.off();
                                //       var label = up_window.filter('.anketa-finish_up').find('u');
                                //       var timer = parseInt(label.text());
                                //       setInterval(function () {
                                //           label.text(timer + declension(timer, [' секунда', ' секунды', ' секунд']));
                                //           if (--timer == 0)
                                //               location.assign(up_window.filter('.anketa-finish_up').find('.btn').attr('href'));
                                //       }, 1000);

                                $.ajax({
                                    url: '/main/rest/firstrequest/auth',
                                    method: 'POST',
                                    contentType: 'application/json',
                                    success: function () {
                                        alert_top(up_window.filter('.anketa-finish_up'));
                                        up_box.off();
                                        var label = up_window.filter('.anketa-finish_up').find('u');
                                        var timer = parseInt(label.text());
                                        setInterval(function () {
                                            label.text(timer + declension(timer, [' секунда', ' секунды', ' секунд']));
                                            if (--timer == 0)
                                                location.assign(up_window.filter('.anketa-finish_up').find('.btn').attr('href'));
                                        }, 1000);
                                    }
                                });

                                $('.anketa-nav li.active').next().removeClass('not-access').trigger('click');

                                return true;
                            } else {
                                var errBox = $('.error-info').last();
                                errBox.text(ans.errorMessage);
                                errBox.removeClass('hide');
                                scrollTo(errBox.closest('.fieldset').offset().top);
                            }
                        }
                    });
                }
                saveUserWork(data, sr);
            } else {
                var $step = $btn.closest('[class*=step-]');

                if (validateStep($step, true)) {
                    var nextStepNum = parseStepNum($step) + 1;
                    switchStepForm(nextStepNum).then(function () {
                        saveUserWork(data);
                    });
                } else {
                    saveUserWork(data);
                }
            }
        });
		
		$('[only-digits]').on('keydown', allowOnlyDigits);
		
        $('#employ').on('change', function () {
					
            var select = $(this),
                id = select.val(),
                option = select.find('[value=' + id + ']').text();

            if (option === 'пенсионер' || option === 'льготный пенсионер' || option === 'неработающий' || option === 'студент/учащийся') {
				
				hideElem(true, $('#profession'));
                $('#profession').val('');
                // сбрасываем селект
                $('#profession').val('').trigger('change');
                hideElem(true, $('#occupation'));
                $('#occupation').val('');
                hideElem(true, $('#placeWork'));
                $('#placeWork').val('');
				hideElem(true, $('#dateStartWork'));
                $('#dateStartWork').val('');
                $('#nextExtText').text('Дата следующего дохода');
                $('#jobAddress').hide();
                $('#jobAddress').find('input').val('');
                $('#jobAddress').find('select').val('');
                $('#address-header').hide();
				hideElem(true, $('#workPhone'));
                $('#workPhone').val('');
				hideElem(true, $('#professionTypes'));
                $('#professionTypes').find('input').val('');
                // сбрасываем селект
                $('#professionTypes').val('').trigger('change');
            } else {
				hideElem(false, $('#profession'));
				hideElem(false, $('#occupation'));
				hideElem(false, $('#placeWork'));
				hideElem(false, $('#dateStartWork'));
                $('#nextExtText').text('Приблизительная дата зарплаты');
                $('#jobAddress').show();
                $('#address-header').show();
				hideElem(false, $('#workPhone'));
				hideElem(false, $('#professionTypes'));
            }

            if (option === 'неработающий') {
                $('#salaryDate').removeClass('required');
            } else {
                $('#salaryDate').addClass('required');
            }
        });

        $('#creditIsOver').on('change', function () {
            if ($(this).find(':selected').text() === 'Открыт') {
				hideElem(true, $('#factcreditclosedate'));
                $('#factcreditclosedate').val('');
                $('#creditclosedate').addClass('date-future');
            } else {
                hideElem(false, $('#factcreditclosedate'));
                $('#creditclosedate').removeClass('date-future');
            }
        });
        // скрываем из блока с рабочим адресом квартиру
        $('#jobAddress').find('.apartment').closest('.row').hide();
		
		//код для подтверждения телефона
		var phone = $('#input-phone');
		var phoneCode = $('#input-phone-code');
		var confirmBtn = $('#confirm-phone-kod');
		var repeat = $('#confirm-phone-kod-repeat');
		var isConfirmed = $('#confirm-phone-ico');
		var formatErr = $('#phone-error-format');
		var codeErr = $('#phone-error-code');
		var serverErr = $('#phone-error-server');
		var timer = $('#phone-timer');
		var timelabel = $('#phone-time-label');
		
		confirmBtn.on('click', function () {
            sendCode(false, phone, confirmBtn, repeat, formatErr, timer, timelabel, serverErr, isConfirmed)
        });
		
		repeat.on('click', function () {
            sendCode(false, phone, confirmBtn, repeat, formatErr, timer, timelabel, serverErr, isConfirmed)
        });

        phoneCode.on('keyup change blur', function (evt) {
            confirmCode(false, phone, phoneCode, confirmBtn, repeat, isConfirmed, formatErr, codeErr, serverErr, timer, 								timelabel)
        });
		
		//код для подтверждения email
		var email = $('#input-email');
		var emailCode = $('#input-email-code');
		var emailConfirmBtn = $('#confirm-email-kod');
		var emailRepeat = $('#confirm-email-kod-repeat');
		var emailIsConfirmed = $('#confirm-email-ico');
		var emailFormatErr = $('#email-error-format');
		var emailCodeErr = $('#email-error-code');
		var emailServerErr = $('#email-error-server');
		var emailTimer = $('#email-timer');
		var emailTimelabel = $('#email-time-label');
		
		emailConfirmBtn.on('click', function () {
            sendCode(true, email, emailConfirmBtn, emailRepeat, emailFormatErr, emailTimer, emailTimelabel, emailServerErr, emailIsConfirmed)
        });
		
		emailRepeat.on('click', function () {
            sendCode(true, email, emailConfirmBtn, emailRepeat, emailFormatErr, emailTimer, emailTimelabel, emailServerErr, emailIsConfirmed)
        });

        emailCode.on('keyup change blur', function (evt) {
            confirmCode(true, email, emailCode, emailConfirmBtn, emailRepeat, emailIsConfirmed, emailFormatErr, emailCodeErr, emailServerErr, emailTimer, emailTimelabel)
        });
    };

	
	//провести код
	function confirmCode(isEmail, mainInput, codeInput, confirmBtn, repeat, isConfirmed, formatErr, codeErr, serverErr, timer, 								timelabel)
	{
		
            if (codeInput.val().length == 6) {
                var urlBase = confirmBtn.data('url');
                preloader.show();
                $.ajax({
                    url: urlBase + '/verify',
                    method: 'POST',
                    dataType: 'json',
                    data: codeInput.val(),
                    success: function (ans) {
                        
                        if (ans.success) {
                            codeInput.unbind('keyup change blur');
							codeInput.addClass('hide');
							repeat.addClass('hide');
							timer.addClass('hide');
                            codeErr.addClass('hide');
							isConfirmed.removeClass('hide');
							preloader.hide();
                        } else {
                            codeErr.removeClass('hide');
							preloader.hide();
                        }
                    }
                });

                return false;
            }
		
	}
	
	//отправка сообщения
	function sendCode(isEmail, mainInput, confirmBtn, repeat, formatErr, timer, timelabel, serverErr, isConfirmed)
	{
		formatErr.addClass('hide');
		serverErr.addClass('hide');
		timer.addClass('hide');
		repeat.addClass('hide');
		
		if (mainInput.val() == '')
		{
            formatErr.removeClass('hide');
		}
        else {
			if (isEmail && mainInput.length && !validateEmail(mainInput.val())) {
				formatErr.removeClass('hide');
                return false;
            }

            var asyncValidator = isEmail ? asyncEmailValidation : asyncPhoneValidation;
			
			//проверяем есть ли такие уже в БД
            asyncValidator(mainInput, serverErr).then(function () {
                    var urlBase = confirmBtn.data('url');
                    preloader.show();
                    $.ajax({
                        url: urlBase + '/send',
                        method: 'POST',
                        dataType: 'json',
                        data: mainInput.val(),
                        success: function (ans) {
                            preloader.hide();
                            if (ans.success) {
								// успешно отправили код
								// скрываем кнопку подтвердить
								confirmBtn.addClass('hide');
								//начинаем отсчет обратный
                                timer.removeClass('hide');
								var sec = 60;
                                var idTimer = setInterval(function () {
                                    sec--;
                                    var secString = sec > 9 ? sec : '0' + sec;
                                    timelabel.text('0:' + secString);
                                    if (sec == 0) {
										//если еще не подтвердили код
										if(isConfirmed.hasClass('hide'))
										{
											//интервал закончился показываем возможность отправить еще раз
											repeat.removeClass('hide');
											clearInterval(idTimer);
											timer.addClass('hide');
										}
                                    }
                                }, 1000);
                            }
                        }
                    });
                });
            }
	}
	
	//скрывает элемент, hide=true;false прятать или открывать, pair элемент который скрываем
	function hideElem(hide, pair)
	{
		if (hide) {
			pair.data('width', pair.width()).animate({'width': 0}, 500, function () {
				pair.addClass('hide');
				t.closest('.row').addClass('green');
			});
		}
		else {
			pair.removeClass('hide').animate({'width': pair.data('width')}, 500, function () {
				t.closest('.row').removeClass('green');
				pair.children().focus();
			});
		}
	}

    function saveUserWork(data, callback) {
        var holder = {
            data: data
        };

        if (data && data.mainData && data.mainData.phoneCode) {
            holder.phoneVerified = $('.step-1').find('#phone').hasClass('verified');
        }
        if (data && data.mainData && data.mainData.emailCode) {
            holder.emailVerified = $('.step-1').find('#email').hasClass('verified');
        }

        holder.activeStep = parseStepNum($('.step.active'));

        return $.ajax({
            url: '/main/rest/firstrequest/cache',
            method: 'POST',
            dataType: 'json',
            data: JSON.stringify(holder),
            contentType: 'application/json',
            success: function(data) {
                if (callback && data && data.success) {
                    callback();
                }
            }
        });
    }

    function collectData() {
        var data = collectStep1Data();
        $.extend(true, data, collectStep2Data());
        $.extend(true, data, collectStep3Data());
        $.extend(true, data, collectStep4Data());
        $.extend(true, data, collectStep5Data());
        return data;
    }

    function fillData(holder) {
        fillStep1Data($('.step-1'), holder);
        fillStep2Data($('.step-2'), holder);
        fillStep3Data($('.step-3'), holder);
        fillStep4Data($('.step-4'), holder);
        fillStep5Data($('.step-5'), holder);

        var idx;
        $('.anketa-nav li').each(function () {
            var $el = $(this);

            idx = $el.index() + 1;
            if (!validateStep($('.step-' + idx), false)) {
                return false;
            }
        });

        if (idx) {
            switchStepForm(idx);
        }

        $(document).trigger('initialData.finish');
    }

    function collectStep1Data() {
        var $step1 = $('.step-1');

        return {
            mainData: collectPeopleMain1($step1),
            agreeData: collectPeopleAgreements($step1)
        };

        function collectPeopleMain1($step) {
            var data = {};

            data.phone = unmaskPhone($step.find('#phone input.value').val());
            data.phoneCode = $step.find('#phone input.code').val();
            //data.phoneVerified = $step.find('#phone input.code').closest('.verification').hasClass('verified');
            data.email = $step.find('#email input.value').val();
            data.emailCode = $step.find('#email input.code').val();
            //data.emailVerified = $step.find('#email input.code').closest('.verification').hasClass('verified');

            return data;
        }

        function collectPeopleAgreements($step) {
            var data = {};

            data.dogovorAgree = $step.find('#dogovorAgree input').is(':checked');
            data.useInfoAgree = $step.find('#useInfoAgree input').is(':checked');
            //data.deliveryAgree = $step.find('#deliveryAgree input').is(':checked');

            return data;
        }
    }

    function fillStep1Data($step, holder) {
        var data = holder.data;

        data.mainData && fillPeopleMain(data.mainData);
        data.agreeData && fillPeopleAgreements(data.agreeData);

        function fillPeopleMain(data) {
            if (data) {
                data.phone && $step.find('#phone input.value').val(data.phone);
                data.phoneCode && $step.find('#phone input.code').val(data.phoneCode);
                holder.phoneVerified && setInputConfirmed($step.find('#phone input.code'));
                data.email && $step.find('#email input.value').val(data.email);
                data.emailCode && $step.find('#email input.code').val(data.emailCode);
                holder.emailVerified && setInputConfirmed($step.find('#email input.code'));
            }
        }

        function fillPeopleAgreements(data) {
            data.dogovorAgree && $step.find('#dogovorAgree input').prop('checked', true);
            data.useInfoAgree && $step.find('#useInfoAgree input').prop('checked', true);
            //data.deliveryAgree && $step.find('#deliveryAgree input').prop('checked', true);
        }
    }

    function collectStep2Data() {
        var $step2 = $('.step-2');

        return {
            mainData: collectPeopleMain2($step2),
            passportData: collectPassportData($step2.find('.passport')),
            addrData: collectPeopleAddressData1($step2),
            famData: collectFamilyData($step2.find('#famData')),
            empData: collectEmpData($step2)
        };

        function collectPeopleMain2($step) {
            var data = {};

            data.lastName = $step.find('#lastName input').val();
            data.firstName = $step.find('#firstName input').val();
            data.middleName = $step.find('#middleName input').val();
            data.birthday = unmaskDate($step.find('#birthday input').val());
            data.placeOfBith = $step.find('#placeOfBith input').val();
            data.gender = $step.find('#gender input:checked').val();

            return data;
        }

        function collectPassportData($step) {
            return {
                nomer: $step.find('#nomer input').val(),
                seria: $step.find('#seria input').val(),
                dateGive: unmaskDate($step.find('#dateGive input').val()),
                byGive: $step.find('#byGive input').val(),
                kodByGive: $step.find('#kod input').val()
            }
        }

        function collectPeopleAddressData1($step) {
            var resp = {
                regAddress: collectAddressData($step.find('#regAddress'))
            };

            return resp;
        }

        function collectFamilyData($step) {
            var res = {
                famStatus: $step.find('#marriage').find(':selected').attr('value'),
                childCount: $step.find('#childCount').val()
            };

            res.childCount = res.childCount || 0;

            return res;
        }

        function collectEmpData($step) {
            var educationOpt = $step.find('#education').find(':selected');

            var res = {
                educationId: educationOpt.attr('value'),
                education: educationOpt.text()
            };

            return res;
        }
    }

    function fillStep2Data($step, holder) {
        var data = holder.data;
        data.mainData && fillPeopleMain(data.mainData);
        data.passportData && fillPassportData(data.passportData);
        data.addrData && data.addrData.regAddress && fillAddressData($step, data.addrData.regAddress, null, holder.fiasMap);
        data.famData && fillFamilyData(data.famData);
        data.empData && fillEmpData(data.empData);

        function fillPeopleMain(data) {
            data.lastName && $step.find('#lastName input').val(data.lastName);
            data.firstName && $step.find('#firstName input').val(data.firstName);
            data.middleName && $step.find('#middleName input').val(data.middleName);
            data.birthday && $step.find('#birthday input').val(data.birthday);
            data.placeOfBith && $step.find('#placeOfBith input').val(data.placeOfBith);
            data.gender && $step.find('#gender input[value=' + data.gender + ']').prop('checked', true);
        }

        function fillPassportData(data) {
            data.nomer && $step.find('#nomer input').val(data.nomer);
            data.seria && $step.find('#seria input').val(data.seria);
            data.dateGive && $step.find('#dateGive input').val(data.dateGive);
            data.byGive && $step.find('#byGive input').val(data.byGive);
            data.kodByGive && $step.find('#kod input').val(data.kodByGive);
        }

        function fillFamilyData(data) {
            if (data.famStatus != null) {
                $step.find('#marriage').find('option[value=' + data.famStatus + ']').prop('selected', true).closest('select').trigger('change');
            }
            data.childCount && $step.find('#childCount').val(data.childCount);
        }

        function fillEmpData(data) {
            if (data.educationId != null) {
                $step.find('#education').find('option[value=' + data.educationId + ']').prop('selected', true).closest('select').trigger('change');
            }
        }
    }

    function collectStep3Data() {
        var $step3 = $('.step-3');

        var res = {
            addrData: collectPeopleAddressData($step3)
        };

        return res;

        function collectPeopleAddressData($step) {
            var resp = {
                regIsLive: $step.find('#regIsLive').is(':checked'),
                liveRealty: $step.find('.liveRealty').find(':selected').attr('value'),
                liveAddress: {
                    realtyDate: unmaskDate($step.find('#realtyDate input').val()),
                    residentPhone: unmaskPhone($step.find('#residentPhone input').val()),
                    phone: unmaskPhone($step.find('#realtyPhone input').val())
                }
            };

            if (!resp.regIsLive) {
                $.extend(true, resp, {
                    liveAddress: collectAddressData($step.find('#liveAddress'))
                });
            }

            return resp;
        }

    }

    function fillStep3Data($step, holder) {
        var data = holder.data;
        if (data.addrData) {
            if (!data.addrData.regIsLive) {
                data.addrData.liveAddress && fillAddressData($step, data.addrData.liveAddress, null, holder.fiasMap);
            } else {
                $step.find('#regIsLive').prop('checked', true);
                $step.find('#regIsLive').trigger('change');
                $('.hideblock-30').hide();
            }
            data.addrData.liveAddress && data.addrData.liveAddress.phone && $step.find('#realtyPhone input').val(data.addrData.liveAddress.phone);
            data.addrData.liveAddress && data.addrData.liveAddress.residentPhone && $step.find('#residentPhone input').val(data.addrData.liveAddress.residentPhone);
            data.addrData.liveAddress && data.addrData.liveAddress.realtyDate && $step.find('#realtyDate input').val(maskDate(data.addrData.liveAddress.realtyDate));

            // когда data.addrData.liveRealty == 0 условие не выполняется, select не выбирается со значением 0
            //data.addrData.liveRealty && $step.find('.liveRealty').find('option[value=' + data.addrData.liveRealty + ']').prop('selected', true).closest('select').trigger('change');

            if (data.addrData.liveRealty != null) {
                $step.find('.liveRealty').find('option[value=' + data.addrData.liveRealty + ']').prop('selected', true).closest('select').trigger('change');
            }
        }

        function maskDate(str) {
            return str && str.split('.').slice(1).join('.');
        }
    }

    function collectStep4Data() {
        var $step4 = $('.step-4');

        var res = {
            empData: collectEmpData($step4),
            jobAddrData: collectAddressData($step4.find('#jobAddress'))
        };

        return res;

        function collectEmpData($step) {
            var typeWorkOpt = $step.find('#employ').find(':selected'),
                professionOpt = $step.find('#profession').find(':selected'),
                professionTypeOpt = $step.find('#professionTypes').find(':selected');

            var res = {
                typeWorkId: typeWorkOpt.attr('value'),
                typeWork: typeWorkOpt.text(),
                salary: $step.find('#salary').val(),
                salaryDate: unmaskDate($step.find('#salaryDate').val()),
                occupation: $step.find('#occupation').val(),
                placeWork: $step.find('#placeWork').val(),
                dateStartWork: unmaskDate($step.find('#dateStartWork').val()),
                professionId: professionOpt.attr('value'),
                professionTypeId: professionTypeOpt.attr('value'),
                profession: professionOpt.text(),
                workPhone: $step.find('#workPhone').val()
            };

            if (!(res.isAdditOutcomeSumExists = $step.find('#isadditoutcome').is(':checked'))) {
                res.additOutcomeSum = $step.find('#additOutcomeSum').val();
            }

            return res;
        }
    }

    function fillStep4Data($step, holder) {
        var data = holder.data;
        data.jobAddrData && fillAddressData($step, data.jobAddrData, null, holder.fiasMap);
        data.empData && fillEmpData(data.empData);

        function fillEmpData(data) {
            //data.typeWorkId && $step.find('#employ').find('option[value=' + data.typeWorkId + ']').prop('selected', true).closest('select').trigger('change');
            if (data.typeWorkId != null) {
                $step.find('#employ').find('option[value=' + data.typeWorkId + ']').prop('selected', true).closest('select').trigger('change');
            }
            data.salary && $step.find('#salary').val(data.salary);
            data.salaryDate && $step.find('#salaryDate').val(data.salaryDate);
            data.occupation && $step.find('#occupation').val(data.occupation);
            data.placeWork && $step.find('#placeWork').val(data.placeWork);
            data.dateStartWork && $step.find('#dateStartWork').val(maskDate(data.dateStartWork));
            //data.professionId && $step.find('#profession').find('option[value=' + data.professionId + ']').prop('selected', true).closest('select').trigger('change');
            if (data.professionId != null) {
                $step.find('#profession').find('option[value=' + data.professionId + ']').prop('selected', true).closest('select').trigger('change');
            }
            //data.professionTypeId && $step.find('#professionTypes').find('option[value=' + data.professionTypeId + ']').prop('selected', true).closest('select').trigger('change');
            if (data.professionTypeId != null) {
                $step.find('#professionTypes').find('option[value=' + data.professionTypeId + ']').prop('selected', true).closest('select').trigger('change');
            }
            data.workPhone && $step.find('#workPhone').val(data.workPhone);

            if (data.isAdditOutcomeSumExists) {
                $step.find('#isadditoutcome').prop('checked', true).trigger('change').closest('.row').removeClass('red');
            } else {
                data.additOutcomeSum && $step.find('#additOutcomeSum').val(data.additOutcomeSum);
            }
        }

        function maskDate(str) {
            return str && str.split('.').slice(1).join('.');
        }
    }

    function collectAddressData($step) {
        return {
            fiasId: $step.find('.address').find('select').last().find(':checked').attr('value'),
            home: $step.find('.home').val(),
            builder: $step.find('.builder').val(),
            korpus: $step.find('.korpus').val(),
            apartment: $step.find('.apartment').val()
        };
    }

    function fillAddressData($step, data, skipPhone, fiasMap) {
        data.fiasId && $step.find('.address').find('select').first().address('setAddressChain', fiasMap[data.fiasId]);
        data.home && $step.find('.home').val(data.home);
        data.builder && $step.find('.builder').val(data.builder);
        data.korpus && $step.find('.korpus').val(data.korpus);
        data.apartment && $step.find('.apartment').val(data.apartment);
        data.realtyDate && $step.find('.realtyDate').val(data.realtyDate);
        data.phone && !skipPhone && $step.find('.phone').val(data.phone);
    }

    function collectStep5Data() {
        var $step = $('.step-5');

        var res;

        if ($('#otherCreditExists').is(':checked')) {
            var $bankOpt = $step.find('#banks').find(':selected'),
                $currencyTypeOpt = $step.find('#currencyTypes').find(':selected'),
                $creditTypeOpt = $step.find('#creditTypes').find(':selected'),
                $сreditOverdueTypeOpt = $step.find('#сreditOverdueTypes').find(':selected'),
                $isovers = $step.find('#creditIsOver').find(':selected');
            res = {
                prevcredits: 1,
                creditOrganization: $bankOpt.attr('value'),
                creditOrganizationTitle: $bankOpt.text(),
                creditsumprev: $step.find('#creditSumPrev').val(),
                creditisover: ($isovers.attr('value') == 1) ? 1 : 0,
                currencytype: $currencyTypeOpt.attr('value'),
                currencytypeTitle: $currencyTypeOpt.text(),
                credittype: $creditTypeOpt.attr('value'),
                credittypeTitle: $creditTypeOpt.text(),
                overdue: $сreditOverdueTypeOpt.attr('value'),
                overdueTitle: $сreditOverdueTypeOpt.text(),
                creditdate: unmaskDate($step.find('#creditdate').val()),
                creditSumMonth: $step.find('#creditSumMonth').val(),
                creditclosedate: unmaskDate($step.find('#creditclosedate').val()),
                factcreditclosedate: unmaskDate($step.find('#factcreditclosedate').val())
            };
        } else {
            res = {
                prevcredits: 0
            };
        }

        return {crOtherData: res};
    }

    function fillStep5Data($step, holder) {
        if (holder.data && holder.data.crOtherData) {
            var data = holder.data.crOtherData;

            data.prevcredits && $step.find('#otherCreditExists').prop('checked', true).trigger('change');
            //data.creditOrganizationTitle && $step.find('#banks').find('option[value="' + data.creditOrganizationTitle + '"]').prop('selected', true).closest('select').trigger('change');
            if (data.creditOrganizationTitle !=null) {
                $step.find('#banks').find('option[value="' + data.creditOrganization + '"]').prop('selected', true).closest('select').trigger('change');
            }
            data.creditsumprev && $step.find('#creditSumPrev').val(data.creditsumprev);
            // если data.creditisover == 0 условие не выполняется
            //data.creditisover && $step.find('#creditIsOver').find('option[value="' + data.creditisover + '"]').prop('selected', true).closest('select').trigger('change');
            if (data.creditisover != null) {
                $step.find('#creditIsOver').find('option[value="' + data.creditisover + '"]').prop('selected', true).closest('select').trigger('change');
            }
            //data.currencytypeTitle && $step.find('#currencyTypes').find('option[value="' + data.currencytypeTitle + '"]').prop('selected', true).closest('select').trigger('change');
            if (data.currencytypeTitle != null) {
                $step.find('#currencyTypes').find('option[value="' + data.currencytype + '"]').prop('selected', true).closest('select').trigger('change');
            }
            //data.credittypeTitle && $step.find('#creditTypes').find('option[value="' + data.credittypeTitle + '"]').prop('selected', true).closest('select').trigger('change');
            if (data.credittypeTitle != null) {
                $step.find('#creditTypes').find('option[value="' + data.credittype + '"]').prop('selected', true).closest('select').trigger('change');
            }
            //data.overdueTitle && $step.find('#сreditOverdueTypes').find('option[value="' + data.overdueTitle + '"]').prop('selected', true).closest('select').trigger('change');
            if (data.overdueTitle != null) {
                $step.find('#сreditOverdueTypes').find('option[value="' + data.overdue + '"]').prop('selected', true).closest('select').trigger('change');
            }
            data.creditdate && $step.find('#creditdate').val(data.creditdate);
            data.creditSumMonth && $step.find('#creditSumMonth').val(data.creditSumMonth);
            data.creditclosedate && $step.find('#creditclosedate').val(data.creditclosedate);
            data.factcreditclosedate && $step.find('#factcreditclosedate').val(data.factcreditclosedate);

        }
    }

    function collectSumData() {
        return {
            creditDays: $('#date_zaim-input').val(),
            creditSum: $('#summ_zaim-input').val()
        }
    }

    function unmaskDate(str) {
        if ((str.match(/\//g) || []).length === 1) {
            str = '01 / ' + str;
        }
        return str && str.replace(/\D/g, '').replace(/(\d{2})(\d{2})(\d{4})/, '$1.$2.$3');
    }

    function unmaskPhone(str) {
        return str && str.replace(/\D/g, '');
    }

    function allowOnlyDigits(e) {
        -1 !== $.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) || /65|67|86|88/.test(e.keyCode) && (!0 === e.ctrlKey || !0 === e.metaKey) || 35 <= e.keyCode && 40 >= e.keyCode || (e.shiftKey || 48 > e.keyCode || 57 < e.keyCode) && (96 > e.keyCode || 105 < e.keyCode) && e.preventDefault()
    }

    function firstInvalidStep() {
        var invalidStep;
        $('[class*=step-]').sort().each(function () {
            var $step = $(this);
            if (!validateStep($step, false)) {
                invalidStep = $step;
                return false;
            }
        });
        return invalidStep;
    }

    function switchStepForm(stepNum) {
        var resDefer = $.Deferred();

        setTimeout(function () {
            var targetStep;

            $('.anketa-nav li').each(function () {
                var step = $(this);
                if ((step.index() + 1) > stepNum) {
                    return false;
                }
                step.removeClass('not-access');
                targetStep = step;
            });
            targetStep.trigger('click');
            resDefer.resolve();
        });

        return resDefer.promise();
    }

    function validateStep($step, forceScroll) {
        var required = true;
        var errorInfoMsg;
        forceScroll = forceScroll || true;

        $step.find('.required:visible').each(function () {
            var invalid = false;
            switch ($(this).attr('type')) {
                case 'radio' :
                    var validRadio = false;
                    $step.find('[name="' + $(this).attr('name') + '"]').each(function () {
                        if ($(this).prop('checked')) validRadio = true;
                    });
                    if (!validRadio) invalid = true;
                    break;
                case 'checkbox' :
                    if (!$(this).prop('checked')) invalid = true;
                    break;
                default :
                    if ($(this).val() == '' || $(this).val() === $(this).attr('placeholder') || $(this).val() === "___-___" || $(this).val() === "+7 (___) ___-__-__" || $(this).val() === "мм / гггг" || $(this).val() === "дд / мм / гггг" || $(this).val() === "+7 (___) ___-__-__ доб._____") invalid = true;
            }
            if (invalid && $(this).prop('tagName') != 'SELECT') {
                $(this).closest('.row').removeClass('green').addClass('red');
                required = false;
                errorInfoMsg = 'Вы не заполнили обязательные поля!';
            } else {
                $(this).closest('.row').removeClass('red');
            }
        });
        $step.find('select.required').each(function () {
            if ($(this).parent().is(':visible') && ($(this).val() == null || $(this).val() == '')) {
                $(this).closest('.row').removeClass('green').addClass('red');
                required = false;
                errorInfoMsg = 'Вы не заполнили обязательные поля!';
            } else {
                $(this).closest('.row').removeClass('red');
            }
        });

        $step.find('.verification').each(function () {
            if (!$(this).hasClass('verified')) {
                $(this).nextAll('.errorMessage').first().text('Вы не верифицировали контактные данные!').show().addClass('invalid');
                $(this).closest('.row').removeClass('green').addClass('red');
                required = false;
            } else {
                $(this).nextAll('.errorMessage').first().text('').hide().removeClass('invalid');
            }
        });

        if (required) {
            $step.find('.confirm-kod .btn').each(function () {
                if ($(this).is(':visible')) {
                    $(this).closest('.row').removeClass('green').addClass('red');
                    required = false;
                }
            });

            $step.find('.error-info').addClass('hide');
            if (parseStepNum($step) != null) {
                $('.anketa-indicator li:nth-child(' + parseStepNum($step) + ")").addClass('valid');
            }
        } else {
            $step.find('.error-info').text(errorInfoMsg);

            if (parseStepNum($step) != null) {
                $('.anketa-indicator li:nth-child(' + parseStepNum($step) + ")").removeClass('valid');
            }

            $step.find('.error-info').removeClass('hide');
            forceScroll && scrollToError($step);
        }

        if ($step.find('.invalid, .error').each(function () {
                if ($(this).is(':visible')) {
                    var row = $(this).prev('.row');
                    if (!row.hasClass('error') && !row.hasClass('tooltip')) {
                        row.removeClass('green').addClass('red');
                        required = false;
                        forceScroll && scrollToError($step);
                    }
                }
            }));

        return required;
    }

    function getQueryParam(name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }

    function parseStepNum($step) {
        if ($step.attr('class').match(/step-(\d)/) == null) {
            return null
        }

        return parseInt($step.attr('class').match(/step-(\d)/)[1], 10);
    }

    function scrollToError($step) {
        if ($step.find('.row.red').length > 0) {
            scrollTo($step.find('.row.red').first().closest('.fieldset').offset().top);
        } else if ($step.find('.row.error').length > 0) {
            scrollTo($step.find('.row.error').first().closest('.fieldset').offset().top);
        }
    }


})(window);

/*
function removeSocialFromSession(network, cb) {
    $.getJSON('/main/rest/social/remove/' + network).then(function () {
        cb()
    })
}

function reInitSocialButtons() {
    $.getJSON('/main/rest/social/getAll').then(function (res) {
        if (res.data.fb) {
            $('#fb_li').html('' +
                '<a href="#" class="fb connect" rel="nofollow" onclick="if(confirm(\'Не будете пользоваться этим контактом?\')){removeSocialFromSession(\'fb\', reInitSocialButtons)}return false;"></a>');
            $('#fb_li').addClass('active');
        } else {
            $('#fb_li').html('<a href="#" class="fb" rel="nofollow"></a>');
            $('#fb_li').removeClass('active');
        }
        if (res.data.ok) {
            $('#ok_li').html('' +
                '<a href="#" class="ok connect" rel="nofollow" onclick="if(confirm(\'Не будете пользоваться этим контактом?\')){removeSocialFromSession(\'ok\', reInitSocialButtons)}return false;"></a>');
            $('#ok_li').addClass('active');
        } else {
            $('#ok_li').html('<a href="#" class="ok" rel="nofollow"></a>');
            $('#ok_li').removeClass('active');
        }
        if (res.data.vk) {
            $('#vk_li').html('' +
                '<a href="#" class="vk connect" rel="nofollow" onclick="if(confirm(\'Не будете пользоваться этим контактом?\')){removeSocialFromSession(\'vk\', reInitSocialButtons)}return false;"></a>');
            $('#vk_li').addClass('active');
        } else {
            $('#vk_li').html('<a href="#" class="vk" rel="nofollow"></a>');
            $('#vk_li').removeClass('active');
        }
        if (res.data.ml) {
            $('#mm_li').html('' +
                '<a href="#" class="mm connect" rel="nofollow" onclick="if(confirm(\'Не будете пользоваться этим контактом?\')){removeSocialFromSession(\'ml\', reInitSocialButtons)}return false;"></a>');
            $('#mm_li').addClass('active');
        } else {
            $('#mm_li').html('<a href="#" class="mm" rel="nofollow"></a>');
            $('#mm_li').removeClass('active');
        }


        $('.social-connect a').on('click', function () {
            var type = $(this).attr("class");
            if (type == "ok") {
                console.log("OK send auth")
                window.open("http://www.odnoklassniki.ru/oauth/authorize?client_id=" + ok + "&response_type=code&redirect_uri=" + callback_url + "ok&state=form", "OAuth", "width=750,height=370");
            }
            if (type == "vk") {
                console.log("VK send auth")
                window.open("https://oauth.vk.com/authorize?client_id=" + vk + "&scope=email,offline,notify&redirect_uri=" + callback_url + "vk&display=popup&v=5.21&response_type=token&state=form", "OAuth", "width=750,height=370");
            }
            if (type == "fb") {
                console.log("FB send auth")
                window.open("https://graph.facebook.com/oauth/authorize?client_id=" + fb + "&redirect_uri=" + callback_url + "fb&display=popup&state=form", "OAuth", "width=750,height=370");
            }
            if (type == "mm") {
                console.log("ML send auth")
                window.open("https://connect.mail.ru/oauth/authorize?client_id=" + ml + "&redirect_uri=" + callback_url + "ml&response_type=token&display=popup&state=form", "OAuth", "width=750,height=370");
            }
        });
    })
}*/
