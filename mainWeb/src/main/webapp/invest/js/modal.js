//Скрипты открытия и закрытия модальных окон, а так же скрипты применяющиеся в этих окнах	
//.fadeOut(300); - Скорость закрытия окна и фона

	//Функция отображения PopUp
	/*-----*/
	function PopUpShow(){
		$(".dark").fadeIn();
		$("#popup").fadeIn();
		$("header, main, footer").addClass("blurriness");
		$("#popup2, #popup3").fadeOut(300);
	}



	//Функция скрытия PopUp при нажатии на закрывающую область
	function PopUpHide(){
		$(".dark").fadeOut(300);
		$("#popup").fadeOut(300);
		$("header, main, footer").removeClass("blurriness");
	}
	/*-----*/



	//Функция скрытия PopUp при нажатии на область вне модального окна
	$(document).ready(function(){
		$(".dark").click(function(){
			$(".dark").fadeOut(300);
			$("#popup").fadeOut(300);
			$("header, main, footer").removeClass("blurriness");

		});
	});


