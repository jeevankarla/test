	function appendRoute(existingUrl) {
      
		var updatedUrl = $(existingUrl).attr('href');
		var shipmentIndex = updatedUrl.indexOf("shipmentId");
		
        if(shipmentIndex > 0){
            updatedUrl = updatedUrl.substring(0, shipmentIndex -1);
        }
		
		var curreElem = $(existingUrl);
        var form = curreElem.parent().parent().find("form");
        var shipmentId = form.find("option:selected").val();
        var facilityId = form.find("option:selected").text();
        
        var resultUrl = updatedUrl + "&shipmentId=" + shipmentId + "&facilityId=" + facilityId;
        $('a[href="'+$(existingUrl).attr('href')+'"]').attr('href', resultUrl);
    }