// Скрипт радио кнопок
$(document).ready(function(){

	$('.tb-block-radio').click(function(){
		//valueRadio = window['radioList'].getValueFromActive();
		//console.log(window['radioList'].getValueFromActive());

		var valueRadio = $(this).find('div[class^="radio"]').attr('data-value');
		$('.radio').parent().find('.radio').removeClass('active');
		$(this).find('.radio').addClass('active');
		$(this).parent().find('input').val(valueRadio);
	});
		
});