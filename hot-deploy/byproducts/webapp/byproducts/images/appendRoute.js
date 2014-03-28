	function appendRoute(existingUrl) {
      
		var updatedUrl = $(existingUrl).attr('href');
		var shipmentIndex = updatedUrl.indexOf("shipmentId");
		
        if(shipmentIndex > 0){
            updatedUrl = updatedUrl.substring(0, shipmentIndex -1);
        }
		
		var curreElem = $(existingUrl);
        var form = curreElem.parent().parent().find("form");
        var shipmentId = form.find("option:selected").val();
        var facilityId = form.find("option:selected").val();
       
 
     
        var resultUrl = updatedUrl + "&shipmentId=" + shipmentId + "&facilityId=" + facilityId;
        $('a[href="'+$(existingUrl).attr('href')+'"]').attr('href', resultUrl);
        
      
    }
	
	// finalize orders calling another  Javscript
	function finalizeFormSubmit(existingUrl) {
	      
		var updatedUrl = $(existingUrl).attr('href');
		var shipmentIndex = updatedUrl.indexOf("shipmentId");
		
        if(shipmentIndex > 0){
            updatedUrl = updatedUrl.substring(0, shipmentIndex -1);
        }
		
		var curreElem = $(existingUrl);
        var form = curreElem.parent().parent().find("form");
        var shipmentId = form.find("option:selected").val();
        var facilityId = form.find("option:selected").val();
        var formId=form.attr('id');
 
        var resultUrl = updatedUrl + "&shipmentId=" + shipmentId + "&facilityId=" + facilityId;
        $('a[href="'+$(existingUrl).attr('href')+'"]').attr('href', resultUrl);
        
        var str = "#"+formId;
       var finalizeNetSales = $(str).attr("action", "FinalizeOrders");
                finalizeNetSales.append("<input type='hidden' name='routeId' value='"+facilityId+"'/>");
                finalizeNetSales.submit();
    }
	
	function appendBillingParam(existingUrl){
		//alert(existingUrl);
		
		var updatedUrl = $(existingUrl).attr('href');
		var billingIndex = updatedUrl.indexOf("periodBillingId");
		
        if(billingIndex > 0){
            updatedUrl = updatedUrl.substring(0, billingIndex -1);
        }
		var domObj = $(existingUrl).parent().parent();
		
		var periodBillingDOM = $(domObj).find( "[name='"+"periodBillingId"+"']");
		//alert($(periodBillingDOM).val());
		var periodBillingId = $(periodBillingDOM).val();
		
		var resultUrl = updatedUrl + "&periodBillingId="+periodBillingId;
        $('a[href="'+$(existingUrl).attr('href')+'"]').attr('href', resultUrl);
	}