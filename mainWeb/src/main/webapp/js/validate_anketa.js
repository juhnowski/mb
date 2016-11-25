// tooltip error
function tooltipError(id,text,status){
	var tt = $('.marker-red.marker-red--error').filter('[data-id="'+id+'"]');
	status ? tt.text(text).slideDown().addClass('invalid') : tt.slideUp().removeClass('invalid');
}

function isValidDate(d) {
  if ( Object.prototype.toString.call(d) !== "[object Date]" )
    return false;
  return !isNaN(d.getTime());
}

function validateEmail(email) {
    var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    return re.test(email);
}

function unmaskDate(str) {
    if ((str.match(/\//g) || []).length === 1) {
        str = '01 / ' + str;
    }
    return str && str.replace(/\D/g, '').replace(/(\d{2})(\d{2})(\d{4})/, '$1.$2.$3');
}

$('.rus_ext input').on('keypress', function (evt) {
	var keyCode = evt.charCode === 0 ? evt.which : evt.charCode;
	var key = String.fromCharCode(keyCode);
	var pattern = /[а-яА-ЯёЁ0-9\s\-'"]/g;
	if (!pattern.test(key) && keyCode !== 8) {
		tooltipError($(this).attr('data-tooltip'), 'Допустимы только русские символы.', true);
		return false;
	} else {
		tooltipError($(this).attr('data-tooltip'), '', false);
		return true;
	}
});

$('.rus_dash input').on('keypress', function (evt) {
	var keyCode = evt.charCode === 0 ? evt.which : evt.charCode;
	var key = String.fromCharCode(keyCode);
	var pattern = /[а-яА-ЯёЁ\s\-,"]/g;
	if (!pattern.test(key) && keyCode !== 8) {
		$(this).closest('.row').addClass('red');
		tooltipError($(this).attr('data-tooltip'), 'Допустимы только русские символы и тире.', true);
		return false;
	} else {
		$(this).closest('.row').removeClass('red');
		tooltipError($(this).attr('data-tooltip'), '', false);
		return true;
	}
});

$('.rus_ext_dot input').on('keypress', function (evt) {
	var keyCode = evt.charCode === 0 ? evt.which : evt.charCode;
	var key = String.fromCharCode(keyCode);
	var pattern = /[а-яА-ЯёЁ0-9\s\-'\.,"]/g;
	if (!pattern.test(key) && keyCode !== 8) {
		tooltipError($(this).attr('data-tooltip'), 'Допустимы только русские символы, тире, точки и запятые.', true);
		return false;
	} else {
		tooltipError($(this).attr('data-tooltip'), '', false);
		return true;
	}
});

$('.rus input').on('keypress', function (e) {
	var k = e.which;
	if (k > 33 && k < 128) {
		$(this).closest('.col').prev().addClass('error');
		$(this).closest('.col').closest('.row').addClass('error');
		$(this).closest('.col').closest('.row').addClass('red');
		tooltipError($(this).attr('data-tooltip'),'Допустимы только русские символы.',true);
		return false;
	}
	else
	{
		$(this).closest('.col').prev().removeClass('error');
		$(this).closest('.col').closest('.row').removeClass('error');
		$(this).closest('.col').closest('.row').removeClass('red');
		tooltipError($(this).attr('data-tooltip'),'',false);
		return true;
	}
	if (k == 32 && $(this).val().length<=1) 
		return false;
	
});

$('.email input').first().on('blur', function (e) {
	if(!validateEmail($(this).val()))
	{
		$(this).closest('.row').addClass('red');
		tooltipError('emailValidation','Неверный формат email.',true);
		return false;
	}
	else
	{
		$(this).closest('.row').removeClass('red');
		tooltipError('emailValidation','Неверный формат email.',false);
		return true;
	}
	if (k == 32 && $(this).val().length<=1) 
		return false;
	
});

$('.date input, .date-not-dd input').on('change', function(e) {
	var component = $(e.currentTarget).closest('.col').find('.input');
	checkDate(component);
});

$('.above-zero input').on('change blur', function(e) {
	var component = $(e.currentTarget).closest('.col').find('.input');
	if (component.val() <= 0) {
		tooltipError($(this).attr('data-tooltip'), 'Значение должно быть больше нуля.', true);
	} else {
		tooltipError($(this).attr('data-tooltip'), '', false);
		$(this).closest('.row').removeClass('red');
	}
});

$('.above-birth input').on('change', function(e) {
	var component = $(e.currentTarget).closest('.col').find('.input');
	var inDate = getDate(component);
	var birthDate = getDate($('#birthday input'));
	if (inDate < birthDate) {
		tooltipError(component.attr('data-tooltip'), 'Дата не должна быть меньше даты рождения', true);
	} else {
		tooltipError(component.attr('data-tooltip'), '', false);
		$(this).closest('.row').removeClass('red');
	}
});


function getDate($component) {
	var strDate = unmaskDate($component.val());
	var arr = strDate.split('.');
	return new Date(arr[2] +'-'+ arr[1]+'-'+ arr[0]);
}

function checkDate($component) {

    var strDate = unmaskDate($component.val());
	var arr = strDate.split('.');
	var curDate = new Date(arr[2] +'-'+ arr[1]+'-'+ arr[0]);

	if (arr.length == 3 && isValidDate(curDate))
	{
		var maxDate = new Date();
		var hasError = false;
		var text = '';
		maxDate.setHours(0, 0, 0, 0);
		curDate.setHours(0, 0, 0, 0);

		if(!$component.hasClass('date-future'))
		{
			if (curDate.valueOf() > maxDate.valueOf()) {
				hasError = true;
				text = 'Дата не должна быть больше текущей';
			}
		}
        else
        {
            if (curDate.valueOf() < maxDate.valueOf()) {
                hasError = true;
                text = 'Дата не должна быть меньше текущей';
            }
        }

        if(!hasError) {

            var minDate = new Date();
            minDate.setHours(0, 0, 0, 0);
            minDate.setFullYear(minDate.getFullYear() - 100, 0, 1);
            if (curDate.valueOf() < minDate.valueOf()) {
                hasError = true;
                text = 'Дата не должна быть раньше ' + minDate.getFullYear() + ' года'
            }
        }

        if (!hasError) {

            if ($component.is('[birth-date]')) {
                var bd18 = new Date();
                bd18.setFullYear(bd18.getFullYear() - 18);
                bd18.setHours(0, 0, 0, 0);
                var bd70 = new Date();
                bd70.setFullYear(bd70.getFullYear() - 70);
                bd70.setHours(0, 0, 0, 0);
                if (bd18.valueOf() <= curDate.valueOf() || bd70.valueOf() > curDate.valueOf()) {
                    hasError = true;
                    text = 'Мы не выдаем займ лицам моложе 18 и старше 70 лет'
                }
            }
            if ($component.is('[id-issue-date]')) {
                minDate = new Date();
                minDate.setHours(0, 0, 0, 0);
                minDate.setFullYear(1997);
				minDate.setDate(7);
				minDate.setMonth(7)
                if (curDate.getFullYear() < minDate.getFullYear()) {
                    hasError = true;
                    text = 'Дата выдачи паспорта не должна быть раньше 07.07.1997 года';
                }
                else {

                    $.getJSON('/main/rest/firstrequest/info').success(function (res) {

                        var strBirthDate = res.prevData.data.mainData.birthday;
						if (strBirthDate == null) {
							var tmpDate = $('#birthday').find('input').val();
							if (tmpDate != null && tmpDate.length > 0) {
								strBirthDate = tmpDate.replace(/\//g, '.').replace(/\s/g, '');
							}
						}
						if (strBirthDate != null && strBirthDate.length > 0) {
							var arr = strBirthDate.split('.');
							var birthDate = new Date(arr[2] +'-'+ arr[1]+'-'+ arr[0]);
							birthDate.setFullYear(birthDate.getFullYear() + 14);
							if(curDate.valueOf() < birthDate.valueOf()) {
								text = 'Дата выдачи паспорта должна превышать дату рождения не менее, чем на 14 лет';
								$component.closest('.row').addClass('red');
								$('.tooltip-' + $component.attr('data-tooltip')).slideUp();
								tooltipError($component.attr('data-tooltip'), text, true);
							}
							else {
								$component.closest('.row').removeClass('red');
								$('.tooltip-' + $component.attr('data-tooltip')).slideDown();
								tooltipError($component.attr('data-tooltip'), text, false);
							}
						}
                    });
                }


            }
            if($component.is('#start-living-date')) {

                $.getJSON('/main/rest/firstrequest/info').success(function (res) {

                    var strBirthDate = res.prevData.data.mainData.birthday;
                    var arr = strBirthDate.split('.');
                    var birthDate = new Date(arr[2] +'-'+ arr[1]+'-'+ arr[0]);
                    if(curDate.valueOf() < birthDate.valueOf()) {
                        text = 'Дата начала проживания не должна быть раньше даты рождения';
                        $component.closest('.row').addClass('red');
                        $('.tooltip-' + $component.attr('data-tooltip')).slideUp();
                        tooltipError($component.attr('data-tooltip'), text, true);
                    }
                    else {
                        $component.closest('.row').removeClass('red');
                        $('.tooltip-' + $component.attr('data-tooltip')).slideDown();
                        tooltipError($component.attr('data-tooltip'), text, false);
                    }
                });
            }
        }
    }
	else
	{
		text='Некорректный формат даты.';
		hasError = true;
	}
	if(hasError)
	{
		$component.closest('.row').addClass('red');
		$('.tooltip-' + $component.attr('data-tooltip')).slideUp();
		tooltipError($component.attr('data-tooltip'), text, true);
	}
	else
	{
        $component.closest('.row').removeClass('red');
		$('.tooltip-' + $component.attr('data-tooltip')).slideDown();
		tooltipError($component.attr('data-tooltip'), text, false);
	}

	return hasError;
}

// user phone validation
function asyncPhoneValidation($phone, $error) {
	if ($phone.val()) {
		var defer = $.Deferred();

		$.ajax({
			url: '/main/rest/firstrequest/validate/phone',
			data: $phone.val(),
			dataType: 'json',
			contentType: 'application/json',
			method: 'POST'
		}).then(function(res) {
			if (!res.success && res.errorMessage) {
				$error.removeClass('hide');
				return defer.reject();
			} else {
				$error.addClass('hide');
				return defer.resolve();
			}
		});
		return defer.promise();
	}
}

// user email validation
function asyncEmailValidation($email, $error) {
	if ($email.val()) {
		var defer = $.Deferred();

		$.ajax({
			url: '/main/rest/firstrequest/validate/email',
			data: $email.val(),
			dataType: 'json',
			contentType: 'application/json',
			method: 'POST'
		}).then(function(res) {
			if (!res.success && res.errorMessage) {
				$error.removeClass('hide');
				return defer.reject();
			} else {
				$error.addClass('hide');
				return defer.resolve();
			}
		});
		return defer.promise();
	} 
}

$(function() {
	$('#seria input, #nomer input').on('change', function() {
		var $input = $(this);
        var $seria = $('#seria input');
        var $nomer = $('#nomer input');
        var hasError = false;
        if ($input.is($seria)) {
        if ($seria.val().length != 4) {

            $seria.closest('.row').addClass('red');
            tooltipError('seriaValidation', 'Серия должна состоять из 4 цифр', true);
            hasError = true;
        }
        else {

            $seria.closest('.row').removeClass('red');
            tooltipError('seriaValidation', '', false);
        }
    	}
    	if ($input.is($nomer)) {
        if ($nomer.val().length != 6) {

            $nomer.closest('.row').addClass('red');
            tooltipError('passportValidation', 'Номер должен состоять из 6 цифр', true);
            hasError = true;
        }
        else {

            $nomer.closest('.row').removeClass('red');
            tooltipError('passportValidation', '', false);
        }
    	}

        if (!hasError && $seria.val() && $nomer.val()) {
			$.ajax({
				url: '/main/rest/firstrequest/validate/passport',
				data: JSON.stringify({
					nomer: $nomer.val(),
					seria: $seria.val()
				}),
				dataType: 'json',
				contentType: 'application/json',
				method: 'POST'
			}).success(function(res) {
				if (!res.success && res.errorMessage) {
					$('.passport').find('#seria, #nomer').addClass('red');
					tooltipError('passportValidation',res.errorMessage,true);
                    hasError = true;
				} else {
					$('.passport').find('#seria, #nomer').removeClass('red');
					tooltipError('passportValidation',res.errorMessage,false);
				}
			});
		}

        if(!hasError) {
			tooltipError('passportValidation','',false);
		}
	});

	// user main validation
	$('#lastName input, #firstName input, #middleName input, #birthday input').on('change', function() {
		var $lastName = $('#lastName input'),
			$firstName = $('#firstName input'),
			$middleName = $('#middleName input'),
			$birthday = $('#birthday input');

		if ($lastName.val() && $firstName.val() && $middleName.val() && $birthday.val()) {
			$.ajax({
				url: '/main/rest/firstrequest/validate/contacts',
				data: JSON.stringify({
					lastName: $lastName.val(),
					firstName: $firstName.val(),
					middleName: $middleName.val(),
					birthday: $birthday.val().replace(/ \/ /g, '.')
				}),
				dataType: 'json',
				contentType: 'application/json',
				method: 'POST'
			}).success(function(res) {
				if (!res.success && res.errorMessage) {
					$('#lastName, #firstName, #middleName, #birthday').closest('.row').addClass('red');
					tooltipError('contactsValidation',res.errorMessage,true);
				} else {
					$('#lastName, #firstName, #middleName, #birthday').closest('.row').removeClass('red');
					tooltipError('contactsValidation',res.errorMessage,false);
				}
			});
		} else {
			tooltipError('contactsValidation','',false);
		}
	});
});
