/* UTF-8

© kovrigin
Все права разрешены
красивый дизайн должен иметь красивый код®

http://htmlpluscss.ru

*/



$window.ready(function(){

	$('.confirm-kod .btn').on('click',function(){
		$(this).siblings('.input').focus();
	});

	$('.foot .edit-setting').trigger('click');

});