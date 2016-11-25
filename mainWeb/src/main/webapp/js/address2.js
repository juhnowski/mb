(function (window) {
    'use strict';

    var Address = window.Address = function (element, options) {
        this.options = options;
        this.$element = $(element);
        this.loadChildren(0);
    };

    var cache = {};

    Address.prototype = {
        constructor: Address,
        setAddressChain: function(chain) {
            chain = chain && chain.slice(); // copy array
            if (!chain || !chain.length) {
                return;
            }

            cache.data = cache.data || {};
            $.each(chain, function(idx, item) {
                cache.data[item.id] = item;
            });

            this.nextSelect && this.nextSelect.trigger('address.clean');

            var item = chain.shift();
            $(this).html($('<option value="' + item.id + '">' + item.label + '</option>').prop('checked', true)).trigger('change', [{chain: chain}]);

        },
        loadChildren: function (level, parentValue, chain) {
            var that = this;
            var $parent = this.$element.find('[data-address="level' + level + '"]');
            while ($parent.next().length > 0) {
                $parent.next().remove();
            }

            var nextLevel = level + 1;
            $parent.find('.ajax_request').show();

            return buildSelect();

            function buildSelect() {
                var label = nextLevel === 1 ? 'Регион' : 'Выберите';
                var value = that.options.value && that.options.value.length > level ? that.options.value[level] : '';

                var row = $('<div class="row" data-address="level' + nextLevel + '"></div>');
                row.append($('<div class="col first"><label>' + label + '</label></div>'));
                row.append('<div class="col"><select class="required"></select></div>');
                that.$element.append(row);

                var select = row.find('select');

                $(select).select2({
                    language: "ru",
                    placeholder: "Выберите...",
                    ajax: {
                        url: "rest/fias/",
                        dataType: 'json',
                        delay: 250,
                        data: function (params) {
                            return {
                                level: nextLevel,
                                parent: (parentValue != undefined ? encodeURI(parentValue) : ""),
                                term: params.term,
                                page: params.page || 1
                            };
                        },
                        processResults: function (data, page) {
                            cache.data = cache.data || {};
                            if (!cache.data.__lastPage || cache.data.__lastPage < page) {
                                cache.data = {};
                            }
                            cache.data.__lastPage = page;

                            $.each(data.items, function(idx, item) {
                                item.text = item.label;
                                cache.data[item.id] = item;
                            });
                            return {
                                results: data.items,
                                pagination: {
                                    more: data.more
                                }
                            };
                        },
                        cache: true
                    }
                });

                $(select).on("select2:open", function () {
                    $(this).closest('.col').prev().andSelf().addClass('focus');
                    if(!$(this).hasClass('not-valid-row')){
                        $(this).closest('.row').addClass('focus').removeClass('red');
                    }
                });
                $(select).on("select2:close", function () {
                    $(this).closest('.col').prev().andSelf().removeClass('focus');
                    $(this).closest('.row').removeClass('focus');
                    if (!$(this).hasClass('not-valid-row')) {
                        ($(this).val() == null || $(this).val() == '') ? $(this).closest('.row').addClass('red') : $(this).closest('.row').addClass('green');
                    }
                });

                var nextSelect = that.nextStep = undefined;

                $(select).on('select change', function(evt, config) {
                    that.options.value = that.options.value || [];
                    value = that.options.value[nextLevel] = $(select).find(':selected').attr('value');
                    if (value) {
                        nextSelect && nextSelect.trigger('address.clean');
                        if (!config || !config.chain || !config.chain.length) {
                            $.getJSON("rest/fias/hasChildren", {parent: value}).then(function (resp) {
                                if (resp.success && resp.data) {
                                    nextSelect = that.loadChildren(nextLevel, value);
                                }
                            });
                        } else {
                            nextSelect = that.loadChildren(nextLevel, value, config.chain);
                        }

                        var item = cache.data[value];
                        item && select.closest('.row').find('.col.first label').text(item.levelName);
                    } else {
                        select.closest('.row').find('.col.first').text(label);
                    }
                });

                $(select).on('address.clean', function(evt, callback) {
                    var $element = $(this);

                    nextSelect ? nextSelect.trigger('address.clean', [rm]) : rm();

                    function rm() {
                        $element.closest('.row').remove();
                        callback && callback();
                    }
                });

                if (chain && chain.length) {
                    nextSelect && nextSelect.trigger('address.clean');

                    var item = chain.shift();
                    $(select).html($('<option value="' + item.id + '">' + item.label + '</option>').prop('checked', true)).trigger('change', [{chain: chain}]);
                }

                return select;
            }
        }
    };

    $.fn.address = function (option, value) {
        return this.each(function () {
            var $this = $(this)
                , data = $this.data('address')
                , options = $.extend({}, $.fn.address.defaults, $this.data(), typeof option == 'object' && option)
            if (!data) {
                $this.data('address', (data = new Address(this, options)));
            }
            if (typeof option == 'string') {
                data[option].call($this, value);
            }
        })
    };

    $.fn.address.Constructor = Address;
})(window);