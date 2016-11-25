//В данном файле содержиться множество скриптов применяемых в шаблоне элементов. По окончанию работ ненужные скрипты можно удалить. Все скрипты в самом начале имеют комментарий для чего нужен данный скрипт.

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


	