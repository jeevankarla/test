<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="text/javascript">
	var shedUnitsData;
   	var shedUnitTimePeriodsJson ;
   	var shedFinAccountsJson ;
   	var shortNamesMap;
   	var ifscMap;
   	<#if shedFinAccountsJson?has_content >
   	   shedFinAccountsJson = ${StringUtil.wrapString(shedFinAccountsJson)}
   	</#if>
	var finAccList;
	var shedFinAccList;
	var unitsList ;
	var timePeriodList;
	var shedTimePeriodList;
		
	function setShedUnitsDropDown(selection){
	  setShedUnitsDropDownByValue(selection.value);
	  setUnitDropdown();
	  shedDisplayOnChange($("#masterShedDropDown option:selected").text());	
	  shedValueOnChange(selection.value);
	  getTimePeriodsByUnit(selection.value,$('[name=unitId]').val());
	  getShedFinAccounts(selection.value);
	  getUnitRouteByValue($('[name=unitId]').val());
	  
	}
	function setShedFacUnitsDropDown(selection){
	  setShedFacUnitsDropDownByValue(selection.value);
	  setUnitDropdown();
	  shedDisplayOnChange($("#masterShedDropDown option:selected").text());	
	  shedValueOnChange(selection.value);
	  getShedFinAccounts(selection.value);
	  getUnitRouteByValue($('[name=unitId]').val());
	  setShortNameAndGbCode();
	}
	function setShortNameAndGbCode(){
		var bankName = $('#finAccountName').val();
		$('[name=ifscCode]').val('');
		$('[name=ifscCode]').attr("readonly",false);
		$('[name=bCode]').val('');
		$('[name=bCode]').attr("readonly",false);
		$('[name=bPlace]').val('');
		$('[name=finAccountBranch]').val('');
		$('[name=bPlace]').attr("readonly",false);
		var bankDetails = shortNamesMap[bankName];
		var key = bankName;
		if(bankDetails){
			for(bankKey in shortNamesMap[key]){
				var keyValue = shortNamesMap[key][bankKey];
				if(bankKey == "gbCode"){
					if(keyValue){
						$('[name=gbCode]').val(keyValue);
						$('[name=gbCode]').attr("readonly",true);
					}else{
						$('[name=gbCode]').attr("readonly",false);
						$('[name=gbCode]').val('');
					}
				}
				if(bankKey == "shortName"){
					if(keyValue){
						$('#shortName').val(keyValue);
						$('#shortName').attr("readonly",true);
					}else{
						$('#shortName').attr("readonly",false);
						$('#shortName').val('');
					}
					$('#shortName').val(keyValue);
				}
			
			}
		}
	}
	
	function setShedFacUnitsDropDownByValue(shedId){
	    getDependentFacUnits(shedId);
		unitsList = shedUnitsData[shedId];
	}
	
	function getDependentFacUnits(shedId){
         var dataString  = 'shedId='+ shedId;
         var tempDataString=dataString;
         var isTimePeriodClosed = $('[name=isTimePeriodClosed]').val();
         var allTimePeriods = $('[name=timePeriodFlag]').val();        
         if(isTimePeriodClosed!=="undefined"){
           var dataJson = "shedId=" + shedId + "&isTimePeriodClosed=" + isTimePeriodClosed +"&timePeriods="+allTimePeriods;
           dataString=dataJson;
       	 }
            //get timeperiods and routes
     $.ajax({
             type: "POST",
             url: "getShedUnitTimePeriodsJsonAjax",
             data: dataString,
             dataType: 'json',
             async: false,
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
            	   alert(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{
            	   shedUnitTimePeriodsJson =   result["shedUnitTimePeriodsJson"];
            	   shedUnitsData = result["shedUnitsJson"];  
               }
               
             } ,
             error: function() {
            	 	alert(result["_ERROR_MESSAGE_"]);
            	 }
            }); 
        }
	
	function setShedUnitsDropDownByValue(shedId){
	    getDependentUnitsTimePeFinAcc(shedId);
		unitsList = shedUnitsData[shedId];
	}
	
	function setProcurementUnitsDropDown(selection){
		setShedUnitsDropDownByValue(selection.value);
		setUnitDropdown();
	}
	
	
	//To Display ShedName On Shed Reports Screen-Let
	function shedDisplayOnChange(selectedShed){
    	var shed = "Shed Reports Of   "+ selectedShed;
    	jQuery("[name='dispShedId']").html(shed);
    }
    
    //To Set Shed Value for all Shed-Wise Reports
    function shedValueOnChange(selectedShed){
    	$('[name=shedId]').val(selectedShed);
    }
	
	function setUnitDropdown(paramName){
			if(!paramName){
				paramName= "unitId";
			}	
			var optionList = '';
			optionList += "<option value = " + "" + " >" +" "+ "</option>";
			var list= unitsList;
			if (list) {		       				        	
	        	for(var i=0 ; i<list.length ; i++){
					var innerList=list[i];	              			             
	                optionList += "<option value = " + innerList['facilityId'] + " >" +innerList['facilityCode']+" " + innerList['facilityName'] + "</option>";          			
	      		}//end of main list for loop
	      if(paramName){
	      	jQuery("[name='"+paramName+"']").html(optionList);
	      }	
	      var gradeUnitsList= '';		
	    	gradeUnitsList += "<option value = " + "allUnits" + " >" +"ALL UNITS"+ "</option>";
	    	gradeUnitsList += "<option value = " + "supervisor" + " >" +"SUPERVISOR"+ "</option>";
	    	gradeUnitsList += optionList;
	      if(gradeUnitsList){
	      	jQuery("[name='"+"gUnitId"+"']").html(gradeUnitsList);
	      }
	      		
	   }//end of main list if
	}

	//this Js will get the all roletype Map for the selected invoiceType
	var unitRouteListValues;
	var getUnitRoutesResult;
	
	function getUnitRoute(selection) { 	
	 	getUnitRouteByValue(selection.value);
	 }
	 function getUnitRouteByValue(selection) {
	   if(!selection){
	   	  optionList = {};	
	      jQuery("[name='routeId']").html(optionList);
	   }else{
		   var request = 'getUnitRoutes';	   
		   jQuery.ajax({
		        url: request,
		        data: {unitId : selection } , 
		        dataType: 'json',
		        async: false,
		        type: 'POST',
		        success: function(result){ getUnitRoutesResult = result;       	
				
		        } //end of success function function
		    });
			setUnitRoutesDropdown(getUnitRoutesResult['routesDetailList'] ,"routeId");
			$('[name=unitId]').val($('[name=unitId]').val());
		}
	 }
	 
	 // function for setting finAccounts by facility
	 function getShedFinAccounts(shedId){
	 	// need to ajaxify 
	 	
	 	 var dataString  = 'shedId='+ shedId;
         var tempDataString=dataString;
         var isTimePeriodClosed = $('[name=isTimePeriodClosed]').val();
         var allTimePeriods = $('[name=timePeriodFlag]').val();        
         if(isTimePeriodClosed!=="undefined"){
           var dataJson = "shedId=" + shedId + "&isTimePeriodClosed=" + isTimePeriodClosed +"&timePeriods="+allTimePeriods;
           dataString=dataJson;
       	 }
            //get timeperiods and routes
     $.ajax({
             type: "POST",
             url: "getFacilityFinAccountsAjax",
             data: dataString,
             dataType: 'json',
             async: false,
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
            	   alert(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{
            	   shedFinAccountsJson =   result["shedFinAccountsJson"];
            	   shortNamesMap = result["shortNamesMap"];
            	   ifscMap = result["ifscMap"];
               }
               
             } ,
             error: function() {
            	 	alert(result["_ERROR_MESSAGE_"]);
            	 }
            }); 
	 	
	 	if(shedFinAccountsJson != undefined){
			var shedFinAccounts = shedFinAccountsJson[shedId];
			var shedFinOptionList = '';
			if(!shedId){
				shedFinOptionList ={};
			}else{
				for(var i=0;i<shedFinAccounts.length;i++){
					shedFinAccList += shedFinAccounts[i];
					shedFinOptionList += "<option> " + shedFinAccounts[i] + " </option>";
					
				}
				finAccList = shedFinAccList;
			}
			jQuery("[name='"+"bankName"+"']").html(shedFinOptionList);
		}
	 }
	 
	 function copy() {
    	var e = document.getElementById("finAcc");
		var strUser = e.options[e.selectedIndex].text;
		finAcc.value = strUser;
	 }
	 
	 // function for setting Time periods by facility
	 function getTimePeriodsByUnit(shedId,unitId){
	     getDependentUnitsTimePeFinAcc(shedId);
		var unitWiseTimePeriods = shedUnitTimePeriodsJson[shedId];
		var unitTimePeriods='' ;
		var shedTimePeriods =shedUnitTimePeriodsJson[shedId+'_timePeriods'];		
		var unitOptionList = '';
		var shedOptionList = '';
		jQuery("[name='"+"customTimePeriodId"+"']").html(shedOptionList);
		jQuery("[name='"+"shedCustomTimePeriodId"+"']").html(shedOptionList);
		if(!shedId){
			shedOptionList ={};
		}else{
			 var shedPeriodSize=4;
			 var periodflag= "N"			 
			 if($('#timePeriodFlag').attr('checked')) {
 				  periodflag= "Y"
 				  $('[name=timePeriodFlag]').val("Y");
			} 
			if(periodflag=='Y'){				   				  
 			 	$('#timePeriodFlag').val('Y');
			   	shedPeriodSize=shedTimePeriods.length;
			}
			for(var i=0;i<shedPeriodSize;i++){
				shedTimePeriodList += shedTimePeriods[i];
				shedOptionList += "<option value = " + shedTimePeriods[i]['customTimePeriodId'] + " >" + shedTimePeriods[i]['fromDate']+"-"+shedTimePeriods[i]['thruDate']+ "</option>";
			}
		}
		jQuery("[name='"+"customTimePeriodId"+"']").html(shedOptionList);
		jQuery("[name='"+"shedCustomTimePeriodId"+"']").html(shedOptionList);
		if(unitId!=''){
			unitTimePeriods = unitWiseTimePeriods[unitId];
			var flag=jQuery('#timePeriodFlag').val();			
			if(unitTimePeriods.length>0){
			    var periodSize=4;
			    if(flag=='Y'){			   
			     	periodSize=unitTimePeriods.length;
			    }
				for(var i=0; i<periodSize;i++){
					timePeriodList += unitTimePeriods[i];
					unitOptionList += "<option value = " + unitTimePeriods[i]['customTimePeriodId'] + " >" + unitTimePeriods[i]['fromDate']+"-"+unitTimePeriods[i]['thruDate']+ "</option>";
				}
			}else{
				timePeriodList = shedTimePeriodList;
				unitOptionList = shedOptionList;
			}
			jQuery("[name='"+"customTimePeriodId"+"']").html(unitOptionList);
			jQuery("[name='"+"shedCustomTimePeriodId"+"']").html(shedOptionList);
		}
	}
	function setUnitRoutesDropdown(list , elementName){
		var optionList = '';  
		optionList += "<option value = " + "all" + " >" +"ALL ROUTES"+ "</option>";
		if (list) {		       				        	
	        	for(var i=0 ; i<list.length ; i++){
					var innerList=list[i];	              			             
	                optionList += "<option value = " + innerList['facilityId'] + " >" + innerList['facilityName'] + "</option>";          			
	      		}//end of main list for loop
	    if(elementName){
	    	jQuery("[name='"+elementName+"']").html(optionList);
	    	
	    }else{
	    	jQuery("[name='routeId']").html(optionList);
	    } 
	    
	     
	   }//end of main list if
	
	
	}



	//OutPut Entry screen
	function setOutPutEntryShedUnitsDropDown(selection){
		var shedValue = selection.value ;
		setOutPutEntryShedUnitsDropDownByValue(shedValue);
		  
	}
	function setOutPutEntryShedUnitsDropDownByValue(selection){
		 if(!selection){
		 	var unitsList = {};
		 	jQuery("[name='"+"facilityId"+"']").html(unitsList);
		 	jQuery("[name='"+"customTimePeriodId"+"']").html(unitsList);
		 }
		 else{
		  setShedUnitsDropDownByValue(selection);	  
		  setUnitDropdown("facilityId");
		  getTimePeriodsByUnit(selection,$('[name=facilityId]').val());
		  }	
	
	}
	function timePeriodFlagChecked(selection){	
		 if($('#timePeriodFlag').attr('checked')) {
		 	$('#timePeriodFlag').val("Y");
		 }else{
		 	$('#timePeriodFlag').val("N");
		 }
		var shedIdValue = $('[name=shedId]').val();
		var unitIdValue = $('[name=untitId]').val();
	   if( shedIdValue !=''){
	  	 getTimePeriodsByUnit(shedIdValue,unitIdValue);
	   }
		 	
	}
	function setUnitRoutesAndTimePeriods(){	
		var shedId = $('[name=shedId]').val();
		var unitId = $('[name=unitId]').val();
		if(!unitId){
			$('[name=unitId]').val($('[name=unitId]').val());	
		}
		getUnitRouteByValue(unitId);
		getTimePeriodsByUnit(shedId,unitId);
	}
	
	//Billing screen
	function setBillingShedUnitsDropDown(selection){
		
		 var shedValue = selection.value ; 
		 setBillingShedUnitsDropDownByValue(shedValue);		  
	}
	function setBillingShedUnitsDropDownByValue(shedId){
		 if(!shedId){
		 	var unitsList = {};
		 	jQuery("[name='"+"facilityId"+"']").html(unitsList);
		 	jQuery("[name='"+"customTimePeriodId"+"']").html(unitsList);
		 }else{
		 	setShedUnitsDropDownByValue(shedId);	  
		  	setUnitDropdown("facilityId");
		  	getTimePeriodsByUnit(shedId,$('[name=facilityId]').val());
		 }
		 
	}	
	
	
