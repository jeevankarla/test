<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="text/javascript">
	var shedUnitTimePeriodsJson = ${StringUtil.wrapString(shedUnitTimePeriodsJson)}
	var unitsList ;
	var timePeriodList;
	var shedTimePeriodList;
	function getTimePeriodsByUnitValue(flag,shedId,unitId){
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
				shedOptionList += "<option value = '" + shedTimePeriods[i]['customTimePeriodId'] + " '>" + shedTimePeriods[i]['fromDate']+"-"+shedTimePeriods[i]['thruDate']+ "</option>";
			}
		}
		if(flag == "find"){
			$('#shedId').val(shedId);
			jQuery("select[name=customTimePeriodId]").html(shedOptionList);
		}else{
			$('#sshedId').val(shedId);
			jQuery("#rCustomTimePeriodId").html(shedOptionList);
		}
		
		if((unitId!='') &&((typeof unitId)!= 'undefined')){
				unitTimePeriods = unitWiseTimePeriods[unitId];
				if(unitTimePeriods.length>0){
					for(var i=0; i<unitTimePeriods.length;i++){
						timePeriodList += unitTimePeriods[i];
						unitOptionList += "<option value = " + unitTimePeriods[i]['customTimePeriodId'] + " >" + unitTimePeriods[i]['fromDate']+"-"+unitTimePeriods[i]['thruDate']+ "</option>";
					}// end of for
				}else{
					timePeriodList = shedTimePeriodList;
					unitOptionList = shedOptionList;
				}
				if(flag == "find"){
					$('#shedId').val(shedId);
					jQuery("select[name=customTimePeriodId]").html(unitOptionList);
				}else{
					$('#sshedId').val(shedId);
					jQuery("#rCustomTimePeriodId").html(unitOptionList);
				}
		}
		
	}//end of getTimePeriodsByUnitValue
	function getTimePeriodsByUnitValueForValidation(shedId,unitId){
		getTimePeriodsByUnitValue("run",shedId,unitId);
	}
	
	
	function resetUnitCodeAndTimePeriod(){
		$('[name=unitCode]').val('');
		getTimePeriodsByUnitValue("find",$('[name=shedId]').val());
	}
	$(document).ready(function() {		
		if($('[name=unitCode]').val()){
		 		shedValue = $('[name=shedId]').val();
	  			unitValue = $('[name=unitCode]').val();
  				getTimePeriodsByUnitValue("find",shedValue,unitValue);
		 }   	
		$("input").keyup(function(e){
	  		if(e.target.name == "unitCode" ){
	  			shedValue = $('[name=shedId]').val();
	  			unitValue = e.target.value;
	  			if(!unitValue){
	  				resetUnitCodeAndTimePeriod();
	  			}else{
  					getTimePeriodsByUnitValue("find",shedValue,unitValue);
  				}
	  		}
	  		
		}); 
	});//end of ready function
	
	if($('#shedId').val()){
		getTimePeriodsByUnitValue("find",jQuery("[name=shedId]").val());
	}
	<#if !unitName?has_content>
		<#if shedName?has_content>
			getTimePeriodsByUnitValue("find",jQuery("#shedId").val());
		</#if>
   	<#else>
		<#if unitName?has_content>
			 getTimePeriodsByUnitValue("find",jQuery("#shedId").val(),${unitId});
		</#if>
  	</#if>
  	
</script>