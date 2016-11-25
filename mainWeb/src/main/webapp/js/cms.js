function initCMSData(type) {
    switch (type) {
        case 'main':
            var method = 'getMainQ'
            apiData(method, function (data) {
                cmsMain(data);
            });
            break;
        case 'news':
            cmsNews();
            break;
        case 'about':
            var method = 'getAbout'
            apiData(method, function (data) {
                cmsAbout(data);
            });
            break;
        case 'faq':
            var method = 'getQuestion'
            apiData(method, function (data) {
                cmsFaq(data);
                initFaq();
            });
            break;
        case 'politics':
            var method = 'getPoliticsNoImg'
            apiData(method, function (data) {
                cmsPolitics(data);
                initAccordion();
            });
            break;
        case 'contacts':
            var method = 'getContacts'
            apiData(method, function (data) {
                cmsContacts(data);
            });
            break;
    }
}
function cmsContacts(data) {
    dataToPage(data);

    $('[itemprop="address"]').text(data.address);
    $('[itemprop="telephone"]').prop('href', 'tel:' + data.phone);
    $('[itemprop="telephone"]').text(data.phone);
    $('[itemprop="email"]').prop('href', 'mailto:' + data.email);
    $('[itemprop="email"]').text(data.email);

    $('.fb a').prop('href', data.fb);
    $('.ok a').prop('href', data.ok);
    $('.tw a').prop('href', data.tw);
    $('.vk a').prop('href', data.vk);

}
function cmsPolitics(data) {
    dataToPage(data.main);
    //$('#feedback').html(data.main.feelback);
    $('.fadeInUp').html(data.main.headerText);
    //var f = findImg(data.image, data.main.fileName);
    //$('#fileName').prop('src', imgData(f));

    $.each(data.main.dataList, function (k) {
        $('<dt>' + this.header + '' +
            (this.fileName ? '<a href="/cms/rest/cms/downloadDoc?fileName=' + encodeURI(this.fileName) + '" target="_blank">скачать pdf</a>' : '<a href="#"></a>') +
            '</dt><dd><div class="box">' + this.distr + '</div></dd>').appendTo('.accordion');
    });
}

function cmsFaq(data) {
    dataToPage(data.main);
    //$('#feedback').html(data.main.feedback);
    $('#header').html(data.main.header);
    //var f = findImg(data.image, data.main.fileName);
    //$('#fileName').prop('src', imgData(f));

    $.each(data.main.categorys, function (k) {
        that = this;
        $('<li>' + this.name + '</li>').appendTo('#category');
        $('<h2 data-ankor="' + this.code + '">' + this.name + '</h2>').appendTo('#content');

        var cntn = '';

        $.each(data.main.questions, function (q) {
            if (this.idCategory == that.code) {
                cntn = cntn + '<dt data-ankor="' + this.code + '">' + this.name + '</dt>';
                cntn = cntn + '<dd>' + this.answer + '</dd>';
            }
        });

        $('<dl>' + cntn + '</dl>').appendTo('#content')
    });

}

function cmsMain(data) {
    dataToPage(data.main);

    var i =1;
    $.each(data.main.yesOntime, function(k, value){
        $('<li><i class="ico li'+i+'"></i><b>'+value['header']+'</b>'+value['body']+'</li>').appendTo('.yesontime');
        i++;
    });
	var indexgif = 1;
    var li = ''
    $.each(data.main.howThisWork, function (k, value) {
        $('<h3 class="dt">'+value['header']+'</h3>' +
        '<div class="dd">' +
        '<b>'+value['header']+'</b>' +
        '<p>'+value['body']+'</p>' +
        /*'<a href="'+value['link']+'" class="readmore visible-lg"><i class="ico info"></i>Больше информации<i class="ico arrow-right-blue"></i></a>' +*/
        '<div class="img"><img src="img/'+ indexgif +'.gif" alt="'+value['header']+'"></div></div>').appendTo('.tabs');
		indexgif++;
    });

    initTabsIndex();
    var quest = '';
    if(data.questions) {
        $.each(data.questions, function (k) {
            if (this.active) {
                quest = quest + '<li><a href="faq.shtml#' + this.link + '">' + this.name + '</a></li>';
            }
        });
        $('.questions').html(quest);
    }
    if(data.main.imgUp){
        var up = findImg(data.images, data.main.imgUp);
        if(up){
            $('.img-head').css('background-image', 'url('+imgData(up)+')');
        }
    }
    if(data.main.imgDown){
        var down = findImg(data.images, data.main.imgDown);
        if(down){
            $('.statistic_block').css('background-image', 'url('+imgData(down)+') ');
        }
    }
}

function dataToImg(img, data) {
    $(img).prop('src', imgData(data));
}
function imgData(data) {
    return 'data:image/jpg;base64,' + data;
}

