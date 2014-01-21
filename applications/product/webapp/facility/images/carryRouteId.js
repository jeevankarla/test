
	function setSelectedRoute(existingUrl, shipmentId) {

        var updatedUrl = $(existingUrl).attr('href');
        var facilityIndex = updatedUrl.indexOf("facilityId");

        if(facilityIndex > 0){
            updatedUrl = updatedUrl.substring(0, facilityIndex -1);
        }
        var form = $("input[value=" + shipmentId + "]").parent();
        var routeId = form.find("option:selected").text();
        var resultUrl = updatedUrl + "&facilityId=" + routeId;

        $('a[href="'+$(existingUrl).attr('href')+'"]').attr('href', resultUrl);
    }