function getDependentUnitsTimePeFinAcc(shedId){
         var dataString  = 'shedId='+ shedId;
         var tempDataString=dataString;
         var isTimePeriodClosed = $('[name=isTimePeriodClosed]').val();
         var allTimePeriods = $('[name=timePeriodFlag]').val();        
         if(isTimePeriodClosed!=="undefined"){
           var dataJson = "shedId=" + shedId + "&isTimePeriodClosed=" + isTimePeriodClosed +"&timePeriods="+allTimePeriods;
           dataString=dataJson;
       	 }
            //get timeperiods and routes
     $.ajax({
             type: "POST",
             url: "getShedUnitTimePeriodsJsonAjax",
             data: dataString,
             dataType: 'json',
             async: false,
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
            	   alert(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{
            	   shedUnitTimePeriodsJson =   result["shedUnitTimePeriodsJson"];
            	   shedUnitsData = result["shedUnitsJson"];  
               }
               
             } ,
             error: function() {
            	 	alert(result["_ERROR_MESSAGE_"]);
            	 }
            }); 
       //get Finaccounts 
       dataString=tempDataString;
       <#if (reportFrequencyFlag?exists) &&(reportFrequencyFlag =="ShedReports")>
        $.ajax({
             type: "POST",
             url: "getFacilityFinAccountsAjax",
             data: dataString,
             dataType: 'json',
             async: false,
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){            	  
            	   alert(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{
            	   shedFinAccountsJson =   result["shedFinAccountsJson"];;  
               }
               
             } ,
             error: function() {
            	 	alert(result["_ERROR_MESSAGE_"]);
            	 }
            }); 
     </#if>    

}	
function setIfscCode(){
	var bankName = $('#finAccountName').val();
	var branchMap = ifscMap[bankName];
	if(branchMap){
		var branchName = $('[name=finAccountBranch]').val();
		var branchDet = branchMap[branchName];
		if(!branchDet){
			$('[name=ifscCode]').val('');
			$('[name=ifscCode]').attr("readonly",false);
			
			$('[name=bPlace]').val('');
			$('[name=bPlace]').attr("readonly",false);
			
			$('[name=bCode]').val('');
			$('[name=bCode]').attr("readonly",false);
			
			
		}
		
		
		if(branchDet){
			var ifscCode = branchDet["ifscCode"];
			var bCode =  branchDet["bCode"];
			var bPlace =  branchDet["bPlace"];
			if(ifscCode){
				$('[name=ifscCode]').val(ifscCode);
				$('[name=ifscCode]').attr("readonly",true);
			}else{
				$('[name=ifscCode]').val('');
				$('[name=ifscCode]').attr("readonly",false);
			}
			if(bCode){
				$('[name=bCode]').val(bCode);
				$('[name=bCode]').attr("readonly",true);
			}else{
				$('[name=bCode]').val('');
				$('[name=bCode]').attr("readonly",false);
			}
			if(bPlace){
				$('[name=bPlace]').val(bPlace);
				$('[name=bPlace]').attr("readonly",true);
			}else{
				$('[name=bPlace]').val('');
				$('[name=bPlace]').attr("readonly",false);
			}
		}
		
	}
}
jQuery(document).ready(function() {
	$("input").keyup(function(e){
		var fieldName = e.target.name; 
		if(fieldName == "finAccountBranch"){
			setIfscCode();
		}
	});
});
	
			
</script>