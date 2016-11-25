function alert_top(a_up, title, message) {
	if (title)
		a_up.children('.h2').html(title);
	if (message)
		a_up.children('.text_success').html(message);
	var t = $(window).scrollTop();
	var h = $(window).height();
	var h_up = a_up.outerHeight();
	var m_top = 50;
	if (h > h_up)
		m_top = (h - h_up) / 2;
	a_up.css('top', t - h_up).show();
	$('.bg_up').fadeIn(function() {
		if ($('.alert_up').is('.animate')) {
			var up_show = $('.alert_up.animate').addClass('down');
			up_show.css('top', t + h + 20).afterTransition(function() {
				up_show.removeClass('animate down').hide();
			});
		}
		a_up.addClass('animate').css('top', t + m_top);
	});
}

function up_select(){
	$('select.hide').each(function(){
		var t = $(this);
		if(t.hasClass('add'))
			return true;
		var r = parseInt(Math.random()*10000);
		t.addClass('add sel-' + r);
		var s = '<label class="l_i select">';
		if(t.val()=='')
			var vs = '';
		else
			var vs = t.children(':selected').text();
		if(t.hasClass('search'))
			s += '<span class="bg_box"><input onFocus="get_select($(this))" type="text" value="'+vs+'">';
		else
			s += '<span class="value" onClick="select_value($(this))">' + vs;
		s += '</span><span class="box scrollpane datepicker-select" data-class="sel-'+r+'">';
		t.children().each(function(){
			if($(this).attr('value')!='')
				s += '<i class="title" data-value="' + $(this).attr('value') + '">' + $(this).text() + '</i>';
		});
		s += '</span></label>';
		t.before(s);
	});
}

//select value
function select_value(t) {
	$('.select.focus .scrollpane').hide().parent().removeClass('focus');
	t.parent().addClass('focus');
	var pane = t.siblings('.scrollpane');
	pane.css({
		width: t.outerWidth(),
		left: t.position().left,
		top: t.position().top + t.outerHeight(true)
	});
	pane.show().jScrollPane({
		verticalDragMinHeight: 30
	}).on('click', 'i', function () {
		if($(this).children('.title').is('.title'))
			var v = $(this).children('.title').text();
		else
			var v = $(this).text();
		var guid = $(this).children('.guid').text();
		t.text(v);
		if ($(this).attr('data-value'))
			guid = $(this).attr('data-value');
		t.next().val(guid).trigger('change');
		pane.hide();
		// fix for Marriage
		if(t.next().is('#marriage'))
			Marriage(guid);
		// fix for datepicker
		if (pane.attr('data-class'))
			$('select.'+pane.attr('data-class')).val(guid).trigger('change');
	});
}
$(document).ready(function (){
	var bg_up = $('.bg_up');
	bg_up.css('opacity', .3).on('click', function() {
		var a_up = $('.alert_up.animate');
		var t = $(window).scrollTop();
		var h_up = a_up.outerHeight();
		a_up.css('top', t - h_up - 30);
		bg_up.fadeOut(function() {
			a_up.removeClass('animate').hide();
		});
	});
	$('.alert_up .close').on('click', function() {
		bg_up.trigger('click');
	});
	//up_select(); // remove for processing ajax loaded selects
});