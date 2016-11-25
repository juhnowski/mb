//Скрипты открытия и закрытия модальных окон, а так же скрипты применяющиеся в этих окнах	
//.fadeOut(300); - Скорость закрытия окна и фона

	//Функция отображения PopUp
	/*-----*/
	function popuplovi(){
		$(".dark").fadeIn();
		$("#popup-lovi").fadeIn();
		$("header, main, footer").addClass("blurriness");
	}

	function popuplovi2(){
		$(".dark").fadeIn();
		$("#popup-lovi-two").fadeIn();
		$("header, main, footer").addClass("blurriness");
	}


	//Функция скрытия PopUp при нажатии на закрывающую область
	function PopUpHide(){
		$(".dark").fadeOut(300);
		$(".popup-lovi").fadeOut(300);
		$("header, main, footer").removeClass("blurriness");
		window.location.href = '/main/client/overview.shtml';
	}
	/*-----*/



