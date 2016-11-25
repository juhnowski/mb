app = angular.module('app', ['layout'])


var callback_url = ""; // адрес реста для коллбеков из соцсетей
var vk = "";
var ok = "";
var fb = "";
var ml = "";


function getUserName() {
    $.ajax({
        url: "/main/rest/usersettings/getUserName",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.success) {
                $('.user-info').text(ans.data);
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}

function getPeopleMain() {
    $.ajax({
        url: "/main/rest/usersettings/getPeopleMain",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                listToOptions('marriageTypes', ans.data.marriageTypes);
                listToOptions('educationTypes', ans.data.educationTypes);
                if (ans.data.main.personal) {
                    var per = ans.data.main.personal;
                    $('#surname').val(per.surname);
                    $('#name').val(per.name);
                    $('#midname').val(per.midname);
                    $('#birthdate').val(per.birthdate);
                    $('#birthplace').val(per.birthplace);
                    $('#gender').val(per.gender);
                    $('#children').val(per.children);
                    $('#marriage').select2("val", per.marriageId);

                }
                if (ans.data.main.passport) {
                    var pas = ans.data.main.passport;
                    $('#series').val(pas.series);
                    $('#number').val(pas.number);
                    $('#docDate').val(pas.docDate);
                    $('#docOrg').val(pas.docOrg);
                    $('#docOrgCode').val(pas.docOrgCode);
                }
                if (ans.data.main.employment) {
                    var emp = ans.data.main.employment;
                    $('#educationId').select2("val", emp.educationId);

                }
                if (ans.data.main.contact) {
                    $('#phone').val(ans.data.main.contact.phone);
                }


                initCanAdd();

            } else {
                console.log("false " + ans.errorMessage);
            }
        }
    })
    ;
}

function saveAnketa() {
    preloader.show();
    $.ajax({
        url: "/main/rest/usersettings/saveAnketa",
        data: JSON.stringify({
            marriageId: $('#marriage').select2('val'),
            children: $('#children').val(),
            educationId: $('#educationId').select2('val'),
            phoneCode: $('#phoneCode').val(),
            phoneHash: getCookie("phone")
        }),
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (answer) {
            preloader.hide();
            console.log(answer);

            valid_vertif($('#marriage'), "", true);
            valid_vertif($('#children'), "", true);
            valid_vertif($('#educationId'), "", true);
            valid_vertif($('#phoneCode'), "", true);

            if (answer.success) {
                if (answer.data.length > 0) {
                    for (var i = 0; i < answer.data.length; i++) {
                        valid_vertif($('#' + answer.data[i].field), answer.data[i].text, false);
                    }
                    scroll_not_valid();
                    return;
                }

                self.location = "settings_doc.shtml";
            } else {
                alert(answer.errorMessage);
            }
        }
    });
}


function accountView(account) {
    if (account.codeType == 1) {
        return rowsAddr('Вид счета', 'Счет в банке') +
            rowsAddr('Номер счета', account.accountnumber) +
            rowsAddr('Банк', account.bik);
    }
    if (account.codeType == 2) {
        return rowsAddr('Вид счета', 'Банковская карта') +
            rowsAddr('Номер карты', account.cardNumberMasked);
    }
    if (account.codeType == 3) {
        return rowsAddr('Вид счета', 'Система Контакт');
    }
    if (account.codeType == 4) {
        return rowsAddr('Вид счета', 'Яндекс.деньги') +
            rowsAddr('№ кошелька', account.accountnumber);
    }
    if (account.codeType == 5) {
        return rowsAddr('Вид счета', 'Qiwi кошелёк') +
            rowsAddr('№ кошелька', account.accountnumber);
    }
}
function addressFormat(name, addr) {
    var res = rowsAddr(name, '');
    if (addr.regionName) {
        res = res + rowsAddr('Регион', addr.regionName);
    }
    if (addr.areaName) {
        res = res + rowsAddr('Район', addr.areaName);
    }
    if (addr.cityName) {
        res = res + rowsAddr('Город', addr.cityName);
    }
    if (addr.placeName) {
        res = res + rowsAddr('Населенный пункт', addr.placeName);
    }
    if (addr.streetName) {
        res = res + rowsAddr('Улица', addr.streetName);
    }
    if (addr.house) {
        res = res + rowsAddr('Дом', addr.house);
    }
    if (addr.flat) {
        res = res + rowsAddr('Квартира', addr.flat);
    }
    return res;
}
function rowsAddr(header, value) {
    return '<tr>' +
        '           <td>' +
        tupleData(header, value)
    '           </td>' +
    '       </tr>';
}
function tupleData(header, value) {
    return '<div class="gray">' + header + '</div>' +
        '<div>' + value + '</div>';
}
var dataPage = [];
var templateLoadscan = '<p class="start-load"><label>Перетащите файл сюда <span class="group"><span class="name"></span> <span class="btn-load">Загрузить</span> <input type="file"></span></label></p>' +
    '<p class="end-load"><label>Файл успешно загружен! <span class="delete"></span></label></p>';

