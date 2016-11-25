//Скрипты открытия и закрытия модальных окон, а так же скрипты применяющиеся в этих окнах	
//.fadeOut(300); - Скорость закрытия окна и фона

	//Функция отображения popupin авторизации
	function PopUpShowIn(){
		$(".dark").fadeIn();
		$("#popupin").fadeIn();
		$("header, .main-block, footer").addClass("blurriness");
	}




	//Функция отображения popup выхода
	/*-----*/
	function PopUpShow(){
		$(".dark").fadeIn();
		$("#popup").fadeIn();
		$("header, .main-block, footer").addClass("blurriness");

			//Функция запуска таймера
			function timer(){				
				var obj=document.getElementById('RealButton');			
				var regexp = /([0-9]+)/ig;
				var RealTimer = regexp.exec(obj.innerHTML)[0];
				RealTimer = (RealTimer == 0) ? 61 : RealTimer;

				RealTimer=RealTimer - 1;
				if (RealTimer<0) RealTimer=0;

				obj.innerHTML = RealTimer;

				if (RealTimer==0) { 
						$(".dark").fadeOut(300);
						$("#popup").fadeOut(300);
						$("header, .main-block, footer").removeClass("blurriness");
					logout();
					return true; 
				}
				else {
					setTimeout(timer,1000);
				}			
			};	

			setTimeout(timer,1000);
			//Конец функция запуска таймера
	}



	//Функция скрытия PopUp при нажатии на закрывающую область
	function PopUpHide(){
		$(".dark").fadeOut(300);
		$("#popup").fadeOut(300);
		$("#popupin").fadeOut(300);
		$("header, .main-block, footer").removeClass("blurriness");
	}
	/*-----*/



	//Функция скрытия PopUp при нажатии на область вне модального окна
	$(document).ready(function(){
		$(".dark").click(function(){
			$(".dark").fadeOut(300);
			$("#popup").fadeOut(300);
			$("#popupin").fadeOut(300);
			$("header, .main-block, footer").removeClass("blurriness");

		});
	});




