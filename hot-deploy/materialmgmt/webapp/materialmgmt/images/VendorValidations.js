jQuery(document).ready(function() {
	vendorValidation();
	
});
function vendorValidation() {
	var roleTypeId=jQuery("[name='roleTypeId']").val();
	jQuery('[name=USER_PANID]').parent().parent().hide();
	jQuery('[name=USER_TINNUMBER]').parent().parent().hide();
	jQuery('[name=USER_CSTNUMBER]').parent().parent().hide();
	jQuery('[name=USER_SERVICETAXNUM]').parent().parent().hide(); 
	formName=document.forms['NewUser'];	
	 if (roleTypeId== "SERVICE_VENDOR") {
		 jQuery('[name=USER_PANID]').parent().parent().hide();
		 jQuery('[name=USER_TINNUMBER]').parent().parent().hide();
		 jQuery('[name=USER_CSTNUMBER]').parent().parent().hide();
		 jQuery('[name=USER_SERVICETAXNUM]').parent().parent().show();
		 
		 jQuery('[name=USER_TINNUMBER]').removeClass("required");
		 jQuery('[name=USER_CSTNUMBER]').removeClass("required");
		 jQuery('[name=USER_PANID]').removeClass("required");
		 jQuery('[name=USER_SERVICETAXNUM]').addClass("required");
     }else if(roleTypeId == "MATERIAL_VENDOR") {
    	 var tin=jQuery("[name='USER_TINNUMBER']").val();
    	 var cst=jQuery("[name='USER_CSTNUMBER']").val();
    	 jQuery('[name=USER_PANID]').parent().parent().hide();
    	 jQuery('[name=USER_SERVICETAXNUM]').parent().parent().hide();
    	 jQuery('[name=USER_TINNUMBER]').parent().parent().show();
    	 jQuery('[name=USER_CSTNUMBER]').parent().parent().show();
         if (tin=="" ) {
    		 jQuery('[name=USER_CSTNUMBER]').removeClass("required");
    		 jQuery('[name=USER_TINNUMBER]').addClass("required");
		 }else if (cst== "" ) {
    		 jQuery('[name=USER_CSTNUMBER]').addClass("required");
    		 jQuery('[name=USER_TINNUMBER]').removeClass("required");
		 }
    	 jQuery('[name=USER_SERVICETAXNUM]').removeClass("required");
		 jQuery('[name=USER_PANID]').removeClass("required");
     }else{
    	 var panId=jQuery("[name='USER_PANID']").val();
    	 jQuery('[name=USER_PANID]').parent().parent().show();
		 jQuery('[name=USER_TINNUMBER]').parent().parent().hide();
		 jQuery('[name=USER_CSTNUMBER]').parent().parent().hide();
		 jQuery('[name=USER_SERVICETAXNUM]').parent().parent().hide();
		
		 jQuery('[name=USER_TINNUMBER]').removeClass("required");
		 jQuery('[name=USER_CSTNUMBER]').removeClass("required");
		 jQuery('[name=USER_SERVICETAXNUM]').removeClass("required");
		 jQuery('[name=USER_PANID]').addClass("required");	
		
	}
}

function partyIdentificationVal(){
	var roleTypeId=jQuery("[name='roleTypeId']").val();
	var idvalue="";
	var flag=false;
	 if(roleTypeId== "SERVICE_VENDOR") {
		 var serviceTaxNum=jQuery("[name='USER_SERVICETAXNUM']").val();
		 idvalue=serviceTaxNum;
		 partyIdentificationTypeId="SERVICETAX_NUMBER";
	 }else if(roleTypeId == "MATERIAL_VENDOR") {
		 var tinNumber=jQuery("[name='USER_TINNUMBER']").val();
		 var cstNumber=jQuery("[name='USER_CSTNUMBER']").val();
		 if(tinNumber!=""){
		  idvalue=tinNumber;
		  partyIdentificationTypeId="TIN_NUMBER";
		 }
		 if(cstNumber!=""){
		  idvalue=cstNumber;
		  partyIdentificationTypeId="CST_NUMBER";
		 }
	 }else{
		 var panId=jQuery("[name='USER_PANID']").val();
		 idvalue=panId;
		 //alert("panId=="+idvalue);
		 partyIdentificationTypeId = "PAN_NUMBER";
	 }
	var request = 'getPartyIdentification';      
	   jQuery.ajax({
	        url: request,
	        data: {partyIdentificationTypeId : partyIdentificationTypeId,idvalue :idvalue } , 
	        dataType: 'json',
	        async: false,
	        type: 'POST',
	       // success: function(result){ partyIdentityList=result;       	
	        success:function(result){
	        	//alert("111"+result);
				if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	               alert('Error in fetching Identification');
				}else{
					streamList = result["partyIdentityList"];
					//alert("stream"+streamList);
					var optionList = '';
					var list= streamList;
					if (list) {		       				        	
			        	for(var i=0 ; i<list.length ; i++){
							var innerList=list[i];
							//alert("innerList11"+innerList);
							if(innerList){
								 if (innerList.length >= 1) {
									     // alert(222);
									  jQuery("[name='groupName']").after("Vendor Group Name already exist."); 
									// jQuery('<div id="content-messages"></div>').insertAfter(jQuery("#partyContentList"));
									 	//$("#test")..tooltip("option", "content", "New Content");
									     $('.smallSubmit').hide();
									     return false;
						            }
							}
			      		}
			      	}else{
			      		$('.smallSubmit').show();
			      		flag=true;
			      		
			      	}
			      	
				}
	        } //end of success function function
	    });   
	   return flag;
}

function lookupParty(url) {
    partyIdValue = document.lookupparty.partyId.value;
    userLoginIdValue = document.lookupparty.userLoginId.value;
    if (partyIdValue.length > 0 || userLoginIdValue.length > 0) {
        document.lookupparty.action = url;
    }
    return true;
}

function refreshInfo() {
    document.lookupparty.lookupFlag.value = "N";
    document.lookupparty.hideFields.value = "N";
    document.lookupparty.submit();
}