function menuMedia(id){
    var template = '<a class="btn" href="/main/rest/usersettings/downloadDocMedia?id='+id+'">Скачать</a>' +
        '<a class="btn" href="#" onclick="if(confirm(\'Вы действительно хотите удалить файл?\')){removeMedia('+id+')}">Удалить</a>';
    return template;
}
var fileStorage = {};
function initloadFile(){
    // load-file
    $('.load-file [type=file]').change(function(evt) {
        var $input = $(this),
            $holder = $input.closest('.load-file'),
            id = $holder.attr('id');

        var files = this.files;

        if (files.length) {
            var file = files[0];

            var formData = new FormData();
            formData.append('file', file, file.name);

            $.ajax({
                url: '/main/rest/firstrequest/upload',
                data: formData,
                processData: false,
                contentType: false,
                type: 'POST',
                success: function(data){
                    if (data) {
                        fileStorage[id] = data;
                    }
                    $holder.addClass('done');
                }
            });
        }
    });

    $('.load-file input').on('change',function(){
        $(this).siblings('.name').text($(this).val());
    });
    $('.load-file .delete').on('click',function(){
        var $holder = $(this).closest('.load-file'),
            $input = $holder.find('[type=file]');
        $input.replaceWith($input=$input.clone(true)); // clear file input
        $holder.find('.name').text('');
        fileStorage[$holder.attr('id')] = undefined;
        $holder.removeClass('done');
    });
}
function getPeopleContact() {
    getSocialConfig();
    $.ajax({
        url: "/main/rest/usersettings/getPeopleContact",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            console.log(ans);
            if (ans.success) {
                dataPage['phone'] = ans.data.phone;
                dataPage['email'] = ans.data.email;

                $('#phone').val(ans.data.phone).keyup().mask('+7 (999) 999 99 99');
                $('#email').val(ans.data.email).keyup();
                $('#inn').val(ans.data.inn);
                $('#snils').val(ans.data.snils);
                if (ans.data.car)
                    $('#car').prop('checked', true);
                $('#car').parents('i.checkbox').addClass('active');
                if (ans.data.hasDriverDoc)
                    $('#hasDriverDoc').prop('checked', true);
                $('#hasDriverDoc').parents('i.checkbox').addClass('active');

                $('#passportDocFile').empty();
                if (ans.data.passportDoc) {
                    $(menuMedia(ans.data.passportDoc)).appendTo('#passportDocFile');
                }else{
                    $(templateLoadscan).appendTo('#passportDocFile');
                }
                $('#innDocFile').empty();
                if (ans.data.innDoc) {
                    $(menuMedia(ans.data.innDoc)).appendTo('#innDocFile');
                }else{
                    $(templateLoadscan).appendTo('#innDocFile');
                }
                $('#snilsFile').empty();
                if (ans.data.snilsDoc) {
                    $(menuMedia(ans.data.snilsDoc)).appendTo('#snilsFile');
                }else{
                    $(templateLoadscan).appendTo('#snilsFile');
                }
                $('#autoCardFile').empty();
                if (ans.data.driveDoc) {
                    $(menuMedia(ans.data.driveDoc)).appendTo('#autoCardFile');
                }else{
                    $(templateLoadscan).appendTo('#autoCardFile');
                }
                initloadFile();


                if (ans.data.fb) {
                    $('#fb2').html('' +
                        '<a href="#" class="fb connect" rel="nofollow" onclick="if(confirm(\'Не будете пользоваться этим контактом?\')){makeArchive(' + ans.data.fbId + ')}return false;"></a>');
                    $('#fb2').addClass('active');
                } else {
                    $('#fb2').html('<a href="#" class="fb" rel="nofollow"></a>');
                    $('#fb2').removeClass('active');
                }
                if (ans.data.mm) {
                    $('#mm2').html('' +
                        '<a href="#" class="mm connect" rel="nofollow" onclick="if(confirm(\'Не будете пользоваться этим контактом?\')){makeArchive(' + ans.data.mmId + ')}return false;"></a>');
                    $('#mm2').addClass('active');
                } else {
                    $('#mm2').html('<a href="#" class="mm" rel="nofollow"></a>');
                    $('#mm2').removeClass('active');
                }
                if (ans.data.odk) {
                    $('#ok2').html('' +
                        '<a href="#" class="ok connect" rel="nofollow" onclick="if(confirm(\'Не будете пользоваться этим контактом?\')){makeArchive(' + ans.data.odkId + ')}return false;"></a>');
                    $('#ok2').addClass('active');
                } else {
                    $('#ok2').html('<a href="#" class="ok" rel="nofollow"></a>');
                    $('#ok2').removeClass('active');
                }
                if (ans.data.vk) {
                    $('#vk2').html('' +
                        '<a href="#" class="vk connect" rel="nofollow" onclick="if(confirm(\'Не будете пользоваться этим контактом?\')){makeArchive(' + ans.data.vkId + ')}return false;"></a>');
                    $('#vk2').addClass('active');
                } else {
                    $('#vk2').html('<a href="#" class="vk" rel="nofollow"></a>');
                    $('#vk2').removeClass('active');

                }


                $('.social-connect a').on('click', function () {
                    var type = $(this).attr("class");
                    if (type == "ok") {
                        console.log("OK send auth")
                        window.open("http://www.odnoklassniki.ru/oauth/authorize?client_id=" + ok + "&response_type=code&redirect_uri=" + callback_url + "ok", "OAuth", "width=750,height=370");
                    }
                    if (type == "vk") {
                        console.log("VK send auth")
                        window.open("https://oauth.vk.com/authorize?client_id=" + vk + "&scope=email,offline,notify&redirect_uri=" + callback_url + "vk&display=popup&v=5.21&response_type=token", "OAuth", "width=750,height=370");
                    }
                    if (type == "fb") {
                        console.log("FB send auth")
                        window.open("https://graph.facebook.com/oauth/authorize?client_id=" + fb + "&redirect_uri=" + callback_url + "fb&display=popup", "OAuth", "width=750,height=370");
                    }
                    if (type == "mm") {
                        console.log("ML send auth")
                        window.open("https://connect.mail.ru/oauth/authorize?client_id=" + ml + "&redirect_uri=" + callback_url + "ml&response_type=token&display=popup", "OAuth", "width=750,height=370");
                    }
                });

                initCanAdd();
            } else {
                console.log("false " + ans.errorMessage);
            }
        }
    });

}



