/* UTF-8

 © kovrigin
 Все права разрешены
 красивый дизайн должен иметь красивый код®

 http://htmlpluscss.ru

 */

var resizeTimeoutId,
    leftBlock = $('.left-block');
$window.on({
    resize: function () {
        leftBlock.height('auto');
        clearTimeout(resizeTimeoutId);
        resizeTimeoutId = setTimeout(function () {
            leftBlock.height($('main').height());
        }, 100);
    }
});

// это временный скрипт
$('.left-block a').each(function () {
    if ($(this).attr('href') == location.pathname)
        $(this).append('<i class="ico arrow-right-white-3"></i>').parent().addClass('active');
});


// edit-setting
$('.edit-setting').on('click', function () {
    if ($(this).attr('data-alt') == $(this).text()) {
        lkSendKodSms($(this).attr('data-mess'));
        return true;
    }
    var f = $(this).closest('form');
    f.find('.fieldset').removeClass('disabled');
    $(this).text($(this).attr('data-alt')).addClass('green').removeClass('white');
});

// tab-lk
$('.tab-lk .tab').on('click', function () {
    var t = $(this);
    if (t.hasClass('radio')) {
        var b = t.closest('.tab-lk').find('.box-44');
        t.closest('.tab-lk').find('.radio').not(t).removeClass('active');
    }
    else {
        if (t.hasClass('title'))
            t = t.siblings('[data-tab="tab-1"]');
        var b = t.parent().siblings('.box-44');
    }
    if (t.hasClass('active')) {
        b.slideUp();
        t.removeClass('active');
    }
    else {
        t.addClass('active').siblings('.tab').removeClass('active');
        var n = b.filter('.' + t.attr('data-tab'));
        b.not(n).slideUp();
        n.slideDown();
    }
});


// verification card
$('.verification-card').on('click', function () {
    var input = $(this).prev();
    if (input.val() == "")
        input.focus();
    else {
        input.val('').attr('placeholder', input.attr('data-alt'));
        $(this).text($(this).attr('data-alt'));
        $(this).off();
    }
});

makeInterfaceProfile = function () {
// help
    $('.help').hover(function () {
            $(this).children().stop().fadeIn(100);
        },
        function () {
            $(this).children().stop().fadeOut(100);
        })
        .wrapInner('<span></span>').children().append('<i></i>').each(function () {
            $(this).css('margin-left', -$(this).outerWidth(true) / 2 + 'px');
        });
};
$window.load(makeInterfaceProfile);

// Send Kod Sms
function lkSendKodSms(text) {
    var f = $('.fieldset');
    var required = true;
    f.find('.required:visible').each(function () {
        var valid = false;
        switch ($(this).attr('type')) {
            case 'checkbox' :
                if (!$(this).prop('checked')) valid = true;
                break;
            default :
                if ($(this).val() == '') valid = true;
        }
        if (valid) {
            $(this).addClass('red').closest('.row').addClass('red');
            required = false;
        }
    });
    f.find('select.required').each(function () {
        if ($(this).parent().is(':visible') && $(this).val() == '') {
            $(this).closest('.row').addClass('red');
            required = false;
        }
    });

    f.find('.invalid, .error').each(function () {
        if ($(this).is(':visible')) {
            var row = $(this).prev('.row');
            row.addClass('red');
            required = false;
        }
    });
    if (!required) {
        scrollTo(f.find('.row.red').first().offset().top);
        return true;
    }

    var up = up_window.filter('.send-sms_up');
    alert_top(up);
    up.find('h3').text(text);
    timerStart(up.find('.timer'));
}

// timer new sms send
function timerStart(timer) {
    var idTimer;
    var btn = timer.next('.retry-sms').hide().one('click', function () {
        clearInterval(idTimer);
        timerStart(timer);
    });
    var label = timer.children('b');
    var time = timer.attr('data-second');
    label.show().text('0:' + time + ' сек.');
    idTimer = setInterval(function () {
        label.text('0:' + time + ' сек.');
        if (--time == 0) {
            clearInterval(idTimer);
            label.hide();
            btn.show();
        }
    }, 1000);
    up_box.one('click', function () {
        clearInterval(idTimer);
    });
}


