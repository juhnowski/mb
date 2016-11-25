//В данном файле содержаться скрипты применяемые в шаблоне. Все скрипты в самом начале имеют комментарий для чего нужен данный скрипт.

//Скрипт сворачивания - разворачивания левого сайдбара при клике на кнопку
jQuery(document).ready(function(){
		jQuery('.sidhd-button').click(function(){
			jQuery('.sidebar').toggleClass('sidebar-rolled');
			jQuery('.main-content').toggleClass('main-content-deployed');
			jQuery('.sidbar-fon').toggleClass('sidbar-fon-mini');

	});
});


//Скрипт открытия - закрытия меню при нажатии на кнопку выхода в левом сайдбаре
jQuery(document).ready(function(){
		jQuery('#user-button').click(function(){
			jQuery('.sidebar-user-menu').toggleClass('btbp-blockdispl');
	});
});


//Скрипт сворачивания - разворачивания п. меню "Отчёты" в левом сайдбаре
jQuery(document).ready(function(){
		jQuery('#reports').click(function(){
			jQuery('#reports').toggleClass('active');
			jQuery('.reports').toggleClass('reports-deployed');

	});
});



//Скрипт сворачивания - разворачивания блока с поиском 
jQuery(document).ready(function(){
	jQuery('#search-rolldown').click(function(){
		jQuery('.block-search-body').toggleClass('btbp-nonedispl');
	});

	jQuery('#search-extended').click(function(){
		jQuery('.block-search-body').toggleClass('bsss-nonedispl');
		jQuery('#search-extended').toggleClass('spantop');
	});


});



//Скрипт сворачивания - разворачивания блока с таблицами на главной странице
jQuery(document).ready(function(){
		jQuery('#borrowers-rolldown').click(function(){
			jQuery('.bt-body-pagination').toggleClass('btbp-nonedispl');
	});
});



//Скрипт блока "Новая задача" на всех страницах
$(document).ready(function(){
		$(".new-task").addClass('animated zoomIn');

		//Звук
		var audio = new Audio(); // Создаём новый элемент Audio
		audio.src = 'audio/new-task.mp3'; // Указываем путь к звуку "клика"
		audio.autoplay = true; // Автоматически запускаем

		$('#n-task').click(function() {
			$('#new-task').css('display', 'none');
		});

		$('#n-chat').click(function() {
			$('#new-chat').css('display', 'none');
		});		
});


//Скрипт сворачивания - разворачивания блока "Таблица клиента" на странице borrower.shtml
jQuery(document).ready(function(){
		jQuery('#loaner-rolldown').click(function(){
			jQuery('.tci-body').toggleClass('btbp-nonedispl');
	});
});


//Скрипт кнопок "позвонить", "написать", "начислить бонус" на странице borrower.shtml
jQuery(document).ready(function(){

	//кнопка "позвонить"
	jQuery('.button-call-head').click(function(){
		jQuery('#bcb').toggleClass('btbp-blockdispl');
	});

		//кнопка "написать"
	jQuery('.button-write-head').click(function(){
		jQuery('#bwb').toggleClass('btbp-blockdispl');
	});

	//кнопка "начислить бонус"
	jQuery('.button-bonus-head').click(function(){
		jQuery('#bbb').toggleClass('btbp-blockdispl');
	});

});



//Скрипт маски ввода 
jQuery(document).ready(function(){

	$(function($) {
		$.mask.definitions['~']='[+-]';
		$('#phone').mask('+7 (999) 999-99-99');
		$('#date, #date2, #date3, #date4').mask('99/99/9999');
		$('#code').mask('999-999');
	});

});



//Скрипт включения выключения звука на странице chat.shtml
jQuery(document).ready(function(){

	//кнопка "позвонить"
	jQuery('.sound').click(function(){
		jQuery('.sound').toggleClass('sound-active');
	});

});



