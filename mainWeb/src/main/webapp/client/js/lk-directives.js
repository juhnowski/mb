(function () {
    'use strict';

    var module = angular.module('lk.directives', []);

    module.directive('lkRmClassIf', [function() {
        return {
            restrict: 'A',
            scope: {
                config: '=lkRmClassIf'
            },
            link: function ($scope, $element) {
                $scope.$watch('config', function (config) {
                    angular.forEach(config, function(bool, cls) {
                        bool ? $element.removeClass('hide') : $element.addClass('hide');
                    });
                }, true);
            }
        }
    }]);

    module.directive('lkMoneySlider', ['$templateCache', 'lkMoneySliderLinkFn', function($templateCache, linkFn) {
        $templateCache.put('lkMoneySlider.tpl',
            '<div class="box clr">' +
            '    <h3 class="value">' +
            '        <span class="pref">Сумма:</span>' +
            '        <span ng-bind="money"></span>' +
            '        <span class="suf">руб.</span>' +
            '    </h3>' +
            '    <div id="summ_zaim_slider" class="slider">' +
            '    </div>' +
            '    <div class="min pull-left" id="minSumLabel"></div>' +
            '    <div class="max pull-right" id="maxSumLabel"></div>' +
            '</div>');

            //'<div class="box clr">' +
            //'    <h4 class="visible-xs">Сумма займа</h4>' +
            //'    <h4><i class="ico ruble-big"></i></h4>' +
            //'    <div class="value pull-left"><input class="input numer pull-left" id="summ_zaim-input" ng-model="money" ng-model-options="{updateOn: \'keyup\', debounce: {default: 500}}"><i class="ico pull-left rub"></i></div>' +
            //'    <div class="slider pull-left" id="summ_zaim_slider" ></div>' +
            //'    <div class="min hidden-sm hidden-xs"><span id="minSumLabel"></span> <span>руб.</span></div>' +
            //'    <div class="max hidden-sm hidden-xs"><span id="maxSumLabel"></span> <span>руб.</span></div>' +
            //'    <a class="plus pull-right hidden-xs"></a>' +
            //'    <a class="minus pull-right hidden-xs"></a>' +
            //'</div>');

        return {
            require: 'ngModel',
            restrict: 'A',
            replace: true,
            templateUrl: 'lkMoneySlider.tpl',
            scope: {
                data: '='
            },
            link: linkFn
        };
    }]);

    module.factory('lkMoneySliderLinkFn', [function() {
        return function($scope, $element, $attrs, $ngModel) {
            var $summSlider = $element.find('#summ_zaim_slider'),
                data = $scope.data,
                dataStep = 100;

            $scope.$watch(function() { return $ngModel.$modelValue; }, function(value) {
                $scope.money = value;
            });

            $scope.$watch('money', function(money) {
                if (money) {
                    $summSlider.slider('value', money);
                    $ngModel.$setViewValue($scope.money = $summSlider.slider('value'));
                }
            });

            $('#minSumLabel').text(data.sumMin);
            $('#maxSumLabel').text(data.sumMax);

            $summSlider.slider({
                min: data.sumMin,
                max: data.sumMax,
                step: dataStep,
                value: $scope.money || data.sumMin,
                slide: function(event,ui) {
                    $ngModel.$setViewValue($scope.money = ui.value);
                    $scope.money = ui.value;
                    $scope.$digest();
                }
            });

            $element.find('.plus, .minus').on('click',function(){
                var s = $(this).siblings('.slider');
                var v = s.slider('value');
                var step = dataStep;
                if($(this).hasClass('minus'))
                    step*=-1;
                v += step;
                if(data.sumMin>v)
                    v = data.sumMin;
                else if(data.sumMax<v)
                    v = data.sumMax;
                $scope.money = v;
                $ngModel.$setViewValue(v);
                $scope.$digest();
            });
        };
    }]);

    module.directive('lkDaysSlider', ['$templateCache', 'lkDaysSliderLinkFn', function($templateCache, linkFn) {
        $templateCache.put('lkDaysSlider.tpl',
            '<div class="box clr">' +
            '   <h3 class="value">' +
            '       <span class="pref">На срок:</span>' +
            '       <span ng-bind="localValue"></span>' +
            '       <span class="suf">дней</span>' +
            '   </h3>' +
            '   <div id="date_zaim_slider" class="slider"></div>' +
            '   <div class="min pull-left" id="minDateLabel"></div>' +
            '   <div class="max pull-right" id="maxDateLabel"></div>' +
            '</div>');

            //'   <h4 class="visible-xs">Срок займа</h4>' +
            //'   <h4><i class="ico time-2"></i></h4>' +
            //'    <div class="value pull-left"><input class="input numer pull-left" id="date_zaim-input" ng-model="localValue" ng-model-options="{updateOn: \'keyup\', debounce: {default: 500}}"><i class="ico pull-left calendar"></i></div>' +
            //'    <div class="slider pull-left" id="date_zaim_slider" ></div>' +
            //'    <div class="min hidden-sm hidden-xs"><span id="minDateLabel"></span> <span>дня</span></div>' +
            //'    <div class="max hidden-sm hidden-xs"><span id="maxDateLabel"></span> <span>день</span></div>' +
            //'    <a class="plus pull-right hidden-xs"></a>' +
            //'    <a class="minus pull-right hidden-xs"></a>' +
            //'</div>' +


        return {
            require: 'ngModel',
            restrict: 'A',
            replace: true,
            templateUrl: 'lkDaysSlider.tpl',
            scope: {
                data: '='
            },
            link: linkFn
        };
    }]);

    module.factory('lkDaysSliderLinkFn', [function() {
        return function($scope, $element, $attrs, $ngModel) {
            var $dateSlider = $element.find('#date_zaim_slider'),
                data = $scope.data,
                dateEndLocal = undefined,
                dataStep = 1;

            $scope.localValue = $ngModel.$viewValue;
            applySlide($scope.localValue);

            $scope.getDateEnd = function() {
                if (!dateEndLocal) {
                    return '';
                }
                return dateEndLocal.getDate()+' / '+ (dateEndLocal.getMonth() + 1) +' / '+dateEndLocal.getFullYear();
            };

            $scope.$watch(function() { return $ngModel.$modelValue; }, function(value) {
                $scope.localValue = value;
            });

            $scope.$watch('localValue', function(localValue) {
                if (localValue) {
                    $dateSlider.slider('value', localValue);
                    $ngModel.$setViewValue($scope.localValue = $dateSlider.slider('value'));
                }
            });

            $('#minDateLabel').text(data.creditDaysMin);
            $('#maxDateLabel').text(data.creditDaysMax);

            $dateSlider.slider({
                min: data.creditDaysMin,
                max: data.creditDaysMax,
                value: $scope.localValue || data.creditDaysMin,
                slide: function(event,ui) {
                    applySlide(ui.value);
                    $ngModel.$setViewValue(ui.value);
                    $scope.localValue = ui.value;
                    $scope.$digest();
                }
            });

            $element.find('.plus, .minus').on('click',function(){
                var s = $(this).siblings('.slider');
                var v = s.slider('value');
                var step = dataStep;
                if($(this).hasClass('minus'))
                    step*=-1;
                v += step;
                if(data.creditDaysMin>v)
                    v = data.creditDaysMin;
                else if(data.creditDaysMax<v)
                    v = data.creditDaysMax;
                $scope.localValue = v;
                $ngModel.$setViewValue(v);
                $scope.$digest();
            });

            function applySlide(value) {
                dateEndLocal = new Date(data.dateEnd);
                dateEndLocal.setDate(dateEndLocal.getDate() + value);
            }
        };
    }]);
})();
