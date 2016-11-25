function circle_slider_init(id, set_slider, ratio, params, credit_params){
	if (ratio == undefined) {
		ratio = 1;
	}
	var slider = $('#'+id);
	var slider_circle = slider.parents('.slider-container:first').find('.progress_circle');
	var start_value = parseInt(slider.children('input').val());
	var min = params.min;
	var max = params.max;
	var step = params.step ? params.step : 10;
	var limit = params.limit;

	slider.slider({
		range: 'min',
		min: min * ratio,
		max: max * ratio,
		step: step,
		slide: function (event, ui) {
			set_slider(slider, ui.value / ratio, credit_params);
		}
	});

	circle(slider_circle,Math.floor(100*(start_value - min)/(max - min)));
	set_slider(slider, start_value, credit_params);

	// set width progress bar max limit
	slider.children('.progress_max').width(limit / (slider.slider('option', 'max') + slider.slider('option', 'min')) * slider.width());
	// hide progress bar max limit
	if (slider.slider('option', 'max') == limit)
		slider.children('.progress_max').hide();
}

function jsf_prolong_credit(s_date, v, credit_params) {
	s_date.find('input').val(v).trigger('change');
	
	var min = s_date.slider('option', 'min') / 10;
	var max = s_date.slider('option', 'max') / 10;
	s_date.slider({value:v*10});

	var string_day = s_date.parents('.slider-container:first').find('.alert').children('span');
	s_date.parents('.slider-container:first').find('.alert').children('b').text(v);
	if (v==1 || v == 21 || v == 31)
		string_day.text('день');
	else if (v < 5 || v == 22 || v == 23 || v == 24)
		string_day.text('дня');
	else
		string_day.text('дней');

	var newDt = new Date();
	var monthNames = ["января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"];
	newDt.setDate(newDt.getDate() + v);
	s_date.siblings('.detals').find('.day').text(newDt.getDate()).siblings('.month').text(monthNames[newDt.getMonth()]).siblings('.year').text(newDt.getFullYear());
	s_date.parents('.slider-container:first').find('.progress_circle').val(100 * (v-min) / (max-min)).trigger('change');
	
	$('#longdays').val(v);
	var longdays = parseInt(v);
	var sumOldStake = Math.floor(credit_params.days * (credit_params.creditSum)*(credit_params.creditPercent));	
	var sumStake = Math.round((longdays+credit_params.daysf)  * credit_params.stake * (credit_params.creditSum));
	
	var now = new Date();
	var dateEnd = new Date(credit_params.dateEnd);
	now.setHours(0, 0, 0, 0);
	dateEnd.setHours(24, 0, 0, 0);
	var days = Math.floor((dateEnd.getTime() - now.getTime())/ (24*60*60*1000));
	var sumAll = sumStake + credit_params.creditSum;
		
	var dataend = new Date(credit_params.dateEnd);
	dataend.setDate(credit_params.dateEnd.getDate()+longdays);
	
	var spans = $($('#idcalc tr')[1]).find('td span');
	spans[1].innerHTML = (sumOldStake);
	var monthNames = ["января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"];
	spans[2].innerHTML = dataend.getDate()+' '+monthNames[dataend.getMonth()]+', '+dataend.getFullYear();
	spans[3].innerHTML = (sumStake);
	spans[4].innerHTML = (sumAll);
}

function smsRepeat(p, a) {

	var t = 60;
	p.html('Выслать код повторно через 0:' + t);
	a.hide();
	p.show();
	var timer = setInterval(function () {
		t--;
		if (t > 0) {
			p.html('Выслать код повторно через 0:' + t);
		} else {
			revert();
		}
	}, 1000);

	function revert() {
		p.hide();
		a.show();
		clearInterval(timer);
	}

	return revert;
}

function after_sms_send(b) {
	$('.confirm_settings .valCode_wrapper').show();
	$('.confirm_settings .submit.grey').hide();
	var revert = smsRepeat($('.confirm_settings .valCode_wrapper span'), $('.confirm_settings .valCode_wrapper a'));
	$('.confirm_settings .send_form').height($('.confirm_settings .send_form').height()+20);

	$('.lk-aferta .scroll').jScrollPane({
		verticalDragMinHeight :46,
		verticalDragMaxHeight :46
	});
	return revert;
}