function removeMedia(id) {
    $.ajax({
        url: "/main/rest/usersettings/removeDocMedia",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({id: id}),
        success: function (ans) {

            getPeopleContact();
        }
    });
}

function makeArchive(id) {
    $.ajax({
        url: "/main/rest/usersettings/makeArchive",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({id: id}),
        success: function (ans) {

            getPeopleContact();
        }
    });
}

function getPeopleDocument() {
    delCookie("phoneHash");
    $.ajax({
        url: "/main/rest/usersettings/getPassport",
        type: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            console.log(ans);
            if (ans.success) {
                dataPage['series'] = ans.data.series;
                dataPage['number'] = ans.data.number;
                dataPage['docDate'] = ans.data.docDate;
                dataPage['docOrgCode'] = ans.data.docOrgCode;
                dataPage['docOrg'] = ans.data.docOrg;

                $('#series').val(ans.data.series);
                $('#number').val(ans.data.number);
                $('#docDate').val(ans.data.docDate);
                $('#docOrg').val(ans.data.docOrg);
                $('#docOrgCode').val(ans.data.docOrgCode).keyup().mask('999-999');

                initCanAdd();
            } else {
                alert(ans.errorMessage);
            }
        }
    });
}

function getSocialConfig() {
    $.ajax({
        url: "/main/rest/usersettings/socialConfig",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            console.log(ans);
            if (ans.success) {
                vk = ans.data.vk;
                ok = ans.data.odk;
                fb = ans.data.fb;
                ml = ans.data.mm;
                callback_url = ans.data.callBackUrl
            } else {
                console.log("false " + ans.errorMessage);
            }
        }
    });
}

