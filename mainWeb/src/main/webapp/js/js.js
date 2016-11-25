/* UTF-8

© kovrigin
Все права разрешены
красивый дизайн должен иметь красивый код®

http://htmlpluscss.ru

*/

var $window = $(window);
var windowHeight;
var windowScroll;

$window.on({
	resize: function(){
		setTimeout(function(){
			windowHeight = $window.height();

			up_window.each(function(){
				$(this).css('left',(up_box.width()-$(this).outerWidth())/2);
			});

			setTimeout(function(){
				$('main').css('min-height',windowHeight - $('.header').outerHeight() - $('.footer').outerHeight());
			},1);

		},1);
	},
	scroll: function(){
		windowScroll = $window.scrollTop();

	// parallax
	//	if($('.statistic_block').length>0)
	//		$('.statistic_block').css('background-position','center '+ ( 100 - ($('.statistic_block').offset().top + $('.statistic_block').outerHeight() - windowScroll) * 100 / ( windowHeight + $('.statistic_block').outerHeight() ) ) + '%');

	// faq
		if($('.faq').length>0) {
			var h2 = $('.faq .list .box h2');
			var box = $('.faq .list .box');
			var block = box.children('.nav-block');
			var blockHeight = block.outerHeight(true);
			var boxTop = box.offset().top;
			var boxHeight = box.height();
			block.removeAttr('style');
			if (windowScroll > boxTop) {
				if (windowScroll > boxHeight + boxTop - blockHeight){
					block.css('top',boxHeight - blockHeight).removeClass('fixed');
				}
				else
					block.css('left',box.offset().left).addClass('fixed');
			}
			else
				block.removeClass('fixed');

			h2.each(function(i){
				block.find('li').eq(i).addClass('active').siblings('.active').removeClass('active');
				if($(this).offset().top>=windowScroll)
					return false;
			});
		}

	}

});

function initTabsIndex(){
	// tabs_block

	if ($('.tabs_block').length>0)
		$('.tabs_block .tabs').tabs();
};


