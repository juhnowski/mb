var geocoder = new google.maps.Geocoder();
var map;
var marker;

var latlng = new google.maps.LatLng(64.568838, 39.836466);
var mapOptions = {
	zoom: 12,
	center: latlng,
	mapTypeId: 'roadmap',

	scrollwheel: false,
	panControl: false,
	mapTypeControl: false,
	scaleControl: false,
	streetViewControl: false,
	overviewMapControl: false
}
map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
  
var companyLogo = new google.maps.MarkerImage('images/location.svg',
	new google.maps.Size(120,136),
	new google.maps.Point(0,0),
	new google.maps.Point(50,50)
);
var latlng = new google.maps.LatLng(64.567806, 39.870369);

geocoder.geocode({'latLng': latlng}, function(results, status) {
	if (status == google.maps.GeocoderStatus.OK) {
		map.setZoom(12);
		marker = new google.maps.Marker({
			icon: companyLogo,
			title:"ЛовиЗайм",
			position: latlng,
			map: map
		});
	}
});