function showSmsBtn() {
    if (validDataPage()) {
        $('#btn_sms_ver').hide();
        $('#phoneCode').attr('disabled', true);
    } else {
        var sms = $('.sms:visible');
        if (sms.length < 1) {
            $('#btn_sms_ver').show();
            $('#phoneCode').attr('disabled', false);
        }
    }
}

function validDataPage() {
    for (i in dataPage) {
        if ($('#' + i).attr('data-type') != undefined && $('#' + i).attr('data-type') == 'mask') {
            if (validPhone(i) == 0) {
                return 0;
            }
        } else {
            var inp = $('#' + i);
            var newValue = $(inp).val();
            if ($(inp).attr('type') != undefined && $(inp).attr('type') == 'checkbox') {
                newValue = $(inp).is(':checked')
            }
            if (newValue != undefined) {
                var oldValue = dataPage[i];
                if (oldValue == null) {
                    oldValue = ''
                }
                if (newValue != oldValue) {
                    return 0;
                }
            }
        }
    }
    return 1;
}

function checkBoxToString(string) {
    if (string instanceof Boolean) {
        if (string) {
            return "on";
        } else {
            return "off";
        }
    }
}

function validPhone(fname) {
    var oldPhone = dataPage[fname];
    var phone = $('#' + fname).val().replace(/\D+/g, "");
    if (oldPhone == '' || oldPhone != phone) {
        return 0;
    }
    return 1;
}

function getBonusInfoPromise() {
    return $.getJSON('/main/rest/bonus/info');
}