$window.ready(function(){



	$window.trigger('resize').trigger('scroll');

// chechbox
	$('.checkbox').checkBox();

	initTabsIndex();

// input
	$('.col .input').on({
		focus: function(){
			$(this).removeClass('red').add($(this).closest('.row')).addClass('focus').removeClass('green red');
		},
		blur: function(){
			var t = $(this);
			var tr = t.add(t.closest('.row'));
			tr.removeClass('focus');
			setTimeout(function(){
				if(!t.hasClass('not-valid-row'))
					t.val()=='' && t.hasClass('required') ? tr.addClass('red') : tr.addClass('green');

				//test email
				if(t.hasClass('email') && !testEmail(t))
					tr.addClass('red').removeClass('green');
			});
		}
	});
	$('.col.first label').on('click',function(){
		$(this).parent().next().find('.input').focus();
	});

// pagin back
	$('.pagin .prev').on('click',function(){
		$('.anketa-nav li.active').prev().trigger('click');
	});

// data-hideblock
	$('[data-hideblock]').on('change',function(){
		var show = $(this).prop('checked') ? true : false;
		if ($(this).attr('data-hideblock-invert')=='') show = !show;
		dataHideblock($(this),show);

		if($(this).attr('type')=='radio')
			$('[name="' + $(this).attr('name')+'"]').not($(this)).each(function(){
				var show = $(this).attr('data-hideblock-invert')=='' ? true : false;
				dataHideblock($(this),show);
			});
	});
	function dataHideblock(t,show){
		z = $('.hideblock-' + t.attr('data-hideblock'));
		show ? z.slideDown() : z.slideUp();
	}


// hideblock-pair 
	$('[hideblock-pair]').on('change',function(){
		var t = $(this);
		var hide = t.prop('checked') ? true : false;
		var pair = $('.hideblock-pair-' + t.attr('hideblock-pair'));
		if(hide){
			t.closest('.row').removeClass('red');
			pair.data('width',pair.width()).animate({'width':0},500,function(){
				pair.addClass('hide');
				t.closest('.row').addClass('green');
			});
			// так же скрываем ошибку если есть
			tooltipError(pair.find('input').first().attr('data-tooltip'), '', false);
			pair.find('input').removeClass('required');
		}
		else {
			pair.removeClass('hide').animate({'width':pair.data('width')},500,function(){
				t.closest('.row').removeClass('green');
				pair.children().focus();
			});
			pair.find('input').addClass('required');
		}
	});

// data-tooltip 
	$('[data-tooltip]').on({
		focus: function(){
			$('.tooltip-' + $(this).attr('data-tooltip')).slideDown();
		},
		blur: function(){
			$('.tooltip-' + $(this).attr('data-tooltip')).slideUp();
		}
	});

// credit-close
	$('.credit-close').on('change',function(){
		$(this).val()==2 ? $('.row.credit-close').slideDown() : $('.row.credit-close').slideUp();
	});

// mask
	$('.numer').on('change keyup keydown', function(event) {
		var code = event.keyCode;
		if(event.type == 'keydown' && ((code >= 65 && code <= 90) || (code >= 186 && code <= 222)))
			event.preventDefault();
		if (this.value.match(/[^0-9]/g))
			this.value = this.value.replace(/[^0-9]/g, '');
	});
	$('[type="tel"]').each(function(){
		$(this).hasClass('tel-suf') ?
			$(this).mask("+7 (999) 999-99-99? доб.99999"):
			$(this).mask("+7 (999) 999-99-99");
	});
	$('.date.input').mask("99 / 99 / 9999",{placeholder:"дд / мм / гггг"});
	$('.date-not-dd.input').mask("99 / 9999",{placeholder:"мм / гггг"});
	$('.cod-passport').mask("999-999");

	$('.datepicker').each(function(){
		var t = $(this);
		var input = t.siblings('.input');
		var dateformat = input.hasClass('date-not-dd') ? 'mm / yy' : 'dd / mm / yy';
        var yearRange = '1940:2015';
        if(input.hasClass('date-future')) {
            yearRange = '2015:2040';
        }
		t.children().datepicker({
			changeMonth: true,
			changeYear: true,
			firstDay: 1,
			dateFormat: 'dd / mm / yy',
			yearRange: yearRange,
		//	maxDate:"+1M +10D",
			altFormat: dateformat,
			altField: input,
			dayNamesMin: ['Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'],
			monthNamesShort: ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"],
			monthNames: ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"],
			showOn:"button"
		});
	});



// select

	$('select').each(function () {
		$(this).select2({
		language: {
			noResults: function () {
				return 'Совпадений не найдено';
			},
		},
			minimumResultsForSearch: 10,
		});
		$(this).on("select2:open", function () {
			if(!$(this).hasClass('not-valid-row'))
				$(this).closest('.row').addClass('focus').removeClass('red');
		});
		$(this).on("select2:close", function () {
			$(this).closest('.row').removeClass('focus');
			if(!$(this).hasClass('not-valid-row'))
				$(this).val()=='' ?
					$(this).closest('.row').addClass('red'):
					$(this).closest('.row').addClass('green');
		});
	});

// Modernizr
//	if(!Modernizr.csstransforms3d)
//		$('.start-slider .btn-send .back').hide();
// placeholder
//	if(!Modernizr.input.placeholder)
//		function insertInput() {
//			var input = document.createElement('input');
//			input.type = "text";
//			input.setAttribute( 'placeholder', "Enter value" );
//			document.body.appendChild( input );
//			inputPlaceholder( input );
//		}
// svg -> png
//	if (!Modernizr.svg)
//		$('img').each(function(){
//			$(this).attr('src',$(this).attr('src').replace('.svg','.png'));
//		});

// menu
//	(function(m){
//
//		var initialPoint;
//
//		$('.sub-menu .center').on('touchstart touchmove', function(event){
//			var x = event.originalEvent.touches[0].pageX;
//				if(event.type == 'touchstart')
//					initialPoint = x;
//				else if (x - initialPoint > 5 && event.originalEvent.touches[1] === undefined)
//					$('.submenu-trigger').trigger('click');
//		})
//
//
//		$('.sub-menu').on('click',function(event) {
//			if($(this).hasClass('active') && $(event.target).is($(this)))
//				$('.submenu-trigger').trigger('click');
//		});
//
//
//		m.find('li').hover(function(){
//			m.addClass('hover');
//		},
//		function(){
//			m.removeClass('hover');
//		});
//	}($('.top-menu')));
//
//	$('.submenu-trigger').on('click', function() {
//		$('.sub-menu, .ico-trigger').toggleClass('active');
//	});



// anketa-nav
	$('.anketa-nav li').on('click',function(){
		var t = $(this);
		if(t.hasClass('active') || t.hasClass('not-access'))
			return true;
		var top = $('.anketa-nav').offset().top;
		var time = top - windowScroll == 0 ? 0 : 1000;
		scrollTo(top);
		setTimeout(function(){
			t.addClass('active').siblings('.active').removeClass('active');
			$('.anketa .step').eq(t.index()).addClass('active').siblings('.active').removeClass('active');
		},time);
	});
	$('.anketa .step').eq($('.anketa-nav li.active').index()).addClass('active');

	// mobile
	$('.anketa-send-cod_up input').on('keyup',function(){
		var inputCod = $(this).parent().removeClass('red').closest('.sms-cod').length>0 ?
			$('#confirm-sms .input-cod'):
			$('#confirm-mail .input-cod');
		inputCod.val($(this).val()).trigger('change');
	});
	$('.anketa-send-cod_up .btn').on('click',function(){
		var btn = $(this).closest('.sms-cod').length>0 ?
			$('#confirm-sms .btn'):
			$('#confirm-mail .btn');
		btn.trigger('click');
	});
	$('.anketa-send-cod_up .close').on('click',function(){
		$('.anketa-send-cod_up').removeClass('show');
		var btn = $(this).closest('.sms-cod').length>0 ?
			$('#confirm-sms .btn'):
			$('#confirm-mail .btn');
		btn.removeClass('repeat').show();
	});


// page begin

// post news
	if($('.img-post').length>0)
		$('.news-post .img-head').css('background-image','url('+$('.img-post').attr('src')+')');

// loading
	$('.load-next a').on('click',function(){
		$(this).toggleClass('active');
	});

// contacts
	$('#feedback .subject input').on('change',function(){
		if($(this).prop('checked'))
			$(this).parent().addClass('active').siblings().removeClass('active');
	}).trigger('change');


	initAccordion();

// politics 
	$('.politics .accordion dt a').on('click',function(e){
		e.stopPropagation();
	});



// alert_up
	$('body').append('<span class="bg_up"></span>');
	up_bg = $('.bg_up');
	up_box = $('.alert_box');
	up_window = up_box.children().each(function(){
		$(this).css('left',(up_box.width()-$(this).outerWidth())/2);
	});

	up_box.on('click',function() {
		var t = up_window.filter('.animate');
		t.css('top', -t.outerHeight()).afterTransition(function(){
			up_bg.fadeOut();
			up_box.hide();
			up_window.removeClass('animate');
			$('body').css({
				'overflow' : 'inherit',
				'margin-left' : 0
			});
			blurSite();
		});
	})
	.children().on('click',function(event){
		event.stopPropagation();
	})
	.find('.close').on('click',function(){
		up_box.triggerHandler('click');
	});

	$('.support-show').on('click',function(){
		alert_top(up_window.filter('.support_up'));
		blurSite(true);
	});

	$('.callback').on('click',function(){
		alert_top(up_window.filter('.callback_up'));
		blurSite(true);
	});

    $('.login-show').on('click',function(){
        alert_top(up_window.filter('.login_up'));
        $('.pass_recowery_up .btn').show();
        $('.pass_new_up .btn').show();
        blurSite(true);
    });

// recovery pass

	$('.login_up a').on('click',function(){
		alert_top(up_window.filter('.pass_recowery_up'));
	});

	//$('.pass_recowery_up form').on('submit',function(){
	//	var email = $('.pass_recowery_up .email');
	//	var pass1 = $('.pass_recowery_up .password-1');
	//	var pass2 = $('.pass_recowery_up .password-2');
	//	var error = $('<p>').addClass('error');
	//	$('.pass_recowery_up p.error').remove();
	//	if (email.val().length == 0) {
	//		error.text('Введите email');
	//		email.one('focus',function(){
	//			$(this).removeClass('red').next('.error').remove();
	//		});
	//		//email.addClass('red').after(error);
	//		return false;
	//	}
    //
	//	if (!testEmail(email)) {
	//		email.addClass('red').after(error);
	//		return false;
	//	}
    //
	//	if (pass1.val() != null && (pass1.val().length < 5 || pass1.val().length > 15 || pass1.val() != pass2.val())) {
	//		if(pass1.val() != pass2.val())
	//			error.text('Пароли не совпадают');
	//		else if(pass1.val().length < 5)
	//			error.text('Пароль должен быть не менее 5 символов');
	//		else if(pass1.val().length > 15)
	//			error.text('Пароль должен быть не более 15 символов');
	//		pass2.after(error);
	//		pass1.add(pass2).addClass('red').one('focus',function(){
	//			$(this).removeClass('red').next('.error').remove();
	//		});
	//		return false;
	//	}
	//	alert_top(up_window.filter('.pass_new_up'));
	//	return false;
	//});

// sms_kod_up
//	$('.input-box-cod input').on('keyup', function(){
//		var t = $(this);
//		if(t.val().length >= parseInt(t.attr('maxlength'))){
//			t.blur().parent().addClass('send-data-server');
//
//			//temp data
//			setTimeout(function(){
//				t.one('focus',function(){
//					t.parent().removeClass('red');
//				});
//				t.parent().removeClass('send-data-server');
//				if(t.val()==111111) {
//					t.parent().addClass('green');
//					setTimeout(function(){
//						up_box.triggerHandler('click');
//					},3000);
//				}
//				else
//					t.val('').parent().addClass('red');
//
//			},5000);
//
//		}
//	});

	// timer
	if ($('#timer').length > 0) {
		timerClock();
	}
});