function addcredit_slider_summ(s_summ, v, credit_params) {
	s_summ.find('input').val(v).trigger('change');
	var min = s_summ.slider('option', 'min');
	var max = s_summ.slider('option', 'max');

	s_summ.slider({value:v});

	s_summ.parents('.slider-container:first').find('.alert').children('b').text(v);
	s_summ.parents('.slider-container:first').find('.circle_indicator').find('.progress_circle').val(100 * (v-min) / (max-min)).trigger('change');

	$('#creditSum').val(v);
	addcredit_recalc(v, parseInt($('#creditDays').val()), credit_params);
}


function addcredit_slider_date(s_date, v, credit_params) {
	s_date.find('input').val(v).trigger('change');
	
	var min = s_date.slider('option', 'min') / 10;
	var max = s_date.slider('option', 'max') / 10;
	s_date.slider({value:v*10});

	var string_day = s_date.parents('.slider-container:first').find('.alert').children('span');
	s_date.parents('.slider-container:first').find('.alert').children('b').text(v);
	if (v==1 || v == 21 || v == 31)
		string_day.text('день');
	else if (v < 5 || v == 22 || v == 23 || v == 24)
		string_day.text('дня');
	else
		string_day.text('дней');

	var newDt = new Date();
	var monthNames = ["января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"];
	newDt.setDate(newDt.getDate() + v);
	s_date.siblings('.detals').find('.day').text(newDt.getDate()).siblings('.month').text(monthNames[newDt.getMonth()]).siblings('.year').text(newDt.getFullYear());
	s_date.parents('.slider-container:first').find('.progress_circle').val(100 * (v-min) / (max-min)).trigger('change');
	
	$('#creditDays').val(v);
	addcredit_recalc(parseInt($('#creditSum').val()), v, credit_params);
}

function addcredit_recalc(sum, days, credit_params) {
	//добавление 1 дня
    var stake = calculateStake(sum, days, credit_params.stakeMin, credit_params.stakeMax);
	var sumStake = Math.round((sum) * (stake) * (days+1));
	var sumAll = (sum) + sumStake;
	var dateend = new Date();
	dateend.setDate(dateend.getDate()+days);
	
	var monthNames = ["января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"];
	
	$('#summ_get span').html(sum);
	$('#percent span').html(sumStake);
   // $('#percent i').html((Math.round(stake * 1000)/10).toString().replace('.', ','));
	$('#summ_return span').html(sumAll);
	$('#date_to span').html(dateend.getDate()+' '+monthNames[dateend.getMonth()]+', '+dateend.getFullYear());
}

function refinance_slider_summ(s_summ, v, credit_params) {
	s_summ.find('input').val(v).trigger('change');
	var min = s_summ.slider('option', 'min');
	var max = s_summ.slider('option', 'max');

	s_summ.slider({value:v});

	s_summ.parents('.slider-container:first').find('.alert').children('b').text(v);
	s_summ.parents('.slider-container:first').find('.circle_indicator').find('.progress_circle').val(100 * (v-min) / (max-min)).trigger('change');

	$('#refinanceAmount').val(v);
	refinance_recalc(v, credit_params);
}

function refinance_recalc(sum, credit_params) {
	
	var sumOld=credit_params.sumAll;
	var sumStake = Math.floor((sumOld-(sum)) * (credit_params.stake) * (credit_params.days));
	var sumAll = sumOld-(sum) + sumStake;
	var dataend = new Date();
	dataend.setDate(dataend.getDate()+credit_params.days);
	
	var spans = $($('#idcalc tr')[1]).find('td span');
	//spans[1].innerHTML = (sumOld);
	var monthNames = ["января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"];
	spans[1].innerHTML = (sumStake);
	spans[2].innerHTML = (sumAll);
	spans[3].innerHTML = dataend.getDate()+' '+monthNames[dataend.getMonth()]+', '+dataend.getFullYear();
	
	
}

$(function(){
	//$('.lk-aferta .scroll').jScrollPane({
	//	verticalDragMinHeight :46,
	//	verticalDragMaxHeight :46
	//});
	$('body').on('click', '#what_is_it', function() {
		$(this).hide();
		$('#what_is_it_table').show();
		$('.gray-line, .orange-line').addClass('expand');
	});
});