$(document).ready(function () {
    $('.button.save').on('click', function () {

        var not_valid = false;

        $(this).parents('.content_text').find('input.required').not('.required_not').each(function () {
            if ($(this).hasClass('condition')) {
                if (!$(this).prop('checked')) {
                    not_valid = true;
                    $(this).parents('label').addClass('required').children('u').text('Вы должны принять правила, чтобы продолжить.');
                }
                else
                    $(this).parents('label').removeClass('required').children('u').text('');
            }
            else if (valid($(this)))
                not_valid = true;
        });

        if (not_valid) {
            scroll_not_valid();
            return false;
        }

        if ($(this).hasClass('disabled'))
            return false;
        //$(this).addClass('disabled');

        submitUserContact();
    });
    $('.button.save2').on('click', function () {

        var not_valid = false;
        $(this).parents('.content_text').find('input.required').not('.required_not').each(function () {
            if (valid($(this)))
                not_valid = true;
        });

        if (not_valid) {
            scroll_not_valid();
            return false;
        }

        if ($(this).hasClass('disabled'))
            return false;
        //$(this).addClass('disabled');

        submitUserDocument();
    });

    $('.button.save3').on('click', function () {

        var not_valid = false;
        $(this).parents('.content_text').find('input.required').not('.required_not').each(function () {
            if (valid($(this)))
                not_valid = true;
        });

        if (not_valid) {
            scroll_not_valid();
            return false;
        }

        if ($(this).hasClass('disabled'))
            return false;
        //$(this).addClass('disabled');

        submitUserEmployment();
    });
    initBtnsave()
    //rus text
    $('.rus-text').on('keypress', function (e) {
        var k = e.which;
        if (k > 33 && k < 128) {
            valid_vertif($(this), 'Допустимы только русские символы', false);
            return false;
        } else {
            valid_vertif($(this), '', true);
        }
    });
    //место рождения
    $('.rus-message').on('keypress', function (e) {
        var k = e.which;
        if (k > 33 && k < 128 && (k != 46 && k != 44)) {
            valid_vertif($(this), 'Допустимы только русские символы', false);
            return false;
        } else {
            valid_vertif($(this), '', true);
        }
    });
    $('.edit-setting').on('click', function () {

        $(this).addClass('send_vertif');
        initBtnsave();
    });




    $('#typeWorkId').on('change', function() {
        var select = $(this),
            id = select.val(),
            option = select.find('[value='+id+']').text();


        if (option === 'пенсионер' || option === 'льготный пенсионер' || option === 'неработающий' || option === 'студент/учащийся') {
            $('#professionId').closest('.row').hide();
            $('#professionId').val('');
            // сбрасываем селект
            $('#professionId').val('').trigger('change');

            $('#placeWork').closest('.row').hide();
            $('#placeWork').val('');
            $('#professionTypeId').closest('.row').hide();
            $('#professionTypeId').val('');
            // сбрасываем селект
            $('#professionTypeId').val('').trigger('change');

            $('#occupation').closest('.row').hide();
            $('#occupation').val('');

            $('#workPlaceFias').closest('.right-block').hide();
            $('#selectEdgeID').val('');
            $('#home').val('');
            $('#builder').val('');
            $('#korpus').val('');
            $('#apartment').val('');

            $('#dateStartWork').closest('.row').hide();
            // так же скрываем ошибку
            $('#dateStartWork').closest('.row').next('.error').hide();

            $('#dateStartWork').val('');
            $('#workPhone').closest('.row').hide();
            $('#workPhone').val('');
        } else {
            $('#professionId').closest('.row').show();
            $('#placeWork').closest('.row').show();
            $('#professionTypeId').closest('.row').show();
            $('#occupation').closest('.row').show();
            $('#workPlaceFias').closest('.right-block').show();
            $('#dateStartWork').closest('.row').show();
            $('#workPhone').closest('.row').show();
        }
    });


    $('#salary').on('change', function() {
        if ($(this).val() != null && $(this).val() != '' && $(this).val() > 0) {
            $(this).closest('.row').removeClass('red');
            tooltipError($(this).attr('data-tooltip'), '', false);
        } else {
            tooltipError($(this).attr('data-tooltip'), 'Значение должно быть больше нуля.', true);
            $(this).closest('.row').addClass('red');
        }
    });


});

function initBtnsave(){
    $('.btn.send_vertif').on('click', function () {
        var b = $(this);
        var l = $('.lk-send-sms_up').find('.input');
        var v = $('#phone');
        sendConfirm(v, l, $(this));
        $('#smsNumber').text($('#phone').val());
    });
}
function sendConfirm(val, l, obj) {

    if (val.val() != '') {
        l.show();
        val.parent().removeClass('required');
        smsRepeat(l.find("a").first());
        $.ajax({
                url: '/main/rest/usersettings/sendSmsCode',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify({
                    phone: $('#phone').val(),
                }),
                success: function (msg) {
                    valid_vertif(val, '', true);
                    setCookie("phone", msg.data);
                }
                ,
                error: function () {
                    alert_top($('.alert_up.messege'), 'Сообщение!', 'Ошибка выполнения.');
                    valid_vertif(val, 'Ошибка выполнения');
                }
            }
        )
        ;
    }
    else
        val.parent().addClass('required');
}

function smsRepeat(obj) {
    var t = 59;
    var p = $(obj).parent();
    p.html('Выслать код повторно через 0:' + t);
    var timer = setInterval(function () {
        t--;
        if (t > 0) {
            p.html('Выслать код повторно через 0:' + t);
        } else {
            p.html('<a onclick="$(\'#btn_sms_ver\').click().next().children(\'input\').val(\'\').focus();return false;">Отправить код еще раз.</a>');
            clearInterval(timer);
        }
    }, 1000);
}

