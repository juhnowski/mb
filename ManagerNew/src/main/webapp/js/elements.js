//В данном файле содержатся скрипты применяемых в шаблоне элементов форм. Все скрипты в самом начале имеют комментарий со своим предназначением.

// Скрпит оформления select (выпадающего списка)
$(document).ready(function(){

	// Select
	$('.slct').click(function(){
		/* Заносим выпадающий список в переменную */
		var dropBlock = $(this).parent().find('.drop');
		
		/* Делаем проверку: Если выпадающий блок скрыт то делаем его видимым*/
		if(dropBlock.is(':hidden')) {
			dropBlock.slideDown();
			
			/* Выделяем ссылку открывающую select */
			$(this).addClass('active');
			
			$('.drop').find('li').click(function(){
				var selectResult = $(this).html();
				$(this).parent().parent().find('input').val(selectResult);

				$(this).parent().siblings(".slct").removeClass('active').html(selectResult);
				dropBlock.slideUp();
			});
			
		/* Продолжаем проверку: Если выпадающий блок не скрыт то скрываем его */
		} else {
			$(this).removeClass('active');
			dropBlock.slideUp();
		}
		
		/* Предотвращаем обычное поведение ссылки при клике */
		return false;
	});
				
});



//Скрпит оформления checkbox
$(document).ready(function(){
		
	// Checkbox
	$('.checkboxes').find('.check').click(function(){
		// Пишем условие: если вложенный в див чекбокс отмечен
		if ($(this).find('input').is(':checked')) {
			// то снимаем активность с дива
			$(this).removeClass('active');
			// и удаляем атрибут checked (делаем чекбокс не отмеченным)
			$(this).find('input').removeAttr('checked');
		
		// если же чекбокс не отмечен, то
		} 
		else {
			// добавляем класс активности диву
			$(this).addClass('active');
			// добавляем атрибут checked чекбоксу
			$(this).find('input').attr('checked', true);
		}
	});
		
});


// Скрипт оформления радио кнопок
$(document).ready(function(){

	$('.radioblock').find('.radio').click(function(){
		var valueRadio = $(this).html();
		$(this).parent().find('.radio').removeClass('active');
		$(this).addClass('active');
		$(this).parent().find('input').val(valueRadio);
	});
		
});