function cmsNews(data) {
    var id = getUrlParameter('page');
    id = id ? id : 1;
    $.ajax({
        url: "/cms/rest/cms/getNews?page=" + id,
        type: "GET",
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)

                cmsNewsData(ans.data);

                var nextpr = '';
                var prev = '';
                if (id > 1) {
                    prev = 'href="news.shtml?page = ' + (parseInt(id) - 1 + '"');
                    nextpr += '<a ' + prev + '><i class="loading-data"></i>ПРЕДЫДУЩИЕ НОВОСТИ </a> ';
                }


                var next = '';
                if (ans.data.total > (id * 3)) {
                    next = 'href="news.shtml?page=' + (parseInt(id) + 1 + '"');
                    nextpr += ' <a ' + next + '> <i class="loading-data"></i> СЛЕДУЮЩИЕ НОВОСТИ</a>';
                    nextpr += '';
                }

                $(nextpr).appendTo('.load-next');
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}

function cmsNewsData(data) {
    dataToPage(data.news);
    $('#feedback').html(data.news.feedback);
    $('#headerContext').html(data.news.headerContext);
    var f = findImg(data.image, data.news.fileName);
    $('#fileName').prop('src', imgData(f));
    var li = ''
    $.each(data.news.news, function (k) {
        var d = findImg(data.image, this.fileNamePreview);
        var blockImg = d ?  '<a href="post.shtml?id=' + this.id + '" class="img"><img style="width:127px;"  src="' + imgData(d) + '" alt=""></a>' : '';
        var noTag;
        try{
            noTag  = $('<p>' + this.text + '</p>').text();
            if(noTag.length==0){
                noTag = this.text;
            }
        }catch(e){
            noTag  = this.text;
        }
        var n = '<li>' +
                    '<h3 class="title"><a href="post.shtml?id=' + this.id + '">' + this.name + '</a></h3>' +
                            blockImg +
                    '<div class="text" style="min-height: 150px;">' +
                        '<time datetime="' + this.date + '">' + this.date + '</time>' +
                    '<p>' + (noTag.length > 340 ? noTag.substr(0, 340) + '...' : noTag) + '</p>' +
                    '<a href="post.shtml?id=' + this.id + '" class="readmore">читать полностью</a></div></li>' +
            '';

        li = li + n;
    });

    $('.list').html(li);


}
function findImg(list, name) {
    var res = '';
    $.each(list, function () {
        if (this.fileName == name && res.length == 0) {
            res = this.data;
        }
    });
    return res;
}

function cmsAbout(data) {

    dataToPage(data.about);

    $('#company').html(data.about.company);
    $('#simply').html(data.about.simply);
    $('#quickly').html(data.about.quickly);
    $('#responsibility').html(data.about.responsibility);
}

function cmsAnketa(data) {
    dataToPage(data.anketa);
    $('#help-me').html(data.anketa.help);
    var daa = findImg(data.image, data.anketa.fileName);
    $('.help_my').find('img').prop('src', imgData(daa));
}

function dataToPage(data) {
    if (data.title != null) {
        $('title').text(data.title);
    }
    if (data.description != null) {
        $('[name="description"]').prop('content', data.description);
    }
    if (data.keywords != null) {
        $('[name="keywords"]').prop('content', data.keywords);
    }
}

function apiData(method, callback) {
    $.ajax({
        url: "/cms/rest/cms/" + method,
        type: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                callback(ans.data);
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}

function getNewsById(){
    var id = getUrlParameter('id');
    if(id){
        $.ajax({
            url: "/cms/rest/cms/getNewsById?id="+id,
            type: "POST",
            success: function (ans) {
                if (ans.success) {
                    console.log(ans.data)

                    if(ans.data.image && ans.data.image.length>0){
                        $('.img-head').css('background-image', 'url('+imgData(ans.data.image[0].data)+')');
                    }
                    if(ans.data.news){
                        dataToPage(ans.data.news);
                        $('.header-news').text(ans.data.news.name);
                        $('time').text(ans.data.news.date);
                        $('#header-news').after(ans.data.news.text);
                    }

                    generateSocialLinks(window.location.href, ans.data.news.name)

                } else {
                    alert("false " + ans.errorMessage);
                }
            }
        });
    }
}
function getUrlParameter(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++)
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam)
        {
            return sParameterName[1];
        }
    }
}

function generateSocialLinks(url, text) {
    var twitter = $('main .tw a');
    var facebook = $('main .fb a');
    var vk = $('main .vk a');
    var ok = $('main .ok a');

    twitter.on('click', function () {
        window.open(
            'https://twitter.com/intent/tweet?text=' + text + '&url=' + url,
            'Твиттер',
            'width=550,height=420')
    });

    facebook.on('click', function () {
        window.open(
            'https://www.facebook.com/sharer/sharer.php?u=' + url,
            'Facebook',
            'width=550,height=420')
    });

    vk.on('click', function () {
        window.open(
            'http://vk.com/share.php?url=' + url,
            'Вконтакте',
            'width=600,height=600')
    });

    ok.on('click', function () {
        window.open(
            'http://www.odnoklassniki.ru/dk?st.cmd=addShare&st.s=1&st.comments=' + text + '&st._surl=' + url,
            'Одноклассники',
            'width=600,height=420')
    });
}