function submitUserContact() {
    preloader.show();
    $.ajax({
        url: "/main/rest/usersettings/savePeopleContact",
        data: JSON.stringify({
            email: $('#email').val(),
            phone: $('#phone').val(),
            phoneCode: $('#phoneCode').val(),
            emailHash: getCookie("email"),
            phoneHash: getCookie("phone"),
            inn: $('#inn').val(),
            snils: $('#snils').val(),
            car: $('#car').is(':checked'),
            passportDocFile: fileStorage.passportDocFile,
            innDocFile: fileStorage.innDocFile,
            snilsFile: fileStorage.snilsFile,
            autoCardFile: fileStorage.autoCardFile
        }),
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (answer) {
            preloader.hide();
            console.log(answer);

            valid_vertif($('#email'), "", true);
            valid_vertif($('#phone'), "", true);
            valid_vertif($('#phoneCode'), "", true);

            if (answer.success) {
                if (answer.data.length > 0) {
                    for (var i = 0; i < answer.data.length; i++) {
                        valid_vertif($('#' + answer.data[i].field), answer.data[i].text, false);
                    }
                    scroll_not_valid();
                    return;
                }

                self.location = "settings-4.shtml";
            } else {
                alert(answer.errorMessage);
            }
        }
    });
}

function submitUserDocument() {
    preloader.show();
    $.ajax({
        url: "/main/rest/usersettings/savePeopleDocument",
        data: JSON.stringify({
            series: $('#series').val(),
            number: $('#number').val(),
            phoneCode: $('#phoneCode').val(),
            docDate: $('#docDate').val(),
            docOrgCode: $('#docOrgCode').val(),
            docOrg: $('#docOrg').val(),
            phoneHash: getCookie("phone")
        }),
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (answer) {
            preloader.hide();
            console.log(answer);

            valid_vertif($('#series'), "", true);
            valid_vertif($('#number'), "", true);
            valid_vertif($('#docDate'), "", true);
            valid_vertif($('#docOrgCode'), "", true);
            valid_vertif($('#docOrg'), "", true);
            valid_vertif($('#phoneCode'), "", true);

            if (answer.success) {
                if (answer.data.length > 0) {
                    for (var i = 0; i < answer.data.length; i++) {
                        valid_vertif($('#' + answer.data[i].field), answer.data[i].text, false);
                    }
                    scroll_not_valid();
                    return;
                }

                self.location = "settings.shtml";
            } else {
                alert(answer.errorMessage);
            }
        }
    });
}
function getPeopleEmployment() {

    $.ajax({
        url: "/main/rest/usersettings/getPeopleEmployment",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            console.log(ans);
            if (ans.success) {
                listToOptions('employTypes', ans.data.employTypes);
                listToOptions('organizationTypes', ans.data.organizationTypes);
                listToOptions('professionTypes', ans.data.professionTypes);

                if (ans.data.workPlaceFias != undefined) {
                    $('#workPlaceFias').address({value: ans.data.workPlaceFias.split(';')});
                }else{
                    $('#workPlaceFias').address({});
                }

                var ids = '';
                $('#home').val(ans.data.home);
                ids = ids + ('#home, ');
                $('#builder').val(ans.data.builder);
                ids = ids + ('#builder, ');
                $('#korpus').val(ans.data.korpus);
                ids = ids + ('#korpus, ');
                $('#apartment').val(ans.data.apartment);
                ids = ids + ('#apartment, ');
                for (i in ans.data.main) {
                    dataPage[i] = ans.data.main[i];
                    var inp = $('#' + i);
                    if ($(inp).attr('type') != undefined && $(inp).attr('type') == 'checkbox') {
                        if (ans.data.main[i]) {
                            $(inp).prop('checked', true);
                            $(inp).parents('i:first').addClass('active');
                        } else {
                            $(inp).prop('checked', false);
                        }
                    } else {
                        inp.val(ans.data.main[i] == null ? '' : ans.data.main[i]);
                    }
                    ids = ids + ('#' + i + ', ');
                }

                $('#typeWorkId').select2("val", ans.data.main.typeWorkId);
                $('#professionId').select2("val", ans.data.main.professionId);
                $('#professionTypeId').select2("val", ans.data.main.professionTypeId);

                $('#phone').val(ans.data.phone);
				
				initCanAdd();
            } else {
                alert(ans.errorMessage);
            }
        }
    });
}

