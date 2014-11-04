<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="text/javascript">
	var shedUnitsData;
   	var shedUnitTimePeriodsJson ;
   	var shedFinAccountsJson ;
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
		if(!shedId){
			shedOptionList ={};
		}else{
			for(var i=0;i<shedTimePeriods.length;i++){
				shedTimePeriodList += shedTimePeriods[i];
				shedOptionList += "<option value = " + shedTimePeriods[i]['customTimePeriodId'] + " >" + shedTimePeriods[i]['fromDate']+"-"+shedTimePeriods[i]['thruDate']+ "</option>";
			}
		}
		jQuery("[name='"+"customTimePeriodId"+"']").html(shedOptionList);
		jQuery("[name='"+"shedCustomTimePeriodId"+"']").html(shedOptionList);
		if(unitId!=''){
			unitTimePeriods = unitWiseTimePeriods[unitId];
			if(unitTimePeriods.length>0){
				for(var i=0; i<unitTimePeriods.length;i++){
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
	
			
</script>