function timerClock() {
	var t = new Date((new Date()).getTime() + 15 * 60000);
	var h = t.getHours();
	var m = t.getMinutes();
	var s = (t.getSeconds() % 2 == 0);
	if (h < 10) {
		h = "0" + h;
	}
	if (m < 10) {
		m = "0" + m;
	}
	$('#timer').html(h + "<span>" + (s ? ":" : "&nbsp;") + "</span>" + m);
	setTimeout('timerClock()', 1000);
}

function initAccordion(){
	// accordion
	$('.accordion dt').on('click',function(){
		var t = $(this);
		if(t.hasClass('active'))
			t.next().slideUp(function(){
				t.removeClass('active');
			});
		else{
			var a = $('.accordion dt.active');
			a.next().slideUp(function(){
				a.removeClass('active');
			});
			t.addClass('active').next().slideDown();
		}
	});
}

function initFaq(){
	// faq list
	$('.faq dt').on('click',function(){
		var t = $(this);
		if(t.hasClass('active'))
			t.removeClass('active').next().slideUp();
		else{
			$('.faq dt.active').removeClass('active').next().slideUp();
			t.addClass('active').next().slideDown();
		}
	});
	$('.faq .list .nav-block li').on('click',function(){
		scrollTo($('.faq .box h2').eq($(this).index()).offset().top);
	});

	// faq ancor
	if ($('.faq').length > 0) {
		$('a[href*="#"]').on('click', function () {
			setTimeout(function () {
				openAncor();
			}, 100);
		});
		function openAncor() {
			var ankor = window.location.href.split('#')[1];
			if (ankor !== undefined) {
				var a = $('[data-ankor=' + ankor + ']');
				if (a.offset() !== undefined) {
					var top = a.offset().top;
					if ($('.faq dt.active').length > 0 && $('.faq dt.active').offset().top < top)
						top -= $('.faq dt.active').next().outerHeight();
					scrollTo(top);
					if (!a.hasClass('active'))
						a.trigger('click');
				}
			}
		}
		openAncor();
	}
}

