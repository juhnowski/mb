function mainPage() {
    $.ajax({
        url: "rest/cms/getMain",
        type: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)

                initAbstrField(ans.data.main);
                $('.contectUp').find('.block-gallery, .dropzone').remove();

                if (ans.data.main.imgUp && findImg(ans.data.images, ans.data.main.imgUp)) {
                    $(insertImageView(findImg(ans.data.images, ans.data.main.imgUp), 'removeImageMain')).appendTo('.contectUp');
                    $('#imgUp').val(ans.data.main.imgUp)
                } else {
                    $(insertImgeUpload('uploadHeader')).appendTo('.contectUp');
                }
                $('.contentDown').find('.block-gallery, .dropzone').remove();
                if (ans.data.main.imgDown && findImg(ans.data.images, ans.data.main.imgDown)) {
                    $(insertImageView(findImg(ans.data.images, ans.data.main.imgDown), 'removeImageMain')).appendTo('.contentDown');
                    $('#imgDown').val(ans.data.main.imgDown)
                } else {
                    $(insertImgeUpload('uploadHeader')).appendTo('.contentDown');
                }

                initdropzone(function(files, t){
                    $(t.element).parents('.col-sm-10').find('[type="hidden"]').val(files.name);
                    saveMain()
                })

                var i = 0;
                $.each(ans.data.main.howThisWork, function (k, value) {
                    $('#work' + i).val(value['header']);
                    $('#workd' + i).val(value['body']);
                    $('#workdl' + i).val(value['link'] == undefined ? '' : value['link']);
                    i++;
                });
                i = 0;
                $.each(ans.data.main.yesOntime, function (k, value) {
                    $('#yes' + i).val(value['header']);
                    $('#yesd' + i).val(value['body']);
                    i++;
                });

            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}

function saveMain() {

    var works = [];
    var w = {
        header: $('#work0').val(),
        body: $('#workd0').val(),
        link: $('#workdl0').val()
    };
    works.push(w);
     w = {
        header: $('#work1').val(),
        body: $('#workd1').val(),
        link: $('#workdl1').val()
    };
    works.push(w);
     w = {
        header: $('#work2').val(),
        body: $('#workd2').val(),
        link: $('#workdl2').val()
    };
    works.push(w);
     w = {
        header: $('#work3').val(),
        body: $('#workd3').val(),
        link: $('#workdl3').val()
    };
    works.push(w);

    var yeses = [];
    var q = {
        header: $('#yes0').val(),
        body: $('#yesd0').val()
    };
    yeses.push(q);
    q = {
        header: $('#yes1').val(),
        body: $('#yesd1').val()
    };
    yeses.push(q);
    q = {
        header: $('#yes2').val(),
        body: $('#yesd2').val()
    };
    yeses.push(q);


    var res = {
        title: $('#title').val(),
        description: $('#description').val(),
        keywords: getKeywords(),
        imgUp: $('#imgUp').val(),
        imgDown: $('#imgDown').val(),
        howThisWork: works,
        yesOntime: yeses
    };

    $.ajax({
        url: "rest/cms/saveMain",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(res),
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)

                alert("Данные изменены");
                mainPage();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}

function getKeywords() {
    var $keywords = $("#keywords").siblings(".tagsinput").children(".tag");
    var tags = '';
    for (var i = $keywords.length; i--;) {
        tags += $($keywords[i]).text().substring(0, $($keywords[i]).text().length - 1).trim() + ",";
    }
    return tags;
}

function findImg(imgArr, name) {
    var result;
    $.each(imgArr, function () {
        if (this.fileName == name) {
            result = this;
        }
    });
    return result;
}
function removeImageMain(id, t) {
    removeImage(id);
    $(t).parents('.col-sm-10').find('[type="hidden"]').val('');
    saveMain();
}
function insertImageView(image, removefunction) {
    return '<div class="col-sm-12 block-gallery"><a class="fancybox" href="' + "data:image/jpg;base64," + image.data + '" ' +
        'title="' + image.fileName + '">' +
        '<img alt="image" src="' + "data:image/jpg;base64," + image.data + '"/></a>' +
        '<button type="button" class="btn btn-danger m-l" onclick="'+removefunction+'(' + this.id + ', this)">Удалить</button>' +
        '</div>'
}
function insertImgeUpload(rest) {
    return '<form class="dropzone" action="rest/cms/' + rest + '" >' +
        '<div class="dropzone-previews"></div>' +
        '<button type="submit" class="btn btn-primary pull-right">Сохранить</button>' +
        '</form>'
}
function initAbstrField(ans) {
    $('#title').val(ans.title);
    $('#description').val(ans.description);
    $('#keywords').val(ans.keywords);
    initKeywords();
}
function removeImage(id, callback) {
    $.ajax({
        url: "rest/cms/removeImage",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({id: id}),
        success: function (ans) {
            if (ans.success) {
                if (callback != undefined) {
                    callback();
                }
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}

function questionPage() {
    var pagesize = getUrlParameter('pagesize')
    pagesize = pagesize ? pagesize : 10;
    var selectPageSize = $('.pagesize option:selected').val();
    var pagesize = selectPageSize == undefined ? pagesize : selectPageSize;
    var page = getUrlParameter('page');
    var codeCat = getUrlParameter('codeCat');
    codeCat = codeCat ? codeCat : -1;
    var selectCat = $('#filterCategory option:selected').val();
    var codeCategory = selectCat == undefined ? codeCat : selectCat;
    var selectPagi = $('#paginator').find('.active').text();
    page = page ? page : selectPagi ? selectPagi : 1;
    $.ajax({
        url: "rest/cms/getQuestionPagi?page=" + page + "&pagesize=" + pagesize + "&codeCategory=" + codeCategory,
        type: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)

                $('#title').val(ans.data.main.title);
                $('#description').val(ans.data.main.description);
                $('#keywords').val(ans.data.main.keywords);
                initKeywords();
                $('#header').val(ans.data.main.header);

                //$('.galeryimg').empty();
                //if (ans.data.image.length > 0) {
                //    $('#my-awesome-dropzone').hide();
                //    var content = '';
                //    $.each(ans.data.image, function (k) {
                //        if (ans.data.main.fileName == this.fileName) {
                //
                //            content = content + '<a class="fancybox" href="' + "data:image/jpg;base64," + this.data + '" ' +
                //                'title="' + this.fileName + '">' +
                //                '<img alt="image" src="' + "data:image/jpg;base64," + this.data + '"/></a>' +
                //                '<button type="button" class="btn btn-danger m-l" onclick="removeImageQuestion(' + this.id + ')">Удалить</button>';
                //            $('.q-fileName').val(this.fileName);
                //        }
                //
                //    });
                //    $(content).appendTo('.galeryimg');
                //} else {
                //    $('#my-awesome-dropzone').show();
                //}

                $('#body_edit').empty();
                $('#filterCategory').empty();
                $('<option value="-1">Все</option>').appendTo('#filterCategory');
                $.each(ans.data.category, function (k) {
                    var bl = '<div class="form-inline category-block m-b">' +
                        '<div class="form-group m-l-n-sm m-r">' +
                        '<input value="' + this.name + '" class="q-name form-control" placeholder="Наименование" size="40">' +
                        '<input class="q-code" type=hidden value="' + this.code + '">' +
                        '</div> ' +

                        '<button class="btn btn-danger m-l" onclick="if(confirm(\'Внимание! Вместе с категорией будут удалены все связанные вопросы!!! Продолжить?\')){removeCategory(this, ' + this.code + ')}">Удалить</button>' +
                        '</div>';

                    $(bl).appendTo('#body_edit');


                    $('<option value="' + this.code + '">' + this.name + '</option>').appendTo('#filterCategory');
                });
                $('#filterCategory').val(codeCategory);
                $(' <div class="form-inline pull-right">' +
                    '<button onclick="insertCategory()" class="btn btn-default btn-sm m-l-n-sm ">Добавить</button>' +
                    '<button onclick="saveCategory()" class="btn btn-primary btn-sm m-l">Сохранить</button>' +
                    '</div>').appendTo('#body_edit');


                $('#newsTable tr.td').remove();
                $('#paginator').empty();
                $.each(ans.data.question, function (k) {
                    $('<tr class="td"><td><a onclick="window.location=\'newquestion.shtml?id=' + this.code + '\'">' + this.name + '</td>' +
                        '<td>' + findCategory(this.idCategory, ans.data.category) + '</td>' +
                        '<td>' +
                        '<input  class="btn btn-danger m-l" type="button" onclick="if(confirm(\'Удалить вопрос\')){removeQuestion(' + this.code + ')}" value="Удалить"></td></tr>').appendTo('#newsTable');
                });

                var pagi = "";
                if (page > 1) {
                    pagi += '<button onclick="clkPagiMath(-1)" ' +
                        'class="btn btn-white" type="button"><i class="fa fa-chevron-left"></i></button> ';
                }
                var n = 0
                while ((n * pagesize) < ans.data.total) {
                    var active = (n + 1) == page ? ' active ' : '';
                    pagi += '<button onclick="clkPagi(this)"' +
                        'class="btn btn-white ' + active + '">' + (n + 1) + '</button>';

                    n = n + 1;
                }
                if ((page * pagesize) < ans.data.total) {
                    pagi += '<button class="btn btn-white" onclick="clkPagiMath(+1)"' +
                        'type="button"><i class="fa fa-chevron-right"></i></button>';
                }


                $(pagi).appendTo('#paginator');

                //$('.input-sm').change(function () {
                //    window.location = 'question.shtml?page=1&pagesize=' + $(this).val() + '&=' + codeCategory;
                //});
                initCheckBox();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function removeCategory(t, code) {
    $(t).parent().remove();
}
function insertCategory() {
    var bl = '<div class="form-inline category-block m-b">' +
        '<div class="form-group m-l-n-sm m-r">' +
        '<input value="" class="form-control q-name" placeholder="Наименование" size="40">' +
        '<input type=hidden value="" class="q-code">' +
        '</div> ' +

        '<button class="btn btn-danger m-l" onclick="removeCategory(this)">Удалить</button>' +
        '</div>';
    if ($('#body_edit').find('.m-b:last').length > 0) {
        $('#body_edit').find('.m-b:last').after(bl);
    } else {
        $('#body_edit').find('.pull-right').before(bl);
    }


    initCheckBox();
}
function getQuestionById() {
    var id = getUrlParameter('id');
    var link = id ? '?id=' + id : '';
    $.ajax({
        url: "rest/cms/getQuestionById" + link,
        type: "POST",
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)

                if (ans.data.category) {
                    $('#idCategory').empty();
                    $.each(ans.data.category, function (k) {
                        var sel = k == 0 ? 'selected' : '';
                        $('<option value="' + this.code + '" ' + sel + '>' + this.name + '</option>').appendTo('#idCategory');
                    });
                }

                if (ans.data.main) {
                    var o = ans.data.main;
                    $('#name').val(o.name);
                    $('#idCategory').val(o.idCategory);
                    $('#answer').val(o.answer);
                    $('#isMain').prop('checked', o.isMain);
                    $('#isFooter').prop('checked', o.isFooter);
                    $('#code').val(o.code);
                }

                initCheckBox();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function saveOneQuestion() {
    if($('#idCategory option:selected').val()==undefined){
        alert("Заполните поле Раздел!");
        return;
    }

    var res = {
        name: $('#name').val(),
        code: $('#code').val(),
        answer: $('#answer').val(),
        idCategory: $('#idCategory option:selected').val(),
        isMain: $('#isMain').is(':checked'),
        isFooter: $('#isFooter').is(':checked')

    };

    $.ajax({
        url: "rest/cms/saveQuestionData",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(res),
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                window.location = 'question.shtml';
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function saveCategory() {

    var arr = [];
    $.each($('#body_edit').find('.category-block'), function () {
        var cat = {
            name: $(this).find('.q-name').val(),
            code: $(this).find('.q-code').val(),
            isFooter: $(this).find('.q-footer').is(':checked') ? true : false,
        }
        arr.push(cat);
    })


    $.ajax({
        url: "rest/cms/saveCategory",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(arr),
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                alert("Данные изменены");
                questionPage();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function removeQuestion(id) {
    var quest = [];
    $.ajax({
        url: "rest/cms/removeQuestion",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({id: id}),
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                alert("Данные изменены");
                questionPage();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}

function removeQuestion(id) {
    var quest = [];
    $.ajax({
        url: "rest/cms/removeQuestion",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({id: id}),
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                alert("Данные изменены");
                questionPage();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function saveQuestion() {

    var arr = [];

    var res = {
        title: $('#title').val(),
        description: $('#description').val(),
        keywords: getKeywords(),
        header: $('#header').val(),
    };

    $.ajax({
        url: "rest/cms/saveQuestion",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(res),
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                alert("Данные изменены");
                questionPage();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function findCategory(id, category) {
    var res = '';
    $.each(category, function (k) {
        if (this.code == id) {
            res = this.name;
        }
    });
    return res;
}
function newsPage() {
    var pagesize = getUrlParameter('pagesize')
    pagesize = pagesize ? pagesize : 10;
    $('.input-sm').val(pagesize);
    var page = getUrlParameter('page');
    page = page ? page : 1;
    $.ajax({
        url: "rest/cms/getNewsNoImage?page=" + page + "&pagesize=" + pagesize,
        type: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)

                $('#title').val(ans.data.news.title);
                $('#description').val(ans.data.news.description);
                $('#keywords').val(ans.data.news.keywords);
                initKeywords();
                $('#feedback').val(ans.data.news.feedback);
                $('#headerContext').val(ans.data.news.headerContext);

                $('.galeryimg').empty();
                if ((ans.data.image)&&(ans.data.image.length > 0)) {
                    $('#my-awesome-dropzone').hide();
                    var content = '';
                    $.each(ans.data.image, function (k) {
						
                        if (ans.data.news.fileName == this.fileName) {

                            content = content + '<a class="fancybox" href="' + "data:image/jpg;base64," + this.data + '" ' +
                                'title="' + this.fileName + '">' +
                                '<img alt="image" src="' + "data:image/jpg;base64," + this.data + '"/></a>' +
                                '<button type="button" class="btn btn-danger m-l" onclick="removeImageNews(' + this.id + ')">Удалить</button>';
                            $('.q-fileName').val(this.fileName);
                        }

                    });
                    $(content).appendTo('.galeryimg');
                } else {
                    $('#my-awesome-dropzone').show();
                }

                $('#newsTable tr.td').remove();
                $.each(ans.data.news.news, function (k) {
                    $('<tr class="td"><td><a onclick="window.location=\'onenews.shtml?id=' + this.id + '\'">' + this.name + '</td>' +
                        '<td><div class="checkbox m-l m-r-xs"><label> ' + this.date + '</label></div></td><td>' +
                        '<input  class="btn btn-danger m-l" type="button" onclick="if(confirm(\'Удалить новость\')){removeNews(' + this.id + ')}" value="Удалить"></td></tr>').appendTo('#newsTable');
                });

                var pagi = "";
                if (page > 1) {
                    pagi += '<button onclick="window.location=\'news.shtml?page=' + (parseInt(page) - 1) + '&pagesize=' + pagesize + '\'" ' +
                        'class="btn btn-white" type="button"><i class="fa fa-chevron-left"></i></button> ';
                }
                var n = 0
                while ((n * pagesize) < ans.data.total) {
                    var active = (n + 1) == page ? ' active ' : '';
                    pagi += '<button onclick="window.location=\'news.shtml?page=' + (n + 1) + '&pagesize=' + pagesize + '\'"' +
                        'class="btn btn-white ' + active + '">' + (n + 1) + '</button>';

                    n = n + 1;
                }
                if ((page * pagesize) < ans.data.total) {
                    pagi += '<button class="btn btn-white" onclick="window.location=\'news.shtml?page=' + (parseInt(page) + 1) + '&pagesize=' + pagesize + '\'"' +
                        'type="button"><i class="fa fa-chevron-right"></i></button>';
                }


                $(pagi).appendTo('#paginator');

                $('.input-sm').change(function () {
                    window.location = 'news.shtml?page=1&pagesize=' + $(this).val();
                });
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function saveNews(callback) {

    var res = {
        title: $('#title').val(),
        description: $('#description').val(),
        keywords: getKeywords(),
        headerContext: $('#headerContext').val(),
        feedback: $('#feedback').val(),
        fileName: $('.q-fileName').val(),
        fileNamePreview: $('.q-fileNamePreview').val(),

    };

    $.ajax({
        url: "rest/cms/saveNews",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(res),
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                alert("Данные изменены");
                newsPage();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function removeImageNews(id, t) {

    removeImage(id);
    $(t).parent().find('.q-fileName').val('');
    saveNews(function () {
        newsPage();
    });
}
function removeNews(id) {
    var quest = [];
    $.ajax({
        url: "rest/cms/removeNews",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({id: id}),
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                alert("Данные изменены");
                newsPage();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function saveOneNews() {
    var res = {
        title: $('#title').val(),
        description: $('#description').val(),
        keywords: getKeywords(),
        name: $('.q-name').val(),
        date: $('.q-date').val(),
        text: $('.q-text').code(),
        id: $('.q-id').val(),
        fileName: $('.q-fileName').val(),
        fileNamePreview: $('.q-fileNamePreview').val()

    };

    $.ajax({
        url: "rest/cms/saveOneNews",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(res),
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                alert("Данные изменены");
                window.location = 'onenews.shtml?id=' + ans.data;
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function removeImageOneNews(id, t) {
    $.ajax({
        url: "rest/cms/removeNewsImg",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({id: id}),
        success: function (ans) {
            if (ans.success) {
                saveOneNews();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
    $('.q-fileName').val('');
}
function removeImageOneNewsPreview(id, t) {
    $.ajax({
        url: "rest/cms/removeNewsImgPreview",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({id: id}),
        success: function (ans) {
            if (ans.success) {
                saveOneNews();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
    $('.q-fileNamePreview').val('');
}
function getNewsById() {
    var id = getUrlParameter('id');
    if (id) {
        $.ajax({
            url: "rest/cms/getNewsById?id=" + id,
            type: "POST",
            success: function (ans) {
                if (ans.success) {
                    console.log(ans.data)

                    $('#title').val(ans.data.news.title);
                    $('#description').val(ans.data.news.description);
                    $('#keywords').val(ans.data.news.keywords);
                    initKeywords();
                    $('.q-name').val(ans.data.news.name);
                    $('.q-date').val(ans.data.news.date).mask('99.99.9999');
                    $('.q-text').html(ans.data.news.text);
                    $('.q-id').val(ans.data.news.id);
                    $('.q-fileName').val(ans.data.news.fileName);
                    $('.q-fileNamePreview').val(ans.data.news.fileNamePreview);

                    $('.galeryimg').empty();
                    if (ans.data.image.length > 0) {
                        $('#my-awesome-dropzone').hide();
                        $.each(ans.data.image, function () {
                            var content = '';
                            var imgByte = "data:image/jpg;base64," + this.data;
                            if (ans.data.news.fileName == this.fileName) {
                                content = content + '<a class="fancybox" href="' + "data:image/jpg;base64," + this.data + '" ' +
                                    'title="' + this.fileName + '">' +
                                    '<img alt="image" src="' + imgByte + '"/></a>' +
                                    '<button type="button" class="btn btn-danger" onclick="removeImageOneNews(' + ans.data.news.id + ')">Удалить</button>';
                                $('.q-fileName').val(this.fileName);

                                if (!ans.data.news.fileNamePreview) {
                                    if (ans.data.news.fileName == this.fileName) {
                                        $('<div class="component">' +
                                            '<div class="overlay"><div class="overlay-inner"></div></div>' +
                                            '<img class="resize-image" src="' + imgByte + '" alt="image for resizing">' +
                                            '<button class="btn-crop js-crop">Выбрать<img class="icon-crop" src="img/crop.svg"></button></div>' +
                                            '').appendTo('.img-preview');
                                    }
                                }
                            }
                            if (ans.data.news.fileNamePreview == this.fileName) {
                                $('<a class="fancybox" href="' + "data:image/jpg;base64," + this.data + '" ' +
                                    'title="' + this.fileName + '">' +
                                    '<img alt="image" src="' + imgByte + '"/></a>' +
                                    '<button type="button" class="btn btn-danger" onclick="removeImageOneNewsPreview(' + ans.data.news.id + ')">Удалить</button>' +
                                    '').appendTo('.img-preview');
                            }
                            $(content).appendTo('.galeryimg');
                        });
                        $('.preview-block').removeClass('hide');
                        //resizeableImage($('.resize-image'));
                         $('.resize-image').one("load",function() {
                            resizeableImage($(this));
                        });
                    } else {
                        $('#my-awesome-dropzone').show();
                    }
                } else {
                    alert("false " + ans.errorMessage);
                }
            }
        });
    } else {
        $('.q-date').mask('99.99.9999');
        initKeywords();
    }

}
function politicsPage() {
    $.ajax({
        url: "rest/cms/getPoliticsNoImg",
        type: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)

                $('#title').val(ans.data.main.title);
                $('#description').val(ans.data.main.description);
                $('#keywords').val(ans.data.main.keywords);
                initKeywords();
                $('#headerText').val(ans.data.main.headerText);

                $('.galeryimg').empty();
                if (ans.data.image) {
                    $('#my-awesome-dropzone').hide();
                    var content = '';
                    $.each(ans.data.image, function (k) {
                        if (ans.data.main.fileName == this.fileName) {

                            content = content + '<a class="fancybox" href="' + "data:image/jpg;base64," + this.data + '" ' +
                                'title="' + this.fileName + '">' +
                                '<img alt="image" src="' + "data:image/jpg;base64," + this.data + '"/></a>' +
                                '<button type="button" class="btn btn-danger m-l" onclick="removeImagePolitics(' + this.id + ')">Удалить</button>';
                            $('.q-fileName').val(this.fileName);
                        }

                    });
                    $(content).appendTo('.galeryimg');
                } else {
                    $('#my-awesome-dropzone').show();
                }

                $('#points tr.td').remove();
                $.each(ans.data.main.dataList, function (k) {
                    $('<tr class="td"><td><a onclick="window.location=\'politicsData.shtml?id=' + this.id + '\'">' + this.header + '</a></td><td>' +
                        '<input type="button" class="btn btn-danger m-l" onclick="if(confirm(\'Удалить?\')){removePolitics(' + this.id + ')}" value="Удалить"></td></tr>').appendTo('#points');
                });

            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function savePolitics() {

    var res = {
        title: $('#title').val(),
        description: $('#description').val(),
        keywords: getKeywords(),
        headerText: $('#headerText').val(),
    };

    $.ajax({
        url: "rest/cms/savePolitics",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(res),
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                alert("Данные изменены");
                politicsPage();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function removePolitics(id) {
    var quest = [];
    $.ajax({
        url: "rest/cms/removePolitics",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify({id: id}),
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                alert("Данные изменены");
                politicsPage();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function getPoliticsById() {
    var id = getUrlParameter('id');
    if (id) {
        $.ajax({
            url: "rest/cms/getPoliticsById?id=" + id,
            type: "POST",
            success: function (ans) {
                if (ans.success) {
                    console.log(ans.data)

                    $('#header').val(ans.data.main.header);
                    $('#distr').html(ans.data.main.distr);
                    $('#id').val(ans.data.main.id);
                    $('#pp').val(ans.data.main.orderList==null? '' : ans.data.main.orderList);
                    $('.q-fileName').text(ans.data.main.fileName);

                    $('.galeryimg').empty();
                    if (ans.data.image) {
                        $('#my-awesome-dropzone').hide();
                        var content = '';
                        content = content + '<button type="button" class="btn btn-danger" onclick="removeImagePoliticsPoint(' + ans.data.image.id + ')">Удалить</button>';
                        $(content).appendTo('.galeryimg');
                    } else {
                        $('#my-awesome-dropzone').show();
                    }
                } else {
                    alert("false " + ans.errorMessage);
                }
            }
        });
    }
}
function removeImagePoliticsPoint(id) {
    removeImage(id);
    $('.q-fileName').text('');
    savePoliticsPoint();
}
function savePoliticsPoint() {
    var res = {
        header: $('#header').val(),
        distr: $('#distr').code(),
        id: $('#id').val(),
        fileName: $('.q-fileName').text(),
        orderList: $('#pp').val()
    };

    $.ajax({
        url: "rest/cms/savePoliticsPoint",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(res),
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                alert("Данные изменены");
                window.location = 'politicsData.shtml?id=' + ans.data;
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
function contactsPage() {
    $.ajax({
        url: "rest/cms/getContacts",
        type: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)

                $('#title').val(ans.data.title);
                $('#description').val(ans.data.description);
                $('#keywords').val(ans.data.keywords);
                initKeywords();
                $('#address').val(ans.data.address);
                $('#phone').val(ans.data.phone);
                $('#email').val(ans.data.email);
                $('#vk').val(ans.data.vk);
                $('#tw').val(ans.data.tw);
                $('#fb').val(ans.data.fb);
                $('#ok').val(ans.data.ok);
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}


function aboutPage() {
    $.ajax({
        url: "rest/cms/getAbout",
        type: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)

                $('#title').val(ans.data.about.title);
                $('#description').val(ans.data.about.description);
                $('#keywords').val(ans.data.about.keywords);
                initKeywords();
                $('#company').html(ans.data.about.company);
                $('#simply').html(ans.data.about.simply);
                $('#quickly').html(ans.data.about.quickly);
                $('#responsibility').html(ans.data.about.responsibility);

            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}



function saveContacts() {

    var res = {
        title: $('#title').val(),
        description: $('#description').val(),
        keywords: getKeywords(),
        address: $('#address').val(),
        phone: $('#phone').val(),
        email: $('#email').val(),
        vk: $('#vk').val(),
        tw: $('#tw').val(),
        fb: $('#fb').val(),
        ok: $('#ok').val(),
    };

    $.ajax({
        url: "rest/cms/saveContacts",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(res),
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                alert("Данные изменены");
                contactsPage();
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}

function saveAbout(callback) {

    var res = {
        title: $('#title').val(),
        description: $('#description').val(),
        keywords: getKeywords(),
        company: $('#company').code(),
        simply: $('#simply').code(),
        quickly: $('#quickly').code(),
        responsibility: $('#responsibility').code(),
        fileName: $('#about_foto').find('.q-fileName').val(),
    };

    $.ajax({
        url: "rest/cms/saveAbout",
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        data: JSON.stringify(res),
        success: function (ans) {
            alert("Данные изменены");
            aboutPage();
        }
    });

}



/*
 Masked Input plugin for jQuery
 Copyright (c) 2007-2013 Josh Bush (digitalbush.com)
 Licensed under the MIT license (http://digitalbush.com/projects/masked-input-plugin/#license)
 Version: 1.3.1
 */
(function (e) {
    function t() {
        var e = document.createElement("input"), t = "onpaste";
        return e.setAttribute(t, ""), "function" == typeof e[t] ? "paste" : "input"
    }

    var n, a = t() + ".mask", r = navigator.userAgent, i = /iphone/i.test(r), o = /android/i.test(r);
    e.mask = {
        definitions: {9: "[0-9]", a: "[A-Za-z]", "*": "[A-Za-z0-9]"},
        dataName: "rawMaskFn",
        placeholder: "_"
    }, e.fn.extend({
        caret: function (e, t) {
            var n;
            if (0 !== this.length && !this.is(":hidden"))return "number" == typeof e ? (t = "number" == typeof t ? t : e, this.each(function () {
                this.setSelectionRange ? this.setSelectionRange(e, t) : this.createTextRange && (n = this.createTextRange(), n.collapse(!0), n.moveEnd("character", t), n.moveStart("character", e), n.select())
            })) : (this[0].setSelectionRange ? (e = this[0].selectionStart, t = this[0].selectionEnd) : document.selection && document.selection.createRange && (n = document.selection.createRange(), e = 0 - n.duplicate().moveStart("character", -1e5), t = e + n.text.length), {
                begin: e,
                end: t
            })
        }, unmask: function () {
            return this.trigger("unmask")
        }, mask: function (t, r) {
            var c, l, s, u, f, h;
            return !t && this.length > 0 ? (c = e(this[0]), c.data(e.mask.dataName)()) : (r = e.extend({
                placeholder: e.mask.placeholder,
                completed: null
            }, r), l = e.mask.definitions, s = [], u = h = t.length, f = null, e.each(t.split(""), function (e, t) {
                "?" == t ? (h--, u = e) : l[t] ? (s.push(RegExp(l[t])), null === f && (f = s.length - 1)) : s.push(null)
            }), this.trigger("unmask").each(function () {
                function c(e) {
                    for (; h > ++e && !s[e];);
                    return e
                }

                function d(e) {
                    for (; --e >= 0 && !s[e];);
                    return e
                }

                function m(e, t) {
                    var n, a;
                    if (!(0 > e)) {
                        for (n = e, a = c(t); h > n; n++)if (s[n]) {
                            if (!(h > a && s[n].test(R[a])))break;
                            R[n] = R[a], R[a] = r.placeholder, a = c(a)
                        }
                        b(), x.caret(Math.max(f, e))
                    }
                }

                function p(e) {
                    var t, n, a, i;
                    for (t = e, n = r.placeholder; h > t; t++)if (s[t]) {
                        if (a = c(t), i = R[t], R[t] = n, !(h > a && s[a].test(i)))break;
                        n = i
                    }
                }

                function g(e) {
                    var t, n, a, r = e.which;
                    8 === r || 46 === r || i && 127 === r ? (t = x.caret(), n = t.begin, a = t.end, 0 === a - n && (n = 46 !== r ? d(n) : a = c(n - 1), a = 46 === r ? c(a) : a), k(n, a), m(n, a - 1), e.preventDefault()) : 27 == r && (x.val(S), x.caret(0, y()), e.preventDefault())
                }

                function v(t) {
                    var n, a, i, l = t.which, u = x.caret();
                    t.ctrlKey || t.altKey || t.metaKey || 32 > l || l && (0 !== u.end - u.begin && (k(u.begin, u.end), m(u.begin, u.end - 1)), n = c(u.begin - 1), h > n && (a = String.fromCharCode(l), s[n].test(a) && (p(n), R[n] = a, b(), i = c(n), o ? setTimeout(e.proxy(e.fn.caret, x, i), 0) : x.caret(i), r.completed && i >= h && r.completed.call(x))), t.preventDefault())
                }

                function k(e, t) {
                    var n;
                    for (n = e; t > n && h > n; n++)s[n] && (R[n] = r.placeholder)
                }

                function b() {
                    x.val(R.join(""))
                }

                function y(e) {
                    var t, n, a = x.val(), i = -1;
                    for (t = 0, pos = 0; h > t; t++)if (s[t]) {
                        for (R[t] = r.placeholder; pos++ < a.length;)if (n = a.charAt(pos - 1), s[t].test(n)) {
                            R[t] = n, i = t;
                            break
                        }
                        if (pos > a.length)break
                    } else R[t] === a.charAt(pos) && t !== u && (pos++, i = t);
                    return e ? b() : u > i + 1 ? (x.val(""), k(0, h)) : (b(), x.val(x.val().substring(0, i + 1))), u ? t : f
                }

                var x = e(this), R = e.map(t.split(""), function (e) {
                    return "?" != e ? l[e] ? r.placeholder : e : void 0
                }), S = x.val();
                x.data(e.mask.dataName, function () {
                    return e.map(R, function (e, t) {
                        return s[t] && e != r.placeholder ? e : null
                    }).join("")
                }), x.attr("readonly") || x.one("unmask", function () {
                    x.unbind(".mask").removeData(e.mask.dataName)
                }).bind("focus.mask", function () {
                    clearTimeout(n);
                    var e;
                    S = x.val(), e = y(), n = setTimeout(function () {
                        b(), e == t.length ? x.caret(0, e) : x.caret(e)
                    }, 10)
                }).bind("blur.mask", function () {
                    y(), x.val() != S && x.change()
                }).bind("keydown.mask", g).bind("keypress.mask", v).bind(a, function () {
                    setTimeout(function () {
                        var e = y(!0);
                        x.caret(e), r.completed && e == x.val().length && r.completed.call(x)
                    }, 0)
                }), y()
            }))
        }
    })
})(jQuery);
function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {
            return sParameterName[1];
        }
    }
}

$(document).ready(function () {
    initCheckBox();
});
function initKeywords() {
    // Tags Input
    $('.tags-input').tagsInput({
        'height': 'auto',
        'width': 'auto',
        'defaultText': 'добавить ключевую фразу',
    });

    $('.tagsinput input').on('focus', function () {
        $(this).closest('.tagsinput').addClass('focus');
    });

    $('.tagsinput input').on('blur', function () {
        $(this).closest('.tagsinput').removeClass('focus');
    });
}
function initCheckBox() {
    $('.i-checks').iCheck({
        checkboxClass: 'icheckbox_square-green m-r-sm',
        radioClass: 'iradio_square-green',
    });
}

$(window).on('resize load', function () {
    setTimeout(function () {
        var b = $('.wrapper.wrapper-content');
        var h = $(window).height();
        var top = b.offset().top;
        b.css('min-height', h - top);
    });
})

function initdropzone(callback) {
    Dropzone.autoDiscover = false;
    $(".dropzone").dropzone({
            autoProcessQueue: false,
            uploadMultiple: true,
            acceptedFiles: ".jpeg,.jpg,.png,.gif",
            dictDefaultMessage: "Перетащите файл сюда",
            maxFiles: 1,
            maxfilesexceeded: function (file) {
                this.removeAllFiles();
                this.addFile(file);
            },

            // Dropzone settings
            init: function () {
                var myDropzone = this;

                this.element.querySelector("button[type=submit]").addEventListener("click", function (e) {
                    $.blockUI({
                        message: '<div id="bird"></div>',
                        timeout: 2000
                    });
                    e.preventDefault();
                    e.stopPropagation();
                    myDropzone.processQueue();
                });
                this.on("sendingmultiple", function () {
                });
                this.on("complete", function (files) {
                    callback(files, this)
                    $.unblockUI();
                });
                this.on("errormultiple", function (files, response) {
                });
            }

        }
    );
}