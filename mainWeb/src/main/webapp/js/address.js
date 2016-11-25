!function ($) {
    "use strict"; // jshint ;_;

    var Address = function (element, options) {
        this.options = options;
        this.$element = $(element);
        this.loadChildren(0);
    };

    Address.prototype = {
        constructor: Address,

        loadChildren: function (level, parentValue) {
            var that = this;
            var $parent = this.$element.find('[data-address="level' + level + '"]');
            while ($parent.next().length > 0) {
                $parent.next().remove();
            }

            var nextLevel = level + 1;
            $parent.find('.ajax_request').show();
            $.ajax({
                url: "rest/fias/?level=" + nextLevel + (parentValue != undefined ? "&parent=" + encodeURI(parentValue) : ""),
                type: "POST",
                contentType: "application/json",
                async: false,
                success: function (res) {
                    if (res.length > 0) {
                        var optionsHtml = '';
                        var label = '';
                        var value = that.options.value && that.options.value.length > level ? that.options.value[level] : '';
                        for (var i = 0; i < res.length; i++) {
                            optionsHtml += "<i><span class='title'>" + res[i].label + "</span><span class='guid'>" + res[i].id + "</span></i>";
                            if (that.options.value && that.options.value.length > level && that.options.value[level] == res[i].id) {
                                label = res[i].label;
                            }
                        }
                        var $nextSelect = that.$element.find('[data-address="level' + nextLevel + '"]');
                        if ($nextSelect.length < 1) {
                            $nextSelect = $('<label class="l_i col-3 select" data-address="level' + nextLevel + '">Выберите <span class="asterisk">*</span>'
                                + '<span class="bg_box">'
                                + '<input type="text" class="required" id="level'+nextLevel+'_text" data-address="level' + nextLevel + '-label" value="'+label+'">'
                                + '<input type="hidden" id="level'+nextLevel+'" data-address="level' + nextLevel + '-value" value="'+value+'">'
                                + '<div class="ajax_request"></div></span>'
                                + '<span class="box scrollpane" id="level'+nextLevel+'_span" data-address="level' + nextLevel + '-options">' + optionsHtml + '</span>'
                                + '</label>');
                            if ($parent.length > 0) {
                                $parent.after($nextSelect);
                            } else {
                                that.$element.append($nextSelect);
                            }
                        } else {
                            $nextSelect.find('[data-address="level' + nextLevel + '-options"]').html(optionsHtml);
                            $nextSelect.find('[data-address="level' + nextLevel + '-label"]').val(label);
                            $nextSelect.find('[data-address="level' + nextLevel + '-value"]').val(value);
                        }
			
						$nextSelect.find('input[type="hidden"]').change(function () {
                            if (this.value) {
                                that.options.value = null;
                                that.loadChildren(nextLevel, this.value);
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
            return this.$element.find('input[type="hidden"]:last').val();
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

    $.fn.address.Constructor = Address;

    $(document).on('focus.address.data-api', '[data-select="address"] input[type="text"]', function (e) {
        var $this = $(this).parents('[data-select="address"]:first');
        console.log($this)
        if ($this.data('address')) {
            get_select($(this));
            return;
        }
        $this.address($this.data());
        get_select($(this));
    })
}
(window.jQuery);