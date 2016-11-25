!function ($) {
    "use strict"; // jshint ;_;

    var Address = function (element, options) {
        this.options = options;
        this.$element = $(element);
        this.loadChildren(0);
    };

    Address.prototype = {
        constructor: Address,

        loadChildren: function (level, parentValue, isEdit) {
            var that = this;
            var $parent = this.$element.find('[data-address="level' + level + '"]').parents('tr');
            while ($parent.next().length > 0) {
                $parent.next().remove();
            }

            var nextLevel = level + 1;
            $parent.find('.ajax_request').show();
            $.ajax({
                url: "/main/rest/fias/?level=" + nextLevel + (parentValue != undefined ? "&parent=" + encodeURI(parentValue) : ""),
                type: "POST",
                contentType: "application/json",
                async: false,
                success: function (res) {
                    if (res.length > 0) {
                        var optionsHtml = '';
                        var label = nextLevel === 1 ? 'Регион' : 'Выберите';
                        var levelName = 'Выберите';
                        var value = that.options.value && that.options.value.length > level ? that.options.value[level] : '';
                        if(value.length==0){
                            optionsHtml += '<option value="" class="placeholder">' + 'Выберите' + '</option>';
                        }
                        for (var i = 0; i < res.length; i++) {
                            optionsHtml += "<option data-levelName='"+res[i].levelName+"' value='"+res[i].id+"'>" + res[i].label + "</option>";
                            if (that.options.value && that.options.value.length > level && that.options.value[level] == res[i].id) {
                                label = res[i].label;

                                if (res[i].levelName) {
                                    levelName = res[i].levelName;
                                }
                            }
                        }
                        var $nextSelect = that.$element.find('[data-address="level' + nextLevel + '"]');
                        if ($nextSelect.length < 1) {
                            $nextSelect = $('<tr>' +
                                '<td class="fias-title">'+(levelName ? levelName : 'Выберите')+'</td>' +
                                '<td class="fias-select">' +
                                '<select class="required" data-address="level'+ nextLevel +'" '+ ( isEdit != undefined ? '' : '') +'>' +
                                optionsHtml+
                                '</select>' +
                                '</td>' +
                                '</tr>');

                            if ($parent.length > 0) {
                                $parent.after($nextSelect);
                                $nextSelect.find('select').select2();
                                $nextSelect.find('select').select2('val', value);
                            } else {
                                that.$element.append($nextSelect);
                            }
                        } else {
                            $nextSelect.append(optionsHtml);
                            $nextSelect.select2('val', value);
                            $nextSelect.closest('tr').find('td.fias-title').text(levelName);
                        }
                        $nextSelect.on('change', function (e) {
                            $(this).find('.placeholder').remove();
                            var selected = $(this).find(':selected');
                            if (selected && selected.attr('data-levelName')) {
                                $(this).closest('tr').find('td.fias-title').text(selected.attr('data-levelName'));
                            } else {
                                $(this).closest('tr').find('td.fias-title').text(label);
                            }
                            if ($(e.target).select2('val')) {
                                that.options.value = null;
                                that.loadChildren(nextLevel, $(e.target).select2('val'), 1);
                            }
                        });
                        if (value) {
                            that.loadChildren(nextLevel, value);
                        }
                    }
                },
                complete: function() {
                    $parent.find('.ajax_request').hide();
                },
                dataType: 'json'
            });
        },

        val: function(value) {
            return this.$element.find('select:last').select2('val');
        }
    };

    $.fn.address = function (option) {
        return this.each(function () {
            var $this = $(this)
                , data = $this.data('address')
                , options = $.extend({}, $.fn.address.defaults, $this.data(), typeof option == 'object' && option)
            if (!data) {
                $this.data('address', (data = new Address(this, options)));
            }
            if (typeof option == 'string') {
                data[option].call($this);
            }
        })
    };

    $.fn.address.Constructor = Address

    //$(document).find('[data-select="address"] select').on('change', function(e) {
    //    var $this = $(this).parents('[data-select="address"]:first');
    //    //if ($this.data('address')) {
    //    //    get_select($(this));
    //    //    return;
    //    //}
    //    $this.address($(this).select2('val'));
    //})
}
(window.jQuery);