function declension(num, expressions) {
	var result;
	var count = num % 100;
	if (count > 4 && count < 21) 
		result = expressions['2'];
	else {
		count = count % 10;
		if (count == 1)
			result = expressions['0'];
		else if (count > 1 && count < 5)
			result = expressions['1'];
		else
			result = expressions['2'];
	}
	return result;
}

// tooltip error
function tooltipError(id,text,status){
	var tt = $('.tooltip.error').filter('[data-id="'+id+'"]');
	status ? tt.text(text).slideDown() : tt.slideUp();
}

// test e-mail
function testEmail(t){
	var filterEmail = /^([a-z0-9_'&\.\-\+=])+\@(([a-z0-9\-])+\.)+([a-z0-9]{2,10})+$/i;
	return filterEmail.test(t.val());
}

// number separator
function sep(str){
	str = str.toString();
	return str.replace(/(\d)(?=(\d\d\d)+([^\d]|$))/g, '$1 ');
}

// scrollTop
function scrollTo(t){
	$('body, html').animate({scrollTop : t}, 1000);
}

// confirm-kod
function confirmKod(type,status) {
	var strType = type;
	var type = $('#confirm-'+type);
	var inform = type.children('.inform').removeClass('red').addClass('show');
	if(status) {


		type.closest('.row').removeClass('red focus').find('.input').attr('disabled','disabled').blur();
		type.closest('.row').next().slideUp();
		type.children().not(inform).hide();
		inform.text(inform.attr('data-true'));

		$('.anketa-send-cod_up').removeClass('show');
	}

	else {
		inform.addClass('red').text(inform.attr('data-false'));
		inform.siblings('input').val('').siblings('.loading-sms').addClass('hide');
		setTimeout(function(){
			type.closest('.row').addClass('red').find('.input').removeAttr('disabled');
			inform.removeClass('show');
			inform.siblings('.btn, input').removeClass('hide');
		},3000);
		// mobile
		$('.anketa-send-cod_up').addClass('show').find('input').val('').parent().addClass('red');
	}

}


// alert_up
function alert_top(a_up) {
	up_box.show();

	var h_up = a_up.outerHeight();
	a_up.css('top', -h_up);
	if($('body').height()>windowHeight)
		$('body').css({
			'overflow' : 'hidden',
			'margin-left' : -getScrollBarWidth()
		});

	up_bg.fadeIn(function(){
		var a_animate = up_window.filter('.animate');
		if (a_animate.length > 0) {
			a_animate.css('top', -a_animate.outerHeight()).afterTransition(function() {
				a_animate.removeClass('animate');
			});
		}
		var	top = 0;
		if (windowHeight > h_up)
			top = (windowHeight - h_up) / 2;
		a_up.addClass('animate').css('top',top);
	});
}
function blurSite(s) {
	var b = $('.header, main, .footer');
	s ? b.addClass('blur') : b.removeClass('blur');
}
function getScrollBarWidth () {
	var inner = document.createElement('p');
	inner.style.width = "100%";
	inner.style.height = "200px";
	var outer = document.createElement('div');
	outer.style.position = "absolute";
	outer.style.top = "0px";
	outer.style.left = "0px";
	outer.style.visibility = "hidden";
	outer.style.width = "200px";
	outer.style.height = "150px";
	outer.style.overflow = "hidden";
	outer.appendChild (inner);
	document.body.appendChild (outer);
	var w1 = inner.offsetWidth;
	outer.style.overflow = 'scroll';
	var w2 = inner.offsetWidth;
	if (w1 == w2) w2 = outer.clientWidth;
	document.body.removeChild (outer);
	return (w1 - w2);
}

// tabs
;(function($){

	$.fn.tabs = function(){
		var tab = function(){
			var t = $(this);
			var dt = t.children('.dt');
			var dd = t.children('.dd');
			t.append(dd);
			dt.wrapAll('<div class="nav notsel"></div>')
			.on('click',function(){
				var t = $(this);
				t.addClass('active').siblings('.active').removeClass('active');
				dd.removeClass('active').eq(dt.index(t)).addClass('active');
			})
			.filter('.active').length > 0 ? dt.filter('.active').triggerHandler('click') : dt.first().triggerHandler('click');
		}
		return this.each(tab);
	};

})(jQuery);

// chechBox
;(function($){

	$.fn.checkBox = function(){
		var set = function(){
			var t = $(this);
			t.addClass('notsel').append('<i></i>');
			t.children('input').on('change',function(){
				if(!$(this).hasClass('not-valid-row'))
					$(this).prop('checked') ? 
						$(this).closest('.row').addClass('green').removeClass('red') : 
						$(this).closest('.row').addClass('red').removeClass('green');
				if($(this).hasClass('blue-valid-true')){
					$('.row').not($(this)).removeClass('blue-valid-true');
					if($(this).prop('checked'))
						$(this).closest('.row').addClass('blue-valid-true');
				}
			});
		}
		return this.each(set);
	};
})(jQuery);




var onloadRecaptcha = function () {
    if ($('#call-captcha').length > 0) {
        window.recaptchaCallback = grecaptcha.render('call-captcha', {
            'sitekey': '6LdKvAkTAAAAAMehrVqjrPgEQ4CxrkIYSssfZcqk',
            'theme': 'light'
        });
    }

    if ($('#contact-captcha').length > 0) {
        window.recaptchaContact = grecaptcha.render('contact-captcha', {
            'sitekey': '6LdKvAkTAAAAAMehrVqjrPgEQ4CxrkIYSssfZcqk',
            'theme': 'light'
        });
    }

};


function fillSelect(select, items, config) {
    var label = config && config.placeholderText || 'Выбрать',
        valueProp = config && config.valueProp || 'code',
        nameProp = config && config.nameProp || 'name';

    var title = select.parent().find('.select2-selection__rendered');
    title.prop('title', label);
    title.text(label);

    var innerHtml = '<option value="" class="placeholder">' + label + '</option>';
    $.each(items, function(idx, item) {
        innerHtml += '<option value="' + item[valueProp] + '">' + item[nameProp] + '</option>';
    });
    select.html(innerHtml);
    select.on('change', function() {
        if ($(this).find(':selected').attr('value')) {
            $(this).find('.placeholder').remove();
        }
    });
}

// login / recovery stuff

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function valid_vertif(input, text, valid) {
    var u = $('<u>').addClass('red');
    if (input.parent().hasClass('bg_box')) {
        if (input.parent().next().is('u'))
            u = input.parent().next();
        else
            input.parent().after(u);
    }
    else if (input.attr('type') == 'checkbox') {
        if (input.parent().siblings('u').is('u'))
            u = input.parent().siblings('u');
        else
            input.parent().parent().append(u);
    }
    else if (input.next().is('u'))
        u = input.next();
    else
        input.after(u);
    u.text(text);
    if (valid)
        input.closest('.l_i').removeClass('required')
    else
        input.closest('.l_i').addClass('required');
}

function getToken(str) {
    var arr = str.split("?tempLink=");
    return arr[1];
}


function slidersInit(data) {
	var $sumSlider = $('#summ_zaim_slider'),
		$dateSlider = $('#date_zaim_slider');

	var initialCreditDays = data.creditDays,
		initialCreditSum = data.creditSum,
		sumStep = 100,
		daysStep = 1,
		change = false;


	$('#minSumLabel').text(data.creditSumMin);
	$('#maxSumLabel').text(data.creditSumMax);

	$sumSlider.data('data-min', data.creditSumMin);
	$sumSlider.data('data-max', data.creditSumMax);
	$sumSlider.data('data-step', sumStep);
	$sumSlider.slider({
		range: 'min',
		min: data.creditSumMin,
		max: data.creditSumMax,
		step: sumStep,
		value: initialCreditSum,
		create: function(event,ui) {
			$("#summ_zaim").text(sep(initialCreditSum));
			$("#summ_zaim-input").val(initialCreditSum);
		},
		slide: function(event,ui) {
			$("#summ_zaim").text(sep(ui.value));
			$("#summ_zaim-input").val(ui.value);
			result(ui.value, $dateSlider.slider('value'));
		},
		change: function(event,ui) {
			$("#summ_zaim").text(sep(ui.value));
			$("#summ_zaim-input").val(ui.value);
			result(ui.value, $dateSlider.slider('value'));
		}
	});


	$('#minDateLabel').text(data.creditDaysMin);
	$('#maxDateLabel').text(data.creditDaysMax);

	$dateSlider.data('data-min', data.creditDaysMin);
	$dateSlider.data('data-max', data.creditDaysMax);
	$dateSlider.data('data-step', daysStep);
	$dateSlider.slider({
		range: 'min',
		min: data.creditDaysMin,
		max: data.creditDaysMax,
		value: initialCreditDays,
		create: function(event,ui) {
			$("#days_zaim").text(initialCreditDays);
			$("#date_zaim-input").val(initialCreditDays);
			var d = new Date();
			d.setDate(d.getDate() + initialCreditDays);
			setDate(d);
		},
		slide: function(event,ui) {
			$("#days_zaim").text(ui.value);
			$("#date_zaim-input").val(ui.value);
			var d = new Date();
			d.setDate(d.getDate() + ui.value);
			setDate(d);
			result($sumSlider.slider('value'), ui.value);
		},
		change: function(event,ui) {
			$("#days_zaim").text(ui.value);
			$("#date_zaim-input").val(ui.value);
			var d = new Date();
			d.setDate(d.getDate() + ui.value);
			setDate(d);
			result($sumSlider.slider('value'), ui.value);
		}
	});

	result($sumSlider.slider('value'), $dateSlider.slider('value'));

	$('#summ_zaim-input, #date_zaim-input').on('change',function(){
		var v = $(this).val();
		if (v.length==0){
			$(this).val('0').trigger('change');
			return true;
		}
		v = parseInt(v);
		var s = $(this).parent().siblings('.slider');
		if(parseInt(s.data('data-min'))>v)
			v = s.data('data-min');
		else if(parseInt(s.data('data-max'))<v)
			v = s.data('data-max');
		s.slider('value',v);
	});

	$('.zaem-slider .plus, .zaem-slider .minus').on('click',function(){
		var s = $(this).siblings('.slider');
		var v = s.slider('value');
		var step = parseInt(s.data('data-step'));
		if($(this).hasClass('minus'))
			step*=-1;
		v += step;
		if(parseInt(s.data('data-min'))>v)
			v = s.data('data-min');
		else if(parseInt(s.data('data-max'))<v)
			v = s.data('data-max');
		s.slider('value',v);
	});

	function setDate(d){
		$("#date_zaim").html(('0' + d.getDate()).slice(-2) + '<i></i>' + ('0' + (d.getMonth() + 1)).slice(-2) + '<i></i>' + d.getFullYear());
		$("#date_zaim-alt").html(('0' + d.getDate()).slice(-2) + '<i></i>' + ('0' + (d.getMonth() + 1)).slice(-2) + '<i></i>' + d.getFullYear());
	}

	function result(sum, days) {
		var stake = calculateStakeUnknown(sum, days, data.stakeMin, data.stakeMax);
		var finalDays = days + data.additionalDayPayment;
		var diskont = stake * sum * finalDays;
		diskont = Math.floor(diskont);
		$("#procent_zaim").text(sep(stakeClean(stake * 100)));
		$("#total_zaim").text(sep(diskont + sum));
	}

	/**
	 * Убирает 0 (ноль) в периоде.
	 *
	 * Например при sum=1000 и days=6 ставка составит 3.5 000 000 000 000 004, процедура преобразует ставку в 3.5
	 * @param float Ставка из расчета
	 * @returns {number} Ставка после очистки 0 в периоде
	 */
	function stakeClean(stake) {
		return (stake * 100).toFixed(0) / 100;
	}
}

/*
 * Copyright 2012 Andrey тA.I.т Sitnik <andrey@sitnik.ru>,
 * sponsored by Evil Martians.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
(function(d){"use strict";d.Transitions={_names:{'transition':'transitionend','OTransition':'oTransitionEnd','WebkitTransition':'webkitTransitionEnd','MozTransition':'transitionend'},_parseTimes:function(b){var c,a=b.split(/,\s*/);for(var e=0;e<a.length;e++){c=a[e];a[e]=parseFloat(c);if(c.match(/\ds/)){a[e]=a[e]*1000}}return a},getEvent:function(){var b=false;for(var c in this._names){if(typeof(document.body.style[c])!='undefined'){b=this._names[c];break}}this.getEvent=function(){return b};return b},animFrame:function(c){var a=window.requestAnimationFrame||window.webkitRequestAnimationFrame||window.mozRequestAnimationFrame||window.msRequestAnimationFrame;if(a){this.animFrame=function(b){return a.call(window,b)}}else{this.animFrame=function(b){return setTimeout(b,10)}}return this.animFrame(c)},isSupported:function(){return this.getEvent()!==false}};d.extend(d.fn,{afterTransition:function(h,i){if(typeof(i)=='undefined'){i=h;h=1}if(!d.Transitions.isSupported()){for(var f=0;f<this.length;f++){i.call(this[f],{type:'aftertransition',elapsedTime:0,propertyName:'',currentTarget:this[f]})}return this}for(var f=0;f<this.length;f++){var j=d(this[f]);var n=j.css('transition-property').split(/,\s*/);var k=j.css('transition-duration');var l=j.css('transition-delay');k=d.Transitions._parseTimes(k);l=d.Transitions._parseTimes(l);var o,m,p,q,r;for(var g=0;g<n.length;g++){o=n[g];m=k[k.length==1?0:g];p=l[l.length==1?0:g];q=p+(m*h);r=m*h/1000;(function(b,c,a,e){setTimeout(function(){d.Transitions.animFrame(function(){i.call(b[0],{type:'aftertransition',elapsedTime:e,propertyName:c,currentTarget:b[0]})})},a)})(j,o,q,r)}}return this},transitionEnd:function(c){for(var a=0;a<this.length;a++){this[a].addEventListener(d.Transitions.getEvent(),function(b){c.call(this,b)})}return this}})}).call(this,jQuery);
