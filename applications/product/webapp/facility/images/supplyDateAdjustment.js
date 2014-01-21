function changesupplydate(element){
  	var shipmentType = element.substring(0,3);
	if( shipmentType == "AM_"){
		document.getElementById('Shipment_PM').style.display = 'none';
 		document.getElementById('Shipment_AM').style.display = 'block';
	}
	if( shipmentType == "PM_"){
	   	document.getElementById('Shipment_AM').style.display = 'none';
	   	document.getElementById('Shipment_PM').style.display = 'block';
	}
 }