function listToOptions(id, options) {
    var sel = $('[data-select="' + id + '"]');
    $(sel).append('<option value=""></option>');
    $.each(options, function () {
        $(sel).append('<option value="' + this.id + '">' + this.value + '</option>');
    })
}
function submitUserEmployment() {
    preloader.show();
    $.ajax({
        url: "/main/rest/usersettings/savePeopleEmployment",
        data: JSON.stringify({
            typeWorkId: $('#typeWorkId').select2('val'),
            professionId: $('#professionId').select2('val'),
            placeWork: $('#placeWork').val(),
            professionTypeId: $('#professionTypeId').select2('val'),
            occupation: $('#occupation').val(),
            dateStartWork: $('#dateStartWork').val(),
            salary: $('#salary').val(),
            workPhone: $('#workPhone').val(),
            salaryDate: $('#salaryDate').val(),
            home: $('#home').val(),
            builder: $('#builder').val(),
            korpus: $('#korpus').val(),
            apartment: $('#apartment'). val(),
            emailHash: getCookie("email"),
            phoneHash: getCookie("phone"),
            phoneCode: $('#phoneCode').val(),
            workPlaceFias: $('#workPlaceFias').data('address') ? $('#workPlaceFias').data('address').val() : null
        }),
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (answer) {
            preloader.hide();
            //console.log(answer);

            valid_vertif($('#typeWorkId'), "", true);
            valid_vertif($('#professionId'), "", true);
            valid_vertif($('#placeWork'), "", true);
            valid_vertif($('#professionTypeId'), "", true);
            valid_vertif($('#occupation'), "", true);
            valid_vertif($('#dateStartWork'), "", true);
			valid_vertif($('#salary'), "", true);
            valid_vertif($('#workPhone'), "", true);
            valid_vertif($('#salaryDate'), "", true);
            valid_vertif($('#home'), "", true);
            valid_vertif($('#builder'), "", true);
            valid_vertif($('#apartment'), "", true);
            valid_vertif($('#phoneCode'), "", true);

            if (answer.success) {
                if (answer.data.length > 0) {
                    for (var i = 0; i < answer.data.length; i++) {
                        valid_vertif($('#' + answer.data[i].field), answer.data[i].text, false);
                    }

                    return;
                }

                self.location = "settings_job.shtml";
            } else {
                alert(answer.errorMessage);
            }
        }
    });
}
function getMySum() {
    $.ajax({
        url: "/main/rest/listsums/mySum",
        type: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                if (ans.data != null) {

                    $('#moneyBox').text(ans.data + ' руб.')

                }
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
getMySum();

getBonusInfoPromise().success(function (resp) {
    var info = resp.data;
    var btn = $('#bonusBtn');
    if (btn.length) {
        console.log('bonus = ' + info.bonusAmount)
        btn.text((info.bonusAmount || 0).toString().replace(/(\d)(?=(\d{3})+(?:\.\d+)?$)/g, "$1 "));
        btn.show();
    }
});

getUserName();

function valid_vertif(input, text, valid) {
    var errBlock = input.closest('.row').nextAll('.errorMessage:first');
    if (!valid) {
            $(errBlock).text(text);
            $(errBlock).show();
    } else {
        $(errBlock).hide();
    }

}

// возвращает cookie с именем name, если есть, если нет, то undefined
function getCookie(name) {
    //console.log("<== get storage "+name);
    return $.jStorage.get(name);
}

function setCookie(name, value, ttl) {
    //console.log("==> set storage "+name+"="+value);
    $.jStorage.set(name, value);
    if (ttl != null && ttl != undefined) {
        $.jStorage.setTTL(name, ttl);
    }
}

function validateEmail(email) {
    var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    return re.test(email);
}