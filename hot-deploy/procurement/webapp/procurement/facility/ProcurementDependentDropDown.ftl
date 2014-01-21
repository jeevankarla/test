<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="text/javascript">
	var shedUnitsData = ${StringUtil.wrapString(shedUnitsJson)}
	 var unitsList ;
	function setShedUnitsDropDown(selection){	
	  unitsList = shedUnitsData[selection.value]; 
	  setUnitDropdown();	
	  shedDisplayOnChange($("#masterShedDropDown option:selected").text());	
	  shedValueOnChange(selection.value);
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
			var list= unitsList;
			if (list) {		       				        	
	        	for(var i=0 ; i<list.length ; i++){
					var innerList=list[i];	              			             
	                optionList += "<option value = " + innerList['facilityId'] + " >" + innerList['facilityName'] + "</option>";          			
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
	   getUnitRouteByValue($('[name=unitId]').val());
	}

	//this Js will get the all roletype Map for the selected invoiceType
	var unitRouteListValues;
	var getUnitRoutesResult;
	
	function getUnitRoute(selection) { 	
	 	getUnitRouteByValue(selection.value);
	 }
	 function getUnitRouteByValue(selection) { 	
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
		  setShedUnitsDropDown(selection);	  
		  setUnitDropdown("facilityId");	
	}

	//Billing screen
	function setBillingShedUnitsDropDown(selection){
		  setShedUnitsDropDown(selection);	  
		  setUnitDropdown("facilityId");	
	}	
	
	
	
	
			
</script>