// select
function get_select(i) {
    i.off('blur keyup keydown').on('blur', function () {
        if (i.hasClass('not_search'))
            i.val('');
        if(i.val()=='') {
            i.next().val('');
            //fillFIAS(i.attr('id'));
        }
        else {
            i.closest('.required').removeClass('required').find('.red').remove();
        }
    }).on('keyup', function () {
        option_hide($(this));
    }).on('keydown',function(e){
        var kod = e.which;
        var pane = $(this).parent().siblings('.scrollpane');
        var api = pane.data('jsp');
        var hover = pane.find('.hover');
        if((kod==38 || kod==40) && !hover.is('.hover'))
            pane.find('i').not('.hide').first().addClass('hover');
        else if (kod==38) {
            if(hover.position().top <= api.getContentPositionY())
                api.scrollToY(hover.position().top - 20);
            hover.prevAll('i').not('.hide').first().addClass('hover');
        }
        else if (kod==40) {
            if(hover.position().top > 100 + api.getContentPositionY())
                api.scrollToY(hover.position().top - 100);
            hover.nextAll('i').not('.hide').first().addClass('hover');
        }
        else if (kod==13)
            hover.trigger('click');
        hover.removeClass('hover');
    });
    select_input(i);
}

// select input
function select_input(t) {
    var b = t.parent();
    $('.select.focus').removeClass('focus').find('.scrollpane').hide();
    b.parents('.select').addClass('focus');
    var pane = b.siblings('.scrollpane');

    if (pane.children().is('i')) {
        pane.css({
            width: b.width(),
            left: b.position().left,
            top: b.position().top + b.outerHeight(true)
        });
        pane.jScrollPane({
            verticalDragMinHeight: 30
        }).on('click', 'i', function () {
            if($(this).is('.title')) {
                var v = $(this).text();
                var guid = $(this).attr('data-value');
            }
            else {
                var v = $(this).children('.title').text();
                var guid = $(this).children('.guid').text();
            }
            if ($(this).hasClass('not_search'))
                v = '';
            t.val(v);
            t.next().val(guid).trigger('change');
            setTimeout(function () {
                pane.hide();
                t.trigger('blur');
            });
            //fillFIAS(t.attr('id'));
            if (pane.attr('data-class'))
                $('select.'+pane.attr('data-class')).val(guid).trigger('change');
        });
    }
    var kol = pane.find('i').size();
    if (kol > 7)
        pane.data('max-height', 20 * 7);
    else
        pane.data('max-height', 20 * kol);
    option_hide(t);
}

// select
function option_hide(input) {
    var reg = new RegExp(input.val(), "i");
    var pane = input.parent().siblings('.scrollpane');
    var api = pane.data('jsp');
    var size = false;
    pane.show();

    pane.find('i').each(function () {
        if($(this).is('.title'))
            var text = $(this).text();
        else
            var text = $(this).children('.title').text();
        var te = reg.exec(text);
        if (te != null) {
            $(this).removeClass('hide');
            size = true;
        }
        else
            $(this).addClass('hide');
    });
    if (size) {
        input.removeClass('not_search');
        pane.find('.not_search').remove();
    }
    else {
        input.addClass('not_search');
        pane.find('.jspPane').append('<i class="not_search title">Не найдено: <b>' + input.val() + '</b></i>');
    }
    api.reinitialise();
    var h = api.getContentHeight();
    if (h == 0)
        pane.hide();
    var ph = pane.height();
    if (pane.data('max-height') < h) {
        pane.children('.jspContainer').height(pane.data('max-height'));
        api.reinitialise();
    }
    else
        pane.children('.jspContainer').height(h).children('.jspVerticalBar').remove();
}
